const NOTE_BOOK_NAMESPACE = {
    LABELS : [],
    LABEL_TAG : "sm_label",
    WAITING_TO_SAVED_NOTES:[],
    MEMO:undefined,
    NEEDING_SAVING:false,
    INITIAL_NOTES:[]
}

$(function(){
    //DONE 去掉
    bindMemoDialogEvents();
    //DONE 去掉
    loadMemo();
    //DONE
    bindDragNavagationEvents();
    window.onbeforeunload = ()=>{
        if(NOTE_BOOK_NAMESPACE.NEEDING_SAVING){
            return "您还没有保存，确定要离开吗";
        }
    };
    //DONE
    initNoteBookContentUI();
    //DONE
    $("#note_book_content_main_header_units_container").on("click",".note_book_for_unit_header_navagation_container",switchBookContentWindow)
        .on("click",".note_book_for_unit_header_navagation_close_window_btn",closeBookWindowByClick)

    //DONE
    $("#note_book_close_all_book_content").click(closeAllBookWindows);


    //TODO 标签管理器  我突然记起来 这还是工作表 未做完的一件事 这个的核心是 可移动窗口
    initNoteLabelsManager();
    loadNoteLabelsAndFillUI();

    $("body").on("keydown",monitorHotKeys);



    $("#note_book_content_main_body_container")
        //DONE
        .on("click",".note_book_create_note_btn",createNoteByClick)
        //DONE
        .on("click",".note_book_baisc_info_switch_container_visibility",switchToShowBookBasicInfoContainer)
        //DONE
        .on("click",".note_book_baisc_info_infos_list_main_container",switchToShowBookNoteListContainer)
        //DONE
        .on("click",".note_book_content_style_sample",chooseStyleTypeByClickForSaveBook)
        //DONE
        .on("input","[name='name']",syncNoteBookNameToSampleForSaveBook)
        //DONE
        .on("click",".note_book_baisc_info_open_edit_mode",openNoteBookBasicInfoEditModeByClick)
        //DONE
        .on("click",".note_book_baisc_info_close_edit_mode",closeNoteBookBasicInfoEditModeByClick)
        //DONE
        .on("click",".note_book_main_info_open_note_page_edit_mode",openNotePageEditModeByClick)
        //DONE
        .on("click",".note_book_main_info_close_note_page_edit_mode",closeNotePageEditModeByClick)
        .on("click",".note_book_main_info_swtich_merging_page_mode",switchNotePageMergingModeByClick)
        .on("click",".note_book_main_info_swtich_spliting_page_mode",switchNotePageSplitingModeByClick)

        //DONE
        .on("click",".note_book_main_info_show_hidden_notes",showHiddenNotesByClick)
        //DONE
        .on("click",".note_book_main_info_close_show_hidden_notes",closeShowHiddenNotesByClick)


        .on("click",".note_book_main_info_show_page_title_btn",showNotePageTitleByClick)
        .on("click",".note_book_main_info_hide_page_title_btn",hideNotePageTitleByClick)
        //DONE
        .on("click",".note_book_main_info_open_note_list_edit_mode",openNoteListEditModeByClick)
        //DONE
        .on("click",".note_book_main_info_close_note_list_edit_mode",closeNoteListEditModeByClick)
        //DONE
        .on("blur","[name='name']",function(){
            testSaveBookFormat(this, (text) => checkNoteBookName(text), "1-20个字符");
        })
        //DONE
        .on("click",".note_book_content_save_basic_info_button",commitSaveNoteBookBasicInfo)
        //DONE
        .on("input","[name='seq_weight']",inputOnlyAllowInt)
        //DONE
        .on("click",".note_book_list_item_delete_mark",deleteNoteByClick)
        .on("click",".note_book_title_item_cotnainer",switchNotePageByClick)
        .on("click",".one_note_book_content_unit_delete_mark",closeNotePageByDeleteMark)
        .on("click","sm_label[label_name='TODO']",changeTODOToDONE)
        .on("click","sm_label[label_name='DONE']",changeDONEToTODO)
        .on("click","sm_label[label_name='EM']",copyEMContent)
        .on("click",".note_book_call_out_labels_manager_btn",callOutLabelsManager)
        .on("click",".one_note_book_content_unit_page_control_mark_important,.one_note_book_content_unit_page_control_mark_general",changeNoteImportant)
        .on("click",".one_note_book_content_unit_page_control_mark_hidden,.one_note_book_content_unit_page_control_mark_show",changeNoteHidden)
        .on("click",".one_note_book_content_unit_page_control_delete_btn",deleteNoteByClickBtnInPage)

    



    bindDragNoteListUnitEvents();
    bindNotePageSaveEvents();
    


});



function copyEMContent(){
    let text = $(this).text();
    copyToClipboardInDefault(text);
}

    function banDraggingMemoItems(){
        $("#memo_dialog_body_items_container").find(".one_memo_item_unit_container").prop("draggable",false);
    }

    function unbanDraggingMemoItems(){
    $("#memo_dialog_body_items_container").find(".one_memo_item_unit_container").prop("draggable",true);
}

