<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<tiles:insertAttribute name="layout.basic">
<div class="row" id="body-row">
	<div class="col p-4">
		<h5>Truy cập bị từ chối</h5>
		<br/>
		<br/>
		<p style="color: red">Xin lỗi, bạn không có quyền truy cập vào trang này.</p> 
		Hãy thoát ra và thử đăng nhập lại <a href="<c:url value="/logout" /> ">tại đây</a>
	</div>
</div>
</tiles:insertAttribute>
