



import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.conf.Configure;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.util.JbwsResume;
import com.util.L2MongoClinet;
import com.util.Log4jFactory;
import com.util.MailClient;
import com.util.SqliteClinet;

public class cl2_mongo_new_sync {
	static ResourceBundle RB = new Configure().getResource();
	static Logger log = Log4jFactory.getInstance(Log4jFactory.getCallingClassname());
	
	static SqliteClinet sqlitclinet= new SqliteClinet();
	static JbwsResume jbwsresume=new JbwsResume();
	static MailClient mail=new MailClient();
	public static void main(String[] args) throws Exception {
		if (args.length!=1){
			System.out.println("Proper Usage is: java program queueName");
			System.exit(0);
		}
		L2MongoClinet l2mongo= new L2MongoClinet();
		com.rabbitmq.client.Connection conn_mq=null;
		Channel channel =null;
		try{
			
			ConnectionFactory factory_get = new ConnectionFactory();
			
			factory_get.setUsername(RB.getString("MqUser"));
			factory_get.setPassword(RB.getString("MqPwd"));
			factory_get.setVirtualHost(RB.getString("MqVirtualHost"));
			factory_get.setHost(RB.getString("MqHost"));
			factory_get.setPort(Integer.parseInt(RB.getString("MqPort")));
			conn_mq = factory_get.newConnection();
			channel = conn_mq.createChannel();
			String queuename =args[0];
			String collectionName="resumenew";
			boolean durable = true;
			channel.queueDeclare(queuename, durable,false,false,null);
			channel.basicQos(10);
			boolean noAck = false;
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queuename, noAck, consumer);
			boolean runInfinite = true;
			while (runInfinite) {
				QueueingConsumer.Delivery delivery;
			    try {
			    	delivery = consumer.nextDelivery();
			        String message = new String(delivery.getBody());
			    	JsonParser parser = new JsonParser();
			    	JsonObject jo_mess=parser.parse(new String(delivery.getBody())).getAsJsonObject();
			    	if (!jo_mess.get("ID_NO").isJsonNull() && !jo_mess.get("VERSION_NO").isJsonNull() && !jo_mess.get("ACTION").isJsonNull() && !jo_mess.get("MQ_TIME").isJsonNull()){
			    		
			    		String id_no=jo_mess.get("ID_NO").getAsString();
			    		String version_no=jo_mess.get("VERSION_NO").getAsString();
			    		String action=jo_mess.get("ACTION").getAsString();
			    		String mq_time=jo_mess.get("MQ_TIME").getAsString();
			    		JsonObject jo_resume=new JsonObject();
			    		try{
			    		if (action.equalsIgnoreCase("EDIT") || action.equalsIgnoreCase("ADD")){
			    			jo_resume=(JsonObject) parser.parse(jbwsresume.getResumeJsonForSE(RB.getString("New_Resume_EndPoint"), id_no, version_no));
			    			jo_resume.addProperty("LEVELTWO_MQ_TIME", mq_time);
			    			boolean del=l2mongo.DeleteOne(collectionName, id_no, version_no);
			    			boolean insert=l2mongo.InsertOne(collectionName, jo_resume);
			    			sqlitclinet.insert_datalog(queuename.replace(".", "_"), id_no, version_no, jo_resume.toString(), message);
			    		}else{
			    			l2mongo.DeleteOne(collectionName, id_no, version_no);
			    			sqlitclinet.insert_datalog(queuename.replace(".", "_"), id_no, version_no, "", message);
			    		}
			    		 } catch (Exception e) {
			    			 sqlitclinet.insert_errdatalog(queuename.replace(".", "_"), "", "", "", message);
			    			 SendErrorMail(queuename,message);
			    		 }
			    	}else{
			    		sqlitclinet.insert_errdatalog(queuename.replace(".", "_"), "", "", "", message);
			    	}	
	            } catch (InterruptedException ie) {
	               continue;
	            }
	         channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	        
	         
	      }
		
	/*	} catch (Exception e) {
			//  throw new RuntimeException("Unable to load logging property " + e);
			    
			log.info("ResumePlusBypass Exception:"+e);*/
		}finally{
			channel.close();
			conn_mq.close();
			l2mongo.close();
		}
		
	}
	public static void SendErrorMail(String queuename,String body) throws Exception {
		 mail.setFromMail("root@aplvc01.104.com.tw", "root");
		 mail.setToMail(RB.getString("ErrsendMaill"));
		 mail.setSubject("leveltwo resume mongo ap error "+queuename);
		 mail.setMailBody(body);
		 mail.setMailServer(RB.getString("MailServer"));
		 mail.send();
	}
}