function bindMemoDialogEvents(){
    let $filter = $("#memo_dialog_body_items_list_for_filter");
    let $dialog = $("#notes_memo_dialog");
    let $addOrSaveingContainer = $("#memo_dialog_body_control_group_container_of_operation_btns");
    let $noteTextarea = $("#memo_note_textarea");
    let $pattern = $("#note_book_content_pattern_container");
    let $controgroupContainer = $("#memo_dialog_body_control_group_container");
    let $itemsContainer = $("#memo_dialog_body_items_container")

    drawCommonIcon("including_modify_mark",$pattern.find(".one_memo_item_unit_btns_for_modify_item"));
    drawCommonIcon("including_minus_mark",$pattern.find(".one_memo_item_unit_btns_for_delete_item"));



    /*备忘录事件*/
    $("#close_notes_memo_edit_mode_btn").click(closeMemoDialogEditMode);
    $("#open_notes_memo_edit_mode_btn").click(openMemoDialogEditMode);

    $("#show_all_memo_items_footer_btn").click(()=>{
        $itemsContainer.find(".one_memo_item_unit_container").each((i,v)=>showMemoItemFooter($(v)));
    })

    $("#hide_all_memo_items_footer_btn").click(()=>{
        $itemsContainer.find(".one_memo_item_unit_container").each((i,v)=>hideMemoItemFooter($(v)));
    })

    $addOrSaveingContainer.on("click",".dropdown-item",function(e){
        let text = $(this).text();
        $addOrSaveingContainer.find(".dropdown-toggle").text(text);
    });


    $filter.on("click",".dropdown-item",function(){
        let text = $(this).text();
        $filter.find(".dropdown-toggle").text(text);
        $dialog.attr("name_for_filter",text.trim()=="无" ? "null":text);
    });

    $noteTextarea.on("change",function(){
        let note = $(this).val();
        sendAjax("CareerServlet","c_save_memo",{
            "note" : note
        },(data)=>{
            NOTE_BOOK_NAMESPACE.MEMO=data;
            showForAWhile("已保存",$("#memo_note_changed_hint"));
        })
    });

    $("#memo_dialog_add_item_button").click(function(){
        let param = $("#memo_dialog_body_control_group_container>form").serializeArray();
        if(param.find(e=>e.name=='content').value.trim().length == 0){
            showForAWhile("请输入内容",$("#memo_dialog_save_or_add_item_hint_mes"))
            return;
        }
        $(this).addClass("common_prevent_double_click");
        param.push({
            "name":"label",
            "value":$("#memo_dialog_body_items_list_for_add_or_saving").find(".dropdown-toggle").text(),
        });
        param.push({
            "name":"src_note_id",
            "value":0,
        });
        sendAjaxBySmartParams("CareerServlet","c_add_item_to_memo",param,(data)=>{
            refreshMemoCacheAndApplyToUI(data);
        },()=>{
            $(this).removeClass("common_prevent_double_click");
        })
    });

    $("#memo_dialog_save_item_button").click(function(){
        let param = $("#memo_dialog_body_control_group_container>form").serializeArray();
        if(param.find(e=>e.name=='content').value.trim().length == 0){
            showForAWhile("请输入内容",$("#memo_dialog_save_or_add_item_hint_mes"))
            return;
        }
        $(this).addClass("common_prevent_double_click");
        param.push({
            "name":"label",
            "value":$("#memo_dialog_body_items_list_for_add_or_saving").find(".dropdown-toggle").text(),
        });
        param.push({
            "name":"item_id",
            "value":$("#memo_dialog_item_id_when_modifying").val(),
        });
        sendAjaxBySmartParams("CareerServlet","c_save_memo_item",param,(data)=>{
            refreshMemoCacheAndApplyToUI(data);
        },()=>{
            $(this).removeClass("common_prevent_double_click");
        })
    });

    $("#memo_dialog_body_items_filter_key_words").on("input",filterMemoItemsByKeyWords)

    $("#memo_dialog_give_up_save_item_button").click(()=>{
        confirmInfo("确定放弃修改吗？",()=>{
            openMemoItemControgroupAddMode();
        })
    })

    $dialog.on("click",".one_memo_item_unit_switch_footer_btn",function(){
        let open = parseToBool($(this).attr("open"));
        let $container = $(this).parents(".one_memo_item_unit_container");
        if(open){
            hideMemoItemFooter($container);
        }else{
            showMemoItemFooter($container);
        }  
    }).on("click",".one_memo_item_unit_copy_btn",function(){
        let $unit = $(this).parents(".one_memo_item_unit_container");
        copyToClipboard($unit.find(".one_memo_item_unit_content").html().replaceAll("<br>","\r\n"),$dialog);
        showForAWhile("成功",$unit.find(".one_memo_item_unit_hint"));
    }).on("click",".one_memo_item_unit_btns_for_delete_item",function(){
        confirmInfo("确定删除吗？",()=>{
            $(this).addClass("common_prevent_double_click");
            let $container = $(this).parents(".one_memo_item_unit_container");
            let itemId = $container.attr("item_id");
            sendAjaxBySmartParams("CareerServlet","c_remove_item_from_memo",{
                "item_id" : itemId
            },(data)=>{
                refreshMemoCacheAndApplyToUI(data);
            },()=>{
                $(this).removeClass("common_prevent_double_click");
            })
        })
    }).on("click",".one_memo_item_unit_btns_for_modify_item",function(){
        let $container = $(this).parents(".one_memo_item_unit_container");
        openMemoItemControgroupSaveMode();
        let itemId = $container.attr("item_id");
        $("#memo_dialog_item_id_when_modifying").val(itemId);
        $controgroupContainer.find("[name='content']").val(transferLineBreaksToText($container.find(".one_memo_item_unit_content").html())).end()
         .find("[name='note']").val(transferLineBreaksToText($container.find(".one_memo_item_unit_container_footer_note").html())).end()
        
        let labelName = $container.attr("label_name");

        $controgroupContainer.find(".dropdown-item").filter((i,v)=>$(v).text()==labelName).click();

    })
    

    $itemsContainer.on("click",".one_memo_item_unit_container[label_name='TODO'] .one_memo_item_unit_content,.one_memo_item_unit_container[label_name='TODO'] .one_memo_item_unit_label_name",function(){
        $(this).addClass("common_prevent_double_click");
        let itemId = $(this).parents(".one_memo_item_unit_container").attr("item_id");
        sendAjaxBySmartParams("CareerServlet","c_save_memo_label",{
            "item_id":itemId,
            "label":"DONE"
        },(data)=>{
            refreshMemoCacheAndApplyToUI(data);
        },()=>{
            $(this).removeClass("common_prevent_double_click");
        })
    }).on("click",".one_memo_item_unit_container[label_name='DONE'] .one_memo_item_unit_content,.one_memo_item_unit_container[label_name='DONE'] .one_memo_item_unit_label_name",function(){
        $(this).addClass("common_prevent_double_click");
        let itemId = $(this).parents(".one_memo_item_unit_container").attr("item_id");
        sendAjaxBySmartParams("CareerServlet","c_save_memo_label",{
            "item_id":itemId,
            "label":"TODO"
        },(data)=>{
            refreshMemoCacheAndApplyToUI(data);
        },()=>{
            $(this).removeClass("common_prevent_double_click");
        })
    })

    /*拖拽调整顺序*/
    let dragTargetClass = ".one_memo_item_unit_container";


    let draggingClass = "common_on_dragging";

    let $dragDiv

    $itemsContainer.on("dragstart",dragTargetClass,function(ev){
        // 拖拽对象
        $dragDiv = $(this);
        $dragDiv.addClass(draggingClass);
    }).on("dragend",dragTargetClass,function(){
        $dragDiv.removeClass(draggingClass);
    }).on("drop",dragTargetClass,function(ev){
        ev.preventDefault();
        let srcId= $dragDiv.attr("item_id");
        let endId = $(this).attr("item_id");
        if(srcId == endId){
            return;
        }

        let indexs = $itemsContainer.find(dragTargetClass).get().map(dom=>$(dom).attr("item_id"));

        let dragIndex = indexs.findIndex((e)=>e==srcId);
        let endBookIndex =  indexs.findIndex((e)=>e==endId);

        if(dragIndex<endBookIndex){
            $dragDiv.insertAfter($(this));
        }else{
            $dragDiv.insertBefore($(this));
        }

        banDraggingMemoItems();

        let ids = $itemsContainer.find(dragTargetClass).get().map(dom=>$(dom).attr("item_id"));

        sendAjaxBySmartParams("CareerServlet", "c_save_memo_items_seq", {
            "ids" : ids
        }, (data) => {
            refreshMemoCacheAndApplyToUI(data);
        }, () => {
            unbanDraggingMemoItems();
        });

    }).on("dragover",dragTargetClass,function(ev){
        ev.preventDefault();
    });

};


function filterMemoItemsByKeyWords(){
    let keyWords = $("#memo_dialog_body_items_filter_key_words").val();
    $("#notes_memo_dialog").find(".one_memo_item_unit_container").each((i,v)=>{
        let shown = keyWords.trim().length == 0 ? true : $(v).text().indexOf(keyWords) != -1;
        $(v).toggleClass("common_hide_unit",!shown);
    })
}

function applyCacheToMemoDialog(){
    let memoCache = NOTE_BOOK_NAMESPACE.MEMO;

    if(memoCache == undefined){
        /*页面初加载时*/
        /*TODO 这里的处理还是有点奇怪 或许不对*/
        return;
    }

    fillTextareaVal(memoCache.memo.note,$("#memo_note_textarea"));

    openMemoItemControgroupAddMode();

    let $pattern = $("#note_book_content_pattern_container");
    let $itemsContainer = $("#memo_dialog_body_items_container");
    let showFooterIds = $itemsContainer.find(".one_memo_item_unit_container").filter((i,v)=>parseToBool($(v).find(".one_memo_item_unit_switch_footer_btn").attr("open"))).get().map(v=>parseInt($(v).attr("item_id")));

    $itemsContainer.empty();
    memoCache.content.items.forEach((e)=>{
        let $unit = $pattern.find(".one_memo_item_unit_container").clone();
        $unit.attr({
            "label_name":e.item.label.name,
            "item_id" : e.item.id}).find(".one_memo_item_unit_label_name").text(e.item.label.name == "无" ? "无标签" : e.item.label.name).end()
            .find(".one_memo_item_unit_content").html(transferLineBreaksToHTML(e.item.content)).end()
            .find(".one_memo_item_unit_container_footer_note").html(transferLineBreaksToHTML(e.item.note)).end()
            .find(".one_memo_item_unit_container_footer_src_book_name").text(e.srcBookName).end()
            .find(".one_memo_item_unit_container_footer_src_note_name").text(e.srcNoteName).end()
            .find(".one_memo_item_unit_container_footer_note_src_by_put_to_memo_container").toggle(!e.createdByUser).end()
            .find(".one_memo_item_unit_container_footer_src_created_by_user").toggle(e.createdByUser)
        
        

            
        if(showFooterIds.indexOf(e.item.id) == -1){
            hideMemoItemFooter($unit);
        }else{
            showMemoItemFooter($unit);
        }

        $itemsContainer.append($unit);
    });

    filterMemoItemsByKeyWords();

    if(parseToBool($("#notes_memo_dialog").attr("open_edit_mode"))){
        unbanDraggingMemoItems();
    }else{
        banDraggingMemoItems();
    }


    syncMemoAndManger();

}

function syncMemoAndManger(){
    let memoCache = NOTE_BOOK_NAMESPACE.MEMO;
    let $managerContainer = $("#sm_note_labels_manager");
    $managerContainer.find(".one_note_sm_label_unit_of_manager").each((i,v)=>{
        let srcNoteId =  parseInt($(v).attr("src_note_id"));
        let content = transferLineBreaksToText($(v).find(".one_note_sm_label_unit_content_of_manager").html()).trim();
        let label = $(v).attr("label_name");
        let alreadyPutIntoMemo = memoCache.content.items.find(e=>{
            /*TODO 和 DONE 特殊一点 这两个标签在这里判断为true*/
            let labelSame = e.item.label.name == label;
            if(!labelSame && (label == "TODO" || label =="DONE")){
                labelSame = e.item.label.name == "TODO" || e.item.label.name == "DONE";
            }
            return e.item.srcNoteId == srcNoteId && e.item.content == content && labelSame
        }) != undefined;

        $(v).find(".one_note_unit_of_manager_hint_of_already_put_to_memo").toggle(alreadyPutIntoMemo).end()
            .find(".one_note_unit_of_manager_put_to_memo_btn").toggle(!alreadyPutIntoMemo)
    })
}

function hideMemoItemFooter($container){
    $container.find(".one_memo_item_unit_container_footer").addClass("common_hide_unit").end()
        .find(".one_memo_item_unit_switch_footer_btn").text("显示备注").attr("open",false);
}



function showMemoItemFooter($container){
    $container.find(".one_memo_item_unit_container_footer").removeClass("common_hide_unit").end()
        .find(".one_memo_item_unit_switch_footer_btn").text("隐藏备注").attr("open",true);
}


