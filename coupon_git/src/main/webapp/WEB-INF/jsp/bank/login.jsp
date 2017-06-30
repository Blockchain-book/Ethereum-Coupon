<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    %>
    <meta charset="UTF-8">
    <title>银行员工注册</title>
    <link href="http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=basePath%>resources/css/main.css">
    <script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.js"></script>
    <script src="http://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.js"></script>
    <script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>
    <script src="https://static.runoob.com/assets/jquery-validation-1.14.0/lib/jquery.js"></script>
    <script src="https://static.runoob.com/assets/jquery-validation-1.14.0/dist/jquery.validate.min.js"></script>
    <script src="https://static.runoob.com/assets/jquery-validation-1.14.0/dist/localization/messages_zh.js"></script>
    <script>
        var SIGN_MODE = "1";
    </script>
    <script language="JavaScript">
        //回车时，默认是登陆
        function on_return(){
            if(window.event.keyCode == 13){
                if (document.all('btn-signin')!=null){
                    document.all('btn-signin').click();
                }
            }
        }
    </script>
</head>
<body onkeydown="on_return();">
<div class="container">
    <div class="well" style="max-width:400px; margin: 150px auto;">
        <form id="login-box" method="post">
            <div align="center"><label id="eroor_label" style="text-align:center; color: #6658ff;font-size: medium;">${error_code}</label></div>
            <div class="form-group">
                <input type="text" class="form-control" id="username" name="username" placeholder="请输入用户名" required minlength="2" />
            </div>
            <div class="form-group">
                <input type="password" class="form-control" id="password" name="password" placeholder="请输入密码" required minlength="6">
            </div>
            <div id="signin-box">
                <button id="btn-signin" onClick='check()' type="button" class="btn btn-primary btn-block">登陆</button>
                <a href="javascript:void(0);" id="check-to-signup"><small>没有帐号？点击注册</small></a>
            </div>
            <div id="signup-box" style="display:none;">
                <div class="form-group">
                    <input type="password" class="form-control" id="repassword" name="repassword" placeholder="再次输入密码" required minlength="6" equalTo="#password">
                </div>
                <div class="form-group">
                    <label for="position1" class="radio-inline">
                        <input type="radio" name="positionOptions" id="position1" value="1">操作员
                    </label>
                    <label for="position2" class="radio-inline">
                        <input type="radio" name="positionOptions" id="position2" value="2">复审员
                    </label>
                </div>
                <button id="btn-signup" type="button" class="btn btn-primary btn-block">注册</button>
                <a href="javascript:void(0);" id="check-to-signin"><small>已有帐号？点击登录</small></a>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    //表单验证
    $("#login-box").validate();
    // 登陆注册切换
    $('#check-to-signin').click(function () {
        $('#signup-box').hide();
        $('#signin-box').show();
        SIGN_MODE = 2;
    });
    $('#check-to-signup').click(function () {
        $('#signin-box').hide();
        $('#signup-box').show();
        SIGN_MODE = 1;
    });
    // 登录
    $('#btn-signin').click(function () {
        $("#login-box").attr("action", "<%=basePath%>bank/login.action");
        $("#login-box").submit();
    });
    // 注册
    $('#btn-signup').click(function () {
        $("#login-box").attr("action", "<%=basePath%>bank/register.action");
        $("#login-box").submit();
    });
</script>
</body>
</html>
