package com.zjy.activiti.activiti.service;

import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessDefinitionService {

	private Logger logger = LoggerFactory.getLogger(ProcessDefinitionService.class) ;
		
	@Autowired
	private RepositoryService repositoryService ;
	
	@Transactional
	public void  setStartables(String  processDefinitionId , String [] users , String [] groups ){
		//1.清除现有的所有设置
		List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId) ;
		 
		for(IdentityLink identityLink : identityLinks){
			if(StringUtils.isNotBlank(identityLink.getUserId())){
				repositoryService.deleteCandidateStarterUser(processDefinitionId, identityLink.getUserId());
			}
			if(StringUtils.isNotBlank(identityLink.getGroupId())){
				repositoryService.deleteCandidateStarterGroup(processDefinitionId, identityLink.getGroupId());
			}
		}
		
		//2.添加候选启动人
		if(!ArrayUtils.isEmpty(users)){
			for(String user : users){
				repositoryService.addCandidateStarterUser(processDefinitionId, user);
			}
		}
		//3.添加候选启动组
		if(!ArrayUtils.isEmpty(groups)){
			for(String group : groups){
				repositoryService.addCandidateStarterGroup(processDefinitionId, group); 
			}
		}
	}	
}
