$(function () {

    openNoteBooksContainer();
    hideClosedBooksContainer();
    loadBooks();
    initCreateNoteBookUI();

    initClosedBooksDataTable();

    $("#notes_show_closed_books_container_btn").click(showClosedBooksContainer);
    $("#notes_hide_closed_books_container_btn").click(hideClosedBooksContainer);


    $(".note_books_switch_to_show_note_books_content_container").click(switchToShowNoteBooksContainer);


    $("#notes_create_note_book_btn,.notes_create_note_book_btn_in_hint").click(openCreateBookDialog);

    $(".notes_create_note_book_entity [name='name']").blur(function () {
        testCreateBookFormat(this, (text) => checkNoteBookName(text), "1-20个字符");
    });

    $("#notes_commit_create_note_book_button").click(createNoteBook);

    $("#note_book_cards_sub_container")
        .on("dblclick",".one_note_book_unit_pattern",onlySeeBookContent)
        .on("click",".one_note_book_unit_pattern",switchToSelectBookUnit)
        .on("click",".one_note_close_book_btn",closeBookByBtn)
        .on("click",".one_note_open_book_btn",openBookByBtn)
        .on("click",".one_note_see_book_content_btn",onlySeeBookContent);

    $("#notes_closed_note_books_container").on("click",".notes_cancel_book_closed_state_btn",cancelBookClosedState)
        .on("click",".notes_delete_closed_book_btn",deletClosedNoteBook)

    $("#notes_create_note_book_form").find("[name='name']").on("input",syncNoteBookNameToSample)
        .end().find(".notes_create_note_book_style_sample").click(chooseStyleTypeByClick)

    $("#notes_call_out_memo_dialog_btn").click(()=>{
        callOutMemoDialog(()=>{
            closeMemoDialogEditMode();
        })
    })

    $(".notes_leran_to_use_notes_btn").click(()=>$("#notes_getting_start_dialog").modal("show"));
});


function initClosedBooksDataTable(){
    jQuery.extend(jQuery.fn.dataTableExt.oSort,{
        "general-date-sorter-pre":function(dom){
            let dateAbs =  parseInt($(dom).attr("date_abs"));
            if(new Date(dateAbs).isBlank()){
                return new Date().getTime();
            }
            return dateAbs;
        },
        "general-date-sorter-asc":function(a,b){
            return (a<b)? -1 : ((a>b)? 1:0);
        },
        "general-date-sorter-desc":function(a,b){
            return (a<b)? 1 : ((a>b)? -1:0);
        }
    })

    $("#notes_closed_note_books_container table").DataTable({
        "oLanguage" : CONFIG.DATATABLE_LANG_CONFIG,
        "columns":[{
            "title":"名称",
        },{
            "title":"状态",
        },{
            "title":"创建日期",
        },{
            "title":"封存日期",
        },{
            "title":"操作",
        }],
        "columnDefs":[
            {"sType":"general-date-sorter","aTargets":[2,3]}
        ]   
    });
}


function chooseStyleTypeByClick(){
    $(this).parents(".notes_create_note_book_style_unit").find("[name='style']").click();
}



function syncNoteBookNameToSample(){
    let text =$(this).val();
    $(".notes_create_note_book_entity .note_book_name").text(text);
}



function initCreateNoteBookUI(){
    let $container = $("#notes_create_note_book_form");
    const NUM_FOR_ONE_ROW = 3;
    $container.find(".notes_create_note_book_style_unit").each((i,v)=>{
        let mainColor =  $(v).attr("main_color");
        let subColor = $(v).attr("sub_color");
        $(v).toggleClass("common_right_unit",(i+1) % NUM_FOR_ONE_ROW == 0);
        let $book = $("#note_book_content_pattern_container").find(".one_note_book_unit_pattern").clone();
        fillBookStyle($book,mainColor,subColor);
        $(v).find(".notes_create_note_book_style_sample").append($book);
    });

    $container.find("[name='style']").eq(0).prop("checked",true);
}


