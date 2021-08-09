const HYN_BASIC_NAMESPACE = {
    "FB_STATE_UNDECIED": {
        "dbCode": 0
    },
    "ENUM_UNDECIED": {
        "dbCode": 0
    },
    "FB_BILL_STATE_UNDECIED": {
        "dbCode": 0
    },
    "FEE_RESPONSE_STATE_OF_UNDECIED": {
        "dbCode": 0
    },
    "FEE_RESPONSE_STATE_OF_CUSTOMERS": {
        "dbCode": 1
    },
    "FEE_RESPONSE_STATE_OF_OURS": {
        "dbCode": 2
    },
    "INVENTION": {
        "dbCode": 1
    },
    "AF": {
        "name": "年费",
        "dbCode": 3,
        "alias": "年费"
    },
    "STAFF_MES_FOR_ZERO_STAFF_ID": "系统自动处理",
    "PROCESS_JOB_CODE": 3,
    "TECH_JOB_CODE": 1,
    "SALE_JOB_CODE": 2
}


$(document).ajaxSuccess(function(interval, data, settings) {
    var dataXml = data.responseXML;
    var currentTime = $(dataXml).find("time");
    if (currentTime.length == 0) {
        $("#frequency").val(0);
    }
});


function getFeeTypeName(fee) {
    return fee.type.name == "年费" ? "第" + fee.patentYearNum + "年年费" : fee.type.name
}

function isSF(fee) {
    return fee.type.dbCode == 0;
}

function getCSSStatementForColorAndBackgroudColor(backgroundColor) {
    return "background-color:" + backgroundColor + ";" + "color :" + parseColor(backgroundColor).getContrastColor();
}


function copyToClipboard(text, $container) {
    $container.text(text).get(0).select();
    let isSuccessful = document.execCommand("copy");
    if (!isSuccessful) {
        showAlertMessage("严重BUG:粘贴功能失效");
    }
}


function initControlgroupByJqueryUI($container, defaultSelectedIndex) {
    $container.find("input").checkboxradio({ icon: false }).end().controlgroup();

    if (defaultSelectedIndex != undefined) {
        $container.find("input").eq(defaultSelectedIndex).prop("checked", true).end().end().controlgroup("refresh");
    }
}



let nums = [];
a.find(()=>{


})


let a = function(){

}
function isEven(num){
    return num%2 == 0;
}


let nums = [1,2,3,4,5,6,7,8,9,10]
/*1,2,3,4,5,6,7,8,9,10 是否 都是偶数*/
nums.every(num => isEven(num));
/*1,2,3,4,5,6,7,8,9,10 是否 存在一个偶数*/
nums.some(num => isEven(num));


Array.prototype.noneMatch = function(predicate){
    return !this.some(predicate)
}
/*1,2,3,4,5,6,7,8,9,10 是否 都不是偶数*/
nums.noneMatch(num => isEven(num))



