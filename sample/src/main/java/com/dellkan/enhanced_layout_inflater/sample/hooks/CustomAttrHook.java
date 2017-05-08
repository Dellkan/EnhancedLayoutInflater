package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.ELIContext;
import com.dellkan.enhanced_layout_inflater.ELIUtils;
import com.dellkan.enhanced_layout_inflater.ViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;


public class CustomAttrHook extends ViewHook {
	@Override
	public boolean shouldTrigger(ELIContext eliContext, @Nullable View parent, @NonNull View view, AttributeSet attrs) {
		return view.getId() == R.id.test_attr;
	}

	@Override
	public void onViewCreated(ELIContext eliContext, @Nullable View parent, @NonNull View view, AttributeSet attrs) {
		if (ELIUtils.containsAttribute(attrs, "somethingorother")) {
			view.setBackgroundColor(view.getContext().getResources().getColor(R.color.test_success));

			if (parent != null) {
				((TextView) parent.findViewById(R.id.custom_attr_test_info)).setText(
						String.format("Found following value: %s", ELIUtils.getAttributeValue(attrs, "somethingorother"))
				);
			}
		}
	}
}
