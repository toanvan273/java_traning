$.urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    }
    return decodeURI(results[1]) || 0;
}

let firstDay = $.urlParam('firstDay');
let lastDay = $.urlParam('lastDay');
let hrcode = $.urlParam('hrcode');
let product = $.urlParam('product');
let acctno = $.urlParam('acctno');
// manager
let totalPopupManager=""
let accountNo;
let groupId;
let saleHrCode;

//function open popup 
function installPopupListener(component) {
    jQuery(component).on('click', function (e) {
        e.preventDefault();
        let href = window.location.href
        if (href.search("daily-manager-report") !== -1) {
            if (e.currentTarget.parentNode.childNodes[5].textContent.trim() !== 'TỔNG') {
                groupId = e.currentTarget.parentNode.getElementsByClassName("group")[0].childNodes[1].textContent;
            }else {totalPopupManager=true}
        }
        let className = $(this).attr('class');
        let role = window.location.pathname;
        accountNo = $(this).closest("tr").find(".popup-link").text();
        jQuery(component).attr("data-toggle", "modal");
        jQuery(component).attr("data-target", "#reportPopupModel");

        if (role === "/daily-sale-report/staff-view") {
            if ($(this).text().trim() === '0') {
                jQuery(component).removeAttr("data-toggle");
                jQuery(component).removeAttr("data-target");
            } else if (className === 'popup-portfolio' || className === 'popup-total-portfolio') {
                loadPopupDetail(accountNo, className, "Tổng giá trị Danh mục chứng khoán cơ sở cuối kỳ báo cáo theo tài khoản", "account-portfolio-view");
            } else if (className === 'popup-average-nav' || className === 'popup-total-average-nav') {
                loadPopupDetail(accountNo, className, "NAV trung bình trong kỳ báo cáo theo tài khoản", "average-nav-detail-view");
            } else if (className === 'popup-average-aum' || className === 'popup-total-average-aum') {
                loadPopupDetail(accountNo, className, "AUM trung bình trong kỳ báo cáo theo tài khoản", "average-aum-detail-view");
            } else if (className === 'popup-end-period-nav' || className === 'popup-total-end-period-nav') {
                loadPopupDetail(accountNo, className, "NAV cuối kỳ báo cáo theo tài khoản", "nav-detail-view");
            } else if (className === 'popup-end-period-aum' || className === 'popup-total-end-period-aum') {
                loadPopupDetail(accountNo, className, "AUM cuối kỳ báo cáo theo tài khoản", "aum-detail-view");
            } else if (className === 'popup-nsr' || className === 'nsr-total-detail-view') {
                loadPopupDetail(accountNo, className, "Tổng NSR trong kỳ báo cáo theo tài khoản", "nsr-detail-view");
            } else {
                jQuery(component).removeAttr("data-toggle");
                jQuery(component).removeAttr("data-target");
            }
        } else if (role === "/daily-manager-report/manager-view") {
            if ($(this).text().trim() === '0') {
                jQuery(component).removeAttr("data-toggle");
                jQuery(component).removeAttr("data-target");
            } else if (className === 'popup-average-nav' || className === 'popup-total-average-nav') {
                loadPopupDetail(accountNo, className, "NAV trung bình trong kỳ báo cáo theo nhân viên", "average-nav-detail-view");
            } else if (className === 'popup-average-aum' || className === 'popup-total-average-aum') {
                loadPopupDetail(accountNo, className, "AUM trung bình trong kỳ báo cáo theo nhân viên", "average-aum-detail-view");
            } else if (className === 'popup-end-period-nav' || className === 'popup-total-end-period-nav') {
                loadPopupDetail(accountNo, className, "NAV cuối kỳ báo cáo theo nhân viên", "nav-detail-view");
            } else if (className === 'popup-end-period-aum' || className === 'popup-total-end-period-aum') {
                loadPopupDetail(accountNo, className, "AUM cuối kỳ báo cáo theo nhân viên", "aum-detail-view");
            } else if (className === 'popup-nsr' || className === 'nsr-total-detail-view') {
                loadPopupDetail(accountNo, className, "Tổng NSR trong kỳ báo cáo theo nhân viên", "nsr-detail-view");
            } else {
                jQuery(component).removeAttr("data-toggle");
                jQuery(component).removeAttr("data-target");
            }
        }
    });
}


