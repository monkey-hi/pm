<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
	<%@ include file="/common/global.jsp"%>
	<%@ include file="/common/meta.jsp" %>
	<title>流程列表</title>
	<%@ include file="/common/include-base-styles.jsp"%>
	<script src="${ctx }/js/common/jquery.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctx }/js/common/bootstrap.min.js"></script>
    <script type="text/javascript" src="${ctx }/js/common/plugins/datetimepicker/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript">
	$(function () {
		 // 显示/隐藏候选属性
	    $('.toggle-startable').click(function(){
	    	var index = $(this).data('index');
	    	$('#startable-'+index).fadeToggle("normal") ;
	    });
	});
	</script>
</head>
<body>
<div class='page-title ui-corner-all'>流程列表</div>
<c:if test="${not empty message }">
	<div id="message" class="alert alert-success">${message}</div>
	<!-- 自动隐藏提示信息 -->
	<script type="text/javascript">
		setTimeout(function (){
			$('#message').hide(3000);
		});
	</script>
</c:if>
<form class="form-search" method="post">
        流程名称：<input type="text" name="processName" value="${processName}" class="input-medium search-query">
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
        <th>流程图</th>
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
						href="${ctx }/deployment/read-resource?pdid=${pd.id }&resourceName=${pd.diagramResourceName }">查看</a>
				</td>
				<td>	
					<c:if test="${not empty linksMap[pd.id]['user'] || not empty linksMap[pd.id]['group']}">
						<a href="#" class="toggle-startable" data-index="${row.index}">
						 人[${fn:length(linksMap[pd.id]['user'])}],
						 组[${fn:length(linksMap[pd.id]['group'])}]
						</a>
					</c:if>
				</td>
				<td>
                <a class="btn btn-small" href='${ctx}/process/getform/start/${pd.id}'><i class="icon-play"></i>启动</a>
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
</body>
</html>