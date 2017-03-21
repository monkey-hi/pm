package com.zjy.activiti.util;

import javax.servlet.http.HttpSession;

import org.activiti.engine.identity.User;

public class UserUtil {

	private static final String USER_ = "user" ;
	/**
	 * 将用户信息保存到Session中
	 * @param user
	 * @param session
	 */
	public static void saveUserToSession(User user  , HttpSession session){
		session.setAttribute(USER_, user);
	}
	
	/**
	 * 从Session取用户信息
	 * @param session
	 * @return
	 */
	public static User getUserFromSession(HttpSession session){
		Object user = session.getAttribute(USER_);
		return user != null ? (User)user : null ;
	}
	
	
	public static void removeUserFromSession(HttpSession session){
		session.removeAttribute(USER_);
	}
}
