package org.kms.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import java.util.logging.Level;
import org.kms.core.KMSUtils;



public class CryptoMain {

	String plain_text_data_key;
	String encrypted_data_key;
	private static HashMap<String, CryptoMain>instanceMap = new HashMap<String, CryptoMain>();
	
	private static String KMS_GLOBAL_KEY = "1234567890abcdef";
	private static String CURRENT_INSTANCE = "current_instance";
	private SecureRandom random;
	
	public CryptoMain() {
		 random = new SecureRandom();
	}
	
	public static CryptoMain getInstance() {
		if (!instanceMap.isEmpty()){
			return instanceMap.get(CURRENT_INSTANCE);
		} else {
			CryptoMain crypto = new CryptoMain();
			instanceMap.put(CURRENT_INSTANCE, crypto);
			return crypto;
		}
	}
	
	public String getGlobalKey() {
		return KMS_GLOBAL_KEY;
	}
	
	/*NOTE: write code to get a key of size 512 and the string version of it */
    public String generateKey(int keySize) throws Exception{
        int randomBitSize = (keySize/8)*5;
    	String keyText = new BigInteger(randomBitSize, random).toString(32);
    	return keyText;
    }
    
	public String encryptKey(String plainText, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
		Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
        String cipherText = Base64.encodeBase64String(byteCipherText);
        return cipherText;		
	}
	
    public String decryptText(String cipherText, String secretKey, String requestId) throws Exception {
        byte[] byteCipherText = Base64.decodeBase64(cipherText);
        System.out.println(cipherText);
    	Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        aesCipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        String plainText = new String(bytePlainText);
	if (requestId == null) requestId = "invalid";
	KMSUtils.logger.logp(Level.INFO, "CryptoMain", "decyrptText", "this is decrypted text:  " + plainText + "  ::for the encrypted text:  " + cipherText + "  ::and the requestId is:  " + requestId );
        return plainText;
    }
    
}
