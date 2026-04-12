<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Edit Leave</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f7f6; }
        .container { max-width: 900px; width: 95%; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        h3 { color: #333; text-align: center; border-bottom: 2px solid #f39c12; padding-bottom: 10px; }
        table { width: 100%; border-spacing: 0 14px; }
        td { padding: 4px; }
        label { font-weight: bold; color: #555; }
        input[type="date"], textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        input[type="submit"] { width: 100%; background-color: #f39c12; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; font-size: 16px; }
        input[type="submit"]:hover { background-color: #d68910; }
        .error { color: #e74c3c; text-align: center; margin-bottom: 10px; }
        .top-nav { max-width: 900px; width: 95%; margin: 0 auto 14px; display: flex; flex-direction: row; justify-content: flex-end; align-items: center; gap: 18px; padding-right: 24px; box-sizing: border-box; }
        .top-nav a { color: #007bff; text-decoration: none; font-weight: bold; white-space: nowrap; }
        .top-nav a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="top-nav">
    <a href="<c:url value='/viewHistory'/>">Back to History</a>
</div>

<div class="container">
    <h3>Edit Pending Leave</h3>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form:form method="post" action="editLeave" modelAttribute="leaveRequest">
        <form:hidden path="id"/>
        <table>
            <tr>
                <td><label>Start Date:</label></td>
                <td><form:input path="startDate" type="date" required="required"/></td>
            </tr>
            <tr>
                <td><label>End Date:</label></td>
                <td><form:input path="endDate" type="date" required="required"/></td>
            </tr>
            <tr>
                <td><label>Reason:</label></td>
                <td><form:textarea path="reason" rows="4" required="required" style="resize: vertical;"/></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Update Leave"/></td>
            </tr>
        </table>
    </form:form>
</div>
</body>
</html>
