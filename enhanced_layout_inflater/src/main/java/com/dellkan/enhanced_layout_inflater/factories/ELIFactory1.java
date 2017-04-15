package com.dellkan.enhanced_layout_inflater.factories;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.dellkan.enhanced_layout_inflater.ELI;


/**
 * The {@link android.view.LayoutInflater.Factory} is legacy. In most cases
 * {@link android.view.LayoutInflater.Factory2} is the factory that will actually be used.
 */
public class ELIFactory1 implements LayoutInflater.Factory {
	private LayoutInflater.Factory mFactory;
	private ELI.Builder mConfigs;

	public ELIFactory1(LayoutInflater.Factory mFactory, ELI.Builder configs) {
		this.mFactory = mFactory;
		mConfigs = configs;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = mFactory.onCreateView(name, context, attrs);

		if (mConfigs != null && view != null) {
			mConfigs.onViewCreated(null, view, attrs);
		}

		return view;
	}
}