function callOutMemoDialog(funcAfterInit){

    /*过滤框*/
    $("#memo_dialog_body_items_fitler").find("input").val("").end()
        .find(".dropdown-item").eq(0).click().end().end();

    /*隐藏所有备注*/
    $("#hide_all_memo_items_footer_btn").click();

    /*使用缓存来init这个memoDialog*/
    applyCacheToMemoDialog();
    
    funcAfterInit();
    $("#notes_memo_dialog").modal("show");
}

function openMemoDialogEditMode(){
    $("#notes_memo_dialog").attr("open_edit_mode",true);
    unbanDraggingMemoItems();
}

function closeMemoDialogEditMode(){
    $("#notes_memo_dialog").attr("open_edit_mode",false);
    banDraggingMemoItems();
}

function openMemoItemControgroupSaveMode(){
    $("#memo_dialog_body_control_group_container").attr("controgroup_mode","save");
}

function openMemoItemControgroupAddMode(){
    $("#memo_dialog_body_control_group_container").attr("controgroup_mode","add");

    /*init add 框*/
    $("#memo_dialog_body_control_group_container").find("textarea").val("").end()
        .find(".dropdown-item").eq(0).click().end().end();
}

/*返回是否监听到tab键*/
function monitorHotKeys(e){
    /*ctrl+q 切换笔记页编辑/查看*/
    if(e.keyCode == 81 && withCtrl(e)){
        e.preventDefault();
        $("#note_book_content_main_container .note_book_for_unit_body_container:visible").find(".note_book_main_info_open_note_page_edit_mode:visible,.note_book_main_info_close_note_page_edit_mode:visible").click().focus();
        return true;
    }

    /*ctrl+m 调出标签管理器*/
    if(e.keyCode == 77 && withCtrl(e)){
        e.preventDefault();
        let $manager = $("#sm_note_labels_manager");
        if($manager.is(":visible")){
            $manager.hide();
        }else{
            callOutLabelsManager();
        }
        return true;
    }
}




function changeNoteHidden(){
    let $bookContainer = $(this).parents(".note_book_for_unit_body_container");
    let $page = $(this).parents(".one_note_book_content_unit");
    let bookId = $bookContainer.attr("book_id");
    let noteId = $page.attr("note_id");
    addBookNavagtionLoadingState(bookId);
    $(this).addClass("common_prevent_double_click");
    sendAjaxBySmartParams("CareerServlet", "c_save_hidden", {
        "note_id":noteId,
        "hidden" : $(this).attr("note_hidden")
    }, (data) => {
        fillNotePageByData($page,data.firstRlt);
        refillNoteList($bookContainer,data.secondRlt);
    }, () => {
        removeBookNavagtionLoadingState(bookId);
        $(this).removeClass("common_prevent_double_click");
    });
}




function changeNoteImportant(){
    let $bookContainer = $(this).parents(".note_book_for_unit_body_container");
    let $page = $(this).parents(".one_note_book_content_unit");
    let bookId = $bookContainer.attr("book_id");
    let noteId = $page.attr("note_id");
    addBookNavagtionLoadingState(bookId);
    $(this).addClass("common_prevent_double_click");
    sendAjaxBySmartParams("CareerServlet", "c_save_important", {
        "note_id":noteId,
        "important" : $(this).attr("note_important")
    }, (data) => {
        fillNotePageByData($page,data.firstRlt);
        refillNoteList($bookContainer,data.secondRlt);
    }, () => {
        removeBookNavagtionLoadingState(bookId);
        $(this).removeClass("common_prevent_double_click");
    });
}

function callOutLabelsManager(){
    $("#sm_note_labels_manager").show();
}


function initNoteLabelsManager(){
    let $manager = $("#note_book_content_pattern_container").find(".one_note_labels_manager_pattern").clone();
    let mangerId = "sm_note_labels_manager";
    new Drag(mangerId, "标签管理器", $manager.get(0).outerHTML);

    let $container = $("#"+mangerId);

    $container.find(".one_note_labels_manager_introduction .one_note_labels_manager_pattern_container_switching_btn").click(function(){
        switchCommonContainer(this,showIntroductionOfLabelManger,hideIntroductionOfLabelManger)
    }).end().find(".call_out_memo_dialog_btn_of_manager").click(()=>{
        callOutMemoDialog(()=>{
            closeMemoDialogEditMode();
        })
    }).end().find(".one_note_labels_manager_labels_management .one_note_labels_manager_pattern_container_switching_btn").click(function(){
        switchCommonContainer(this,showLabelsOfLabelManger,hideLabelsOfLabelManger)
    }).end().find(".one_note_labels_manager_others .one_note_labels_manager_pattern_container_switching_btn").click(function(){
        switchCommonContainer(this,showOthersOfLabelManger,hideOthersOfLabelManger)
    }).end().on("click",".dropdown-item",switchLabelToFilter)
    .on("click",".one_note_unit_of_manager_put_to_memo_btn",putNoteToMemo)
    .on("click",".one_note_unit_of_manager_copy_btn",function(){
        let $unit = $(this).parents(".one_note_sm_label_unit_of_manager");
        copyToClipboardInDefault($unit.find(".one_note_sm_label_unit_content_of_manager").text());
        showForAWhile("成功",$unit.find(".one_note_sm_label_unit_hint_of_manager"));
    }).on("click",'.one_note_sm_label_unit_of_manager[label_name="TODO"] .one_note_sm_label_unit_tag_name_of_manager,.one_note_sm_label_unit_of_manager[label_name="TODO"] .one_note_sm_label_unit_content_of_manager',switchLabelState)
    .on("click",'.one_note_sm_label_unit_of_manager[label_name="DONE"] .one_note_sm_label_unit_tag_name_of_manager,.one_note_sm_label_unit_of_manager[label_name="DONE"] .one_note_sm_label_unit_content_of_manager',switchLabelState)
    .on("input",".note_labels_search_keys",()=>{
        filterManagerLabelsByKeyWords($container);
    }).on("input",".note_page_text_search_keys",()=>{
        searchNotesTextByKeyWords($container);
    }).on("input",".one_note_labels_manager_introduction_labels_container_sample_input",function(){
        let text = $(this).val();
        applySampleInput(text);
    }).on("click",".one_note_labels_manager_introduction_labels_sample_container sm_label[label_name='TODO']",()=>changeLabelToAnotherForManager("TODO","DONE"))
    .on("click",".one_note_labels_manager_introduction_labels_sample_container sm_label[label_name='DONE']",()=>changeLabelToAnotherForManager("DONE","TODO"))


    function putNoteToMemo(){
        confirmInfo("确定放入备忘录中吗（TODO标签在被放入备忘录的同时，会将笔记页中的TODO自动标为DONE，其余标签不变）",()=>{
            let $unit = $(this).parents(".one_note_sm_label_unit_of_manager");
            $(this).addClass("common_prevent_double_click");
            let label = $unit.attr("label_name");
            let content = transferLineBreaksToText($unit.find(".one_note_sm_label_unit_content_of_manager").html());
            let note = new Date().toString()+" 被放入备忘录";

            if(label == "TODO"){
                $unit.find(".one_note_sm_label_unit_content_of_manager").click();
            }

            sendAjaxBySmartParams("CareerServlet","c_add_item_to_memo",{
                "label" : label,
                "content" : content,
                "note" : note,
                "src_note_id" : $unit.attr("src_note_id")
            },(data)=>{
                refreshMemoCacheAndApplyToUI(data);
            },()=>{
                $(this).removeClass("common_prevent_double_click");
            })
        })
    }


    function changeLabelToAnotherForManager(src,target){
        let $input = $container.find(".one_note_labels_manager_introduction_labels_container_sample_input");
        let changedContent = changeContentNoteLabels(0,$input.val(),src,target);
        $input.val(changedContent);
        applySampleInput(changedContent);
    }

    function applySampleInput(text){
        $container.find(".one_note_labels_manager_introduction_labels_container_sample_rlt").html(calculateNoteLabels(text.replaceAll("&","&amp;")));
    }

    hideIntroductionOfLabelManger();
    showLabelsOfLabelManger();
    hideOthersOfLabelManger();

    /*根据index 和 text 找到对应Label 模拟点击*/
    function switchLabelState(){
        let $unit = $(this).parents(".one_note_sm_label_unit_of_manager");
        let index = $unit.attr("label_index");
        let srcNoteId = $unit.attr("src_note_id");
        let bookId = $("#"+mangerId).attr("book_id");
        preventDoubleClick(this);
        getWindowBodyById(bookId).find(".one_note_book_content_unit[note_id='"+srcNoteId+"']").find("sm_label").eq(index).click();
    }

    function switchLabelToFilter(){
        let text = $(this).text();
        $("#label_list_for_filter .dropdown-toggle").text(text);
        $("#sm_note_labels_manager").attr("name_for_filter",text.trim()=="无" ? "null":text);
    }
}

