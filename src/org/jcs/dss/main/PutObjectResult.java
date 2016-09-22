package org.jcs.dss.main;
///Contains the data returned by JCS DSS from the putObject operation. 
/**
 * Use this class to access information about the new object created from the putObject request, 
 * such as its ETag and uploadDate. 
 */
public class PutObjectResult {

	private String ETag;
	private String uploadDate;
	/// Constructors		
	public PutObjectResult(String ETag, String uploadDate) {
		super();
		this.ETag= ETag;
		this.uploadDate = uploadDate;
	}
	/// Returns the server-side ETag value for the newly created object.
	/**
	 * 
	 * @return ETag
	 */
	public String getETag() {
		return ETag;
	}
	/// Sets the server-side ETag value for the newly created object.
	/**
	 * 
	 * @param ETag
	 */
	public void setEtag(String ETag) {
		this.ETag = ETag;
	}
	/// Returns the upload date of the newly created object.
	/**
	 * 
	 * @return UploadDate
	 */
	public String getUploadDate() {
		return uploadDate;
	}
	/// Sets the upload date of the newly created object.
	/**
	 * 
	 * @param UploadDate
	 */
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
}
