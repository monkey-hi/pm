<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
	<%@ include file="/common/global.jsp"%>
	<%@ include file="/common/meta.jsp" %>
	<title>流程定义列表和部署</title>
	<%@ include file="/common/include-base-styles.jsp"%>
	<script src="${ctx }/js/common/jquery.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctx }/js/common/bootstrap.min.js"></script>
    <script type="text/javascript" src="${ctx }/js/common/plugins/datetimepicker/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript">
	//全局变量 
	var processDefinitionId  ;
	$(function () {
		 //设置候选人启动人
		$('.set-startable').click(function (){
			var pid = $(this).data('pid');
			processDefinitionId = pid ;
			var rowIndex = $(this).parents('tr').attr('index');
			if($('#startable-'+rowIndex).length == 1){
				var users = $('#startable-'+rowIndex).find('.users li');
				var groups = $('#startable-'+rowIndex).find('.groups li');
			}
			//打开对话框的时候先读取从后台读取的已有配置
			$('input[name=user],input[name=group]').attr('checked',false);
			$.getJSON(ctx + '/process/startable/read/' + pid , function (result){
				$(result.users).each(function (){
					$('input[name=user][value='+this+']').attr('checked',true);
				});
				$(result.groups).each(function (){
					$('input[name=group][value='+this+']').attr('checked',true);
				});
				$('#addStartableModal').modal();
			});
						
		});
        // 全选用户、组
        $('#selectAllUser').change(function (){
        	$('input[name=user]').attr('checked',$(this).attr('checked')||false);
        });
        $('#selectAllGroup').change(function (){
        	$('input[name=group]').attr('checked',$(this).attr('checked')||false);
        });
		$('#addStartableAttr').click(function () {
			var users = new Array() ;
			var groups = new Array() ;
			$('input[name=user]:checked').each(function (){
				users.push($(this).val());
			});
			$('input[name=group]:checked').each(function (){
				groups.push($(this).val());
			});
			$.post(ctx + '/process/startable/set/' + processDefinitionId ,{
				users : users ,
				groups : groups
			},function (resp){
				if('true'==resp){
					location.reload();
				}else{
					alert('设置失败');
				}
			});
		});
		 // 显示/隐藏候选属性
	    $('.toggle-startable').click(function(){
	    	var index = $(this).data('index');
	    	$('#startable-'+index).fadeToggle("normal") ;
	    });
		// 显示/隐藏部署表单		 
		$('.deploy').click(function (){
			$('#deloyFiledSet').fadeToggle("normal");
		}) ;
		
		//显示日期框
		$('#datetimepicker').datetimepicker();
		
        $('.resource-name').tooltip();

        $('input[name=execTime]').change(function () {
            if ($(this).val() == 'now') {
                $('#changeStateForm input[name=effectiveDate]').val('');
            }
        });

        // 监听挂起、激活的click事件设置表单的processDefintionId值
        $('.change-state').click(function () {
            var state = $(this).data('state');
             processDefinitionId = $(this).data('pid');
            $('#changeStateForm input[name=processDefinitionId]').val(processDefinitionId);
            $('.state').text(state == 'active' ? '激活' : '挂起');
            $('#changeStateForm').attr('action', ctx + '/process/' + state);
        });
	});
	</script>
</head>
<body>
<c:if test="${not empty message }">
	<div id="message" class="alert alert-success">${message}</div>
	<!-- 自动隐藏提示信息 -->
	<script type="text/javascript">
		setTimeout(function (){
			$('#message').hide(3000);
		});
	</script>
</c:if>
<div style="text-align: right;padding: 2px 1em 2px">
		<span><a  class="deploy" href='#'><i class="icon-circle-arrow-up"></i>部署流程</a></span>
</div>
<fieldset id="deloyFiledSet" style="display: none">
	<legend>部署流程资源</legend>
	<form action="${ctx}/deployment/deploy-process" method="post" enctype="multipart/form-data" style="margin-top:1em;">
		<input type="file" name="file">
		<input type="submit" value="Submit" class="btn">
	</form>
	<hr class="soften"/>
</fieldset>
<div class='page-title ui-corner-all'>流程部署列表</div>
<form class="form-search" method="post">
        流程定义名称：<input type="text" name="processDefinitionName" value="${processDefinitionName}" class="input-medium search-query">
        <button type="submit" class="btn">查询</button>