function searchNotesTextByKeyWords($container){
    let bookId = $container.attr("book_id");
    let keyWords = $container.find(".note_page_text_search_keys").val();
    let $rltContainer = $container.find(".one_note_labels_manager_of_text_search_container_body");
    $rltContainer.empty();

    if(keyWords.trim().length == 0){
        return;
    }

    getWindowBodyById(bookId).find(".one_note_book_content_unit").each((i,v)=>{
        let pageName = $(v).find(".one_note_book_content_unit_page_name").text();
        let text = $(v).find(".one_note_book_content_unit_body_show_info").text();
        let index = text.indexOf(keyWords);
        if(index == -1){
            return true;
        }

        const EXTENTION_NUM = 10;
        /*截取的文本 向前向后各截取十个字符*/

        let startIndex = index < EXTENTION_NUM ? 0 : index-EXTENTION_NUM;
        let endIndex = index+keyWords.length+EXTENTION_NUM;
        let calcualtedText = text.substring(startIndex,endIndex);
        if(index > EXTENTION_NUM){
            calcualtedText = "..."+calcualtedText;
        }
        if(index + keyWords.length+EXTENTION_NUM <=text.length-1){
            calcualtedText = calcualtedText + "...";
        }  

        calcualtedText = calcualtedText.replaceAll(keyWords,"<em>"+keyWords+"</em>")

        let $unit = $("#note_book_content_pattern_container").find(".one_note_sm_text_unit_of_manager").clone();
        $unit.find(".one_note_sm_text_src>span").html("<em>"+pageName+"</em>").end()
            .find(".one_note_sm_text_content_of_manager").html(calcualtedText);


        $rltContainer.append($unit);
    });
}




function filterManagerLabelsByKeyWords($container){
    let keyWords = $container.find(".note_labels_search_keys").val();
    $container.find(".one_note_sm_label_unit_of_manager").each((i,v)=>{
        let shown = keyWords.trim().length == 0 ? true : $(v).text().indexOf(keyWords) != -1;
        $(v).toggleClass("hidden_by_key_words_filter",!shown);
    })

}


function showIntroductionOfLabelManger(){
    showLabelManagerContainer($("#sm_note_labels_manager .one_note_labels_manager_introduction"));
}
function hideIntroductionOfLabelManger(){
    hideLabelManagerContainer($("#sm_note_labels_manager .one_note_labels_manager_introduction"));
}

function showOthersOfLabelManger(){
    showLabelManagerContainer($("#sm_note_labels_manager .one_note_labels_manager_others"));
}
function hideOthersOfLabelManger(){
    hideLabelManagerContainer($("#sm_note_labels_manager .one_note_labels_manager_others"));
}



function showLabelsOfLabelManger(){
    showLabelManagerContainer($("#sm_note_labels_manager .one_note_labels_manager_labels_management"));
}
function hideLabelsOfLabelManger(){
    hideLabelManagerContainer($("#sm_note_labels_manager .one_note_labels_manager_labels_management"));
}


function showLabelManagerContainer($container){
    $container.find(".one_note_labels_manager_pattern_container_body").show().end()
        .find(".one_note_labels_manager_pattern_container_switching_btn").text("收起").attr("open",true)
}

function hideLabelManagerContainer($container){
    $container.find(".one_note_labels_manager_pattern_container_body").hide().end()
        .find(".one_note_labels_manager_pattern_container_switching_btn").text("展开").attr("open",false)
}

/*$pageCntainer == null means 关掉了所有页，此时应显示备注信息*/
/*header 标签管理器-BookName*/
function refreshNoteLabelsManager($pageCntainer){
    let $managerContainer = $("#sm_note_labels_manager");
    if($pageCntainer == null){
        $managerContainer.attr({
            "book_id":0
        }).find(".title_for_drag_window").text("标签管理器").end()
        .find(".one_note_sm_label_hint_when_close_all_books").show();
        return;
    }

    $managerContainer.attr({
        "book_id":$pageCntainer.attr("book_id")
    }).find(".title_for_drag_window").text("标签管理器-"+$pageCntainer.find(".note_book_baisc_info_book_name_span").text()).end()
    .find(".one_note_sm_label_hint_when_close_all_books").hide().end()

    let $labelsContainer = $managerContainer.find(".one_note_sm_label_units_container");
    $labelsContainer.empty();

    $pageCntainer.find(".one_note_book_content_unit").each((i,v)=>{
        let notePageName =  $(v).find(".one_note_book_content_unit_page_name").text();
        let noteId = $(v).attr("note_id");
        $(v).find("sm_label").each((indexMark,label)=>{
            let $unit = $("#note_book_content_pattern_container").find(".one_note_sm_label_unit_of_manager").clone();
        
            let labelName = $(label).attr("label_name");
            
            $unit.prop({
                "title" : "来自 "+notePageName
            }).attr({
                "src_note_id" : noteId,
                "label_index" : indexMark,
                "label_name" : labelName
            }).find(".one_note_sm_label_unit_tag_name_of_manager").text(labelName).end()
            .find(".one_note_sm_label_unit_content_of_manager").html(pretreatManagerLabelHTML($(label).html())).end();
    
            $labelsContainer.append($unit);
        })
        
    });

    filterManagerLabelsByKeyWords($managerContainer);
    searchNotesTextByKeyWords($managerContainer);

    syncMemoAndManger();
}

/*这里要处理的是 将里边的sm_label标签都去掉*/
function pretreatManagerLabelHTML(text){
    let rlt = text;
    let reg = new RegExp("<sm_label[^>]*>","gm");
    rlt = rlt.replace(reg,"")
    return rlt.replaceAll("</sm_label>","");
}


function changeTODOToDONE(){
    changeLabelToAnother(this,"TODO","DONE");
}
function changeLabelToAnother(dom,src,target){
    let $noteContainer = $(dom).parents(".one_note_book_content_unit");
    let noteId = $noteContainer.attr("note_id");

    let index = $noteContainer.find("sm_label[label_name='"+src+"']").get().findIndex(e=>{
        return e==dom;
    })
    
    let editor = getEditor(noteId);
    let srcContent = editor.getContent();
    let changedContent = changeContentNoteLabels(index,srcContent,src,target);

    if(srcContent.length - changedContent.length >20){
        alertInfo("出现了罕见的错误，请刷新页面重试。")
        throw "differ too big";
    }

    editor.setContent(changedContent);
    saveNoteContent($noteContainer);
}

function changeDONEToTODO(){
    changeLabelToAnother(this,"DONE","TODO");
}

function loadMemo(){
    sendAjax("CareerServlet", "c_load_memo", {}, (data) => {
        NOTE_BOOK_NAMESPACE.MEMO = data;
    });
}

function refreshMemoCacheAndApplyToUI(data){
    NOTE_BOOK_NAMESPACE.MEMO = data;
    applyCacheToMemoDialog();
}

/**
 * EM
 */
function loadNoteLabelsAndFillUI(){
    sendAjax("CareerServlet", "c_load_note_labels", {}, (data) => {
        NOTE_BOOK_NAMESPACE.LABELS = data;
        /*画下拉框*/
        let  $dropUpContainer = $("#sm_note_labels_manager .dropdown-menu")
        let $noLimit = getDropDownItemButton();
        $noLimit.text("无");
        $dropUpContainer.append($noLimit);
        data.forEach(label=>{
            let $dropDown = getDropDownItemButton();
            $dropDown.text(label.name);
            $dropUpContainer.append($dropDown);
        });
        /*默认为无*/
        $noLimit.click();


        /*画说明信息列表*/
        let $labelsContainer = $("#sm_note_labels_manager .one_note_labels_manager_introduction_labels_container")
        data.forEach(label=>{
            let $unit = $("#note_book_content_pattern_container").find(".one_note_label_of_manager_introduction").clone();
            $unit.attr("label_name",label.name).find(".one_note_label_of_manager_introduction_label_name").text(label.name).end()
                .find(".one_note_label_of_manager_introduction_label_desc").text(label.desc);
            $labelsContainer.append($unit);
        })

        /*画备忘录的过滤列表*/
        /*画下拉框*/
        $dropUpContainer = $("#memo_dialog_body_items_list_for_filter .dropdown-menu")
        $noLimit = getDropDownItemButton();
        $noLimit.text("无");
        $dropUpContainer.append($noLimit);
        data.forEach(label=>{
            let $dropDown = getDropDownItemButton();
            $dropDown.text(label.name);
            $dropUpContainer.append($dropDown);
        });
        /*默认为无*/
        $noLimit.click();

        /*备忘录的添加/修改列表*/
        /*这里有个危险的地方：这里的“无”是有意义的，是要和后台Label对应的！*/
        $dropUpContainer = $("#memo_dialog_body_items_list_for_add_or_saving .dropdown-menu")
        $noLimit = getDropDownItemButton();
        $noLimit.text("无");
        $dropUpContainer.append($noLimit);
        data.forEach(label=>{
            let $dropDown = getDropDownItemButton();
            $dropDown.text(label.name);
            $dropUpContainer.append($dropDown);
        });
        // /*默认为无*/
        $noLimit.click();


    });
}

function closeNotePageByDeleteMark(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    let $existed = $(this).parents(".one_note_book_content_unit");
    let noteId = $existed.attr("note_id");

    removeNotePageIfExisted($container,noteId);
    
    $container.find(".note_book_title_item_cotnainer[note_id='"+noteId+"']").removeClass("selected_page_btn");
}
function generateEditorId(noteId){
    return "note_page_editor_"+noteId;
}
function getEditor(noteId){
    return tinymce.get(generateEditorId(noteId));
}

