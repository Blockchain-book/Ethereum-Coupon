<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    %>
    <title>Title</title>
    <script src="<%=basePath%>resources/js/coupon.js"></script>
</head>
<body>
<%--<div id="coupon" class="tab-pane fade in active" role="tabpanel">--%>
    <%--<h2>优惠券查询</h2>--%>
    <%--<form class="form-horizontal" role="form">--%>
        <%--<div class="form-group">--%>
            <%--<label for="consumer-name" class="col-md-2 control-label">优惠券id</label>--%>
            <%--<div class="col-md-8">--%>
                <%--<input type="text" id="consumer-name" class="form-control" placeholder="请输入优惠券id">--%>
            <%--</div>--%>
            <%--<div class="col-md-2">--%>
                <%--<button type="button" class="btn btn-info">搜索</button>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</form>--%>

    <%--<div class="panel panel-default" id="coupon_list">--%>
        <%--<div class="panel-body">--%>
            <%--&lt;%&ndash;<div class="pull-left col-md-8">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<p>优惠券id：<span>b1944d0a-5117-4d0e-ab63-9ac55ed845d0</span></p>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<p>优惠券持有者：<span>消费者2</span></p>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<p>优惠券状态：<span>已领取未使用</span></p>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<p>优惠券发行期：<span>2017年03月14 -- 2017年05月13日</span></p>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="col-md-4 pull-right">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<span style="font-size: 80px;color: #ff0000;">10</span>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<span style="font-size: 20px;">元</span>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<nav>--%>
        <%--<ul class="pagination">--%>
            <%--<li><a href="#">&laquo;</a></li>--%>
            <%--<li class="active"><a href="#">1</a></li>--%>
            <%--<li><a href="#">2</a></li>--%>
            <%--<li><a href="#">3</a></li>--%>
            <%--<li><a href="#">4</a></li>--%>
            <%--<li><a href="#">5</a></li>--%>
            <%--<li><a href="#">&raquo;</a></li>--%>
        <%--</ul>--%>
    <%--</nav>--%>
<%--</div>--%>
</body>
</html>
