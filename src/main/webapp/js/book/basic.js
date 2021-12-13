

/*1班和2班的同学要进行文理文科了*/
let studentsOfClass1 = getAllStudentsInClass1();
let studentsOfClass2 = getAllStudentsInClass2();

/*第一步，把两个班的学生放在一块*/
let studentsOfBothClass = studentsOfClass1.concat(studentsOfClass2);

/*  
 *  无论是1班还是2班的学生，在高二文理分科之前，都至少进行了四次阶段性考试
 *  高一上、下学期的期中、期末考试。 
 */
studentsOfBothClass.forEach(student=>{
	for(let i=0 ;i < 4 ;i++) {
		let score = examine(student);
		student.historicalScore.push(score);
	}
});

/* 将要去文科班的，是选择了主修文科的同学 */
let studentsForLiberalArtsClass = studentsOfBothClass.filter(student=>student.selectedMajorCategory() == LIBERAL_ARTS);

/* 将要去理科班的，是选择了主修理科的同学 */
let studentsForScienceClass = studentsOfBothClass.filter(student=>student.selectedMajorCategory() == SCIENCE);

/* 谁能去理科实验班呢？ 理科班中，按历史成绩逆序排序，排名第一的同学 */
let studentForExperimentalClass = studentsForScienceClass.sort((a,b)=>{
    let averageScoreOfA = calculateAverage(a.historicalScore);
    let averageScoreOfB = calculateAverage(b.historicalScore);
    return averageScoreOfB - averageScoreOfA;
})[0];

/* 尽管被分到理科实验班的同学成绩很好，但他还是不会分身术 */
let indexForRemove = studentsForScienceClass.indexOf(studentForExperimentalClass);
studentsForScienceClass.splice(indexForRemove, 1);

/* 人员分配完毕，工作完成了*/
assignToLiberalArtClass(studentsForLiberalArtsClass);
assignToScienceClass(studentsForScienceClass);
assignToExperimentalClass(studentForExperimentalClass);



function calculateAverage(){

}




let strArray = ["马尔科姆的一家","发展受阻","钢之炼金术师","傲骨贤妻","无问西东"];

let sumOfArrayChars = strArray.reduce((accumulator,currentValue)=>
	accumulator + currentValue.length
,0);

console.log(sumOfArrayChars);





function selectBestOne(t1,t2,t3,judger) {
	let score1 = judger(t1);
	let score2 = judger(t2);
	let score3 = judger(t3);
	if(score1 >= score2 && score1 >= score3) {
		return t1;
	}
	if(score2 >= score1 && score2 >= score3) {
		return t2;
	}
	return t3;
}

function Son(){

}

let son = new Son()
//true
console.log(son.__proto__ == Son.prototype)





function a1(){
	console.log("a1.....");
}

function a2(){
	console.log("a2.....");
}

//true
console.log(a1 == a1.prototype.constructor);

//打印 a1..... 证明要不然执行的是a1、要不然执行的是a1.prototype.constructor
let m = new a1();

a1.prototype.constructor = a2;
//打印 a1..... 证明执行的是a1而非a1.prototype.constructor
new a1();

//true
console.log(m.constructor == a1.prototype.constructor);
//false 
console.log(m.constructor == a1);
//打印 a2..... 结合之前两句打印，足以证明m.constructor指向的是 a1.prototype.constructor
m.constructor();


console.log(a2 == a2.prototype.constructor);
let ok = new a2();
console.log(ok)

function b(){
 
}

let a=new b()

console.log(a.__proto__ == b.prototype)
console.log(b.prototype.prototype == null)
console.log(b.prototype)
console.log(b.prototype.constructor)
console.log(b.__proto__==Function.prototype)
console.log(Function.prototype==Object.__proto__)
console.log(Function.__proto__==Function.prototype)
console.log(Object.__proto__==Function.prototype)
console.log(Object.prototype)
console.log(b.prototype.__proto__==Object.prototype)
console.log(Function.prototype.__proto__==Object.prototype)
console.log(Object.prototype.__proto__==null)







/*我的面前摆着三个苹果*/
let a1 = new Apple();
let a2 = new Apple();
let a3 = new Apple();

let theSweetest = selectBestOne(a1, a2, a3,
		/*闻起来香的苹果，我就认为更甜*/
		(apple)=>apple.smellGoodLevel);

/*我吃到了最甜的苹果，我很开心*/
eatAndHappyForMe(theSweetest);

/*有三个好看的人对我有好感*/
let p1 = new People();
let p2 = new People();
let p3 = new People();

let theMostSuitable = selectBestOne(p1, p2, p3,
		/*相处更舒服的人，我认为是更适合的*/
		(people)=>people.comfortableLevel);

/*我和最合适的人在一起了，我很开心*/
accompanyAndHappyForMe(theMostSuitable);







let obj = {
    sonInt:5
}
let targetKey = Object.keys(obj).filter(e=>e == 'sonInt');
//输出5
console.log(obj[targetKey])

