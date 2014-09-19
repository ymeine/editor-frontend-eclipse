package com.ariatemplates.tools.ide.plugin.activator;



import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ariatemplates.tools.ide.backend.backend.Backend;



public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "POC";
	private static Activator plugin;

	public static Activator getDefault() {
		return Activator.plugin;
	}



	public Activator() {}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		Activator.plugin = this;

		try {
			Backend.get().start();
		} catch (Exception exception) {
			System.err.println("No external server running, and could not start the backend server internally.");
			exception.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		Activator.plugin = null;
		super.stop(context);

		Backend.get().stop();
	}

}
