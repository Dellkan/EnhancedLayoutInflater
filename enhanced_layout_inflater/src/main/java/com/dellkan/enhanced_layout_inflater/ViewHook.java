package com.dellkan.enhanced_layout_inflater;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public abstract class ViewHook {
	// Simple way of filtering out views/attributes that is uninteresting to your purposes
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return true;
	}

	public abstract void onViewCreated(@Nullable View parent, @NonNull View view, AttributeSet attrs);
}
