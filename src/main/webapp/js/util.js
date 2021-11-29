/*==============================Date Start========================================*/


Date.prototype.getThisWeekRange = function(){
    let dayOfWeek = this.getDay();

    let start = cloneObj(this).changeToMinOfOneDay();
    start.addDays(-dayOfWeek+1);
    
    let end = cloneObj(this).changeToMaxOfOneDay();
    end.addDays(6-dayOfWeek+1);

    return {
        "start":start,
        "end":end
    }
}

Date.prototype.getThisMonthRange = function(){
    let start = cloneObj(this).changeToMinOfOneDay();
    start.setDate(1);

    let end = cloneObj(start).changeToMaxOfOneDay();
    end.setMonth(end.getMonth()+1);
    end.addDays(-1);

    return {
        "start":start,
        "end":end
    }
}

Date.prototype.getThisQuarterRange = function(){
    let nowMonth = this.getMonth();
    let quarterStartMonth = 0;
    if(nowMonth<3){
        quarterStartMonth = 0;
    }else if(2 < nowMonth && nowMonth < 6){
        quarterStartMonth = 3;
    }else if(5 < nowMonth && nowMonth < 9){
        quarterStartMonth = 6;
    }else if(nowMonth > 8){
        quarterStartMonth = 9;
    }else{
        throw "impossible month" + nowMonth;
    }

    let start = cloneObj(this);
    start.setMonth(quarterStartMonth);
    start = start.getThisMonthRange().start;

    let end = cloneObj(this).changeToMaxOfOneDay();
    end.setMonth(quarterStartMonth+2);
    end = end.getThisMonthRange().end;

    return {
        "start":start,
        "end":end
    };
}

Date.prototype.getThisYearRange = function(){
    let start = cloneObj(this).changeToMinOfOneDay();
    start.setDate(1);
    start.setMonth(0);

    let end = cloneObj(start).changeToMaxOfOneDay();
    end.setFullYear(end.getFullYear()+1);
    end.addDays(-1);

    return {
        "start":start,
        "end":end
    };
}

Date.prototype.changeToMaxOfOneDay = function(){
    this.setHours(23);
    this.setMinutes(59);
    this.setSeconds(59);
    return this;
}

Date.prototype.changeToMinOfOneDay = function(){
    this.setHours(0);
    this.setMinutes(0);
    this.setSeconds(0);
    return this;
}

