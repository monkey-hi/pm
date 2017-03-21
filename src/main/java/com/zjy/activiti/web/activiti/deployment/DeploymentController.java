package com.zjy.activiti.web.activiti.deployment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zjy.activiti.util.Page;
import com.zjy.activiti.util.PageUtil;

@Controller
@RequestMapping("/deployment")
public class DeploymentController {
	private static Logger logger = LoggerFactory.getLogger(DeploymentController.class);
	
	@Autowired
	private RepositoryService repositoryService ; 
	
	@Autowired
	private IdentityService identityService ;
	
	/**
	 * 流程定义列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/deployment-list")
	public ModelAndView deploymentList(@RequestParam(value="processDefinitionName",required=false) String  processDefinitionName , 
										HttpServletRequest request){
		ModelAndView mav  = new ModelAndView("deployment/deployment-list");
		Page<ProcessDefinition> page = new Page<ProcessDefinition>(PageUtil.PAGE_SIZE);
		int [] paras = PageUtil.init(page, request);
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery() ;
		
		if(StringUtils.isNotBlank(processDefinitionName)){
			processDefinitionQuery.processDefinitionNameLike("%"+processDefinitionName+"%");
			mav.addObject("processDefinitionName",processDefinitionName);
		}
		List<ProcessDefinition> processDefinitions = processDefinitionQuery.listPage(paras[0], paras[1]);
		
		page.setResult(processDefinitions);
		page.setTotalCount(processDefinitionQuery.count());
		mav.addObject("page",page);
		/**
		 * 读取所有人员列表
		 */
		List<User> users = identityService.createUserQuery().list() ;
		mav.addObject("users",users) ;
		
		/**
		 * 读取所有组
		 */
		
		List<Group> groups = identityService.createGroupQuery().list() ;
		mav.addObject("groups",groups);
		
		/**
		 * 读取每个流程定义的候选属性
		 */
		Map<String, Map<String, List<? extends Object>>> linksMap =setCandidateUserAndGroups(processDefinitions); 
		mav.addObject("linksMap",linksMap);
		
		return mav;
	}
	
	@RequestMapping("/process-list-view")
	public ModelAndView  processList(@RequestParam(value="processName",required=false) String processName , 
													HttpServletRequest request){
		
		ModelAndView  mav = new ModelAndView("deployment/process-list-view");
		
		Page<ProcessDefinition> page = new Page<ProcessDefinition>(PageUtil.PAGE_SIZE);
		int [] pageParams =  PageUtil.init(page, request);
		
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery() ;
		
		processDefinitionQuery.active() ;
		if(StringUtils.isNotBlank(processName)){
			processDefinitionQuery.processDefinitionNameLike("%"+processName+"%");
			mav.addObject("processName",processName);
		}
		
		List<ProcessDefinition> processDefinitions = processDefinitionQuery.listPage(pageParams[0], pageParams[1]);
		
		page.setResult(processDefinitions);
		page.setTotalCount(processDefinitionQuery.count());
		
		mav.addObject("page",page);
		
		Map<String, Map<String, List<? extends Object>>> linksMap = setCandidateUserAndGroups(processDefinitions);
	
		mav.addObject("linksMap",linksMap);
		
		return mav ;
	}
	
	
	/**
	 * 返回所有流程定义的候选属性
	 * @param processDefinitions
	 * @return
	 */
	protected Map<String, Map<String, List<? extends Object>>> setCandidateUserAndGroups(List<ProcessDefinition> processDefinitions){
		Map<String, Map<String, List<? extends Object>>> linksMap = new HashMap<String, Map<String,List<? extends Object>>>();
		for(ProcessDefinition processDefinition : processDefinitions){
			
			List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId()) ;
			Map<String, List<? extends Object>> single = new Hashtable<String,List<? extends Object>>() ;
			List<User> users = new ArrayList<User>() ;
			List<Group> groups = new ArrayList<Group>();
			String userId = null;
			String groupId = null ;
			for(IdentityLink identityLink : identityLinks){
				userId = identityLink.getUserId() ;
				if(StringUtils.isNotBlank(userId)){
					users.add(identityService.createUserQuery().userId(userId).singleResult());
				}
				groupId = identityLink.getGroupId() ;
				if(StringUtils.isNotBlank(groupId)){
					groups.add(identityService.createGroupQuery().groupId(groupId).singleResult());
				}
			}
			single.put("user", users);
			single.put("group", groups);
			
			linksMap.put(processDefinition.getId(), single);
			
		}
		
		return linksMap;
		
	}
	
	
	/**
	 * 读取流程资源文件
	 * @param pid 流程定义id
	 * @param resourceName 流程资源名称
	 */
	@RequestMapping("/read-resource")
	public void readResource(@RequestParam(value="pdid") String pid , 
							 @RequestParam(value="resourceName") String resourceName,
							 HttpServletResponse response) throws Exception{
		
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(pid).singleResult();
		
		InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName) ;
		
		byte [] bs = new byte[1024] ;
		int len = -1 ;
		while( (len = inputStream.read(bs, 0, 1024)) != -1){
			response.getOutputStream().write(bs, 0, len);
		}
	}
	
	/**
	 * 级联删除流程部署 
	 * @param deploymentId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/delete-deployment")
	public String deleteDeployment(@RequestParam(value="deploymentId") String deploymentId,RedirectAttributes redirectAttributes){
		Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult() ;
		if(null  != deployment ){
			repositoryService.deleteDeployment(deploymentId, true);
			redirectAttributes.addFlashAttribute("message","删除成功");
		}else{
			redirectAttributes.addFlashAttribute("message","删除失败");
		}

		return "redirect:deployment-list" ;
	}
	
	@RequestMapping("/deploy-process")
	public String deployProcess(@RequestParam(value="file" , required=true) MultipartFile file,
								RedirectAttributes redirectAttributes ){
		String  fileName = file.getOriginalFilename() ;
		try {
			InputStream inputStream = file.getInputStream() ;
			String extension = FilenameUtils.getExtension(fileName);
			DeploymentBuilder deploymentBuilder = repositoryService.createDeployment() ;
			if("bar".equals(extension) || "zip".equals(extension)){
				ZipInputStream zipInputStream = new ZipInputStream(inputStream);
				deploymentBuilder.addZipInputStream(zipInputStream);
			}else{
				deploymentBuilder.addInputStream(fileName, inputStream);
			}
			deploymentBuilder.deploy() ;
			redirectAttributes.addFlashAttribute("message","部署成功");
			return "redirect:deployment-list";
		} catch (Exception e) {
			logger.error("error  on deployment , because of input file ");
			redirectAttributes.addFlashAttribute("message","部署失败,部署文件不符合格式");
		}
		return "redirect:deployment-list";
	}
	
}
