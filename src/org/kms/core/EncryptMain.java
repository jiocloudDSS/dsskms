package org.kms.core;

import java.io.*;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.kms.crypto.CryptoMain;



/**
 * Servlet implementation class SseMain
 */
@WebServlet("/encrypt")
public class EncryptMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String USERID = "user_id";
    public static String KEYSET = "data_key";
    String userId, raw_data_key_str, raw_data_iv_str, encoded_data_key_str, encoded_data_iv_str, masterKey, encryptedMKVersionId;
    boolean get_keys_successfully = false;
    HashMap<String, String>queryParams;
    String errorMsg = null;
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
//        out.println("<h1>Hello harshal! This is encrypt function new</h1>");
        parseRequestParams(request, out);
		CryptoMain crypto = CryptoMain.getInstance();

        
//        if (queryParams.containsKey(USERID) && queryParams.get(USERID).length() > 0){
		if (true) {
//        	userId = queryParams.get(USERID);
			userId = "1234";
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
        		
        			get_keys_successfully = true;
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
			createResponseObject(response, out, crypto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMsg = "CouldNot form json object successfully";
			out.print(errorMsg);
		}
        
	}
	
	@SuppressWarnings("unchecked")
	private void createResponseObject(HttpServletResponse response, PrintWriter out, CryptoMain crypto) throws Exception {
		// TODO Auto-generated method stub
		// NOTE: write code to create response object
		JSONObject jsonObj = createJsonObject();
		response.setContentType("application/json");
		out.print(jsonObj);

	}

	@SuppressWarnings("unchecked")
	private JSONObject createJsonObject() {
		// TODO Auto-generated method stub	
		JSONObject jsonObj = new JSONObject();
		if (errorMsg == null){
			jsonObj.put("KMS_RAW_DATA_KEY", raw_data_key_str);
			jsonObj.put("KMS_ENCRYPTED_DATA_KEY", encoded_data_key_str);
			jsonObj.put("KMS_RAW_DATA_IV", raw_data_iv_str);
			jsonObj.put("KMS_ENCRYPTED_DATA_IV", encoded_data_iv_str);
			jsonObj.put("KMS_ENCRYPTED_MK_VERSION", encryptedMKVersionId);
		} else {
			jsonObj.put("KMS_ERROR", errorMsg);
		}
		return  jsonObj;
	}

	private void parseRequestParams(HttpServletRequest request, PrintWriter out) {
		queryParams = new HashMap<String, String>();
		String queryStr = request.getQueryString();
        if ( queryStr != null && queryStr.length() > 0) {
        	out.println("found query string");
        	String[] params = queryStr.split("&");
        	out.print(params.length);
        	for (int i=0; i < params.length; i++) {
        		String param = params[i];
        		if (param.contains("=")){
        			String[] currentPair = param.split("=");
        			queryParams.put(currentPair[0], currentPair[1]);
        		}
        	}
        }
	}

}
