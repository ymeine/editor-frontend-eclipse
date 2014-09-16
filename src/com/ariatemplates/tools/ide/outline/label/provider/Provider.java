package com.ariatemplates.tools.ide.outline.label.provider;



import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;



public class Provider extends LabelProvider {

	private static final String KEY_LABEL = "label";

	@Override
	public String getText(Object element) {
		return (String) Map.class.cast(element).get(Provider.KEY_LABEL);
	}

}
