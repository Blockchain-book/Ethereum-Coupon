<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    %>
    <title>Title</title>
    <script src="<%=basePath%>resources/js/merchantAudit.js"></script>
</head>
<body>
<div id="audit" class="tab-pane fade in active" role="tabpanel">
    <h2>商户审批</h2>
    <div id="audit-content">
        <c:forEach items="${requestScope.uncheckedMerchantList}" var="uncheckMerchant">
            <div class="panel panel-default">
                <div class="panel-heading">${uncheckMerchant.name}</div>
                <div class="panel-body">
                    <div class="col-md-4">
                        <p><b>商户注册信息：</b></p>
                        <p hidden><span id="merchant_id">${uncheckMerchant.id}</span></p>
                        <p>注册手机号：<span>${uncheckMerchant.account}</span></p>
                        <p>营业执照号：<span>${uncheckMerchant.licence}</span></p>
                        <p>注册地址：<span>${uncheckMerchant.address}</span></p>
                        <p>经营范围：<span>${uncheckMerchant.businessScope}</span></p>
                        <p>法人姓名：<span>${uncheckMerchant.legalEntityName}</span></p>
                    </div>
                    <div class="pull-right col-md-4">
                        <textarea id="txarea" style="padding-top: 20px; padding-bottom: 20px; margin-bottom: 20px;" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>
                        <button id="${uncheckMerchant.id}" typee="button" class="btn btn-success w-45" onclick="pass_check(this)">通过</button>
                        <button id="${uncheckMerchant.id}" type="button" class="btn btn-danger pull-right w-45" onclick="refuse_check(this)" >拒绝</button>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<script type="text/javascript">

    function pass_check(obj){
        var pass_id = $(obj).prop("id");
        var status = "1";
        var url = "<%=basePath %>" + "/bank/merchantPass.action?merchantId="
            + pass_id + "&status=" + status + "&operatorOpinion=" + $('#txarea').val();
        // alert(url);
        window.location = url;
    }

    function refuse_check(obj) {
        var refuse_id = $(obj).prop("id");
        var status = "2";
        var url = "<%=basePath %>" + "/bank/merchantPass.action?merchantId="
            + refuse_id + "&status=" + status + "&operatorOpinion=" + $('#txarea').val();
        // alert(url);
        window.location = url;
    }

</script>
</body>
</html>
