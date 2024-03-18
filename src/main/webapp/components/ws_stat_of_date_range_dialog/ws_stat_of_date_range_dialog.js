const WS_STAT_NAMESAPCE={
    DATA:null
};

$(function(){
    $("#ws_stat_controlgroup_container_quick_terms_this_week").click(()=>{
        let range = new Date().getThisWeekRange();
        $("[name='ws_stat_start_date']").val(range.start.getDateStr());
        $("[name='ws_stat_end_date']").val(range.end.getDateStr());
    });

    $("#ws_stat_controlgroup_container_quick_terms_this_month").click(()=>{
        let range = new Date().getThisMonthRange();
        $("[name='ws_stat_start_date']").val(range.start.getDateStr());
        $("[name='ws_stat_end_date']").val(range.end.getDateStr());
    });

    $("#ws_stat_controlgroup_container_quick_terms_this_quarter").click(()=>{
        let range = new Date().getThisQuarterRange();
        $("[name='ws_stat_start_date']").val(range.start.getDateStr());
        $("[name='ws_stat_end_date']").val(range.end.getDateStr());
    });

    $("#ws_stat_controlgroup_container_quick_terms_this_year").click(()=>{
        let range = new Date().getThisYearRange();
        $("[name='ws_stat_start_date']").val(range.start.getDateStr());
        $("[name='ws_stat_end_date']").val(range.end.getDateStr());
    });

    $("[name='ws_stat_mode']").click(switchWSStatMode)
    //默认按计划分组
    .eq(0).click();

    $("#ws_stat_analyze_ws_btn").click(analyzeWSsOfDateRange);


    $("#ws_stat_content_container_of_text .ws_stat_switch_to_show_more_info").click(switchToShowMoreInfo);

    $("#ws_stat_content_container_of_more_info_container").on("click",".ws_stat_unit_for_one_plan_header_switch_to_show_btn",switchToShowOnePlanStatDetail);

})

function switchWSStatMode(){
    let prop =$(this).attr("group_by");
    $("#ws_stat_of_date_range_dialog").attr("group_by",prop);
    drawGroupByChartBaseRadio();
}


function switchToShowOnePlanStatDetail(){
    let open = parseToBool($(this).attr("open"));

    let $unit = $(this).parents(".ws_stat_unit_for_one_plan");

    if(open){
        closeDetailStatByPlanContainer($unit);
    }else{
        openDetailStatByPlanContainer($unit);
    }
}



function switchToShowMoreInfo(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        closeMoreInfoContainer();
    }else{
        openMoreInfoContainer();
    }
}


function closeDetailStatByPlanContainer($unit){


    $unit.find(".ws_stat_unit_for_one_plan_body").hide().end()
        .find(".ws_stat_unit_for_one_plan_header_switch_to_show_btn").text("详情").attr("open",false);
}

function openDetailStatByPlanContainer($unit){
    $unit.find(".ws_stat_unit_for_one_plan_body").show().end()
        .find(".ws_stat_unit_for_one_plan_header_switch_to_show_btn").text("收起").attr("open",true);
}








function closeMoreInfoContainer(){
    $("#ws_stat_content_container_of_more_info_container").hide();
    $("#ws_stat_content_container_of_text .ws_stat_switch_to_show_more_info").text("分组统计").attr("open",false);
}

function openMoreInfoContainer(){
    $("#ws_stat_content_container_of_more_info_container").show();
    $("#ws_stat_content_container_of_text .ws_stat_switch_to_show_more_info").text("收起").attr("open",true);
}

