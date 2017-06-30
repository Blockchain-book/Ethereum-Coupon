<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <script src="<%=basePath%>resources/js/merchantAudit.js"></script>
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
<script src="<%=basePath%>resources/js/recheck.js"></script>
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
                <li role="presentation" class="active"><a href="<%=path%>/main/toReCheck.action">商户中心</a></li>
                <li role="presentation"><a href="<%=path%>/main/toConsumerCenter.action">消费者中心</a></li>
                <li role="presentation"><a href="<%=path%>/main/toCouponCenter.action">优惠券中心</a></li>
            </ul>
        </div>
        <div class="col-md-9">
            <ul id="sub-tab-title" class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active" onclick="re_load()"><a href="#audit" role="tab" data-toggle="tab">商户审批</a></li>
                <%--<li role="presentation"><a href="#authorize" role="tab" data-toggle="tab">商户授权</a></li>--%>
                <li role="presentation" onclick="queryReCheckList()"><a href="#re_distribution" role="tab" data-toggle="tab">结算券申请复审</a></li>
                <li role="presentation" onclick="queryReWithdrawCheckList()"><a href="#re_withdrawal" role="tab" data-toggle="tab">结算券提现复审</a></li>
            </ul>
            <div class="tab-content">
                <div id="audit" class="tab-pane fade in active" role="tabpanel">
                    <h2>商户审批</h2>
                    <div id="merchants_list">

                    </div>
                    <div class="modal fade" id="myModal_audit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel_audit" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    <h4 class="modal-title" id="myModalLabel_audit">系统提示</h4>
                                </div>
                                <div class="modal-body" id="audit_text">

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                    <button type="button" class="btn btn-primary" id="confirmAudit" data-dismiss="modal">确定</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nav>
                        <ul class="pagination" id="pagination-merchants">

                        </ul>
                    </nav>
                </div>


                <div id="re_distribution" class="tab-pane fade" role="tabpanel">
                    <h2>结算券申请复审</h2>
                    <div id="distribution-content">

                    </div>
                    <div class="modal fade" id="myModal_re_distribution" tabindex="-1" role="dialog" aria-labelledby="myModalLabel_re_distribution" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    <h4 class="modal-title" id="myModalLabel_re_distribution">系统提示</h4>
                                </div>
                                <div class="modal-body" id="re_distribution_text">

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                    <button type="button" class="btn btn-primary" id="confirmReDistribution" data-dismiss="modal">确定</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nav>
                        <ul class="pagination" id="pagination-reApplyCheck">

                        </ul>
                    </nav>
                </div>


                <div id="re_withdrawal" class="tab-pane fade" role="tabpanel">
                    <h2>结算券提现复审</h2>
                    <div id="withdrawal-content">

                    </div>
                    <div class="modal fade" id="myModal_re_withdrawal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel_re_withdrawal" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    <h4 class="modal-title" id="myModalLabel_re_withdrawal">系统提示</h4>
                                </div>
                                <div class="modal-body" id="re_withdrawal_text">

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                    <button type="button" class="btn btn-primary" id="confirmReWithdrawal" data-dismiss="modal">确定</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <nav>
                        <ul class="pagination" id="pagination-reWithDrawCheck">

                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">

</script>
</body>
</html>
