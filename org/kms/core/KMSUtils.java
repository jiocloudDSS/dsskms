package org.kms.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class KMSUtils {

	private static ConcurrentHashMap<String, Integer>lockMap = new ConcurrentHashMap<String, Integer>();
	private static Lock lock = new ReentrantLock();
	public static Logger logger = Logger.getLogger("KMSUtils");

	static ConcurrentHashMap<String, Integer> getMap(){
		return lockMap;
	}


//	static Logger getKMSLogger(){
//		if (logger !=null)
//			return logger;
//		else return Logger.getLogger("KMSUtils");
//	}

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
	
}