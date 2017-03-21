package com.zjy.activiti.util;

import java.util.Iterator;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class InitEngineeDatabase {
	 public static void main(String[] args) {
	        ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault().buildProcessEngine();
	    }
	    
	    public InitEngineeDatabase(){
	    	
	    }
	    public InitEngineeDatabase(IdentityService  identityService){
	    	try {
				initUserAndGroupFromXML(identityService);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public static void initUserAndGroupFromXML(IdentityService identityService) throws  Exception{
	    	System.out.println("*********************************************************");
	    	
	    	String fileName = "data/identity-data.xml" ;
	    	Document document = read(fileName) ;
	        if(null != document ){
	        	Element root = document.getRootElement() ;
	        	Element tElement = null;
	        	Group group = null ;
	        	User  user = null;
	        	//添加组
	        	for(Iterator iterator = root.elementIterator("ACT_ID_GROUP") ; iterator.hasNext();){
	        		tElement =(Element)iterator.next();
	        		group = identityService.newGroup(tElement.attributeValue("ID_"));
	        		group.setName(tElement.attributeValue("NAME_"));
	        		identityService.saveGroup(group);
	        	}
	        	//添加角色
	        	for(Iterator iterator = root.elementIterator("ACT_ID_USER") ; iterator.hasNext();){
	        		tElement =(Element)iterator.next();
	        		user = identityService.newUser(tElement.attributeValue("ID_"));
	        		user.setFirstName(tElement.attributeValue("FIRST_"));
	        		user.setLastName(tElement.attributeValue("LAST_"));
	        		user.setEmail(tElement.attributeValue("EMAIL_"));
	        		user.setPassword(tElement.attributeValue("PWD_"));
	        		identityService.saveUser(user);
	        	}
	        	//添加角色和用户的关系
	        	for(Iterator iterator = root.elementIterator("ACT_ID_MEMBERSHIP"); iterator.hasNext();){
	        		tElement = (Element)iterator.next() ;
	        		identityService.createMembership(tElement.attributeValue("USER_ID_"),tElement.attributeValue("GROUP_ID_"));
	        	}
	        	
	        }
	    }
	    /**
	     * 获得  xml Document 文档
	     * @param fileName
	     * @return
	     * @throws Exception
	     */
	    public static Document read(String fileName) throws Exception{
	    	SAXReader reader = new SAXReader() ;
	    	Document document = reader.read(InitEngineeDatabase.class.getClassLoader().getResource(fileName));
	    	return document;
	    }
}
