package org.kms.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class KMSUtils {

	private static ConcurrentHashMap<String, Integer>lockMap = new ConcurrentHashMap<String, Integer>();
	private static Lock lock = new ReentrantLock();
	public static Logger logger = Logger.getLogger("KMSUtils");
	public static String access, secret, sslStorePath, host, sslStorePasswd, dssBucket, globalKey, objNameSuffix, rotationType, rotationMin;
	public static boolean secure;
	public static boolean success = false;
	public static boolean fieldsSetupDone = false;

	static ConcurrentHashMap<String, Integer> getMap(){
		return lockMap;
	}

	public static boolean putWithLock(String id){
		if (lockMap.containsKey(id)){
			return false;
		} else {
			lock.lock();
			if (lockMap.containsKey(id)){
				lock.unlock();
				return false;
			} else {
				lockMap.put(id, new Integer(1));
				lock.unlock();
				return true;
			}
		}
	}
	
	public static void setupFields() {
		String path = "/var/lib/tomcat7/webapps/ROOT/WEB-INF/resources/config.properties";
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(path);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			access = prop.getProperty("access");
			secret = prop.getProperty("secret");
			host = prop.getProperty("dssHost");
			String secureStr = prop.getProperty("isSecure", "true");
			if (secureStr.equals("true"))
				secure = true;
			else
				secure = false;
			sslStorePasswd = prop.getProperty("sslPasswd");
			sslStorePath = prop.getProperty("sslStorePath");
			dssBucket = prop.getProperty("bucketName");
			globalKey = prop.getProperty("kms_global_key");
			objNameSuffix = prop.getProperty("objNameSuffix", "_kms_master_key");
			rotationType = prop.getProperty("rotation", "month");
			rotationMin = prop.getProperty("rotationDuration", "60");
			
			if (access != null && 
				secret !=null &&
				host != null &&
				sslStorePasswd != null &&
				sslStorePath != null &&
				globalKey != null &&
				secureStr != null) {
				success = true;
			} else {
				success = false;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean confSetupDone() {
		if (!fieldsSetupDone) {
			KMSUtils.setupFields();
			if (success){
				fieldsSetupDone= true;
				return fieldsSetupDone;
			} else return false;
		} else return true;
	}
	
}