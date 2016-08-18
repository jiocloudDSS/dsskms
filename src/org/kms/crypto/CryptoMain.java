package org.kms.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;


public class CryptoMain {

	String plain_text_data_key;
	String encrypted_data_key;
	private static HashMap<String, CryptoMain>instanceMap = new HashMap<String, CryptoMain>();
	
	private static String KMS_GLOBAL_KEY = "1234";
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
//        System.out.print(secretKey);
//        System.out.print(plainText);
		Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
//        String cipherText = new String(byteCipherText, "UTF8");
        String cipherText = Base64.encodeBase64String(byteCipherText);
        return cipherText;		
	}
	
    public String decryptText(String cipherText, String secretKey) throws Exception {
    	System.out.print("this is the cipherText to decrypt: " + cipherText + " and length is: " + cipherText.length() + " \n");
        byte[] byteCipherText = Base64.decodeBase64(cipherText);
        System.out.println(cipherText);
    	Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        aesCipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        String plainText = new String(bytePlainText);
        return plainText;
    }
    
}
