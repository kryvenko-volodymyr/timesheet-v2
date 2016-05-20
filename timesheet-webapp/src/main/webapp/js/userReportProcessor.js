var ROW_ALREADY_EXISTS_ERROR = "Такий рядок вже є у вашому звіті.\n\n" +
    "Дублювання рядків не дозволяється.\n" +
    "Будь-ласка, використовуйте інсуючий рядок.";
var UNDOALL_CONFIRM_PROMT = "Скасувати усі зміни, що не були збережені на сервері?";
var REPORT_POST_SUCESS = "Звіт було успішно збережено на сервері";
var REPORT_POST_500 = "При збереженні звіту сталася помилка сервера.\n" +
    "Звіт не було збережено.\n" +
    "Будь ласка, не закривайте вікно та зверніться до адміністратора системи.";
var REPORT_POST_403 = "Доступ до сервера заборонено.\n" +
    "Звіт не було збережено.\n" +
    "Будь ласка, не закривайте вікно та зверніться до адміністратора системи.";

function populateUserReportTalbe() {
    var userReportRecordsLine;

    $("#user_report_table").append("<tr id = 'user_report_table_dates'><td id = 'dates_empty_cell'></td></tr>");
    for (columnDate = new Date(userReport.date_from);
         columnDate <= userReport.date_to;
         columnDate.setDate(columnDate.getDate() + 1)) {
        var optionsLine1 = {weekday: 'short'};
        var optionsLine2 = {day: 'numeric'};
        var optionsLine3 = {month: 'short'};

        $("#user_report_table_dates").append("<td id ='" +
            "user_report_table_dates_" + columnDate.getTime() + "'>" +
            "<p class='report_record_weekday'>" +
            columnDate.toLocaleDateString("uk-ua", optionsLine1) +
            "</p><p class='report_record_monthday'>" +
            columnDate.toLocaleDateString("uk-ua", optionsLine2) +
            "</p><p class='report_record_month'>" +
            columnDate.toLocaleDateString("uk-ua", optionsLine3) +
            "</p></td>");
    }

    $.each(userReport, function (wi_key, userReportRow) {
        if (wi_key != "date_from" && wi_key != "date_to") {
            userReportRecordsLine = new Array();
            $("#user_report_table").append("<tr id = '" + wi_key + "'>" +
                "<td class = 'user_report_row_head'>" +
                "<br>" +
                /*"<p class='work_type_title'>" +
                userReportRow.work_instance.work_type_title +*/
                "</p><p class='work_title_title'>" +
                userReportRow.work_instance.work_title_title +
                "</p><p class='work_form_title'>" +
                userReportRow.work_instance.work_form_title +
                "</p><p class='work_instance_details'>" +
                userReportRow.work_instance.details +
                "</p></td></tr>")
        }

        $.each(userReportRow, function (rr_key, reportRecord) {
            if (rr_key != "work_instance") {
                userReportRecordsLine.push(reportRecord);
            }
        });

        if (wi_key != "date_from" && wi_key != "date_to") {
            userReportRecordsLine.sort(function (a, b) {
                return new Date(a.date_reported) - new Date(b.date_reported);
            });
        }

        $.each(userReportRecordsLine, function (indexArray, reportRecord) {
            $("#" + wi_key).append(
                "<td id ='" + wi_key + "_rr" + reportRecord.date_reported.getTime() + "'>" +
                "<input " +
                "type = number class = 'report_record_hours_num_input' " +
                "id = 'input_" + wi_key + "_rr" + reportRecord.date_reported.getTime() + "' " +
                "value='" + reportRecord.hours_num + "' " +
                "min='0' max='24'" +
                ">" +
                "</td>")
        })
    });
}

function prepareDataInput() {
    $("#user_report_table").on("focusout", ".report_record_hours_num_input", function () {
        var wi = (this.id).slice(6, -16);
        var rr = (this.id).slice(-15);
        var hoursNum = parseInt(this.value, 10);
        if (isNaN(hoursNum)) {
            userReport[wi][rr].hours_num = 0;
        } else {
            userReport[wi][rr].hours_num = hoursNum;
        }
    });
    $("#user_report_table").on("click", function () {
        $("#post_user_report").removeAttr('disabled');
    });
}

function undoUnsavedChanges() {

    if (confirm(UNDOALL_CONFIRM_PROMT) == true) {
        $("#user_report_table").empty();
        $.getJSON("user-report", JSONToUserReport).done(populateUserReportTalbe());
    }
}

function postUserReport() {
    $.ajax({
        type: "POST",
        url: "user-report",
        data: {
            user_report: JSON.stringify(userReport)
        },
        statusCode: {
            200: function () {
                alert(REPORT_POST_SUCESS);
                $("#post_user_report").attr('disabled', 'disabled');
            },
            500: function () {
                alert(REPORT_POST_500);
            },
            403: function () {
                alert(REPORT_POST_403);
            }
        },
        dataType: "text/html"
    });
}

function prepareAddingUserReportRow() {
    $.getJSON("work-types", JSONToList).done(function () {
        $("#work_type_select").empty();
        $("#work_title_select").hide();
        $("#work_form_select").hide();
        $("#work_instance_details_input").hide();
        $("#add_user_report_row_button").hide();
        $("#work_type_select")
            .append("<option value='0' disabled selected>Додати рядок</option>");
        $.each(tempList, function (indexInArray, value) {
            $("#work_type_select")
                .append("<option value=\"" + value.id + "\">" + value.title + "</option>");
        });
    })
}

