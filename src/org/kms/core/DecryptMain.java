package org.kms.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kms.crypto.CryptoMain;

/**
 * Servlet implementation class DecryptMain
 */
@WebServlet("/decrypt")
public class DecryptMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static String DATA_KEY = "encrypted_data_key";
    private static String DATA_IV = "encrypted_data_iv";
    private static String ENCRYPTED_MASTER_KEY = "encryptedMKVersionId";
       
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
	String raw_data_key_str, raw_data_iv_str, errorMsg;
	raw_data_key_str = raw_data_iv_str = errorMsg = null;
	String requestId = request.getParameter("randomId");
	boolean get_decrypted_keys_successfully = false;
	HashMap<String, String> queryParamsDecrypt = parseRequestParams(request, requestId);
        
        if (queryParamsDecrypt.containsKey(DATA_KEY) && queryParamsDecrypt.containsKey(DATA_IV) && queryParamsDecrypt.containsKey(ENCRYPTED_MASTER_KEY)) {
        	MKRequester requester = new MKRequester();
        	String master_key = requester.getMasterKeyForVersion(queryParamsDecrypt.get(ENCRYPTED_MASTER_KEY));
        	try{
        		if (master_key != null){
        			CryptoMain crypto = CryptoMain.getInstance();
        			raw_data_key_str = crypto.decryptText(queryParamsDecrypt.get(DATA_KEY), master_key, requestId);
        			raw_data_iv_str = crypto.decryptText(queryParamsDecrypt.get(DATA_IV), master_key, requestId);
        		} else {
        			errorMsg = "CouldNot fetch masterKey successfully";
        		}
        		
        	} catch (Exception e ){
        		System.out.print(e.getMessage());
        		errorMsg = e.getMessage();
        	}
        	get_decrypted_keys_successfully = checkKeys(raw_data_key_str, raw_data_iv_str);
        	try{
            	if (!get_decrypted_keys_successfully) {
        			errorMsg = "CouldNot decrypt keys successfully";
            	}
            	createResponseObject(response, out, raw_data_key_str, raw_data_iv_str, requestId);
        	} catch(Exception e){
        		errorMsg = "CouldNot form json object successfully";
        		response.setStatus(500);
        		out.print(errorMsg);
        	}
        }
    }
	
	private boolean checkKeys(String raw_data_key_str, String raw_data_iv_str){
		if (raw_data_key_str != null && raw_data_key_str.length() == 64) {
			if (raw_data_iv_str != null && raw_data_iv_str.length() == 16) {
				return true;
			}
		}
		return false;
	}
	
	private void createResponseObject(HttpServletResponse response, PrintWriter out, String raw_data_key_str, String raw_data_iv_str, String requestId) throws Exception {
		// TODO Auto-generated method stub
		// NOTE: write code to create response object
//		JSONObject jsonObj = createJsonObject();
//		response.setContentType("application/json");
//		out.print(jsonObj);
		out.print("{\"KMS_RAW_DATA_KEY\":\"" + raw_data_key_str +  "\",\"KMS_RAW_DATA_IV\":\"" + raw_data_iv_str + "\",\"random_id\":\"" + requestId + "\"}");

	}

	@SuppressWarnings("unchecked")
//	private JSONObject createJsonObject() {
//		// TODO Auto-generated method stub
//		JSONObject jsonObj = new JSONObject();
//		if (errorMsg == null){
//			jsonObj.put("KMS_RAW_DATA_KEY", raw_data_key_str);
//			jsonObj.put("KMS_RAW_DATA_IV", raw_data_iv_str);
//		} else {
//			jsonObj.put("KMS_ERROR", errorMsg);
//		}
//		return  jsonObj;
//	}

	private HashMap<String,String> parseRequestParams(HttpServletRequest request, String requestId) {
		HashMap<String,String> queryParamsDecrypt = new HashMap<String, String>();
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
        parseReqAttributes(requestId, queryParamsDecrypt);
	return queryParamsDecrypt;
	}
	
	private void parseReqAttributes(String requestId, HashMap<String, String>queryParamsDecrypt) {
		for (String key: queryParamsDecrypt.keySet()){
			String val = queryParamsDecrypt.get(key);
			String reg = "\\";
			val = val.replace(reg, "");
			queryParamsDecrypt.put(key, val);
			KMSUtils.logger.logp(Level.INFO, "DecryptMain", "parseReqAttributes", "value: " + val + "  for key: " + key + "  ::for id: " + requestId );
		}
	}

}