function onlySeeBookContent(){
    let $book = $(this).parents(".one_note_book_unit_pattern_and_btns_container");
    closeBookUnit($book.get(0));
    let id = $book.attr("book_id");
    openNoteBook(id,true);
}

function openBookByBtn(){
    let $book = $(this).parents(".one_note_book_unit_pattern_and_btns_container");
    closeBookUnit($book.get(0));
    let id = $book.attr("book_id");
    openNoteBook(id);
}

function deletClosedNoteBook(){
    confirmInfo("确定彻底删除吗？",()=>{
        let id = $(this).parents(".notes_closed_books_controlgroup").attr("book_id");
        sendAjax("CareerServlet","c_delete_note_book",{
            "book_id" : id
        },()=>{
            loadBooks();
            loadMemo();
        });
    });
}

function cancelBookClosedState(){
    confirmInfo("确定解除封存吗？",()=>{
        let id = $(this).parents(".notes_closed_books_controlgroup").attr("book_id");
        sendAjax("CareerServlet","c_open_note_book",{
            "book_id" : id
        },loadBooks);
    });
}

function closeBookByBtn(){
    confirmInfo("确定封存吗？（如果该笔记本不包含笔记页且不含备注，会被<em>直接删除</em>）",()=>{
        let $book = $(this).parents(".one_note_book_unit_pattern_and_btns_container");
        closeBookUnit($book.get(0));
        let id = $book.attr("book_id");
        sendAjax("CareerServlet","c_close_note_book",{
            "book_id" : id
        },loadBooks);

        tryToCloseBookWindow(id);
    });
}




function switchToSelectBookUnit(){
    let $container = $(this).parents(".one_note_book_unit_pattern_and_btns_container");
    let selected = parseToBool($container.attr("select"));
    if(!selected){
        /*同一时间 只允许选一张卡片*/
        closeAllBookUnits();
        openBookUnit($container);
    }else{
        closeBookUnit($container);
    }
}

function closeAllBookUnits(){
    $("#note_book_cards_sub_container .one_note_book_unit_pattern_and_btns_container[select='true']").each((i,v)=>{
        closeBookUnit(v);
    })
}

function openBookUnit(dom){
    $(dom).attr("select",true);
}

function closeBookUnit(dom){
    $(dom).attr("select",false);
}


function createNoteBook() {
    let test = $("#notes_create_note_book_form .notes_create_note_book_entity").get().every(one => parseToBool($(one).attr("test")));
    if (!test) {
        alertInfo("请根据提示填写好必要信息");
        return;
    }

    let $commitBtn = $("#notes_commit_create_note_book_button");
    let buttonText = $commitBtn.text();
    $commitBtn.text("创建中......").addClass("common_waiting_button");

    let param = $("#notes_create_note_book_form").serializeArray();

    sendAjax("CareerServlet", "c_create_note_book", param, (data) => {
        $("#notes_create_note_book_dialog").modal("hide");
        loadBooks();
    }, true, () => { }, () => $commitBtn.text(buttonText).removeClass("common_waiting_button"));
}