function parseColor(color) {
    const DARK = "black";
    const LIGHT = "white";
    let isError = false;
    let R = 0;
    let G = 0;
    let B = 0;
    if (matchRGB(color)) {
        let rgb = color.replace("(", "");
        rgb = rgb.replace("rgb", "");
        rgb = rgb.replace(")", "");
        rgb = rgb.split(",");
        R = parseInt(rgb[0]);
        G = parseInt(rgb[1]);
        B = parseInt(rgb[2]);

    } else if (matchHexdecimalValue(color)) {
        let resStr = color.substr(1);
        let resArr = spiltArr(resStr);
        let rgb = toTen(resArr);
        R = parseInt(rgb[0]);
        G = parseInt(rgb[1]);
        B = parseInt(rgb[2]);
    } else {
        isError = true;
    }

    function matchRGB(color) {
        return color.indexOf("rgb") == 0;
    }

    function matchHexdecimalValue(color) {
        let reg = new RegExp(/^#([0-9a-fA-F]){3,6}$/);
        return color.match(reg);
    }

    function toTen(arr) {
        var rgb = [];
        for (var i = 0; i < 3; i++) {
            rgb.push(parseInt(arr[i], 16));
        }
        return rgb;
    }

    function spiltArr(str) {
        var tmp = ['', '', ''];
        var strLen = str.length;
        if (strLen == 3) {
            var a = str.substr(0, 1);
            var b = str.substr(1, 1);
            var c = str.substr(2, 1);
            tmp[0] = a + a;
            tmp[1] = b + b;
            tmp[2] = c + c;
            return tmp;
        } else if (strLen == 6) {
            tmp[0] = str.substr(0, 2);
            tmp[1] = str.substr(2, 2);
            tmp[2] = str.substr(4, 2);
            return tmp;
        }
    }

    function calculateColorBrightness(colorObj) {
        if (colorObj.isError)
            return 128;

        let lightness = colorObj.R * 0.299 + colorObj.G * 0.587 + colorObj.B * 0.114;
        return lightness;
    }

    return {
        "isError": isError,
        "R": R,
        "G": G,
        "B": B,
        "getRGB": function() {
            let rgb = this.R + "," + this.G + "," + this.B;
            return 'rgb' + '(' + rgb + ')';
        },
        "isBrightTone": function() {
            return calculateColorBrightness(this) > 128;
        },
        "getContrastColor": function() {
            if (this.isBrightTone())
                return DARK;

            return LIGHT;
        }
    }

}



function parseDateToObj(dateStr) {
    let dateArr = dateStr.split("-");
    let isNull = dateArr[0] == "1970";
    let year = parseInt(dateArr[0]);
    let month = parseInt(dateArr[1]);
    let day = parseInt(dateArr[2]);
    return {
        "year": year,
        "month": month,
        "day": day,
        "is_null": isNull,
        "isAfter": function(obj) {
            if(this.is_null && obj.is_null)
                return true;
            if (this.is_null && !obj.is_null)
                return false;
            if (!this.is_null && obj.is_null)
                return true;
            if (obj.year < this.year)
                return true;
            if (obj.year > this.year)
                return false;

            if (obj.month < this.month)
                return true;
            if (obj.month > this.month)
                return false;

            if (obj.day < this.day)
                return true;
            if (obj.day > this.day)
                return false;

            return false;
        },
        "addMonths": function(months) {
            let rlt = this.month + months;
            if (rlt > 12) {
                this.year++;
                this.month = rlt - 12;
            } else {
                this.month = rlt;
            }
        },
        "toChineseInfo": function(mesWhenDateNull) {
            if (mesWhenDateNull != undefined && this.is_null)
                return mesWhenDateNull;

            return this.year + "年" + this.month + "月" + this.day + "日";
        },
        "toString": function(mesWhenDateNull) {
            if (mesWhenDateNull != undefined && this.is_null)
                return mesWhenDateNull;

            let month = this.month < 10 ? "0" + this.month : this.month;
            let day = this.day < 10 ? "0" + this.day : this.day;
            return this.year + "-" + month + "-" + day;
        },
        "isBlank": function() {
            return this.year == 1970 && this.month == 1 && this.day == 1;
        },
        "countMonthsBaseFutureDate": function(future) {
            return (future.year - this.year) * 12 + (future.month - this.month);
        },
        "isSameByMonth" : function(obj){
            return this.year == obj.year && this.month == obj.month
        }
    }
}

function getFileNameWithoutSuffix(fileName) {
    return fileName.substring(0, fileName.lastIndexOf('.'));
}

function getFileNameSuffix(fileName) {
    return fileName.substring(fileName.lastIndexOf('.') + 1);
}

        [1,2,3,4].some(i=>{
            return i%2 == 0;
        })


/*当登录超时后，x秒后跳回首页 */
const AUTO_STEP_TO_INDEX_JSP = 3;

const URL_FOR_BODY_IMAGE = "img/body_background.jpg";

$(function() {
    $("#error_msg_ok").click(function() {
        $("#error_msg").text("").hide();
        $("#error_msg_ok").hide();
    });
    if (parseStringToBoolean($("#is_local").val())) {
        $("body").css("background", "#920c0c");
    } else {
        $("body").css("background-image", "url(" + URL_FOR_BODY_IMAGE + ")");
    }
    $("#the_footer").addClass("box_shadow");

    $(document).ajaxError(function(event, data, setting, thrownError) {
    	console.log("In ajax error report!  thrownError:" + thrownError + " dataReadyState/status:" + data.readyState + ", " + data.status);
    	
        if (thrownError == "timeout") {
            showAlertMessage("网络请求超时...");
            return;
        }

        /* 两种寻找text 的方法 */
        /* 未找到对应例子*/
        let text = $(data.responseText).filter('p:eq(1)').find('u').text();
        if (text.length == 0) {
            text = $(data.responseText).filter('p:eq(1)').text();
            text = text.substring(text.indexOf(" ") + 1).trim();
        }

        if (text == "登录超时") {
            showAlertMessage(text + "，" + AUTO_STEP_TO_INDEX_JSP + "秒后自动跳转回登录页面.....");
            setTimeout(() => {
                self.location = "index.jsp";
            }, AUTO_STEP_TO_INDEX_JSP * 1000)
            return;
        }
        
        if (text.length != 0) {
            showAlertMessage(text + " " + thrownError);
            return;
        }

        if (data.readyState == "0") {
            showAlertMessage("服务器出现了意外状况，请稍后再试，或联系系统维护人员。" + thrownError);
            return;
        }

        showAlertMessage("发生未知错误，请联系系统维护人员。 Error:" + thrownError + " state/status:" + data.readyState + ", " + data.status);
    });

    var ajaxDialog = {
        "autoOpen": false,
        "modal": true,
        "minWidth": 500,
        "minHeight": 200,
        "title": "提示信息",
        "buttons": {
            "关闭": function() {
                $("#ajax_dialog").dialog("close");
            },
        }
    };
    $("#ajax_dialog").dialog(ajaxDialog).dialog(centerDialogSetting).parents(".ui-dialog").find(".ui-dialog-titlebar").css("background", "#FCB7B4");
});

function checkAnnualFeeLimit(patentTypeCode, years) {
    var maxYearsForInvention = parseInt($("#max_years_for_invention").val());
    var maxYearsForNonInvention = parseInt($("#max_years_for_non_invention").val());
    var dbcodeOfInvention = parseInt($("#dbcode_of_invention").val());
    if (patentTypeCode == dbcodeOfInvention) {
        return years <= maxYearsForInvention;
    } else {
        return years <= maxYearsForNonInvention;
    }
}

var centerDialogSetting = {
    position: {
        my: 'center',
        at: 'center',
        of: window,
        collision: 'fit',
        using: function(pos) {
            var topOffset = $(this).css(pos).offset().top;
            var topPosition = $(this).css(pos).position().top;
            $(this).css('top', pos.top - topOffset / 2 + $(document).scrollTop() / 2);
        }
    }
};



function decideLogic() {
    var value = $("#logout_value").val();
    if (value == "true") {
        logout();
    } else {
        gotoPage("login.jsp");
    }
}

function checkContentHeight($content) {
    $(".show_complete_content").remove();
    var height = parseInt($content.css("height").replace("px", ""));
    var maxHeight = parseInt($content.css("line-height").replace("px", "")) * 5;
    if (height == 0) {
        return;
    }
    if (height <= maxHeight) {
        return;
    }
    $content.addClass("hide_part_content");
    var $button = $("<div></div>");
    $button.addClass("show_complete_content").text("查看全文").click(showCompleteContent);
    $content.after($button);
}

function showCompleteContent() {
    showCompleteValue($(this), "hide_part_content")
}

function showCompleteValue($thisButton, className) {
    if ($thisButton.text() == "查看全文") {
        $thisButton.text("收起").prev().removeClass(className);
    } else {
        $thisButton.text("查看全文").prev().addClass(className);
    }
}


function setupHeaderMenu(theTab) {
    var menuId = "#menu_" + theTab;
    $(menuId).toggleClass("menu_selected");

    $("#menu_bar > li").on('click', function() {
        $(this).addClass("menu_selected").siblings().removeClass("menu_selected");
    });
}

$(document).ajaxStart(function(event, data, setting, thrownError) {
    $("#error_msg").text("").hide();
    $("#error_msg_ok").hide();
    $(".dialog_error_msg").text("").hide();
});

function dialogClose(dialogId) {
    $("#" + dialogId + "").dialog('close').dialog('destory');
}

function closeWindow(message) {
    if (confirm(message)) {
        window.close();
    }
}

function gotoPage(url) {
    window.location.href = url;
}

//TODO 这儿还有 path，小心 jsp 里的 path
function gotoUserDetail(userId) {
    window.open("user_detail.jsp?id=" + userId, "_blank");
}


function isBlankDate(str) {
    /*TODO:并不严谨 */
    return str == "1970-01-01";
}



function login() {
    var name = $("#name").val();
    var password = $("#password").val();


    if (name.length == null || name.length == 0) {
        showAlertMessage("username must be filled!");
        return;
    }

    if (password.length == null || password.length == 0) {
        showAlertMessage("password must be filled!");
        return;
    }
    var param = {
        'op': "login",
        'name': name,
        'timeout': 1000,
        'password': password
    };
    $.post("UserServlet", param, login_render, "xml");
}

function login_render(data) {
    if ($(data).find("login_info").length != 0) {
        $("#tips_info").text("您的密码太过简单,请修改");
        $("#simple_password_user_id").val($(data).find("login_info").attr("id"));
        $("#simple_password_user_account").val($(data).find("login_info").attr("account"));
        $("#change_simple_password_dialog").dialog("open");
        return;
    }
    window.location.href = $(data).find("first_module").text();
}

function logout() {
    $.post("UserServlet", { op: "logout" }, logout_render, "xml");
}

function logout_render(data) {
    var warningMsg = $(data).find("warning_msg").text();
    if (warningMsg.length > 0) {
        showAlertMessage(warningMsg);
    } else {
        window.location.href = "index.jsp";
    }
}

function newRegist() {
    gotoPage("register.jsp");
}

function enterkeydown(event) {
    if (navigator.appName == "Microsoft Internet Explorer") {
        if (event.keyCode == 13) {
            login();
        }
    } else {
        if (event.which == 13) {
            login();
        }
    }
}

function showAlertMessage(text) {
    $("#ajax_dialog pre").html(text);
    $("#ajax_dialog").dialog("open");
}

function showAlertInfo(desc) {
    $("#alert_info_text").html(desc);
    $("#alert_info_dialog").dialog("open");
}

function showConfirmCancelDialog(fun, text, id,cancelFun) {
    var cancelDialog = {
        "autoOpen": false,
        "modal": true,
        "minWidth": 450,
        "minHeight": 200,
        "title": "提示",
        "buttons": {
            "确认": function() {
                fun();
                $("#cancel_dialog").dialog("close");
            },
            "关闭": function() {
                
                if(cancelFun!=null){
                    cancelFun();
                }

                $("#cancel_dialog").dialog("close");
            },
        }
    };
    $("#cancel_dialog").dialog(cancelDialog).dialog(centerDialogSetting);
    $("#cancel_promt").html(text);
    $("#hidden_cancel_dialog_id").val(id);
    $("#cancel_dialog").dialog("open");
}

function unbindAllFileUpload() {
    $("input[type=file]").fileupload({ "autoUpload": false });
    $("body").unbind();
}

function cancelCheckboxEvent(event) {
    event.stopPropagation();
}

function clickCheckbox() {
    $(this).children("input").click();
}

var language = {
    "sLengthMenu": "每页显示 _MENU_ 条记录",
    "sZeroRecords": "无内容",
    "sInfo": " _START_-_END_ (共 _TOTAL_ 条记录)",
    "sInfoEmpty": "无记录",
    "sInfoFiltered": "",
    "sSearch": "搜索",
    "oPaginate": {
        "sPrevious": "前一页",
        "sNext": "后一页",
        "sLast": "尾页",
        "sFirst": "首页"
    }
};

function paramValidation(fields, judges) {
    for (let key in judges) {
        var Effective = true;
        var isReadAll = false;
        $(fields).each(function(i, field) {
            var name = field.name;
            var value = field.value;
            if (key == name) {
                if (value == null || value == "" || value == 0) {
                    Effective = false;
                    return false;
                }
                isReadAll = true;
            }
        });
        if (!isReadAll) {
            showAlertMessage(judges[key]);
            return false;
        }
        if (!Effective) {
            showAlertMessage(judges[key]);
            return false;
        }
    }
    return true;
}

function checkInputOnlyNum(inputId) {
    var num = $("#" + inputId).val().replace(/[^\d^\.]+/g, '');
    $("#" + inputId).val(num);
}

function ignoreReverseInputWhenUnnecessary($dataSet, $reverseInput) {
    if ($dataSet.length == 0) {
        $reverseInput.hide();
    } else {
        $reverseInput.show();
    }
}

function parseStringToBoolean(str) {
    switch (str) {
        case "true":
        case "yes":
        case "1":
            return true;
        case "false":
        case "no":
        case "0":
        case null:
            return false;
        default:
            return Boolean(str);
    }
}

function getDataSupportEmpty($data, dataName) {
    var oneData = $data.attr(dataName);
    if (oneData == null) {
        return "";
    }
    return oneData;
}

function getImagesFromData($data) {
    var fileNameOrigin = getDataSupportEmpty($data, "file_name_origin");
    var fileName = getDataSupportEmpty($data, "file_name");

    var fileNameSmall = getDataSupportEmpty($data, "file_name_small");
    var fileNameBig = getDataSupportEmpty($data, "file_name_big");

    var fileNameMid = getDataSupportEmpty($data, "file_name_mid");

    var majorColor = getDataSupportEmpty($data, "major_color");
    var lastModified = getDataSupportEmpty($data, "last_modified");

    var fileInfos = {
        value: fileName,
        origin: fileNameOrigin,
        small: parseImageNameByLastModified(fileNameSmall, lastModified),
        big: parseImageNameByLastModified(fileNameBig, lastModified),
        mid: parseImageNameByLastModified(fileNameMid, lastModified),
        major: majorColor,
    };
    return fileInfos;
}

function parseImageNameByLastModified(name, lastModified) {
    if (lastModified.length > 0) {
        lastModified = "&" + lastModified;
    }
    return name + lastModified;
}

function createWebUploader(elementId) {
    return createWebUploaderByServer(elementId, "FileUploadServlet?op=upload_pic&is_logo=true");
}

function createWebUploaderByServer(elementId, server) {
    var uploader = WebUploader.create({
        auto: true,
        server: server,
        dnd: elementId,
        duplicate: true,
        disableGlobalDnd: true,
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*',
        },
        compress: {
            width: 626,
            height: 413,
            quality: 100,
            allowMagnify: false,
            crop: false,
            preserveHeaders: true,
            noCompressIfLarger: false,
            compressSize: 300 * 1024,
        },
    });
    return uploader;
}

