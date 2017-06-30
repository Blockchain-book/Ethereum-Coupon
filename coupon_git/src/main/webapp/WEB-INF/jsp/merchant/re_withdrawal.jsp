<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    %>
    <title>Title</title>
</head>
<body>
<div id="re_withdrawal" class="tab-pane fade" role="tabpanel">
    <h2>结算券提现复审</h2>
    <div id="withdrawal-content">
        <c:forEach items="${requestScope.SWDFList}" var="swdflist">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="pull-left">
                        <p>商户名：<span>${swdflist.merchantName}</span></p>
                        <p>申请结算券金额：<span>${swdflist.operationAmount}</span></p>
                        <p>商户当前银行账户可用余额：<span><%--${swdflist.settlementBalance}--%></span></p>
                    </div>
                    <div class="col-md-4 pull-right">
                        <button id="${swdflist.merchantId}" type="button" onclick="pass_fushen(this)" class="btn btn-success w-45 js-check-success">通过</button>
                        <button id="${swdflist.merchantId}" type="button" onclick="refuse_fushen(this)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>
                        <textarea id="operatorOpinion" name="operatorOpinion" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<script type="text/javascript">
    function pass_fushen(obj) {
        var pass_id = $(obj).prop("id");
        var status = "fushen_pass";
        var url = "<%=basePath %>" + "/bank/bankStaffCheck.action?merchantId="
            + pass_id + "&status=" + status + "&operatorOpinion=" + $('#operatorOpinion').val();
        // alert(url);
        window.location = url;
    }

    function refuse_fushen(obj) {
        var refuse_id = $(obj).prop("id");
        var status = "fushen_refuse";
        var url = "<%=basePath %>" + "/bank/bankStaffCheck.action?merchantId="
            + refuse_id + "&status=" + status + "&operatorOpinion=" + $('#operatorOpinion').val();
        // alert(url);
        window.location = url;
    }
</script>
</body>
</html>
