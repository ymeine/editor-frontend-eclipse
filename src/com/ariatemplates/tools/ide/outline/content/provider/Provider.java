package com.ariatemplates.tools.ide.outline.content.provider;


import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ariatemplates.tools.ide.outline.content.provider.Provider;

public class Provider implements ITreeContentProvider {

	private static final String KEY_ROOT = "tree";
	private static final String KEY_CHILDREN = "children";

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
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null) {
			try {
				Object[] elements = this.getList(inputElement, Provider.KEY_ROOT);
				if (elements != null) {
					return elements;
				}
			} catch (ClassCastException exception) {}
		}

		return new Object[0];
	}

	/**
	 * We assume that once this kind of method gets called, it means that the input is accurate.
	 * Thus, not every possible errors will be handled here.
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = this.getList(parentElement, Provider.KEY_CHILDREN);
		return children != null ? children : new Object[0];
	}

	@Override
	public boolean hasChildren(Object element) {
		return this.getChildren(element).length > 0;
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
	private Object[] getList(Object element, String key) throws ClassCastException {
		List<?> list = List.class.cast(Map.class.cast(element).get(key));
		return list != null ? list.toArray() : null;
	}

	/***************************************************************************
	 * Unused
	 **************************************************************************/

	@Override public void dispose() {}
	@Override public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	@Override public Object getParent(Object element) {return null;}
	
}