function stringToXml(xmlString) {
    var xmlDoc;
    if (typeof xmlString == "string") {
        if (document.implementation.createDocument) {
            var parser = new DOMParser();
            xmlDoc = parser.parseFromString(xmlString, "text/xml");
        } else if (window.ActiveXObject) {
            xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = false;
            xmlDoc.loadXML(xmlString);
        }
    } else {
        xmlDoc = xmlString;
    }
    return xmlDoc;
}

function showOriginPic(originPic) {
    showOriginImageByFancybox(originPic);
}

function showOriginImageByFancybox(picName) {
    $.fancybox.open(picName);
}

function loadLogoError() {
    $("#img1").empty();
}

function showImgByFancyBox() {
    $.fancybox.open($(this).attr("src"));
}

function refineDateStrIfNull(dateStr) {
    if (dateStr == "1970-01-01 00:00:00" ||
        dateStr == "1970-01-01") {
        return "未知";
    } else {
        return dateStr;
    }
}
/**
 * 抽象的是一种对dom元素的二元切换操作，
 * classifierAttr:用来做二元切换,第一次切换后为true，第二次切换后为false;
 * funcInFirstState和funcInSecondState分别是classifierAttr切换为true和false时调用的函数
 * classInFirstState和classInSecondState分别是dom元素切换状态时为dom元素加的class。可缺省，缺省时默认不加class
 * 
 * 应当只有在二元操作都十分复杂的时候使用，如果不复杂的话，不如直接写
 * 
 *  */
