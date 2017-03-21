package com.zjy.activiti.web.activiti.processinstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zjy.activiti.util.UserUtil;


@RequestMapping("/process-instance")
@Controller
public class ProcessInstanceController {
	private static Logger logger = LoggerFactory.getLogger(ProcessInstanceController.class);

	@Autowired
	private RepositoryService repositoryService ; 
	
	@Autowired
	private FormService formService ;

	@Autowired
	private RuntimeService runtimeService ;
	
	/**
	 * 提交表单字段， 并启动一个流程，
	 * @param processDefinitionId
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("/start/{processDefinitionId}")
	public String processStart(@PathVariable(value="processDefinitionId") String processDefinitionId , 
								HttpServletRequest request ,
								RedirectAttributes redirectAttributes){
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult() ;
		
		boolean hasStartFormKey = processDefinition.hasStartFormKey();
		
		Map<String, String> formValues = new HashMap<String, String>() ;
		
		/**
		 * 获取表单数据
		 */
		if(hasStartFormKey){//有hasStartFormKey
			Map<String, String[]> parameterMap = request.getParameterMap() ;
			Set<Entry<String, String[]>> entrySet = parameterMap.entrySet() ;
			for(Entry<String, String[]> entry : entrySet){
				String key = entry.getKey() ;
				formValues.put(key, entry.getValue()[0]);
			}
		}else{//动态表单
			StartFormData formData = formService.getStartFormData(processDefinitionId);
			List<FormProperty> formProperties = formData.getFormProperties();
			String value = null ;
			for(FormProperty formProperty : formProperties){
				value = request.getParameter(formProperty.getId());
				formValues.put(formProperty.getId(), value);
			}
		}
		
		User user = UserUtil.getUserFromSession(request.getSession());
		
		if(null == user || StringUtils.isBlank(user.getId())){
			return "redirect:/login.jsp?timeout=true";
		}
		ProcessInstance processInstance =  formService.submitStartFormData(processDefinitionId, formValues);
		redirectAttributes.addFlashAttribute("message","流程已启动，实例ID： "+processInstance.getId());
		logger.debug("start a processInstance " +processDefinition.getName());
		return "redirect:/deployment/process-list-view" ;
	}
	
}
