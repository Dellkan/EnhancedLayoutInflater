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
import com.dellkan.enhanced_layout_inflater.reflectionutils.ReflectionUtils;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * We don't really have much work to do within the layoutInflater itself, but rather
 * we use this to mess with the various factories within. Those factories are where the real magic
 * happens
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
		// Skip if not attached to an activity.
		if (!(getContext() instanceof Factory2)) {
			mSetPrivateFactory = true;
			return;
		}

		Factory2 privateFactory = getPrivateFactory();
		ReflectionUtils.tryToInvokeMethod(
				this,
				"setPrivateFactory",
				new ELIFactory2(privateFactory != null ? privateFactory : (Factory2) getContext(), mConfigs)
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
		public Builder addHook(ViewHook hook) {
			hooks.add(hook);
			return this;
		}

		public void onViewCreated(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
			for (ViewHook hook : hooks) {
				if (hook.shouldTrigger(parent, view, attrs)) {
					hook.onViewCreated(parent, view, attrs);
				}
			}

		}
	}
}
