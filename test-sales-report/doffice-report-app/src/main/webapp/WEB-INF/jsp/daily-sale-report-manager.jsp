<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="context" value="/daily-manager-report" scope="request" />
<tiles:insertAttribute name="layout.basic">
<div class="manager-bound-screen">
    <div id="main-content">
        <div class="days">
           <form id="formDataInput">
                <div style="padding: 2px">
                    <div style="display: inline-block">
                        <span class="text">Kỳ báo cáo </span>
                        <label style="margin-left: 20px;" class="text">Từ ngày</label>
                        <input class="input-day custom-input-data" type="text" name="first-day" id="first-day" onchange="setAndCheckDay()" placeholder="dd/mm/yy" readonly>
                    </div>
                    <div style="display: inline-block">
                        <label style="margin-left: 20px;" class="text">Đến ngày</label>
                        <input class="input-day custom-input-data" type="text" name="last-day" id="last-day" onchange="setAndCheckDay()" placeholder="dd/mm/yy" readonly>
                    </div>
                    <div style="display: inline-block">
                        <label style="margin-left: 20px; margin-right: 20px;" class="text">Phòng ban</label>
                        <input type="text" onclick="toggleDepartment()" placeholder="Tất cả" class="custom-input-data" id="department-description" readonly />

                        <div class="form-department" style="display: none" id="zone-department-data">
                            <div id="select-all-department">
                                <input type="checkbox" onclick="isCheckAllDepartment() " id="choice-all-department" class="depart-checkbox" checked>
                                <label for="choice-all-department" id="label-choice-all-department" style="font-size: 14px">Tất cả</label>
                            </div>
                            <div  id="listDepartment" style="background-color: white;">
                                <c:forEach items="${sessionScope['group']}" var="departments" varStatus="vs">
                                    <div class="select-department" >
                                        <input type="checkbox" class="department-item depart-checkbox" id="${departments["code"]}" value=${departments["code"]} name="${departments["fullName"]}" checked onclick="checkSelectDepartment(); ">
                                        <label class="department-list" style="font-size: 14px" for="${departments["code"]}" >${departments["fullName"]}</label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    <div class="wrap-button">
                        <span class="wrap-button-search">
                            <i class="fa fa-search style-icon"></i>
                            <input value="Tìm Kiếm" id="submit-search" class="button-search shadow" type="button" onclick="featureApp('', 'search')">
                        </span>
                        <span class="wrap-submit-reset">
                                <i class="fa fa-refresh style-icon"></i>
                                <input value="Reset" class="button-reset shadow" type="button" onclick="resetData()">
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
                    <div class="day-set set">
                        <span class="text-error" id="text-error-depart">Vui lòng chọn phòng</span>
                    </div>
                </div>
           </form>

           <div id="wrap-checkbox">
               <div style="padding: 2px">

                   <div class="wrap-button-download" >
                       <input value="Download" type="button" id="button-download" onclick="featureApp('&type=excel','download')">
                   </div>

                   <label style="margin-right: 20px">Họ tên NV Sales - HRCode</label>
                   <input type="text" onfocus="publicMenu()" id="item" placeholder="Tất cả" class="custom-input-data" readonly>
                   <form style="display:none; height: 255px;overflow-y: scroll;" class="form-user-hrcode" id="menuCheckbox">
                       <div style="width: 313px; position: sticky; top: 0px; margin-left: -14px;">
                           <input type="text" class="checkboxes custom-input-data" placeholder="Tìm kiếm" id="searchData" style="margin-top:-1px; box-shadow: none;width: 90%" onkeyup="searchUser();">
                       </div>
                       <div id="select-all">
                           <input type="checkbox" onclick="selectAll()" class="checkboxes checkflag" id="choice-all" value="choice-all" checked>
                           <label for="choice-all" class="checkboxes content-items" style="font-size: 14px">Tất cả</label>
                       </div>
                       <div id="no-data"style="display:none; text-align: center; margin: 10px; color: #666;">Không tìm thấy kết quả phù hợp</div>
                       <div id="listUser" style="background-color: white;"></div>
                   </form>
                   <div id="error-noti">Chọn nhân viên để tiếp tục</div>
               </div>

           </div>
       </div>
    </div>

    <div id="myModal" class="modal">
        <div class="modal-content" >
            <div style="background-color:white"><span class="close" style="background-color: white;">&times;</span></div>
            <p style="background-color: white;margin:35px 0 15px 0" id = "notice-rewritten-date"></p>
        </div>
    </div>

     <div class="table-manager-content">
        <iframe id="iframe-data-return"></iframe>
    </div>
