package com.util;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Log4jFactory {

		private final static String DEFAULT_LOG_PATH = "log4j.log";
	
		static{
			System.setProperty("log4jlog", DEFAULT_LOG_PATH);
			try{
				URL url = Log4jFactory.class.getClassLoader().getResource("log4j.properties");
				if(url==null){
					url = Log4jFactory.class.getClassLoader().getResource("conf/log4j.properties");
				}
				PropertyConfigurator.configure(url);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("e:" + e);
			}
			//PropertyConfigurator.configure("conf.log4j.properties");
		}

		public static Logger getInstance(Class c, String logpath){
			return getInstance(c.getName(), logpath);
		}
		
		public static Logger getInstance(String name, String logpath){
			changeLogPath(logpath);
			return getInstance(name);
		}

		public static Logger getInstance(Class c){
			return getInstance(c.getName());
		}
		
		public static Logger getInstance(String name){
			return Logger.getLogger(name);
		}
		
		private static void changeLogPath(String logpath){
			if(logpath!=null){
				System.setProperty("log4jlog", logpath);
				try{
					URL url = Log4jFactory.class.getClassLoader().getResource("log4j.properties");
					if(url==null){
						System.out.println("conf/log4j.properties");
						url = Log4jFactory.class.getClassLoader().getResource("conf/log4j.properties");
					}
					PropertyConfigurator.configure(url);
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("e:" + e);
				}
			}
		}
		public static String getCallingClassname() {
		    return new RuntimeException().getStackTrace()[1].getClassName();
		}
	}

