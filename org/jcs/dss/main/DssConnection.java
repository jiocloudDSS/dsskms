package org.jcs.dss.main;
import java.net.URL;
import java.util.List;
import org.jcs.dss.http.Response;
import org.jcs.dss.op.*;
///Provides an interface to the client for accessing the JCS DSS web service.
/**

  		 The JCS DSS java-sdk  provides a interface that can be used to store and retrieve any amount of data, at any time, from anywhere on the web.

 */

public class DssConnection {

	private String accessKey;
	private String secretKey;
	private String host;
	private static boolean isSecure;
	/// Constructors
	public DssConnection(String accessKey, String secretKey, String host,boolean isSecure) {
		DssConnection.isSecure = isSecure;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.host = host;
	}
	/// Returns if the connection is secure or not
		/**
		 * 
		 * @return String : isSecure	
		 */
		public static boolean getIsSecure() {
			return isSecure;
		}
	/// Returns the Access key entered by client
	/**
	 * 
	 * @return String : AccessKey	
	 */
	public String getAccessKey() {
		return accessKey;
	}
	/// Returns the Secret key entered by client
	/**
	 * 
	 * @return String : SecretKey	
	 */
	public String getSecretKey() {
		return secretKey;
	}
	/// Returns the Host address entered by client
	/**
	 * 
	 * @return String : Host	
	 */
	public String getHost() {
		return host;
	}
	
	///Uploads a new file in the specified DSS bucket 
	/**
	 * 
	 * @param BucketName : Name of the DSS bucket to where data need to be uploaded.
	 * @param ObjectName : Sets the key under which to store the new object.
	 * @param FilePath : Path of the file from where file is need to be uploaded.
	 * @return PutObjectResult : Returns details related to uploaded object.
	 * @throws Exception
	 */
	public Response uploadObjectFromFileName(String bucketName, String objectName,
			String filePath) throws Exception {
		PutObjectOp op = new PutObjectOp(this,bucketName,objectName,filePath);
		Response resp = op.execute();	
		return resp;
	}
	
	public Response downloadObject(String bucketName, String objectName) throws Exception {
		GetObjectOp op = new GetObjectOp(this, bucketName,objectName);
		String resp = op.Execute();
		return op.processResult(resp);
	}
}
