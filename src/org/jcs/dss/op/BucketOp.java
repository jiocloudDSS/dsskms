package org.jcs.dss.op;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.jcs.dss.http.Response;
import org.jcs.dss.main.DssConnection;
///Head Class for all operations related to bucket
public class BucketOp extends Op {
	private String bucketName;
	///Constructors
	public BucketOp(DssConnection conn, String bucketName) throws UnsupportedEncodingException {
		super(conn);
		this.bucketName = bucketName;
		if(bucketName != null) {
			//String Bucket = Utils.getEncodedURL(this.bucketName);
			this.opPath = '/' + this.bucketName;
		}
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
	public Object processResult(Object result) throws IOException{
		return result;
	}	
}



