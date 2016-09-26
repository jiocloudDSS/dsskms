package org.jcs.dss.op;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.jcs.dss.auth.DssAuth;
import org.jcs.dss.auth.DssAuthBuilder;
import org.jcs.dss.http.ErrorResponse;
import org.jcs.dss.http.Response;
import org.jcs.dss.main.DssConnection;
import org.jcs.dss.utils.Utils;
/// Class to download object file from request key to desired path
public class GetObjectOp extends ObjectOp {
	///Constructors
	public GetObjectOp(DssConnection conn, String bucketName, String objectName) {
		super(conn, bucketName, objectName);
		httpMethod="GET";
		opPath = '/' + bucketName + '/' + objectName;
	}
	/// Executes the method requested by user
	/**
	 * 
	 * @return Response : Gets response object returned from makeRequest()
	 * @throws Exception
	 */
	
	public String Execute() throws Exception {
		return MakeRequest();
	}
	///This method first gets signature, sets httpHeaders and then gets Response object
	/**
	 * @return Response : response object by calling request method under Request class
	 * @throws Exception
	 */
	
	public String MakeRequest() throws Exception {
		String date = Utils.getCurTimeInGMTString();
		///Creating object of DssAuth to get signature
		DssAuth authentication = new DssAuthBuilder()
				.httpMethod(httpMethod)
				.accessKey(conn.getAccessKey())
				.secretKey(conn.getSecretKey())
				.path(opPath)
				.dateStr(date)
				.build();
		String signature = authentication.getSignature();
		//Assigning headers
		httpHeaders.put("Authorization", signature);
		httpHeaders.put("Date", date);
		String path = Utils.getEncodedURL(opPath);
		String request_url = conn.getHost() + path;
		//Calling Request.request method to get inputStream
		//Response resp = Request.request("GET", request_url,httpHeaders);
		
		return request_url;
	}

	@Override
	///Process final step to download file to desired path
	/**
	 * @param Response : Response message got from Request.request()
	 * @return String : Message for successful or unsuccessful file download
	 * @throws IOException
	 */
	public Response processResult(Object request) throws Exception{
		
		URL requestUrl = new URL((String) request);
		HttpsURLConnection Connection = (HttpsURLConnection) requestUrl.openConnection();
		Connection.setSSLSocketFactory(Utils.getSslFactory());
		Connection.setDoOutput(true);
		Connection.setDoInput(true);
		//Setting HTTP Method
		Connection.setRequestMethod("GET");
		// Setting request headers
		for(Entry<String, String> entry : httpHeaders.entrySet()) {
			Connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
		//Checks if server has returned message OK 
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