Date.prototype.format = function(fmt)  { //author: meizz   
  let o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(let k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}

Date.prototype.getDateStr = function(){
    return this.format("yyyy-MM-dd");
}

Date.prototype.isSameByDate = function(otherDate){
    return this.getDateStr() == otherDate.getDateStr();
}
Date.prototype.isToday = function(){
    return this.isSameByDate(new Date());
}

/* override 小时/分钟/秒/毫秒*/
Date.prototype.overrideTime = function(theMerged){
    this.setHours(theMerged.getHours());
    this.setMinutes(theMerged.getMinutes());
    this.setSeconds(theMerged.getSeconds());
    this.setMilliseconds(theMerged.getMinutes());
    return this;
}

/*
 解析time inpot字符串的val值 
 重置小时或分钟
 假如val值为空 返回defaultWhenValNull  假如 defaultWhenValNull 没传入 返回空值（1970年1月1日）
 */
Date.prototype.overrideTimeInput = function(inputVal,defaultWhenValNull){
    if(inputVal.trim().length==0){
        if(defaultWhenValNull == undefined){
            this.setTime(0);
            return this;
        }
        this.setTime(defaultWhenValNull.getTime());
        return this;
    }
    let values = inputVal.split(":");
    let hours = parseInt(values[0]);
    let minutes = parseInt(values[1]);

    this.setHours(hours);
    this.setMinutes(minutes);
    return this;
}

Date.prototype.addDays = function(days){
    days = parseInt(days);
    this.setDate(this.getDate()+days);
    return this;
}


Date.prototype.addMinutes = function(minutes){
    minutes = parseInt(minutes);
    this.setMinutes(this.getMinutes()+minutes);
    return this;
}


Date.prototype.toStandardHoursAndMinutesOnly = function(defualtText){
    if(defualtText != undefined && this.isBlank()){
        return defualtText;
    }

    return this.format("hh:mm");
}

Date.prototype.toHoursAndMinutesOnly = function(defualtText){
    if(defualtText != undefined && this.isBlank()){
        return defualtText;
    }

    let hours = this.getHours();
    let minutes = this.getMinutes();
    let usingPM = hours>=12;
    hours = hours > 12 ? hours -12 : hours;
    minutes = minutes < 10 ? "0"+minutes : minutes;
    return hours+":"+minutes+" "+( usingPM?"pm":"am");
}

Date.prototype.toString = function(){
    return this.format("yyyy-MM-dd hh:mm:ss");
}

/*假如和今天是同一天 只显示小时 分钟  不显示秒 /假如同一年 则不显示年*/
Date.prototype.toSmartString = function(){
    if(this.isToday()){
        return this.toHoursAndMinutesOnly();
    }

    let today = new Date();
    if(today.getFullYear() == this.getFullYear()){
        return (this.getMonth()+1)+"-"+this.getDate()+" "+this.toHoursAndMinutesOnly();
    }

    return this.format("yyyy-MM-dd")+" "+this.toHoursAndMinutesOnly();
}


Date.prototype.isSunday = function(){
    return this.getDay() == 0;
}

Date.prototype.isSaturday = function(){
    return this.getDay() == 6;
}


Date.prototype.isBlank = function(){
    return this.format("yyyy-MM-dd") == "1970-01-01";
}
Date.prototype.isAfter = function(otherDate){
    return this>otherDate;
}



Date.prototype.countDaysDiffer = function(otherDate){
    return Math.round((this-otherDate)/(1*24*60*60*1000));
}

Date.prototype.countMinutesDiffer = function(otherDate){
    return parseInt((this-otherDate)/(60*1000));
}

Date.prototype.toStandardChinese = function(defaultValWhenBlank){
    if(this.isBlank()){
        return defaultValWhenBlank;
    }

    return this.getFullYear()+"年"+(this.getMonth()+1)+"月"+this.getDate()+"日";
}

Date.prototype.toChineseDate = function(defaultValWhenBlank){
    if(this.isBlank()){
        return defaultValWhenBlank;
    }

    let today = new Date();
    today.setHours(this.getHours());
    today.setMinutes(this.getMinutes());
    today.setSeconds(this.getSeconds());
    today.setMilliseconds(this.getMinutes());
    let countDays = this.countDaysDiffer(today);
    switch (countDays) {
        case 0:
            return "今天";
        case 1:
            return "明天";
        case -1:
            return "昨天";
        case 2:
            return "后天";
        case -2:
            return "前天";
        default:
            if(today.getFullYear() == this.getFullYear()){
                return (this.getMonth()+1)+"月"+this.getDate()+"日";
            }else{
                return this.getFullYear()+"年"+(this.getMonth()+1)+"月"+this.getDate()+"日";
            }
    }
}



/*==============================Date End==========================================*/


/*==============================Number Start========================================*/

/*decimal 保留几位小数 默认2位*/
Number.prototype.toText = function(decimal){
    let fixed = decimal == undefined ? 2 :decimal;
    let isInteger = parseInt(this) == parseFloat(this.toFixed(fixed));
    return isInteger ? parseInt(this) : this.toFixed(fixed).replace(/[.]?0+$/g,"");;
}

Number.prototype.transferToHoursMesIfPossible = function(decimal){
    let min = this;
    
    let prefix = min >= 0 ? "" : "-";
    let absNum = Math.abs(min);
    
    if(absNum>60){
        return prefix+(absNum/60).toText(decimal)+"小时";
    }
    return prefix+absNum.toText(decimal)+"分钟";
}


/*==============================Number End==========================================*/


/*==============================String Start========================================*/

String.prototype.transferToHoursMesIfPossible = function(decimal){
    return parseFloat(this).transferToHoursMesIfPossible(decimal)
}



String.prototype.replaceAll = function(s1,s2){ 
    return this.replace(new RegExp(s1,"gm"),s2); 
 }



/*==============================String End==========================================*/


/*==============================Array Start========================================*/
/**
 * 返回形如{
 *  "name": "abc"
 *  "value": 3
 * }对象的数组
 * @param {*} nameMapper 
 * @param {*} valueMapper 可缺省 
 */
 Array.prototype.sumBySpecificField = function(nameMapper,valueMapper){
    let rlt = [];
    
    this.forEach(item=>{
        let existedItem = rlt.find(p=>p.name==nameMapper(item));

        if(existedItem != undefined){
            existedItem.value += valueMapper == undefined ? 1 : valueMapper(item);
            return ;
        }

        rlt.push({
            name : nameMapper(item),
            value: valueMapper == undefined ?  1 : valueMapper(item)
        })
    })

    return rlt;    
}

/**
 * rlt keys属性 将记录有哪些可能
 * classifier 暂时只允许生成字符串吧
 */
Array.prototype.groupBy = function(classifier){
    let rlt = {
        keys:[]
    };

    this.forEach((e)=>{
        let key = classifier(e);
        if(!rlt.keys.contains(key)){
            rlt[key] = [];
            rlt.keys.push(key);
        }
        rlt[key].push(e);
    })

    return rlt;
}

Array.prototype.contains = function(e){
    return this.find(m=>m==e) != null;
}



/**
 * 排序，假设项太多，则合并少数项
 * @param {*} showDetailNum 
 * @returns 
 */
 Array.prototype.sortAndMergeSumRlt = function(showDetailNum){
    let num = showDetailNum ? showDetailNum : 7;
    this.sort((a,b)=>b.value-a.value);

    let othersText = "其它";

    let rlt = [];
    for(let i = 0 ; i<this.length ; i++){
        if(i < num){
            rlt.push(this[i]);
            continue;
        }

        if(rlt[rlt.length-1].name != othersText){
            rlt.push({
                name : othersText,
                value : this[i].value
            })
            continue;
        }

        rlt[rlt.length-1].value += this[i].value;

    }

    return rlt;
}


if (!Array.prototype.flatMap) {
    Object.defineProperty(Array.prototype, 'flatMap', {
      value: function(func) {
        let rlt = [];
        this.forEach(e=>{

            let unitRlt = func(e);
            if(!unitRlt.forEach instanceof Function){
                throw "wrong flatMapFunc";
            }

            unitRlt.forEach(ee=>{
                rlt.push(ee);
            });
        })

        return rlt;
      }
    });
}

Array.prototype.limit = function(limit){
    if(this.length<=limit)
        return this;
    
    return this.slice(0,limit);
}



if (!Array.prototype.find) {
    Object.defineProperty(Array.prototype, 'find', {
      value: function(predicate) {
       // 1. Let O be ? ToObject(this value).
        if (this == null) {
          throw new TypeError('"this" is null or not defined');
        }
  
        var o = Object(this);
  
        // 2. Let len be ? ToLength(? Get(O, "length")).
        var len = o.length >>> 0;
  
        // 3. If IsCallable(predicate) is false, throw a TypeError exception.
        if (typeof predicate !== 'function') {
          throw new TypeError('predicate must be a function');
        }
  
        // 4. If thisArg was supplied, let T be thisArg; else let T be undefined.
        var thisArg = arguments[1];
  
        // 5. Let k be 0.
        var k = 0;
  
        // 6. Repeat, while k < len
        while (k < len) {
          // a. Let Pk be ! ToString(k).
          // b. Let kValue be ? Get(O, Pk).
          // c. Let testResult be ToBoolean(? Call(predicate, T, « kValue, k, O »)).
          // d. If testResult is true, return kValue.
          var kValue = o[k];
          if (predicate.call(thisArg, kValue, k, o)) {
            return kValue;
          }
          // e. Increase k by 1.
          k++;
        }
  
        // 7. Return undefined.
        return undefined;
      }
    });
  }


/*==============================Array End==========================================*/
