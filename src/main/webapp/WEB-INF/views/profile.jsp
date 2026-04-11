<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>My Profile</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f7f6; }
        .container { max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        h3 { color: #333; text-align: center; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 20px; }
        .profile-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }
        .profile-row:last-child { border-bottom: none; }
        .label { font-weight: bold; color: #555; }
        .value { color: #333; }
        .top-nav { max-width: 500px; width: 95%; margin: 0 auto 14px; display: flex; flex-direction: row; justify-content: flex-end; align-items: center; gap: 18px; flex-wrap: nowrap; padding-right: 24px; box-sizing: border-box; }
        .top-nav a { color: #007bff; text-decoration: none; font-weight: bold; white-space: nowrap; }
        .top-nav a:hover { text-decoration: underline; }
        .top-nav a.logout { color: #d32f2f; }
    </style>
</head>
<body>
    <div class="top-nav">
        <a href="<c:url value='/applyLeave'/>">Apply Leave</a>
        <a href="<c:url value='/viewHistory'/>">My History</a>
        <a href="<c:url value='/profile'/>">Profile</a>
        <c:if test="${sessionScope.loggedInUser.role == 'Admin'}">
            <a href="<c:url value='/viewRequests'/>">Manager Dashboard</a>
        </c:if>
        <a href="<c:url value='/logout'/>" class="logout">Logout</a>
    </div>

    <div class="container">
        <h3>My Profile</h3>
        <div class="profile-row">
            <span class="label">User ID:</span>
            <span class="value">${employee.id}</span>
        </div>
        <div class="profile-row">
            <span class="label">Full Name:</span>
            <span class="value">${employee.name}</span>
        </div>
        <div class="profile-row">
            <span class="label">Email Address:</span>
            <span class="value">${employee.email}</span>
        </div>
        <div class="profile-row">
            <span class="label">Department:</span>
            <span class="value">${employee.department}</span>
        </div>
        <div class="profile-row">
            <span class="label">System Role:</span>
            <span class="value">${employee.role}</span>
        </div>
    </div>
</body>
</html>