</div>
    <script>
        // Params Reset
        let firstdayReset
        let lastdayReset
        // ----------------
        let checkBox = document.getElementById("menuCheckbox");
        let listUser = document.getElementById("listUser");
        let valueLabel = document.getElementsByClassName("value-label");
        let modal = document.getElementById("myModal");
        let textDescription = document.getElementById("item");
        let btn = document.getElementById("myBtn");
        let closeModal = document.getElementsByClassName("close")[0];
        // User
        let checkSelectBox = document.getElementsByClassName("checkflag");
        let allSelection = document.getElementsByClassName("wrap-selection");
        let filterUser = document.getElementsByClassName("filter-user");
        // Department
        let zoneDepartmentData = document.getElementById("zone-department-data");
        let departmentCheckboxs=document.getElementsByClassName("depart-checkbox")
        //DATA NV Sale
        let textUserNameAndHrCode = [];
        let dataUserAndHrCode;
        let arrDataUser = [];
        let arrConvert = [];



        document.onclick =  (e) => {
            if (e.target.id !== "item"
                && e.target.id !== "menuCheckbox"
                && e.target.id !== "listUser"
                && e.target.id !== "no-data"
                && e.target.id !== "error-day-input"
                && e.target.id !== "select-all"
                && e.target.id !== "select-all-department"
                && e.target.id !== "choice-all-department"
                && e.target.id !== "label-choice-all-department"
                && e.target.id !== "department-description"
                && e.target.className !== "custom-input-data"
                && e.target.className !== "department-list"
                && e.target.className !== "checkboxes"
                && e.target.className !== "department-list"
                && e.target.className !== "form-user-hrcode"
                && e.target.className !== "department-item"
                && e.target.className !== "depart-checkbox"
                && e.target.className !== "value-label"
                && e.target.className !== "checkflag"
                && e.target.className !== "content-items"
                && e.target.className !== "filter-user"
                && e.target.className !== "department-item depart-checkbox"
                && e.target.className !== "checkboxes checkflag filter-user"
                && e.target.className !== "checkboxes checkflag"
                && e.target.className !== "checkboxes value-label"
                && e.target.className !== "checkboxes custom-input-data"
                && e.target.className !== "checkboxes content-items"
                && e.target.className !== "custom-input-data"
                && e.target.className !== "wrap-selection"
                && e.target.className !== "form-department"
                && e.target.className !== "checkboxes value-label content-items"){
                checkBox.style.display = "none";
                zoneDepartmentData.style.display ="none"
            }

            if(e.target.id === "item"){
                zoneDepartmentData.style.display ="none";
            }
            if (e.target.id === "department-description"){
                checkBox.style.display ="none";
            }


        }

        closeModal.onclick = () => {
            modal.style.display = "none";
            resetData()
        }
        window.onclick = (event) => {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }

        $( () => {
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
        } );


        let getDataNVSale = async () => {
            let firstDay = $("#first-day").val();
            let lastDay = $("#last-day").val();
            let checkBoxDepartment = document.getElementsByClassName("department-item")
            let arrDepartments=[];
            let groupId,url

            for(let i=0;checkBoxDepartment[i];i++){
                if (checkBoxDepartment[i].checked){
                    arrDepartments.push(checkBoxDepartment[i].getAttribute("value"))
                }
            }
            groupId = arrDepartments.length > 0 ? arrDepartments.join(" ") : '';
            if(firstDay&&lastDay&&groupId){
                url = "${context}/hrcode?firstDay=" + firstDay
                    + "&lastDay=" + lastDay
                    + "&groupId=" + groupId;
                const response =await fetch(url)
                document.getElementById("no-data").style.display = "none";
                document.getElementById("select-all").style.display = "block";

                dataUserAndHrCode = await  response.json();
                // console.log("dataUserAndHrCode: ",dataUserAndHrCode)
                if(dataUserAndHrCode.length>0){
                    // Push Data NV Sale into dropbox (Họ tên NV Sales - HRCode)
                    arrDataUser = [];
                    for(let count = 0; count < dataUserAndHrCode.length; count++){
                        arrDataUser.push(`<div class="wrap-selection" style="display:block;">
                                      <input type="checkbox" id=user-`+ dataUserAndHrCode[count].hrCode +  ` onclick="checkSelectAll()" class="checkboxes checkflag filter-user" checked>
                                      <label class="checkboxes value-label content-items" style="font-size: 13px" for=user-`+ dataUserAndHrCode[count].hrCode +">"
                            + dataUserAndHrCode[count].saleFullName + " - " + dataUserAndHrCode[count].hrCode +`
                                      </label>
                                  </div>`);
                    }
                    listUser.innerHTML = arrDataUser.join(" ");
                    listUser.style.display = "block";
                    // create Array convert data NV Sale to Search tool
                    arrConvert = dataUserAndHrCode.map((item) => {
                        return (item.saleFullName+" "+item.hrCode).normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/đ/g, "d").replace(/Đ/g, "D").toLowerCase();
                    })
                    document.getElementById("item").placeholder="Tất cả"
                    document.getElementById("choice-all").checked=true
                }else{
                    listUser.innerHTML="";
                    document.getElementById("item").placeholder=""
                    arrDataUser = [];
                    document.getElementById("select-all").style.display = "none";
                }

            }
        }

        let reRenderListEmployeeWhenSelectDepartment = ()  => {
            let checkBoxDepartment = document.getElementsByClassName("department-item")
            let arrDepartments=[];
            document.getElementById("searchData").value = "";
            for(let i=0;checkBoxDepartment[i];i++){
                if (checkBoxDepartment[i].checked){
                    arrDepartments.push(checkBoxDepartment[i].getAttribute("value"))
                }
            }
            listUser.style.display = "block"
            if(arrDepartments.length === 0){
                allSelection.length = 0;
                document.getElementById("no-data").style.display = "block";
                listUser.style.display = "none";
                document.getElementById("item").placeholder = "Không có lựa chọn nào";
                document.getElementById("select-all").style.display = "none";
            } else {
                getDataNVSale()
            }
        }
        // check lại
        let formatDate = (date) => {
            let newDate = '';
            if(date !== ""){
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
           let firstDay = document.getElementById("first-day");
           let lastDay = document.getElementById("last-day");
           let valueOfFirstDay = formatDate(firstDay.value);
           let valueOfLastDay = formatDate(lastDay.value);
            if(valueOfFirstDay !== "" && valueOfLastDay !== ""){
               if( (Date.parse(valueOfLastDay) - Date.parse(valueOfFirstDay)) > 2592000000 || Date.parse(valueOfLastDay) < Date.parse(valueOfFirstDay) ){
                   modal.style.display = "block";
               } else {
                   getDataNVSale();
               }
           }
        }

        let resetData = () => {
           let firstDay = document.getElementById("first-day")
           let lastDay = document.getElementById("last-day")
           for(let i=0;i<departmentCheckboxs.length;i++){
               departmentCheckboxs[i].checked=true
           }
           // Disable noti error department
            document.getElementById("text-error-depart").style.display = "none";
            // Set day default
           firstDay.value = firstdayReset;
           lastDay.value = lastdayReset;
            $("#last-day").datepicker('setDate', new Date(formatDate(lastdayReset)));
            $("#first-day").datepicker('setDate', new Date(formatDate(firstdayReset)));
           document.getElementById('department-description').placeholder = "Tất cả";
            document.getElementById("searchData").value=""
           getDataNVSale();
        }

        $('input').on('keydown',  (e) => {
            if (e.keyCode == 13) {
                return false;
            }
        });

        let searchUser = () => {
            let searchData = document.getElementById("searchData");
            document.getElementById("select-all").style.display = "none";
            if(searchData.value.length === 0 && allSelection.length !== 0) {
                document.getElementById("select-all").style.display = "block";
            }
            document.getElementById("no-data").style.display = "none";
            if(allSelection.length === 0) {
               document.getElementById("no-data").style.display = "block";
            }
            let keyword = searchData.value.normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/đ/g, "d").replace(/Đ/g, "D").toLowerCase();
            for(let index = 0; index < arrDataUser.length; index++){
                if(allSelection.length !== 0){
                    if(arrConvert[index].search(keyword) !== -1){
                        allSelection[index].style.display = "block";
                    } else {
                       allSelection[index].style.display = "none";
                    }
                }
            }
           for(let index = 0; index < arrDataUser.length; index++){
               if(allSelection.length !== 0) {
                   if(allSelection[index].style.display === "block"){
                       break;
                   } else {
                       if(index === arrDataUser.length - 1 && allSelection[index].style.display === "none"){
                           document.getElementById("no-data").style.display = "block";
                           document.getElementById("select-all").style.display = "none";
                       }
                   }
               }
           }
           if(document.getElementById("department-description").placeholder === "Không có lựa chọn nào"){
               document.getElementById("no-data").style.display = "block";
               document.getElementById("select-all").style.display = "none";
               document.getElementById("item").placeholder = "Không có lựa chọn nào";
           }
        }

        let changeContentInputData = () => {
            let countUserSelect = 0;
            for(let index = 0; index < filterUser.length; index++){
                if(filterUser[index].checked){
                    countUserSelect++;
                }
            }
            if(countUserSelect === 0){
                document.getElementById("item").placeholder = "Không có lựa chọn nào";
            }

            if(countUserSelect === 1 ){
                for(let index = 0; index < filterUser.length; index++){
                    if(filterUser[index].checked){
                        document.getElementById("item").placeholder = valueLabel[index].textContent.slice(0, 21) + "...";
                        break;
                    }
                }
            }

            if(countUserSelect > 1 && countUserSelect < filterUser.length ){
                document.getElementById("item").placeholder = "Chọn nhiều nhân sự";
            }

            if(countUserSelect === filterUser.length ){
                document.getElementById("item").placeholder = "Tất cả";
            }

            if(allSelection.length === 0){
                document.getElementById("item").placeholder = "Không có lựa chọn nào";
            }
        }

        let checkSelectAll = () => {
            let n = filterUser.length
            let count = 0
            for(let i = 0; i < filterUser.length; i++){
                if(filterUser[i].checked){
                   count++
                }
            }
            document.getElementById("choice-all").checked = n === count
            changeContentInputData();
            if(document.getElementById("department-description").placeholder === "Không có lựa chọn nào"){
                document.getElementById("no-data").style.display = "block";
                document.getElementById("select-all").style.display = "none";
                document.getElementById("item").placeholder = "Không có lựa chọn nào";
            }
        }
       // =============) Check Department (===============
       let checkSelectDepartment=()=>{
           let listDataSelection = document.getElementsByClassName('department-item')
           let totalDepartment = listDataSelection.length
           let count = 0
           let descriptions=[]
           let showDescription=""
           for(let i = 0; i < totalDepartment; i++){
               if(listDataSelection[i].checked){
                   count++
                   descriptions.push(listDataSelection[i].getAttribute("name"))
               }
           }
           if(descriptions.length===0){
               showDescription="Không có lựa chọn nào"
           }else if(descriptions.length===totalDepartment){
               showDescription="Tất cả"
           }else if( descriptions.length===1){
               showDescription=descriptions.join('')
           }else{
               showDescription="Chọn nhiều phòng"
           }
           document.getElementById('choice-all-department').checked = count===totalDepartment
           document.getElementById('department-description').placeholder=showDescription
           // render again HR_Code
           reRenderListEmployeeWhenSelectDepartment()
        }
        let isCheckAllDepartment=()=>{
            let inputDescription =  document.getElementById('department-description')
            let checkDepartment = document.getElementById('choice-all-department').checked
            let arrDepartments = document.getElementsByClassName('department-item');
            inputDescription.placeholder= checkDepartment?"Tất cả":"Không có lựa chọn nào"
            for (let i=0;arrDepartments[i];i++){
                arrDepartments[i].checked=checkDepartment
            }
            // render again HR_Code
            reRenderListEmployeeWhenSelectDepartment()
        }
        let selectAll = () => {
            if(document.getElementById("choice-all").checked){
                for(let index = 0; index < dataUserAndHrCode.length; index++ ){
                    if(allSelection[index].style.display === "block"){
                        filterUser[index].checked = true;
                    }
                }
            } else {
                for(let index = 0; index < dataUserAndHrCode.length; index++ ){
                    if(allSelection[index].style.display === "block"){
                        filterUser[index].checked = false
                    }
                }
            }
            changeContentInputData();
        }
        let publicMenu = () => {
           checkBox.style.display = "block";
        }
        let toggleDepartment = () => {
            zoneDepartmentData.style.display = "block";
        }
        let featureApp = (type, work) => {
            let firstDayCheck = document.getElementById("first-day");
            let lastDayCheck = document.getElementById("last-day");
            let valueOfFirstDay = firstDayCheck.value;
            let valueOfLastDay = lastDayCheck.value;
            let checkBoxDepartment = document.getElementsByClassName("department-item")
            let checkSelectTick = false;
            let arrDepartments=[];

            for(let index = 0; index < checkSelectBox.length; index++){
                if(checkSelectBox[index].checked === true) {
                    checkSelectTick = true;
                }
            }
            for(let i=0;checkBoxDepartment[i];i++){
                if (checkBoxDepartment[i].checked){
                    arrDepartments.push(checkBoxDepartment[i].getAttribute("value"))
                }
            }
            document.getElementById("error-noti").style.display = "none";
            document.getElementById("text-error-1").style.display = "none";
            document.getElementById("text-error-2").style.display = "none";
            document.getElementById("text-error-depart").style.display = "none";
            if(checkSelectTick && valueOfFirstDay !== "" && valueOfLastDay !== "" &&arrDepartments.length>0){
                let checkedValue = [];
                let inputElements = document.getElementsByClassName("checkflag");
                let checkAll=""
                for(let i=0; inputElements[i]; ++i){
                    if (inputElements[i].checked){
                        let match = inputElements[i].id.match('-([A-Za-z0-9]+)')
                        if(match&&match[1]==='all'){checkAll='all'}
                        if(match) checkedValue.push(match&&match[1]);
                    }
                }
                let firstDay = $("#first-day").val();
                let lastDay = $("#last-day").val();
                let saleHrCode,saleHrCodes='';
                let groupId = arrDepartments.length>0?arrDepartments.join("%20"):'';

                if (checkedValue.length == 0 ){
                    saleHrCode = "";
                }else if(checkAll==='all'){
                    saleHrCode = "";
                    saleHrCodes="";
                }else {
                    saleHrCode = checkedValue.join(' ')
                    saleHrCodes= checkedValue.join('%20')
                }
                localStorage.setItem('groupId',groupId);
                localStorage.setItem('saleHrCodes',saleHrCodes);
                let url = '${context}/manager-view?firstDay=' + firstDay+ '&lastDay=' + lastDay+ '&groupId='+groupId+ '&saleHrCode=' + saleHrCode+ type;
                if(work === "search"){
                    $('#iframe-data-return').prop('src', url);
                    document.getElementById('iframe-data-return').onload = () => {
                        let cssLink = document.createElement("link");
                        cssLink.href = "/resources/css/stylePopupManager.css";
                        cssLink.rel = "stylesheet";
                        cssLink.type = "text/css";
                        document.getElementById('iframe-data-return').contentWindow.document.head.appendChild(cssLink);
                        document.getElementById('iframe-data-return').contentWindow.document.getElementsByClassName("jrPage")[0].style.width = "2510px"
                        // iframe
                    }
                } else {
                    window.open(url);
                }
            } else {
                document.getElementById("text-error-1").style.display = valueOfFirstDay !== ""?"none":"inline"
                document.getElementById("text-error-2").style.display = valueOfLastDay !== ""?"none":"inline"
                document.getElementById("error-noti").style.display = checkSelectTick?"none":"inline"
                document.getElementById("text-error-depart").style.display = arrDepartments.length!==0?"none":"inline"
            }
        }
        window.onload = () => {
            let firstDay = document.getElementById("first-day");
            let lastDay = document.getElementById("last-day");

            // let lastTime = new Date(new Date() - 24*60*60*1000);
            // let firstTime = new Date(lastTime - 2592000000);
            let lastTime = new Date(new Date().setDate(new Date().getDate()-1))
            let firstTime = new Date(new Date().setDate(lastTime.getDate()-29));

            firstDay.value = auformatDate(firstTime);
            lastDay.value = auformatDate(lastTime);
            // Set Param Reset
            firstdayReset= auformatDate(firstTime);
            lastdayReset=auformatDate(lastTime);
            getDataNVSale();
            //content notice
            let contentNotice = document.getElementById("notice-rewritten-date");
            contentNotice.textContent = "Xin vui lòng nhập thời gian trong khoảng 30 ngày tính đến ngày "  + lastDay.value;
            //get data user and hrCode
        }
    </script>
</tiles:insertAttribute>