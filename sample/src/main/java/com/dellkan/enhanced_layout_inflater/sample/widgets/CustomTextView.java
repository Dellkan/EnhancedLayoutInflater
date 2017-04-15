package com.dellkan.enhanced_layout_inflater.sample.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
}
