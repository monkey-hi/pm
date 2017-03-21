<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
	<%@ include file="/common/global.jsp" %>
	<%@ include file="/common/meta.jsp" %>
	<title>首页</title>
	<%@ include file="/common/include-base-styles.jsp" %>
	<script src="${ctx }/js/common/jquery.js" type="text/javascript"></script>
    <script src="${ctx }/js/common/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx }/js/modules/main/main.js" type="text/javascript"></script>
	<link rel="stylesheet" type="text/css" href="${ctx }/css/menu.css"/>
    <style type="text/css">
        iframe {
            margin-top: .5em;
        }
    </style>    
	<script type="text/javascript">
		var notLogon = '<%=session.getAttribute("user")%>';
		if(!notLogon){
			location.href="${ctx}/login.jsp?timeout=true" ;
		}
	</script>
</head>
<body>
	<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a data-target=".nav-collapse" data-toggle="collapse" class="btn btn-navbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a href="#" class="brand">流程管理平台--PM</a>
            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active"><a href="#" rel="main/welcome"><i class="icon-home icon-black"></i>首页</a>
                    </li>
                    <li class="dropdown">
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                                class="icon-th-large icon-black"></i>个人事务<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="#" rel="#"><i class="icon-th-list icon-black"></i>待办任务</a></li>
                            <li><a href="#" rel="#"><i class="icon-th-list icon-black"></i>参与流程</a></li>
                             <li class="divider"></li>
                            <li><a href="#" rel="#"><i class="icon-th-list icon-black"></i>日程</a></li>
                            <li><a href="#" rel="#t"><i class="icon-th-list icon-black"></i>通讯录</a></li>
                            <li><a href="#" rel="#"><i class="icon-th-list icon-black"></i>备忘录</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                                class="icon-th-large icon-black"></i>流程管理<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="#" rel='deployment/deployment-list'><i class="icon-th-list icon-black"></i>流程部署</a></li>
                            <li><a href="#" rel='deployment/process-list-view'><i class="icon-th-list icon-black"></i>流程列表</a></li>
                            <li class="divider"></li>
                            <li><a href="#" rel='#'><i class="icon-th-list icon-black"></i>运行中流程</a></li>
                            <li><a href="#" rel='#'><i class="icon-th-list icon-black"></i>已归档流程</a></li>
                            <li class="divider"></li>
                            <li><a href="#" rel='#'><i class="icon-th-list icon-black"></i>流程设计</a></li>
                            <li><a href="#" rel='#'><i class="icon-th-list icon-black"></i>作业管理</a></li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                                class="icon-th-large icon-black"></i>引擎状态<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="#" rel='#'><i class="icon-th-list icon-black"></i>引擎属性</a></li>
                            <li><a href="#" rel='#'><i class="icon-th-list icon-black"></i>引擎数据库</a></li>
                        </ul>
                    </li>
                     <li><a href="#" rel='#'><i class="icon-th-large icon-black"></i>用户与组</a></li>
                </ul>
                <ul class="nav pull-right">
                    <li class="dropdown">
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#" title="角色：${groupNames}">
                            <i class="icon-user icon-black"
                               style="margin-right: .3em"></i>${user.firstName }&nbsp;${user.lastName }/${user.id }<b
                                class="caret"></b>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a id="changePwd" href="#"><i class="icon-wrench icon-black"></i>修改密码</a></li>
                            <li><a id="loginOut" href="#"><i class="icon-eject icon-black"></i>安全退出</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div class="container">
        <iframe id="mainIframe" name="mainIframe" src="welcome" class="module-iframe" scrolling="auto" frameborder="0"
                style="width:100%;"></iframe>
    </div>
</body>
</html>