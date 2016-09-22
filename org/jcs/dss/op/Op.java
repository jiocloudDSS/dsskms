package org.jcs.dss.op;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcs.dss.auth.DssAuth;
import org.jcs.dss.auth.DssAuthBuilder;
import org.jcs.dss.http.Request;
import org.jcs.dss.http.Response;
import org.jcs.dss.main.DssConnection;
import org.jcs.dss.utils.Utils;
///Head Class for all API operations
public abstract class Op {
	protected DssConnection conn;
	protected String httpMethod;
	protected Map<String, String> httpHeaders;
	protected String opPath;
	protected String queryStr;
	protected String queryStrForSignature;
	///Constructors
	public  Op(DssConnection conn) {
		this.conn = conn;
		httpHeaders = new HashMap<String, String>();
		httpHeaders.put("Authorization", "");
		httpHeaders.put("Date", "");
		queryStr = "";
		opPath = "";
	}
	/// Processes the final result
	public abstract Object processResult(Object result) throws Exception;
	///This method first gets signature, sets httpHeaders and then gets Response object
	/**
	 * @return Response : response object by calling request method under Request class
	 */
	public Response makeRequest() throws Exception {
		String date = Utils.getCurTimeInGMTString();
		///Creating object of DssAuth to get signature
		DssAuth authentication = new DssAuthBuilder()
				.httpMethod(httpMethod)
				.accessKey(conn.getAccessKey())
				.secretKey(conn.getSecretKey())
				.path(opPath)
				.dateStr(date)
				.queryStr(queryStr)
				.build();
		String signature = authentication.getSignature();
		//Assigning headers
		httpHeaders.put("Authorization", signature);
		httpHeaders.put("Date", date);
		String path = Utils.getEncodedURL(opPath);
		String request_url = conn.getHost() + path;
		if(queryStr != ""){
			request_url += '?' + queryStr;  
		}
		//Calling Request.request method to execute APIs
		Response resp =  Request.request(httpMethod,request_url,httpHeaders);
		return resp;
	}





}
