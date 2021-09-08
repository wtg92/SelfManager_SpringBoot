const BASIC_NAMESPACE = {
    "INFORMATION_GROUPS" :
        [{
           "name" : "关于本站",
           "items":[
            createInformationItem("起源", 1,"web_info_what_sm_to_src"),
            createInformationItem("精神", 2,"web_info_sm_spirit"),
            createInformationItem("我要捐款", 3,"web_info_i_want_to_donate"),
           ]
        },
        {
            "name" : "其它",
            "items":[
                createInformationItem("手机端使用",5,"web_info_system_info_for_phone"),
                createInformationItem("联系方式",4,"web_info_connect_with_us")
            ]
        }
        ],


    /*工作表模块 */
    WS_HINT_FOR_NULL_PLAN_ITEM_VAL:"您没有填写投入值，将默认为0，确定吗？",
    WS_HINT_FOR_NULL_PLAN_ITEM_MAPPING_VAL:"您没有填写换算值，将默认为0，确定吗？",
    WS_ITEM_CAT_TYPE_OF_MINUTES :1,
    WS_ITEM_CAT_TYPE_OF_TIMES:2,

    WORK_ITEM_TYPE_OF_DEPT:2,
    WEBUPLOADER_SWF_PATH : "js/dependency/Uploader.swf"

}


$(function(){
    $("body").on("click",".common_open_new_window",openNewWindow)
        .on("click",".common_go_to_page",goToPageByLabel)
        .on("click","pdf",seePDFFileByOpenNewWindow)
        .on('hidden.bs.modal', function () {
            /*StackOverFlow 救命 这个Bootstrap 在modal切换时滚轮失效的BUG 解决了*/
            if($('.modal.show').length){
                $('body').addClass('modal-open');
            }
        }).on('input',"textarea",function(){
            /*100是basic.js设置的textarea的值*/
            $(this).css("height",100)
                .css("height",$(this).prop('scrollHeight'));
        })
    

    $(".spirit_img").click(()=>window.location.href = "web_info.jsp?container=web_info_sm_spirit");

    drawCommonIcon("inluding_exclamation_mark",$("#including_alert_dialog .modal-title"));
    drawCommonIcon("inluding_question_mark",$("#including_confirm_dialog .modal-title"));

    checkSignInStateAndAssignIdentifier();

    $(document).ajaxError(commmonAjaxErrorHandler);

    initCommonUI();

    $('[data-toggle="popover"]').popover();

    adpatPhoenAPP();
})



function adpatPhoenAPP(){
    if(isPC()){
        return;
    }

    $("#header_main_container,#footer_main_container,.common_main_container").css("min-width","0");
    $(".modal-lg,.modal-xl").css("max-width","none");
    $("input[type='time']").css({
        "max-width":"6.5em",
        "margin-right" : "0.5em"
    });
}

function isPC() {
   var userAgentInfo = navigator.userAgent;
   var Agents = ["Android", "iPhone",
      "SymbianOS", "Windows Phone",
      "iPad", "iPod"];
   var flag = true;
   for (var v = 0; v < Agents.length; v++) {
      if (userAgentInfo.indexOf(Agents[v]) > 0) {
         flag = false;
         break;
      }
   }
   return flag;
}


function seePDFFileByOpenNewWindow(){
    let pdf = $(this).attr("pdf");
    window.open("/sm_files/pdf/"+pdf+".pdf");
}

function fillTextareaVal(text,$textarea){
    $textarea.css("height",100);

    if(text == undefined){
        $textarea.val("");
        return;
    }

    $textarea.val(text);

    if(text.length == 0){
        /*此时不必弄什么自适应高度了*/
        return;
    }
    let scrollHeight = $textarea.get(0).scrollHeight;
    if(scrollHeight>0){
        /*合法的*/
        $textarea.css("height",scrollHeight);
        return;
    }
    /**
     * 这种有两种情况出现：
     * a.加载dom 先于这个方法 此时应该修改加载Dom的顺序
     * b.未知问题，此时会根据换行符 来决定大体的高度 font-size 认为是16px
     * 
     * 24.8是计算出来的一行字的高度
     */
    $textarea.height((text.split("\n").length) * 24.8);
}

function resetTextAreaHeight($content){
    $content.find("textarea").each((i,v)=>{
        $(v).css("height",$(v).get(0).scrollHeight);    
    });
}