/*editor可能存在加载延迟导致setContent失效 需要做点保护手段*/
function setEditorContent(noteId,content){
    let targetEditor = getEditor(noteId);
    targetEditor.setContent(content);   
    setTimeout(()=>{
        let checkContent = tinymce.get(generateEditorId(noteId)).getContent();
        if(!isBlankNoteContent(content) && 
                isBlankNoteContent(checkContent)){
            console.log("递归校验content 为空机制 is working !!!")
            setEditorContent(noteId,content);
        }
    },500);
}

function getNoteFromCache(noteId){
    let cache = NOTE_BOOK_NAMESPACE.INITIAL_NOTES;
    let identify = parseInt(noteId);
    return cache.find(one=>one.id == identify)
}


function addOrUpdateToNotesCache(note){

    let cache = NOTE_BOOK_NAMESPACE.INITIAL_NOTES;

    let existedIndex = cache.findIndex(one=>one.id == note.id)
    if(existedIndex != -1){
        cache.splice(existedIndex, 1);
    }


    let afterlength = cache.push(note);
    
    console.log("Print Cache Size ::: "+afterlength+ "::" +cache.length);
}

function removeNoteFromCache(noteId){

    let cache = NOTE_BOOK_NAMESPACE.INITIAL_NOTES;

    let identify = parseInt(noteId);
    let existedIndex = cache.findIndex(one=>one.id == identify)
    if(existedIndex != -1){
        cache.splice(existedIndex, 1);
    }

    console.log("Print Cache Size ::: " +cache.length);
}

function removeNotesFromCacheByBook(bookId){

    let identify = parseInt(bookId);
    let cache = NOTE_BOOK_NAMESPACE.INITIAL_NOTES;

    cache.filter(one => one.noteBookId == identify).forEach(e=>{
        removeNoteFromCache(e.id)
    });
}


function fillNotePageByData($page,data){
    let noteId = data.note.id;
    let content = data.note.content;
    let calculateLabeledRlt = calculateNoteLabels(content);
    
    addOrUpdateToNotesCache(data.note);

    let noteHidden = data.note.hidden == null ? false : data.note.hidden;

    $page.attr({
        "note_id":noteId,
        "hidden_note":data.note.hidden
    }).find(".one_note_book_content_unit_page_name").text(data.note.name).end()
    .find(".one_note_book_content_unit_page_control_mark_important").toggle(!data.note.important).end()
    .find(".one_note_book_content_unit_page_control_mark_general").toggle(data.note.important).end()
    .find(".one_note_book_content_unit_page_control_mark_hidden").toggle(!noteHidden).end()
    .find(".one_note_book_content_unit_page_control_mark_show").toggle(noteHidden).end()
    .find(".one_note_book_content_unit_page_name_input").val(data.note.name).end()
    .find(".page_create_time").text(new Date(data.note.createTime).toSmartString()).end()
    .find(".page_update_time").text(new Date(data.note.updateTime).toSmartString()).end()
    .find(".one_note_book_content_unit_body_show_info").html(calculateLabeledRlt);

    setEditorContent(noteId,content);
}
function initEditor($page,noteId){
    let editorId = generateEditorId(noteId);
    $page.find(".one_note_book_content_unit_body>textarea").prop("id",editorId);
    tinymce.init({
        plugins: "paste",
        paste_as_text: true,
        selector: '#'+editorId,
        language: "zh_CN",
        menubar: 'file edit format',
        toolbar: 'styleselect | bold italic | alignleft aligncenter alignright alignjustify | outdent indent', 
        font_formats:"微软雅黑='微软雅黑';宋体='宋体';黑体='黑体';仿宋='仿宋';楷体='楷体';隶书='隶书';幼圆='幼圆';Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings",
        height:"750px",
        content_css : "./components/note_book_content/editor.css",
        setup:  function(editor){
            let originalText;
            editor.on("focus",function(e){
                NOTE_BOOK_NAMESPACE.NEEDING_SAVING = true;
                originalText = editor.getContent();
            }).on('blur', function(e) {
                let nowText = editor.getContent();
                if(originalText != nowText){
                    originalText = nowText;
                    saveNoteContent($page);
                }else{
                    NOTE_BOOK_NAMESPACE.NEEDING_SAVING = false;
                }
            }).on("keydown",function(e){
                let nowText = editor.getContent();
                if(isCtrlS(e)){
                    e.preventDefault();
                    if(originalText != nowText){
                        originalText = nowText;
                        saveNoteContent($page);
                    }else{
                        NOTE_BOOK_NAMESPACE.NEEDING_SAVING = false;
                    }
                }
                if(monitorHotKeys(e)){
                    if(originalText != nowText){
                        originalText = nowText;
                        saveNoteContent($page);
                    }else{
                        NOTE_BOOK_NAMESPACE.NEEDING_SAVING = false;
                    }
                }
            }).on('keydown', function (evt) {
                if (evt.keyCode == 9) {
                    if (evt.shiftKey) {
                        editor.execCommand('Outdent');
                    } else {
                        editor.execCommand('Indent');
                    }
                    evt.preventDefault();
                    evt.stopPropagation();
                }
        
            });;
        }
    });
}

/*这里要第一下点击加载 第二下点击将对应容器 放到首页*/
//TODO
function switchNotePageByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    let noteId = $(this).attr("note_id");
    let bookId = $container.attr("book_id");
    let $existed = $container.find(".one_note_book_content_unit[note_id='"+noteId+"']");

    let $notesContainer=  $container.find(".note_book_note_note_pages_container_for_merge_mode");

    if($existed.length > 0){
        if($existed.length >1){
            console.error("dup note??"+$existed.length);
        }
        removeNotePageIfExisted($container,noteId);
    }

    addBookNavagtionLoadingState(bookId);

    $(this).addClass("common_prevent_double_click");
    $(this).addClass("selected_page_btn");

    let $page = $("#note_book_content_pattern_container").find(".one_note_book_content_unit").clone();
    $notesContainer.prepend($page);
    initEditor($page,noteId);

    sendAjaxBySmartParams("CareerServlet", "c_load_note", {
        "note_id":noteId,
    }, (data) => {
        fillNotePageByData($page,data);
        calculateZeroNoteBodyVisibility($container);
    }, () => {
        removeBookNavagtionLoadingState(bookId);
        $(this).removeClass("common_prevent_double_click");
        refreshNoteLabelsManager($container);
    });
 
}




function removeNotePageIfExisted($container,noteId){
    
    removeNoteFromCache(noteId);
    
    
    let $existed = $container.find(".one_note_book_content_unit[note_id='"+noteId+"']");
    if($existed.length == 0){
        return;
    }

    tinymce.remove("#"+generateEditorId(noteId));
    $existed.remove();
    calculateZeroNoteBodyVisibility($container);

    refreshNoteLabelsManager($container);
}


function calculateZeroNoteBodyVisibility($container){
    let existsPageAtLeastOne = $container.find(".one_note_book_content_unit").length > 0;
    $container.find(".note_book_open_zero_note_body").toggle(!existsPageAtLeastOne)
        .end().find(".note_book_note_content_main_container").toggle(existsPageAtLeastOne);
    
}



function tryToCloseBookWindow(bookId){
    let $navagations = $("#note_book_content_main_header_units_container").find(".note_book_for_unit_header_navagation_container[book_id='"+bookId+"']");
    if($navagations.length == 0){
        return;
    }

    $navagations.find(".note_book_for_unit_header_navagation_close_window_btn").click();
}

function bindNotePageSaveEvents(){
    let originalText ;
    $("#note_book_content_main_body_container").on("focus",".one_note_book_content_unit_page_name_input",function(){
        originalText = $(this).val();
    }).on("change",".one_note_book_content_unit_page_name_input",function(){
        let nowText = $(this).val();
        if(originalText != nowText){
            originalText = nowText;
            let $noteContent =$(this).parents(".one_note_book_content_unit");
            saveNoteContent($noteContent);
        }
    }).on("keydown",".one_note_book_content_unit_page_name_input",function(e){
        if (isCtrlS(e)){
            e.preventDefault();
            let nowText = $(this).val();
            if(originalText != nowText){
                originalText = nowText;
                let $noteContent =$(this).parents(".one_note_book_content_unit");
                saveNoteContent($noteContent);
            }
         }
    });
}
function isCtrlS(e){
    return e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey);
}


function isBlankNoteContent(content){
    return content == null || content.replaceAll("\n","")
    .replaceAll("</p>","")
    .replaceAll("<p>","")
    .replaceAll("&nbsp;","")
    .trim().length == 0;
}




/** 
 * 这里必须要保证页面的同步 即编辑器和DIV的同步
 * 理论上 我这里得防止它连点 我让它延迟个300毫秒再发送请求 (防止更新过于频繁)
 */
