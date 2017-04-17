package com.dellkan.enhanced_layout_inflater;

import android.content.res.XmlResourceParser;
import android.util.AttributeSet;

public final class ELIUtils {
	public static boolean containsAttribute(AttributeSet attrs, String namespace, String attributeName) {
		// If we have both namespace and attributeName ready, there's no reason to dillydally,
		// just check directly.
		return attrs.getAttributeValue(namespace, attributeName) != null;
	}

	public static boolean containsAttribute(AttributeSet attrs, String attributeName) {
		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			if (attrs.getAttributeName(i).equals(attributeName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsNamespace(AttributeSet attrs, String namespace) {
		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			XmlResourceParser parser = (XmlResourceParser) attrs;
			if (parser.getAttributeNamespace(i).equals(namespace)) {
				return true;
			}
		}
		return false;
	}

	public static String getAttributeValue(AttributeSet attrs, String attributeName) {
		for (int i = 0; i < attrs.getAttributeCount(); i++) {
			if (attrs.getAttributeName(i).equals(attributeName)) {
				return attrs.getAttributeValue(i);
			}
		}
		return null;
	}
}