function getUrlParam(key) {
    let params = {};
    if (window.location.search.indexOf("?") == 0 && window.location.search.indexOf("=") > 1) {
        let paramArray = unescape(window.location.search).substring(1, window.location.search.length).split("&");
        if (paramArray.length > 0) {
            paramArray.forEach(function (currentValue) {
                params[currentValue.split("=")[0]] = currentValue.split("=")[1];
            });
        }
    }
    return params[key];
}

function switchCommonContainer(dom,showFunc,hideFunc){
    let open = parseToBool($(dom).attr("open"));
    if(open){
        hideFunc();
    }else{
        showFunc();
    }
}


function initCommonUI(){
    $("[type='date'],[type='time']").each((i,v)=>{
        if(!$(v).prop("title")){
            $(v).prop("title","如果无法选择或仅仅觉得太丑，建议更换为Google浏览器");
        }
    }); 
    $("input[name='name']").prop("autocomplete","off");
}

function showForAWhile(message,$cotainer, lastingTime){
    const defaultLastingTime = 3000;
    let time = lastingTime == undefined ? defaultLastingTime : lastingTime

    $cotainer.addClass("common_hint_message").html(message);
    setTimeout(() => {
        $cotainer.html("").removeClass("common_hint_message");
    }, time);
}


function commmonAjaxErrorHandler(event, data, setting, thrownError){
    
    try{
        alertInfo(data.responseJSON.message);
    }catch(e){
        try{
            alertInfo("未知错误 " + data.responseJSON.error);
        }catch(e2){
            alertInfo("连接超时，请刷新页面");
        }
    }
}



function clearSignInInfo(){
    sessionStorage[CONFIG.SIGN_IN_KEY] = "";
    localStorage[CONFIG.USER_TOKEN_KEY] = "";
}


function goToPage(url){
    window.location.href = url;
}

/**
 * 检查SIGN_IN_KEY true 表明已经验证过了，啥事都不用做
 *                 false 检查UserToken 有 发给后台验证 成功ok 失败则同无 处理      
 *                                     无 分析tempId 有 发给后台验证  没失效则后台刷新存活时间 失效则重新获取一个
 *                                                   无 重新获取一个
 */
function checkSignInStateAndAssignIdentifier(){
    if(sessionStorage[CONFIG.SIGN_IN_KEY]){
        if(CONFIG.REQUIRE_SIGN_OUT){
            goToPage(CONFIG.DEFAULT_GOING_TO_PAGE);
        }
        return ;
    }

    let userToaken = localStorage[CONFIG.USER_TOKEN_KEY];
    if(userToaken != undefined && userToaken.length > 0){
        sendAjax("UserServlet","u_confirm_user_token",{
            "user_token" : userToaken
        },confirmUserToken_render,false,clearSignInInfo);
        return;
    }

    if(CONFIG.REQUIRE_SIGN_IN){
        goToPage(CONFIG.DEFAULT_SIGN_OUT_PAGE);
    }

    /**
     * 证明是临时用户（至少没登录）
     * 这时看tempId tempId 有: 发给后台确认 ，假如存在则刷新时间 假如已失效，那么重新要一个tempId
     */
    let tempId = sessionStorage[CONFIG.TEMP_USER_ID_KEY];
    if(tempId != undefined && tempId.length > 0){
        sendAjax("UserServlet","u_confirm_temp_user",{
            "temp_user_id" : tempId
        },confirmTempUserId_render,false,clearSignInInfo);
        return;
    }
    
    sendAjax("UserServlet","u_get_temp_user",[],getTempUserId_render,false,clearSignInInfo);    
}


function getFileName(data) {
    return data.substring(0,data.indexOf("."));
}


