package com.dellkan.enhanced_layout_inflater;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ELIConfig {
	private List<ViewHook> hooks = new ArrayList<>();
	private boolean enablePostCallbacks = false;

	private ELIContext eliContext;
	void preInflation(ELIContext eliContext) {
		this.eliContext = eliContext;
	}

	void postInflation(ELIContext eliContext) {
		this.eliContext = null;
	}

	/**
	 * Purpose: Delegate the "Oh, a view has been created" callback to all the registered hooks.
	 *
	 * Gathering points for all the various methods or places that can create a view. This should be
	 * called exactly once for each and all views.
	 *
	 * @param view The view in question
	 * @param attrs The attributeset, containing all the xml attributes set on this view
	 */
	public void onViewCreated(@Nullable View parent, @NonNull View view, @Nullable AttributeSet attrs) {
		// Iterate through all the hooks, and let them know about the new-born view
		for (ViewHook hook : hooks) {
			hook.onViewCreatedRaw(eliContext, parent, view, attrs);
		}

		// If the view was created manually (new View(Context)), it won't trigger our callbacks
		// through the usual means. However, we've got a couple of tricks up our sleeves that might
		// help
		if (view instanceof ViewGroup) {
			final ViewGroup viewGroup = (ViewGroup) view;

			// If the view itself adds children in the constructor, we could check for children
			// after the view has been created - and trigger the callback for those at least. Of
			// course, we won't have access to AttributeSet, since these child-views were created
			// programmatically. But, you can still do some interesting things from the style or Theme
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				onViewCreated(view, viewGroup.getChildAt(i), null);
			}

			// Another option: HierarchyChangeListener. This should trigger for views that are
			// added programmatically, after the initial inflation. We're doing this delayed, so
			// the inflation get a chance to run its course.
			// TODO: Make sure OnHierarchyChangeListener don't propagate its triggers upwards.
			// TODO: I.e make sure we don't call the triggers twice due to nested hierarchies.
			if (enablePostCallbacks) {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						viewGroup.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
							@Override public void onChildViewAdded(View parent, View child) {
								onViewCreated(parent, child, null);
							}
							@Override public void onChildViewRemoved(View parent, View child) {}
						});
					}
				});
			}
		}
	}

	/**
	 * Builder class to create an ELIConfig, registering hooks, and set other variables that might
	 * change how the LayoutInflation works.
	 */
	public static class Builder {
		private List<ViewHook> hooks = new ArrayList<>();
		private boolean enablePostCallbacks = false;
		/**
		 * Add a hook that will get called whenever an appropriate view is created.
		 *
		 * @param hook The hook you want to add
		 * @return Config Builder
		 *
		 * @see com.dellkan.enhanced_layout_inflater.hooks
		 */
		public Builder addHook(ViewHook hook) {
			hooks.add(hook);
			return this;
		}

		public Builder removeHook(ViewHook hook) {
			hooks.remove(hook);
			return this;
		}

		/**
		 * Enable callbacks for views added programmatically
		 * @return Builder
		 */
		public Builder enablePostCallbacks() {
			enablePostCallbacks = true;
			return this;
		}

		/**
		 * Build and returns an instance of ELIConfig based on the variables you've set/changed through
		 * the builder.
		 *
		 * @return ELIConfig instance
		 */
		public ELIConfig build() {
			ELIConfig config = new ELIConfig();

			config.enablePostCallbacks = this.enablePostCallbacks;
			config.hooks = this.hooks;

			return config;
		}
	}
}
