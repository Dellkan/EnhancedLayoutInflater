package com.dellkan.enhanced_layout_inflater.hooks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dellkan.enhanced_layout_inflater.ELIUtils;
import com.dellkan.enhanced_layout_inflater.ViewHook;

/**
 * Shortcut class to attach hooks for raw attributes (i.e attributes that are not defined in attrs.xml)
 * @param <ViewType>
 */
public abstract class RawAttributeViewHook<ViewType extends View> extends ViewHook<ViewType> {
	private String attributeNamespace;
	private String attributeName;

	public RawAttributeViewHook(String attributeNamespace, String attributeName) {
		this.attributeNamespace = attributeNamespace;
		this.attributeName = attributeName;
	}

	public RawAttributeViewHook(Class<ViewType> clz, String attributeNamespace, String attributeName) {
		super(clz);
		this.attributeNamespace = attributeNamespace;
		this.attributeName = attributeName;
	}

	@Override
	public boolean shouldTrigger(@Nullable View parent, @NonNull View view, AttributeSet attrs) {
		// Check if the view is appropriate
		if (!super.shouldTrigger(parent, view, attrs)) {
			return false;
		}

		// Don't run at all if neither namespace or attributeName is defined
		if (attributeNamespace == null && attributeName == null) {
			return false;
		}

		if (attributeNamespace != null && attributeName == null) {
			// Run if we want all attributes as long as they have the appropriate namespace
			return ELIUtils.containsNamespace(attrs, attributeNamespace);
		} else if (attributeNamespace == null) {
			// Run if we only care about the attribute, independently of the namespace
			return ELIUtils.containsAttribute(attrs, attributeName);
		} else {
			// Run if we care about a specific attribute in a specific namespace
			return ELIUtils.containsAttribute(attrs, attributeNamespace, attributeName);
		}
	}
}
