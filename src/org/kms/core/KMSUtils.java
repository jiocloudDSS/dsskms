package org.kms.core;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KMSUtils {

	private static ConcurrentHashMap<String, Integer>lockMap = new ConcurrentHashMap<String, Integer>();
	private static Lock lock = new ReentrantLock();

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
	
}