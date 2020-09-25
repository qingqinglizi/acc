<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <%--<base href="<%=basePath%>">--%>
    <title>View Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">

</head>
<body>
    This is view data page. <br>
    ${pageContext.request.contextPath}<br>
    <form id="loginForm" name="loginForm" action="" method="POST">
        用户名：<input name="loginId"><br>
        密码：<input name="password"><br>
        <input type="submit" value="登录">
    </form>
</body>
<script>
    var suffix = document.location.search;
    console.info(suffix);
    document.getElementById("loginForm").setAttribute("action", "${pageContext.request.contextPath}/certification/checkLoginResult" + suffix);
</script>
</html>
