package com.dellkan.enhanced_layout_inflater.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dellkan.enhanced_layout_inflater.ELIConfig;
import com.dellkan.enhanced_layout_inflater.ELIContextWrapper;
import com.dellkan.enhanced_layout_inflater.sample.hooks.ActionbarTitleTest;
import com.dellkan.enhanced_layout_inflater.sample.hooks.CustomAttrHook;
import com.dellkan.enhanced_layout_inflater.sample.hooks.ProgrammaticallyAddedViewTest;
import com.dellkan.enhanced_layout_inflater.sample.hooks.StyleHook;
import com.dellkan.enhanced_layout_inflater.sample.hooks.StyleHook2;
import com.dellkan.enhanced_layout_inflater.sample.hooks.ThemeHook;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(new ELIContextWrapper(newBase, new ELIConfig.Builder()
				.enablePostCallbacks()
				.addHook(new CustomAttrHook())
				.addHook(new StyleHook())
				.addHook(new ThemeHook())
				.addHook(new StyleHook2())
				.addHook(new ActionbarTitleTest())
				.addHook(new ProgrammaticallyAddedViewTest())
				.build()
		));
	}
}
