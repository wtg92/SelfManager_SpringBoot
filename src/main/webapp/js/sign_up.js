
claimPageRequireSignOut();

$(function(){
    initSignUpPwdVisibilityUI();

    $(".sign_up_control_pwd_visibility_container").click(swithSignOnPwdVisbiiltiy);
    /*MagicNumber 但先不管了，表名未知性别*/
    $("#sign_up_switch_to_select_gender").find("[value='4']").click();

    $(".sign_up_option_for_email_or_tel [type='checkbox']").click(switchToInputEmailOrTel);

    $(".sign_up_entity_for_sign_up [name='account']").blur(function(){
        if(testSignUpFieldFormat(this,checkSignUpAccountLegal,"5-20个以字母开头，可带数字、下划线的字符")){
            testSignUpUnqiueField(this);
        }
    });
    $(".sign_up_entity_for_sign_up [name='pwd']").blur(function(){
        testSignUpFieldFormat(this,checkSignUpPwdLegal,"包含字母，数字，至少8位");
    });
    $(".sign_up_entity_for_sign_up [name='nick_name']").blur(function(){
        if(testSignUpFieldFormat(this,checkSignUpNickNameLegal,"1-10个字符")){
            testSignUpUnqiueField(this);
        }
    });
    $(".sign_up_entity_for_sign_up [name='email']").blur(function(){
        if(testSignUpFieldFormat(this,checkSignUpEmailLegal,"请填写正确的邮箱地址")){
            testSignUpUnqiueField(this,()=>unlockEmailOrTelVerifyCodeInputs(true),()=>lockAndClearEmailOrTelVerifyCodeInputs(true));
        }else{
            lockAndClearEmailOrTelVerifyCodeInputs(true);
        }
    }).change(function(){
        $(".sign_up_send_verify_code_container").find("[name='email_verify_code']").val("");
    });
    $(".sign_up_entity_for_sign_up [name='tel']").blur(function(){
        if(testSignUpFieldFormat(this,checkSignUpTelLegal,"请填写正确的手机号")){
            testSignUpUnqiueField(this,()=>unlockEmailOrTelVerifyCodeInputs(false),()=>lockAndClearEmailOrTelVerifyCodeInputs(false));
        }else{
            lockAndClearEmailOrTelVerifyCodeInputs(false);
        }
    }).change(function(){
        $(".sign_up_send_verify_code_container").find("[name='tel_verify_code']").val("");
    })
    $(".sign_up_entity_for_sign_up.eamil_or_tel .sign_up_send_verify_code_container [type='text']").blur(function(){
        testVerifyCodeNotNull(this);
    });

    $("#sign_up_send_email_verfy_code_button").click(openImgCheckDialogForEmail);
    $("#sign_up_send_tel_verfy_code_button").click(openImgCheckDialogForTel);

    bindYZMEvents();

    lockAndClearEmailOrTelVerifyCodeInputs(true);
    lockAndClearEmailOrTelVerifyCodeInputs(false);

    $("#sign_up_commit_button").click(commitSignUpInfo)
});

function commitSignUpInfo(){
    $container = $("#sign_up_information_container");
    let accountTest = parseToBool($container.find("[name='account']").parents(".sign_up_entity_for_sign_up").attr("test"));
    let pwdTest = parseToBool($container.find("[name='pwd']").parents(".sign_up_entity_for_sign_up").attr("test"));
    let nickNameTest = parseToBool($container.find("[name='nick_name']").parents(".sign_up_entity_for_sign_up").attr("test"));
    let emailAndTelTest = $container.find(".sign_up_entity_for_sign_up.eamil_or_tel").get().every(one=>{
        if($(one).hasClass("email_or_tel_null"))
            return true;

        return parseToBool($(one).attr("test")) && parseToBool($(one).attr("verify_code_test"));
    })
    if(!accountTest||!pwdTest||!nickNameTest||!emailAndTelTest){
        alertInfo("请根据提示填写好必要信息（如不想填写手机号或邮箱，请勾选对应按钮），再进行注册");
        return;
    }
    
    let signUpText = $("#sign_up_commit_button").text();
    $("#sign_up_commit_button").text("注册中......").addClass("common_waiting_button");

   let param = $("#sign_up_information_container>form").serializeArray();
   param.push({
       "name" : "temp_user_id",
       "value" : sessionStorage[CONFIG.TEMP_USER_ID_KEY]
   })

   sendAjax("UserServlet","u_sign_up",param,signUp_render,false,()=>{},()=>$("#sign_up_commit_button").text(signUpText).removeClass("common_waiting_button"));
}
function signUp_render(){
    let seconds = 2;
    alertInfo("注册成功！"+seconds+"秒后自动跳转回登录页面");
    setTimeout(()=>{
        goToPage(CONFIG.DEFAULT_SIGN_OUT_PAGE)
    },seconds*1000);
}



