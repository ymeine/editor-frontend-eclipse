package poc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import poc.document.POCDocument;

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
	 **************************************************************************/

	// -------------------------------------------------------------------- JSON

	private Gson gson = null;

	// -------------------------------------------------------------------- HTTP

	private DefaultHttpClient httpclient = null;

	// POST --------------------------------------------------------------------

	private HttpPost rpc = null;
	private static final String URL_PATH_RPC = "rpc";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HEADER_VALUE_CONTENT_TYPE = "application/json";

	// GET ---------------------------------------------------------------------

	private HttpGet shutdown = null;
	private static final String URL_PATH_SHUTDOWN = "shutdown";

	private HttpGet ping = null;
	private static final String URL_PATH_PING = "ping";

	private HttpGet guid = null;
	private static final String URL_PATH_GUID = "80d007698d534c3d9355667f462af2b0";

	// -------------------------------------------------------------------- URLs

	private static final String URL_BASE = "http://localhost:3000/";


	//

	/**
	 * Builds a new backend instance.
	 */
	public Backend() {
		this.httpclient = new DefaultHttpClient();

		this.rpc = new HttpPost(Backend.URL_BASE + Backend.URL_PATH_RPC);
		this.rpc.setHeader(Backend.HEADER_CONTENT_TYPE, Backend.HEADER_VALUE_CONTENT_TYPE);

		this.shutdown = new HttpGet(Backend.URL_BASE + Backend.URL_PATH_SHUTDOWN);
		this.ping = new HttpGet(Backend.URL_BASE + Backend.URL_PATH_PING);
		this.guid = new HttpGet(Backend.URL_BASE + Backend.URL_PATH_GUID);

		this.gson = new Gson();
	}



	/***************************************************************************
	 * Backend runtime management
	 **************************************************************************/

	// FIXME Don't make it hard-coded and absolute!
	// Use Eclipse preferences system
	// Otherwise use PATH
	// Otherwise use packaged node version, with a relative path

	//private static final String programPath = "resources/app/";
	private static final String[] command = {
		"editor-backend"
	};

	private static final String OUTPUT_GUID = "e531ebf04fad4e17b890c0ac72789956";

	private static final int POLLING_SLEEP_TIME = 50; // ms
	private static final int POLLING_TIME_OUT = 1000; // ms

	private Process process = null;
	private Boolean isManagedExternally = null;

	/**
	 * Tells whether the external backend server is running or not.
	 *
	 * It uses the safe identification method, to ensure that the server has not been unfortunately replaced. Otherwise, the ping method could have been used.
	 */
	private Boolean isExternalBackendRunning() {
		try {
			HttpResponse response = this.get(this.guid);
			if (response.getStatusLine().getStatusCode() != 200) {
				return false;
			}

			return EntityUtils.toString(response.getEntity()).equals(Backend.OUTPUT_GUID);
		} catch (IOException exception) {
			return false;
		}
	}

	/**
	 * Tells whether the backend is running or not.
	 *
	 * @return true if the backend is running, false otherwise.
	 * @throws IOException
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

		if (process == null) return false;
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	/**
	 * If not running, starts the backend.
	 *
	 * FIXME Broken, can't automatically find the process inside PATH
	 *
	 * @return the created Process instance behind
	 */
	public Process start() throws IOException, InterruptedException {
		if (!isRunning()) {
			// Launches the process --------------------------------------------

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			//processBuilder.directory(new File(programPath));
			this.process = processBuilder.start();

			// Polling to check the backend is fully set up --------------------

			boolean started = false;
			int time = 0;
			while (!started && (time < Backend.POLLING_TIME_OUT)) {
				try {
					this.get(this.ping);
					started = true;
				} catch (IOException ex) {
					Thread.sleep(Backend.POLLING_SLEEP_TIME);
					time += Backend.POLLING_SLEEP_TIME;
				}
			}
		}

		return this.process;
	}

	/**
	 * If we manage the backend process ourself and it is running, stops it by sending a specific request, and ensures the process is stopped with process utilities.
	 *
	 * @return If the backend properly stopped under the request, returns its response (see <code>get</code>), otherwise returns <code>null</code>.
	 *
	 * @see isRunning
	 * @see get
	 *
	 * @throws IOException
	 */
	public HttpResponse stop() {
		HttpResponse response = null;

		if (!this.isManagedExternally && this.isRunning()) {
			try {
				response = this.get(this.shutdown);
			} catch (IOException exception) {
				this.process.destroy();
			}

			this.process = null;
			this.isManagedExternally = null;
		}

		return response;
	}



	/***************************************************************************
	 * Backend communication
	 **************************************************************************/

	// Mode service ------------------------------------------------------------

	private static final String METHOD_EDITOR_EXEC = "exec";

	private static final String ARGUMENT_GUID = "guid";
	private static final String ARGUMENT_SERVICE = "svc";
	private static final String ARGUMENT_SERVICE_ARGUMENT = "arg";

	public Map<String, Object> service(Map<String, Object> guid, String service) throws ParseException, IOException, JsonSyntaxException, BackendException {
		return this.service(guid, service, null);
	}

	public Map<String, Object> service(POCDocument document, String service, Object argument) throws ParseException, IOException, JsonSyntaxException, BackendException {
		return this.service(document.getGUID(), service, argument);
	}

	public Map<String, Object> service(POCDocument document, String service) throws ParseException, IOException, JsonSyntaxException, BackendException {
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
	 * FIXME The backend should allow to pass a list of arguments, instead of forcing to use an object.
	 * TODO When the backend will implement fields aliasing, change the names of the field: mod, member, arg
	 *
	 * @return The JSON result of the RPC.
	 *
	 * @see postJson
	 *
	 * @throws IOException
	 * @throws BackendException
	 */
	public Map<String, Object> rpc(String module, String member, Object argument) throws ParseException, IOException, JsonSyntaxException, BackendException {
		Map<String, Object> object = new HashMap<String, Object>();
		object.put(Backend.KEY_MODULE, module);
		object.put(Backend.KEY_MEMBER, member);
		object.put(Backend.KEY_ARGUMENT, argument);

		String json = gson.toJson(object);

		StringEntity content = new StringEntity(json);
		this.rpc.setEntity(content);

		return this.postJson(this.rpc);
	}



	/***************************************************************************
	 * Raw HTTP communication
	 **************************************************************************/

	public static String getStringFromResponse(HttpResponse response) throws ParseException, IOException {
		return EntityUtils.toString(response.getEntity());
	}

	/**
	 * Executes the given HTTP GET request with the internal HTTP client instance, and returns the response.
	 *
	 * @param request A HTTP GET request.
	 *
	 * @return the response
	 *
	 * @throws ClientProtocolException
	 * @throws ParseException
	 * @throws IOException
	 */
	private HttpResponse get(HttpGet request) throws ClientProtocolException, IOException {
		return this.httpclient.execute(request);
	}

	/**
	 * Executes the given HTTP POST request with the internal HTTP client instance, and returns the response.
	 *
	 * @param request A HTTP POST request.
	 *
	 * @return the response
	 *
	 * @throws ClientProtocolException
	 * @throws ParseException
	 * @throws IOException
	 */
	private HttpResponse post(HttpPost request) throws ClientProtocolException, IOException {
		return this.httpclient.execute(request);
	}

	// TODO Maybe return "primitive" types too? (not necessarily a key/value collection)
	/**
	 * Sends the given HTTP POST request and returns the response content as a JSON object.
	 *
	 * @param request A HTTP POST request.
	 *
	 * @return the response content as a JSON object, that is a Map of Strings/Objects in the Java system.
	 *
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonSyntaxException
	 * @throws BackendException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> postJson(HttpPost request) throws ParseException, IOException, JsonSyntaxException, BackendException {
		HttpResponse response = this.post(request);

		Map<String, Object> result = new HashMap<String, Object>();
		result = gson.fromJson(Backend.getStringFromResponse(response), Map.class);

		switch (response.getStatusLine().getStatusCode()) {
			case 200:
				return result;
			default:
				throw new BackendException(result);
		}
	}
}
