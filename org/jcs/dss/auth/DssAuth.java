package org.jcs.dss.auth;
import java.io.UnsupportedEncodingException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.jcs.dss.utils.Utils;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
///Class to generate Signature
public class DssAuth {

	private String httpMethod;
	private String accessKey;
	private String secretKey;
	private String path;
	private String queryStr;
	private String contentType;
	private boolean useTimeInSeconds;
	private String dateStr;
	private int expiryTime;
	///Constructors
	public DssAuth(String httpMethod, String accessKey, String secretKey,
			String path, String queryStr, String contentType, String dateStr,
			boolean useTimeInSeconds, int expiryTime) {
		this.httpMethod = httpMethod;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.path = path;
		this.queryStr = queryStr;
		this.contentType = contentType;
		this.dateStr = dateStr;
		this.useTimeInSeconds = useTimeInSeconds;
		this.expiryTime = expiryTime;
	}
	///	Method to generate Canonical String
	/**
	 * 
	 * @return cannonical String
	 * @throws UnsupportedEncodingException
	 */
	public String getCannonicalStr() throws UnsupportedEncodingException {

		String cannonicalStr = "";
		String md5Checksum = "";
		if (useTimeInSeconds) {
			dateStr = Integer.toString(expiryTime);
		}
		///Getting encoded path
		path = this.getPathForCannonicalString();
		///Genrating cannonical String
		cannonicalStr += httpMethod;
		cannonicalStr += "\n" + md5Checksum;
		cannonicalStr += "\n" + contentType;
		cannonicalStr += "\n" + dateStr;
		/*	if(httpMethod=="PUT" & contentType==""){
		cannonicalStr += "\n"+"x-amz-copy-source:"+Op.getJcsCopySource();
		cannonicalStr += "\n"+"x-amz-metadata-directive:COPY";
		}*/
		cannonicalStr += "\n" + path;
		return cannonicalStr;
	}
	///Method to generate Signature
	/**
	 * 
	 * @return Auth : Signature
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public String getSignature() throws UnsupportedEncodingException,
	NoSuchAlgorithmException, InvalidKeyException {
		String secret = secretKey;
		///Converting secret key string into Bytes
		byte[] secretBytes = secret.getBytes("UTF-8");
		String cannonicalStr = getCannonicalStr();
		///Converting Cannonical  string into Bytes
		byte[] cannonicalStrBytes = cannonicalStr.getBytes("UTF-8");
		SecretKeySpec keySpec = new SecretKeySpec(secretBytes, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);
		byte[] result = mac.doFinal(cannonicalStrBytes);
		///Encoding to base64
		//String b64_hmac = DatatypeConverter.printBase64Binary(result);
		String b64_hmac=DatatypeConverter.printBase64Binary(result); ;
		String auth = "";
		if (useTimeInSeconds) {
			auth = b64_hmac;
		} else {
			auth = ("AWS " + accessKey + ":" + b64_hmac);
		}
		return auth;
	}
	///Method to genrate encoded path
	/**
	 * 
	 * @return path
	 * @throws UnsupportedEncodingException
	 */
	public String getPathForCannonicalString() throws UnsupportedEncodingException {
		///Encoding path
		path = Utils.getEncodedURL(path);
		if (queryStr != "")
		{
			path += '?' + queryStr;	
		}
		return path;
	}
}
