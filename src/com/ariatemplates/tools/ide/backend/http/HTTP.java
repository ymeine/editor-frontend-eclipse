package com.ariatemplates.tools.ide.backend.http;



import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



public class HTTP {

	private final DefaultHttpClient httpclient = new DefaultHttpClient();



	static public String getString(HttpResponse response) throws ParseException, IOException {
		return EntityUtils.toString(response.getEntity());
	}

	static public int getCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}

	static public void release(HttpResponse response) throws IllegalStateException, IOException {
		response.getEntity().getContent().close();
	}



	/**
	 * Executes the given HTTP GET request with the internal HTTP client instance, and returns the response.
	 *
	 * @param request A HTTP GET request.
	 *
	 * @return the response
	 */
	public HttpResponse get(HttpGet request) throws ClientProtocolException, IOException {
		return this.httpclient.execute(request);
	}

	/**
	 * Executes the given HTTP POST request with the internal HTTP client instance, and returns the response.
	 *
	 * @param request A HTTP POST request.
	 *
	 * @return the response
	 */
	public HttpResponse post(HttpPost request) throws ClientProtocolException, IOException {
		return this.httpclient.execute(request);
	}

}
