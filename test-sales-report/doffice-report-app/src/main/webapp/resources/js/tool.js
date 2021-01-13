let activeTab
// Tool
function auformatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;
    return [day, month, year].join('/');
}

// Vinh làm active menu ở đây =============
// let checkTabActive=key=>{
//     console.log("checkTabActive",key)
//     document.getElementById("main-page").style.backgroundColor = "#ffa900";
//     // window.location
// }

// let checkMainPage = () => {
//     localStorage.setItem("checkMainPageActive", true)
// }
// let checkSubPage=()=>{
//     localStorage.setItem("checkMainPageActive", false)
// }
// console.log('check',localStorage.getItem("checkMainPageActive"))
// if(localStorage.getItem("checkMainPageActive")===null){
//     localStorage.setItem("checkMainPageActive", false)
//     document.getElementById("main-page").style.backgroundColor = "#ffa900";
// }else {
//    let boolMainPage = JSON.parse(localStorage.getItem("checkMainPageActive"))
//     if(boolMainPage){
//         document.getElementById("main-page").style.backgroundColor = "#ffa900";
//     }else {
//         localStorage.setItem("checkMainPageActive", false)
//         let checkPathName = window.location.pathname;
//         if( checkPathName.search("staff") !== -1 ){
//             document.getElementById("staff-page").style.backgroundColor = "#ffa900";
//         }
//         if(checkPathName.search("manager") !== -1){
//             document.getElementById("manager-page").style.backgroundColor = "#ffa900";
//         }
//         if(checkPathName.search("director") !== -1){
//             document.getElementById("director-page").style.backgroundColor = "#ffa900";
//         }
//     }
// }
//-------------------
let checkPathName = window.location.pathname;
if( checkPathName.search("staff") !== -1 ){
    document.getElementById("staff-page").style.backgroundColor = "#ffa900";
}
if(checkPathName.search("manager") !== -1){
    document.getElementById("manager-page").style.backgroundColor = "#ffa900";
}
if(checkPathName.search("director") !== -1){
    document.getElementById("director-page").style.backgroundColor = "#ffa900";
}



//-------------------