claimPageNonRequireSignIn();

//DONE
$(function(){
    //DONE
    drawWebInfoTitles();
    //DONE
    $("#web_info_main_invagation_container").on("click","[container_id]",function(){
        switchWebInfoContainer($(this).attr("container_id"),$(this).text());
    });
    //DONE
    $(".go_click_connection_btn").click(()=>{
        $("#web_info_main_invagation_container [container_id='web_info_connect_with_us']").click();
    })
    //DONE
    let goToContainerId = getUrlParam("container") == undefined ? "web_info_what_sm_to_do" :  getUrlParam("container");
    //DONE
    $("#web_info_main_invagation_container [container_id='"+goToContainerId+"']").click();
    //DONE
    $("#web_info_connect_with_us").find(".common_copy_font").click(copyConnectionInfoToClipboard);
})  

function copyConnectionInfoToClipboard(){
    let $container = $(this).parents(".web_info_connect_unit");
    let copyId = $(this).attr("copy_id");
    copyToClipboardInDefault($("#"+copyId).text());
    showForAWhile("复制成功",$container.find(".web_info_copy_hint"));
}

function switchWebInfoContainer(containerId,title){
    $("#web_info_main_context_container>div").hide();
    $("#"+containerId).show().find(".web_info_sub_header_title").text(title);
}


function drawWebInfoTitles(){
    BASIC_NAMESPACE.INFORMATION_GROUPS.forEach(group=>{
        let $group = $("#web_info_pattern_container").find(".web_info_title_container").clone();
        $group.find(".web_info_title").text(group.name);
        group.items.forEach(item=>{
            let $span = $("<span>");
            $span.attr("container_id",item.containerId).text(item.name)
            $group.find(".web_info_items").append($span);
        });
        $("#web_info_footer_titles_container").append($group);
    })
}
