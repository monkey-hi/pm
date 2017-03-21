package com.zjy.activiti.web.activiti.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zjy.activiti.activiti.service.ProcessDefinitionService;
import com.zjy.activiti.util.UserUtil;

@Controller
@RequestMapping("/process")
public class ProcessController {
	private static Logger logger = LoggerFactory.getLogger(ProcessController.class);
	
	@Autowired
	private RepositoryService repositoryService ;
	
	
	@Autowired
	private ProcessDefinitionService processDefinitionService ;
	
	@Autowired
	private FormService formService ;
	
	/**
	 * 读取流程定义的候选属性
	 * @param processDefinitionId
	 * @return
	 */
	@RequestMapping("/startable/read/{pid}")
	@ResponseBody
	public Map<String, List<String>> readProcessStartable(@PathVariable(value="pid") String processDefinitionId){
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<String>  users = new ArrayList<String> () ;
		List<String>  groups = new ArrayList<String>();
		
		List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId);
		for(IdentityLink identityLink : identityLinks){
			if(StringUtils.isNotBlank(identityLink.getUserId())){
				users.add(identityLink.getUserId());
			}
			if(StringUtils.isNotBlank(identityLink.getGroupId())){
				groups.add(identityLink.getGroupId());
			}
		}
		result.put("users",users);
		result.put("groups",groups);
		
		return result ;
	}
	
	/**
	 * 设置流程定义的候选启动
	 * @param processDefinitionId
	 * @param users
	 * @param groups
	 * @return
	 */
	@RequestMapping("/startable/set/{pid}")
	@ResponseBody
	public String setProcessStartable(@PathVariable(value="pid") String processDefinitionId , 
									 @RequestParam(value="users[]" , required=false) String [] users , 
									 @RequestParam(value="groups[]" , required=false) String [] groups ){
		processDefinitionService.setStartables(processDefinitionId, users, groups);
		return "true";
	}
	
	@RequestMapping(value="/{state}",method=RequestMethod.POST)
	public String changeProcessState(@PathVariable(value="state") String state , 
									 @RequestParam(value="processDefinitionId") String processDefinitionId ,
									 @RequestParam(value="cascade" , required=false) Boolean cascade ,
									 @RequestParam(value="effectiveDate" , required=false) String effectiveDate ,
									 RedirectAttributes redirectAttributes)throws Exception{
		Date effectiveDateTem = null ;
		if(StringUtils.isNotBlank(effectiveDate)){
			effectiveDateTem = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(effectiveDate);
		}
		if(StringUtils.isNotBlank(state)){
			if("active".equals(state)){
				//激活流程
				repositoryService.activateProcessDefinitionById(processDefinitionId,cascade,effectiveDateTem);
				redirectAttributes.addFlashAttribute("message",processDefinitionId+" 已激活");
			} else if("suspend".equals(state)){
				//挂起流程
				repositoryService.suspendProcessDefinitionById(processDefinitionId,cascade,effectiveDateTem);
				redirectAttributes.addFlashAttribute("message",processDefinitionId+" 已挂起");
			}
		}
		return "redirect:/deployment/deployment-list";
	}

	@RequestMapping("/getform/start/{processDefinitionId}")
	public ModelAndView getFormStart(@PathVariable(value="processDefinitionId") String processDefinitionId , 
									 HttpServletRequest request ,
									 RedirectAttributes redirectAttributes){
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
 																			 .processDefinitionId(processDefinitionId).singleResult() ;
		User user = UserUtil.getUserFromSession(request.getSession());
		List<Group> groups = (List<Group>)request.getSession().getAttribute("groups") ;
		
		/**
		 * 权限拦截， 判断用户是否有启动权限
		 * 启动流程的条件
		 * 	1.流程没有候选启动人，
		 *  2.当前用户是候选启动人，
		 *  3.当前用户所在组是候选启动组
		 */
		boolean startable = false ;
	    List<IdentityLink> links = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId);
	    if(null == links || links.isEmpty()){
	    	startable = true ;
	    }
	    for(IdentityLink identityLink : links){
	    	if(StringUtils.isNotBlank(identityLink.getUserId()) && identityLink.getUserId().equals(user.getId())){
	    		startable = true ;
	    		break ;
	    	}
	    	if(StringUtils.isNotBlank(identityLink.getGroupId())){
	    		for(Group group : groups){
	    			if(group.getName().equals(identityLink.getGroupId())){
	    				startable = true ;
	    				break ;
	    			}
	    		}
	    	}
	    }
	    if(!startable){
	    	redirectAttributes.addFlashAttribute("message","对不起 ， 您没有权限启动 【"+processDefinition.getName()+"】");
	    	return new ModelAndView("redirect:/deployment/process-list-view");
	    }
	    
	    String viewName = "process/process-start-form";
	    ModelAndView mav = new ModelAndView(viewName);
	    
	    boolean hasStartFormKey = processDefinition.hasStartFormKey();
	    //判断是否有启动表单
	    if(hasStartFormKey){
	    	Object renderStartFormData = formService.getRenderedStartForm(processDefinitionId); 
	    	mav.addObject("startFormData",renderStartFormData);
	    	mav.addObject("processDefinition",processDefinition);
	    }else { //动态表单
	    	StartFormData startFormData = formService.getStartFormData(processDefinitionId) ;
	    	mav.addObject("startFormData",startFormData);
	    }
	    mav.addObject("processDefinitionId",processDefinitionId);
	    mav.addObject("hasStartFormKey",hasStartFormKey);
	    
	    return mav ;
	}
	
}
