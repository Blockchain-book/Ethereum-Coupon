<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    %>
    <meta charset="UTF-8">
    <title>区块链优惠券系统管理后台</title>
    <link href="//cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=basePath%>resources/css/main.css">
    <script src="//cdn.bootcss.com/jquery/3.1.1/jquery.js"></script>
    <script src="//cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.js"></script>
    <script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>
    <script src="<%=basePath%>resources/js/coupon.js"></script>
  <!--  <script language="javascript" type="text/javascript">
        function logout(){
            if (confirm("您确定要退出登录吗？"))
                top.location = "login.action";
            return false;
        }
    </script>-->
    <script language="javascript" type="text/javascript">
        function logout(){
            $.ajax({
                url:"<%=basePath%>bank/logout.action",
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
                <li role="presentation"><a href="<%=path%>/main/toConsumerCenter.action">消费者中心</a></li>
                <li role="presentation" class="active"><a href="<%=path%>/main/toCouponCenter.action">优惠券中心</a></li>
            </ul>
        </div>
        <div class="col-md-9">
            <ul id="sub-tab-title" class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active" onclick="re_load_abnormal()"><a href="#coupon" role="tab" data-toggle="tab">优惠券查询</a></li>
                <li role="presentation"><a href="#analysis" role="tab" data-toggle="tab">统计分析</a></li>
                <li role="presentation"><a href="#monitor" role="tab" data-toggle="tab">平台监控</a></li>
            </ul>
            <div class="tab-content">
                <div id="coupon" class="tab-pane fade in active" role="tabpanel">
                    <h2>优惠券查询</h2>
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="consumer-name" class="col-md-2 control-label">商户名：</label>
                            <div class="col-md-8">
                                <input type="text" id="consumer-name" class="form-control" placeholder="请输入商户名">
                            </div>
                            <div class="col-md-2">
                                <button type="button" class="btn btn-info" onclick="search(this)">搜索</button>
                            </div>
                        </div>
                    </form>

                    <div id="coupon_list"></div>

                    <nav>
                        <ul class="pagination" id="pagination-coupon"></ul>
                    </nav>
                </div>
                <div id="analysis" class="tab-pane fade" role="tabpanel">
                    <h4>该功能尚未上线</h4>
                </div>
                <div id="monitor" class="tab-pane fade" role="tabpanel">
                    <h4>该功能尚未上线</h4>
                </div>
                <%--<jsp:include page="../coupon/coupon.jsp" />--%>
                <%--<jsp:include page="../coupon/analysis.jsp" />--%>
                <%--<jsp:include page="../coupon/monitor.jsp" />--%>
            </div>
        </div>
    </div>
</div>
</body>
</html>
