<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div id="monitor" class="tab-pane fade" role="tabpanel">
    <h2>平台监控</h2>
    <form class="form-horizontal" role="form">
        <div class="form-group">
            <label for="consumer-name" class="col-md-1 control-label">优惠券id</label>
            <div class="col-md-6">
                <input type="text" id="consumer-name" class="form-control" placeholder="请输入用户名">
            </div>
            <div class="col-md-2">
                <button type="button" class="btn btn-info">搜索</button>
            </div>
        </div>
    </form>
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="pull-left">
                <p>优惠券id：<span>消费者3</span></p>
                <p>状态：<span>已冻结</span></p>
            </div>
            <div class="col-md-4 pull-right">
                <button type="button" class="btn btn-success pull-right w-45">解冻</button>
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
