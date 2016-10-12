package org.jcs.dss.http;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.*;

import org.jcs.dss.main.Config;
import org.jcs.dss.utils.Utils;
/// Class to make connection to JCS DSS Server
public class Request {
	///To create connection and to store response in resp object of Response class
	/**
	 * @param method : HTTP method
	 * @param url : Url of the server
	 * @param headers 
	 * @return resp : An object of Response class
	 * @throws Exception
	 */
	public static Response request(String method, String url, Map<String, String> headers) throws Exception {
		// Create a trust manager that does not validate certificate chains
		URL requestUrl = new URL(url);
		HttpURLConnection Connection = null;
		if(!Config.isSecure())
		{
			TrustManager[] trustAllCerts = new TrustManager[] 
					{	new X509TrustManager() 
					{
						public java.security.cert.X509Certificate[] getAcceptedIssuers() 
						{
							return new X509Certificate[0];
						}
						public void checkClientTrusted(X509Certificate[] certs, String authType) 
						{
						}
						public void checkServerTrusted(X509Certificate[] certs, String authType) 
						{
						}
					}
					};

			// Install the all-trusting trust manager
			SSLContext sc;
			try 
			{
				sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				Connection = (HttpURLConnection) requestUrl.openConnection();
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} 
			catch (NoSuchAlgorithmException e) 
			{
				e.printStackTrace();
			} 
			catch (KeyManagementException e) {
				e.printStackTrace();
			}
		} else {
			Connection = (HttpsURLConnection) requestUrl.openConnection();
//			Connection.setSSLSocketFactory(Utils.getSslFactory());
		}
		Connection.setDoOutput(true);
		Connection.setDoInput(true);
		//Setting HTTP Method
		Connection.setRequestMethod(method);
		// Setting request headers
		for(Entry<String, String> entry : headers.entrySet()) {
			Connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
		//Creating connection
		//Connection.connect();
		Response resp = new Response ();
		//If Operation succeed 
		if (Connection.getResponseCode() == 200 || Connection.getResponseCode() == 204) {
			resp.setStatusCode(Connection.getResponseCode());
			resp.setStatusMsg(Connection.getResponseMessage());
			resp.setHeaders(Connection.getHeaderFields());
			resp.setData(Connection.getInputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
			String XML= input.readLine();
			resp.setXMLString(XML);
		}
		//If operation fails throw ErrorException 
		else {
			throw (new ErrorResponse(Connection.getResponseCode(),Connection.getResponseMessage(),Connection.getErrorStream()));
		}
		Connection.disconnect();
		return resp;

	}

	///To create connection, to upload object to the server and to store response in resp object of Response class
	/**
	 * 
	 * @param httpMethod
	 * @param Url : Url of the server
	 * @param HttpHeader
	 * @param Data : InputStream of the file to be upload
	 * @return : resp : An object of Response class
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws ErrorResponse
	 */
	public static Response Put(String httpMethod,String Url,Map<String, String> HttpHeader,InputStream Data) throws InvalidKeyException, NoSuchAlgorithmException, IOException, ErrorResponse{

		// Create a trust manager that does not validate certificate chains
		URL RequestUrl = new URL(Url);
		HttpURLConnection Connection = null;
		if(!Config.isSecure())
		{
			TrustManager[] trustAllCerts = new TrustManager[] 
					{	new X509TrustManager() 
					{
						public java.security.cert.X509Certificate[] getAcceptedIssuers() 
						{
							return new X509Certificate[0];
						}
						public void checkClientTrusted(X509Certificate[] certs, String authType) 
						{
						}
						public void checkServerTrusted(X509Certificate[] certs, String authType) 
						{
						}
					}
					};

			// Install the all-trusting trust manager
			SSLContext sc;
			try 
			{
				sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				Connection = (HttpURLConnection)RequestUrl.openConnection();
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} 
			catch (NoSuchAlgorithmException e) 
			{
				e.printStackTrace();
			} 
			catch (KeyManagementException e) {
				e.printStackTrace();
			}
		} else {
			Connection = (HttpsURLConnection)RequestUrl.openConnection();
			((HttpsURLConnection) Connection).setSSLSocketFactory(Utils.getSslFactory());
		}
		Connection.setDoOutput(true);
		//Setting HTTP Method
		Connection.setRequestMethod(httpMethod);
		// Setting request headers
		for(Entry<String, String> entry : HttpHeader.entrySet()) {
			Connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
		//Creating connection
		//Connection.connect();
		//Uploading Data in smaller parts of 4096 byte by reading data in buffer and then writing till the end of file
		DataOutputStream out = new DataOutputStream(Connection.getOutputStream());
		byte[] buffer = new byte[4096];
		int bytesRead=Data.read(buffer);
		while ( bytesRead != -1) {
			out.write(buffer,0,bytesRead);
			bytesRead=Data.read(buffer);
		}
		Data.close();
		out.close();
		Response resp = new Response();
		//If Operation succeed 
		if (Connection.getResponseCode() == 200 || Connection.getResponseCode() == 204) {
			resp.setStatusCode(Connection.getResponseCode());
			resp.setStatusMsg(Connection.getResponseMessage());
			resp.setHeaders(Connection.getHeaderFields());
			resp.setData(Connection.getInputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
			String XML= input.readLine();
			resp.setXMLString(XML);
		} 
		//If operation fails throw ErrorException 
		else {
			throw (new ErrorResponse(Connection.getResponseCode(),Connection.getResponseMessage(),Connection.getErrorStream()));
		}
		Connection.disconnect();
		return resp;
	}
}


