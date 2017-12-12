

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.conf.Configure;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.util.Log4jFactory;


public class mongoBypass {
	/**
	 * @param args
	 */
	static ResourceBundle RB = new Configure().getResource();
	static Logger log = Log4jFactory.getInstance(Log4jFactory.getCallingClassname());
	static String exgName="";
	static String queueName="";
	public static void main(String[] args) throws Exception {
try{
	
			if (args.length!=2){
				 System.out.println("Proper Usage is: java program queueName exgName");
			        System.exit(0);
			}
		//	queueName="queue.leveltwo.resume.104puls";
		//	exgName="exg.resume104MongoBypass";
			queueName=args[0];
			exgName=args[1];
			
			
			
			ConnectionFactory factory_get = new ConnectionFactory();
			
			factory_get.setUsername(RB.getString("MqUser"));
			factory_get.setPassword(RB.getString("MqPwd"));
			factory_get.setVirtualHost(RB.getString("MqVirtualHost"));
			factory_get.setHost(RB.getString("MqHost"));
			factory_get.setPort(Integer.parseInt(RB.getString("MqPort")));
			
			ConnectionFactory factory_out = new ConnectionFactory();
			factory_out.setUsername(RB.getString("MqUser"));
			factory_out.setPassword(RB.getString("MqPwd"));
			factory_out.setVirtualHost(RB.getString("MqVirtualHost"));
			factory_out.setHost(RB.getString("MqHost"));
			factory_out.setPort(Integer.parseInt(RB.getString("MqPort")));
			com.rabbitmq.client.Connection conn_out = factory_out.newConnection();
			com.rabbitmq.client.Connection conn_mq = factory_get.newConnection();
			
			Channel channel = conn_mq.createChannel();
			boolean durable = true;
			//  channel.exchangeDeclare(exchangeName, "direct", durable);
			channel.queueDeclare(queueName, durable,false,false,null);
			//  channel.queueBind(queueName, exchangeName, routingKey);
			channel.basicQos(1);
			boolean noAck = false;
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, noAck, consumer);
			boolean runInfinite = true;
		
			while (runInfinite) {
				QueueingConsumer.Delivery delivery;
			    try {
			    	Channel channel_out = conn_out.createChannel();
			    	delivery = consumer.nextDelivery();
			    	if (!setexg(channel_out,new String(delivery.getBody()))){
			    		continue;
			    	}
			    	channel_out.close();
			    } catch (InterruptedException ie) {
	               continue;
	            }
	         channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	       }
			channel.close();
			conn_mq.close();
		
		}finally{
			
		}
	}
	public static boolean setexg(Channel channel,String msg) throws Exception {
		     int qcount=Integer.parseInt(RB.getString("qcount"));
		      String exchangeName =exgName;
		      JsonObject jo=new JsonParser().parse(msg).getAsJsonObject();
		      String idno=jo.get("ID_NO").getAsString();
		      String routingKey =String.valueOf(Integer.parseInt(idno.substring(idno.length()-1))%qcount);
		       byte[] messageBodyBytes = msg.getBytes();
		    	  channel.basicPublish(exchangeName, routingKey,MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes) ;
		return true;
	 }
}
