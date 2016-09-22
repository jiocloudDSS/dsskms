package org.kms.core;
import java.util.Calendar;
import java.util.Date;

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

public static String MK_OBJ_NAME_SUFFIX = "_kms_master_key";
private static String DSS_ACCESS_KEY = "access_key";//TODO:: need to put from a file
private static String DSS_SECRET_KEY = "secret_key";//TODO:: need to put from a file
private static String DSS_HOST_NAME = "server";
private static String DSS_MK_BUCKET = "kmsbucket1";
//private static String MASTER_KEY = "abcdef0987654321";


public MKRequester() {
	conn = new DssConnection(DSS_ACCESS_KEY, DSS_SECRET_KEY, DSS_HOST_NAME, false);
}

public void setUserId(String _userId){
	user_id = _userId;
}

public String getLatestMasterKey(){
	Date date = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    if (month < 10){
	    mkVersionSuffix = "_0" + month + year;
    } else {
	    mkVersionSuffix = "_" + month + year;
    }
	mkObjectName = user_id + MK_OBJ_NAME_SUFFIX + mkVersionSuffix;
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
	String master_key = null;
	String decrypted_master_key = null;
	CryptoMain crypto = CryptoMain.getInstance();
	while (retry < 5) {
		try {
			Response res = conn.downloadObject(DSS_MK_BUCKET, mkObjectName);
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
						Response res = conn.downloadObject(DSS_MK_BUCKET, mkObjectName);
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
	return decrypted_master_key;

}

//private String processBackendResponse(Response res){
//	try{
//		InputStream inputStream = res.getData();
//		ByteArrayOutputStream result = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int length;
//		while ((length = inputStream.read(buffer)) != -1) {
//			result.write(buffer, 0, length);
//		}
//		String resStr =  result.toString("UTF-8");
//		return resStr;
//	} catch (Exception e) {
//		return null;
//	}
//}

public boolean putMasterKeyToBackend(String master_key) throws Exception {
	int retry = 0;
	while (retry < 5){
		Response res = conn.uploadObjectFromFileName(DSS_MK_BUCKET, mkObjectName, master_key);
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

}

