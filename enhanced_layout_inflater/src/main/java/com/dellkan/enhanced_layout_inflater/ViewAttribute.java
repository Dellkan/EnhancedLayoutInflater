package com.dellkan.enhanced_layout_inflater;

public class ViewAttribute {
	private String namespace;
	private String attributeName;
	private String value;

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

	public String getNamespace() {
		return namespace;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getValue() {
		return value;
	}
}
