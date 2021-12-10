package com.technotes.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WebserviceInvoker {
	public static void main(String args[]) {
		System.out.println(send());
	}

	public static String send() {
		try	{
			String requestSOAP = "<Message>" + 
								   "<Header>" +
								      "<target>cn=Peer1</target>" +
								   "</Header>" +
								   "<Body>" +
								      "<GetCities/>" +
								   "</Body>" +
								"</Message>";

			URL url = new URL("http://localhost:8080");
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			byte[] bytesSOAP = new byte[requestSOAP.length()];
			try {
				// transform request into byte[]
				bytesSOAP = requestSOAP.getBytes("UTF-8");
			}
			catch(UnsupportedEncodingException uee) {
				throw new RuntimeException("Failed to encode SOAP request: " + uee.toString());
			}
			System.out.println("Content-Length:" + bytesSOAP.length);

			httpConn.setRequestProperty( "Content-Length", String.valueOf(bytesSOAP.length) );
			httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
			httpConn.setRequestMethod( "POST" );
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			// Everything's set up; send the XML that was read in to bytesSOAP.
			DataOutputStream out = new DataOutputStream(
												new BufferedOutputStream(
														httpConn.getOutputStream(), 100000));
			out.write( bytesSOAP );
			out.flush();
			out.close();

			// Read the response and convert into string
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null) {
				result += inputLine;
			}
			in.close();

			return result;
		}
		catch ( Throwable t ) {
			throw new RuntimeException(t.toString());
		}
	}
}
