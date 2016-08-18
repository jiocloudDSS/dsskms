package org.jcs.dss.auth;

public class DssAuthBuilder {

	private String _httpMethod = "";
	private String _accessKey = "";
	private String _secretKey = "";
	private String _path = "";
	private String _queryStr = "";
	private String _contentType = "";
	private String _dateStr = "";
	private boolean _useTimeInSeconds = false;
	private int _expiryTime = 0;
	
	public DssAuthBuilder httpMethod(String httpMethod) {
		_httpMethod = httpMethod;
		return this;	
	}
	
	public DssAuthBuilder accessKey(String accessKey) {
		_accessKey = accessKey;
		return this;
	}
	
	public DssAuthBuilder secretKey(String secretKey) {
		_secretKey = secretKey;
		return this;
	}
	
	public DssAuthBuilder path(String path) {
		_path = path;
		return this;
	}
	
	public DssAuthBuilder queryStr(String queryStr) {
		_queryStr = queryStr;
		return this;
	}
	
	public DssAuthBuilder contentType(String contentType) {
		_contentType = contentType;
		return this;
	}
	
	public DssAuthBuilder useTimeInSeconds(boolean useTimeInSeconds) {
		_useTimeInSeconds = useTimeInSeconds;
		return this;
	}
	
	public DssAuthBuilder expiryTime(int expiryTime) {
		_expiryTime = expiryTime;
		return this;
	}
	
	public DssAuthBuilder dateStr(String dateStr) {
		_dateStr = dateStr;
		return this;
	}
	
	
	public DssAuth build() {
		return new DssAuth(_httpMethod, _accessKey, _secretKey, _path, _queryStr, _contentType, _dateStr, _useTimeInSeconds, _expiryTime);
	}
}
