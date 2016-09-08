package org.kms.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.kms.crypto.CryptoMain;

/**
 * Servlet implementation class DecryptMain
 */
@WebServlet("/decrypt")
public class DecryptMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String USERID = "user_id";
    private static String DATA_KEY = "encrypted_data_key";
    private static String DATA_IV = "encrypted_data_iv";
    private static String ENCRYPTED_MASTER_KEY = "encryptedMKVersionId";
    private String raw_data_key_str, raw_data_iv_str;
    boolean get_decrypted_keys_successfully = false;
    HashMap<String, String>queryParamsDecrypt;
    String errorMsg;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DecryptMain() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        PrintWriter out  = response.getWriter();
        out.println("<h1>Hello harshal! This is decrypt function</h1>");
        parseRequestParams(request, out);
        
        if (queryParamsDecrypt.containsKey(DATA_KEY) && queryParamsDecrypt.containsKey(DATA_IV) && queryParamsDecrypt.containsKey(ENCRYPTED_MASTER_KEY)) {
        	MKRequester requester = new MKRequester();
        	String master_key = requester.getMasterKeyForVersion(queryParamsDecrypt.get(ENCRYPTED_MASTER_KEY));
        	try{
        		if (master_key != null){
        			CryptoMain crypto = CryptoMain.getInstance();
        			raw_data_key_str = crypto.decryptText(queryParamsDecrypt.get(DATA_KEY), master_key);
        			raw_data_iv_str = crypto.decryptText(queryParamsDecrypt.get(DATA_IV), master_key);
        		} else {
        			errorMsg = "CouldNot fetch masterKey successfully";
        		}
        		
        	} catch (Exception e ){
        		System.out.print(e.getMessage());
        		errorMsg = e.getMessage();
        	}
        	get_decrypted_keys_successfully = checkKeys();
        	try{
            	if (!get_decrypted_keys_successfully) {
        			errorMsg = "CouldNot decrypt keys successfully";
            	}
            	createResponseObject(response, out);
        	} catch(Exception e){
        		errorMsg = "CouldNot form json object successfully";
        		response.setStatus(500);
        		out.print(errorMsg);
        	}
        }
    }
	
	private boolean checkKeys(){
		if (raw_data_key_str != null && raw_data_key_str.length() == 64) {
			if (raw_data_iv_str != null && raw_data_iv_str.length() == 16) {
				return true;
			}
		}
		return false;
	}
	
	private void createResponseObject(HttpServletResponse response, PrintWriter out) throws Exception {
		// TODO Auto-generated method stub
		// NOTE: write code to create response object
		JSONObject jsonObj = createJsonObject();
		response.setContentType("application/json");
		out.print(jsonObj);
		out.print("{\"KMS_RAW_DATA_KEY\":\"" + raw_data_key_str +  "\",\"KMS_RAW_DATA_IV\":\"" + raw_data_iv_str + "\"}");

	}

	@SuppressWarnings("unchecked")
	private JSONObject createJsonObject() {
		// TODO Auto-generated method stub		
		JSONObject jsonObj = new JSONObject();
		if (errorMsg == null){
			jsonObj.put("KMS_RAW_DATA_KEY", raw_data_key_str);
			jsonObj.put("KMS_RAW_DATA_IV", raw_data_iv_str);
		} else {
			jsonObj.put("KMS_ERROR", errorMsg);
		}
		return  jsonObj;
		
	}

	private void parseRequestParams(HttpServletRequest request, PrintWriter out) {
		queryParamsDecrypt = new HashMap<String, String>();
		String queryStr = request.getQueryString();
        if ( queryStr != null && queryStr.length() > 0) {
        	String[] params = queryStr.split("&");
        	for (int i=0; i < params.length; i++) {
        		String param = params[i];
        		if (param.contains("=")){
        			int index = param.indexOf('=');
        			String paramKey = param.substring(0, index);
        			String paramValue = param.substring(index+1, param.length());
        			queryParamsDecrypt.put(paramKey, paramValue);
        		}
        	}
        } 
        parseReqAttributes();
	}
	
	private void parseReqAttributes() {
		for (String key: queryParamsDecrypt.keySet()){
			String val = queryParamsDecrypt.get(key);
			String reg = "\\";
			val = val.replace(reg, "");
			queryParamsDecrypt.put(key, val);
		}
	}

}
