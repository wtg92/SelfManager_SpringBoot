

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












