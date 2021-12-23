
let TAG_EDIT_DIALOG_NAMESPACE = {
    /** 
     * 提供给外部模块的重置标签函数，在点击保存标签时调用，第一个参数为用户选择的标签，第二个参数为dialog关闭函数，第三个参数为取消保存按钮的禁止点击的函数
    */
    RESET_TAGS_FUNC:null,
};

$(function(){
    $("#tag_edit_dialog_add_tag_btn").click(addTagToList);
    $("#tag_edit_dialog_tag_list_container").on("click",".tag_unit_close_icon",function(){
        $(this).parents(".tag_unit_container").remove();
    });
    $("#tag_edit_dialog_reset_tags_btn").click(resetTags);
})

function resetTags(){

    $(this).addClass("common_prevent_double_click");

    let tags = $("#tag_edit_dialog_tag_list_container").find(".tag_unit_container_content").get().map(e=>$(e).text());

    TAG_EDIT_DIALOG_NAMESPACE.RESET_TAGS_FUNC(tags,()=>{
        $("#tag_edit_dialog").modal("hide")
    },()=>{
        $(this).removeClass("common_prevent_double_click");
    });
}



function openTagEditDialogForExternalModule(resetTagsFunc){
    TAG_EDIT_DIALOG_NAMESPACE.RESET_TAGS_FUNC = resetTagsFunc;
    $("#tag_edit_dialog").modal("show");
    $("#tag_edit_dialog_add_tag_input").val("");
}

function addTagToList(){
    let $input = $("#tag_edit_dialog_add_tag_input");
    let val = $input.val();
    if(val.trim().length==0){
        alertInfo("请填写标签名");
        return;
    }

    let dup = $("#tag_edit_dialog_tag_list_container").find(".tag_unit_container_content").get().filter(e=>$(e).text() == val).length > 0;
    if(dup){
        alertInfo("标签重复 "+val);
        return;
    }

    let $unit = $("#tag_edit_dialog_pattern_container .tag_unit_container").clone();
    $unit.find(".tag_unit_container_content").text(val);
    $("#tag_edit_dialog_tag_list_container").append($unit);
    $input.val("");
}
