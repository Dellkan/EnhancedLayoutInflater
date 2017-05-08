package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.ELIContext;
import com.dellkan.enhanced_layout_inflater.ViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;


public class ThemeHook extends ViewHook {
	@Override
	public boolean shouldTrigger(ELIContext eliContext, @Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return view.getId() == R.id.test_theme;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onViewCreated(ELIContext eliContext, @Nullable View parent, @NonNull View view, AttributeSet attrs) {
		TypedArray array = view.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.test});

		// String.format("#%08X", (array.getColor(0, 0)));

		view.setBackgroundColor(array.getColor(0, 0));

		if (parent != null) {
			((TextView) parent.findViewById(R.id.theme_test_info)).setText("Theme test ran successfully, color read directly from Theme");
		}
	}
}
