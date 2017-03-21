package com.zjy.activiti.web.activiti.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zjy.activiti.entity.UserDTO;
import com.zjy.activiti.util.UserUtil;

import jodd.typeconverter.Convert;

@Controller
@RequestMapping("/user")
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class) ;
	
	@Autowired
	private IdentityService identityService ; 
	
	/**
	 * 登录系统
	 * @param username 用户名 
	 * @param password 密码
	 * @param request  
	 * @return
	 */
	@RequestMapping("/logon")
	public String  login(@RequestParam(value="username") String username , 
						 @RequestParam(value="password") String password ,
						 HttpServletRequest request){
		logger.debug("request parameter username={} , password={}",username,password);
		
		boolean checkpassword = identityService.checkPassword(username, password) ;
		
		if(checkpassword){
			User user = identityService.createUserQuery().userId(username).singleResult() ;
			UserUtil.saveUserToSession(user, request.getSession());
			
			List<Group> groupList = identityService.createGroupQuery().groupMember(username).list() ;
			
			request.getSession().setAttribute("groups",groupList);
			
			String[]  groupNames = new String[groupList.size()];
			for(int i = 0 ; i < groupNames.length ;i++){
				groupNames[i] = groupList.get(i).getName() ;
			}
			
			request.getSession().setAttribute("groupNames", groupNames);
			
			return "redirect:/main/index" ;
		}else{
			return "redirect:/login.jsp?error=true" ;
		}
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session){
		UserUtil.removeUserFromSession(session);
		return "redirect:/login.jsp" ;
	}
	
	
	@RequestMapping("/list")
	@ResponseBody
	public Map<String, List<UserDTO>> list(){
		List<Group> groups = identityService.createGroupQuery().list() ;
		Map<String, List<UserDTO>> result = new HashMap<String, List<UserDTO>> () ;
		for(Group group : groups){
			List<User> users = identityService.createUserQuery().memberOfGroup(group.getId()).list();
			List<UserDTO> userDTOs = convertToDTO(users);
			result.put(group.getName(), userDTOs);
		}
		return result;
	}
	
	protected List<UserDTO> convertToDTO(List<User> users){
		List<UserDTO> userDTOs = new ArrayList<UserDTO> () ;
		UserDTO userDTO = null ;
		for(User user : users){
			userDTO = new UserDTO() ;
			userDTO.setId(user.getId());
			userDTO.setFirstName(user.getFirstName());
			userDTO.setLastName(user.getLastName());
			userDTO.setPassword(user.getPassword());
			userDTO.setEmail(user.getEmail());
			userDTOs.add(userDTO);
		}
		return userDTOs;
	}
}