function saveNoteContent($noteContent){
    const WAITING_MILL_SECONDS = 300;
    
    let $container = $noteContent.parents(".note_book_for_unit_body_container");
    let bookId = $container.attr("book_id");

    addBookNavagtionLoadingState(bookId);

    let noteId = $noteContent.attr("note_id");
    let name = $noteContent.find(".one_note_book_content_unit_page_name_input").val();


    let $notePageName =  $noteContent.find(".one_note_book_content_unit_page_name")
    let srcNoteName = $notePageName.text();
    $notePageName.text(name);

    let content = tinymce.get(generateEditorId(noteId)).getContent();

    /**
     * 检验Content 和 缓存里的Content:
     * 当缓存里的content不为空  这里为空时候 我会认为他恨可疑 从而提示confirm框。
     */
    let existedInCache = getNoteFromCache(noteId);
    confirmInfoConditionally(existedInCache != null 
        && !isBlankNoteContent(existedInCache.content)
        && isBlankNoteContent(content)
        ,"你似乎删除了笔记页全部的内容，确定保存清空吗？",()=>{
                /*只需要同步即可*/
            let calculateLabeledRlt = calculateNoteLabels(content);
            $noteContent.find(".one_note_book_content_unit_body_show_info").html(calculateLabeledRlt);

            let withTodos = $noteContent.find("sm_label[label_name='TODO']").length>0;
            refreshNoteLabelsManager($container);

            let existedSavingNote = NOTE_BOOK_NAMESPACE.WAITING_TO_SAVED_NOTES.find(e=>e.noteId == noteId);
            if(existedSavingNote != undefined){
                clearTimeout(existedSavingNote.timeoutId);
                let indexForDelete = NOTE_BOOK_NAMESPACE.WAITING_TO_SAVED_NOTES.indexOf(existedSavingNote);
                NOTE_BOOK_NAMESPACE.WAITING_TO_SAVED_NOTES.splice(indexForDelete,1);
                if(indexForDelete == -1){
                    throw "unbelieveable? 用错API了";
                }
            }

            let savingMark = {
                "noteId" : noteId
            };
            
            savingMark.timeoutId= setTimeout(()=>{
                sendAjaxBySmartParams("CareerServlet", "c_save_note", {
                    "note_id":noteId,
                    "name":name,
                    "content":content,
                    "with_todos" : withTodos
                }, (data) => {

                    addOrUpdateToNotesCache(data.note);

                    $noteContent.find(".page_update_time").text(new Date(data.note.updateTime).toSmartString()).end()
                    let $navagation = $container.find(".note_book_title_item_cotnainer[note_id='"+noteId+"']");
                    fillNoteNavagationByData($navagation,data.note);
                
                    /*假如NoteName变了 刷新一下memo缓存 防止《来自》的数据不一致*/
                    if(srcNoteName!=name){
                        loadMemo();
                    }
                }, () => {
                    removeBookNavagtionLoadingState(bookId);
                });


            NOTE_BOOK_NAMESPACE.NEEDING_SAVING = false;

            },WAITING_MILL_SECONDS);

            NOTE_BOOK_NAMESPACE.WAITING_TO_SAVED_NOTES.push(savingMark);
    },"确定","还原",()=>{

        let inCache = getNoteFromCache(noteId);

        /**
         * 函数的遗留问题 需要包一层
         */
        fillNotePageByData($noteContent,{
            "note" : inCache
        });

        removeBookNavagtionLoadingState(bookId);
    });
}




function prependNewLineIfNotExists(content){
    let rlt = content;
    if(!rlt.endsWith("\n")){
        rlt = rlt+"\n";
    }
    return rlt;
}

function pretreateContentForSpecialCloseMark(rlt){
    rlt = rlt.replace(/&amp;\)/gm,speciMarkForCloseInEnglish);
    rlt = rlt.replace(/&amp;）/gm,speciMarkForCloseInChinese);
    return rlt;
}

function recoverContentForSpecialCloseMark(rlt){
    return rlt.replaceAll(speciMarkForCloseInEnglish,"&amp;)")
    .replaceAll(speciMarkForCloseInChinese,"&amp;）")
}
function endTreatContentForSpecialCloseMark(rlt){
    return rlt.replaceAll(speciMarkForCloseInEnglish,")")
        .replaceAll(speciMarkForCloseInChinese,"）")
}

function changeContentNoteLabels(index,content,src,target){

    if(index == undefined){
        alertInfo("出现了极为意外的错误，请刷新页面重试。 index==null");
        throw "index==null";
    }

    let srcContent =  prependNewLineIfNotExists(content);
    let rlt = pretreateContentForSpecialCloseMark(srcContent);
    let reg = new RegExp(src+"{1}[ \s]?[^\)\n）]+[\)）\n]{1}","gm");
    let i = 0;
    let targetLabelIndex;
    for(let matchRlt of rlt.matchAll(reg)){
        if(i==index){
            targetLabelIndex = matchRlt.index;
            break;
        }
        i++;
    }

    if(targetLabelIndex == undefined){
        alertInfo("意外错误，请刷新页面重试。")
        throw "targetLabelIndex can not found??";
    }

    rlt = recoverContentForSpecialCloseMark(rlt.substr(0, targetLabelIndex)+ target + rlt.substr(targetLabelIndex+src.length));

    /*转化后不可能相差那么大的字符量*/
    if(rlt.length - srcContent.length > 10){
        alertInfo("出现了极为意外的错误，请刷新页面重试。")
        throw "differ too big?";
    }

    return rlt;
}
/*当未来继续添加时，别忘了要把说明补充上*/
const speciMarkForCloseInEnglish = "ж";
const speciMarkForCloseInChinese = "ц";

function calculateNoteLabels(content){
    let tag = NOTE_BOOK_NAMESPACE.LABEL_TAG;
    let rlt = pretreateContentForSpecialCloseMark(prependNewLineIfNotExists(content))
        .replaceAll("&amp;r","<br>")


    NOTE_BOOK_NAMESPACE.LABELS.forEach(label=>{
        rlt=rlt.replaceAll("&amp;"+label.name,"&"+label.name);
        let reg = new RegExp("(?<!&)"+label.name+"[ \s]{1}([^\)\n）]+)[\)）\n]","gm");
        rlt = rlt.replace(reg,"<"+tag+" label_name='"+label.name+"'>$1</"+tag+">")
    })

    /*TODO 编辑器有些BUG 段首和段尾出现无意义的<p>标签 这时把它去掉 但是好像是很偶然才会出现的 先不处理了。等到比较简单复现出来后再说吧*/



    return endTreatContentForSpecialCloseMark(rlt);
}


function bindDragNoteListUnitEvents(){
    let dragTargetClass = ".note_book_title_item_cotnainer";

    let draggingClass = "common_on_dragging";

    let $dragDiv

    $("#note_book_content_main_body_container").on("dragstart",dragTargetClass,function(ev){
        // 拖拽对象
        $dragDiv = $(this);
        $dragDiv.addClass(draggingClass);
    }).on("dragend",dragTargetClass,function(){
        $dragDiv.removeClass(draggingClass);
    }).on("drop",dragTargetClass,function(ev){
        ev.preventDefault();
        let srcId= $dragDiv.attr("note_id");
        let endId = $(this).attr("note_id");
        if(srcId == endId || $dragDiv.attr("important_note") !=  $(this).attr("important_note")){
            return;
        }

        let indexs = $("#note_book_content_main_body_container").find(dragTargetClass).get().map(dom=>$(dom).attr("note_id"));

        let dragIndex = indexs.findIndex((e)=>e==srcId);
        let endBookIndex =  indexs.findIndex((e)=>e==endId);

        if(dragIndex<endBookIndex){
            $dragDiv.insertAfter($(this));
        }else{
            $dragDiv.insertBefore($(this));
        }
 
        let $container = $(this).parents(".note_book_for_unit_body_container");
        let bookId = $container.attr("book_id");

        let notesSeq = $(".note_book_note_infos_list").find(".note_book_title_item_cotnainer").get().map(e=>parseInt($(e).attr("note_id")));

        banDraggingNoteItems($container);

        addBookNavagtionLoadingState(bookId);
        let param = {
            "target_id":bookId,
            "notes_seq" : notesSeq
        }
        sendAjaxBySmartParams("CareerServlet", "c_save_notes_seq", param, () => {}, () => {
            removeBookNavagtionLoadingState(bookId);
            unbanDraggingNoteItems($container);
        });


    }).on("dragover",dragTargetClass,function(ev){
        ev.preventDefault();
    });
}


function bindDragNavagationEvents(){
  
    let navagationClassSelector = ".note_book_for_unit_header_navagation_container";

    let draggingClass = "common_on_dragging";

    let $dragDiv

    $("#note_book_content_main_header_units_container").on("dragstart",navagationClassSelector,function(ev){
        // 拖拽对象
        $dragDiv = $(this);
        $dragDiv.addClass(draggingClass);
    }).on("drag",navagationClassSelector,function(){

    }).on("dragend",navagationClassSelector,function(){
        $dragDiv.removeClass(draggingClass);
    }).on("drop",navagationClassSelector,function(ev){
        ev.preventDefault();
        let bookId= $dragDiv.attr("book_id");
        let endBookId = $(this).attr("book_id");
        if(bookId == endBookId){
            return;
        }

        let indexs = $("#note_book_content_main_header_units_container").find(navagationClassSelector).get().map(dom=>$(dom).attr("book_id"));

        let dragIndex = indexs.findIndex((e)=>e==bookId);
        let endBookIndex =  indexs.findIndex((e)=>e==endBookId);

        if(dragIndex<endBookIndex){
            $dragDiv.insertAfter($(this));
        }else{
            $dragDiv.insertBefore($(this));
        }

    }).on("dragover",navagationClassSelector,function(ev){
        ev.preventDefault();
    });

}



