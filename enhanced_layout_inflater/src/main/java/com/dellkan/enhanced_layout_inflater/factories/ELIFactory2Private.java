package com.dellkan.enhanced_layout_inflater.factories;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.dellkan.enhanced_layout_inflater.ELI;
import com.dellkan.enhanced_layout_inflater.ELIConfig;
import com.dellkan.enhanced_layout_inflater.reflectionutils.ReflectionUtils;

/**
 * Second attempt - if ELIFactory1 & 2 fails, this is given a crack at it
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ELIFactory2Private implements LayoutInflater.Factory2 {
	private ELI layoutInflater;
	private LayoutInflater.Factory2 mFactory2;
	private ELIConfig mConfigs;

	public ELIFactory2Private(ELI layoutInflater, LayoutInflater.Factory2 mFactory2, ELIConfig configs) {
		this.layoutInflater = layoutInflater;
		this.mFactory2 = mFactory2;
		mConfigs = configs;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = mFactory2.onCreateView(name, context, attrs);

		if (view == null) {
			view = createCustomViewInternal(name, context, attrs);
		}

		if (mConfigs != null && view != null) {
			mConfigs.onViewCreated(null, view, attrs);
		}

		return view;
	}

	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		View view = mFactory2.onCreateView(parent, name, context, attrs);

		if (view == null) {
			view = createCustomViewInternal(name, context, attrs);
		}

		if (mConfigs != null && view != null) {
			mConfigs.onViewCreated(parent, view, attrs);
		}

		return view;
	}

	/**
	 * Nasty method to inflate custom layouts that haven't been handled else where. If this fails it
	 * will fall back through to the PhoneLayoutInflater method of inflating custom views
	 *
	 * @param parent      parent view
	 * @param view        view if it has been inflated by this point, if this is not null this method
	 *                    just returns this value.
	 * @param name        name of the thing to inflate.
	 * @param viewContext Context to inflate by if parent is null
	 * @param attrs       Attr for this view which we can steal fontPath from too.
	 * @return view or the View we inflate in here.
	 */
	private Object[] mConstructorArgs = null;
	private View createCustomViewInternal(String name, Context context, AttributeSet attrs) {
		// I by no means advise anyone to do this normally, but Google have locked down access to
		// the createView() method, so we never get a callback with attributes at the end of the
		// createViewFromTag chain (which would solve all this unnecessary rubbish).
		// We at the very least try to optimise this as much as possible.
		// We only call for customViews (As they are the ones that never go through onCreateView(...)).
		// We also maintain the Field reference and make it accessible which will make a pretty
		// significant difference to performance on Android 4.0+.

		View view = null;

		if (mConstructorArgs == null)
			mConstructorArgs = ReflectionUtils.getField(layoutInflater, "mConstructorArgs", Object[].class);

		final Object lastContext = mConstructorArgs[0];
		// The LayoutInflater actually finds out the correct context to use. We just need to set
		// it on the mConstructor for the internal method.
		// Set the constructor ars up for the createView, not sure why we can't pass these in.
		mConstructorArgs[0] = context;
		ReflectionUtils.setField(layoutInflater, "mConstructorArgs", Object[].class, mConstructorArgs);
		try {
			view = layoutInflater.createView(name, null, attrs);
		} catch (ClassNotFoundException ignored) {
		} finally {
			mConstructorArgs[0] = lastContext;
			ReflectionUtils.setField(layoutInflater, "mConstructorArgs", Object[].class, mConstructorArgs);
		}

		return view;
	}
}
