package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.hooks.TypedAttributeViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;


public class StyleHook extends TypedAttributeViewHook {
	public StyleHook() {
		super(R.attr.test);
	}

	@Override
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return view.getId() == R.id.test_style;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@Nullable View parent, @NonNull View view, @NonNull TypedArray attrs) {
		int colorRef = attrs.getColor(R.styleable.CustomTextView_test, 0);
		view.setBackgroundColor(colorRef);

		if (parent != null) {
			((TextView) parent.findViewById(R.id.style_test_info)).setText("Retrieving values in styles is a bit of a pain, but is made easier by TypedAttributeViewHook");
		}
	}
}
