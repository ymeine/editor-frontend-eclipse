package com.ariatemplates.tools.ide.outline.label.provider



import org.eclipse.jface.viewers.LabelProvider



class Provider extends LabelProvider {
	String getText(element) {element["label"]}
}
