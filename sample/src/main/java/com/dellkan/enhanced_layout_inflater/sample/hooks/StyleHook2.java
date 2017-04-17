package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.hooks.TypedAttributeViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;

public class StyleHook2 extends TypedAttributeViewHook<TextView> {
	public StyleHook2() {
		super(TextView.class, R.styleable.CustomTextView);
	}

	@Override
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return super.shouldTrigger(parent, view, attrs) && view.getId() == R.id.custom_theme_test2_info;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(@Nullable View parent, @NonNull final TextView view, @NonNull TypedArray attrs) {
		view.setText("This runs if this TextView has a style-theme that includes the R.styleable.CustomTextView");

		final int color = attrs.getColor(R.styleable.CustomTextView_test, 0);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				if (view.getRootView() != null) {
					view.getRootView().findViewById(R.id.test_theme_style).setBackgroundColor(color);
				}
			}
		});
	}
}
