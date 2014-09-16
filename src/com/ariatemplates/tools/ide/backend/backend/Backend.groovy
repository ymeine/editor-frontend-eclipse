package com.ariatemplates.tools.ide.backend.backend



import org.apache.http.HttpResponse
import org.apache.http.ParseException
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.eclipse.core.runtime.FileLocator

import com.ariatemplates.tools.ide.backend.backend.Backend
import com.ariatemplates.tools.ide.backend.exception.BackendException
import com.ariatemplates.tools.ide.backend.http.HTTP
import com.ariatemplates.tools.ide.backend.process_runner.ProcessRunner
import com.ariatemplates.tools.ide.document.document.Document
import com.ariatemplates.tools.ide.plugin.activator.Activator
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException



class Backend {

	/***************************************************************************
	 * Singleton
	 **************************************************************************/

	static singleton
	/**
	 * Returns a singleton object, for convenience.
	 *
	 * Indeed, nothing prevents you from creating and managing your own instances.
	 *
	 * @return A singleton.
	 */
	static get() {
		this.class.singleton = this.class.singleton ?: new Backend()
	}



	/***************************************************************************
	 * Initialization
	 *
	 * Prepares HTTP requests, JSON tools, and process management.
	 **************************************************************************/

	// ------------------------------------------------------------- HTTP / JSON

	private final gson = new Gson()

	private http = new HTTP()

	// POST --------------------------------------------------------------------

	private HttpPost rpc
	private static final HEADER_CONTENT_TYPE = "Content-Type"
	private static final HEADER_VALUE_CONTENT_TYPE = "application/json"

	// GET ---------------------------------------------------------------------

	private HttpGet shutdown
	private HttpGet ping
	private HttpGet guid

	// -------------------------------------------------------------------- URLs

	private static final URL_PORT = 50000
	private static final URL_BASE = "http://localhost:${this.URL_PORT}/"

	private static final URL_PATH_RPC = "rpc"
	private static final URL_PATH_SHUTDOWN = "shutdown"
	private static final URL_PATH_PING = "ping"
	private static final URL_PATH_GUID = "80d007698d534c3d9355667f462af2b0"



	/**
	 * Builds a new backend instance.
	 */
	def Backend() {
		this.@rpc = new HttpPost("${this.class.URL_BASE}${this.class.URL_PATH_RPC}")
		this.rpc.setHeader "${this.class.HEADER_CONTENT_TYPE}${this.class.HEADER_VALUE_CONTENT_TYPE}"

		this.shutdown = new HttpGet("${this.class.URL_BASE}${this.class.URL_PATH_SHUTDOWN}")
		this.ping = new HttpGet("${this.class.URL_BASE}${this.class.URL_PATH_PING}")
		this.guid = new HttpGet("${this.class.URL_BASE}${this.class.URL_PATH_GUID}")

		def basePath = new File("")
		try {
			basePath = new File(FileLocator.toFileURL(Activator.getDefault().getBundle().getEntry("/")).toURI())
		} catch (Exception e) {
			println "Could not resolve bundle absolute path."
			print e
		}

		this.processRunner.process_path = new File(basePath, this.class.NODE_PATH).getAbsolutePath()
		this.processRunner.arguments = [
			new File(basePath, this.class.APPLICATION_ENTRY_PATH).getAbsolutePath(),
			this.class.APPLICATION_OPTIONS
		]
	}



	/***************************************************************************
	 * Backend runtime management
	 *
	 * The thing is to be able to manage either an externally running backend,
	 * or one launched by ourself.
	 **************************************************************************/

	// FIXME
	// Use Eclipse preferences system
	// Otherwise use PATH
	// Otherwise use packaged node version, with a relative path

	private isManagedExternally = null

	private final processRunner = new ProcessRunner()
	private static final NODE_PATH = "runtime\\node"
	private static final APPLICATION_ENTRY_PATH =  "node_modules/editor-backend/app/index"
	private static final APPLICATION_OPTIONS = "--slave" // tells the node application it has been created by us and that we want to use its stdout for communication


	private static final OUTPUT_GUID = "e531ebf04fad4e17b890c0ac72789956"
	private static final POLLING_SLEEP_TIME = 50 // ms
	private static final POLLING_TIME_OUT = 1000 // ms



	// Start/Stop --------------------------------------------------------------

