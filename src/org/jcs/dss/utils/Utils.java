package org.jcs.dss.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
///Utils Class
public class Utils {
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

}
