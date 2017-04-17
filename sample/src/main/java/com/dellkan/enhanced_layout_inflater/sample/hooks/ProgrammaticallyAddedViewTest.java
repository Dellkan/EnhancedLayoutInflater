package com.dellkan.enhanced_layout_inflater.sample.hooks;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dellkan.enhanced_layout_inflater.ViewHook;
import com.dellkan.enhanced_layout_inflater.sample.R;

public class ProgrammaticallyAddedViewTest extends ViewHook<TextView> {
	public ProgrammaticallyAddedViewTest() {
		super(TextView.class);
	}

	@Override
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, @Nullable AttributeSet attrs) {
		return super.shouldTrigger(parent, view, attrs) && parent instanceof Toolbar && attrs == null;
	}

	@Override
	public void onViewCreated(@Nullable View parent, @NonNull final TextView toolbar, AttributeSet attrs) {
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
				rootView.findViewById(R.id.programmatically_test).setBackgroundColor(
						toolbar.getContext().getResources().getColor(R.color.test_success)
				);
				((TextView) rootView.findViewById(R.id.programmatically_test_info)).setText(
						"This test makes sure that we can catch views that were added programmatically as well. See Builder.enablePostCallbacks"
				);
			}
		});
	}
}