function getWorkTitles() {
    var workTypeid = $("#work_type_select").val();

    $.getJSON("work-titles?work-type-id=" + workTypeid, JSONToList).done(function () {
        $("#work_type_select option[value='0']").remove();
        $("#work_title_select").empty();
        $("#work_title_select").show();
        $("#work_form_select").hide();
        $("#work_instance_details_input").hide();
        $("#add_user_report_row_button").hide();
        $("#work_title_select")
            .append("<option value='0' disabled selected>Оберіть назву</option>");
        $.each(tempList, function (indexInArray, value) {
            $("#work_title_select")
                .append("<option value=\"" + value.id + "\">" + value.title + "</option>");
        });
    })
}

function getWorkForms() {
    var workTypeid = $("#work_type_select").val();

    $.getJSON("work-forms?work-type-id=" + workTypeid, JSONToList).done(function () {
        $("#work_title_select option[value='0']").remove();
        $("#work_form_select").empty();
        $("#work_form_select").show();
        $("#work_instance_details_input").hide();
        $("#add_user_report_row_button").hide();
        $("#work_form_select")
            .append("<option value='0' disabled selected>Оберіть вид роботи</option>");
        $.each(tempList, function (indexInArray, value) {
            $("#work_form_select")
                .append("<option value=\"" + value.id + "\">" + value.title + "</option>");
        });
    })
}

function getWorkInstanceDetails() {
    $("#work_form_select option[value='0']").remove();
    var workTitleId = $("#work_title_select").val();
    var workFormId = $("#work_form_select").val();

    $.getJSON("work-instances?work-title-id=" + workTitleId + "&work-form-id=" + workFormId, JSONToList).done(function () {
        $("#work_instance_datalist").empty();
        $("#work_instance_details_input").show();
        $("#add_user_report_row_button").show();
        $.each(tempList, function (indexInArray, value) {
            if (value.details != "") {
                $("#work_instance_datalist")
                    .prepend("<option value=\'" + value.details + "\' id=\'" + value.id + "\'>");
            }
        });
    })
}

function addUserReportRow() {
    var workInstanceDetails = $("#work_instance_details_input").val();

    var foundWIsWithoutDetails = $.grep(tempList, function (workInstance) {
        return workInstance.details == workInstanceDetails;
    });

    if (foundWIsWithoutDetails.length) {
        foundWIsWithoutDetails.sort(function (a, b) {
            return b.id - a.id;
        });
        addUserReportRowIfWI(foundWIsWithoutDetails[0]);
    } else {
        addUserReportRowWhereNOWi(workInstanceDetails);
    }
    getWorkInstanceDetails();
}

function addUserReportRowIfWI(workInstance) {
    if ($("#wi" + workInstance.id).length) {      //TODO change to search in userReport obj instead of table
        alert(ROW_ALREADY_EXISTS_ERROR);
        return;
    }

    $("#user_report_table").append("<tr id = 'wi" + workInstance.id + "'>" +
        "<td class = 'user_report_row_head'>" +
        "<br>" +
        "<p class='work_type_title'>" +
        workInstance.work_type_title +
        "</p><p class='work_title_title'>" +
        workInstance.work_title_title +
        "</p><p class='work_form_title'>" +
        workInstance.work_form_title +
        "</p><p class='work_instance_details'>" +
        workInstance.details +
        "</p></td></tr>");
    for (columnDate = new Date(userReport.date_from);
         columnDate <= userReport.date_to;
         columnDate.setDate(columnDate.getDate() + 1)) {
        $("#wi" + workInstance.id).append(
            "<td id ='wi" + workInstance.id + "_rr" + columnDate.getTime() + "'>" +
            "<input " +
            "type = number class = 'report_record_hours_num_input' " +
            "id = 'input_wi" + workInstance.id + "_rr" + columnDate.getTime() + "' " +
            "value='" + 0 + "' " +
            "min='0' max='24'" +
            ">" +
            "</td>");
    }
    newRowToUserReporObj(workInstance);
}

function newRowToUserReporObj(workInstance) {
    userReport["wi" + workInstance.id] = {};
    userReport["wi" + workInstance.id]["work_instance"] = workInstance;
    for (columnDate = new Date(userReport.date_from);
         columnDate <= userReport.date_to;
         columnDate.setDate(columnDate.getDate() + 1)) {
        userReport["wi" + workInstance.id]["rr" + columnDate.getTime()] = {
            date_reported: columnDate.getTime(),
            hours_num: 0,
            work_instance_id: workInstance.id
        };
    }
}

function addUserReportRowWhereNOWi(workInstanceDetails) {

    $.ajax({
        type: "POST",
        url: "work-instances",
        data: {
            work_title_id: $("#work_title_select").val(),
            work_form_id: $("#work_form_select").val(),
            details: workInstanceDetails
        },
        success: function (workInstance) {          //TODO add status 200 check
            addUserReportRowIfWI(workInstance);
        },
        dataType: "json"
    });
}