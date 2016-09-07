package org.kms.core;

import java.util.concurrent.ConcurrentHashMap;

public class KMSUtils {

	public static ConcurrentHashMap<String, Integer>lockMap = new ConcurrentHashMap<String, Integer>();

	static ConcurrentHashMap<String, Integer> getMap(){
		return lockMap;
	}
}