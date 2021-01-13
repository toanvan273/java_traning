let all = document.getElementById("all");
let checkBox = document.getElementById("menuProduct");
let allCheckBox = document.getElementsByClassName("checkboxes");
let modal = document.getElementById("myModal");
let btn = document.getElementById("myBtn");
let span = document.getElementsByClassName("close")[0];
let checkSelectBox = document.getElementsByClassName("checkflag");
let textDescription = document.getElementById("item");
let contentItems = document.getElementsByClassName("content-items");
let pageNum = 1;
let pageSize = 10;
let totalPageNum;
let checkLoadPage = false;


let formatDate = (date) => {
    let newDate = '';
    for(let index = 3; index < 6; index++ ){
        newDate += date[index];
    }

    for(let index = 0; index < 3; index++ ){
        newDate += date[index];
    }

    for(let index = 6; index < date.length; index++ ){
        newDate += date[index];
    }
    return newDate;
}
let setAndCheckDay = () => {
    let firstDay = document.getElementById("first-day")
    let lastDay = document.getElementById("last-day")

    let valueOfFirstDay = formatDate(firstDay.value);
    let valueOfLastDay = formatDate(lastDay.value);

    if (valueOfFirstDay !== "" && valueOfLastDay !== ""){
        if ((Date.parse(valueOfLastDay) - Date.parse(valueOfFirstDay)) > 2678400000 ||Date.parse(valueOfFirstDay) > Date.parse(valueOfLastDay) ){
            modal.style.display = "block";
            document.getElementById("text-error-1").style.display = "none";
            document.getElementById("text-error-2").style.display = "none";
            document.getElementById("error-noti").style.display = "none";
            firstDay.value = "";
            lastDay.value = "";
        }
    }

    if (valueOfFirstDay == "" || valueOfLastDay == "") {
        modal.style.display = "block";
        firstDay.value = "";
        lastDay.value = "";
    }
}

$( function() {
    $( "#first-day").datepicker({
        dateFormat: "dd/mm/yy",
        beforeShow: function(input, inst) {
        }
    });
} );

$( function() {
    $( "#last-day").datepicker({
        dateFormat: "dd/mm/yy",
        beforeShow: function(input, inst) {

        }
    });
} );

span.onclick = () => {
    modal.style.display = "none";
}

window.onclick = (event) => {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

let resetData = () => {
    let firstDay = document.getElementById("first-day")
    let lastDay = document.getElementById("last-day")
    let accountNumber = document.getElementById("input-account-number")
    for(let count = 0; count < allCheckBox.length; count += 2) {
        allCheckBox[count].checked = false;
    }
    accountNumber.value = ""
    firstDay.value = ""
    lastDay.value = ""
    textDescription.placeholder = "Lựa chọn";
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
        pageNumberMoment++;
        pageMoment.textContent = pageNumberMoment;
        pageNum++;
        searchDataIframe();
    }
    if (pageNumberMoment === pageNumberTotal){
        iconNextPage.style.color = "#848484b3";
        iconNextAllPage.style.color = "#848484b3";

    }
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
        searchDataIframe();
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
    if (e.target.id !== "item"
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
    // document.getElementById("text-error-2").style.marginLeft = "180px"
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
        let hrCode = $("#input-hrCode").val();
        let department = $('#input-department').val();
        let careByName = $('#input-saleName').val();
        let position = $('#input-role').val();
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

        let url = '/revenue-report/admin-view?fromDate=' + firstDay
            + '&toDate=' + lastDay
            + '&acctNo=' + acctno
            + '&product=' + product
            + '&pageNum=' + pageNum
            + '&pageSize=' + pageSize
            + '&hrCode=' + hrCode
            + '&department=' + department
            + '&careByName=' + careByName
            + '&position=' + position;
        $('#iframe-data-return-revenue').prop('src', url);

        let iFrameDataReturn = document.getElementById("iframe-data-return-revenue")

        document.getElementById('iframe-data-return-revenue').onload = function() {
            totalPageNum = iFrameDataReturn.contentWindow.document.getElementById("number-page").textContent;
            document.getElementById("page-total").textContent = totalPageNum;
            paginationElement.style.display = "flex"; // show pagination element
            if (pageNum === 1){
                document.getElementById("next-all-page").style.color = "#666";
                document.getElementById("back-page").style.color = "#848484b3";
                if (parseInt(totalPageNum) > 1){
                    document.getElementById("next-page").style.color = "#666";
                } else {
                    document.getElementById("next-page").style.color = "#848484b3";
                    document.getElementById("next-all-page").style.color = "#848484b3";
                }
            }
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
                    document.getElementById("text-error-2").style.marginLeft = "495px";
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
        let hrCode = $("#input-hrCode").val();
        let department = $('#input-department').val();
        let careByName = $('#input-saleName').val();
        let position = $('#input-role').val();

        if (checkedValue.length == 0) {
            product = [];
            product = JSON.stringify(product);
            product = product.substring(1,product.length-1);

        } else {
            if (checkedValue.includes("all")) {
                checkedValue.splice(0,1);
            }
            product = JSON.stringify(checkedValue);
            product = product.substring(2,product.length-2);
        }

        let urlDownload =  '/revenue-report/admin-view/export?fromDate=' + firstDay
            + '&toDate=' + lastDay
            + '&acctNo=' + acctno
            + '&product=' + product
            + '&type=excel'
            + '&hrCode=' + hrCode
            + '&department=' + department
            + '&careByName=' + careByName
            + '&position=' + position;
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
                    document.getElementById("text-error-2").style.marginLeft = "495px";
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
    setAndCheckDay();
    let iconBackAllPage = document.getElementById("back-all-page");
    iconBackAllPage.style.color = "#848484b3"
    checkLoadPage = true;
    searchDataIframe();
    return false;
});

$('menuProduct').submit(function(){
    return false;
});

window.onload = () => {
    //initialization first day and last day

    let firstDay = document.getElementById("first-day");
    let lastDay = document.getElementById("last-day");
    let lastTime = new Date()
    let firstTime = new Date(lastTime - 2592000000);
    let resultMonthOfFirstTime = (firstTime.getMonth() + 1).toString();
    let resultMonthOfLastTime = (lastTime.getMonth() + 1).toString();
    let resultDateOfFirstTime = (firstTime.getDate()).toString();
    let resultDateOfLastTime = (lastTime.getDate()).toString();

    if ( resultDateOfFirstTime.length === 1 ){
        resultDateOfFirstTime  = '0' + resultDateOfFirstTime;
    }

    if ( resultDateOfLastTime.length === 1 ){
        resultDateOfLastTime  = '0' + resultDateOfLastTime;
    }

    if ( resultMonthOfFirstTime.length === 1 ){
        resultMonthOfFirstTime  = '0' + resultMonthOfFirstTime;
    }

    if ( resultMonthOfLastTime.length === 1 ){
        resultMonthOfLastTime  = '0' + resultMonthOfLastTime;
    }

    firstDay.value = resultDateOfFirstTime + '/' +  resultMonthOfFirstTime + '/' + (firstTime.getFullYear()).toString();
    lastDay.value = resultDateOfLastTime + '/' + resultMonthOfLastTime + '/' + (lastTime.getFullYear()).toString();

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

}