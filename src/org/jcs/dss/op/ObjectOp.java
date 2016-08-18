package org.jcs.dss.op;

import java.io.IOException;

import org.jcs.dss.http.Response;
import org.jcs.dss.main.DssConnection;
///Head Class for all operations related to objects
public class ObjectOp extends Op {

	private String bucketName;
	private String objectName;
	///Constructors
	public ObjectOp(DssConnection conn, String bucketName, String objectName) {
		super(conn);
		this.bucketName=bucketName;
		this.objectName = objectName;
		opPath = '/' + this.bucketName + '/' + this.objectName;
	}
	/// Executes the method requested by user
	/**
	 * 
	 * @return Response : Gets response object returned from makeRequest()
	 * @throws Exception
	 */
	public Response execute() throws Exception {
		return makeRequest();	
	}
	/// Processes the final result
	public Object processResult(Object result) throws Exception{
		return result;
	}
}



