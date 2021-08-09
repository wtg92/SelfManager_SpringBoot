$(function(){
    loadWStatistics();
    loadWSByStateCircle();
    $(".all_ws_state_unit_container").click(switchToShowPlansByState);

    initAllWSDataTable();
    $("#all_ws_statics").on("click","tbody tr",openWorkSheetDialogFromDataTable);
    $("#all_ws_open_plan_dept_dialog_btn").click(openPlanDeptDialog);
})


function openWorkSheetDialogFromDataTable(){
    let wsId = $(this).find(".ws_state_span").attr("ws_id");
    $("#all_ws_plan_work_sheet_dialog").modal("show");
    $("#all_ws_plan_work_sheet_dialog .modal-title").text($(this).find(".ws_date").text()+"的工作表");
    drawWorkSheetDetail(wsId);
}

function initAllWSDataTable(){
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

    $("#all_ws_statics table").DataTable({
        "oLanguage" : CONFIG.DATATABLE_LANG_CONFIG,
        "columns":[{
            "title":"日期",
        },{
            "title":"状态",
        },{
            "title":"计划",
        },{
            "title":"最新修改日期",
        },{
            "title":"备注",
        }],
        "columnDefs":[
            {"sType":"general-date-sorter","aTargets":[0,3]}
        ]   
    });
}


function switchToShowPlansByState(){
    let thisClicked = $(this).hasClass("selected_state");
    $(".selected_state").removeClass("selected_state");
    if(!thisClicked){
        $(this).addClass("selected_state");
    }

    loadWSByStateCircle();
}


function loadWSByStateCircle(){
    let $hint = $("#all_ws_hint_when_loading");
    $hint.show();

    let state = $(".all_ws_state_unit_container.selected_state") ? $(".all_ws_state_unit_container.selected_state").attr("state_code") : 0;
    sendAjax("CareerServlet","c_load_ws_by_state",{
        state : state
    },(data)=>{
        $hint.hide();

        let linesDate = [];
        data.forEach(ws=>{
            let oneLine = [];
            let $state = $("<span>");
            $state.text(ws.ws.state.name).addClass("common_state_block").addClass("ws_state_span").attr("ws_id",ws.ws.id).css(getFontColorAndBackgroudColor(ws.ws.state.color));
            
            let $date = $("<span>");
            $date.addClass("ws_date").attr("date_abs",ws.ws.date).text(new Date(ws.ws.date).toChineseDate());

            let $updateTime = $("<span>");
            $updateTime.attr("date_abs",ws.ws.updateTime).text(new Date(ws.ws.updateTime).toChineseDate());

            let $note = $("<span>");
            $note.text(ws.ws.note).attr("title",ws.ws.note);

            oneLine.push($date.get(0).outerHTML);
            oneLine.push($state.get(0).outerHTML);
            oneLine.push(ws.basePlanName);
            oneLine.push($updateTime.get(0).outerHTML);
            oneLine.push($note.get(0).outerHTML);
            linesDate.push(oneLine);
        })

        let table = $("#all_ws_statics table").DataTable();
        table.clear();
        table.rows.add(linesDate);
        table.draw();
    });

}

function loadWStatistics(){
    sendAjax("CareerServlet","c_load_ws_state_statistics",{},(data)=>{
        for(let key in data){
            $(".all_ws_state_unit_container[state_code='"+key+"']").find(".all_ws_unit_count").text(data[key]);
        }
    });
}

