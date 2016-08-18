package org.jcs.dss.main;

public class Config {

	private static boolean Secure = DssConnection.getIsSecure();
	public static Boolean isSecure (){
		return Secure;
	}
}