/*TODO 我不知道为什么ajax 没法有效解码 中文文件名，因此就让文件名有能力传进来吧*/
function invokeDownloadingSucFuncForAjax(blob, xhr, fileNameForDownload) {
    // check for a filename
    var filename = "";
    var disposition = xhr.getResponseHeader('Content-Disposition');
    if (disposition && disposition.indexOf('attachment') !== -1) {
        var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        var matches = filenameRegex.exec(disposition);
        if (matches != null && matches[1]) {
            filename = matches[1].replace(/['"]/g, '')
            if(fileNameForDownload){
                filename = fileNameForDownload;
            }
        };
    }

    if (typeof window.navigator.msSaveBlob !== 'undefined') {
        // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
        window.navigator.msSaveBlob(blob, filename);
    } else {
        var URL = window.URL || window.webkitURL;
        var downloadUrl = URL.createObjectURL(blob);

        if (filename) {
            // use HTML5 a[download] attribute to specify filename
            var a = document.createElement("a");
            // safari doesn't support this yet
            if (typeof a.download === 'undefined') {
                window.location.href = downloadUrl;
            } else {
                a.href = downloadUrl;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
            }
        } else {
            window.location.href = downloadUrl;
        }

        setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
    }
}



function getTempUserId_render(data){
    sessionStorage[CONFIG.TEMP_USER_ID_KEY] = data.rlt;
}


function confirmTempUserId_render(data){
    if(!data.rlt){
        sendAjax("UserServlet","u_get_temp_user",[],getTempUserId_render,false,clearSignInInfo);    
    }
}
function processLoginInfo(data){
    sessionStorage[CONFIG.TEMP_USER_ID_KEY] = "";
    sessionStorage[CONFIG.SIGN_IN_KEY] = data.token;
    sessionStorage[CONFIG.SEE_USERS_MODULE_KEY] = data.seeUsersModule;
    sessionStorage[CONFIG.SEE_NOTES_MODULE_KEY] = data.seeNotesModule;
}

function sendAjaxForUploadFileInReqAndDownloadFileFromRes(url,formData,newFileName,complete){
    $.ajax({
        url: url,
        type: 'post',
        data: formData,
        async: true,
        contentType: false,
        processData: false,
        success: (blob, status, xhr)=>{
            invokeDownloadingSucFuncForAjax(blob, xhr, newFileName);
        },
        complete:complete,
        xhrFields: {
            responseType: 'blob' // to avoid binary data being mangled on charset conversion
        },

    })
}

function confirmUserToken_render(data){
    if(data.success){
        processLoginInfo(data);
        localStorage[CONFIG.USER_TOKEN_KEY] = data.token;
        if(CONFIG.REQUIRE_SIGN_OUT){
            goToPage(CONFIG.DEFAULT_GOING_TO_PAGE);
        }
    }else{
        clearSignInInfo();
        if(CONFIG.REQUIRE_SIGN_IN){
            let secondsToGo = 3;
            alertInfo(data.errMsg+","+secondsToGo+"秒后自动退回登录界面");
            setTimeout(() => {
                goToPage(CONFIG.DEFAULT_SIGN_OUT_PAGE);
            }, secondsToGo * 1000);
            return;
        }

        alertInfo(data.errMsg);    
    }
    resetHeaderContainer();
}

function setBase64Img(base64,$img){
    $img.prop("src","data:image/jpg;base64,"+base64);
}
function setBase64BackGround(base64,$dom){
    $dom.css("background","url(data:image/jpg;base64,"+base64+")");
}

function goToPageByLabel(){
    let href = $(this).attr("href");
    goToPage(href);
};
function openNewWindow(){
    let href = $(this).attr("href");
    window.open(href);
}


function createInformationItem(name,code,containerId){
    return {
        "name" : name,
        "code" : code,
        "containerId" : containerId
    }
}
function drawCommonLogs(logs,$logsCotainer){
    $logsCotainer.empty();
    logs.forEach((log)=>{
        let $log = $("#including_pattern_container .including_log_container").clone();
        $log.find(".common_log_content").html(log.info).end()
            .find(".common_log_creator_name").text(log.creatorName).end()
            .find(".common_log_time").text(new Date(log.log.createTime).toSmartString());
        
        $logsCotainer.append($log);
    })
}


function drawEyeSlash($cotainer){
    drawCommonIcon("including_icon_eye_slash",$cotainer);
}

function drawCommonIcon(classVal,$cotainer){
    let $eyeSlash = $("#including_pattern_container").find("."+classVal).clone();
    $cotainer.empty().append($eyeSlash);
}

function drawEye($cotainer){
    drawCommonIcon("including_icon_eye",$cotainer);
}

function parseToBool(str){
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
/** 
 * 有一些N层回调的函数 导致防止连点的remove不好加 就采用这个方法 更安全些
 * */
function preventDoubleClick(dom){
    $(dom).addClass("common_prevent_double_click");
    setTimeout(()=>{
        $(dom).removeClass("common_prevent_double_click");
    },500);
}



/** 
 * positiveButtonText 可缺省 默认为 确定
 * negativeButtonText 可缺省 默认为 取消
*/
function confirmInfo(text,positiveFunc,positiveButtonText,negativeButtonText,negativeFunc){
    confirmInfoUnit($("#including_confirm_dialog"),text,positiveFunc,positiveButtonText,negativeButtonText,negativeFunc);
}

/**
 * bootstrap 有BUG 假如两个相同的dialog 在极短的时间间隔内 连续show 就会导致页面崩，因此假如两次confirmInfo 时间间隔十分短暂时（如在第一次confirm的回调里 同时也有confirm）
 * 第二次confirm 应当使用该方法 
 */
function confirmInfoSecond(text,positiveFunc,positiveButtonText,negativeButtonText,negativeFunc){
    confirmInfoUnit($("#including_confirm_dialog_2"),text,positiveFunc,positiveButtonText,negativeButtonText,negativeFunc);
}


function confirmInfoUnit($dialog,text,positiveFunc,positiveButtonText,negativeButtonText,negativeFunc){

    if(!isPC()){
        /*手机端*/
        if(confirm(text)){
            positiveFunc();
        }else{
            if(negativeFunc){
                negativeFunc();
            }
        }
        return;
    }



    let positiveButton = positiveButtonText == undefined ? "确定" : positiveButtonText;
    let negativeButton = negativeButtonText == undefined ? "取消" : negativeButtonText;

    $dialog.find(".modal-body").html(text).end()
        .find(".positive_event").text(positiveButton)
        .off("click.myFunc")
        .on("click.myFunc",positiveFunc)
        .end()
        .find(".negative_event").text(negativeButton)
        .off("click.myFunc")
        .on("click.myFunc",negativeFunc == undefined ? ()=>{} : negativeFunc)
        .end()
        .modal("show");
}


function alertInfo(text){
    if(isPC()){
        $("#including_alert_dialog").find(".modal-body").text(text).end().modal("show");
    }else{
        alert(text);
    }
}

function sendAjaxBySmartParams(url,op,params,suncFunc,completeFunc){
    sendAjax(url,op,params,suncFunc,true, () => {},completeFunc);
}

/**
 * @param {*} requireSignIn 可缺省 默认true 当为true时，会自动在param里边加上user_token参数及值
 * @param {*} errorFunc 可缺省
 * @param {*} completeFunc 可缺省
 */
function sendAjax(url,op,params,suncFunc,requireSignIn,errorFunc,completeFunc){

    let addUserToken = requireSignIn == undefined ? true : requireSignIn;

    if(addUserToken){
        /**
         * 解决校验登录的时间差 导致的问题
         */
        if(!sessionStorage[CONFIG.SIGN_IN_KEY]){
            setTimeout(()=>sendAjax(url,op,params,suncFunc,requireSignIn,errorFunc,completeFunc),1000);
            return;
        }
    }

    if(params instanceof Array){
        params.push({"name":"op","value":op});
        if(addUserToken){
            params.push({"name":"user_token","value":sessionStorage[CONFIG.SIGN_IN_KEY]});
        }
    }else{
        params.op = op;
        if(addUserToken){
            params.user_token = sessionStorage[CONFIG.SIGN_IN_KEY];
        }
    }
    let param = {
        "url" : url,
        "type" : "post",
        "success" : suncFunc,
        "data" : params,
        "dataType":"json",
        "traditional" : true
    };
    if(errorFunc != undefined){
        param.error = errorFunc;
    }
    if(completeFunc != undefined){
        param.complete = completeFunc;
    }
    $.ajax(param);
}

/**
 * colorClass:
 * 
 * btn-primary
 * btn-outline-primary        蓝色
 * btn-secondary
 * btn-outline-secondary      灰色
 * btn-success
 * btn-outline-success        绿色
 * btn-danger
 * btn-outline-danger         红色
 * btn-warning
 * btn-outline-warning        黄色
 * btn-info
 * btn-outline-info           青色
 * btn-light
 * btn-outline-light          灰白色
 * btn-dark
 * btn-outline-dark           黑色
 * btn-link                   白色
 */
function getOneRadioHtml(name,value,labelContent,colorClass){
    let $label = $("<label>");
    $label.addClass("btn").addClass(colorClass);
    let $radio = $("<input>");
    $radio.val(value).attr({
        "type" : "radio",
        "name" : name,
    });
    $label.html(labelContent).append($radio);
    return $label;
}

function getTopPopoverBtn(popoverText,btnText){
   let $span = $("<span>");
   $span.attr({
       "data-container":"body",
       "data-toggle":"popover",
       "data-placement":"top",
       "data-content":popoverText,
   }) 
   $span.text(btnText);
   return $span;
}



function translateUndefinedForDataTable(val){
    if(val==undefined)
        return "";

    return val;
}

function getFontColorAndBackgroudColor(backgroundColor) {
    return {
        "background-color": backgroundColor,
        "color" :  parseColor(backgroundColor).getContrastColor()
    }
}


/*假设 红色->绿色 渐变色 最小数字1 最大数字5 那么当取值为3时，会返回一个红色和绿色中间相对比例的颜色 */
function calculateColorByRange(startColorText, endColorText,minNum,maxNum,num) {
    let startColor = parseColor(startColorText);
    let endColor = parseColor(endColorText);
    let sR = (endColor.R - startColor.R) / (maxNum-minNum);
    let sG = (endColor.G - startColor.G) / (maxNum-minNum);
    let sB = (endColor.B - startColor.B) / (maxNum-minNum);
    let coe = (num-minNum);
    return "rgb("+parseInt((sR * coe + startColor.R))+","+ parseInt((sG * coe + startColor.G))+","+ parseInt((sB * coe + startColor.B))+")";
}



function parseColor(color) {
    const DARK = "#212529";
    const LIGHT = "#fff";
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
        let rgb = [];
        for (let i = 0; i < 3; i++) {
            rgb.push(parseInt(arr[i], 16));
        }
        return rgb;
    }

    function spiltArr(str) {
        let tmp = ['', '', ''];
        let strLen = str.length;
        if (strLen == 3) {
            let a = str.substr(0, 1);
            let b = str.substr(1, 1);
            let c = str.substr(2, 1);
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


function inputOnlyAllowInt() {
    let $input = $(this);
    let reg = /^([0-9])+$/; 
    if (!reg.test($input.val())) {
        $input.val($input.val().replace(/[^\d{1,}\d{1,}|\d{1,}]/g, ''));
        $input.focus();
        let range = document.createRange();
        range.selectNodeContents($input[0]);
        range.collapse(false);
        let select = window.getSelection();
        select.removeAllRanges();
        select.addRange(range);
    }
}

function inputOnlyAllowFloat() {
    let $input = $(this);
    let reg = /^([0-9]|\.)+$/;
    if (!reg.test($input.val())) {
        $input.val($input.val().replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g, ''));
        $input.focus();
        let range = document.createRange();
        range.selectNodeContents($input[0]);
        range.collapse(false);
        let select = window.getSelection();
        select.removeAllRanges();
        select.addRange(range);
    }
}

function cloneObj(obj){
    if(obj instanceof Date){
        return new Date(obj.toString());
    }

    return JSON.parse(JSON.stringify(obj));
}


function getDropDownItemButton(){
    let $span = $("<button>");
    $span.addClass("dropdown-item").prop("type","button");
    return $span;
}

function calculateRootPlanItemText(item){
    if(item.type.dbCode != BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES){
        return item.name+"  "+item.value+item.type.name;
    }
    return item.name+"  "+"<span title='"+item.value.transferToHoursMesIfPossible() +"'>"+item.value+item.type.name+"</span>";
}

function calculateUnitPlanItem(item,fatherItem){
    if(item.type.dbCode == fatherItem.type.dbCode){
        return item.name+"<little>比率 " +item.mappingValue*100+"%</little>";
    }
    if(item.type.dbCode == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES){
        return item.name+"<little> 1次"+fatherItem.name +" 相当于 "+item.mappingValue+"分钟</little>"
    }
    return item.name +"(1次)<little>抵 "+fatherItem.name+" 的 "+item.mappingValue+"分钟</little>"
}


function isInDevelopment(){
    alertInfo("开发中");
    throw "block the func";
}




function initBootStrapBtns($container){
    $container.find("label").filter((i,v)=>$(v).find("input").prop("checked")).button('toggle').removeClass("active");   
}

/*使用前需要先初始化$container*/
function fillAutocompletInput(data, $container) {
    $container.autocomplete("option", "source", data)
    .autocomplete("search", "");
}

function transferLineBreaksToHTML(str){
    return str.replaceAll("\n","<br>");
}

function transferLineBreaksToText(str){
    return str.replaceAll("<br>","\n");
}


function copyToClipboardInDefault(text) {
    copyToClipboard(text,$("body"));
}

/*当使用dialog时，需要把对应dialog 传入*/
function copyToClipboard(text,$visibileContainer) {
    var $temp = $("<textarea>");
    $visibileContainer.append($temp);
    $temp.val(text).select();
    document.execCommand("copy");
    $temp.remove();
}
function withCtrl(e){
    return (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey);
}


/*至少包含大写字母，小写字母，数字，且不少于8位*/
function checkSignUpPwdLegal(text){
    let re =/^(?=.*[a-z])(?=.*\d)[^]{8,}$/;
    return re.test(text);
}

function checkSignUpEmailLegal(text){
    let re =/^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
    return re.test(text);
}

function checkSignUpTelLegal(text){
    let re =/^[1][0-9]{10}$/;
    return re.test(text);
}



/*5-20个以字母开头，可带数字、下划线*/ 
function checkSignUpAccountLegal(text){
    let re =/^[a-zA-Z]{1}([a-zA-Z0-9]|[_]){4,19}$/;  
    return re.test(text);
}




jQuery(function($){
    //解决模态框背景色越来越深的问题
    $(document).on('show.bs.modal', '.modal', function(event) {
        $(this).appendTo($('body'));
    }).on('shown.bs.modal', '.modal.in', function(event) {
        setModalsAndBackdropsOrder();
    }).on('hidden.bs.modal', '.modal', function(event) {
        setModalsAndBackdropsOrder();
    });

    function setModalsAndBackdropsOrder() {
        var modalZIndex = 1055;
        $('.modal.in').each(function(index) {
            var $modal = $(this);
            modalZIndex++;
            $modal.css('zIndex', modalZIndex);
            $modal.next('.modal-backdrop.in').addClass('hidden').css('zIndex', modalZIndex - 1);
        });
        $('.modal.in:visible:last').focus().next('.modal-backdrop.in').removeClass('hidden');
    }

    //覆盖Modal.prototype的hideModal方法
    $.fn.modal.Constructor.prototype.hideModal = function () {
        var that = this
        this.$element.hide()
        this.backdrop(function () {
            //判断当前页面所有的模态框都已经隐藏了之后body移除.modal-open，即body出现滚动条。
            $('.modal.fade.in').length === 0 && that.$body.removeClass('modal-open')
            that.resetAdjustments()
            that.resetScrollbar()
            that.$element.trigger('hidden.bs.modal')
        })
    }
});



function getOrInitEcharts(id){
    let dom = document.getElementById(id);
    let instance = echarts.getInstanceByDom(dom);
    if(instance){
        return instance;
    }
    return echarts.init(dom);
}


/**
 * 
 * @param {*} containerId 
 * @param {*} barSource  接受数组元素为 数组 或 {name,value} 的元素 
 * @param {*} yAxisName 
 * @param {*} xAxisNamesLength 
 */
function drawCommonBarChart(containerId,barSource,yAxisName,xAxisNamesLength){


    let isArrayElement = barSource.length > 0  && barSource[0] instanceof Array ;
    

    getOrInitEcharts(containerId).setOption({
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        xAxis: {
            type:"category",
            nameTextStyle : {
                color:"green",
                width : 100,
                overflow : "truncate",
            },
            axisLabel:{
                interval:0,
                formatter : function(params){
                    var newParamsName = "";// 最终拼接成的字符串
                             var paramsNameNumber = params.length;// 实际标签的个数
                             var provideNumber = 6;// 每行能显示的字的个数
                             switch(xAxisNamesLength){
                                case 1:
                                    provideNumber = 30;
                                    break;
                                case 2:
                                    provideNumber = 20;
                                    break;
                                case 3:
                                    provideNumber = 10
                                    break;
                                case 4:
                                case 5:
                                    provideNumber = 8;
                                    break;
                             }
                             var rowNumber = Math.ceil(paramsNameNumber / provideNumber);// 换行的话，需要显示几行，向上取整
                             /**
                              * 判断标签的个数是否大于规定的个数， 如果大于，则进行换行处理 如果不大于，即等于或小于，就返回原标签
                              */
                             // 条件等同于rowNumber>1
                             if (paramsNameNumber > provideNumber) {
                                 /** 循环每一行,p表示行 */
                                 for (var p = 0; p < rowNumber; p++) {
                                     var tempStr = "";// 表示每一次截取的字符串
                                     var start = p * provideNumber;// 开始截取的位置
                                     var end = start + provideNumber;// 结束截取的位置
                                     // 此处特殊处理最后一行的索引值
                                     if (p == rowNumber - 1) {
                                         // 最后一次不换行
                                         tempStr = params.substring(start, paramsNameNumber);
                                     } else {
                                         // 每一次拼接字符串并换行
                                         tempStr = params.substring(start, end) + "\n";
                                     }
                                     newParamsName += tempStr;// 最终拼成的字符串
                                 }
 
                             } else {
                                 // 将旧标签的值赋给新标签
                                 newParamsName = params;
                             }
                             //将最终的字符串返回
                             return newParamsName
                 }
            }
        },
        yAxis: {
            name : yAxisName

        },
        series: [{
            type: 'bar',
            data: isArrayElement ? barSource : barSource.map(e=>[e.name,e.value]) 
        }]
    })
}



/**
 * 
 * @param {*} pieSource  数组，数组内元素为带有name和value
 */
function drawCommonPieChart(containerId,pieSource,radius,transValueToLabel){
    let pieChartForPlanDistribution = getOrInitEcharts(containerId);
    pieChartForPlanDistribution.setOption({
        legend: {
            data: pieSource.map(e=>e.name),
            z:500
        },
        series: [{
            type:"pie",
            data: pieSource,
            radius:radius,
            label:{
                overflow:"break",
            }
        }],
        label: {
            alignTo: 'edge',
            formatter: function(params){
                return '{name|'+params.data.name+'}\n{time|'+transValueToLabel(params.data.value)+'}';
            },
            minMargin: 5,
            edgeDistance: 10,
            lineHeight: 15,
            rich: {
                time: {
                    fontSize: 10,
                    color: '#999'
                }
            }
        },
        itemStyle: {
            borderColor: '#fff',
            borderWidth: 1
        },
        labelLine: {
            length: 15,
            length2: 0,
            maxSurfaceAngle: 80
        },
        labelLayout : function (params) {
            var isLeft = params.labelRect.x < pieChartForPlanDistribution.getWidth() / 2;
            var points = params.labelLinePoints;
            // Update the end point.
            points[2][0] = isLeft
                ? params.labelRect.x
                : params.labelRect.x + params.labelRect.width;

            return {
                labelLinePoints: points
            };
        }
    });
}


/**
 * 根据planItemId merge
 * 去掉进行中的 
 * 去掉同步项
 */
 function mergeWorkItemsExceptUndone(content){
    let rlt = [];
    
    /*去掉进行中的 同步项*/
    content.workItems.filter(w => w.item.endTime != 0 && w.item.type.dbCode != BASIC_NAMESPACE.WORK_ITEM_TYPE_OF_DEPT).forEach(item=>{
        let planItem = getPlanItemOfWorkItem(item,content.planItems);
        let existedItem = rlt.find(p=>p.pItem==planItem);
        let costMinutes = new Date(item.item.endTime).countMinutesDiffer(new Date(item.item.startTime));

        if(existedItem != undefined){
            existedItem.costMinutes = existedItem.costMinutes + costMinutes;
            return ;
        }

        rlt.push({
            pItem : planItem,
            costMinutes:costMinutes
        })
    })

    return rlt;    
}

function getPlanItemOfWorkItem(workItem,basePlanItems){
    let planItem;
    traverseWSPlanItems(basePlanItems,(plan)=>{
        if(plan.item.id==workItem.item.planItemId){
            planItem = plan;
        }
    })
    return planItem;
}


function traverseWSPlanItems(items,func){
    items.forEach((item)=>{
        func(item);
        traversePlanItems(item.descendants,func);
    })
}


function traversePlanItems(items,func){
    items.forEach((item)=>{
        func(item);
        traversePlanItems(item.descendants,func);
    })
}