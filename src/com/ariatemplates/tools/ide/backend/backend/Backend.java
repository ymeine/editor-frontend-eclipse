package com.ariatemplates.tools.ide.backend.backend;



import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.eclipse.core.runtime.FileLocator;

import com.ariatemplates.tools.ide.backend.backend.Backend;
import com.ariatemplates.tools.ide.backend.exception.BackendException;
import com.ariatemplates.tools.ide.backend.http.HTTP;
import com.ariatemplates.tools.ide.backend.process_runner.ProcessRunner;
import com.ariatemplates.tools.ide.document.document.Document;
import com.ariatemplates.tools.ide.plugin.activator.Activator;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;



public class Backend {

	/***************************************************************************
	 * Singleton
	 **************************************************************************/

	private static Backend singleton = null;

	/**
	 * Returns a singleton object, for convenience.
	 *
	 * Indeed, nothing prevents you from creating and managing your own instances.
	 *
	 * @return A singleton.
	 */
	public static Backend get() {
		if (Backend.singleton == null) {
			Backend.singleton = new Backend();
		}

		return Backend.singleton;
	}



	/***************************************************************************
	 * Initialization
	 *
	 * Prepares HTTP requests, JSON tools, and process management.
	 **************************************************************************/

	// ------------------------------------------------------------- HTTP / JSON

	private final Gson gson = new Gson();

	private HTTP http = new HTTP();

	// POST --------------------------------------------------------------------

	private HttpPost rpc = null;
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HEADER_VALUE_CONTENT_TYPE = "application/json";

	// GET ---------------------------------------------------------------------

	private HttpGet shutdown = null;
	private HttpGet ping = null;
	private HttpGet guid = null;

	// -------------------------------------------------------------------- URLs

	private static final int URL_PORT = 50000;
	private static final String URL_BASE = "http://localhost:" + Backend.URL_PORT + "/";

	private static final String URL_PATH_RPC = "rpc";
	private static final String URL_PATH_SHUTDOWN = "shutdown";
	private static final String URL_PATH_PING = "ping";
	private static final String URL_PATH_GUID = "80d007698d534c3d9355667f462af2b0";


	//

