package com.ariatemplates.tools.ide.backend.process_runner;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ariatemplates.tools.ide.backend.process_runner.ProcessRunner;



public class ProcessRunner {

	private boolean oneInstance = true;

	private String processPath = null;
	private List<String> arguments = new ArrayList<String>(0);

	private Process process = null;
	private Integer exitValue = null;



	/***************************************************************************
	 * Runtime management
	 **************************************************************************/

	// Run ---------------------------------------------------------------------

	public Process start() throws IOException {
		boolean isRunning = this.isRunning();

		// Early termination ---------------------------------------------------

		if (this.oneInstance && isRunning) {
			return this.process;
		}



		// Pre-processing ------------------------------------------------------

		if (!this.oneInstance && isRunning) {
			this.stop();
		}


		// Start ---------------------------------------------------------------

		// ------------------------------------------------------------- Command

		ProcessBuilder processBuilder = new ProcessBuilder(this.getCommand());

		// -------------------------------------------------------------- Launch

		this.process = processBuilder.start();



		// Return --------------------------------------------------------------

		return this.process;
	}

	public Process run() throws IOException {return this.start();} // alias

	public List<String> getCommand() {
		List<String> arguments = this.getArguments();
		List<String> command = new ArrayList<String>(1 + arguments.size());

		command.add(this.getProcessPath());
		command.addAll(arguments);

		return command;
	}

	// Stop --------------------------------------------------------------------

	public int stop() {
		// Stopping if indeed still running ------------------------------------
		// Note that it could already have stopped by itself without we knew it

		if (this.isRunning()) {
			this.process.destroy();
		}

		// ---------------------------------------------------------------------

		if (this.process != null) {
			this.exitValue = this.process.exitValue();
			this.process = null;
		}

		return this.exitValue;
	}

	public int abort() {return this.stop();} // alias

	// Check -------------------------------------------------------------------

	public boolean isRunning() {
		if (this.process == null) {
			return false;
		}

		try {
			this.process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}



	/***************************************************************************
	 * Configuration (getters & setters)
	 **************************************************************************/

	public boolean isOneInstance() {
		return oneInstance;
	}

	public void setOneInstance(boolean oneInstance) {
		this.oneInstance = oneInstance;
	}



	public String getProcessPath() {
		return processPath;
	}

	public void setProcessPath(String processPath) {
		this.processPath = processPath;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public void setArguments(String... arguments) {
		this.setArguments(Arrays.asList(arguments));
	}



	public Integer getExitValue() {
		return this.exitValue;
	}

	public Process getProcess() {
		return this.process;
	}

}
