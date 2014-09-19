package com.ariatemplates.tools.ide.backend.http



import org.apache.http.HttpResponse
import org.apache.http.ParseException
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils



class HTTP {
	private final httpclient = new DefaultHttpClient()



	static getString(response) {
		EntityUtils.toString response.entity
	}

	static getCode(response) {
		response.statusLine.statusCode
	}

	static release(response) {
		response.entity.content.close()
	}



	/**
	 * Executes the given HTTP GET request with the internal HTTP client instance, and returns the response.
	 *
	 * @param request A HTTP GET request.
	 *
	 * @return the response
	 */
	def get(request) {
		this.httpclient.execute request
	}

	/**
	 * Executes the given HTTP POST request with the internal HTTP client instance, and returns the response.
	 *
	 * @param request A HTTP POST request.
	 *
	 * @return the response
	 */
	def post(request) {
		this.httpclient.execute request
	}
}