	/**
	 * Builds a new backend instance.
	 */
	public Backend() {
		this.rpc = new HttpPost(Backend.URL_BASE + Backend.URL_PATH_RPC);
		this.rpc.setHeader(Backend.HEADER_CONTENT_TYPE, Backend.HEADER_VALUE_CONTENT_TYPE);

		this.shutdown = new HttpGet(Backend.URL_BASE + Backend.URL_PATH_SHUTDOWN);
		this.ping = new HttpGet(Backend.URL_BASE + Backend.URL_PATH_PING);
		this.guid = new HttpGet(Backend.URL_BASE + Backend.URL_PATH_GUID);

		File basePath = new File("");
		try {
			basePath = new File(FileLocator.toFileURL(Activator.getDefault().getBundle().getEntry("/")).toURI());
		} catch (Exception e) {
			System.err.println("Could not resolve bundle absolute path.");
			System.err.print(e);
		}

		this.processRunner.setProcessPath(new File(basePath, Backend.NODE_PATH).getAbsolutePath());
		this.processRunner.setArguments(
			new File(basePath, Backend.APPLICATION_ENTRY_PATH).getAbsolutePath(),
			Backend.APPLICATION_OPTIONS
		);
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

	private Boolean isManagedExternally = null;

	private final ProcessRunner processRunner = new ProcessRunner();
	private static final String NODE_PATH = "runtime\\node";
	private static final String APPLICATION_ENTRY_PATH =  "node_modules/editor-backend/app/index";
	private static final String APPLICATION_OPTIONS = "--slave"; // tells the node application it has been created by us and that we want to use its stdout for communication


	private static final String OUTPUT_GUID = "e531ebf04fad4e17b890c0ac72789956";
	private static final int POLLING_SLEEP_TIME = 50; // ms
	private static final int POLLING_TIME_OUT = 1000; // ms



	// Start/Stop --------------------------------------------------------------

	/**
	 * If not already running, starts the backend.
	 *
	 * @return the created Process instance behind if so, or <code>null</code> if the backend is not managed by us
	 */
	public Process start() throws IOException, InterruptedException {

		boolean isRunning = this.isRunning();

		// Early termination ---------------------------------------------------

		if (isRunning && this.isManagedExternally) {
			return null;
		}

		// Actually starts it --------------------------------------------------

		if (!isRunning) {
			// Launches the process --------------------------------------------

			this.processRunner.run();

			// Polling to check the backend is fully set up --------------------

			boolean started = false;
			int time = 0;
			while (!started && (time < Backend.POLLING_TIME_OUT)) {
				try {
					HTTP.release(this.http.get(this.ping));
					started = true;
				} catch (IOException ex) {
					Thread.sleep(Backend.POLLING_SLEEP_TIME);
					time += Backend.POLLING_SLEEP_TIME;
				}
			}


		}

		// Return ----------------------------------------------------------

		return this.processRunner.getProcess();
	}

	/**
	 * If we manage the backend process ourself and it is running, stops it by sending a specific request.
	 *
	 * If the shutdown request fails, the process is aborted with lower level utilities.
	 *
	 * @see isRunning
	 */
	public void stop() {
		if (this.isRunning() && !this.isManagedExternally) {
			try {
				HTTP.release(this.http.get(this.shutdown));
			} catch (IOException exception) {
				this.processRunner.abort();
			}

			this.isManagedExternally = null;
		}
	}



	// Check -------------------------------------------------------------------

	/**
	 * Tells whether the backend is running or not.
	 *
	 * @return <code>true</code> if the backend is running, <code>false</code> otherwise.
	 */
	public Boolean isRunning() {
		// We don't know if it is an external process or not yet ---------------
		// (first check in the program or after a stop)

		if (this.isManagedExternally == null) {
			if (this.isExternalBackendRunning()) {
				this.isManagedExternally = true;
				return true; // to avoid unnecessary duplicate request below
			} else {
				this.isManagedExternally = false;
			}
		}

		// Externally managed --------------------------------------------------

		if (this.isManagedExternally) {
			return this.isExternalBackendRunning();
		}

		// We manage the process ourself ---------------------------------------

		return this.processRunner.isRunning();
	}

	/**
	 * Tells whether the external backend server is running or not.
	 *
	 * It uses the safe identification method, to ensure that the server has not been unfortunately replaced. Otherwise, the ping method could have been used.
	 */
	private Boolean isExternalBackendRunning() {
		try {
			HttpResponse response = this.http.get(this.guid);
			if (HTTP.getCode(response) != 200) {
				HTTP.release(response);
				return false;
			}

			return HTTP.getString(response).equals(Backend.OUTPUT_GUID);
		} catch (IOException exception) {
			return false;
		}
	}





	/***************************************************************************
	 * Backend communication
	 *
	 * This is the High-level protocol implementation (RPC essentially).
	 **************************************************************************/



	// Mode service ------------------------------------------------------------

	private static final String METHOD_EDITOR_EXEC = "exec";

	private static final String ARGUMENT_GUID = "guid";
	private static final String ARGUMENT_SERVICE = "svc";
	private static final String ARGUMENT_SERVICE_ARGUMENT = "arg";

	public Map<String, Object> service(Map<String, Object> guid, String service) throws ParseException, IOException, JsonSyntaxException, BackendException {
		return this.service(guid, service, null);
	}

	public Map<String, Object> service(Document document, String service, Object argument) throws ParseException, IOException, JsonSyntaxException, BackendException {
		return this.service(document.getGUID(), service, argument);
	}

	public Map<String, Object> service(Document document, String service) throws ParseException, IOException, JsonSyntaxException, BackendException {
		return this.service(document, service, null);
	}

	/**
	 * For every RPC related to an editor service.
	 *
	 * @throws BackendException
	 */
	public Map<String, Object> service(Map<String, Object> guid, String service, Object serviceArgument) throws ParseException, IOException, JsonSyntaxException, BackendException {
		Map<String, Object> argument = new HashMap<String, Object>();

		argument.put(Backend.ARGUMENT_GUID, guid);
		argument.put(Backend.ARGUMENT_SERVICE, service);
		if (serviceArgument != null) {
			argument.put(Backend.ARGUMENT_SERVICE_ARGUMENT, serviceArgument);
		}

		return this.editor(Backend.METHOD_EDITOR_EXEC, argument);
	}



	// Editor module -----------------------------------------------------------

	private static final String MODULE_NAME_EDITOR = "editor";

	public Map<String, Object> editor(String member) throws JsonSyntaxException, ParseException, IOException, BackendException {
		return this.editor(member, null);
	}

	public Map<String, Object> editor(String member, Object argument) throws JsonSyntaxException, ParseException, IOException, BackendException {
		return this.rpc(Backend.MODULE_NAME_EDITOR, member, argument);
	}



	// RPC ---------------------------------------------------------------------

	private static final String KEY_MODULE = "module";
	private static final String KEY_MEMBER = "method";
	private static final String KEY_ARGUMENT = "argument";

	public Map<String, Object> rpc(String module, String member) throws IOException, JsonSyntaxException, ParseException, BackendException {
		return this.rpc(module, member, null);
	}

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
	@SuppressWarnings("unchecked")
	public Map<String, Object> rpc(String module, String member, Object argument) throws ParseException, IOException, JsonSyntaxException, BackendException {
		Map<String, Object> object = new HashMap<String, Object>();
		object.put(Backend.KEY_MODULE, module);
		object.put(Backend.KEY_MEMBER, member);
		object.put(Backend.KEY_ARGUMENT, argument);

		this.rpc.setEntity(new StringEntity(gson.toJson(object)));
		HttpResponse response = this.http.post(this.rpc);

		Map<String, Object> result = new HashMap<String, Object>();
		result = gson.fromJson(HTTP.getString(response), Map.class);

		switch (HTTP.getCode(response)) {
			case 200:
				return result;
			default:
				throw new BackendException(result);
		}
	}

}
