package com.dellkan.enhanced_layout_inflater;

import android.content.res.XmlResourceParser;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class ViewAttributes {
	protected List<ViewAttribute> viewAttributes = new ArrayList<>();

	/**
	 * If you'd like, you can overwrite this in your custom ViewAttributes class.
	 * However, beware that you'll have to setup the individual attributes yourself
	 */
	protected ViewAttributes() {

	}

	public ViewAttributes(AttributeSet attrs) {
		if (attrs == null) { return; }
		XmlResourceParser parser = (XmlResourceParser) attrs;
		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			viewAttributes.add(new ViewAttribute(
					parser.getAttributeNamespace(i),
					parser.getAttributeName(i),
					parser.getAttributeValue(i)
			));
		}
	}

	public ViewAttribute getValue(String namespace, String attributeName, String value) {
		for (ViewAttribute attribute : viewAttributes) {
			if (attribute.contains(namespace, attributeName, value)) {
				return attribute;
			}
		}
		return null;
	}

	public ViewAttribute getValue(String namespace, String attributeName) {
		for (ViewAttribute attribute : viewAttributes) {
			if (attribute.contains(namespace, attributeName, null)) {
				return attribute;
			}
		}
		return null;
	}

	public ViewAttribute getValue(String attributeName) {
		for (ViewAttribute attribute : viewAttributes) {
			if (attribute.contains(null, attributeName, null)) {
				return attribute;
			}
		}
		return null;
	}

	public List<? extends ViewAttribute> getValues() {
		return new ArrayList<>(viewAttributes);
	}

	public List<? extends ViewAttribute> getValues(String namespace, String attributeName, String value) {
		List<ViewAttribute> attributes = new ArrayList<>();
		for (ViewAttribute attribute : viewAttributes) {
			if (attribute.contains(namespace, attributeName, value)) {
				attributes.add(attribute);
			}
		}
		return attributes;
	}

	public boolean contains(String namespace, String attributeName) {
        for (ViewAttribute attribute : viewAttributes) {
            if (attribute.contains(namespace, attributeName, null)) {
                return true;
            }
        }
        return false;
    }

	public boolean contains(String namespace, String attributeName, String value) {
		for (ViewAttribute attribute : viewAttributes) {
			if (attribute.contains(namespace, attributeName, value)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(String qualifiedAttributeName) {
		for (ViewAttribute attribute : viewAttributes) {
			if (attribute.contains(qualifiedAttributeName)) {
				return true;
			}
		}
		return false;
	}
}
