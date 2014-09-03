package com.ariatemplates.tools.ide.data.preferences.workbench.page;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class Page extends PreferencePage implements IWorkbenchPreferencePage {

	public Page() {}
	public Page(String title) {super(title);}
	public Page(String title, ImageDescriptor image) {super(title, image);}
	
	@Override public void init(IWorkbench workbench) {}
	@Override protected Control createContents(Composite parent) {return null;}

}