	/**
	 * If not already running, starts the backend.
	 *
	 * @return the created Process instance behind if so, or <code>null</code> if the backend is not managed by us
	 */
	def start() {
		def isRunning = this.isRunning()

		// Early termination ---------------------------------------------------

		if (isRunning && this.isManagedExternally) {
			return null
		}

		// Actually starts it --------------------------------------------------

		if (!isRunning) {
			// Launches the process --------------------------------------------

			this.processRunner.run()

			// Polling to check the backend is fully set up --------------------

			def started = false
			def time = 0
			while (!started && (time < this.class.POLLING_TIME_OUT)) {
				try {
					HTTP.release this.http.get this.ping
					started = true
				} catch (IOException ex) {
					Thread.sleep this.class.POLLING_SLEEP_TIME
					time += this.class.POLLING_SLEEP_TIME
				}
			}


		}

		// Return ----------------------------------------------------------

		this.processRunner.getProcess()
	}

	/**
	 * If we manage the backend process ourself and it is running, stops it by sending a specific request.
	 *
	 * If the shutdown request fails, the process is aborted with lower level utilities.
	 *
	 * @see isRunning
	 */
	def stop() {
		if (this.isRunning() && !this.isManagedExternally) {
			try {
				HTTP.release(this.http.get(this.shutdown))
			} catch (IOException exception) {
				this.processRunner.abort()
			}

			this.isManagedExternally = null
		}
	}



	// Check -------------------------------------------------------------------

	/**
	 * Tells whether the backend is running or not.
	 *
	 * @return <code>true</code> if the backend is running, <code>false</code> otherwise.
	 */
	def isRunning() {
		// We don't know if it is an external process or not yet ---------------
		// (first check in the program or after a stop)

		if (this.isManagedExternally == null) {
			if (this.isExternalBackendRunning()) {
				this.isManagedExternally = true
				return true // to avoid unnecessary duplicate request below
			} else {
				this.isManagedExternally = false
			}
		}

		// Externally managed --------------------------------------------------

		if (this.isManagedExternally) {
			return this.isExternalBackendRunning()
		}

		// We manage the process ourself ---------------------------------------

		this.processRunner.is_running()
	}

	/**
	 * Tells whether the external backend server is running or not.
	 *
	 * It uses the safe identification method, to ensure that the server has not been unfortunately replaced. Otherwise, the ping method could have been used.
	 */
	private isExternalBackendRunning() {
		try {
			HttpResponse response = this.http.get(this.guid)
			if (HTTP.getCode(response) != 200) {
				HTTP.release response
				return false
			}

			HTTP.getString(response) == Backend.OUTPUT_GUID
		} catch (IOException exception) {
			false
		}
	}





	/***************************************************************************
	 * Backend communication
	 *
	 * This is the High-level protocol implementation (RPC essentially).
	 **************************************************************************/



	// Mode service ------------------------------------------------------------

	private static final METHOD_EDITOR_EXEC = "exec"

	private static final ARGUMENT_GUID = "guid"
	private static final ARGUMENT_SERVICE = "svc"
	private static final ARGUMENT_SERVICE_ARGUMENT = "arg"

	/**
	 * For every RPC related to an editor service.
	 *
	 * @throws BackendException
	 */
	def service(guid, service, serviceArgument=null) {
		if (guid instanceof Document) {
			guid = guid.getGUID()
		}

		def argument = new HashMap<String, Object>()

		argument[this.class.ARGUMENT_GUID] = guid
		argument[this.class.ARGUMENT_SERVICE] = service

		if (serviceArgument != null) {
			argument[this.class.ARGUMENT_SERVICE_ARGUMENT] = serviceArgument
		}

		this.editor this.class.METHOD_EDITOR_EXEC, argument
	}



	// Editor module -----------------------------------------------------------

	private static final MODULE_NAME_EDITOR = "editor"

	def editor(member, argument=null) {
		this.rpc this.class.MODULE_NAME_EDITOR, member, argument
	}



	// RPC ---------------------------------------------------------------------

	/**
	 * Builds a JSON RPC request (compatible with the implementation of the backend) and executes it.
	 *
	 * @param module The name of the remote module
	 * @param member The name of the member to access inside the remote module
	 * @param argument If the member is expected to be a function, it will be called with the given argument
	 *
	 * TODO Maybe return "primitive" types too? (not necessarily a key/value collection)
	 *
	 * @return The JSON result of the RPC.
	 */
	def rpc(module, member, argument=null) {
		HashMap<String, Object> object = [
			"module": module,
			"method": member,
			"argument": argument
		]

		this.rpc.setEntity(new StringEntity(gson.toJson(object)))
		def response = this.http.post this.rpc

		def result = new HashMap<String, Object>()
		result = gson.fromJson(HTTP.getString(response), Map.class)

		switch (HTTP.getCode(response)) {
			case 200:
				result
			default:
				throw new BackendException(result)
		}
	}
}
