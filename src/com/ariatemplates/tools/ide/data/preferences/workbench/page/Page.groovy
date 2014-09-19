package com.ariatemplates.tools.ide.data.preferences.workbench.page



import org.eclipse.jface.preference.PreferencePage
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage



class Page extends PreferencePage implements IWorkbenchPreferencePage {
	Page() {}
	Page(title) {super(title)}
	Page(title, image) {super(title, image)}

	void init(IWorkbench workbench) {}
	protected Control createContents(Composite parent) {null}
}