function drawMoodStat(data){
    if(data.length == 0){
        //TODO 找时间处理一下 在时间范围内没有工作表的情况
        return ;
    }
    data.sort((a,b)=>
        a.ws.date-b.ws.date
    )

    let minDateWS = data[0];
    let maxDateWS = data[data.length-1];

    let rlt = [];
    let cur = new Date(minDateWS.ws.date);
    while(cur.getTime() <= maxDateWS.ws.date){
        let targetWS = data.filter(e=>e.ws.date == cur.getTime());
        
        rlt.push({
            "date" : new Date(cur.getTime()),
            "ws" : targetWS.length == 0 ? null : targetWS[0]
        })

        cur.addDays(1);
    }

    let timeData = rlt.map(e=>e.date.toChineseDate());
    getOrInitEcharts("ws_stat_content_container_of_charts_average_moods").setOption({
        title: {
            left: 'center',
            text: '总工作时长/平均心情变化'
        },

        tooltip: {
            trigger: 'axis'
        },

        legend: {
            data: ['总工作时长/小时','平均心情'],
            left: 10
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {},
                magicType: {type: ['line', 'bar']}
            }
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
        },
        dataZoom: [
            {
                show: true,
                realtime: true,
                start: 30,
                end: 70,
                xAxisIndex: [0, 1]
            },
            {
                type: 'inside',
                realtime: true,
                start: 30,
                end: 70,
                xAxisIndex: [0, 1]
            }
        ],
        grid: [{
            left: 50,
            right: 50,
            height: '35%'
        }, {
            left: 50,
            right: 50,
            top: '55%',
            height: '35%'
        }],
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                axisLine: {onZero: true},
                data: timeData,
            },
            {
                gridIndex: 1,
                type: 'category',
                boundaryGap: false,
                axisLine: {onZero: true},
                data:  timeData,
                position: 'top'
            }
        ],
        yAxis: [
            {
                type: 'value',
                max:24
            },
            {
                gridIndex: 1,
                max: 5,
                type: 'value',
                inverse: true
            },
        ],
        series: [
            {
                name: '总工作时长/小时',
                type: 'line',
                symbolSize: 8,
                hoverAnimation: false,
                data:rlt.map(e=>{
                    return e.ws == null ? 0 : (mergeWorkItemsExceptUndone(e.ws.content).reduce((accum,current)=>{
                        return accum+current.costMinutes;           
                    },0)/60).toText(2);
                }),
                markPoint: {
                    data: [
                        {type: 'max', name: '最大'},
                    ]
                },
                markLine: {
                    data: [
                        {type: 'average', name: '平均'},
                        {yAxis:8,name:"八小时工作线"},
                        {yAxis:5,name:"正常工作线"},
                    ]
                }
            },
            {
                name: '平均心情',
                type: 'line',
                xAxisIndex: 1,
                yAxisIndex: 1,
                symbolSize: 8,
                hoverAnimation: false,
                data: rlt.map(e=>{
                    return e.ws == null ? 0 : e.ws.mood.toText(2);
                })
            }
        ]
    })

}


function drawGroupByChartBaseRadio(){
    if(!WS_STAT_NAMESAPCE.DATA){
        return;
    }

    let groupBy = $("#ws_stat_switch_stat_mode_container").find(":checked").attr("group_by");
    let data = [];
    switch(groupBy){
        case "plan":
            data = WS_STAT_NAMESAPCE.DATA.sumBySpecificField(item=>item.basePlanName)
            break;
        case "tag":
            data = WS_STAT_NAMESAPCE.DATA.sumBySpecificArrayField(item=>item.ws.tags.map(e=>e.name));
            break;
    }
    drawCommonPieChart("ws_stat_content_container_of_charts_for_distribution",data.sortAndMergeSumRlt(),70,(value)=>value+"天");
}



