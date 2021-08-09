$(function(){
    loadUserSummary();
    loadAllUserGroups();
    initUsersDataTable();
    $("#users_group_detail_container").hide();
    $("#users_groups_container").on("click","[type='radio']",showGroupDetail);
    $("#users_open_group_perms_dialog").click(openGroupPermsDialog);
    $("#users_override_group_perms_button").click(overrideGroupPerms);
});

function overrideGroupPerms(){
    let groupId = $("#users_target_group_id").val();
    let permsId = $(".users_one_group_perms_container").find(":checked").get().map(one=>$(one).val());
    sendAjax("UserServlet","u_override_group_perms",{
        "group_id" : groupId,
        "perms" : permsId
    },()=>{
        alertInfo("设置成功");
        $("#users_group_perms_dialog").modal("hide");
    })
}


function openGroupPermsDialog(){
    let groupId = $("#users_groups_container").find(":checked").val();
    $("#users_target_group_id").val(groupId);

    sendAjax("UserServlet","u_load_perms_of_group",{
        "group_id" : groupId,
    },(data)=>{
        $(".users_one_group_perms_container").find("label").filter((i,v)=>$(v).find("input").prop("checked")).button('toggle');
        data.forEach(perm=>{
            $(".users_one_group_perms_container").find("label").filter((i,v)=>parseInt($(v).find("input").val())==perm.dbCode).button('toggle');
        })
        $("#users_group_perms_dialog").modal("show");
    })

}



function initUsersDataTable(){
    let $table = $("#users_datatable_container table");
    $table.DataTable({
        "oLanguage" : CONFIG.DATATABLE_LANG_CONFIG,
        "columns":[{
            "title":"昵称",
        },{
            "title":"账号",
        },{
            "title":"性别",
        },{
            "title":"邮箱",
        },{
            "title":"手机",
        }]   

    });
}



function showGroupDetail(){
    $("#users_group_detail_container").show();
    let groupId = $(this).val();
    sendAjax("UserServlet","u_load_users_of_group",{
        "group_id" : groupId
    },(data)=>{
        let linesDate = [];
        data.forEach(user=>{
            let oneLine = [];
            oneLine.push(user.user.nickName);
            oneLine.push(user.user.account);
            oneLine.push(user.genderInfo);
            oneLine.push(translateUndefinedForDataTable(user.user.email));
            oneLine.push(translateUndefinedForDataTable(user.user.telNum));
            linesDate.push(oneLine);
        })

        let table = $("#users_datatable_container table").DataTable();
        table.clear();
        table.rows.add(linesDate);
        table.draw();
    })
}


function loadUserSummary(){
    sendAjax("UserServlet","u_load_user_summary",{},(data)=>{
        $("#users_user_summary_container").find(".users_sign_up_users em").text(data.countUsers)
            .end().find(".users_active_users em").text(data.countActiveUsers);
    });
}


function loadAllUserGroups(){
    let $container = $("#users_groups_container");
    $container.empty();
    sendAjax("UserServlet","u_load_all_user_groups",{},(data)=>{
        data.forEach(element => {
            let $radio = getOneRadioHtml("group_id",element.group.id,element.group.name+" ("+element.countUsers+")","btn-success");
            $container.append($radio);
        });
    });
}