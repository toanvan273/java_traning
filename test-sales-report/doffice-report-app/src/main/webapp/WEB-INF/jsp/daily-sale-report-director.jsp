<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<tiles:insertAttribute name="layout.basic">
	<div style="display: flex; margin: 20px">
		<div class="days" style="width: 90%;">
            <form style="width: 100%;">
                <form>
                    <span class="text">Kỳ báo cáo </span> <label style="margin-left: 20px;" class="text">Từ ngày</label>
                    <input class="input-day" type="date" name="first-day" id="first-day" onchange="setAndCheckDay()" oninvalid="this.setCustomValidity('Vui lòng nhập ngày')" required>
                    <label style="margin-left: 50px;" class="text">Đến ngày</label> 
                    <input class="input-day" type="date" name="last-day" id="last-day" onchange="setAndCheckDay()"  oninvalid="this.setCustomValidity('Vui lòng nhập ngày')" required>
                    <input value="Reset" class="button submit-search" type="button" onclick="resetData()" >
                    <input value="Tìm Kiếm" class="button submit-search" type="submit" > 
                </form>
                <div style="font-size: 14px;color:red;">*Chú ý: Khoảng cách giữa 2 thời điểm không quá 30 ngày</div>


                <div style="margin-top: 6px; width: 100%;">
                    <input value="Download" class="button" type="button" id="download-button">
                </div>
            </form>
			<div>
				<label class="text" style="margin-right: 30px;">Phòng ban</label> <input type="text" onfocus="publicMenu()" id="item" placeholder="Lựa chọn" readonly="">
				<form style="display: none" id="menuCheckbox">
					<input type="checkbox" onclick="selectAll()" class="checkboxes" id="answer1"> <label for="answer1" class="checkboxes">--ALL--</label><br>
					<input type="checkbox" onclick="check()" class="checkboxes" id="answer2"> <label for="answer2" class="checkboxes">Phòng
						HN.MG15</label><br> <input type="checkbox" onclick="check()" class="checkboxes" id="answer3"> <label for="answer3" class="checkboxes">Phòng HN.MG26</label><br> <input type="checkbox" onclick="check()" class="checkboxes" id="answer4">
					<label for="answer4" class="checkboxes">Phòng HN.MG8</label><br>
					<input type="checkbox" onclick="check()" class="checkboxes" id="answer5"> <label for="answer5" class="checkboxes">Phòng
						HCM.MG1</label><br> <input type="checkbox" onclick="check()" class="checkboxes" id="answer6"> <label for="answer6" class="checkboxes">Phòng HCM.MG6</label><br>
				</form>

			</div>
		</div>


    </div>
    <div id="myModal" class="modal">
        <div class="modal-content" >
            <div style="background-color:white">
          <span class="close" style="background-color: white;">x</span></div>
          <p style="background-color: white;">Vui lòng nhập lại ngày</p>
        </div>
    </div>

	<script>
    let all = document.getElementById("all");
    let checkBox = document.getElementById("menuCheckbox");
    let allCheckBox = document.getElementsByClassName("checkboxes");
    let modal = document.getElementById("myModal");
    let span = document.getElementsByClassName("close")[0];

    function setAndCheckDay(){
            let firstDay = document.getElementById("first-day")
            let lastDay = document.getElementById("last-day")
            let valueOfFirstDay = firstDay.value;
            let valueOfLastDay = lastDay.value;
            console.log(valueOfFirstDay)
            if(valueOfFirstDay !== "" && valueOfLastDay !== ""){
                if(Date.parse(valueOfLastDay) - Date.parse(valueOfFirstDay) > 2592000000 || Date.parse(valueOfLastDay) < Date.parse(valueOfFirstDay) ){
                    modal.style.display = "block";
                    firstDay.value = "";
                    lastDay.value = "";
                }
            }
    }
    span.onclick = function() {
            modal.style.display = "none";
        }  

    window.onclick = function(event) {
        if (event.target == modal) {
                modal.style.display = "none";
        }
    }

  
  function resetData(){
      let firstDay = document.getElementById("first-day")
      let lastDay = document.getElementById("last-day")
      for(let count = 0; count < allCheckBox.length; count += 2){
              allCheckBox[count].checked =false;
      }
      firstDay.value = "" 
      lastDay.value = ""
  }

  function formatNumber(){
      input = document.getElementById("stk"); 
      const {value} = input;
      let temp = input.value.replace(/$\s?|(,*)/g, "")
      temp = parseInt(temp, 10);
      temp = temp.toString();
      let lengthTemp = temp.length;
      input.value = temp.replace(/\D/g, "")
  }

  function check(){
      for(let count = 2; count < allCheckBox.length; count += 2){
          if( allCheckBox[count].checked === false){
              allCheckBox[0].checked =false;
              break;
          }

          if(count == allCheckBox.length - 2){
              allCheckBox[0].checked = true;
          }
      }

  }

  document.onclick = function (e) {
      if (e.target.id !== "item" 
      		&& e.target.className !== "checkboxes" 
      		&& e.target.id !== "menuCheckbox"){
          checkBox.style.display = "none"
      }
  }

  function publicMenu(){
      checkBox.style.display = "block";
  }
  
  function selectAll(){
      let item = document.getElementById("item");

      if (allCheckBox[0].checked === false){
          for(let count = 0; count < allCheckBox.length; count += 2){
              allCheckBox[count].checked = false;
          }   
      } else {
          for(let count = 0; count < allCheckBox.length; count += 2){
              allCheckBox[count].checked = true;
          }
      }

  }
  
  window.onload = function(){
	    let firstDay = document.getElementById("first-day");
	    let lastDay = document.getElementById("last-day");   
	    let time = new Date();
	    if  (time.getDate().toString().length == 1 ){
	        firstDay.max = (time.getFullYear()).toString() +'-'+ (time.getMonth() + 1).toString() +'-0'+ (time.getDate()-1).toString();
	        lastDay.max = (time.getFullYear()).toString() +'-'+ (time.getMonth() + 1).toString() +'-0'+ (time.getDate()-1).toString();
	    } else {
	        firstDay.max = (time.getFullYear()).toString() +'-'+ (time.getMonth() + 1).toString() +'-'+ (time.getDate()-1).toString();
	        lastDay.max = (time.getFullYear()).toString() +'-'+ (time.getMonth() + 1).toString() +'-'+ (time.getDate()-1).toString();
	    }
	    console.log(firstDay);
	    console.log(lastDay);
	}
</script>
</tiles:insertAttribute>
