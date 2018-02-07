package com.dellkan.enhanced_layout_inflater;

import java.util.Arrays;

public class ViewAttribute {
	private String namespace;
	private String attributeName;
	private String value;

	public ViewAttribute(String qualifiedAttributeName) {
		int delimiter = qualifiedAttributeName.lastIndexOf(":");
		this.namespace = qualifiedAttributeName.substring(0, delimiter);
		this.attributeName = qualifiedAttributeName.substring(delimiter + 1);
	}

	public ViewAttribute(String namespace, String attributeName, String value) {
		this.namespace = namespace;
		this.attributeName = attributeName;
		this.value = value;
	}

	public boolean contains(String namespace, String attributeName, String value) {
		return  !(namespace != null && !namespace.equals(this.namespace)) &&
				!(attributeName != null && !attributeName.equals(this.attributeName)) &&
				!(value != null && !value.equals(this.value));
	}

	public boolean contains(String qualifiedAttributeName) {
	    return this.getQualifiedAttributeName().equals(qualifiedAttributeName);
    }

	public String getNamespace() {
		return namespace;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getRawValue() {
		return value;
	}

	public String getQualifiedAttributeName() {
		return String.format("%s:%s", namespace, attributeName);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ViewAttribute)) {
			return false;
		}
		ViewAttribute otherAttribute = (ViewAttribute) other;
		return Utils.equals(this.namespace, otherAttribute.namespace) && Utils.equals(this.attributeName, otherAttribute.attributeName);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{namespace, attributeName, value});
	}
}