function toggleWithBinaryState(classifierAttr, funcInFirstState, funcInSecondState, dom, classInFirstState, classInSecondState) {
    let isInFirstState = parseStringToBoolean($(dom).attr(classifierAttr));
    $(dom).attr(classifierAttr, !isInFirstState);

    if (classInFirstState != undefined) {
        $(dom).toggleClass(classInFirstState, !isInFirstState);
    }

    if (classInSecondState != undefined) {
        $(dom).toggleClass(classInSecondState, isInFirstState);
    }

    if (isInFirstState) {
        funcInSecondState();
    } else {
        funcInFirstState();
    }
}

function matchOnlyNumbersBaseInput() {
    var $input = $(this);
    var reg = /^([0-9]|\.)+$/; //匹配一个马上的0-9和小数点
    matchBaseInput(reg, $input);
}

function matchBaseInput(regex, $input) {
    var reg = regex;
    if (!reg.test($input.val())) {
        $input.val($input.val().replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g, ''));
        $input.focus();
        var range = document.createRange();
        range.selectNodeContents($input[0]);
        range.collapse(false);
        var select = window.getSelection();
        select.removeAllRanges();
        select.addRange(range);
    }
}

//feeObj包括服务费和官费
//TODO 应该跟getFeeTypeName做一个区分，两者或许有重合
function getTypeNameFromFeeObj(feeObj) {
    if (isSF(feeObj)) {
        return feeObj.sfFee.nameForShow;
    } else {
        return parseInt(feeObj.type.dbCode) == HYN_BASIC_NAMESPACE.AF.dbCode ?
            "第" + feeObj.patentYearNum + "年" + feeObj.type.alias : feeObj.type.alias;
    }
}


