/**
 *  SendMail.java
 * @Version:$Revision: 1.1 $<br>
 * @Author:Dale<br>
 *  Change History:<br>		
 * 
 *  Description:Mail發送物件
 *  Method Summary:
 */

package com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.conf.Configure;

public class MailClient {
	static Logger log = Log4jFactory.getInstance(Log4jFactory.getCallingClassname());
	static ResourceBundle RB = new Configure().getResource();
	private InternetAddress iaFromMail = null;		// 寄件者
	private InternetAddress[] iaToMail = null;		// 收件者
	private InternetAddress[] iaCCMail = null;		// 複本收件者
	private InternetAddress[] iaBCCMail = null;		// 密件副本收件者
	private String sSubject = "";					// 信件主旨
	private String sMailBody = "";					// 信件內容
	private String sMailServer = "";				// 發信主機
	private String sAttachFile = "";				// 附件檔案路徑
	private int iToFlag = 0;
	private int iCCFlag = 0;
	private int iBCCFlag = 0;
	private int iAttachFlag = 0;
	
	/**
     * setFromMail(String sMiemail, String sMiname) <br>
     * @param	String sMiemail, String sMiname
     * 設定郵件寄送者。
     */
	public void setFromMail(String sMiemail, String sMiname) throws Exception{
		this.iaFromMail = new InternetAddress(sMiemail, sMiname);
	}
	
	/**
     * setToMail(ArrayList<String> vToMail) <br>
     * @param	ArrayList<String> vToMail
     * 設定郵件收件者。
     */
	public void setToMail(String ToMails) throws Exception{
		List<String> arToMail = Arrays.asList(ToMails.split(";"));
		setToMail(arToMail);
	}
	public void setToMail(List<String> arToMail) throws Exception{
		if(arToMail.size() > 0){
			this.iToFlag = 1;
			this.iaToMail = new InternetAddress[arToMail.size()];
			for(int i=0;i<arToMail.size();i++){
				this.iaToMail[i] = new InternetAddress(arToMail.get(i).toString());
			}
		}
	}
	
	/**
     * setCCMail(ArrayList<String> vCCMail) <br>
     * @param	ArrayList<String> vCCMail
     * 設定郵件副本收件者。
     */
	public void setCCMail(String CCMails) throws Exception{
		List<String> arCCMail = Arrays.asList(CCMails.split(";"));
		setCCMail(arCCMail);
	}
	public void setCCMail(List<String>  arCCMail) throws Exception{
		if(arCCMail.size() > 0){
			this.iCCFlag = 1;
			this.iaCCMail = new InternetAddress[arCCMail.size()];
			for(int i=0;i<arCCMail.size();i++){
				this.iaCCMail[i] = new InternetAddress(arCCMail.get(i).toString());
				
			}
		}
	}
	
	/**
     * setBCCail(Vector vBCCMail) <br>
     * @param	Vector vBCCMail
     * 設定郵件密件副本收件者。
     */
	public void setBCCMail(String BCCMails) throws Exception{
		List<String> arBCCMail =  Arrays.asList(BCCMails.split(";"));
		setBCCMail(arBCCMail);
	}
	public void setBCCMail(List<String> arBCCMail) throws Exception{
		if(arBCCMail.size() > 0){
			this.iBCCFlag = 1;
			this.iaBCCMail = new InternetAddress[arBCCMail.size()];
			for(int i=0;i<arBCCMail.size();i++){
				this.iaBCCMail[i] = new InternetAddress(arBCCMail.get(i).toString());
			}
		}
	}
	
	/**
     * setSubject(String sSubject) <br>
     * @param	String sSubject
     * 設定郵件標題。
     */
	public void setSubject(String sSubject)throws Exception{
		this.sSubject = sSubject;
	}
	
	/**
     * setMailBody(String sMailBody) <br>
     * @param	String sMailBody
     * 設定郵件內容。
     */
	public void setMailBody(String sMailBody)throws Exception{
		this.sMailBody = sMailBody;
	}
	
	/**
     * setAttachFile(String sFilePath) <br>
     * @param	String sFilePath
     * 設定郵件附件檔案路徑。
     */
	public void setAttachFile(String sFilePath)throws Exception{
		if(sFilePath.length()==0 || sFilePath.equalsIgnoreCase("")){
			this.iAttachFlag = 0;
			this.sAttachFile = "";
		}else{
			this.iAttachFlag = 1;
			this.sAttachFile = sFilePath;
		}
	}
	
	/**
     * setMailServer(String sMailServer) <br>
     * @param	String sMailServer
     * 設定郵件主機。
     */
	public void setMailServer(String sMailServer) throws Exception{
		if(sMailServer.length() != 0){
			this.sMailServer = sMailServer;
		}
	}
	
	
	/**
     * send() <br>
     * 郵件發送。
     */
	public  void send() throws Exception{
		Properties prop_session = new Properties();
        prop_session.put("mail.smtp.host", this.sMailServer);
        prop_session.put("mail.debug", "false");

        Session session = Session.getInstance(prop_session, null);
        MimeMessage message = new MimeMessage(session);

        message.setFrom(this.iaFromMail);
        if(this.iToFlag != 0){
         	message.addRecipients(Message.RecipientType.TO, this.iaToMail);
        }
        if(this.iCCFlag != 0){
          	message.addRecipients(Message.RecipientType.CC, this.iaCCMail);
        }
        if(this.iBCCFlag != 0){
          	message.addRecipients(Message.RecipientType.BCC, this.iaBCCMail);
        }
        message.setSentDate(new java.util.Date());
        message.setSubject(this.sSubject, "UTF-8");

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(this.sMailBody, "text/html; charset=UTF-8");
        MimeBodyPart attachPart = new MimeBodyPart();
        if(this.iAttachFlag != 0){
        	FileDataSource fds = new FileDataSource(this.sAttachFile);
        	attachPart.setDataHandler(new DataHandler(fds));
        	attachPart.setFileName(MimeUtility.encodeText(fds.getName()));
        }
            
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        if(this.iAttachFlag != 0){
        	multipart.addBodyPart(attachPart);
        }
            
        message.setContent(multipart);
        Transport.send(message);
		 
	}
	
}
