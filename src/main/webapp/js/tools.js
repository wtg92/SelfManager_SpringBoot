
const TOOLS_NAMESPACE = {
    DBCODE_FOR_IMGS_EXTRACTOR_OF_PPT:1,
}

$(function () {
    initToolRecordsDataTable();
    loadToolRecordSummary();
    bindUploadEventsForTools();
    $("#tools_datatable_main_container").on("click",".upload_btn_for_imgs_extractor",()=>{
        $("[name='imgs_extractor_src']").click();
    });
});

function refreshToolRecordStatics(targetTool){
    let $targetRow = $("#tools_datatable_main_container").find("tbody tr").filter((i,v)=>{
        return parseInt($(v).find(".tool_opreation_btns_container").attr("tool")) == targetTool;
    })

    if($targetRow.length>0){
        sendAjax("ToolServlet","t_load_tool_record",{
            "tool" :targetTool
        },(data)=>{
            $targetRow.find(".tools_tool_statictics").parent("td").html(calculateToolStatistics(data));
        });
    }

}

function loadToolRecordSummary(){
    let $loadingInfo = $(".tool_record_summary.common_loading_message").show();
    sendAjax("ToolServlet","t_load_tool_record_summary",{},(data)=>{
        $loadingInfo.hide();
       
        let linesDate = [];
        data.records.forEach(record=>{
            if(record.record.tool.dbCode == 2){
                /*修改图片DPI的工具.......好像没再关注了 就先不管了。。。。Java和Python的互相调用 还是比较麻烦.....*/
                return ;
            }


            let oneLine = [];
            
            let $createTime = $("<span>");
            $createTime.attr("date_abs",record.record.createTime).text(new Date(record.record.createTime).toChineseDate());
            
            oneLine.push(record.record.tool.name);
            oneLine.push(record.record.tool.fileTypeMes);
            oneLine.push($createTime.get(0).outerHTML);
            oneLine.push(calcualteToolOpreations(record));
            oneLine.push(calculateToolStatistics(record));
            linesDate.push(oneLine);
        })

        let $dataTable =  $("#tools_datatable_main_container table");
        let table = $dataTable.DataTable();
        table.clear();
        table.rows.add(linesDate);
        table.draw();

        $dataTable.find('[data-toggle="popover"]').popover()



    });
}
function bindUploadEventsForTools() {

    let $extractorFormFileBtn = $("[name='imgs_extractor_src']");
    $extractorFormFileBtn.change(() => {
        if ($extractorFormFileBtn.val() == "") {
            return;
        }
        let $clickBtn = $(".upload_btn_for_imgs_extractor");
        let text = $clickBtn.text();
        $clickBtn.addClass("common_waiting_font").text("处理中");

        let formData = new FormData();
        let file = $("[name='imgs_extractor_src']").get(0).files[0];
        formData.append("imgs_extractor_src", file);

        sendAjaxForUploadFileInReqAndDownloadFileFromRes('DownloadServlet?op=t_extract_ppt_imgs&user_token=' + localStorage[CONFIG.USER_TOKEN_KEY],
            formData,
            getFileName(file.name) + ".zip",
            ()=>{
                $extractorFormFileBtn.val("");
                $clickBtn.removeClass("common_waiting_font").text(text);
                refreshToolRecordStatics(TOOLS_NAMESPACE.DBCODE_FOR_IMGS_EXTRACTOR_OF_PPT);
            });
    });

}





function calcualteToolOpreations(record){
    let $div = $("<div>");
    $div.addClass("tool_opreation_btns_container")
        .attr("tool",record.record.tool.dbCode).append(getTopPopoverBtn(record.record.tool.desc,"基本介绍"))
    switch(record.record.tool.dbCode){
        case TOOLS_NAMESPACE.DBCODE_FOR_IMGS_EXTRACTOR_OF_PPT:
            initPPTExtractor($div);
            break;
    }

    
    $div.children("span").addClass("common_blue_font").addClass("common_hover");
    return $div.get(0).outerHTML;
}

function initPPTExtractor($div){
    let $pickFile = $("<span>");
    $pickFile.text("选择文件").addClass("upload_btn_for_imgs_extractor");
    $div.append($pickFile);
}


function calculateToolStatistics(record){
    let sucCount = record.content.sucCount;
    let failCount = record.content.failCount;
    let sum = sucCount+failCount;
    let sucRatio = sum==0? 0 : ((sucCount/sum)*100).toText()+"%";

    let $div = $("#tools_pattern_container").find(".tools_tool_statictics").clone();
    $div.find(".suc_count").text(sucCount).end()
        .find(".fail_count").text(failCount).end()
        .find(".sum_count").text(sum).end()
        .find(".suc_ratio").text(sucRatio).end();

    return $div.get(0).outerHTML;
}




function initToolRecordsDataTable(){
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

    $("#tools_datatable_main_container table").DataTable({
        "oLanguage" : CONFIG.DATATABLE_LANG_CONFIG,
        "columns":[{
            "title":"名称",
        },{
            "title":"文件类型",
        },{
            "title":"发布日期",
        },{
            "title":"操作",
        },{
            "title":"统计"
        }],
        "columnDefs":[
            {"sType":"general-date-sorter","aTargets":[2]}
        ]   
    });
}