//target 一般是this
function autoFixHeightForTextarea(target, basicHeight) {
    $(target).css("height", basicHeight)
        .css("height", $(target).get(0).scrollHeight);

}


function getSumFromFeeObj(feeObj) {
    return isSF(feeObj) ? feeObj.sfFee.value : feeObj.sum;
}


function getValueFromFeeObj(feeObj) {
    return isSF(feeObj) ? feeObj.sfFee.value : feeObj.value;
}

function getPenaltyInfo(feeObj) {
    if (feeObj.penalty == 0) {
        return "";
    }
    return getPenaltyStateName(feeObj.fbRspbState.dbCode);
}

function getPenaltyStateName(stateCode) {
    stateCode = parseInt(stateCode);
    switch (stateCode) {
        case HYN_BASIC_NAMESPACE.FEE_RESPONSE_STATE_OF_UNDECIED.dbCode:
            return "未明确";
        case HYN_BASIC_NAMESPACE.FEE_RESPONSE_STATE_OF_CUSTOMERS.dbCode:
            return "客户责任";
        case HYN_BASIC_NAMESPACE.FEE_RESPONSE_STATE_OF_OURS.dbCode:
            return "我方责任";
        default:
            throw stateCode;
    }
}

/**
 * 重新创建一个可以接受大多类型文件的上传容器
 * 会把container之前绑定的uploader先消除
 * 返回webUploader
 */
