package org.kms.core;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.jcs.dss.http.ErrorResponse;
import org.jcs.dss.http.Response;
import org.jcs.dss.main.*;
import org.kms.crypto.CryptoMain;

public class MKRequester {

String user_id;
String data_key;
String master_key;
String mkObjectName;
String mkVersionSuffix;
String iv_str;
DssConnection conn;

private static String MASTER_KEY = "abcdef0987654321";

public MKRequester() {
	conn = new DssConnection(KMSUtils.access, KMSUtils.secret, KMSUtils.host, KMSUtils.secure);
}

public void setUserId(String _userId){
	user_id = _userId;
}

public String getLatestMasterKey(){
	String rType = KMSUtils.rotationType;
	if (rType == "false"){
		mkVersionSuffix = "00000000";
	} else {
		if (rType == "year") getVSuffix(true, false, false, 0);
		else if (rType == "month") getVSuffix(false, true, false, 0);
		else if (rType == "day") getVSuffix(false, false, true, 0);
		else if (rType == "min") {
			int min = 2;
			getVSuffix(false, false, true, min);
		}
	}
	mkObjectName = user_id + KMSUtils.objNameSuffix + mkVersionSuffix;
	return getMasterKey();
}

public String getMasterKeyForVersion(String encryptedMKStr){
	CryptoMain crypto = CryptoMain.getInstance();
	try {
		mkObjectName = crypto.decryptText(encryptedMKStr, crypto.getGlobalKey(), null);
	} catch (Exception e){
		mkObjectName = "";
	}
	return getMasterKey();
}

private String getUserIdFromRequestMK(){
	int loc = mkObjectName.indexOf("_");
	String userID = mkObjectName.substring(0, loc);
	if (loc != -1)
		return userID;
	else return null;
}

public String getEncryptedMKVersion() {
	CryptoMain crypto = CryptoMain.getInstance();
	String encryptedMKVersionId;
	try {
		encryptedMKVersionId = crypto.encryptKey(mkObjectName, crypto.getGlobalKey());
	} catch (Exception e){
		encryptedMKVersionId = null;
	} 
	return encryptedMKVersionId;
}


public String getMasterKey() {
	/* code for get object master key from radosgw backend */
	int retry = 0;
	long dssStartTime = System.currentTimeMillis();
	String master_key = null;
	String decrypted_master_key = null;
	CryptoMain crypto = CryptoMain.getInstance();
	while (retry < 5) {
		try {
			Response res = conn.downloadObject(KMSUtils.dssBucket, mkObjectName);
			if (res.getStatusCode() == 200) {
				master_key = res.getXMLString();
				if (master_key != null){
					try {
						decrypted_master_key = crypto.decryptText(master_key, crypto.getGlobalKey(), null);
					} catch (Exception e){
						return null;
					}
				}
				break;
			}
			
		} catch (ErrorResponse error) {
			if (error.getErroCode() == 404){
				String uid = getUserIdFromRequestMK();
				if (uid == null) 
					return null;
				try {
					if (!KMSUtils.putWithLock(uid)) {
						while (KMSUtils.getMap().containsKey(uid)){
							Thread.sleep(50);
						}
						Response res = conn.downloadObject(KMSUtils.dssBucket, mkObjectName);
						if (res.getStatusCode() == 200) {
							master_key = res.getXMLString();
							if (master_key != null){
								try {
									decrypted_master_key = crypto.decryptText(master_key, crypto.getGlobalKey(), null);
								} catch (Exception e){
									return null;
								}
							}
						}
					} else {
						master_key = generateRawMasterKey();
						String encrypted_master_key = crypto.encryptKey(master_key, crypto.getGlobalKey());
						if (putMasterKeyToBackend(encrypted_master_key)){
							decrypted_master_key = master_key;
						}
						KMSUtils.getMap().remove(uid);
					}

				} catch (Exception e) {
					if (KMSUtils.getMap().containsKey(uid)){
						KMSUtils.getMap().remove(uid);
					}
					return null;
				}
				break;
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
		retry++;
	}
	long dssEndTime = System.currentTimeMillis();
	KMSUtils.logger.logp(Level.INFO, "MKRequester", "getMasterKey", "this is the total time to get master key:  " + Long.toString((dssEndTime - dssStartTime)) + " for key: " + mkObjectName);
	return decrypted_master_key;

}

public boolean putMasterKeyToBackend(String master_key) throws Exception {
	int retry = 0;
	while (retry < 5){
		Response res = conn.uploadObjectFromFileName(KMSUtils.dssBucket, mkObjectName, master_key);
		if (res.getStatusCode() == 200) {
			return true;
		}
		retry++;
	}
	return false;
}

public String generateRawMasterKey() throws Exception {
	// TODO Auto-generated method stub
	
	CryptoMain crypto = CryptoMain.getInstance();
	String masterKey = crypto.generateKey(128);
	return masterKey;
}

public void getVSuffix(boolean isYear, boolean isMonth, boolean isDay, int min ) {
	Date date = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;

    if (isYear) {
    	makeVPrefixString(0, 0, year, -1);
    }
    if (isMonth) {
		makeVPrefixString(0, month, year, -1);
    } else {
		int day = cal.get(Calendar.DAY_OF_MONTH);
    	if (min == 0) {
			makeVPrefixString(day, month, year, -1);
    	} else {
    		int hour = cal.get(Calendar.HOUR_OF_DAY);
    		int minute = cal.get(Calendar.MINUTE);
    		int currentMin = (hour*60 + minute) - ((hour*60 + minute) % min);
			makeVPrefixString(day, month, year, currentMin);
    		
    	}
    }
}

public void makeVPrefixString(int day, int month, int year, int min){
	String mStr, dStr, yStr, minStr;
	if (month < 10) 
		mStr = "0" + month;
	else
		mStr = "" + month;
	
	if (day < 10) 
		dStr = "0" + day;
	else
		dStr = "" + day;
	
	yStr = "" + year;

	if (min == -1)
		minStr = "";
	else
		minStr = "" + min;
	
	mkVersionSuffix =  "_" + minStr + dStr + mStr + yStr;

}

}

