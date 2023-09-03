const INDEX_NAMESPACES = {
    EMAIL_METHOD:2,
    TEL_METHOD:3
}

//TODO Migrate.
claimPageRequireSignOut();

/**
 * 每次进入该页面需要验证登录信息 假如登录成功会跳转
 */
$(function(){
    $(".intex_control_reset_pwd_visibility_container").click(function(){
        switchPwdVisibility(this,$(this).parents("#index_reset_pwd_form").find("[name='reset_pwd_val']"));
    });


    //DONE
    initPwdVisibilityUI();
    //DONE
    $("#index_switch_log_in_method_container .btn-group").on("click","input",switchSignInMethod)
        .find("input").eq(0).click();
    //DONE
    $(".index_control_pwd_visibility_container").click(function(){
        switchPwdVisibility(this, $(this).parents(".index_entity_for_sign_in").find("[name='account_pwd'],[name='email_pwd']"));
    });


    $("#index_sign_in_button").click(signIn);

    $("#index_send_email_verify_code_btn").click(sendEmailVerifyCodeForSignIn);
    $("#index_send_tel_verify_code_btn").click(sendTelVerfyCodeForSignIn);
    $("#index_forgot_account_btn").click(openRetrieveAccountDialog);
    $("#send_account_for_find_account_btn").click(retrieveAccount);
    $("#index_forgot_pwd_btn").click(openResetPwdDialog);

    $("#send_verify_code_for_reset_pwd_btn").click(sendVerifyCodeForResetPwd);
    $("#commit_reset_pwd_btn").click(commitResetPWD);

});



function openResetPwdDialog(){
    let $dialog = $("#index_reset_pwd_dialog");
    $dialog.find("[type='text'],[type='password']").val("").end().modal("show");
    initResetPwdUI();

    $("#reset_pwd_dialog_step_one_container").removeClass("lock_for_reset_pwd");
    $("#reset_pwd_dialog_step_two_container,#commit_reset_pwd_btn").addClass("lock_for_reset_pwd");

}


function openRetrieveAccountDialog(){
    let $dialog = $("#index_find_account_dialog");
    $dialog.find("[type='text']").val("").end().modal("show");
    initBootStrapBtns($("#index_switch_find_account_method_container"));
}

function sendVerifyCodeForResetPwd(){
    let $dialog = $("#index_reset_pwd_dialog");
    let method = $dialog.find("[name='method']:checked").val();
    if(!method){
        alertInfo("请选择一种验证方式");
        return;
    }

    let val  = $dialog.find("[name='verify_src']").val();
    if(parseInt(method) == INDEX_NAMESPACES.EMAIL_METHOD){
        if(!checkSignUpEmailLegal(val)){
            alertInfo("邮箱格式错误，请检查");
            return;
        }
    }else{
        if(!checkSignUpTelLegal(val)){
            alertInfo("手机号格式错误，请检查");
            return;
        }
    }

    let account = $dialog.find("[name='account']").val();
    if(!checkSignUpAccountLegal(account)){
        alertInfo("账号格式错误，请检查");
        return;
    }



    $(this).addClass("common_prevent_double_click");
    sendAjax("UserServlet","u_send_verify_code_for_reset_pwd",{
        "method" : method,
        "val" :val,
        "account" : account
    },()=>{
        let countMinues = 30;
        $(this).text("没收到？"+countMinues+"秒后可以重发").addClass("sign_in_lock_send_verify_code");
        let intervalId = setInterval(()=>{
            countMinues--;
            $(this).text("没收到？"+countMinues+"秒后可以重发");
             if(countMinues == 0){
                $(this).text("发送验证码").removeClass("sign_in_lock_send_verify_code");
                clearInterval(intervalId);
            }
        },1000);

        $("#reset_pwd_dialog_step_one_container").addClass("lock_for_reset_pwd");
        $("#reset_pwd_dialog_step_two_container,#commit_reset_pwd_btn").removeClass("lock_for_reset_pwd");



    },false,()=>{},()=>{
        $(this).removeClass("common_prevent_double_click");
    });
}

function commitResetPWD(){
    let params = $("#index_reset_pwd_form").serializeArray();


    if(!checkSignUpPwdLegal(params.find(e=>e.name=='reset_pwd_val').value)){
        alertInfo("重置密码格式错误");
        return;
    }

    if(params.find(e=>e.name=='verify_code').value.length == 0){
        alertInfo("请填写验证码");
        return;
    }

    let $button  =$("#commit_reset_pwd_btn");
    let btnText = $button.text();
    $button.text("重置中......").addClass("common_waiting_button");

    sendAjax("UserServlet","u_reset_pwd",params,(data)=>{
        alertInfo("重置成功");
        $("#index_reset_pwd_dialog").modal("hide");
    },false,()=>{}
        ,()=>$button.text(btnText).removeClass("common_waiting_button"));
}