function analyzeWSsOfDateRange(){
    /*直接让它不许为空 就行了*/
    let startDate  = $("[name='ws_stat_start_date']").val();
    if(startDate.trim().length == 0){
        alertInfo("请选择分析起始日期");
        return;
    }

    let endDate = $("[name='ws_stat_end_date']").val();
    if(endDate.trim().length == 0){
        alertInfo("请选择分析截止日期");
        return;
    }

    $(this).addClass("common_prevent_double_click");
    sendAjaxBySmartParams("CareerServlet","c_load_work_sheets_by_date_scope",{
        "start_date":startDate,
        "end_date":endDate
    },(data)=>{

        WS_STAT_NAMESAPCE.DATA = data;

        closeMoreInfoContainer();

        $("#ws_stat_content_container").show();
        let text = "<em>"+new Date(startDate).toChineseDate()+"</em>"+"到<em>"+new Date(endDate).toChineseDate()+"</em>";
        $(".stat_date_range").html(text);
        $(".count_for_search_date_range").text(new Date(endDate).countDaysDiffer(new Date(startDate))+1);
        $(".count_for_stat_days").text(data.length);

        drawCommonBarChart("ws_stat_content_container_of_charts_for_ws_state_distribution",data.sumBySpecificField(item=>item.ws.state.name).sortAndMergeSumRlt(),"工作表状态/天",4);
        drawCommonPieChart("ws_stat_content_container_of_charts_for_type_count",data.flatMap((e)=>{
            return mergeWorkItemsExceptUndone(e.content).map((ee)=>{
                return {"name": ee.pItem.item.name,
                     "value": ee.costMinutes
                };
            })
         }).sumBySpecificField(item=>item.name,item=>item.value).sortAndMergeSumRlt(),70,(value)=>value.transferToHoursMesIfPossible());

        drawCommonBarChart("ws_stat_content_container_of_charts_for_finish_situation",data.sumBySpecificField(item=>item.finishPlanWithoutDeptItems ? "完成（除同步项）" : "未完成（除同步项）").sortAndMergeSumRlt(),"实际完成/天",2);

        drawMoodStat(data);

        let dataByPlan = data.groupBy(item=>item.basePlanName);
        drawStatByGroup(dataByPlan,".ws_stat_group_by_plan");
        let dataByTag = data.groupByArrayAttr(item=>item.ws.tags.map(e=>e.name));
        drawStatByGroup(dataByTag,".ws_stat_group_by_tag");

        
        drawGroupByChartBaseRadio(); 
    },()=>{
        $(this).removeClass("common_prevent_double_click");
    })
}

function addZeroIfNotEnough(arr,shouldSize){
    if(arr.length>shouldSize){
        //不可能
        console.error("addZeroIfNotEnough Shouldn't happen")
        return;
    }
    let added =  shouldSize-arr.length;
    for(let i=0;i<added;i++){
        arr.push(0);
    }
    return arr;
}