function loadBooks(){
    let $content = $("#note_book_cards_sub_container");
    $content.empty();
    let $loadingInfo = $("#note_books_cards_main_container .common_loading_message").show();
    sendAjax("CareerServlet","c_load_books",{},(data)=>{
        $loadingInfo.hide();
        $("#note_books_mes_when_zero_plan").toggle(data.length == 0);
        const NUM_FOR_ONE_ROW = 6;

        $(data.filter(e=>!e.book.closed).sort((a,b)=>{
            /*根据seqWeight 决定瞬息*/
            return b.book.seqWeight-a.book.seqWeight;
        })).each((i,book)=>{
            let $book = $("#note_book_content_pattern_container").find(".one_note_book_unit_pattern_and_btns_container").clone();
            $book.attr("book_id",book.book.id).toggleClass("note_book_right_unit_pattern",(i+1) % NUM_FOR_ONE_ROW == 0)
            .find(".one_note_book_unit_pattern").prop("title","双击直接打开").end()        
            .find(".note_book_name").text(book.book.name).end();

            fillBookStyle($book,book.book.style.mainColor,book.book.style.subColor);

            $content.append($book);
        });

        let linesDate = [];
        data.filter(e=>e.book.closed).sort((a,b)=>{
            /*根据seqWeight 决定瞬息*/
            return b.book.seqWeight-a.book.seqWeight;
        }).forEach(book=>{
            let oneLine = [];
            let $state = $("<span>");
            $state.text("封存").addClass("closed_book_span");
            
            let $createTime = $("<span>");
            $createTime.attr("date_abs",book.book.createTime).text(new Date(book.book.createTime).toChineseDate());
            
            let $updateTime = $("<span>");
            $updateTime.attr("date_abs",book.book.updateTime).text(new Date(book.book.updateTime).toChineseDate());

            let $btns = $("#notes_pattern_container").find(".notes_closed_books_controlgroup").clone();

            $btns.attr("book_id",book.book.id).find(".notes_show_book_note_btn").attr("data-content",book.book.note.length == 0 ? "无备注" : book.book.note );

            oneLine.push(book.book.name);
            oneLine.push($state.get(0).outerHTML);
            oneLine.push($createTime.get(0).outerHTML);
            oneLine.push($updateTime.get(0).outerHTML);
            oneLine.push($btns.get(0).outerHTML);
            linesDate.push(oneLine);
        })

        let $dataTable =  $("#notes_closed_note_books_container table");
        let table = $dataTable.DataTable();
        table.clear();
        table.rows.add(linesDate);
        table.draw();

        $dataTable.find('[data-toggle="popover"]').popover()
    });
}

function testCreateBookFormat(inputDom, checkFunc, errorAppendInfo) {
    /*假如清空了就不校验了*/
    let text = $(inputDom).val();
    let test = text.length == 0 ? true : checkFunc(text);
    let $container = $(inputDom).parents(".notes_create_note_book_entity");
    $container.attr("test", test);
    let $errorContainer = $container.find(".notes_create_note_book_error_container");
    $errorContainer.toggle(!test);
    if (!test) {
        $errorContainer.text("格式有误，" + errorAppendInfo);
    } else {
        $errorContainer.text("");
    }
    return test && text.length != 0;
}




function openCreateBookDialog() {
    $("#notes_create_note_book_form").find("[type='text'],[type='date'],textarea").val("");
    $(".notes_create_note_book_entity .note_book_name").text("");
    $(".notes_create_note_book_entity.note_book_name").removeAttr("test");
    $("#notes_create_note_book_dialog").modal("show");
}



function hideClosedBooksContainer() {
    $("#notes_show_closed_books_container_btn").show();
    $("#notes_hide_closed_books_container_btn").hide();
    $("#notes_closed_note_books_container").hide();

}


function showClosedBooksContainer() {
    $("#notes_show_closed_books_container_btn").hide();
    $("#notes_hide_closed_books_container_btn").show();
    $("#notes_closed_note_books_container").show();
}



function switchToShowNoteBooksContainer() {
    let open = parseToBool($(this).attr("open"));
    if (open) {
        closeNoteBooksContainer();
    } else {
        openNoteBooksContainer();
    }
}

function closeNoteBooksContainer() {
    $("#note_books_content_container").hide();
    $(".note_books_switch_to_show_note_books_content_container").text("展开").attr("open", false);
    closeAllBookUnits();
}

function openNoteBooksContainer() {
    $("#note_books_content_container").show();
    $(".note_books_switch_to_show_note_books_content_container").text("收起").attr("open", true);
}