function closeAllBookWindows(){
    let $navagations = $("#note_book_content_main_header_units_container").find(".note_book_for_unit_header_navagation_container");
    if($navagations.length == 1){
        $navagations.find(".note_book_for_unit_header_navagation_close_window_btn").click();
        return;
    }

    confirmInfo("确定关闭所有窗口吗？",()=>{
        $navagations.find(".note_book_for_unit_header_navagation_close_window_btn").click();
    })

}

function closeBookWindowByClick(e){
    e.stopPropagation();
    let bookId = $(this).parents(".note_book_for_unit_header_navagation_container").attr("book_id");
    closeBookWindowAndRemoveFromNavagation(bookId);
}

/**
 * 删除后的自动跳转逻辑 和 浏览器保持一致：
 * 假如有next() 则跳转到next()
 * 无next() 跳转到prev()
 */
function closeBookWindowAndRemoveFromNavagation(bookId){

    removeNotesFromCacheByBook(bookId);

    getWindowBodyById(bookId).remove();
    let $targetNavagtion = getWindowNavagationById(bookId);

    let $next = $targetNavagtion.next(".note_book_for_unit_header_navagation_container");
    let $prev = $targetNavagtion.prev(".note_book_for_unit_header_navagation_container");

    $targetNavagtion.remove();
    calculateWindowVisibility();

    if($next.length>0){
        $next.click();
        return;
    }

    if($prev.length>0){
        $prev.click();
    }

}

function getWindowNavagationById(bookId){
    return $("#note_book_content_main_header_units_container")
        .find(".note_book_for_unit_header_navagation_container[book_id='"+bookId+"']");
}

function getWindowBodyById(bookId){
    return $("#note_book_content_main_body_container")
        .find(".note_book_for_unit_body_container[book_id='"+bookId+"']")
}


function switchBookContentWindow(){
    let bookId = $(this).attr("book_id");
    switchWindowByBookId(bookId);
}

function addBookNavagtionLoadingState(bookId){
    getWindowNavagationById(bookId).addClass("book_window_loading");
}

function removeBookNavagtionLoadingState(bookId){
    getWindowNavagationById(bookId).removeClass("book_window_loading");
}


function switchWindowByBookId(bookId){
    //DONE
    $("#note_book_content_main_header_units_container").find(".note_book_for_unit_header_navagation_container").removeClass("select_navagation").end()
        .find(".note_book_for_unit_header_navagation_container[book_id='"+bookId+"']").addClass("select_navagation");

    //DONE
    $("#note_book_content_main_body_container").find(".note_book_for_unit_body_container").hide().end()
        .find(".note_book_for_unit_body_container[book_id='"+bookId+"']").show();

    let $container = $("#note_book_content_main_body_container").find(".note_book_for_unit_body_container[book_id='"+bookId+"']");
    refreshNoteLabelsManager($container);
}
function deleteNoteByClickBtnInPage(){
    let noteId = $(this).parents(".one_note_book_content_unit").attr("note_id");
    let $container = $(this).parents(".note_book_for_unit_body_container");
    $container.find(".note_book_title_item_cotnainer[note_id='"+noteId+"']").find(".note_book_list_item_delete_mark").click();   
}

function deleteNoteByClick(e){
    e.stopPropagation();
    confirmInfo("确定删除吗？",()=>{
        let $container = $(this).parents(".note_book_for_unit_body_container");
        let bookId = $container.attr("book_id");
        addBookNavagtionLoadingState(bookId);
        let $noteInfo = $(this).parents(".note_book_title_item_cotnainer");
        let noteId = $noteInfo.attr("note_id");

        $noteInfo.remove();
        removeNotePageIfExisted($container,noteId);

        let param = {
            "note_id":noteId,
        };

        sendAjax("CareerServlet", "c_delete_note", param, () => {}, true, () => {}, () => {
            removeBookNavagtionLoadingState(bookId);
            loadMemo();
        });
    })
}

//DONE
function createNoteByClick(){

    $(this).addClass("common_prevent_double_click");

    let $container = $(this).parents(".note_book_for_unit_body_container");
    let bookId = $container.attr("book_id");
    let param = {
        "book_id":bookId,
        "name" : new Date().toStandardChinese()
    }
    addBookNavagtionLoadingState(bookId);

    sendAjax("CareerServlet", "c_create_note", param, (data) => {
        refillNoteList($container,data.firstRlt);
        openNotePageEditMode($container);
        let noteId = data.secondRlt;
        $container.find(".note_book_title_item_cotnainer[note_id='"+noteId+"']").click();
    }, true, () => {}, () => {
        $(this).removeClass("common_prevent_double_click");
        removeBookNavagtionLoadingState(bookId);
    });
}

function fillNoteNavagationByData($note,note){
    
    $note.attr({"note_id":note.id,
        "important_note":note.important,
        "hidden_note":note.hidden
    }).find(".note_book_list_item_title").text(note.name).end()
    .toggleClass("warning_with_todos",note.withTodos)

    if(note.withTodos){
        $note.prop("title","该页存在待办TODO，请尽快处理或放入备忘录")
    }
}

function refillNoteListForOneKind($list,notes,selectedNotesIds){
    $list.empty();
    notes.forEach(note=>{
        let $note = $("#note_book_content_pattern_container .note_book_title_item_cotnainer").clone();

        fillNoteNavagationByData($note,note);

        if(selectedNotesIds.indexOf(note.id)!=-1){
            $note.addClass("selected_page_btn");
        }
        $list.append($note);
    });
}

/*这里由于empty里了 需要根据页面已有信息 重新管理一下draggable*/
/**还要维护 "selected_page_btn" */
function refillNoteList($container,notes){

    let $importantList = $container.find(".note_book_important_note_infos_list");
    let $genralList = $container.find(".note_book_general_note_infos_list");
    let selectedNotesIds = $container.find(".selected_page_btn").get().map(e=>parseInt($(e).attr("note_id")));

    refillNoteListForOneKind($importantList,notes.importantNotes,selectedNotesIds);
    refillNoteListForOneKind($genralList,notes.generalNotes,selectedNotesIds);

   
    if(parseToBool($container.attr("open_note_list_edit_mode"))){
        unbanDraggingNoteItems($container);
    }else{
        banDraggingNoteItems($container);
    }
}



function commitSaveNoteBookBasicInfo(){
    let $container = $(this).parents(".note_book_for_unit_body_container");

    let test = $container.find(".note_book_baisc_info_unit_container.unit_note_book_name").get().every(one =>parseToBool($(one).attr("test")));
    if (!test) {
        alertInfo("请根据提示填写好必要信息");
        return;
    }
    
    let $commitBtn = $container.find(".note_book_content_save_basic_info_button");
    let buttonText = $commitBtn.text();
    $commitBtn.text("保存中......").addClass("common_waiting_button");
    
    let bookId = $container.attr("book_id");
    let param = $container.find(".note_book_baisc_info_form").serializeArray();
    param.push({
        "name":"book_id",
        "value":bookId
    })

    addBookNavagtionLoadingState(bookId);

    sendAjax("CareerServlet", "c_save_note_book", param, (data) => {
        reloadExternalModuleForNoteBookContent();

        let $navagation = getWindowNavagationById(data.book.id);
        if($navagation){
            $navagation.find(".note_book_for_unit_header_navagation_window_name").text(data.book.name);
        }

        fillNoteBookBasicInfo($container,data.book);
    }, true, () => { }, () => {
        $commitBtn.text(buttonText).removeClass("common_waiting_button");
        removeBookNavagtionLoadingState(bookId);
        refreshNoteLabelsManager($container);
    });
}

function reloadExternalModuleForNoteBookContent(){
    if($("#notes_sub_main_container").length>0){
        /*笔记模块*/
        loadBooks();
    }
}


function openNoteListEditModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    openNoteListEditMode($container)
}

function openNoteListEditMode($container){
    $container.attr("open_note_list_edit_mode",true);
    unbanDraggingNoteItems($container);
}


function banDraggingNoteItems($container){
    $container.find(".note_book_title_item_cotnainer").prop("draggable",false);
}

function unbanDraggingNoteItems($container){
    $container.find(".note_book_title_item_cotnainer").prop("draggable",true);
}

function closeNoteListEditModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    closeNoteListEditMode($container)
}

function closeNoteListEditMode($container){
    $container.attr("open_note_list_edit_mode",false);
    banDraggingNoteItems($container);
}



function openNoteBookBasicInfoEditModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    openNoteBookBasicInfoEditMode($container)
}


function showHiddenNotesByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    showHiddenNotes($container)
}

function showHiddenNotes($container){
    $container.attr("show_hidden_notes",true);
}

function closeShowHiddenNotesByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    closeShowHiddenNotes($container)
}

function closeShowHiddenNotes($container){
    $container.attr("show_hidden_notes",false);
}


function openNotePageEditModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    openNotePageEditMode($container)
}

function openNotePageEditMode($container){
    $container.attr("open_note_page_edit_mode",true);
}

function closeNotePageEditModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    closeNotePageEditMode($container)
}

