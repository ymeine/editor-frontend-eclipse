package com.ariatemplates.tools.ide.backend.process_runner



class ProcessRunner {
	def one_instance = true

	def process_path
	def arguments = []

	def process
	def exit_value



	/***************************************************************************
	 * Runtime management
	 **************************************************************************/

	// Run ---------------------------------------------------------------------

	def start() {
		def is_running = this.is_running()
		def one_instance = this.one_instance

		// Early termination ---------------------------------------------------

		if (one_instance && is_running) {
			return this.process
		}

		// Pre-processing ------------------------------------------------------

		if (!one_instance && is_running) {
			this.stop()
		}

		// Start ---------------------------------------------------------------

		// ------------------------------------------------------------- Command

		def processBuilder = new ProcessBuilder([
			this.process_path,
			*this.arguments
		])

		// -------------------------------------------------------------- Launch

		def process = processBuilder.start()
		this.process = process

		// Return --------------------------------------------------------------

		process
	}

	def run = this.&start

	// Stop --------------------------------------------------------------------

	def stop() {
		// Stopping if indeed still running ------------------------------------
		// Note that it could already have stopped by itself without we knew it

		if (this.is_running()) {
			this.process.destroy()
		}

		// ---------------------------------------------------------------------

		if (this.process != null) {
			this.exit_value = this.process.exitValue()
			this.process = null
		}

		this.exit_value
	}

	def abort = this.&stop

	// Check -------------------------------------------------------------------

	def is_running() {
		if (this.process == null) {
			return false
		}

		try {
			process.exitValue()
			false
		} catch (IllegalThreadStateException e) {
			true
		}
	}
}
