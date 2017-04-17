package com.dellkan.enhanced_layout_inflater.hooks;

import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.view.View;

import com.dellkan.enhanced_layout_inflater.ViewHook;

/**
 * Purpose: Allow you to focus on pre-defined typed attributes (i.e attributes that is defined in
 * an attrs.xml file)
 * @param <ViewType> What view types this hook should trigger for.
 */
public abstract class TypedAttributeViewHook<ViewType extends View> extends ViewHook<ViewType> {
	private int[] attributes;

	public TypedAttributeViewHook(int... attributes) {
		this.attributes = attributes;
	}

	/**
	 *
	 * @param clz
	 * @param attributes
	 */
	public TypedAttributeViewHook(Class<ViewType> clz, @StyleableRes int... attributes) {
		super(clz);
		this.attributes = attributes;
	}

	@Override
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		if (!super.shouldTrigger(parent, view, attrs)) {
			return false;
		}

		// Check for attrs of the correct type
		TypedArray values = view.getContext().obtainStyledAttributes(attrs, attributes);

		try {
			if (values.length() == 0) {
				return false;
			}
		} finally {
			values.recycle();
		}
		return true;
	}

	@Override
	public void onViewCreated(@Nullable View parent, @NonNull ViewType view, AttributeSet attrs) {
		TypedArray values = view.getContext().obtainStyledAttributes(attrs, attributes);

		try {
			onViewCreated(parent, view, values);
		} finally {
			values.recycle();
		}
	}

	public abstract void onViewCreated(@Nullable View parent, @NonNull ViewType view, @NonNull TypedArray attrs);
}
