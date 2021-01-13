<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="tilesx" uri="http://tiles.apache.org/tags-tiles-extras" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>  
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Reporting System - ${principal.username}</title>
    <!-- Bootstrap core CSS -->
    <%-- <link href="<c:url value="/resources/static/bootstrap/css/bootstrap.min.css" />" type="text/css" rel="stylesheet"> --%>
<%--    <link href="<c:url value="/resources/static/jquery/jquery-ui.min.css" />" type="text/css" rel="stylesheet">--%>
<%--    <link href="<c:url value="/resources/static/jquery/jquery-ui.theme.min.css" />" type="text/css" rel="stylesheet">--%>
    <link href="<c:url value="/resources/css/salereport.css" />" type="text/css" rel="stylesheet">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.2/css/all.css"
          integrity="sha384-fnmOCqbTlWIlj8LyTjo7mOUStjsKC4pOpQbqyi7RrhN7udi9RwhKkMHpvLbHG9Sr" crossorigin="anonymous">


    <!-- Bootstrap core JavaScript -->
    <script src="<c:url value="/resources/static/jquery/jquery.min.js" />" type="text/javascript"></script>
    <script src="<c:url value="/resources/static/jquery/jquery-ui.min.js" />" type="text/javascript"></script>
    <script src="<c:url value="/resources/static/jquery/notify.js"/>"></script>
<%--    <script src="<c:url value="/resources/static/bootstrap/js/bootstrap.bundle.min.js" />" type="text/javascript"></script>--%>
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
</head>
<body style="margin:0px">
   <header>
   		<ul class="topnav">
<%--            <c:out value="${sessionScope['accessURL']}"></c:out>--%>
            <c:forEach items="${sessionScope['accessURL']}" var="AccessURL" varStatus="vs">
    			 <c:choose>
                    <c:when test="${fn:endsWith(AccessURL.url, '/daily-sale-report/staff')}"><li id="staff-page" ><a href="${AccessURL.url}">Nhân Viên</a></li></c:when>
<%--                    <c:when test="${fn:endsWith(AccessURL.url, '/daily-manager-report/manager')}"> <li id="manager-page" ><a href="${AccessURL.url}">Quản Lý</a></li></c:when>--%>
                     <c:when test="${fn:endsWith(AccessURL.url, '/daily-manager-report/manager')}"> <li id="manager-page" ><a href="${AccessURL.url}">Manager 2</a></li></c:when>
                    <c:when test="${fn:endsWith(AccessURL.url, '/revenue-report/admin')}"><li id="staff-page" ><a href="${AccessURL.url}">Doanh Thu</a></li></c:when>
                    <c:when test="${fn:endsWith(AccessURL.url, '/commission-report/admin')}"><li id="staff-page" ><a href="${AccessURL.url}">Hoa Hồng</a></li></c:when>
<%--                     <c:when test="${fn:endsWith(AccessURL.url, '/daily-director-report/director')}"> <li id="director"><a href="${AccessURLAccessURL.url}">Giám đốc</a></li></c:when>--%>
		        <c:otherwise></c:otherwise>
		    </c:choose>
			</c:forEach>

			<sec:authorize access="hasRole('ROLE_VNDS/ADMIN')">
                   <li id="staff-page" onclick="checkSubPage()"><a href="/revenue-report/admin">Doanh Thu</a></li>
                   <li id="staff-page" onclick="checkSubPage()"><a href="/commission-report/admin">Hoa Hồng</a></li>
             </sec:authorize>
			
		  <li style="float:right"><a href="/logout" onclick="logOut()" id="text-logout">Thoát</a></li>
		</ul>
    </header>
	<tiles:insertAttribute name="body" />
</body>
    <script src="<c:url value="/resources/js/tool.js" />" type="text/javascript"></script>
</html>