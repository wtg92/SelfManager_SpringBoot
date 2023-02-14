$(function (){
    drawFooterInformationGroups();
    $("#footer_information_groups_container").on("click","[container]",function(){
        window.location.href = "web_info.jsp?container="+$(this).attr("container")
    });

    reloadAppStartTimeInfo();
});


function reloadAppStartTimeInfo(){
    sendGet("common/getBasicInfo",(data)=>{
        let timeInfo = new Date(data.data.appStartTime)
        $("#bei_an_mes_container .version_mes>span").text(timeInfo);
    })
}



function drawFooterInformationGroups(){
    BASIC_NAMESPACE.INFORMATION_GROUPS.forEach(group=>{
        let $group = $("#footer_pattern_container").find(".info_group_container").clone();
        $group.find(".name").text(group.name);
        group.items.forEach(item=>{
            let $li = $("<li>");
            let $span = $("<span>");
            $span.attr({
                "container":item.containerId,
                "code":item.code
            }).text(item.name).addClass("common_hover");
            $li.append($span);
            $group.find(".items ul").append($li);
        });
        $("#footer_information_groups_container").append($group);
    })
}