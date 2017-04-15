package com.dellkan.enhanced_layout_inflater;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;


/**
 * The purpose of this wrapper, is merely to attach itself to the running Context.
 * By overwriting {@link #getSystemService(String)} and ensuring that our own Inflater is returned
 * from it, we ensure that all inflaters all over the activity is derived from ours.
 */
public class ELIContextWrapper extends ContextWrapper {
	public ELIContextWrapper(Context base, @Nullable ELI.Builder configs) {
		super(base);
		mConfigs = configs;
	}

	private ELI mInflater;
	private ELI.Builder mConfigs;

	@Override
	public Object getSystemService(String name) {
		if (LAYOUT_INFLATER_SERVICE.equals(name)) {
			if (mInflater == null) {
				mInflater = new ELI(LayoutInflater.from(getBaseContext()), this, false, mConfigs);
			}
			return mInflater;
		}
		return super.getSystemService(name);
	}
}
