

$(function(){
    resetHeaderContainer();
    $("#header_sign_out").click(signOut);
    drawCommonIcon("including_avator",$("#header_avator_container"));
    $("#middle_flex_header_sign_in_container .common_hover_font").click(goToModule);
    $(".web_logo").click(goBackToDefaultPage);
});
function goBackToDefaultPage(){
    if(sessionStorage[CONFIG.SIGN_IN_KEY]){
        goToPage(CONFIG.DEFAULT_GOING_TO_PAGE);
    }else{
        goToPage(CONFIG.DEFAULT_SIGN_OUT_PAGE);
    }
}


function goToModule(){
    let href = $(this).attr("page");
    goToPage(href);
}

function signOut(){
    clearSignInInfo();
    goToPage(CONFIG.DEFAULT_SIGN_OUT_PAGE);
}


function switchToShwoHeaderContainer(id){
    $("#header_main_container").children("div").hide()
        .end().find("#"+id).show();
}

function resetHeaderContainer(){
    if(!sessionStorage[CONFIG.SIGN_IN_KEY]){
        switchToShwoHeaderContainer("header_sign_out_container");
        return;
    }

    switchToShwoHeaderContainer("header_sign_in_container");
    $("#middle_flex_header_sign_in_container>.users_module_button").toggle(parseToBool(sessionStorage[CONFIG.SEE_USERS_MODULE_KEY]));
    $("#middle_flex_header_sign_in_container>.notes_module_button").toggle(parseToBool(sessionStorage[CONFIG.SEE_NOTES_MODULE_KEY]));
}