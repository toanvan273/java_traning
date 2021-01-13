<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="context" value="/daily-sale-report" scope="request" />
<tiles:insertAttribute name="layout.basic">
	<link href="https://fonts.googleapis.com/css?family=Montserrat:200,400,700" rel="stylesheet">
	<style>
		* {
			-webkit-box-sizing: border-box;
			box-sizing: border-box;
		}

		body {
			padding: 0;
			margin: 0;
		}

		#notfound {
			position: relative;
			height: 100vh;
		}

		#notfound .notfound {
			position: absolute;
			left: 50%;
			top: 50%;
			-webkit-transform: translate(-50%, -50%);
			-ms-transform: translate(-50%, -50%);
			transform: translate(-50%, -50%);
		}

		.notfound {
			max-width: 520px;
			width: 100%;
			line-height: 1.4;
			text-align: center;
		}

		.notfound .notfound-403 {
			position: relative;
			height: 200px;
			margin: 0px auto 20px;
			z-index: -1;
		}

		.notfound .notfound-403 h1 {
			font-family: 'Montserrat', sans-serif;
			font-size: 236px;
			font-weight: 200;
			margin: 0px;
			color: #211b19;
			text-transform: uppercase;
			position: absolute;
			left: 50%;
			top: 50%;
			-webkit-transform: translate(-50%, -50%);
			-ms-transform: translate(-50%, -50%);
			transform: translate(-50%, -50%);
		}

		.notfound .notfound-403 h2 {
			width: max-content;
			font-family: 'Montserrat', sans-serif;
			font-size: 28px;
			font-weight: 400;
			text-transform: uppercase;
			color: #211b19;
			background: #fff;
			padding: 10px 5px;
			margin-bottom: -50px;
			display: inline-block;
			position: absolute;
			bottom: 0px;
			left: 0;
			right: 0;
		}

		.notfound a {
			margin-top:50px;
			font-family: 'Montserrat', sans-serif;
			display: inline-block;
			font-weight: 700;
			text-decoration: none;
			color: #fff;
			text-transform: uppercase;
			padding: 13px 23px;
			background: #ff6300;
			font-size: 18px;
			-webkit-transition: 0.2s all;
			transition: 0.2s all;
		}

		.notfound a:hover {
			color: #ff6300;
			background: #211b19;
		}

		@media only screen and (max-width: 767px) {
			.notfound .notfound-403 h1 {
				font-size: 148px;
			}
		}

		@media only screen and (max-width: 480px) {
			.notfound .notfound-403 {
				height: 148px;
				margin: 0px auto 10px;
			}
			.notfound .notfound-403 h1 {
				font-size: 86px;
			}
			.notfound .notfound-403 h2 {
				font-size: 16px;
			}
			.notfound a {
				padding: 7px 15px;
				font-size: 14px;
			}
		}

	</style>
	<title>Bạn không có quyền truy cập !</title>
	<div id="notfound">
		<div class="notfound">
			<div class="notfound-403">
				<h1>Oops!</h1>
				<h2>Bạn không có quyền truy cập !</h2>
			</div>
			<a href="/logout">Trở lại Đăng Nhập</a>
		</div>
	</div>
</tiles:insertAttribute>