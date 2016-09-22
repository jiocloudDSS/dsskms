package org.jcs.dss.http;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
///Class for error messages returned from server
public class ErrorResponse extends Exception{
	private static final long serialVersionUID = 9127818703042955256L;
	private int ErrorCode;
	private String ErrorMsg;
	private InputStream ErrorStream;
	///Constructors
	public ErrorResponse (int ErrorCode , String ErrorMessage, InputStream ErrorStream){
		this.ErrorCode = ErrorCode;
		this.ErrorMsg = ErrorMessage;
		this.ErrorStream = ErrorStream;

	}
	///Returns Error code example : 403
	/**
	 * 
	 * @return ErrorCode
	 */
	public int getErroCode() {
		return ErrorCode;
	}
	///Returns Error Message example : forbidden
	/**
	 * 
	 * @return ErrorMsg
	 */
	public String getErrorMsg() {
		return ErrorMsg;
	}
	///Returns Error Stream 
	/**
	 * 
	 * @return ErrorStream
	 */
	public InputStream getErrorStream() {
		return ErrorStream;
	}
	///Reads inputStream and parse it and prints the concatenated Error Message
	public String toString(){
		//Checking if inputStream is null or not. If null then does not parse it
		if(ErrorStream !=null){
			//Reading a InputStream using BufferedReader
			BufferedReader input = new BufferedReader(new InputStreamReader(ErrorStream));
			String XML = null;
			try {
				XML = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Parsing XML using DocumentBuilderFactory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			String Error = null;
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				//Type casting a string to a Document
				Document doc= db.parse(new InputSource(new StringReader(XML)));
				doc.getDocumentElement().normalize();
				NodeList List1 = doc.getElementsByTagName("Error");
				Node Node1 = List1.item(0);
				if (Node1.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element Element1 = (Element) Node1;
					// Extracting Error message from XML String and storing in local variable
					Error = Element1.getElementsByTagName("Code").item(0).getTextContent();
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			//Printing Error Message
			return Error + "\n" + ErrorMsg + "\n" + ErrorCode +"\n";}
		else{
			//Printing Error Message if inputStream is null
			return  ErrorMsg + "\n" + ErrorCode +"\n";
		}

	}
}
