package com.dellkan.enhanced_layout_inflater;

import android.support.annotation.LayoutRes;

import java.util.HashMap;
import java.util.Map;

public final class ELIContext {
	private ELI eli;
	private ELIConfig config;
	private @LayoutRes int layoutFile;
	private boolean attachToRoot;
	private Map<String, Object> data = new HashMap<>();

	private ELIContext(ELI eli, ELIConfig config, int layoutFile, boolean attachToRoot, Map<String, Object> data) {
		this.eli = eli;
		this.config = config;
		this.layoutFile = layoutFile;
		this.attachToRoot = attachToRoot;
		this.data.putAll(data);
	}

	public ELI getLayoutInflater() {
		return eli;
	}

	public ELIConfig getConfig() {
		return config;
	}

	public @LayoutRes int getLayoutFile() {
		return layoutFile;
	}

	public boolean isAttachToRoot() {
		return attachToRoot;
	}

	public Map<String, Object> getData() {
		return new HashMap<>(data);
	}

	public static class Builder {
		private Map<String, Object> data = new HashMap<>();

		public Builder addData(String key, Object value) {
			this.data.put(key, value);
			return this;
		}

		public ELIContext build(ELI eli, ELIConfig config, int layoutFile, boolean attachToRoot) {
			return new ELIContext(eli, config, layoutFile, attachToRoot, data);
		}
	}
}
