package org.kms.core;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KMSUtils {

	private static HashSet<String>lockSet = new HashSet<String>();
	private static Lock lock = new ReentrantLock();

	static HashSet<String> getMap(){
		return lockSet;
	}

	public static boolean putWithLock(String id){
		if (lockSet.contains(id)){
			return false;
		} else {
			lock.lock();
			if (lockSet.contains(id)){
				lock.unlock();
				return false;
			} else {
				lockSet.add(id);
				lock.unlock();
				return true;
			}
		}
	}
}