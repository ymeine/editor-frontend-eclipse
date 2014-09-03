package poc.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



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
			process.exitValue();
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

	public ProcessRunner setOneInstance(boolean oneInstance) {
		this.oneInstance = oneInstance;
		
		return this;
	}
	
	

	public String getProcessPath() {
		return processPath;
	}

	public ProcessRunner setProcessPath(String processPath) {
		this.processPath = processPath;
		
		return this;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public ProcessRunner setArguments(List<String> arguments) {
		this.arguments = arguments;
		
		return this;
	}
	
	public ProcessRunner setArguments(String... arguments) {
		return this.setArguments(Arrays.asList(arguments));
	}

	
	
	public Integer getExitValue() {
		return exitValue;
	}

	public Process getProcess() {
		return process;
	}
}
