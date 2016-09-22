package org.kms.core;

import java.io.*;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.simple.JSONObject;
import org.kms.crypto.CryptoMain;



/**
 * Servlet implementation class SseMain
 */
@WebServlet("/encrypt")
public class EncryptMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String USERID = "user_id";

	/**
     * @see HttpServlet#HttpServlet()
     */
    public EncryptMain() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        PrintWriter out  = response.getWriter();
        String userId, raw_data_key_str, raw_data_iv_str, encoded_data_key_str, encoded_data_iv_str, masterKey, encryptedMKVersionId, errorMsg;
        userId=raw_data_key_str= raw_data_iv_str= encoded_data_key_str= encoded_data_iv_str = masterKey = encryptedMKVersionId = errorMsg = null;
        HashMap<String, String> queryParams = parseRequestParams(request);
        CryptoMain crypto = CryptoMain.getInstance();

        if (queryParams.containsKey(USERID) && queryParams.get(USERID).length() > 0){
        	userId = queryParams.get(USERID);
    		MKRequester requester = new MKRequester();
    		requester.setUserId(userId);

        	try{        	
        		masterKey = requester.getLatestMasterKey();
        		if (masterKey != null){
            		raw_data_key_str = crypto.generateKey(512);
            		raw_data_iv_str = crypto.generateKey(128);
        			encoded_data_key_str = crypto.encryptKey(raw_data_key_str, masterKey);
        			encoded_data_iv_str = crypto.encryptKey(raw_data_iv_str, masterKey);
        			encryptedMKVersionId = requester.getEncryptedMKVersion();
        		
        		} else {
        			errorMsg = "CouldNot fetch masterKey successfully";
        		}
        	} catch (Exception e){
        		errorMsg = e.getMessage();
        	}
        } else {
        	errorMsg = "UserId in request is empty or null";
        }
       
        try {
			createResponseObject(response, out, raw_data_key_str, raw_data_iv_str, encoded_data_key_str, encoded_data_iv_str, encryptedMKVersionId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMsg = "CouldNot form json object successfully";
			out.print(errorMsg);
		}
        
	}
	
	@SuppressWarnings("unchecked")
	private void createResponseObject(HttpServletResponse response, PrintWriter out, String raw_data_key_str, String raw_data_iv_str, String encoded_data_key_str, String encoded_data_iv_str, String encryptedMKVersionId) throws Exception {
		// TODO Auto-generated method stub
		// NOTE: write code to create response object
//		JSONObject jsonObj = createJsonObject();
//		response.setContentType("application/json");
//		out.print(jsonObj);
		out.print("{\"KMS_RAW_DATA_KEY\":\"" + raw_data_key_str + "\",\"KMS_ENCRYPTED_DATA_KEY\":\"" + encoded_data_key_str + "\",\"KMS_RAW_DATA_IV\":\"" + raw_data_iv_str + "\",\"KMS_ENCRYPTED_DATA_IV\":\"" + encoded_data_iv_str + "\",\"KMS_ENCRYPTED_MK_VERSION\":\"" + encryptedMKVersionId + "\"}");
	}

	@SuppressWarnings("unchecked")
//	private JSONObject createJsonObject() {
//		// TODO Auto-generated method stub
//		JSONObject jsonObj = new JSONObject();
//		if (errorMsg == null){
//			jsonObj.put("KMS_RAW_DATA_KEY", raw_data_key_str);
//			jsonObj.put("KMS_ENCRYPTED_DATA_KEY", encoded_data_key_str);
//			jsonObj.put("KMS_RAW_DATA_IV", raw_data_iv_str);
//			jsonObj.put("KMS_ENCRYPTED_DATA_IV", encoded_data_iv_str);
//			jsonObj.put("KMS_ENCRYPTED_MK_VERSION", encryptedMKVersionId);
//		} else {
//			jsonObj.put("KMS_ERROR", errorMsg);
//		}
//		return  jsonObj;
//	}

	private HashMap<String, String> parseRequestParams(HttpServletRequest request) {
		HashMap<String,String> queryParams = new HashMap<String, String>();
		String queryStr = request.getQueryString();
        if ( queryStr != null && queryStr.length() > 0) {
        	String[] params = queryStr.split("&");
        	for (int i=0; i < params.length; i++) {
        		String param = params[i];
        		if (param.contains("=")){
        			String[] currentPair = param.split("=");
        			queryParams.put(currentPair[0], currentPair[1]);
        		}
        	}
        }
	return queryParams;
	}

}