function drawStatByGroup(dataGroupBy,subContainer){

    dataGroupBy.keys.sort((a,b)=>dataGroupBy[b].length - dataGroupBy[a].length);

    let $container = $("#ws_stat_content_container_of_more_info_container").find(subContainer);
    $container.empty();
    
    const MERGE_OTHERS_LIMIT = 4;
    const OTHER_TEXT = "其它";
    const ALL_TEXT = "总计";

    dataGroupBy.keys.forEach(key=>{
        let $unit = $("#ws_stat_pattern_container").find(".ws_stat_unit_for_one_plan").clone();
        let sumDays = dataGroupBy[key].length;

        $unit.find(".ws_stat_unit_for_one_plan_header_plan_name").text(key).end()
            .find(".ws_stat_unit_for_one_plan_header_count_days em").text(sumDays).end()
            .find(".count_days_for_saturday").text(dataGroupBy[key].filter(e=>new Date(e.ws.date).isSaturday()).length).end()
            .find(".count_days_for_sunday").text(dataGroupBy[key].filter(e=>new Date(e.ws.date).isSunday()).length).end()

        
        /**
         * 这片逻辑其实有点易错：
         * 在于下面需要根据总时长找到前几个显示 合并其它为“其它”
         * 那么此时取平均数等，是要/总天数 而非下列经过flatMap的数组的。
         * 因为当flatMap时，会清除掉0项，此时，便需要补0了
         */
        let workItemsAfterMergedForEveryDay = dataGroupBy[key].map((e)=>{
            return mergeWorkItemsExceptUndone(e.content).map((ee)=>{
                return {"name": ee.pItem.item.name,
                     "value": ee.costMinutes
                };
            })
         });

        let workItemsGroupBy = workItemsAfterMergedForEveryDay.flatMap(e=>e).groupBy(item=>item.name);
             
        let statRLt = workItemsGroupBy.keys.map(subKey=>{
            return {
                "name":subKey,
                //这里需要补0
                "stat":calculateCommonStat(addZeroIfNotEnough(workItemsGroupBy[subKey].map(item=>item.value),sumDays)),
                "src":workItemsGroupBy[subKey]
            };
        });
        
        /**
         * 根据总时长倒序排序，取前3个的显示，其它合并为其它
         */
        statRLt.sort((a,b)=>b.stat.sum-a.stat.sum);
        let mergedRlt = [];
        let othersData=[];
        for(let i = 0 ; i<statRLt.length ; i++){
            if(i < MERGE_OTHERS_LIMIT){
                mergedRlt.push(statRLt[i]);
                continue;
            }
            othersData = othersData.concat(statRLt[i].src);
        }

        if(othersData.length>0){
            /**
             * 找到每一天并非这些targetKeys的workItems 合并 视为每一天的其它项 重新组成数组
             */
            let targetKeys = mergedRlt.map(e=>e.name);
            let othersDataMerged = workItemsAfterMergedForEveryDay.map(e=>{
                let sum = e.filter(e=>!targetKeys.contains(e.name)).map(e=>e.value).sum();
                return {
                    "name": OTHER_TEXT,
                    "value": sum
                };
            })

            mergedRlt.push({
                "name":OTHER_TEXT,
                "stat":calculateCommonStat(addZeroIfNotEnough(othersDataMerged.map(item=>item.value),sumDays)),
                "src":othersDataMerged
            })
        }

        mergedRlt.forEach(e=>{
            let $statUnit = calculateStatUnit(e);
            $unit.find(".ws_stat_container_for_one_plan_items").append($statUnit);
        })

        let allDataMerged = workItemsAfterMergedForEveryDay.map(e=>{
            let sum = e.map(e=>e.value).sum();
            return {
                "name": ALL_TEXT,
                "value": sum
            };
        })
        let sumObj = {
            "name":ALL_TEXT,
            "stat":calculateCommonStat(addZeroIfNotEnough(allDataMerged.map(item=>item.value),sumDays)),
            "src":allDataMerged
        };
        
        let $sumUnit = calculateStatUnit(sumObj);
        $sumUnit.addClass("sum_for_item_stat")
        $unit.find(".ws_stat_container_for_one_plan_items").append($sumUnit);        
            
        $container.append($unit);

        closeDetailStatByPlanContainer($unit);   
    })
}

function calculateStatUnit(e){
    let $statUnit = $("#ws_stat_pattern_container").find(".ws_stat_unit_for_one_item").clone();
    $statUnit.find(".ws_stat_unit_for_one_item_name").text(e.name).end()
        .find(".ws_stat_unit_for_one_item_sum_value em").text(e.stat.sum.transferToHoursMesIfPossible()).end()
        .find(".ws_stat_unit_for_one_item_median_value em").text(e.stat.mid.transferToHoursMesIfPossible()).end()
        .find(".ws_stat_unit_for_one_item_avg_value em").text(e.stat.avg.transferToHoursMesIfPossible()).end()
        .find(".ws_stat_unit_for_one_item_std_dev_value em").text(e.stat.stdDev.transferToHoursMesIfPossible()).end()
    return $statUnit;
}







/*For External Module*/
function openWSOfDateRangeDialog(){
    $("#ws_stat_controlgroup_container_real_terms_lefter [type='date']").val("");
    $("#ws_stat_content_container").hide();
    $("#ws_stat_of_date_range_dialog").modal("show");
}






