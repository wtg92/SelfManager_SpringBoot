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

    $("#ws_stat_analyze_ws_btn").click(analyzeWSsOfDateRange);


    $("#ws_stat_content_container_of_text .ws_stat_switch_to_show_more_info").click(switchToShowMoreInfo);

    $("#ws_stat_content_container_of_more_info_container").on("click",".ws_stat_unit_for_one_plan_header_switch_to_show_btn",switchToShowOnePlanStatDetail);



})

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
    $("#ws_stat_content_container_of_text .ws_stat_switch_to_show_more_info").text("分计划统计").attr("open",false);
}

function openMoreInfoContainer(){
    $("#ws_stat_content_container_of_more_info_container").show();
    $("#ws_stat_content_container_of_text .ws_stat_switch_to_show_more_info").text("收起").attr("open",true);
}

function drawMoodStat(data){

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

        closeMoreInfoContainer();

        $("#ws_stat_content_container").show();
        let text = "<em>"+new Date(startDate).toChineseDate()+"</em>"+"到<em>"+new Date(endDate).toChineseDate()+"</em>";
        $(".stat_date_range").html(text);
        $(".count_for_search_date_range").text(new Date(endDate).countDaysDiffer(new Date(startDate))+1);
        $(".count_for_stat_days").text(data.length);

        drawCommonPieChart("ws_stat_content_container_of_charts_for_plan_distribution",data.sumBySpecificField(item=>item.basePlanName).sortAndMergeSumRlt(),70,(value)=>value+"天");
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
        drawStatByPlan(data);
    },()=>{
        $(this).removeClass("common_prevent_double_click");
    })
}
/**根据名字分组的意义在于，假设重名 在前台本就无意义，谁也看不到ID */
function drawStatByPlan(data){
    let dataByPlan = data.groupBy(item=>item.basePlanName);

    dataByPlan.keys.sort((a,b)=>dataByPlan[b].length - dataByPlan[a].length);

    let $container = $("#ws_stat_content_container_of_more_info_container");
    $container.empty();

    dataByPlan.keys.forEach(key=>{
        let $unit = $("#ws_stat_pattern_container").find(".ws_stat_unit_for_one_plan").clone();
        console.log(dataByPlan[key]);
        let sumDays = dataByPlan[key].length;

        console.log(dataByPlan[key]);

        $unit.find(".ws_stat_unit_for_one_plan_header_plan_name").text(key).end()
            .find(".ws_stat_unit_for_one_plan_header_count_days em").text(sumDays).end()
            .find(".count_days_for_saturday").text(dataByPlan[key].filter(e=>new Date(e.ws.date).isSaturday()).length).end()
            .find(".count_days_for_sunday").text(dataByPlan[key].filter(e=>new Date(e.ws.date).isSunday()).length).end()


        let sumMinutes = 0;

        dataByPlan[key].flatMap((e)=>{
                return mergeWorkItemsExceptUndone(e.content).map((ee)=>{
                    return {"name": ee.pItem.item.name,
                         "value": ee.costMinutes
                    };
                })
             }).sumBySpecificField(item=>item.name,item=>item.value).sortAndMergeSumRlt(3).forEach(e=>{
                let $planStatUnit = $("#ws_stat_pattern_container").find(".ws_stat_unit_for_one_plan_item").clone();
                
                sumMinutes += e.value;

                $planStatUnit.find(".ws_stat_unit_for_one_plan_item_name").text(e.name).end()
                    .find(".ws_stat_unit_for_one_plan_item_value").text((e.value/sumDays).transferToHoursMesIfPossible());
                $unit.find(".ws_stat_container_for_one_plan_items").append($planStatUnit);
             })
        
        let $planStatUnit = $("#ws_stat_pattern_container").find(".ws_stat_unit_for_one_plan_item").clone();
                
        $planStatUnit.addClass("sum_for_plan_item_stat").find(".ws_stat_unit_for_one_plan_item_name").text("总计").end()
                 .find(".ws_stat_unit_for_one_plan_item_value").text((sumMinutes/sumDays).transferToHoursMesIfPossible());
        $unit.find(".ws_stat_container_for_one_plan_items").append($planStatUnit);        
            
        $container.append($unit);

        closeDetailStatByPlanContainer($unit);   
    })
}







/*For External Module*/
function openWSOfDateRangeDialog(){
    $("#ws_stat_controlgroup_container_real_terms_lefter [type='date']").val("");
    $("#ws_stat_content_container").hide();

    $("#ws_stat_of_date_range_dialog").modal("show");
}






