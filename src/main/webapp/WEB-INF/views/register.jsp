<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Register - Leave Management</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f7f6; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 400px; }
        h3 { text-align: center; color: #333; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #666; }
        input[type="text"], input[type="email"], input[type="password"] { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        input[type="submit"] { width: 100%; background-color: #28a745; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; font-size: 16px; margin-top: 10px; }
        input[type="submit"]:hover { background-color: #218838; }
        .error { color: red; text-align: center; margin-bottom: 10px; }
        .login-link { text-align: center; margin-top: 15px; }
        .login-link a { color: #007bff; text-decoration: none; }
        .login-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="login-container">
        <h3>Register New Account</h3>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        <form:form action="register" method="post" modelAttribute="employee">
            <div class="form-group">
                <label>Full Name</label>
                <form:input path="name" required="required" type="text" />
            </div>
            <div class="form-group">
                <label>Email ID</label>
                <form:input path="email" required="required" type="email" />
            </div>
            <div class="form-group">
                <label>Password</label>
                <form:input path="password" required="required" type="password" />
            </div>
            <div class="form-group">
                <label>Department</label>
                <form:input path="department" required="required" type="text" />
            </div>
            <input type="submit" value="Register">
        </form:form>
        <div class="login-link">
            Already have an account? <a href="<c:url value='/login'/>">Login here</a>
        </div>
    </div>
</body>
</html>
