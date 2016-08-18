package org.jcs.dss.op;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jcs.dss.auth.DssAuth;
import org.jcs.dss.auth.DssAuthBuilder;
import org.jcs.dss.http.Request;
import org.jcs.dss.http.Response;
import org.jcs.dss.main.DssConnection;
import org.jcs.dss.main.PutObjectResult;
import org.jcs.dss.utils.Utils;
/// Class to Upload object file to the requested DSS bucket
public class PutObjectOp extends ObjectOp{
	private String filePath;
	///Constructors
	public PutObjectOp(DssConnection conn,String bucketName, String objectName,String filepath) throws FileNotFoundException {
		super(conn,bucketName,objectName);
		filePath= filepath;
		httpMethod="PUT";
		opPath = '/' + bucketName + '/' + objectName;
	}
	/// Executes the method requested by user
	/**
	 * 
	 * @return Response : Gets response object returned from makeRequest()
	 * @throws Exception
	 */
	@Override
	public Response execute() throws Exception {
		return makeRequest();
	}
	///This method first gets signature, sets httpHeaders and then gets Response object
	/**
	 * @return Response : response object by calling put method under Request class
	 * @throws Exception
	 */
	@Override
	public Response makeRequest() throws Exception {
		String date = Utils.getCurTimeInGMTString();
		///Creating object of DssAuth to get signature
		DssAuth authentication = new DssAuthBuilder()
				.httpMethod(httpMethod)
				.accessKey(conn.getAccessKey())
				.secretKey(conn.getSecretKey())
				.path(opPath)
				.dateStr(date)
				.contentType("application/octet-stream")
				.build();
		String signature = authentication.getSignature();
		// Creating inputstream of File to be uploaded
		InputStream object = new ByteArrayInputStream(filePath.getBytes());
		//Assigning Headers
		httpHeaders.put("Authorization", signature);
		httpHeaders.put("Date", date);
		httpHeaders.put("Content-Length", Integer.toString(object.available()));
		httpHeaders.put("Content-Type", "application/octet-stream");
		String path = Utils.getEncodedURL(opPath);
		String request_url = conn.getHost() + path;
		if(queryStr != ""){
			request_url += '?' + queryStr;  
		}
		//Calling Request.put to complete file upload
		Response resp =  Request.Put(httpMethod,request_url,httpHeaders,object);
		return resp;
	}

	@Override
	///Method to extract ETag and upload date of the uploaded object
	/**
	 * @param Response : response object by calling put method under Request class
	 * @return PutObjectResult : Class containing ETag and Upload date
	 */
	public Object processResult(Object resp){

		String ETag = null;
		String Date = null;
		//Extracting values of ETag, ContentLength etc from headers and storing in local variable
		for (Map.Entry<String, List<String>> headers : ((Response) resp).getHeaders().entrySet()) {
			String key = new String();
			if(headers.getKey()!=null)
				key = headers.getKey();
			List<String> valueList = headers.getValue();
			if(key.contentEquals("ETag") )
			{
				ETag=valueList.get(0).substring(1, valueList.get(0).length()-1);

			}
			else if(key.contentEquals("Date")){
				Date = valueList.get(0);
			}
		}
		//Creating an object for class PutObjectResult and assigning values
		PutObjectResult putObject = new PutObjectResult(ETag,Date);

		return putObject;
	}
}
