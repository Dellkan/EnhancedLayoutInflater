package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.ViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;


public class StyleHook extends ViewHook {
	@Override
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return view.getId() == R.id.test_style;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onViewCreated(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		Context context = view.getContext();

		TypedArray styles = context.obtainStyledAttributes(
				attrs.getStyleAttribute(),
				new int[]{R.attr.test}
		);

		int colorRef = styles.getColor(0, 0);
		view.setBackgroundColor(colorRef);

		styles.recycle();

		if (parent != null) {
			((TextView) parent.findViewById(R.id.style_test_info)).setText(
					String.format("Style reference: %d\nRetrieving values in styles is a bit of a pain, and by no means automatic", attrs.getStyleAttribute())
			);
		}
	}
}
