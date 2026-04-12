<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Leave History</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f7f6; }
        .container { max-width: 1100px; width: 95%; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        h3 { color: #333; text-align: center; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { text-align: center; background-color: #007bff; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .status-pending { color: #f39c12; font-weight: bold; }
        .status-approved { color: #2ecc71; font-weight: bold; }
        .status-rejected { color: #e74c3c; font-weight: bold; }
        .top-nav { max-width: 1100px; width: 95%; margin: 0 auto 14px; display: flex; flex-direction: row; justify-content: flex-end; align-items: center; gap: 18px; flex-wrap: nowrap; padding-right: 24px; box-sizing: border-box; }
        .top-nav a { color: #007bff; text-decoration: none; font-weight: bold; white-space: nowrap; }
        .top-nav a:hover { text-decoration: underline; }
        .top-nav a.logout { color: #d32f2f; }
        .action-cell { text-align: center; display: flex; justify-content: center; align-items: center; gap: 6px; flex-wrap: wrap; }
        .form-inline { display: inline-flex; margin: 0; }
        .cancel-btn { background-color: #e74c3c; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; min-width: 78px; }
        .cancel-btn:hover { background-color: #c0392b; }
        .edit-btn { background-color: #f39c12; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; min-width: 78px; }
        .edit-btn:hover { background-color: #d68910; }
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
        <h3>My Leave History</h3>

        <div class="table-wrap">
        <table>
            <tr>
                <th>Req ID</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Reason</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <c:choose>
                <c:when test="${empty history}">
                    <tr>
                        <td colspan="6" style="text-align:center;">No leave requests found.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="req" items="${history}">
                        <tr>
                            <td>${req.id}</td>
                            <td>${req.startDate}</td>
                            <td>${req.endDate}</td>
                            <td>${req.reason}</td>
                            <td>
                                <span class="status-${req.status.toLowerCase()}">${req.status}</span>
                            </td>
                            <td class="action-cell">
                                <c:if test="${req.status == 'Pending'}">
                                    <form class="form-inline" method="get" action="<c:url value='/editLeave'/>">
                                        <input type="hidden" name="id" value="${req.id}" />
                                        <input type="submit" value="Edit" class="edit-btn"/>
                                    </form>
                                    <form class="form-inline" method="post" action="<c:url value='/cancelLeave'/>">
                                        <input type="hidden" name="id" value="${req.id}" />
                                        <input type="submit" value="Cancel" class="cancel-btn" onclick="return confirm('Are you sure you want to cancel this request?');"/>
                                    </form>
                                </c:if>
                                <c:if test="${req.status != 'Pending'}">
                                    N/A
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </table>
        </div>
    </div>
</body>
</html>