function lockAndClearEmailOrTelVerifyCodeInputs(forEmail){
    let $container = $(".sign_up_send_verify_code_container[for_email='"+forEmail+"']");
    $container.addClass("sign_up_lock").find("[type='text']").text("");
}

function unlockEmailOrTelVerifyCodeInputs(forEmail){
    $(".sign_up_send_verify_code_container[for_email='"+forEmail+"']").removeClass("sign_up_lock");
}


function initYZMUI(){
    drawCommonIcon("including_right_arrow",$("#sign_up_rignt_arrow"));
    $(".verify-move-block").css('left',  "0");
    $(".verify-left-bar").css('width', "0");
    $("#sign_up_yzm_cut_img").css("left","0");
    $(".verify-bar-area>.verify-msg").text("向右滑动完成验证");
    $("#sign_up_rignt_arrow").removeClass("wrong").removeClass("right");
}
/*实在抱歉 这个函数写的太恶心了 但我实在是不想再改了 凑合能用就行*/
function bindYZMEvents(){

    let startFlag = false;
    let finalXOffset;
    $(".verify-move-block").on('touchstart', function(e) {
        start(e);
    }).on('mousedown', function(e) {
        start(e);
    });

    $(window).on('touchmove', function(e) {
        move(e);
    }).on('mousemove', function(e) {
        move(e);
    }).on('touchend', function(e) {
        end(e);
    }).on('mouseup', function(e) {
        end(e);
    });



    function start(e){
        e.stopPropagation();
        $(".verify-bar-area>.verify-msg").text("");
        $(".verify-bar-area").addClass("start_moving");
        startFlag = true;
        xOffset = 0;

    }


    function move(e){
        if(startFlag) {
            let xOffset;
            var x
            if(!e.touches) {    
                x = e.clientX;
            }else {    
                x = e.touches[0].pageX;
            }
            
            let $barArea = $(".verify-bar-area");
            var bar_area_left = $barArea.offset().left; 
            xOffset = x - bar_area_left; //小方块相对于父元素的left值
            let moveBlockHalfWidth = $(".verify-move-block").width()/2;
            if(xOffset >= $barArea.offsetWidth - moveBlockHalfWidth) {
                xOffset = $barArea.offsetWidth - moveBlockHalfWidth;
            }
                
            if(xOffset <= 0) {
                xOffset = moveBlockHalfWidth;
            }
            
            //拖动后小方块的left值
            $(".verify-move-block").css('left', xOffset-moveBlockHalfWidth + "px");
            $(".verify-left-bar").css('width', xOffset-moveBlockHalfWidth + "px");
            $("#sign_up_yzm_cut_img").css("left",xOffset-moveBlockHalfWidth + "px");
        }
    }


    function end(e){
        if(startFlag){
            try{
                var childPos = $(".verify-move-block").offset();
                var parentPos = $(".verify-move-block").parent().offset();
                finalXOffset = (childPos.left-parentPos.left).toFixed(0);
                sendAjax("UserServlet","u_check_yzm",{
                    /*做一下约分*/
                    "x" : finalXOffset,
                    "temp_user_id" : sessionStorage[CONFIG.TEMP_USER_ID_KEY],
                    "img_src" : $("#sign_up_yzm_src").val(),
                    "for_email" : $("#sign_up_yzm_for_email").val(),
                },checkYzm_render,false);
            }finally{
                startFlag = false;
                $(".verify-bar-area").removeClass("start_moving");
            }     
        }

      
    }

    function checkYzm_render(data){
        try{
            if(data.checkSeccuss){
                let val = $("#sign_up_yzm_email_or_tel").val();
                let forEmail = parseToBool($("#sign_up_yzm_for_email").val());
                sendAjax("UserServlet","u_send_verify_code",{
                    "val":val,
                    "for_email" : forEmail,
                    "x":finalXOffset,
                    "temp_user_id" : sessionStorage[CONFIG.TEMP_USER_ID_KEY]
                },(data)=>sendVerifyCode_render(data,forEmail),false)
                return 
            }
            $("#sign_up_rignt_arrow").html("X").addClass("wrong");
            getYZM_render(data)
        }finally{
            finalXOffset=0;
        }

    }
}