function retrieveAccount(){
    let $dialog = $("#index_find_account_dialog");
    let val  = $dialog.find("[name='retrieve_account_val']").val();
    let method = $dialog.find("[name='retrieve_method']:checked").val();
    if(!method){
        alertInfo("请选择一种验证方式");
        return;
    }

    if(parseInt(method) == INDEX_NAMESPACES.EMAIL_METHOD){
        if(!checkSignUpEmailLegal(val)){
            alertInfo("请填写合法的邮箱地址");
            return;
        }
    }else{
        if(!checkSignUpTelLegal(val)){
            alertInfo("请填写合法的手机号");
            return;
        }
    }

    $(this).addClass("common_prevent_double_click");
    sendAjax("UserServlet","u_retrieve_account",{
        "method" : method,
        "val" :val
    },()=>{
        let countMinues = 30;
        $(this).text("没收到？"+countMinues+"秒后可以重发").addClass("sign_in_lock_send_verify_code");
        let intervalId = setInterval(()=>{
            countMinues--;
            $(this).text("没收到？"+countMinues+"秒后可以重发");
             if(countMinues == 0){
                $(this).text("发送验证码").removeClass("sign_in_lock_send_verify_code");
                clearInterval(intervalId);
            }
        },1000);
    },false,()=>{},()=>{
        $(this).removeClass("common_prevent_double_click");
    });
}


function sendEmailVerifyCodeForSignIn(){
    let val = $(".index_entity_for_sign_in [name='email']").val();
    if(!checkSignUpEmailLegal(val)){
        alertInfo("请填写合法的邮箱地址");
        return;
    }

    $(this).addClass("common_prevent_double_click");

    sendAjax("UserServlet","u_send_email_verify_code_for_sign_in",{
        "email" :val
    },()=>{
        let countMinues = 30;
        $(this).text("没收到？"+countMinues+"秒后可以重发").addClass("sign_in_lock_send_verify_code");
        let intervalId = setInterval(()=>{
            countMinues--;
            $(this).text("没收到？"+countMinues+"秒后可以重发");
             if(countMinues == 0){
                $(this).text("发送验证码").removeClass("sign_in_lock_send_verify_code");
                clearInterval(intervalId);
            }
        },1000);
    },false,()=>{},()=>{
        $(this).removeClass("common_prevent_double_click");
    });
}

function sendTelVerfyCodeForSignIn(){
    let val = $(".index_entity_for_sign_in [name='tel']").val();
    if(!checkSignUpTelLegal(val)){
        alertInfo("请填写合法的手机号");
        return;
    }

    $(this).addClass("common_prevent_double_click");

    sendAjax("UserServlet","u_send_tel_veirfy_code_for_sign_in",{
        "tel" :val
    },()=>{
        let countMinues = 30;
        $(this).text("没收到？"+countMinues+"秒后可以重发").addClass("sign_in_lock_send_verify_code");
        let intervalId = setInterval(()=>{
            countMinues--;
            $(this).text("没收到？"+countMinues+"秒后可以重发");
             if(countMinues == 0){
                $(this).text("发送验证码").removeClass("sign_in_lock_send_verify_code");
                clearInterval(intervalId);
            }
        },1000);
    },false,()=>{},()=>{
        $(this).removeClass("common_prevent_double_click");
    });
}




function signIn(){
    
    let checkNotNullAndShowHint = $("#index_sign_in_main_container>div:visible").find(".index_entity_for_sign_in").get().every((one)=>{
        let notNull = $(one).find("[type='text'],[type='password']").val().trim().length>0;
        let title = $(one).find(".sign_in_title").text();
        $(one).attr("test",notNull).find(".index_sign_in_error_container").toggle(!notNull).text("请填写"+title.trim());
        return notNull;
    })

    if(!checkNotNullAndShowHint){
        return;
    }

    let signInText = $("#index_sign_in_button").text();
    $("#index_sign_in_button").text("登陆中......").addClass("common_waiting_button");

    let params = $("#index_sign_in_info_container>form").serializeArray();
    let rememberMe = $("#index_select_remember_me_info [name='remember_me']").prop("checked");
    params.push({
        "name" : "temp_user_id",
        "value" : sessionStorage[CONFIG.TEMP_USER_ID_KEY]
    })
    sendAjax("UserServlet","u_sign_in",params,(data)=>signIn_render(data,rememberMe),false,clearSignInInfo,()=>$("#index_sign_in_button").text(signInText).removeClass("common_waiting_button"));
}

function signIn_render(data,rememberMe){
    processLoginInfo(data);
    if(rememberMe){
        localStorage[CONFIG.USER_TOKEN_KEY] = data.token;
    }
    goToPage(CONFIG.DEFAULT_GOING_TO_PAGE);
}

function switchPwdVisibility(dom,$pwdInput){
    let pwdNone = parseToBool($(dom).attr("none"));
    $(dom).attr("none",!pwdNone);

    if(pwdNone){
        drawEye($(dom));
        $pwdInput.prop("type","text");
        return;
    }
    $pwdInput.prop("type","password");
    drawEyeSlash($(dom));
}

function initResetPwdUI(){
    let $eyeContainer = $(".intex_control_reset_pwd_visibility_container");
    $("#index_reset_pwd_dialog").find("[name='reset_pwd_val']").prop("type","password");
    $eyeContainer.attr("none",true);
    drawEyeSlash($eyeContainer);
}


function initPwdVisibilityUI(){
    let $eyeContainer = $(".index_control_pwd_visibility_container");
    $(".index_entity_for_sign_in").find("[name='account_pwd'],[name='email_pwd']").prop("type","password");
    $eyeContainer.attr("none",true);
    drawEyeSlash($eyeContainer);
}

function switchSignInMethod(){
    let code  = $(this).val();
    $("#index_sign_in_main_container").find("input").val("").end()
        .children("div").hide().end().find("div[code='"+code+"'").show();
    
    initPwdVisibilityUI();
}