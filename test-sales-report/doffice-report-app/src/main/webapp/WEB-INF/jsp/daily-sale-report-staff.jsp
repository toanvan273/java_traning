<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url var="context" value="/daily-sale-report" scope="request" />
<tiles:insertAttribute name="layout.basic">
<div class="staff-bound-screen">
	<div id="main-content">
		<div class="days">
			<form id="formDataInput">
				<div style="padding: 2px">
					<span class="text">Kỳ báo cáo </span>
					<label style="margin-left: 20px;" class="text">Từ ngày</label>
					<input class="input-day custom-input-data" type="text" name="first-day" id="first-day" placeholder="dd/mm/yy" onchange="setAndCheckDay()" readonly>
					<label style="margin-left: 20px;" class="text">Đến ngày</label>
					<input class="input-day custom-input-data" type="text" name="last-day" id="last-day" placeholder="dd/mm/yy" onchange="setAndCheckDay()" readonly>
					<label class="text" id="text-number-account" oninvalid="this.setCustomValidity('Vui lòng nhập ngày')" oninput="this.setCustomValidity('')" required>Số tài khoản giao dịch</label>
					<input type="text" id="input-account-number" class="custom-input-data" maxlength="10" onkeyup="formatNumber()" pattern=".{0}|.{10}" oninvalid="this.setCustomValidity('Gồm các kí tự từ 0-9')" oninput="this.setCustomValidity('')">
					<div class="wrap-button">
						<span class="wrap-button-search"> <i
							class="fa fa-search style-icon"></i> <input value="Tìm Kiếm"
							id="submit-search" class="button-search shadow" type="submit">
						</span> <span class="wrap-submit-reset"> <i
							class="fa fa-refresh style-icon"></i> <input value="Reset"
							class="button-reset shadow" type="button" onclick="resetData()">
						</span>
					</div>
				</div>

				<div id="error-day-input">
					<div class="day-set from-day">
						<span class="text-error" id="text-error-1">Vui lòng nhập ngày</span>
					</div>
					<div class="day-set set">
						<span class="text-error" id="text-error-2">Vui lòng nhập ngày</span>
					</div>
				</div>
			</form>

			<div id="wrap-checkbox">
				<div>
					<div>
						<div>
							<label style="margin-top: 30px; margin-right: 20px;" class="text">Phòng
								ban</label>
							<input placeholder=" " disabled="disabled" class="custom-input-data" id="input-department">

							<div class="wrap-button-download">
								<input value="Download" type="button" id="button-download"
									onclick="queryUrlAndDownloadExcel()">
							</div>

							<div id="wrap-select-filter">
								<label class="text" id="text-items">Sản phẩm</label>
								<input type="text" onfocus="publicMenu()" id="product-description" class="custom-input-data" placeholder="Tất cả" readonly>
								<span id="error-noti">Chọn sản phẩm để tiếp tục</span>
								<form style="display: none" id="menuProduct">
									<input type="checkbox" onclick="selectAll();check()" class="checkboxes checkflag" id="all" value="all" checked>
									<label for="all" class="checkboxes content-items">Tất cả</label><br>
									<input type="checkbox" onclick="check()" class="checkboxes checkflag" id="stockFlag" value="stockFlag" checked>
									<label for="stockFlag" class="checkboxes content-items">Giao dịch cơ sở</label><br>
									<input type="checkbox" onclick="check()" class="checkboxes checkflag" id="marginStockFlag" value="marginStockFlag" checked>
									<label for="marginStockFlag" class="checkboxes content-items">Sản phẩm cho vay cơ sở</label><br>
									<input type="checkbox" onclick="check()" class="checkboxes checkflag" id="bondFlag" value="bondFlag" checked>
									<label for="bondFlag" class="checkboxes content-items">Sản phẩm trái phiếu</label><br>
									<input type="checkbox" onclick="check()" class="checkboxes checkflag" id="fundFlag" value="fundFlag" checked>
									<label for="fundFlag" class="checkboxes content-items">Sản phẩm CCQ</label><br>
									<input type="checkbox" onclick="check()" class="checkboxes checkflag" id="assetManagementFlag" value="assetManagementFlag" checked>
									<label for="assetManagementFlag" class="checkboxes content-items">Quản lý tài sản</label><br>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div id="myModal" class="modal">
		<div class="modal-content">
			<div style="background-color: white">
				<span class="close" style="background-color: white;">&times;</span>
			</div>
			<p style="background-color: white; margin: 35px 0 15px 0"
				id="notice-rewritten-date"></p>
		</div>
	</div>

	<div class="table-staff-content">
		<iframe id="iframe-data-return"></iframe>
	</div>

	<div id="pagination">
		<div class="back-all-page">
			<i class="fas fa-angle-double-left" id="back-all-page"
				onclick="backAllPage()"></i>
		</div>
		<div class="next-pagination-left">
			<i class="fas fa-angle-left" id="back-page" onclick="backPage()"></i>
		</div>
		<div style="width: 90px; text-align: center">
			<span id="page-number-moment">1</span><span> / </span><span
				id="page-total"></span>
		</div>
		<div class="next-pagination-right">
			<i class="fas fa-angle-right" id="next-page" onclick="nextPage()"></i>
		</div>
		<div class="next-all-page">
			<i class="fas fa-angle-double-right" id="next-all-page"
				onclick="nextAllPage()"></i>
		</div>
	</div>
