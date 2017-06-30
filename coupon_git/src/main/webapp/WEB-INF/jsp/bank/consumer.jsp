<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    %>
    <base href="<%=basePath%>" >
    <meta charset="UTF-8">
    <title>区块链优惠券系统管理后台</title>
    <link href="//cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=basePath%>resources/css/main.css">
    <script src="//cdn.bootcss.com/jquery/3.1.1/jquery.js"></script>
    <script src="//cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.js"></script>
    <script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>
    <script src="<%=basePath%>resources/js/consumers.js"></script>
    <script language="javascript" type="text/javascript">
        function logout(){
                $.ajax({
                    url:"bank/logout.action",
                    dataType:"json",
                    type:"POST",
                    async:false,
                    success:function(data){
                        if(data.resultCode == 1){
                            top.location = "<%=basePath%>main/login.action";
                        }
                    }
                });
        }
    </script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-3"></div>
        <div class="page-header col-md-6">
            <h1 class="text-center">电子优惠券后台管理系统</h1>
        </div>
        <div class="col-md-3" style="text-align: right; padding-top: 90px;">
            欢迎您，银行员工 &nbsp; <strong>${sessionScope.currenBankStaff.account}</strong> &nbsp;&nbsp;
           <!-- <a href="">退出登录</a>
            <a id=HyperLink3 onclick="logout()" href="javascript:window.opener=null;%20window.close();">退出系统</a>-->
            <a data-toggle="modal" data-target="#myModal_logout">退出登录</a>
        </div>
        <div class="modal fade" id="myModal_logout" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title" id="myModalLabel_logout">
                            系统提示
                        </h4>
                    </div>
                    <div class="modal-body">
                        是否退出登录？
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消
                        </button>
                        <button type="button" onclick="logout()" class="btn btn-primary" data-dismiss="modal" id="confirm_logout">
                            确定
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-3">
            <ul class="nav nav-pills nav-stacked" role="tablist">
                <c:if test="${sessionScope.currenBankStaff.position eq '1'}">
                    <li role="presentation"><a href="<%=path%>/main/toFirstTrial.action">商户中心</a></li>
                </c:if>
                <c:if test="${sessionScope.currenBankStaff.position eq '2'}">
                    <li role="presentation"><a href="<%=path%>/main/toReCheck.action">商户中心</a></li>
                </c:if>
                <li role="presentation" class="active"><a href="<%=path%>/main/toConsumerCenter.action">消费者中心</a></li>
                <li role="presentation"><a href="<%=path%>/main/toCouponCenter.action">优惠券中心</a></li>
            </ul>
        </div>
        <div class="col-md-9">
            <ul id="sub-tab-title" class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active" onclick="re_load_normal()"><a href="#consumers" role="tab" data-toggle="tab">消费者查询</a></li>
                <li role="presentation" onclick="re_load_abnormal()"><a href="#abnormalities" role="tab" data-toggle="tab">异常用户处理</a></li>
                <li role="presentation" onclick="load_frozen_users()"><a href="#frozenusers" role="tab" data-toggle="tab">已冻结用户列表</a></li>
            </ul>
            <div class="tab-content">
                <div id="consumers" class="tab-pane fade in active" role="tabpanel">
                    <h2>消费者查询</h2>
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="consumer-name2" class="col-md-1 control-label">用户名</label>
                            <div class="col-md-6">
                                <input type="text" id="consumer-name2" class="form-control" placeholder="请输入用户名">
                            </div>
                            <div class="col-md-2">
                                <button type="button" name="1" class="btn btn-info" onclick="search(this)">搜索</button>
                            </div>
                        </div>
                    </form>

                        <div id="consumer_list">

                        </div>
                    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                        &times;
                                    </button>
                                    <h4 class="modal-title" id="myModalLabel">
                                        系统提示
                                    </h4>
                                </div>
                                <div class="modal-body">
                                    是否确认将该消费者标记为异常
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消
                                    </button>
                                    <button type="button" class="btn btn-primary" id="confirmMark" data-dismiss="modal">确定
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nav>
                        <ul class="pagination" id="pagination-normal">

                        </ul>
                    </nav>
                </div>
                <div id="abnormalities" class="tab-pane fade" role="tabpanel">
                    <h2>异常用户处理</h2>
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="consumer-name" class="col-md-1 control-label">用户名</label>
                            <div class="col-md-6">
                                <input type="text" id="consumer-name" class="form-control" placeholder="请输入用户名">
                            </div>
                            <div class="col-md-2">
                                <button type="button" name="2" onclick="search(this)" class="btn btn-info">搜索</button>
                            </div>
                        </div>
                    </form>
                    <div id="abnormal_list">
                    </div>
                    <div class="modal fade" id="myModal_ab" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                        &times;
                                    </button>
                                    <h4 class="modal-title" id="myModalLabel_ab">
                                        系统提示
                                    </h4>
                                </div>
                                <div class="modal-body" id="modal-text">

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消
                                    </button>
                                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="confirm_op">
                                        确定
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nav>
                        <ul class="pagination" id="pagination-abnormal">

                        </ul>
                    </nav>
                </div>
                <div id="frozenusers" class="tab-pane fade" role="tabpanel">
                    <h2>已冻结用户列表</h2>
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="consumer-name1" class="col-md-1 control-label">用户名</label>
                            <div class="col-md-6">
                                <input type="text" id="consumer-name1" class="form-control" placeholder="请输入用户名">
                            </div>
                            <div class="col-md-2">
                                <button type="button" name="3" onclick="search(this)" class="btn btn-info">搜索</button>
                            </div>
                        </div>
                    </form>
                    <div id="frozen_consumer_list">

                    </div>
                    <div class="modal fade" id="myModal_thaw" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                        &times;
                                    </button>
                                    <h4 class="modal-title" id="myModalLabel_thaw">
                                        系统提示
                                    </h4>
                                </div>
                                <div class="modal-body">
                                    是否解冻该消费者
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消
                                    </button>
                                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="confirm_thaw">
                                        确定
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nav>
                        <ul class="pagination" id="pagination-frozen">

                        </ul>
                    </nav>
                </div>
            </div>
                <%--<jsp:include page="../consumer/consumers.jsp" />--%>
                <%--<jsp:include page="../consumer/abnormalities.jsp" />--%>
                <%--<jsp:include page="../consumer/frozenusers.jsp" />--%>
            </div>
        </div>
    </div>
</div>

</body>
</html>
