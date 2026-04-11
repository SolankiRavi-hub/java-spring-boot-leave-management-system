<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login - Leave Management</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f7f6; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 350px; }
        h3 { text-align: center; color: #333; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #666; }
        input[type="email"], input[type="password"] { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        input[type="submit"] { width: 100%; background-color: #007bff; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; font-size: 16px; margin-top: 10px; }
        input[type="submit"]:hover { background-color: #0056b3; }
        .error { color: red; text-align: center; margin-bottom: 10px; }
        .register-link { text-align: center; margin-top: 15px; }
        .register-link a { color: #007bff; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="login-container">
        <h3>Login</h3>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        <form action="<c:url value='/login'/>" method="post">
            <div class="form-group">
                <label>Email ID</label>
                <input type="email" name="email" required="required">
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required="required">
            </div>
            <input type="submit" value="Login">
        </form>
        <div class="register-link">
            Don't have an account? <a href="<c:url value='/register'/>">Register here</a>
        </div>
    </div>
</body>
</html>
