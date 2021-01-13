<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url var="context" value="/revenue-report" scope="request"/>
<tiles:insertAttribute name="layout.basic">
    <div id="main-content">
        <div class="days">
            <form id="formDataInput">
                <div style="">
                    <span class="text">Kỳ báo cáo </span>
                    <label style="margin-left: 20px;" class="text">Từ ngày</label>
                    <input class="input-day custom-input-data" type="text" name="first-day" id="first-day" placeholder="dd/mm/yy" readonly>
                    <label style="margin-left: 20px;" class="text">Đến ngày</label>
                    <input class="input-day custom-input-data" type="text" name="last-day" id="last-day" placeholder="dd/mm/yy" readonly>
                    <label class="text" style="margin-left: 60px; font-size: 24px" id="text-items">Báo cáo doanh thu tài khoản careby theo môi giới</label>
<%--                    <label class="text" style="margin-left: 60px; display:none;" id="text-items">Sản phẩm</label>--%>
                    <input type="text" onfocus="publicMenu()" id="item" class="custom-input-data" placeholder="Tất cả" style="display:none;" readonly>
                    <span id="error-noti">Chọn sản phẩm để tiếp tục</span>
                    <div style="display:none"  id="menuProduct">
                        <input type="checkbox" onclick="selectAll();check()" class="checkboxes checkflag" id="all" value = "all" checked>
                        <label for="all" class="checkboxes content-items">Tất cả</label><br>
                        <input type="checkbox" onclick="check()" class="checkboxes checkflag" id="stockFlag" value ="D0" checked>
                        <label for="stockFlag" class="checkboxes content-items">D0</label><br>
                        <input type="checkbox" onclick="check()" class="checkboxes checkflag" id="marginStockFlag" value ="D1" checked>
                        <label for="marginStockFlag" class="checkboxes content-items">D1</label><br>
                    </div>
                    <div class="wrap-button">
                            <span class="wrap-button-search">
                                <i class="fa fa-search style-icon"></i>
                                <input value="Tìm Kiếm" id="submit-search" class="button-search shadow" type="submit">
                            </span>

                        <span class="wrap-submit-reset">
                                <i class="fa fa-refresh style-icon"></i>
                                <input value="Reset"  class="button-reset shadow" type="button" onclick="resetData()">
                        </span>
                    </div>
                </div>
                <div id="error-day-input">

                        <span id="text-error-1" class="text-error">
                            Vui lòng nhập ngày
                        </span>
                    <span id="text-error-2" class="text-error">
                            Vui lòng nhập ngày
                        </span>

                </div>

            </form>

            <div id="wrap-checkbox">
                <div>
                    <div>
                        <div>
                            <label style="margin-top: 30px; margin-right: 10px;" class="text">Phòng ban</label>
                            <input class="custom-input-data" id="input-department">

                            <label style="margin-top: 30px; margin-right: 10px; margin-left: 20px;" class="text">Tên môi giới</label>
                            <input class="custom-input-data" id="input-saleName">

                            <label style="margin-top: 30px; margin-right: 10px; margin-left: 20px;" class="text">Hrcode</label>
                            <input class="custom-input-data" id="input-hrCode">

                            <label style="margin-top: 30px; margin-right: 10px; margin-left: 20px;" class="text">Số tài khoản</label>
                            <input class="custom-input-data" id="input-account-number">

                            <label style="margin-top: 30px; margin-right: 10px; margin-left: 20px;" class="text">Role</label>
                            <input class="custom-input-data" id="input-role">

                            <div class="wrap-button-download" >
                                <input value="Download" type="button" id="button-download" onclick="queryUrlAndDownloadExcel()">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="myModal" class="modal">
        <div class="modal-content" >
            <div style="background-color:white">
                <span class="close" style="background-color: white;">&times;</span>
            </div>
            <p style="background-color: white;margin:0 0 15px 0" id="notice-rewritten-date"></p>
        </div>
    </div>

    <div class="table-staff-content">
        <iframe id="iframe-data-return-revenue"></iframe>
    </div>

    <div id="pagination">
        <div class="back-all-page">
            <i class="fas fa-angle-double-left" id="back-all-page" onclick="backAllPage()"></i>
        </div>
        <div class="next-pagination-left" >
            <i class="fas fa-angle-left" id="back-page" onclick="backPage()"></i>
        </div>
        <div style="width: 90px; text-align: center">
            <span id="page-number-moment">1</span><span> / </span><span id="page-total"></span>
        </div>
        <div class="next-pagination-right">
            <i class="fas fa-angle-right" id="next-page" onclick="nextPage()"></i>
        </div>
        <div class="next-all-page">
            <i class="fas fa-angle-double-right" id="next-all-page" onclick="nextAllPage()"></i>
        </div>
    </div>
    <script src="<c:url value="/resources/js/revenue-report-admin.js" />" type="text/javascript"></script>
</tiles:insertAttribute>