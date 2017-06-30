<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
    <title>Title</title>
    <script src="<%=basePath%>resources/js/consumers.js"></script>
</head>
<body>

<div id="consumers" class="tab-pane fade in active" role="tabpanel">
    <h2>消费者查询</h2>
    <form class="form-horizontal" role="form">
        <div class="form-group">
            <label for="consumer-name" class="col-md-1 control-label">用户名</label>
            <div class="col-md-6">
                <input type="text" id="consumer-name" class="form-control" placeholder="请输入用户名">
            </div>
            <div class="col-md-2">
                <button type="button" class="btn btn-info">搜索</button>
            </div>
        </div>
    </form>
    <div role="content" id="consumer_list">
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="panel-body">
                <c:forEach items="${requestScope.allConsumerList}" var="consumer">
                <div class="clo-md-4 pull-left">
                    <p>注册手机号：
                        <span id="reg_phone">${consumer.account}</span>
                    </p>
                    <p>状态：
                        <span id="consumer_state">translateStatus(${consumer.isFrozen})</span>
                    </p>
                </div>
                <div class="clo-md-1 pull-left">
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
                </div>
                <div class="clo-md-3 pull-left">
                    <p>持有优惠券：<span id="own_coupon">${consumer.totalOwn} 元</span></p>
                    <p>消费记录：<span id="spent_coupon">${consumer.totalConsume} 元</span></p>
                    <p>转赠优惠券：<span id="send_coupon">${consumer.totalSendOut} 元</span></p>
                </div>
                <c:if test="${consumer.mark} ==\"1\"">
                    <div class="col-md-4 pull-right">
                        <button type="button" class="btn btn-default pull-right w-45" onclick="makAbnormal(this)" id="${consumer.consumerId}">标为异常用户</button>
                    </div>
                </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
    </div>
    <nav>
        <ul class="pagination">
            <li><a href="#">&laquo;</a></li>
            <li class="active"><a href="#">1</a></li>
            <li><a href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li><a href="#">4</a></li>
            <li><a href="#">5</a></li>
            <li><a href="#">&raquo;</a></li>
        </ul>
    </nav>
</div>
</body>
</html>
