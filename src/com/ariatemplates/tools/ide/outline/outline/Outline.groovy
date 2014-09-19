package com.ariatemplates.tools.ide.outline.outline



import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.views.contentoutline.ContentOutlinePage



class Outline extends ContentOutlinePage {
	def input

	/***************************************************************************
	 * Init
	 **************************************************************************/

	void createControl(Composite parent) {
		super.createControl parent

		def viewer = this.treeViewer
		viewer.contentProvider = new com.ariatemplates.tools.ide.outline.content.provider.Provider()
		viewer.labelProvider = new com.ariatemplates.tools.ide.outline.label.provider.Provider()
	}

	/***************************************************************************
	 * Update
	 **************************************************************************/

	def setInput(input) {
		this.@input = input
		this.update()
	}

	def update() {
		def viewer = this.treeViewer
		if (viewer != null) {
			viewer.input = this.input
		}
	}
}
