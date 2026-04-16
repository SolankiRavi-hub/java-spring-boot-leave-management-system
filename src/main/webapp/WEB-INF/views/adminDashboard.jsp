<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Manager Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f7f6; }
        .container { max-width: 1200px; width: 95%; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        h3 { color: #333; text-align: center; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; table-layout: auto; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; vertical-align: middle; }
        th { text-align: center; }
        .center-col { text-align: center; }
        .id-col { width: 80px; }
        .date-col { width: 120px; }
        .status-col { width: 120px; }
        .action-col { width: 220px; }
        .reason-col { min-width: 360px; white-space: normal; word-break: break-word; }
        .approve-btn { background-color: #2ecc71; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
        .approve-btn:hover { background-color: #27ae60; }
        .reject-btn { background-color: #e74c3c; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
        .reject-btn:hover { background-color: #c0392b; }
        .action-cell { text-align: center; display: flex; flex-wrap: wrap; justify-content: center; gap: 4px; }
        .status-badge { display: inline-block; padding: 4px 8px; border-radius: 12px; font-size: 12px; font-weight: bold; }
        .status-pending { background: #fff3cd; color: #856404; }
        .status-approved { background: #d4edda; color: #155724; }
        .status-rejected { background: #f8d7da; color: #721c24; }
        .top-nav { max-width: 1200px; width: 95%; margin: 0 auto 14px; display: flex; flex-direction: row; justify-content: flex-end; align-items: center; gap: 18px; flex-wrap: nowrap; padding-right: 24px; box-sizing: border-box; }
        .top-nav a { color: #007bff; text-decoration: none; font-weight: bold; white-space: nowrap; }
        .top-nav a:hover { text-decoration: underline; }
        .top-nav a.logout { color: #d32f2f; }
        .form-inline { display: inline-flex; margin: 2px; }
    </style>
</head>
<body>
    <div class="top-nav">
        <a href="<c:url value='/applyLeave'/>">Apply Leave</a>
        <a href="<c:url value='/viewHistory'/>">My History</a>
        <a href="<c:url value='/profile'/>">Profile</a>
        <a href="<c:url value='/viewRequests'/>">Manager Dashboard</a>
        <a href="<c:url value='/logout'/>" class="logout">Logout</a>
    </div>

    <div class="container">
        <h3>All Leave Requests (Manager)</h3>

        <div class="table-wrap">
        <table>
            <tr>
                <th class="center-col id-col">Req ID</th>
                <th class="center-col id-col">Emp ID</th>
                <th class="date-col">Start Date</th>
                <th class="date-col">End Date</th>
                <th class="reason-col">Reason</th>
                <th class="status-col">Status</th>
                <th class="center-col action-col">Action</th>
            </tr>
            <c:choose>
                <c:when test="${empty requests}">
                    <tr>
                        <td colspan="7" style="text-align:center;">No leave requests found.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="req" items="${requests}">
                        <tr>
                            <td class="center-col id-col">${req.id}</td>
                            <td class="center-col id-col">${req.employeeId}</td>
                            <td class="date-col">${req.startDate}</td>
                            <td class="date-col">${req.endDate}</td>
                            <td class="reason-col">${req.reason}</td>
                            <td class="status-col">
                                <span class="status-badge status-${req.status.toLowerCase()}">${req.status}</span>
                            </td>
                            <td class="action-cell action-col">
                                <c:if test="${req.status == 'Pending'}">
                                    <form class="form-inline" method="post" action="<c:url value='/updateStatus'/>">
                                        <input type="hidden" name="id" value="${req.id}" />
                                        <input type="hidden" name="status" value="Approved" />
                                        <input type="submit" value="Approve" class="approve-btn" onclick="return confirm('Approve this request?');"/>
                                    </form>
                                    <form class="form-inline" method="post" action="<c:url value='/updateStatus'/>">
                                        <input type="hidden" name="id" value="${req.id}" />
                                        <input type="hidden" name="status" value="Rejected" />
                                        <input type="submit" value="Reject" class="reject-btn" onclick="return confirm('Reject this request?');"/>
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
