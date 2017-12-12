package com.util;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.slf4j.Logger;

import com.conf.Configure;
public class SqliteClinet {
	static ResourceBundle RB = new Configure().getResource();
	 Connection con=null;
	 String ndate="";
	public boolean getConnection(String queuename) throws Exception {
		if (con!=null) con.close();
		String logpath=RB.getString("logpath")+"/"+ndate+"_"+queuename;
		boolean iscreateTable=!new File(logpath).exists();
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:"+logpath);
		
		if (iscreateTable)
			createTable (con);
		return true;
	}

	// create Table
	private boolean createTable(Connection con) throws Exception {
		String[] createtablesql = ("CREATE TABLE if not exists datalog(no INTEGER PRIMARY KEY,idno varchar(30),vino varchar(30),cdate TIMESTAMP ,data TEXT,mqdata TEXT);"
									+ "CREATE TABLE if not exists errdatalog(no INTEGER PRIMARY KEY,idno varchar(30),vino varchar(30),cdate TIMESTAMP ,data TEXT,mqdata TEXT)").split(";");
		for (String sql : createtablesql) {
			Statement stat = null;
			stat = con.createStatement();
			stat.executeUpdate(sql);
			if (stat != null)
				stat.close();
		}
		return true;
	}


	// 新增

	public  void insert_datalog(String queuename, String idno,String versionno ,String data,String mqdata) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String now=sdf.format( new Date());
		if (con==null ||ndate.equals(now)){
			ndate=now;
			getConnection(queuename);
		}
		String sql = "INSERT INTO datalog(idno,vino,cdate,data,mqdata) VALUES(?,?,datetime('now','localtime'),?,?)";
		PreparedStatement pst = null;
		pst = con.prepareStatement(sql);
		pst.setString(1, idno);
		pst.setString(2, versionno);
		pst.setString(3, data);
		pst.setString(4, mqdata);
		pst.executeUpdate();
		if (pst!=null) pst.close();
	}
	public  void insert_errdatalog(String queuename, String idno,String versionno ,String data,String mqdata) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String now=sdf.format( new Date());
		if (con==null ||ndate.equals(now)){
			ndate=now;
			getConnection(queuename);
		}
			
		String sql = "INSERT INTO errdatalog(idno,vino,cdate,data,mqdata) VALUES(?,?,datetime('now','localtime'),?,?)";
		PreparedStatement pst = null;
		pst = con.prepareStatement(sql);
		pst.setString(1, idno);
		pst.setString(2, versionno);
		pst.setString(3, data);
		pst.setString(4, mqdata);
		pst.executeUpdate();
		if (pst!=null) pst.close();
	}
	
	public  void main(String args[]) throws Exception {
		SqliteClinet test = new SqliteClinet();
	}
}