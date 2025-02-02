package manager.service.books;

import static manager.service.work.WorkContentConverter.addItemToPlan;
import static manager.service.work.WorkContentConverter.addItemToWorkSheet;
import static manager.service.work.WorkContentConverter.convertPlanContent;
import static manager.service.work.WorkContentConverter.convertWorkSheet;
import static manager.service.work.WorkContentConverter.pushToWorkSheet;
import static manager.service.work.WorkContentConverter.updateWorkItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;

import manager.service.work.WorkService;
import org.junit.Before;
import org.junit.Test;

import manager.TestUtil;
import manager.data.career.PlanContent;
import manager.data.career.WorkSheetContent;
import manager.data.proxy.career.PlanBalanceProxy;
import manager.data.proxy.career.WorkSheetProxy;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.exception.LogicException;
import manager.service.UserLogic;
import manager.system.SMError;
import manager.system.career.PlanItemType;
import manager.system.career.WorkSheetState;
import manager.util.TimeUtil;

public class WorkServiceTest {
	
	
	@Before
	public void setUp() throws Exception {
		TestUtil.initEnvironment();
		TestUtil.initData();
		TestUtil.addAdmin();
		TimeUtil.resetTravel();
	}
	
	/**
	 * 2020-10-1 创建计划A（开始日期当天，结束日期至今） 计划B （开始日期 2020-10-10，结束日期至今）
	 *  基于A开启今日工作表 ok -> 第二次开启今日工作表 fail -> 去到第二天 基于B开启工作表 fail ->去到 2020-10-10 开启工作表 ok 检查基于A开启的工作表（这时它应该超期了）
	 */

	/**
	 * A 1次                                
	 * B 10分钟                                                                                
	 * C A的儿子 分钟 mappingVl 50                        
	 * D B的儿子 比例0.5 
	 * 
	 *  添加 A 1次
	 *    C 100min
	 *    D 100min
	 * 
	 * WorkSheet
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateWSContentDetail() throws Exception {
		Plan base = new Plan();
		String catName1 = "A";
		String catName2 = "B";
		String noteFor1 = "some note";
		assert 1 == addItemToPlan(base,1, catName1 , 1, noteFor1 , PlanItemType.TIMES,  0, 0);
		assert 2 == addItemToPlan(base,1, catName2 , 10, "" , PlanItemType.MINUTES,  0, 0);
		
		PlanContent planContent = convertPlanContent(base);
		assertEquals(2, planContent.items.size());
		
		String sonCat = "A的儿子";
		assert 3 == addItemToPlan(base, 1, sonCat, 0, "", PlanItemType.MINUTES, 1, 50);
		planContent = convertPlanContent(base);
		assertEquals(2, planContent.items.size());
		assertEquals(1, planContent.items.get(0).getDescendants().size());
		
		String bSon = "B的儿子";
		assert 4 == addItemToPlan(base, 1, bSon, 0, "", PlanItemType.MINUTES, 2, 0.5);
		planContent = convertPlanContent(base);
		assertEquals(2, planContent.items.size());
		assertEquals(1, planContent.items.get(1).getDescendants().size());
		
		WorkSheet ws = new WorkSheet();
		pushToWorkSheet(base,ws);
		Calendar startTime = TimeUtil.getCurrentTime();
		assert 1 == addItemToWorkSheet(ws, 1, 1, 0, "", 0, false,startTime , TimeUtil.getBlank());
		TimeUtil.travelHours(1);
		updateWorkItem(ws, 1, 1,  1, "", 0, false, (long)0, (long)0);
		
		WorkSheetContent wsContent = convertWorkSheet(ws);
		
		WorkService.calculateWSContentDetail(wsContent);

		assertEquals(2 , wsContent.planItems.size());
		
		assertTrue(0==wsContent.planItems.get(0).remainingValForCur);
		assertTrue(10==wsContent.planItems.get(1).remainingValForCur);
		assertTrue(20==wsContent.planItems.get(1).descendants.get(0).remainingValForCur);
		
		/*相当于时间重叠*/
		assert 2 == addItemToWorkSheet(ws, 1, 3, 100, "", 0, false,startTime , TimeUtil.getBlank());
		assert 3 == addItemToWorkSheet(ws, 1, 4, 100, "", 0, false,startTime , TimeUtil.getBlank());
		
		wsContent = convertWorkSheet(ws);
		WorkService.calculateWSContentDetail(wsContent);
		assertEquals(100,wsContent.planItems.get(0).descendants.get(0).sumValForWorkItems);
		
