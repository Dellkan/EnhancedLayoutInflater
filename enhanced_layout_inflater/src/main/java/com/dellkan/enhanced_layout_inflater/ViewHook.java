package com.dellkan.enhanced_layout_inflater;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public abstract class ViewHook<ViewType extends View> {
	private Class<ViewType> clz;

	public ViewHook() {
		this.clz = (Class<ViewType>) View.class;
	}

	public ViewHook(Class<ViewType> clz) {
		this.clz = clz;
	}

	/**
	 * Determine whether or not this view is interesting.
	 * Please note that this will run extremely frequently (all views, every time). Make sure that
	 * your checks are efficient
	 * @param parent Parent view, if any. May be null
	 * @param view Target view
	 * @param attrs Attributes on the view, in raw form
	 * @return Return true if this hook should trigger for this view
	 */
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return clz.isAssignableFrom(view.getClass());
	}

	/**
	 * This is your callback, where you will receive the target view, and may perform actions on it
	 * @param parent Parent view, if any. May be null
	 * @param view Target view
	 * @param attrs Attributes on the view, in raw form
	 */
	public abstract void onViewCreated(@Nullable View parent, @NonNull ViewType view, AttributeSet attrs);
}