function rebindStandardWebUploader(url, $container) {
    $container.off();
    var uploader = WebUploader.create({
        auto: true,
        server: url,
        dnd: $container,
        duplicate: true,
        disableGlobalDnd: true,
        accept: {
            title: 'StandardFile',
            extensions: 'zip,pdf,docx,doc,jpg,jpeg,bmp,png,tif',
            mimeTypes: 'image/*,application/*',
        },
        compress: {
            width: 626,
            height: 413,
            quality: 100,
            allowMagnify: false,
            crop: false,
            preserveHeaders: true,
            noCompressIfLarger: false,
            compressSize: 300 * 1024,
        },
    });
    return uploader;
}

function parseFileType(fileName) {
    let a = fileName.split('').reverse().join('');
    let b = a.substring(0, a.search(/\./)).split('').reverse().join('');
    return b;
}


function sortByTime(a, b, translator) {
    var dateA = new Date(Date.parse(translator(a)));
    var dateB = new Date(Date.parse(translator(b)));
    if (dateA < dateB) {
        return -1;
    } else if (dateA > dateB) {
        return 1;
    } else {
        return 0;
    }
}

Array.prototype.distinct = function() {
    var arr = this,
        result = [],
        i,
        j,
        len = arr.length;
    for (i = 0; i < len; i++) {
        for (j = i + 1; j < len; j++) {
            if (arr[i] === arr[j]) {
                j = ++i;
            }
        }
        result.push(arr[i]);
    }
    return result;
}

function showResultMessageForAWhile($dom, message, intervalTime) {
    const defaultIntervalTime = 3000;
    let intervalT = intervalTime == undefined ? defaultIntervalTime : intervalTime

    $dom.addClass("to_show_for_hint_message").html(message);
    setTimeout(() => {
        $dom.html("").removeClass("to_show_for_hint_message");
    }, intervalT);
}

function getEmLableHtml(text) {
    return "<em>" + text + "</em>";
}

function clone(obj) {
    return JSON.parse(JSON.stringify(obj));
}

function searchCorrelativeCustomerByKW_render(data, $container) {
	var names = [];
	$(data).find("customer").each(function() {
        names.push($(this).attr("name"));
    })
    $container.autocomplete("option", "source", names)
    .autocomplete("search", "");
}