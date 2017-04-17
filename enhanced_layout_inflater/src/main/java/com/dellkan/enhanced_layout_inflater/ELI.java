package com.dellkan.enhanced_layout_inflater;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
import java.util.List;

/**
 *  The majority of work is actually done through the factories (ELIFactory1 & 2). However,
 *  because has majorly screwed us over and over with inconsistencies and bugs through the
 *  LayoutInflater, we have to jump through several hoops to get our callbacks workin'.
 *
 *  In short, our calls are dictated by
 *  {@link LayoutInflater#createViewFromTag(View, String, Context, AttributeSet, boolean)} which
 *  runs through the following methods in this respective order until a view is given.
 *
 *  <ul>
 *  <li>{@link LayoutInflater.Factory2#onCreateView(View, String, Context, AttributeSet)} or
 *  {@link LayoutInflater.Factory#onCreateView(String, Context, AttributeSet)} if Factory2 isn't set
 *  </li>
 *  <li>{@link LayoutInflater.Factory2#onCreateView(View, String, Context, AttributeSet)} through
 *  {@link LayoutInflater.mPrivateFactory}</li>
 *  <li>{@link LayoutInflater#createView(String, String, AttributeSet)}</li>
 *  </ul>
 *
 *  If all of these fails, we're left without a view.
 *
 *  Robobinding and Calligraphy both runs {@link LayoutInflater#createView(String, String, AttributeSet)}
 *  manually, because it's set to final, and we're not allowed to overwrite it. This code directly steals
 *  createCustomViewInternal from Calligraphy, as it seems to be slightly more efficient and readable than
 *  Robobinding's attempt
 */
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

	@Override
	public LayoutInflater cloneInContext(Context newContext) {
		return new ELI(this, newContext, true, mConfigs);
	}

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

		if (view != null) {
			mConfigs.onViewCreated(null, view, attrs);
		}

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
//            LayoutInflaterCompat.setFactory(this, new WrapperFactory2(factory2, mCalligraphyFactory));
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

		public void onViewCreated(@Nullable View parent, @NonNull View view, @Nullable AttributeSet attrs) {
			for (ViewHook hook : hooks) {
				if (hook.shouldTrigger(parent, view, attrs)) {
					hook.onViewCreated(parent, view, attrs);
				}
			}

			// If the view was created manually (new View(Context)), it won't trigger our callbacks.
			// But, if the view itself adds children in the constructor, we could check for children
			// after the view has been created - and trigger the callback for those at least. Of
			// course, we won't have access to AttributeSet, since those aren't actually created. But,
			// you can still do some interesting things from the style or Theme
			if (view instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) view;

				// Another option: HierarchyChangeListener. This should trigger for views that are
				// added programmatically, after the initial setup.
				if (enablePostCallbacks) {
					viewGroup.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
						@Override public void onChildViewAdded(View parent, View child) {
							onViewCreated(parent, child, null);
						}
						@Override public void onChildViewRemoved(View parent, View child) {}
					});
				}

				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					onViewCreated(view, viewGroup.getChildAt(i), null);
				}
			}
		}
	}
}
