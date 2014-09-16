package com.ariatemplates.tools.ide.plugin.activator



import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext

import com.ariatemplates.tools.ide.backend.backend.Backend



class Activator extends AbstractUIPlugin {
	static final PLUGIN_ID = "POC"

	private static Activator plugin

	static Activator getDefault() {
		this.class.plugin
	}



	def Activator() {}

	void start(BundleContext context) {
		super.start context

		this.class.plugin = this

		try {
			Backend.get().start()
		} catch (exception) {
			System.err.println "No external server running, and could not start the backend server internally."
			System.err.println "Exception: "
			System.err.println exception
		}
	}

	void stop(BundleContext context) {
		this.class.plugin = null
		super.stop context

		Backend.get().stop()
	}
}
