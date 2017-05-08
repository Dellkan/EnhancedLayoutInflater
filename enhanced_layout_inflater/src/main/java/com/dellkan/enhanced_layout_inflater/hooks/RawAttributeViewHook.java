package com.dellkan.enhanced_layout_inflater.hooks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dellkan.enhanced_layout_inflater.ELIContext;
import com.dellkan.enhanced_layout_inflater.ViewAttributes;
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

	public String getAttributeNamespace() {
		return attributeNamespace;
	}

	public String getAttributeName() {
		return attributeName;
	}

	@Override
	public void onViewCreatedRaw(ELIContext eliContext, @Nullable View parent, @NonNull ViewType view, @Nullable AttributeSet attrs) {
		ViewAttributes attributes = new ViewAttributes(attrs);
		if (shouldTrigger(eliContext, parent, view, attrs)) {
			onViewCreated(eliContext, parent, view, attrs);
			onViewCreated(eliContext, parent, view, attributes);
		}
	}

	public boolean shouldTrigger(ELIContext eliContext, @Nullable View parent, @NonNull View view, ViewAttributes attributes) {
		// Check if the view is appropriate
		if (!super.shouldTrigger(eliContext, parent, view, null)) {
			return false;
		}

		// Don't run at all if neither namespace or attributeName is defined
		if (attributeNamespace == null && attributeName == null) {
			return false;
		}

		return attributes.contains(attributeNamespace, attributeName, null);
	}

	/**
	 * In all likelyhood, you'll get more use out of {@link #onViewCreated(ELIContext, View, View, ViewAttributes)}.
	 * This will still be called, in case you need it though.
	 * @param eliContext A summary context containing all the relevant pieces of information used in an inflation
	 * through ELI
	 * @param parent
	 * @param view Target view
	 * @param attrs Attributes on the view, in raw form
	 *
	 * @see #onViewCreated(ELIContext, View, View, ViewAttributes)
	 * @see #onViewCreatedRaw(ELIContext, View, View, AttributeSet)
	 */
	@Override
	public void onViewCreated(ELIContext eliContext, @Nullable View parent, @NonNull ViewType view, @Nullable AttributeSet attrs) {

	}

	public abstract void onViewCreated(ELIContext eliContext, @Nullable View parent, @NonNull ViewType view, @NonNull ViewAttributes attrs);
}
