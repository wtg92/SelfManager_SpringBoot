package manager.logic.career;

import java.time.ZoneId;
import java.util.Calendar;

import manager.entity.general.career.Plan;
import manager.system.career.PlanState;
import manager.util.RefiningUtil;
import manager.util.TimeUtil;
import org.junit.Test;

import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;

public class DEBUG_WorkLogic {


	@Test
	public void testTimezone(){
		final ZoneId z1 = ZoneId.of("Asia/Tokyo");
		final ZoneId z2 = ZoneId.of("Asia/Tokyo");
		System.out.println(z1.equals(z2));
	}

	@Test
	public void testCalculateState2(){
		System.out.println(TimeUtil.isBlank(TimeUtil.getBlank()));

		Plan plan = new Plan();
		plan.setTimezone("Asia/Tokyo");
		plan.setStartUtc(Long.parseLong("1704326400000"));
		plan.setEndUtc((long)0);
	}


	@Test
	public void testCalculateState(){
		Plan plan = new Plan();
		plan.setTimezone("Asia/Tokyo");
		plan.setStartUtc(Long.parseLong("1704326400000"));
		plan.setEndUtc((long)0);
		final PlanState planState = WorkLogic.calculateStateByNow(plan);
		System.out.println(planState);
	}


	@Test
	public void test() throws LogicException, DBException {
		WorkLogic wL = WorkLogic.getInstance();
		String note = "";
		for(int i=0;i<5000;i++) {
			note+="a";
		}
		wL.saveWorkSheet(1, 1, note);
	}
	
	@Test
	public void test1() throws LogicException, DBException {
		WorkLogic wL = WorkLogic.getInstance();
		
		final int loginerId = 1;
		new Thread(()->{
			try {
				wL.loadActivePlans(loginerId);
			} catch (LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				wL.loadActivePlans(loginerId);
			} catch (LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				wL.loadActivePlans(loginerId);
			} catch (LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				wL.loadActivePlans(loginerId);
			} catch (LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				wL.loadActivePlans(loginerId);
			} catch (LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		
		
		
		new Thread(()->{
			try {
				wL.loadWorkSheetInfosRecently(loginerId, 1);
			} catch (DBException | LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				wL.loadWorkSheetCount(loginerId, Calendar.getInstance());
			} catch (SMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		
		new Thread(()->{
			try {
				wL.loadWorkSheet(loginerId, 326);
			} catch (SMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		while(true) {
			
		}
	}
}
