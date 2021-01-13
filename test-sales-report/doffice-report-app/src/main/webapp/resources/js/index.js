$(function () {

    var successOptions = {
        autoHideDelay: 1500,
        showAnimation: "fadeIn",
        hideAnimation: "fadeOut",
        hideDuration: 700,
        arrowShow: false,
        className: "success",
    };

    var errorOptions = {
        autoHideDelay: 1500,
        showAnimation: "fadeIn",
        hideAnimation: "fadeOut",
        hideDuration: 700,
        arrowShow: false,
        className: "error",
    };

    function confirmDialog(message, yesFunction, noFunction) {
        $('<div class="confirm-box"></div>').appendTo('body')
            .html('<div><h6>' + message + '?</h6></div>')
            .dialog({
                modal: true,
                title: 'Cảnh báo',
                zIndex: 10000,
                autoOpen: true,
                width: 'auto',
                resizable: false,
                buttons: {
                    Yes: function() {
                        yesFunction();
                        $(".confirm-box").dialog("close");
                    },
                    No: function() {
                        noFunction();
                        $(".confirm-box").dialog("close");
                    }
                },
                close: function(event, ui) {
                    $(".confirm-box").remove();
                }
            });
    };

    function submitCall(submitForm) {
        var callType = submitForm.find('input:first').val();
        var url = submitForm.attr('action');
        var type = submitForm.attr('method');
        $.ajax({
            method: type,
            url: url + "?callType=" + callType,
            processData: false,
            success: function (success) {
                console.log(callType);
                $.notify(success, successOptions);
            },
            error: function (error) {
                console.log(error);
                $.notify(error.responseJSON.message, errorOptions);
            }
        });
        submitForm.trigger('reset');
    }

    function rejectCall(rejectForm) {
        rejectForm.trigger('reset');
    };

    // upload

    $("#upload-margin-form, #upload-margin-high-mr-form, #upload-rtt-form, #upload-rtt-handle-form, #upload-fds-margin-form, #upload-deposit-form, #upload-fds-external-director-form, " +
        "#upload-margin-external-broker-form, #upload-margin-external-manager-form, #upload-rtt-external-broker-form, #upload-rtt-external-manager-form, #upload-fds-external-broker-form, #upload-fds-external-manager-form, #upload-deposit-external-broker-form, #upload-deposit-external-manager-form, " +
        "#upload-sss-form, " +
        "#upload-ccq-form, " +
        "#upload-order-bo-form, #upload-order-fds-form, " +
        "#upload-missing-order-1-bo-form, #upload-missing-order-1-fds-form, #upload-missing-order-2-bo-form, #upload-missing-order-2-fds-form," +
        "#upload-ca-profit-date-form, #upload-ca-profit-done-form, #upload-ca-pay-date-form, #upload-ca-cetificate-form, #upload-ca-pay-done-form, #upload-ca-auction-result-form, #upload-ca-noti-ipo-result-form, #upload-ca-white-label-form, " +
        "#upload-reject-order-form, " +
        "#upload-broker-quitjob-form, " +
        "#upload-salary-botimebroker-form, #upload-salary-botimemanagerbroker-form, #upload-salary-fdstimemanagerbroker-form, #upload-salary-timecommissionmanagerbroker-form, " +
        "#upload-manualmessage-msmflexible-form, " +
        "#upload-ignore-symbol-form"
    ).submit(function (e) {
        e.preventDefault();
        var form = $(this);
        var fileInput = $(this).find('input[type=file]').get(0);
        if (fileInput !== undefined && fileInput !== null && fileInput.files.length > 0) {
            if (window.confirm("Process upload?")) {
                var url = form.attr('action');
                var type = form.attr('method');
                var fileForm = new FormData($(this)[0])
                $.ajax({
                    method: type,
                    url: url,
                    data: fileForm,
                    processData: false,
                    contentType: false,
                    success: function (success) {
                        $.notify(success, successOptions);
                    },
                    error: function (error) {
                        console.log(error);
                        $.notify(error.responseJSON.message, errorOptions);
                    },
                });
                $(this).trigger('reset');
            } else {
                $(this).trigger('reset');
            }
        } else {
            alert("No file chosen!");
        }
    });

    $("#upload-adjust-stock, " +
        "#upload-manualmessage-emailflexible-form, " +
        "#upload-compare-form"
    ).submit(function (e) {
        e.preventDefault();
        var form = $(this);
        var fileInput = $(this).find('input[type=file]');
        if (fileInput !== undefined && fileInput !== null) {
            if (window.confirm("Process Upload?")) {
                var url = form.attr('action');
                var type = form.attr('method');
                var fileForm = new FormData();
                $.each($(this).find('input[type=file]'), function (i, file) {
                    fileForm.append("file" + i, file.files[0]);
                });
                $.ajax({
                    method: type,
                    url: url,
                    data: fileForm,
                    processData: false,
                    contentType: false,
                    success: function (success) {
                        $.notify(success, successOptions);
                    },
                    error: function (error) {
                        console.log(error);
                        $.notify(error.responseJSON.message, errorOptions);
                    }
                });
                $(this).trigger('reset');

            } else {
                $(this).trigger('reset');
            }
        } else {
            alert("No file chosen!");
        }
    });

    // action

    $("#call-margin-form, #call-rtt-form, #call-fds-margin-form, #call-deposit-form, #call-adjust-stock, " +
        "#call-sss-form, " +
        "#call-ccq-form, " +
        "#call-bo-missing-order-first-time-form, #call-fds-missing-order-first-time-form, #call-bo-missing-order-second-time-form, #call-fds-missing-order-second-time-form," +
        "#call-ca-profit-date-form, #call-ca-profit-done-form, #call-ca-pay-date-form, #call-ca-cetificate-form, #call-ca-pay-done-form, #call-ca-auction-result-form, #call-ca-noti-ipo-result-form, " +
        "#call-reject-order-form, " +
        "#call-broker-quitjob-form, " +
        "#call-salary-botimebroker-form, #call-salary-botimemanagerbroker-form, #call-salary-fdstimemanagerbroker-form, #call-salary-timecommissionmanagerbroker-form, " +
        "#call-manualmessage-msmflexible-form, #call-manualmessage-emailflexible-form, " +
        "#call-compare-excel-form"
    ).submit(function (e) {
        e.preventDefault();
        var form = $(this);

        if (window.confirm("Process call?")) {
            submitCall(form);
        } else
            rejectCall(form);
    });

    $("#call-statement-form")
        .submit(function (e) {
            e.preventDefault();
            var form = $(this);
            var currentdate = new Date();
            currentdate.setDate(1);
            var message = "Sẽ sử dụng dữ liệu sao kê của tháng " + currentdate.getMonth() + "/" + currentdate.getFullYear() + " ,bạn có muốn thực hiện?";

            confirmDialog(message,
                function () {
                    submitCall(form)
                }, function () {
                    rejectCall(form)
                })
        });

    $.datepicker._gotoToday = function(id) {
        var target = $(id);
        var inst = this._getInst(target[0]);
        if (this._get(inst, 'gotoCurrent') && inst.currentDay) {
            inst.selectedDay = inst.currentDay;
            inst.drawMonth = inst.selectedMonth = inst.currentMonth;
            inst.drawYear = inst.selectedYear = inst.currentYear;
        }
        else {
            var date = new Date();
            inst.selectedDay = date.getDate();
            inst.drawMonth = inst.selectedMonth = date.getMonth();
            inst.drawYear = inst.selectedYear = date.getFullYear();
            // the below two lines are new
            this._setDateDatepicker(target, date);
            this._selectDate(id, this._getDateDatepicker(target));
        }
        this._notifyChange(inst);
        this._adjustDate(target);
    }

    $(".datepicker").datepicker({
        changeMonth: true,
        changeYear: true,
        showButtonPanel: true,
        dateFormat: "dd/mm/yy"
    });

    first();
})

function first() {

    // Hide submenus
    $('#body-row .collapse').collapse('hide');

// Collapse/Expand icon
    $('#collapse-icon').addClass('fa-angle-double-left');

// Collapse click
    $('[data-toggle=sidebar-colapse]').click(function () {
        SidebarCollapse();
    });

    function SidebarCollapse() {
        $('.menu-collapsed').toggleClass('d-none');
        $('.sidebar-submenu').toggleClass('d-none');
        $('.submenu-icon').toggleClass('d-none');
        $('#sidebar-container').toggleClass('sidebar-expanded sidebar-collapsed');

        // Treating d-flex/d-none on separators with title
        var SeparatorTitle = $('.sidebar-separator-title');
        if (SeparatorTitle.hasClass('d-flex')) {
            SeparatorTitle.removeClass('d-flex');
        } else {
            SeparatorTitle.addClass('d-flex');
        }

        // Collapse/Expand icon
        $('#collapse-icon').toggleClass('fa-angle-double-left fa-angle-double-right');
    }
}

