package org.jcs.dss.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
///Utils Class
public class Utils {
	
	private static SSLSocketFactory sslFactory = null;
	///This method returns Date as a String in the required form
	/**
	 * 
	 * @return dateStr : Date String
	 */
	public static String getCurTimeInGMTString() {
		Date date = new Date();
		@SuppressWarnings("deprecation")
		String dateGMTStr = date.toGMTString();
		Calendar calendar = Calendar.getInstance();
		Date curTime = calendar.getTime();
		String curTimeStr = new SimpleDateFormat("EE", Locale.ENGLISH)
				.format(curTime.getTime());
		String dateStr = curTimeStr + ", " + dateGMTStr;
		return dateStr;
	}
	///This method returns the URL Encoded String
	/**
	 * 
	 * @param URL : String to be encoded
	 * @return URL : Encoded String
	 * @throws UnsupportedEncodingException
	 */
	public static String getEncodedURL(String URL) throws UnsupportedEncodingException{
		URL = URLEncoder.encode(URL,"UTF-8");
		URL= URL.replaceAll("%2F","/");
		URL = URL.replaceAll("%7E","~");
		return URL;
	}
	
	public static SSLSocketFactory getSslFactory() {
		if (sslFactory == null){
			try {
				File fs = new File("/Users/harshalgupta/Desktop/dss-staging.jks");
				InputStream in = new FileInputStream(fs);
				KeyStore keyStore = KeyStore.getInstance("JKS");
				char[] password = {'9','8','7','6','5','4'};
				keyStore.load(in, password);
				in.close();
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(keyStore);
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, tmf.getTrustManagers(), null);
				sslFactory = ctx.getSocketFactory();
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		return sslFactory;
	}
	
	

}
