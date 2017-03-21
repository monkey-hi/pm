package com.zjy.activiti.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器
 * @author 郑金友
 *
 */
@Controller
@RequestMapping("/main")
public class MainController {

	@RequestMapping("/index")
	public String  index(){
		return "main/index" ;
	}
	
	@RequestMapping("/welcome")
	public String  welcome(){
		return "main/welcome" ;
	}
}
