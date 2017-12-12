package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.conf.Configure;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class L2MongoClinet {
	static Logger log = Log4jFactory.getInstance(Log4jFactory.getCallingClassname());
	static MongoClient mongoClient = null;
	static MongoDatabase database = null;
	static ResourceBundle RB = new Configure().getResource();
	static JsonObject FieldType = new JsonObject();

	public L2MongoClinet() {
		MongoClientURI uri = new MongoClientURI(RB.getString("MongoClientURI"));
		mongoClient = new MongoClient(uri);
		database = mongoClient.getDatabase(RB.getString("MongoDB"));
		FieldType = (JsonObject) new JsonParser().parse(RB.getString("FieldType"));

	}

	public boolean InsertOne(String collectionName, JsonObject parameter)  throws Exception {
		//log.info("strt insert="+parameter.get("ID_NO").getAsLong()+"-"+parameter.get("VERSION_NO").getAsLong());
		/*Document inDoc = new Document();
		Set<Map.Entry<String, JsonElement>> entries = parameter.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String key = entry.getKey();
			log.info(key);
			if (parameter.get(key).isJsonObject()) {
				Document doc = new Document();
				JsonObject jo=parameter.get(key).getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> jokeys = jo.entrySet();
				for (Map.Entry<String, JsonElement> joentry : jokeys) {
					String jokey = joentry.getKey();
					doc.put(jokey, jo.get(jokey));
				}
				inDoc.put(key, doc);
				log.info("isJsonObject=" + parameter.get(key).toString());
			} else if (parameter.get(key).isJsonArray()) {
				JsonArray ja=parameter.get(key).getAsJsonArray();
				List<Document> al = new ArrayList<Document>();
				for(int i=0;i<ja.size();i++){
					Document doc = new Document();
					JsonObject jo=ja.get(i).getAsJsonObject();
					Set<Map.Entry<String, JsonElement>> jokeys = jo.entrySet();
					for (Map.Entry<String, JsonElement> joentry : jokeys) {
						String jokey = joentry.getKey();
						doc.put(jokey, jo.get(jokey));
					}
					al.add(doc);
				}
				inDoc.put(key, al);
				log.info("isJsonArray=" + parameter.get(key).toString());
			} else if (parameter.get(key).isJsonPrimitive()) {
				if (FieldType.has(key)) {
					String type = FieldType.get(key).getAsString();
					if (type.equals("int")) {
						inDoc.put(key, new BsonInt32(Integer.parseInt(parameter.get(key).getAsString())));
					} else if (type.equals("bigint")) {
						inDoc.put(key, new BsonInt64(Long.parseLong(parameter.get(key).getAsString())));
					} else {
						inDoc.put(key,parameter.get(key).getAsString());
					}

				} else {
					inDoc.put(key, parameter.get(key).getAsString());
				}

			}
		}
		log.info(inDoc.toString());*/
		 MongoCollection<Document> collection =
		 database.getCollection(collectionName);
		
		 Document inpDoc = Document.parse(parameter.toString());
		 Set<Map.Entry<String, JsonElement>> jokeys = FieldType.entrySet();
			for (Map.Entry<String, JsonElement> joentry : jokeys) {
				String jokey = joentry.getKey();
				if (inpDoc.containsKey(jokey)){
					String type = FieldType.get(jokey).getAsString();
					if (type.equals("int")) {
						inpDoc.put(jokey, new BsonInt32(Integer.parseInt(parameter.get(jokey).getAsString())));
					} else if (type.equals("bigint")) {
						inpDoc.put(jokey, new BsonInt64(Long.parseLong(parameter.get(jokey).getAsString())));
					} else {
						inpDoc.put(jokey,parameter.get(jokey).getAsString());
					}
				}
			}
		 collection.insertOne(inpDoc);
		return true;
	}

	public boolean DeleteOne(String collectionName, String ID_NO ,String VERSION_NO)  throws Exception {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		Document inpDoc = new Document();
		inpDoc.put("ID_NO", new BsonInt64(Long.parseLong(ID_NO)));
		inpDoc.put("VERSION_NO", new BsonInt64(Long.parseLong(VERSION_NO)));
		collection.deleteOne(inpDoc);
		return true;
	}

	

	public boolean close()  throws Exception {
		if (mongoClient!=null)
				mongoClient.close();
		return true;
	}

}
