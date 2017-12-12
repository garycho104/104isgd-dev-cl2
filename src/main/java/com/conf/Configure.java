/**
 * Configure.java

 * Description:

 */

package com.conf;

import java.util.*;


public class Configure {
	
	
	public ResourceBundle getResource(){
		ResourceBundle RB = null;
		try{
			RB = ResourceBundle.getBundle("conf.configure");            
		}catch(Exception e){
			System.out.println("com.conf.Configure.getResource() Exception:"+e.getMessage());
		}
		return RB;
		    
	}
}