		assertTrue(wsContent.planItems.get(0).remainingValForCur+"", -2==wsContent.planItems.get(0).remainingValForCur);
		
		assertTrue(wsContent.planItems.get(0).descendants.get(0).remainingValForCur+"", -100==wsContent.planItems.get(0).descendants.get(0).remainingValForCur);
		
		assertTrue(wsContent.planItems.get(1).remainingValForCur+"",-40==wsContent.planItems.get(1).remainingValForCur);
		assertTrue(-80==wsContent.planItems.get(1).descendants.get(0).remainingValForCur);
		
		
		assert 4 == addItemToWorkSheet(ws, 1, 3, 100, "", 0, false,startTime , TimeUtil.getBlank());
		wsContent = convertWorkSheet(ws);
		WorkService.calculateWSContentDetail(wsContent);
		assertEquals(200,wsContent.planItems.get(0).descendants.get(0).sumValForWorkItems);
		assertTrue(wsContent.planItems.get(0).remainingValForCur+"", -4==wsContent.planItems.get(0).remainingValForCur);
		assertTrue(wsContent.planItems.get(0).descendants.get(0).remainingValForCur+"", -200==wsContent.planItems.get(0).descendants.get(0).remainingValForCur);
		
		
		assertTrue(1==wsContent.workItems.get(0).remainingValAtStart);
		assertTrue(0==wsContent.workItems.get(1).remainingValAtStart);
		assertTrue(20==wsContent.workItems.get(2).remainingValAtStart);
		assertTrue(-100==wsContent.workItems.get(3).remainingValAtStart);
	}
	
	/**
	 * 开启工作表 --》工作表进行中--》同步所有--》工作表完成
	 */
	@Test
	public void testSyncPlanItemWithDept() throws Exception{
		UserLogic uL = UserLogic.getInstance();
		WorkService wL = WorkService.getInstance();
		
		/*表明admin已经存在 之后可以直接用userId==1了*/
		uL.getUser(1);
		

		/*随便加点 让它不会立马完成*/
		String minutesItem = "分数类型计划项";
		String timesItem = "次数类型计划项";
		int valForMinutes = 100;
		int valForTimes = 10;
		wL.addItemToPlan(1, 1, minutesItem, valForMinutes, "", PlanItemType.MINUTES, 0, 0);
		wL.addItemToPlan(1, 1, timesItem, valForTimes, "", PlanItemType.TIMES, 0, 0);
		
		assert 1 == wL.openWorkSheetToday(1,1);
		
		WorkSheetProxy ws = wL.loadWorkSheet(1, 1);
		assertEquals(WorkSheetState.ACTIVE, ws.ws.getState());
		
		PlanBalanceProxy dept =  wL.getBalance(1);
		assertEquals(0, dept.content.items.size());
		
		assertTrue(0 == ws.content.planItems.get(0).sumValForWorkItems);
		assertTrue(valForMinutes == ws.content.planItems.get(0).remainingValForCur);
		assertTrue(0 == ws.content.planItems.get(1).sumValForWorkItems);
		assertTrue(valForTimes == ws.content.planItems.get(1).remainingValForCur);
		
		wL.syncToBalance(1, 1, 1);
		
		ws = wL.loadWorkSheet(1, 1);
		assertEquals(WorkSheetState.ACTIVE, ws.ws.getState());
		
		dept =  wL.getBalance(1);
		assertEquals(1, dept.content.items.size());
		assertEquals(minutesItem, dept.content.items.get(0).getName());
		assertTrue(valForMinutes == dept.content.items.get(0).getValue());
		assertEquals(PlanItemType.MINUTES, dept.content.items.get(0).getType());
		
		assertTrue(0 == ws.content.planItems.get(0).remainingValForCur);
		assertTrue(ws.content.planItems.get(0).sumValForWorkItems+"",0 == ws.content.planItems.get(0).sumValForWorkItems);
		assertTrue(valForTimes == ws.content.planItems.get(1).remainingValForCur);
		
		wL.syncToBalance(1, 1, 2);
		
		ws = wL.loadWorkSheet(1, 1);
		assertEquals(WorkSheetState.FINISHED, ws.ws.getState());
		
		dept =  wL.getBalance(1);
		assertEquals(2, dept.content.items.size());
		assertEquals(timesItem, dept.content.items.get(1).getName());
		assertTrue(valForTimes == dept.content.items.get(1).getValue());
		assertEquals(PlanItemType.TIMES, dept.content.items.get(1).getType());
		
		assertTrue(0 == ws.content.planItems.get(0).remainingValForCur);
		assertTrue(0 == ws.content.planItems.get(1).remainingValForCur);
	}
	
}