function sendVerifyCode_render(data,forEmail){
    $("#sign_up_rignt_arrow").html("√").addClass("right");
    $("#sign_up_img_drag_check_dialog").modal("hide");
    let $sender = forEmail ? $("#sign_up_send_email_verfy_code_button") : $("#sign_up_send_tel_verfy_code_button")
    let countMinues = 30;
    $sender.text("没收到？"+countMinues+"秒后可以重发").addClass("sign_up_lock_send_verify_code");
    let intervalId = setInterval(()=>{
        countMinues--;
        $sender.text("没收到？"+countMinues+"秒后可以重发");
         if(countMinues == 0){
            $sender.text("发送验证码").removeClass("sign_up_lock_send_verify_code");
            clearInterval(intervalId);
        }

    },1000);

    $(".sign_up_send_verify_code_container[for_email='"+forEmail+"']").find("[type='text']").focus();
}
function openImgCheckDialogForTel(){
    $("#sign_up_img_drag_check_dialog").modal("show");
    $("#sign_up_yzm_for_email").val(false);
    let email =  $("#sign_up_information_container [name='tel']").val();
    $("#sign_up_yzm_email_or_tel").val(email);

    let param = {
        "temp_user_id" : sessionStorage[CONFIG.TEMP_USER_ID_KEY],
        "for_email" : false,
    }
    $("#sign_up_yzm_hint_when_loading").show();
    $("#sign_up_yzm_background_img").css("background","");
    $("#sign_up_yzm_cut_img").css("src","");

    sendAjax("UserServlet","u_get_yzm",param,getYZM_render,false); 
}


function openImgCheckDialogForEmail(){
    $("#sign_up_img_drag_check_dialog").modal("show");
    $("#sign_up_yzm_for_email").val(true);
    let email =  $("#sign_up_information_container [name='email']").val();
    $("#sign_up_yzm_email_or_tel").val(email);

    let param = {
        "temp_user_id" : sessionStorage[CONFIG.TEMP_USER_ID_KEY],
        "for_email" : true,
    }
    $("#sign_up_yzm_hint_when_loading").show();
    $("#sign_up_yzm_background_img").css("background","");
    $("#sign_up_yzm_cut_img").css("src","");

    sendAjax("UserServlet","u_get_yzm",param,getYZM_render,false);
}

function getYZM_render(data){
    initYZMUI();
    $("#sign_up_yzm_hint_when_loading").hide();

    let $backGroundContianer = $("#sign_up_yzm_background_img");
    let $cutImg = $("#sign_up_yzm_cut_img");
    $cutImg.css("top",data.yOffset+"px");
    $backGroundContianer.css({
        "width" : data.widthForSrc,
        "height" : data.heightForSrc
    });
    setBase64Img(data.cutImgBase64,$cutImg);
    setBase64BackGround(data.backgroundImgBase64,$backGroundContianer);
    $("#sign_up_yzm_src").val(data.srcImg);
}

