
 const CONFIG = {
    /*  localStorage  */
    /* user token 里 应当存储 id和密码的信息 每次baiscJS需要验证 当验证成功ok 验证失败的话 跳回登录页面 并给予一定提示信息 同时获取tempId
     * 当登陆时选择记住我使用 
     */
    USER_TOKEN_KEY : "user_token",

    /* sessionStorage */ 
    /* 是否为登录状态每次进来需要验证一次 这个 会保存UserToken信息 供后续使用 */
    SIGN_IN_KEY : "sign_in",
    /*sessionStorage 每次刷新页面都要重新获取一次*/
    TEMP_USER_ID_KEY:"temp_user_id",
    SEE_USERS_MODULE_KEY : "see_users_module",
    SEE_NOTES_MODULE_KEY : "see_notes_module",

    /* currentPage */
    /*假如判断没有登录 true：会自动跳转到首页（默认） false:不做处理 */
    REQUIRE_SIGN_IN: true,
    /*假如判断为登录状态 true：则自动跳转到该去的页面 false:不做处理（默认）  */
    REQUIRE_SIGN_OUT:false,
    /*登录后默认跳转页面 每个人一定有这个模块的权限*/
    DEFAULT_GOING_TO_PAGE : "work.jsp",
    DEFAULT_SIGN_OUT_PAGE : "index.jsp",

    DATATABLE_LANG_CONFIG : {
        "sLengthMenu" : "每页显示 _MENU_ 条记录",
        "sZeroRecords" : "无",
        "sInfo" : "共 _TOTAL_ 条",
        "sInfoEmpty" :"",
        "sInfoFiltered" :"",
        "sSearch" : "搜索",
        "oPaginate" : {
            "sPrevious" : "上一页",
            "sNext" : "下一页",
            "sLast" : "尾页",
            "sFirst":"首页"
        }
    }

};

/*这两个函数 不能在页面加载后完成 而是在对应页面的js开头声明*/
function claimPageNonRequireSignIn(){
    CONFIG.REQUIRE_SIGN_IN = false;
}

function claimPageRequireSignOut(){
    claimPageNonRequireSignIn();
    CONFIG.REQUIRE_SIGN_OUT = true;
}


 $(function(){
    if(!storageAvailable('localStorage')){
        alertInfo("浏览器版本过低或设置了较严格的隐私策略，请选择较高版本（推荐谷歌）或调整隐私策略（默认）");
    }
    if(!storageAvailable('sessionStorage')){
        alertInfo("浏览器版本过低或设置了较严格的隐私策略，请选择较高版本（推荐谷歌）或调整隐私策略（默认）");
    }
 });


 function storageAvailable(type) {
    var storage;
    try {
        storage = window[type];
        var x = '__storage_test__';
        storage.setItem(x, x);
        storage.removeItem(x);
        return true;
    }
    catch(e) {
        return e instanceof DOMException && (
            // everything except Firefox
            e.code === 22 ||
            // Firefox
            e.code === 1014 ||
            // test name field too, because code might not be present
            // everything except Firefox
            e.name === 'QuotaExceededError' ||
            // Firefox
            e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
            // acknowledge QuotaExceededError only if there's something already stored
            (storage && storage.length !== 0);
    }
}