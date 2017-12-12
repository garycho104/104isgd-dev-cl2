package com.util;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import com.util.HttpClientService;

public class JbwsResume {
	static Logger log = Log4jFactory.getInstance(Log4jFactory.getCallingClassname());
	
	/**
	 * @method createResumeJsonForSE
	 * @purpose 產生getResumeJsonForSE請求內容
	 * @author bruce.sun
	 * @create 2017年8月9日 下午16:00:00
	 * @return String
	 * @modify
	 */
	private String createResumeJsonForSE(String idNoParam, String versionNoParam){
		Namespace soapenv = Namespace.getNamespace("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
	    Namespace urn = Namespace.getNamespace("urn", "urn:JbwsResumeControllerwsdl");
	    
	    Document document = new Document();
		Element envelope = new Element("Envelope", soapenv);
		Element header = new Element("Header", soapenv);
		Element body = new Element("Body", soapenv);
	    
		//soap body請求參數
		Element getResumeJsonForSE = new Element("getResumeJsonForSE", urn);
		Element idNo = new Element("idNo");
		idNo.setText(idNoParam);
		Element versionNo = new Element("versionNo");
		versionNo.setText(versionNoParam);
		getResumeJsonForSE.addContent(idNo);
		getResumeJsonForSE.addContent(versionNo);
		
		document.setRootElement(envelope);
		envelope.addContent(header);
		envelope.addContent(body);
		body.setContent(getResumeJsonForSE);
		
		XMLOutputter xo = new XMLOutputter();
		return xo.outputString(document);
	}
	
	/**
	 * @method parseResumeJsonForSE
	 * @purpose 解析getResumeJsonForSE回覆內容
	 * @author bruce.sun
	 * @create 2017年8月9日 下午16:00:00
	 * @return String
	 * @modify
	 */
	private String parseResumeJsonForSE(String respXml) throws JDOMException, IOException{
		Namespace soapenv = Namespace.getNamespace("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
	    Namespace ns1 = Namespace.getNamespace("ns1", "urn:JbwsResumeControllerwsdl");
	    
		StringReader strReader = new StringReader(respXml);	
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(strReader);
		Element envelope = document.getRootElement();
		
		//soap body回覆內容
		Element body = envelope.getChild("Body", soapenv);
		Element getResumeJsonForSEResponse = body.getChild("getResumeJsonForSEResponse", ns1);
		Element returnTag = getResumeJsonForSEResponse.getChild("return");
		
		return returnTag.getTextTrim();
	}
	
	/**
	 * @method getResumeJsonForSE
	 * @purpose 呼叫getResumeJsonForSE soap串接
	 * @author bruce.sun
	 * @create 2017年8月9日 下午16:00:00
	 * @return String
	 * @modify
	 */
	public String getResumeJsonForSE(String endPoint, String idNoParam, String versionNoParam) throws Exception{
		String soapAction = "urn:JbwsResumeControllerwsdl#getResumeJsonForSE";
		String soapBody = this.createResumeJsonForSE(idNoParam, versionNoParam);

		HttpClientService httpClient = new HttpClientService();
		String responString = httpClient.sendSoapReq(endPoint, soapAction, soapBody);
		String parseString = this.parseResumeJsonForSE(responString);
		return parseString;
	}
	
	/**
	 * @method createResumeJsonForOldSE
	 * @purpose 產生getResumeJsonForOldSE請求內容
	 * @author bruce.sun
	 * @create 2017年8月9日 下午16:00:00
	 * @return String
	 * @modify
	 */
	private String createResumeJsonForOldSE(String idNoParam, String versionNoParam){
		Namespace soapenv = Namespace.getNamespace("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
	    Namespace urn = Namespace.getNamespace("urn", "urn:JbwsResumeControllerwsdl");
	    
	    Document document = new Document();
		Element envelope = new Element("Envelope", soapenv);
		Element header = new Element("Header", soapenv);
		Element body = new Element("Body", soapenv);
	    
		//soap body請求參數
		Element getResumeJsonForSE = new Element("getResumeJsonForOldSE", urn);
		Element idNo = new Element("idNo");
		idNo.setText(idNoParam);
		Element versionNo = new Element("versionNo");
		versionNo.setText(versionNoParam);
		getResumeJsonForSE.addContent(idNo);
		getResumeJsonForSE.addContent(versionNo);
		
		document.setRootElement(envelope);
		envelope.addContent(header);
		envelope.addContent(body);
		body.setContent(getResumeJsonForSE);
		
		XMLOutputter xo = new XMLOutputter();
		return xo.outputString(document);
	}
	
	/**
	 * @method parseResumeJsonForOldSE
	 * @purpose 解析getResumeJsonForOldSE回覆內容
	 * @author bruce.sun
	 * @create 2017年8月9日 下午16:00:00
	 * @return String
	 * @modify
	 */
	private String parseResumeJsonForOldSE(String respXml) throws JDOMException, IOException{
		Namespace soapenv = Namespace.getNamespace("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
	    Namespace ns1 = Namespace.getNamespace("ns1", "urn:JbwsResumeControllerwsdl");
	    
		StringReader strReader = new StringReader(respXml);	
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(strReader);
		Element envelope = document.getRootElement();
		
		//soap body回覆內容
		Element body = envelope.getChild("Body", soapenv);
		Element getResumeJsonForSEResponse = body.getChild("getResumeJsonForOldSEResponse", ns1);
		Element returnTag = getResumeJsonForSEResponse.getChild("return");
		
		return returnTag.getTextTrim();
	}
	
	/**
	 * @method getResumeJsonForOldSE
	 * @purpose 呼叫getResumeJsonForOldSE soap串
	 * @author bruce.sun
	 * @create 2017年8月9日 下午16:00:00
	 * @return String
	 * @modify
	 */
	public String getResumeJsonForOldSE(String endPoint, String idNoParam, String versionNoParam) throws Exception{
		String soapAction = "urn:JbwsResumeControllerwsdl#getResumeJsonForOldSE";
		String soapBody = this.createResumeJsonForOldSE(idNoParam, versionNoParam);

		HttpClientService httpClient = new HttpClientService();
		String responString = httpClient.sendSoapReq(endPoint, soapAction, soapBody);
		String parseString = this.parseResumeJsonForOldSE(responString);
		return parseString;
	}
	
	//執行進入點
	public static void main(String[] args) throws Exception {
		String endPoint = "http://wsp-jb.s104.com.tw/jb/apply_api/index.php?r=inner/jbwsResume/soap&ws=1";
		JbwsResume resume = new JbwsResume();
		//getResumeJsonForSE
		log.info(resume.getResumeJsonForSE(endPoint, "1", "1"));
		//getResumeJsonForOldSE
		log.info(resume.getResumeJsonForOldSE(endPoint, "9982", "1"));
	}
}
