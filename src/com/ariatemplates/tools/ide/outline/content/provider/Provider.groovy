package com.ariatemplates.tools.ide.outline.content.provider



import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.Viewer



class Provider implements ITreeContentProvider {
	/***************************************************************************
	 * Elements access
	 **************************************************************************/

	/**
	 * This method handles all possible input errors:
	 *
	 * - input is null
	 * - input is not a Map
	 * - input is a Map but doesn't contain the root key
	 * - input contains the root key but the value under is null
	 */
	Object[] getElements(inputElement) {
		if (inputElement != null) {
			try {
				def elements = inputElement["tree"]
				if (elements != null) {
					return elements
				}
			} catch (exception) {}
		}

		[]
	}

	/**
	 * We assume that once this kind of method gets called, it means that the input is accurate.
	 * Thus, not every possible errors will be handled here.
	 */
	@Override
	Object[] getChildren(parentElement) {
		parentElement["children"] ?: []
	}

	@Override
	boolean hasChildren(element) {
		this.getChildren(element).length > 0
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	void dispose() {}
	void inputChanged(Viewer viewer, oldInput, newInput) {}
	Object getParent(element) {null}

}