</div>
	<script>
    let all = document.getElementById("all");
    let checkBox = document.getElementById("menuProduct");
    let allCheckBox = document.getElementsByClassName("checkboxes");
    let modal = document.getElementById("myModal");
    let btn = document.getElementById("myBtn");
    let closeModal = document.getElementsByClassName("close")[0];
    let checkSelectBox = document.getElementsByClassName("checkflag");
    let textDescription = document.getElementById("product-description");
    let contentItems = document.getElementsByClassName("content-items");
    let pageNum = 1;
    let totalPageNum;
    let checkLoadPage = false;
	// Reset Params
	let firstdayReset
	let lastdayReset

    let formatDate = (date) => {
        let newDate = '';
        if(date.length>0){
            for(let index = 3; index < 6; index++ ){
                newDate += date[index];
            }

            for(let index = 0; index < 3; index++ ){
                newDate += date[index];
            }

            for(let index = 6; index < date.length; index++ ){
                newDate += date[index];
            }
        }
        return newDate;
    }
    let setAndCheckDay = () => {
        let firstDay = document.getElementById("first-day")
        let lastDay = document.getElementById("last-day")
        let valueOfFirstDay = formatDate(firstDay.value);
        let valueOfLastDay = formatDate(lastDay.value);
        if(valueOfLastDay === ""){
            document.getElementById("text-error-2").style.display = "inline";
        }else{
            if ((Date.parse(valueOfLastDay) - Date.parse(valueOfFirstDay)) > 2592000000 ||  Date.parse(valueOfFirstDay) > Date.parse(valueOfLastDay) ){
                modal.style.display = "block";
                document.getElementById("text-error-1").style.display = "none";
                document.getElementById("text-error-2").style.display = "none";
                document.getElementById("error-noti").style.display = "none";
                // firstDay.value = "";
                // lastDay.value = "";
                // document.getElementById('input-department').setAttribute('placeholder','')
            }else {
                document.getElementById("text-error-2").style.display = "none";
                getDepartment()
            }
        }

    }

	$( () => {
		$("#first-day").datepicker({
			dateFormat: "dd/mm/yy",
			beforeShow: (input, inst) => {
				let maxDate = new Date();
				let minDate = new Date();
				maxDate.setDate(maxDate.getDate() - 1);
				minDate.setDate(minDate.getDate() - 30);
				$('#first-day').datepicker('option', 'maxDate', maxDate);
				$('#first-day').datepicker('option', 'minDate', minDate);
			}
		});
		$( "#last-day").datepicker({
			dateFormat: "dd/mm/yy",
			beforeShow: function(input, inst) {
				let maxDate = new Date();
				let minDate = new Date();
				maxDate.setDate(maxDate.getDate() - 1);
				minDate.setDate(minDate.getDate() - 30);
				$('#last-day').datepicker('option', 'maxDate', maxDate);
				$('#last-day').datepicker('option', 'minDate', minDate);
			}
		});
	} )


	closeModal.onclick = () => {
        modal.style.display = "none";
        resetData()
    }

    window.onclick = (event) => {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    let resetData = () => {
        let firstDay = document.getElementById("first-day")
        let lastDay = document.getElementById("last-day")
        // let accountNumber = document.getElementById("input-account-number")
		for(let i = 0; i < checkSelectBox.length; i++) {
			checkSelectBox[i].checked=true
		}
		// reset day
        firstDay.value = firstdayReset
        lastDay.value = lastdayReset
		$("#last-day").datepicker('setDate', new Date(formatDate(lastdayReset)));
		$("#first-day").datepicker('setDate', new Date(formatDate(firstdayReset)));
        textDescription.placeholder = "Tất cả";
		document.getElementById('input-department').placeholder=""
		document.getElementById("input-account-number").value=""
		document.getElementById("error-noti").style.display = "none";
		getDepartment()
    }

    let nextPage = () => {
        let pageMoment = document.getElementById("page-number-moment");
        let iconBackAllPage = document.getElementById("back-all-page");
        let iconNextAllPage = document.getElementById("next-all-page");
        let pageTotal = document.getElementById("page-total");
        let iconNextPage = document.getElementById("next-page");
        let iconBackPage = document.getElementById("back-page");
        let pageNumberMoment = parseInt(pageMoment.textContent);
        let pageNumberTotal = parseInt(pageTotal.textContent);
        if (pageNumberMoment < pageNumberTotal){
            checkLoadPage = false;
            iconBackPage.style.color ="#666"
            iconBackAllPage.style.color = "#666"
			iconNextPage.style.cursor = "pointer";
			iconNextAllPage.style.cursor = "pointer";
            pageNumberMoment++;
            pageMoment.textContent = pageNumberMoment;
            pageNum++;
            searchDataIframe();
        }
        if (pageNumberMoment === pageNumberTotal){
            iconNextPage.style.color = "#848484b3";
			iconNextPage.style.cursor = "no-drop";
            iconNextAllPage.style.color = "#848484b3";
			iconNextAllPage.style.cursor = "no-drop";
        }
		checkCusorPagnig()
    }


    let backPage = () => {
        let pageMoment = document.getElementById("page-number-moment");
        let iconNextAllPage = document.getElementById("next-all-page");
        let iconBackAllPage = document.getElementById("back-all-page");
        let pageTotal = document.getElementById("page-total");
        let iconNextPage = document.getElementById("next-page");
        let iconBackPage = document.getElementById("back-page");
        let pageNumberMoment = parseInt(pageMoment.textContent);
        let pageNumberTotal = parseInt(pageTotal.textContent);
        if ( pageNumberMoment > 1 ){
            checkLoadPage = false;
            iconNextPage.style.color = "#666";
            iconNextAllPage.style.color = "#666";
            pageNumberMoment--;
            pageMoment.textContent = pageNumberMoment;
            pageNum--;
            searchDataIframe();
        }
        if (pageNumberMoment === 1){
            iconBackPage.style.color = "#848484b3";
            iconBackAllPage.style.color = "#848484b3";
        }
		checkCusorPagnig()
    }

    let backAllPage = () => {
        let pageMoment = document.getElementById("page-number-moment");
        let pageTotal = document.getElementById("page-total");
        let iconBackAllPage = document.getElementById("back-all-page");
        let iconNextAllPage = document.getElementById("next-all-page");
        let iconBackPage = document.getElementById("back-page");
        let pageNumberMoment = parseInt(pageMoment.textContent);
        let pageNumberTotal = parseInt(pageTotal.textContent);
        checkLoadPage = false;
        if ( pageNumberMoment > 1 ){
            iconBackPage.style.color = "#848484b3";
            iconBackAllPage.style.color = "#848484b3";
            iconNextAllPage.style.color = "#666";
            pageMoment.textContent = "1";
            pageNum = 1;
            searchDataIframe();
        }
		checkCusorPagnig()
    }

    let nextAllPage = () => {
        let pageMoment = document.getElementById("page-number-moment");
        let pageTotal = document.getElementById("page-total");
        let iconNextAllPage = document.getElementById("next-all-page");
        let iconBackAllPage = document.getElementById("back-all-page");
        let iconNextPage = document.getElementById("next-page");
        let iconBackPage = document.getElementById("back-page");
        let pageNumberMoment = parseInt(pageMoment.textContent);
        let pageNumberTotal = parseInt(pageTotal.textContent);
        checkLoadPage = false;
        if (pageNumberMoment < pageNumberTotal) {
            iconNextAllPage.style.color = "#848484b3";
            iconNextPage.style.color = "#848484b3";
            iconBackAllPage.style.color = "#666";
            iconBackPage.style.color = "#666";
            pageMoment.textContent = totalPageNum;
            pageNum = parseInt(totalPageNum);
			// iconNextPage.style.cursor='pointer';
			// iconNextAllPage.style.cursor='pointer';
            searchDataIframe();
        }
		checkCusorPagnig()
    }
	let checkCusorPagnig=()=>{
		let totalPage = parseInt(totalPageNum)
		let iconNextAllPage = document.getElementById("next-all-page");
		let iconBackAllPage = document.getElementById("back-all-page");
		let iconNextPage = document.getElementById("next-page");
		let iconBackPage = document.getElementById("back-page");
		if(totalPage>1){
			if(pageNum===1){
				iconNextPage.style.cursor='pointer';
				iconNextAllPage.style.cursor='pointer';
				iconBackAllPage.style.cursor='no-drop';
				iconBackPage.style.cursor='no-drop';
			}else if(pageNum<totalPage){
				iconNextAllPage.style.cursor='pointer';
				iconNextPage.style.cursor='pointer';
				iconBackPage.style.cursor='pointer';
				iconBackAllPage.style.cursor='pointer';
			}else {
				iconNextAllPage.style.cursor='no-drop';
				iconNextPage.style.cursor='no-drop';
				iconBackPage.style.cursor='pointer';
				iconBackAllPage.style.cursor='pointer';
			}
		}else {
			iconNextAllPage.style.cursor='no-drop';
			iconBackAllPage.style.cursor='no-drop';
			iconNextPage.style.cursor='no-drop';
			iconBackPage.style.cursor='no-drop';
		}
	}
    let check = () => {
        for(let count = 2; count < allCheckBox.length; count += 2){
            if ( allCheckBox[count].checked === false) {
                allCheckBox[0].checked = false;
                break;
            }

            if (count == allCheckBox.length - 2) {
                allCheckBox[0].checked = true;
            }
        }
        let context = ""
        for(let index = 0; index < checkSelectBox.length; index++) {
            if (checkSelectBox[0].checked){
                context = "Tất cả"
                break;
            }

            if (checkSelectBox[index].checked) {
                context +=  contentItems[index].textContent + " ";
            }
        }

        if (context.length > 18){
            context = context.slice(0,20);
            context += "..."
        }

        textDescription.placeholder = context;
    }

    let formatNumber = () => {
        let input = document.getElementById("input-account-number");
        let {value} = input;
        let temp = input.value.replace(/$\s?|(,*)/g, "")
        let lengthTemp = temp.length;
        input.value = temp.replace(/\D/g, "")
    }

    document.onclick = (e) => {
        if (e.target.id !== "product-description"
            && e.target.className !== "checkboxes"
            && e.target.id !== "menuProduct"
            && e.target.className !== "checkflag"
            && e.target.className !== "checkboxes checkflag"
            && e.target.classname !== "content-items"
            && e.target.className !== "checkboxes content-items") {
            checkBox.style.display = 'none';
        }
    }

    let publicMenu = () => {
        checkBox.style.display = "block";
    }

    let selectAll = () => {
        if (allCheckBox[0].checked === false) {
            for(let count = 0; count < allCheckBox.length; count += 2) {
                allCheckBox[count].checked = false;
            }
        } else {
            for(let count = 0; count < allCheckBox.length; count += 2) {
                allCheckBox[count].checked = true;
            }
        }
    }


    let searchDataIframe = () => {
        let firstDayCheck = document.getElementById("first-day");
        let lastDayCheck = document.getElementById("last-day");
        let valueOfFirstDay = firstDayCheck.value;
        let valueOfLastDay = lastDayCheck.value;
        let checkSelectTick = false;
        let paginationElement = document.getElementById("pagination");
        for(let index = 0; index < checkSelectBox.length; index++){
            if (checkSelectBox[index].checked === true) {
                checkSelectTick = true;
                break;
            } else {
                continue;
            }
        }
        document.getElementById("text-error-1").style.display = "none";
        document.getElementById("text-error-2").style.display = "none";
        document.getElementById("error-noti").style.display = "none";
        if (checkSelectTick && valueOfFirstDay !== "" && valueOfLastDay !== "" ){
            let checkedValue = [];
            let inputElements = document.getElementsByClassName("checkflag");

            for(let i=0; inputElements[i]; ++i){
                if (inputElements[i].checked){
                    checkedValue.push(inputElements[i].value);
                }
            }

            let firstDay = $("#first-day").val();
            let lastDay = $("#last-day").val();
            let acctno = $("#input-account-number").val();
            let product = [];

            if (checkedValue.length == 0 || checkedValue.includes("all")){
                product = [];
                product = JSON.stringify(product);
                product = product.substring(1,product.length-1);

            } else {
                product = JSON.stringify(checkedValue);
                product = product.substring(2,product.length-2);
            }

            if (checkLoadPage){
                pageNum = 1;
                document.getElementById("page-number-moment").textContent = 1;
            }

            let url = '${context}/staff-view?firstDay=' + firstDay
                + '&lastDay=' + lastDay
                + '&acctno=' + acctno
                + '&product=' + product
                + '&pageNum=' + pageNum;
            $('#iframe-data-return').prop('src', url);

            let iFrameDataReturn = document.getElementById("iframe-data-return")
            document.getElementById('iframe-data-return').onload = function() {
                totalPageNum = iFrameDataReturn.contentWindow.document.getElementById("number-page").textContent;
				console.log('totalPage: ',totalPageNum)
                document.getElementById("page-total").textContent = totalPageNum;
				if(totalPageNum>0){
					paginationElement.style.display = "flex"; // show pagination element
				}
                if (pageNum === 1){
                    document.getElementById("next-all-page").style.color = "#666";
                    document.getElementById("back-page").style.color = "#848484b3";
                    if (parseInt(totalPageNum) > 1){
                        document.getElementById("next-page").style.color = "#666";
                    } else {
                        document.getElementById("next-page").style.color = "#848484b3";
                        document.getElementById("next-all-page").style.color = "#848484b3";
                    }
                } // check phan trang
				checkCusorPagnig()
				let cssLink = document.createElement("link");
				cssLink.href = "/resources/css/stylePopupStaff.css";
				cssLink.rel = "stylesheet";
				cssLink.type = "text/css";
				document.getElementById('iframe-data-return').contentWindow.document.head.appendChild(cssLink);
				// style iframe
            };

        } else {
            if (!checkSelectTick && valueOfFirstDay === "" && valueOfLastDay === "" ){
                document.getElementById("error-noti").style.display = "inline";
                document.getElementById("text-error-1").style.display = "inline";
                document.getElementById("text-error-2").style.display = "inline";
            } else {
                if (valueOfFirstDay === "" && valueOfLastDay === "" ) {
                    document.getElementById("text-error-1").style.display = "inline";
                    document.getElementById("text-error-2").style.display = "inline";
                } else {
                    if (valueOfLastDay === "") {
                        document.getElementById("text-error-2").style.display = "inline";
                    }
                    if (valueOfFirstDay === "") {
                        document.getElementById("text-error-1").style.display = "inline"
                    }
                    if (!checkSelectTick){
                        document.getElementById("error-noti").style.display = "inline";
                    }
                }
            }
        }
    }

    let queryUrlAndDownloadExcel = () => {
        let firstDayCheck = document.getElementById("first-day")
        let lastDayCheck = document.getElementById("last-day")
        let valueOfFirstDay = firstDayCheck.value;
        let valueOfLastDay = lastDayCheck.value;
        let checkSelectTick = false;
        for(let index = 0; index < checkSelectBox.length; index++){
            if (checkSelectBox[index].checked === true) {
                checkSelectTick = true;
                break;
            } else {
                continue;
            }
        }
        document.getElementById("text-error-1").style.display = "none";
        document.getElementById("text-error-2").style.display = "none";
        document.getElementById("error-noti").style.display = "none";
        //document.getElementById("text-error-2").style.marginLeft = "185px"
        if (checkSelectTick && valueOfFirstDay !== "" && valueOfLastDay !== "" ){
            let checkedValue = [];
            let inputElements = document.getElementsByClassName("checkflag");
            for(let i=0; inputElements[i]; ++i){
                if (inputElements[i].checked){
                    checkedValue.push(inputElements[i].value);
                }
            }

            let firstDay = $("#first-day").val();
            let lastDay = $("#last-day").val();
            let acctno = $("#input-account-number").val();
            let product = [];

            if (checkedValue.length == 0 || checkedValue.includes("all")) {
                product = [];
                product = JSON.stringify(product);
                product = product.substring(1,product.length-1);

            } else {
                product = JSON.stringify(checkedValue);
                product = product.substring(2,product.length-2);
            }

            let urlDownload =  '${context}/staff-view?firstDay=' + firstDay
                + '&lastDay=' + lastDay
                + '&acctno=' + acctno
                + '&product=' + product
                + '&type=excel';
                window.open(urlDownload);

        } else {
            if (!checkSelectTick && valueOfFirstDay === "" && valueOfLastDay === ""){
                document.getElementById("error-noti").style.display = "inline";
                document.getElementById("text-error-1").style.display = "inline";
                document.getElementById("text-error-2").style.display = "inline";
            } else {
                if (valueOfFirstDay === "" && valueOfLastDay === "" ) {
                    document.getElementById("text-error-1").style.display = "inline";
                    document.getElementById("text-error-2").style.display = "inline";
                } else {
                    if (valueOfLastDay === "") {
                        document.getElementById("text-error-2").style.display = "inline";
                    }
                    if (valueOfFirstDay === "") {
                        document.getElementById("text-error-1").style.display = "inline"
                    }
                    if (!checkSelectTick){
                        document.getElementById("error-noti").style.display = "inline";
                    }
                }
            }
        }
    }

    $('#formDataInput').submit(function () {
        let iconBackAllPage = document.getElementById("back-all-page");
        iconBackAllPage.style.color = "#848484b3"
        checkLoadPage = true;
        searchDataIframe();
        return false;
    });

    $('menuProduct').submit(function(){
        return false;
    });
    let getDepartment = async () => {
        // let firstDay = $("#first-day").val();
        let lastDay = $("#last-day").val();
        let url = '${context}/groupname?lastDay=' + lastDay;
        fetch(url,{
            method:'GET',
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
                'Content-Type': 'application/json',
            },
        }).then(res=>res.text()
        ).then(text=>{
            let input = document.getElementById('input-department')
            input.setAttribute('placeholder',text)
        })

    }
    window.onload = () => {
        //initialization first day and last day
        let firstDay = document.getElementById("first-day");
        let lastDay = document.getElementById("last-day");
        let lastTime = new Date(new Date().setDate(new Date().getDate()-1))
        let firstTime = new Date(new Date().setDate(lastTime.getDate()-29));
		firstDay.value = auformatDate(firstTime);
		lastDay.value = auformatDate(lastTime);
		firstdayReset=auformatDate(firstTime);
		lastdayReset=auformatDate(lastTime);
        //format content department

        let inputDepartment = document.getElementById("input-department");
        let text = document.createElement("span");
        document.body.appendChild(text);
        text.style.fontSize = 13 + "px";
        text.style.height = 'auto';
        text.style.width = 'auto';
        text.style.position = 'absolute';
        text.style.whiteSpace = 'no-wrap';
        text.innerHTML = inputDepartment.placeholder;
        let width = Math.ceil(text.clientWidth);
        document.body.removeChild(text);
        let content = inputDepartment.placeholder;
        let newTextContent = "";

        if (width > 147){
            let newTextContentWidth = 0;
            let index = 0;
            while (newTextContentWidth < 115) {
                let tempText = document.createElement("span");
                document.body.appendChild(tempText);
                tempText.style.fontSize = 13 + "px";
                tempText.style.height = 'auto';
                tempText.style.width = 'auto';
                tempText.style.position = 'absolute';
                tempText.style.whiteSpace = 'no-wrap';
                tempText.innerHTML = newTextContent;
                newTextContentWidth = Math.ceil(tempText.clientWidth);
                newTextContent += content[index];
                index++;
                document.body.removeChild(tempText);
            }
            newTextContent +=  "...";
        } else {
            newTextContent = content;
        }
        inputDepartment.placeholder = newTextContent;


        //content notice

        let contentNotice = document.getElementById("notice-rewritten-date");
        contentNotice.textContent = "Xin vui lòng nhập thời gian trong khoảng 30 ngày tính đến ngày "  + lastDay.value;
        getDepartment()
    }
    </script>
</tiles:insertAttribute>