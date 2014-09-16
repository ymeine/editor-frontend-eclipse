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
				elements = this.getList inputElement, "tree"
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
		children = this.getList parentElement, "children"
		children ?: []
	}

	@Override
	boolean hasChildren(element) {
		this.getChildren(element).length > 0
	}

	/***************************************************************************
	 * Helpers
	 **************************************************************************/

	/**
	 * Returns an array of Objects from a List contained in the given Map under the given key.
	 *
	 * What this method essentially does is taking care of type conversions for you.
	 *
	 * @param[in] element A map: must be not bull!
	 * @param[in] key The key of the value in the given map: must be not null!
	 *
	 * @returns The list of elements if found and defined, <code>null</code> otherwise.
	 * @throws ClassCastException In case the given element is not a map
	 */
	private getList(element, key) {
		list = (element as Map)[key] as List
		list?.toArray()
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	void dispose() {}
	void inputChanged(Viewer viewer, oldInput, newInput) {}
	Object getParent(element) {null}

}