function testVerifyCodeNotNull(inputDom){
    let $container = $(inputDom).parents(".sign_up_entity_for_sign_up");
    let test = $container.hasClass("email_or_tel_null") || $container.find(".sign_up_send_verify_code_container").hasClass("sign_up_lock")
        || $container.find(".sign_up_send_verify_code_container").find("[type='text']").val().length>0;
    $container.attr("verify_code_test",test)
        .find(".sign_up_error_container_for_verify_code").toggle(!test).text(test?"":"请填写验证码");
}

/*return 是否无重复*/
function testSignUpUnqiueField(inputDom,succFunc,failFunc){
    let $container = $(inputDom).parents(".sign_up_entity_for_sign_up");
    let val=  $(inputDom).val();
    let field = $(inputDom).attr("field");
    sendAjax("UserServlet","u_exists_user_with_field",{
        "val" :val,
        "field" : field
    },(data)=>{
        let $errorContainer= $container.find(".sign_up_error_container");
        $errorContainer.toggle(data.rlt)
        if(data.rlt){
            $container.attr("test",!data.rlt);
            $errorContainer.text("该"+$container.find(".sign_up_title").text() +"已存在");
            if(failFunc!=undefined)
                failFunc();
            return false;
        }else{
            if(succFunc!=undefined)
                succFunc();

            $errorContainer.text("");
            return true;
        }
    },false)
}




/*return 是否符合校验成功*/
function testSignUpFieldFormat(inputDom,checkFunc,errorAppendInfo){
    /*假如清空了就不校验了*/
    let text=  $(inputDom).val();
    let test =text.length == 0 ? true : checkFunc(text);
    let $container = $(inputDom).parents(".sign_up_entity_for_sign_up");
    $container.attr("test",test);
    let $errorContainer= $container.find(".sign_up_error_container");
    $errorContainer.toggle(!test);
    if(!test){
        $errorContainer.text("格式有误，"+errorAppendInfo);
    }else{
        $errorContainer.text("");
    }
    return test && text.length != 0;
}

/*邮箱或手机号必须填写一个*/
function checkSignUpEmailOrTelLegal(){
    return $(".sign_up_option_for_email_or_tel [type='checkbox']:checked").length < 2; 
}

function switchToInputEmailOrTel(){
    let choosed = $(this).prop("checked");
    let $container = $(this).parents(".sign_up_entity_for_sign_up.eamil_or_tel");
    if(choosed){
        /*2个*/
        if(!checkSignUpEmailOrTelLegal()){
            alertInfo("请至少完成一个手机或邮箱验证")
            $(this).prop("checked",false);
            return;
        }
    }
    $container.find("[name='email'],[name='tel']").val("").blur().end().toggleClass("email_or_tel_null",choosed);
}



function swithSignOnPwdVisbiiltiy(){
    let pwdNone = parseToBool($(this).attr("none"));
    $(this).attr("none",!pwdNone);
    let $pwdInput = $(this).parents(".sign_up_entity_for_sign_up").find("[name='pwd']");
    if(pwdNone){
        drawEye($(this));
        $pwdInput.prop("type","text");
        return;
    }
    $pwdInput.prop("type","password");
    drawEyeSlash($(this));
}

function initSignUpPwdVisibilityUI(){
    $(".sign_up_entity_for_sign_on").find("[name='pwd']").prop("type","password");
    let $eyeContainer = $(".sign_up_control_pwd_visibility_container");
    $eyeContainer.attr("none",true);
    drawEyeSlash($eyeContainer);
}



/*5-20个以字母开头，可带数字、下划线*/ 
function checkSignUpAccountLegal(text){
    let re =/^[a-zA-Z]{1}([a-zA-Z0-9]|[_]){4,19}$/;  
    return re.test(text);
}

function checkSignUpNickNameLegal(text){
    return text.trim().length >0 && text.trim().length <11;
}

