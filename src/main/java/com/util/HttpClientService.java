package com.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;


public class HttpClientService {
	static Logger log = Log4jFactory.getInstance(Log4jFactory.getCallingClassname());
	
	
	public String sendSoapReq(String endPoint, String soapAction, String soapBody) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		HttpURLConnection httpConn = null;
		DataOutputStream wr = null;
		BufferedReader bufferedReader = null; 
		int httpStatusCode = 0;
		try{
			//http 連線設定
			URL obj = new URL(endPoint);
			httpConn = (HttpURLConnection) obj.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			httpConn.setRequestProperty("SOAPAction", soapAction);
			httpConn.setConnectTimeout(10000);
			httpConn.setReadTimeout(10000);
			
			//傳送資料
			httpConn.setDoOutput(true);
			wr = new DataOutputStream(httpConn.getOutputStream());
			wr.write(soapBody.getBytes("utf-8"));
			wr.flush();
			
			//回覆資料
			httpStatusCode = httpConn.getResponseCode();

			String line = null;
			bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			while((line = bufferedReader.readLine())!= null){
				stringBuilder.append(line);
			}	
			return stringBuilder.toString();			
		}catch(IOException e){
			log.error("http status:" + httpStatusCode);
			log.error(e.getMessage(), e);
			throw e;
		}finally {
			if(wr != null){
				wr.close();
			}
			
			if(null != bufferedReader){
				bufferedReader.close();
			}
			
			if(httpConn != null) {
				httpConn.disconnect();
			}
		}
	}
}
