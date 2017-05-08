package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.ELIContext;
import com.dellkan.enhanced_layout_inflater.ViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;

public class ActionbarTitleTest extends ViewHook<Toolbar> {
	public ActionbarTitleTest() {
		super(Toolbar.class);
	}

	@Override
	public void onViewCreated(ELIContext eliContext, @Nullable View parent, @NonNull final Toolbar toolbar, AttributeSet attrs) {
		toolbar.setTitle("Enhanced Layout Inflater");
		/*
			This test is all about the toolbar - which happen to create-add children programmatically,
			which we want to make sure we pick up right. However, those results we would like to display
			elsewhere, namely through a couple of views that isn't inflated yet. Hence, throw this into
			the queue, that'll run later on.
		*/
		new Handler().post(new Runnable() {
			@SuppressLint("SetTextI18n")
			@Override
			public void run() {
				View rootView = toolbar.getRootView();
				rootView.findViewById(R.id.actionbar_test).setBackgroundColor(
						toolbar.getContext().getResources().getColor(R.color.test_success)
				);
				((TextView) rootView.findViewById(R.id.actionbar_test_info)).setText(
						"This test is mostly in there to make sure we pick up on the toolbar inflation"
				);
			}
		});
	}
}