function closeNotePageEditMode($container){
    $container.attr("open_note_page_edit_mode",false);
}


function switchNotePageMergingModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    switchNotePageMergingMode($container)
}

function switchNotePageMergingMode($container){
    $container.attr("showing_pages_mode","merge");
}

function switchNotePageSplitingModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    switchNotePageSplitingMode($container)
}

function switchNotePageSplitingMode($container){
    $container.attr("showing_pages_mode","split");
}


function showNotePageTitleByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    showNotePageTitle($container)
}

function showNotePageTitle($container){
    $container.attr("show_note_page_title",true);
}

function hideNotePageTitleByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    hideNotePageTitle($container)
}

function hideNotePageTitle($container){
    $container.attr("show_note_page_title",false);
}



function openNoteBookBasicInfoEditMode($container){
    $container.find(".note_book_baisc_info_for_unit_body_container").attr("open_edit_mode",true);
}
function closeNoteBookBasicInfoEditMode($container){
    $container.find(".note_book_baisc_info_for_unit_body_container").attr("open_edit_mode",false);
}


function closeNoteBookBasicInfoEditModeByClick(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    closeNoteBookBasicInfoEditMode($container)
}


function syncNoteBookNameToSampleForSaveBook(){
    let text =$(this).val();
    let $container = $(this).parents(".note_book_for_unit_body_container");
    $container.find(".notebook-skin.note_book_name").text(text)
}


function chooseStyleTypeByClickForSaveBook(){   
    $(this).parents(".note_book_content_style_unit").find("[name='style']").click();
}

function switchToShowBookNoteListContainer(){
    let $container = $(this).parents(".note_book_for_unit_body_container");
    let state = $container.attr("showing_type");
    const firstStae = "hide_null";
    const secondState = "hide_important";
    const thirdState = "hide_general";
    const fourthState = "hide_all";
    
    let afterState; 
    let aftetText;
    switch(state){
        case firstStae:
            afterState = secondState;
            aftetText = "隐藏普通页" ;
            break;
        case secondState:
            afterState = thirdState;
            aftetText = "隐藏全部页";
            break;
        case thirdState:
            afterState = fourthState;
            aftetText = "不隐藏页";
            break;
        /*初始化*/
        case undefined:
        case fourthState:
            afterState = firstStae;
            aftetText = "隐藏重要页";
            break;
        default:
            throw "can not be there!" + state
    }

    $container.attr("showing_type",afterState).find(".note_book_baisc_info_infos_list_main_container").text(aftetText);
}


function switchToShowBookBasicInfoContainer(){
    let open = parseToBool($(this).attr("open"));
    let $container = $(this).parents(".note_book_for_unit_body_container");
    if(open){
        hideBookBasicInfoContainer($container);
    }else{
        showBookBasicInfoContainer($container);
    }
}

function hideBookBasicInfoContainer($container){
    let text = $(".note_book_baisc_info_title").eq(0).text();

    $container.find(".note_book_baisc_info_for_unit_body_container").hide().end()
        .find(".note_book_baisc_info_switch_container_visibility").text("显示"+text).attr("open",false);
}

function showBookBasicInfoContainer($container){
    let text = $(".note_book_baisc_info_title").eq(0).text();

    $container.find(".note_book_baisc_info_for_unit_body_container").show().end()
    .find(".note_book_baisc_info_switch_container_visibility").text("隐藏"+text).attr("open",true);
}


//DONE
function initNoteBookContentUI(){
    let $container = $("#note_book_content_pattern_container .note_book_for_unit_body_container");
    $container.find(".note_book_content_style_unit").each((i,v)=>{
        let mainColor =  $(v).attr("main_color");
        let subColor = $(v).attr("sub_color");
        let $book = $("#note_book_content_pattern_container>.one_note_book_unit_pattern_and_btns_container").find(".one_note_book_unit_pattern").clone();
        fillBookStyle($book,mainColor,subColor);
        $(v).find(".note_book_content_style_sample").append($book);
    });
}

function fillBookStyle($book,mainColor,subColor){
    $book.find(".notebook-cover").css("background",mainColor);
    $book.find(".notebook-cover .notebook_pre_span").css("background",
        "linear-gradient(to right, "+subColor+" 0%, "+mainColor+" 12%, "+subColor+" 25%, "+mainColor+" 37%, "+subColor+" 50%, "+mainColor+" 62%, "+subColor+" 75%, "+mainColor+" 87%, "+subColor+" 100%"
    );
}




/*For External Module*/
//TODO ABCD
function openNoteBook(bookId,usingWatchMode){
    //DONE 已打开的情况下 重新加载
    addBookNavagtionLoadingState(bookId);
    sendAjax("CareerServlet","c_load_book_content",{
        "book_id" : bookId
    },(data)=>{
        //DONE
        drawHeaderNavagation(data.book);
        //DONE
        removeBookNavagtionLoadingState(data.book.id);
        //DONE
        calculateWindowVisibility();
        //TODO
        drawNotesBookContent(data,usingWatchMode);
        switchWindowByBookId(data.book.id);

    });
}

function fillNoteBookBasicInfo($container,book){
    $container.attr("book_id",book.id)
    /*加载时 一定是合法的名称*/
    .find(".unit_note_book_name").attr("test",true).end()
    .find("[name='name']").val(book.name).end()
    .find(".note_book_baisc_info_book_name_span").text(book.name).end()
    .find(".one_note_book_unit_pattern .note_book_name").text(book.name).end()
    .find(".note_book_baisc_info_sub_title_seq_weight_span").text(book.seqWeight).end()
    .find("[name='seq_weight']").val(book.seqWeight).end()
    .find(".note_book_baisc_info_note_div").html(adaptDivForTextare(book.note)).end();

    /*TODO 说实话我不知道为啥在第一次加载的时候 这里的scrollHeight为0 为此我才专门加了特殊处理*/
    fillTextareaVal(book.note,$container.find("[name='note']"));

    $container.find(".note_book_select_book_style [name='style'][value='"+book.style.dbCode+"']").click();

}


function fillNoteBookContent($container,data){
    let book = data.book;
    fillNoteBookBasicInfo($container,book);
    refillNoteList($container,data);
}


function checkNoteBookName(text) {
    return text.length < 21 && text.length > 0;
}


function testSaveBookFormat(inputDom, checkFunc, errorAppendInfo) {
    let text = $(inputDom).val();
    let test = checkFunc(text);
    let $container = $(inputDom).parents(".note_book_baisc_info_unit_container");
    $container.attr("test", test);
    let $errorContainer = $container.find(".note_book_baisc_info_book_name_hint");
    $errorContainer.toggle(!test);
    if (!test) {
        $errorContainer.text("格式有误，" + errorAppendInfo);
    } else {
        $errorContainer.text("");
    }
    return test && text.length != 0;
}
function switchContainerMode($content,usingWatchMode){
    if(usingWatchMode){
        closeNoteListEditMode($content);
        closeNotePageEditMode($content);
    }else{
        openNoteListEditMode($content);
        openNotePageEditMode($content);
    }
}
//TODO
function drawNotesBookContent(data,usingWatchMode){
    let $contentContainer = $("#note_book_content_main_body_container");

    let $existed = $contentContainer.find(".note_book_for_unit_body_container[book_id='"+data.book.id+"']");
    if($existed.length == 0){
        let $container = $("#note_book_content_pattern_container").find(".note_book_for_unit_body_container").clone();
        $contentContainer.append($container);
        
        fillNoteBookContent($container,data);
        hideBookBasicInfoContainer($container);

        /*控制目录显示状态 模拟点击初始化*/
        $container.find(".note_book_baisc_info_infos_list_main_container").click();

        closeNoteBookBasicInfoEditMode($container);
        closeShowHiddenNotes($container);
        switchNotePageSplitingMode($container);
        showNotePageTitle($container);
        switchContainerMode($container,usingWatchMode)
        calculateZeroNoteBodyVisibility($container);
        return;
    }

    if($existed.length>1){
        throw "impossable";
    }

    fillNoteBookContent($existed,data);
    switchContainerMode($existed,usingWatchMode)
}





/*根据navagation 是否为0决定是否隐藏MainContainer*/
function calculateWindowVisibility(){
    let isShown = $("#note_book_content_main_header_units_container").find(".note_book_for_unit_header_navagation_container").length>0;
    $("#note_book_content_main_container").toggle(isShown);

    if(!isShown){
        refreshNoteLabelsManager(null);
    }
}


function drawHeaderNavagation(book){
    let $headerContainer = $("#note_book_content_main_header_units_container");

    let $existed = $headerContainer.find(".note_book_for_unit_header_navagation_container[book_id='"+book.id+"']");
    if($existed.length == 0){
        let $navagation = $("#note_book_content_pattern_container").find(".note_book_for_unit_header_navagation_container").clone();
        $navagation.attr("book_id",book.id).find(".note_book_for_unit_header_navagation_window_name").prop("title",book.name).text(book.name);
        $headerContainer.append($navagation);
        return;
    }

    if($existed.length>1){
        throw "impossable";
    }

    $existed.find(".note_book_for_unit_header_navagation_window_name").prop("title",book.name).text(book.name);
}
