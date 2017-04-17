package com.dellkan.enhanced_layout_inflater;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dellkan.enhanced_layout_inflater.factories.ELIFactory1;
import com.dellkan.enhanced_layout_inflater.factories.ELIFactory2;
import com.dellkan.enhanced_layout_inflater.factories.ELIFactory2Private;
import com.dellkan.enhanced_layout_inflater.reflectionutils.ReflectionUtils;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Hopefully, this part will work as expected. If it isn't, and you have to debug through all of
 *  this, abandon all hope. Our entire purpose here is to get a callback whenever a (sub)view is
 *  created. While such callbacks exists, the following will possibly illustrate how painful it is
 *  to get those callbacks working. Essentially, the problem comes from a clusterfuck of
 *  inconsistencies, bugs, and weirdness on Google's part. As such, we have to jump through quite a
 *  few hoops to get everything running. Our work here is largely inspired by the work done in
 *  Calligraphy, with a few pinches of spice from Robobinding and our own (misguided) ideas.
 *
 *  The majority of work is actually done through the factories (ELIFactory1 & 2)
 *  (or should have been, anyway). However, we have to put in quite a few work-arounds and backups in
 *  here as well.
 *
 *  In short, our calls are dictated by
 *  {@link LayoutInflater#createViewFromTag(View, String, Context, AttributeSet, boolean)} which
 *  runs through the following methods in this respective order until a view is given.
 *
 *  <ul>
 *  <li>{@link ELIFactory2#onCreateView(View, String, Context, AttributeSet)} if Factory2 is set, or
 *  {@link ELIFactory1#onCreateView(String, Context, AttributeSet)} if Factory2 isn't set (or pre-hc)
 *  </li>
 *  <li>
 *      {@link ELIFactory2Private#onCreateView(View, String, Context, AttributeSet)}. Note that this
 *      is mostly just Factory2 with a couple of extra hacks put in as backup. Also note that this is
 *      set as a private field within LayoutInflater. Reflections hacks ensues.
 *  </li>
 *  <li>{@link #onCreateView(View, String, AttributeSet)}</li>
 *  <li>{@link #onCreateView(String, AttributeSet)}</li>
 *  <li>{@link #createView(String, String, AttributeSet)}</li>
 *  </ul>
 *
 *  <p>If all of these fails, we're left without a view.</p>
 *  <p>
 *  Robobinding and Calligraphy both runs
 *  {@link LayoutInflater#createView(String, String, AttributeSet)}
 *  manually within the private factory
 *  ({@link ELIFactory2Private#createCustomViewInternal(String, Context, AttributeSet)}),
 *  because it's set to final, and we're not allowed to overwrite it. This code directly steals
 *  createCustomViewInternal from Calligraphy, as it seems to be slightly more efficient and
 *  readable than Robobinding's attempt
 *  </p>
 */
@SuppressWarnings("JavadocReference")
public class ELI extends LayoutInflater {
	private static final String[] sClassPrefixList = {
			"android.widget.",
			"android.webkit."
	};
	private ELI.Builder mConfigs;

	protected ELI(Context context, ELI.Builder configs) {
		super(context);
		mConfigs = configs;
		wrapFactories(false);
	}

	protected ELI(LayoutInflater original, Context newContext, boolean cloned, ELI.Builder configs) {
		super(original, newContext);
		mConfigs = configs;
		wrapFactories(cloned);
	}

	/**
	 * If attempts are made to clone this LayoutInflater, make sure to bring along the configs!
	 */
	@Override
	public LayoutInflater cloneInContext(Context newContext) {
		return new ELI(this, newContext, true, mConfigs);
	}

	/**
	 * This thing wraps and sets the factories from the original LayoutInflater.
	 * We want the factories of the original LayoutInflater - as we want to add callbacks onto
	 * existing inflation mechanics, we're not seeking to replace the original behavior.
	 * @param cloned The LayoutInflater has (for some insane reason) restrictions on how many times
	 *               the factories can be set. Meanwhile, the LayoutInflater is bounced around,
	 *            cloned and recreated routinely. As such, this parameter just keeps track of that state.
	 */
	protected void wrapFactories(boolean cloned) {
		if (cloned) { return; }
		// If we are HC+ we get and set Factory2 otherwise we just wrap Factory1
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (getFactory2() != null) {
				// Sets both Factory/Factory2
				setFactory2(getFactory2());
			}
		}
		// We can do this as setFactory2 is used for both methods.
		if (getFactory() != null) {
			setFactory(getFactory());
		}
	}

	/**
	 * All inflates routes through this one, meaning it will always run.
	 * Copying Calligraphy, it seems we must set the internal factory in here
	 * TODO: Verify the above assumption
	 */
	@Override
	public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
		setPrivateFactoryInternal();
		return super.inflate(parser, root, attachToRoot);
	}

	/*
		Inflation stuff
	 */
	/**
	 * The LayoutInflater onCreateView is the fourth port of call for LayoutInflation.
	 * BUT only for none CustomViews.
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
		View view = super.onCreateView(parent, name, attrs);

		if (view != null && mConfigs != null) {
			mConfigs.onViewCreated(parent, view, attrs);
		}

		return view;
	}

	/**
	 * The LayoutInflater onCreateView is the fourth port of call for LayoutInflation.
	 * BUT only for none CustomViews.
	 * Basically if this method doesn't inflate the View nothing probably will.
	 *
	 * Apparently, this will only ever be called through {@link #onCreateView(View, String, AttributeSet)}
	 */
	@Override
	protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
		// This mimics the {@code PhoneLayoutInflater} in the way it tries to inflate the base
		// classes, if this fails its pretty certain the app will fail at this point.
		View view = null;
		for (String prefix : sClassPrefixList) {
			try {
				view = createView(name, prefix, attrs);
			} catch (ClassNotFoundException ignored) {
			}
		}
		// In this case we want to let the base class take a crack
		// at it.
		if (view == null) view = super.onCreateView(name, attrs);

		// Apparently, running the hooks from here will make them trigger twice -
		// This method is always called from some other method where the hooks are called
//		if (view != null) {
//			mConfigs.onViewCreated(null, view, attrs);
//		}

		return view;
	}

	/*
		Factory stuff
	 */
	@Override
	public void setFactory(LayoutInflater.Factory factory) {
		// Only set our factory and wrap calls to the Factory trying to be set!
		if (!(factory instanceof ELIFactory1)) {
			super.setFactory(new ELIFactory1(factory, mConfigs));
		} else {
			super.setFactory(factory);
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setFactory2(LayoutInflater.Factory2 factory2) {
		// Only set our factory and wrap calls to the Factory2 trying to be set!
		if (!(factory2 instanceof ELIFactory2)) {
			super.setFactory2(new ELIFactory2(factory2, mConfigs));
		} else {
			super.setFactory2(factory2);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
	private Factory2 getPrivateFactory() {
		return ReflectionUtils.getField(this, "mPrivateFactory", LayoutInflater.Factory2.class);
	}

	private boolean mSetPrivateFactory = false;
	private void setPrivateFactoryInternal() {
		// Already tried to set the factory.
		if (mSetPrivateFactory) return;

		if (!(getContext() instanceof Factory2)) {
			mSetPrivateFactory = true;
			return;
		}

		Factory2 privateFactory = getPrivateFactory();
		ReflectionUtils.tryToInvokeMethod(
				this,
				"setPrivateFactory",
				new ELIFactory2Private(this, privateFactory != null ? privateFactory : (Factory2) getContext(), mConfigs)
		);

		mSetPrivateFactory = true;
	}

	/*
		Other hacks necessary to make the factories trigger correctly
	 */

	/*
		Config builder
	 */

	public static class Builder {
		private List<ViewHook> hooks = new ArrayList<>();
		private boolean enablePostCallbacks = false;
		public Builder addHook(ViewHook hook) {
			hooks.add(hook);
			return this;
		}

		/**
		 * Enable callbacks for views added programmatically
		 * @return Builder
		 */
		public Builder enablePostCallbacks() {
			this.enablePostCallbacks = true;
			return this;
		}

		private Map<View, Integer> refCount = new HashMap<>();
		private static final boolean debugCallCount = false;
		public void onViewCreated(@Nullable View parent, @NonNull View view, @Nullable AttributeSet attrs) {
			if (debugCallCount) {
				if (!refCount.containsKey(view)) {
					refCount.put(view, 1);
				} else {
					refCount.put(view, refCount.get(view) + 1);
				}
			}
			for (ViewHook hook : hooks) {
				if (hook.shouldTrigger(parent, view, attrs)) {
					hook.onViewCreated(parent, view, attrs);
				}
			}

			// If the view was created manually (new View(Context)), it won't trigger our callbacks
			// through the usual means. However, we've got a couple of tricks up our sleeves that might
			// help
			if (view instanceof ViewGroup) {
				final ViewGroup viewGroup = (ViewGroup) view;

				// If the view itself adds children in the constructor, we could check for children
				// after the view has been created - and trigger the callback for those at least. Of
				// course, we won't have access to AttributeSet, since these child-views were created
				// programmatically. But, you can still do some interesting things from the style or Theme
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					onViewCreated(view, viewGroup.getChildAt(i), null);
				}

				// Another option: HierarchyChangeListener. This should trigger for views that are
				// added programmatically, after the initial inflation. We're doing this delayed, so
				// the inflation get a chance to run its course.
				// TODO: Make sure OnHierarchyChangeListener don't propagate its triggers upwards.
				// TODO: I.e make sure we don't call the triggers twice due to nested hierarchies.
				if (enablePostCallbacks) {
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							viewGroup.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
								@Override public void onChildViewAdded(View parent, View child) {
									onViewCreated(parent, child, null);
								}
								@Override public void onChildViewRemoved(View parent, View child) {}
							});
						}
					});
				}
			}
		}
	}
}
