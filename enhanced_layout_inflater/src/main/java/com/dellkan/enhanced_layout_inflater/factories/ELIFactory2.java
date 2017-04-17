package com.dellkan.enhanced_layout_inflater.factories;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.dellkan.enhanced_layout_inflater.ELIConfig;

/**
 * This is where the real magic happens
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ELIFactory2 implements LayoutInflater.Factory2 {
	private LayoutInflater.Factory2 mFactory2;
	private ELIConfig mConfigs;

	public ELIFactory2(LayoutInflater.Factory2 mFactory2, ELIConfig configs) {
		this.mFactory2 = mFactory2;
		mConfigs = configs;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = mFactory2.onCreateView(name, context, attrs);

		if (mConfigs != null && view != null) {
			mConfigs.onViewCreated(null, view, attrs);
		}

		return view;
	}

	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		View view = mFactory2.onCreateView(parent, name, context, attrs);

		if (mConfigs != null && view != null) {
			mConfigs.onViewCreated(parent, view, attrs);
		}

		return view;
	}
}