//function create content of popup
function loadPopupDetail(accountNo, className, title, context) {
    if (product == 0) {
        product = '';
    }
    jQuery("#popup-status").text("");
    jQuery(".modal-body").html("");
    jQuery(".modal").attr("style", "display:block");
    jQuery(".modal-text").text(title);
    jQuery(".loader").attr("style", "display:block");
    let arr = className.split("-");
    let url = '';
    const role = window.location.pathname;
    switch (role) {
        case '/daily-sale-report/staff-view':
            if (arr[1] != 'total') {
                url = '/daily-sale-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&acctno=' + accountNo + '&product=' + product;
            } else {
                (acctno == 0) ? (url = '/daily-sale-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&product=' + product) : (url = '/daily-sale-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&acctno=' + acctno + '&product=' + product);
            }
            break
        case '/daily-manager-report/manager-view':
            saleHrCode = accountNo.slice(accountNo.indexOf("-") + 1, accountNo.length).trim()
            let allGroupId = localStorage.getItem('groupId');
            if (saleHrCode.length > 0) {
                url = '/daily-manager-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&saleHrCode=' + saleHrCode + '&groupId=' + groupId;
            } else {
                let saleHrCodes = localStorage.getItem('saleHrCodes');
                url = '/daily-manager-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&saleHrCode=' + saleHrCodes + '&groupId=' + allGroupId;
            }
            break
        default:
            break
    }
    jQuery(".modal-body").load(url, function (response, status, xhr) {
        if (xhr.status != '200') {
            console.log("check");
            jQuery(".modal").attr("style", "display:none");
            jQuery(".modalError").attr("style", "display:block");
        }

        console.log(xhr.status);
    });
};

// function to detach url
function queryUrlAndDownloadExcel() {
    if ($(".accNo-popup-portfolio")[0]) {
        downloadStaff('account-portfolio-view');
    } else if ($(".accNo-popup-aum-detail")[0]) {
        downloadStaff('aum-detail-view');
    } else if ($(".accNo-popup-average-aum")[0]) {
        downloadStaff('average-aum-detail-view');
    } else if ($(".accNo-popup-average-nav")[0]) {
        downloadStaff('average-nav-detail-view');
    } else if ($(".accNo-popup-nav-detail")[0]) {
        downloadStaff('nav-detail-view');
    } else if ($(".accNo-popup-nsr-detail")[0]) {
        downloadStaff('nsr-detail-view');
    } else if ($(".manager-popup-aum-detail")[0]) {
        downloadManager('aum-detail-view');
    } else if ($(".manager-popup-aum-average")[0]) {
        downloadManager('average-aum-detail-view');
    } else if ($(".manager-popup-nav-average")[0]) {
        downloadManager('average-nav-detail-view');
    } else if ($(".manager-popup-nav-detail")[0]) {
        downloadManager('nav-detail-view');
    } else if ($(".manager-popup-nsr-detail")[0]) {
        downloadManager('nsr-detail-view');
    }
}

// function to download excel
function downloadStaff(context) {
    let urlDownload = "";
    if (product == 0) {
        product = '';
    }
    if (accountNo != "") {
        urlDownload = "/daily-sale-report/" + context + '?firstDay=' + firstDay + "&lastDay=" + lastDay + '&acctno=' + accountNo + '&product=' + product + '&type=excel';
    } else {
        // urlDownload = '/daily-sale-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&product=' + product + '&type=excel&total=true'
        (acctno == 0) ? (urlDownload = '/daily-sale-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&product=' + product + '&type=excel&total=true') :
        (urlDownload = '/daily-sale-report/' + context + '?firstDay=' + firstDay + '&lastDay=' + lastDay + '&acctno=' + acctno + '&product=' + product + '&type=excel&total=true');
    }
    console.log(urlDownload)
    window.open(urlDownload);
}

let downloadManager = (context) => {
    let urlDownload
    if (saleHrCode.length > 0) {
        urlDownload = "/daily-manager-report/" + context + '?firstDay=' + firstDay + "&lastDay=" + lastDay + '&saleHrCode=' + saleHrCode + '&groupId=' + groupId + '&type=excel';
    } else {
        let allGroupId = localStorage.getItem('groupId');
        let saleHrCodes = localStorage.getItem('saleHrCodes');
        console.log("ABC",totalPopupManager)
        urlDownload = "/daily-manager-report/" + context + '?firstDay=' + firstDay + "&lastDay=" + lastDay + '&saleHrCode=' + saleHrCodes +'&total='+totalPopupManager+ '&groupId=' + allGroupId + '&type=excel';
    }
    window.open(urlDownload)
}


// show tooltip
function installTooltipListener(component) {
    jQuery(component).tooltip({
        delay: 500,
        placement: "bottom",
        title: showProducts,
        html: true
    });
}

function showProducts() {
    let accountNo = $(this).closest("tr").find(".popup-link").text();
    var url = '/daily-sale-report/show-product?firstDay=' + firstDay + '&lastDay=' + lastDay + '&acctno=' + accountNo + '&hrcode=' + hrcode + '&product=' + product;
    var tooltipText = "";
    $.ajax({
        url: url,
        type: 'get',
        async: false,
        success: function (response) {
            tooltipText = response;
        }
    });
    return tooltipText;
};
