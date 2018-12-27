<%--
  Created by IntelliJ IDEA.
  User: 12345
  Date: 2018/12/20
  Time: 11:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>主页</title>
</head>
<body>
<%response.sendRedirect(request.getContextPath()+"/product?method=index");%>
</body>
</html>
