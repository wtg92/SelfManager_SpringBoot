package manager.service.books;
import static manager.service.work.WorkContentConverter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


import manager.data.worksheet.PlanContent;
import manager.data.worksheet.WorkSheetContent;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.entity.virtual.career.PlanItem;
import manager.system.career.PlanItemType;
import manager.util.TimeUtil;
/**
 * ContentConvert管理 Plan WorkSheet的Content的相关，那这个类就理应能表达出大部分真实的逻辑
 */
public class WorkContentConverterTest {
	
	
	/**
	 * 创建一个plan 添加item 添加儿子item  更新儿子item 删除儿子item 删除item
	 */
	@Test
	public void testPlan() throws Exception{
		Plan base = new Plan();
		String cateName = "运动";
		String note = "some note";
		addItemToPlan(base,1, cateName , 1, note , PlanItemType.TIMES,  0, 0);
		assertTrue(base.getContent().length()>0);
		
		PlanContent planContent = convertPlanContent(base);
		assertEquals(1, planContent.items.size());
		PlanItem one = planContent.items.get(0);
		assertTrue(1 == one.getId());
		assertEquals(cateName, one.getName());
		assertEquals(note,one.getNote());
		
		String cateNameForSon = "跳绳";
		addItemToPlan(base,1, cateNameForSon, 35, "", PlanItemType.MINUTES,  1,35);
		planContent = convertPlanContent(base);
		assertEquals(1, planContent.items.size());
		one = planContent.items.get(0);
		assertTrue(1 == one.getId());
		assertEquals(cateName, one.getName());
		assertEquals(note,one.getNote());
		
		assertEquals(1, one.getDescendants().size());
		PlanItem son = one.getDescendants().get(0);
		
		assertTrue(2 == son.getId());
		assertEquals(cateNameForSon, son.getName());
		assertEquals("",son.getNote());
		
		cateNameForSon="游泳";
		updatePlanItem(base,1, 2, cateNameForSon,1,"", 1);
		
		planContent = convertPlanContent(base);
		assertEquals(1, planContent.items.size());
		one = planContent.items.get(0);
		assertTrue(1 == one.getId());
		assertEquals(cateName, one.getName());
		assertEquals(note,one.getNote());
		
		assertEquals(1, one.getDescendants().size());
		son = one.getDescendants().get(0);
		
		assertTrue(2 == son.getId());
		
		assertEquals(cateNameForSon, son.getName());
		assertEquals("",son.getNote());
		
		removeItemFromPlan(base,1, 2);
		planContent = convertPlanContent(base);
		assertEquals(1, planContent.items.size());
		one = planContent.items.get(0);
		assertTrue(1 == one.getId());
		assertEquals(cateName, one.getName());
		assertEquals(note,one.getNote());
		
		assertEquals(0, one.getDescendants().size());
		
		removeItemFromPlan(base, 1,1);
		planContent = convertPlanContent(base);
		assertEquals(0, planContent.items.size());
	}
	
	/**
	 * 建立一个Plan 基于Plan 建立一个WorkSheet 添加一个正在进行中的 item  验证 修改item 验证 删除Item 验证
	 */
	@Test
	public void testWorkSheet() throws Exception {
		Plan base = new Plan();
		String catName1 = "运动";
		String catName2 = "读书";
		String noteFor1 = "some note";
		assert 1 == addItemToPlan(base,1, catName1 , 1, noteFor1 , PlanItemType.TIMES,  0, 0);
		assert 2 == addItemToPlan(base,1, catName2 , 10, "" , PlanItemType.MINUTES,  0, 0);
		
		PlanContent planContent = convertPlanContent(base);
		assertEquals(2, planContent.items.size());
		assert 1 == planContent.items.get(0).getId();
		assert 2 == planContent.items.get(1).getId();
		
		String sonCat = "运动的儿子";
		assert 3 == addItemToPlan(base, 1, sonCat, 0, "", PlanItemType.MINUTES, 1, 50);
		planContent = convertPlanContent(base);
		assertEquals(2, planContent.items.size());
		assertEquals(1, planContent.items.get(0).getDescendants().size());
		
		WorkSheet ws = new WorkSheet();
		pushToWorkSheet(base,ws);
		
		WorkSheetContent wsContent = convertWorkSheet(ws);
		assertEquals(2, wsContent.planItems.size()); 
		assertEquals(wsContent.planItems.get(0).item.getName(), 1 , wsContent.planItems.get(0).descendants.size()); 
		
		assertEquals(catName1, wsContent.planItems.get(0).item.getName());
		assertEquals(catName2, wsContent.planItems.get(1).item.getName());
		assertEquals(sonCat, wsContent.planItems.get(0).descendants.get(0).item.getName());
		
		assert 1 == wsContent.planItems.get(0).item.getId();
		assert 2 == wsContent.planItems.get(1).item.getId();
		assert 3 == wsContent.planItems.get(0).descendants.get(0).item.getId();
		
		String wsItemNote = "啧啧";
		int mood = 3;
		long startTime = System.currentTimeMillis();
		addItemToWorkSheet(ws, 1, 1, 10, wsItemNote, mood, false,startTime ,(long)0);
		
		wsContent = convertWorkSheet(ws);
		assertEquals(1, wsContent.workItems.size());
		assert 1 == wsContent.workItems.get(0).item.getId();
		assertEquals(wsItemNote, wsContent.workItems.get(0).item.getNote());
		assertTrue(mood == wsContent.workItems.get(0).item.getMood());
		assertTrue(TimeUtil.isBlank(wsContent.workItems.get(0).item.getEndTime()));

		long end = System.currentTimeMillis();
		mood = 5;
		updateWorkItem(ws, 1, 1, 100, "", mood, true, startTime, (long)0);

		wsContent = convertWorkSheet(ws);
		assertEquals(1, wsContent.workItems.size());
		assertEquals("", wsContent.workItems.get(0).item.getNote());
		assertTrue(mood == wsContent.workItems.get(0).item.getMood());

		removeItemFromWorkSheet(ws, 1, 1);
		wsContent = convertWorkSheet(ws);
		assertEquals(0, wsContent.workItems.size());
	}
	
}