</form>
<table width="100%" class="table table-bordered table-hover table-condensed">
	<thead>
	<tr>
		<th>流程定义ID</th>
		<th>部署ID</th>
		<th>流程定义名称</th>
		<th>流程定义Key</th>
		<th width="150">流程描述</th>
		<th width="50">版本号</th>
        <th>XML</th>
        <th>流程图</th>
        <th width="40">状态</th>
        <th>候选启动</th>
        <th width="70">操作</th>
	</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.result}" var="pd" varStatus="row">
			<tr index="${row.index}">
				<td>${pd.id}</td>
				<td>${pd.deploymentId}</td>
				<td>${pd.name}</td>
				<td>${pd.key}</td>
				<td>${pd.description}</td>
				<td style="text-align: center;">${pd.version}</td>
				<td><a target="_blank" title="资源名称:${pd.resourceName}"
						href="${ctx }/deployment/read-resource?pdid=${pd.id }&resourceName=${pd.resourceName }">查看</a>
				</td>
				<td><a target="_blank" title="资源名称:${pd.resourceName}"
						href="${ctx }/deployment/read-resource?pdid=${pd.id }&resourceName=${pd.diagramResourceName }">查看</a>
				</td>
				<td style="text-align: center;">${pd.suspended ? '挂起':'正常' }</td>
				<td>	
					<c:if test="${not empty linksMap[pd.id]['user'] || not empty linksMap[pd.id]['group']}">
						<a href="#" class="toggle-startable" data-index="${row.index}">
						 人[${fn:length(linksMap[pd.id]['user'])}],
						 组[${fn:length(linksMap[pd.id]['group'])}]
						</a>
					</c:if>
				</td>
				<td>
					<div class="btn-group">
						<a class="btn btn-small btn-danger dropdown-toggle" data-toggle="dropdown" href="#">操作
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu" style="min-width: 150px;margin-left: -50px;">
							<li><a href="${ctx}/deployment/delete-deployment?deploymentId=${pd.deploymentId}"><i class="icon-trash"></i>删除</a></li>
							<li><a href="#" class="set-startable" data-pid="${pd.id}" title="设置候选启动人/组"><i class="icon-user"></i>候选启动</a></li>
							<c:if test="${!pd.suspended }">
								<li><a href="#changeStateModal" class="change-state" data-state="suspend"
									   data-pid="${pd.id }"  data-toggle="modal"><i class="icon-lock"></i>挂起</a></li>
							</c:if>
							<c:if test="${pd.suspended }">
								<li><a href="#changeStateModal" class="change-state" data-state="active"
									   data-pid="${pd.id }"  data-toggle="modal"><i class="icon-ok"></i>激活</a></li>
							</c:if>	
							<li><a href="#"><i class=" icon-circle-arrow-right"></i>模型Model</a> </li>						
						</ul>
					</div>
				</td>
			</tr>
			<!-- 显示候选属性 -->
			<c:if test="${not empty linksMap[pd.id]}">
				<tr id="startable-${row.index}" style="display: none">
					<td colspan="10">
						<table>
							<thead>
							<tr>
								<th class="text-info">候选启动人</th>
								<th class="text-info">候选启动组</th>	
							</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										<ul class="users">
											<c:forEach items="${linksMap[pd.id]['user'] }" var="user">
												<li>${user.firstName } . ${user.lastName }</li>
											</c:forEach>
										</ul>
									</td>
									<td>
										<ul class="groups">
											<c:forEach items="${linksMap[pd.id]['group']}" var="group">
												<li>${group.name}</li>
											</c:forEach>
										</ul>
									</td>
								</tr>
							</tbody>
						</table>
					</td>	
				</tr>
			</c:if>
		</c:forEach>
	</tbody>
</table>
<tags:pagination page="${page}" paginationSize="${page.pageSize}"/>
<!-- 设置流程候选启动组 -->
<div id="addStartableModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="addStartableModalLabel"
	 aria-hidden="true"	>
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="addStartableModalLabel"><span class="state"></span>设置候选启动人|组</h4>
	</div>
	<div class="modal-body">
		<div class="row">
			<div class="span3">
				<label class="checkbox" >
					<input type="checkbox" id="selectAllUser">候选启动(人)</label>
				<hr>
				<c:forEach items="${users}" var="user">
					<label class="checkbox"><input type="checkbox" value="${user.id }" name="user">${user.firstName}.${user.lastName}</label>
				</c:forEach>
			</div>
			<div class="span3">
				<label class="checkbox" >
					<input type="checkbox" id="selectAllGroup">候选启动(组)</label>
				<hr>
				<c:forEach items="${groups}" var="group">
					<label class="checkbox"><input type="checkbox" value="${group.id }" name="group">${group.name}</label>
				</c:forEach>
			</div>
		</div>
	</div>
	 <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
        <button type="button" id="addStartableAttr" class="btn btn-primary">确定<span class="state"></span></button>
    </div>
</div>

<!-- 流程挂起和激活 -->
<div id="changeStateModal" class="modal hide fade" tabindex="-1" role="dialog" 
	 aria-labelledby="changeStateModalModalLabel" aria-hidden="true" >
	<div class="modal-headeer">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="changeStateModalLabel"><span class="state">流程定义</span></h4>
	</div>
	<div class="modal-body">
		<form id="changeStateForm" class="form-horizontal" method="post" >
			<input type="hidden" name="processDefinitionId">
			<div class="control-group">
				<label class="control-label">何时执行</label>
				<div class="controls">
					<label class="radio">
						<input type="radio" name="execTime" id="optionRadio1" value="now" checked>现在
					</label>
					<label class="radio">
						<input type="radio" name="execTime" id="optionRadio2" value="timer">定时
						<div id="datetimepicker" class="input-append date">
                            <input data-format="yyyy-MM-dd hh:mm:ss" name="effectiveDate" class="input-medium"
                                   type="text"/>
                                <span class="add-on">
                                    <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                                </span>
                        </div>
					</label>
				</div>
			</div>
			 <div class="control-group">
                <div class="controls">
                    <label class="checkbox">
                        <input type="checkbox" name="cascade" checked="checked"/>同时挂起所有与流程定义相关的流程实例？
                    </label>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
                    <button type="submit" class="btn btn-primary">确定<span class="state"></span></button>
                </div>
            </div>
		 </form>
	</div>	 
</div>
</body>
</html>