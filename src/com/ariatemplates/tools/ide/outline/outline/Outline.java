package com.ariatemplates.tools.ide.outline.outline;



import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;



public class Outline extends ContentOutlinePage {

	private Object input = null;

	/***************************************************************************
	 * Init
	 **************************************************************************/

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = this.getTreeViewer();
		viewer.setContentProvider(new com.ariatemplates.tools.ide.outline.content.provider.Provider());
		viewer.setLabelProvider(new com.ariatemplates.tools.ide.outline.label.provider.Provider());
	}

	/***************************************************************************
	 * Update
	 **************************************************************************/

	public void setInput(Object input) {
		this.input = input;
		this.update();
	}

	public void update() {
		TreeViewer viewer = this.getTreeViewer();
		if (viewer != null) {
			viewer.setInput(this.input);
		}
	}

}
