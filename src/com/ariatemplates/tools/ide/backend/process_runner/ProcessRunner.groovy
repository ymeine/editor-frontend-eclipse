package com.ariatemplates.tools.ide.backend.process_runner



class ProcessRunner {
	def oneInstance = true

	def processPath
	def arguments = []

	def process
	def exitValue



	/***************************************************************************
	 * Runtime management
	 **************************************************************************/

	// Run ---------------------------------------------------------------------

	def start() {
		def isRunning = this.isRunning()
		def oneInstance = this.oneInstance

		// Early termination ---------------------------------------------------

		if (oneInstance && isRunning) {
			return this.process
		}

		// Pre-processing ------------------------------------------------------

		if (!oneInstance && isRunning) {
			this.stop()
		}

		// Start ---------------------------------------------------------------

		// ------------------------------------------------------------- Command

		def processBuilder = new ProcessBuilder([
			this.processPath,
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

		if (this.isRunning()) {
			this.process.destroy()
		}

		// ---------------------------------------------------------------------

		if (this.process != null) {
			this.exitValue = this.process.exitValue()
			this.process = null
		}

		this.exitValue
	}

	def abort = this.&stop

	// Check -------------------------------------------------------------------

	def isRunning() {
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
