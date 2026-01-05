package manager;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Caffeine;
import manager.dao.DAOFactory;
import manager.dao.UserDAO;
import manager.dao.career.NoteDAO;
import manager.dao.career.WorkDAO;
import manager.data.MultipleItemsResult;
import manager.data.proxy.career.PlanProxy;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.solr.books.SharingBook;
import manager.entity.general.career.Plan;
import manager.service.AuthService;
import manager.service.UserService;
import manager.service.books.BooksService;
import manager.service.work.WorkService;
import manager.system.SelfXPerms;
import manager.system.books.SharingBookStatus;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SelfXManagerSpringbootApplication.class)
class ApplicationTests {

	@Autowired
	UserDAO userDAO;

	@Autowired
    UserService ul;

	@Autowired
	WorkDAO workDAO;


	@Resource
	WorkService workService;

	@Resource
	BooksService booksService;

	@Resource
	AuthService authService;

	@Autowired
	NoteDAO noteDAO;

	@Test
	public void testBooks(){
		List<NoteBook> noteBooks = noteDAO.selectBooksByOwner(1);
		System.out.println(JSON.toJSONString(noteBooks.stream().map(one->one.getName()+one.getId()).collect(Collectors.joining(","))));
	}

	@Test
	public void testNotes(){
		List<Note> notes = noteDAO.selectNoteInfosByBook(17);
		notes.forEach(note->{
			note.setContent(note.getContent().replaceAll("\\n",""));
		});
		System.out.println(JSON.toJSONString(notes));
	}


	@Test
	public void authTest(){
//		AlipayClient alipayClient = authService.getAlipayClient();
//		System.out.println(JSON.toJSONString(alipayClient));
	}


	@Test
	public void	testGetPlan(){
		Plan plan = workDAO.selectPlan(47);
		System.out.println(plan.getState());
	}



	@Test
	void contextLoads() {
		System.out.println("say Hello");
	}

	@Test
	public void testDB(){
		userDAO.insertUsersToGroup(List.of((long)1,(long)2),100);
	}



	@Test
	public  void testul(){
		ul.checkPerm(1, SelfXPerms.SEE_SELF_PLANS);
	}

	@Resource
	WorkService wl;

	@Test
	public void test2(){
		workDAO.includeWorkSheetByPlanId(1);
	}

	@Test
	public void testCaffeine(){
		/**
		 *
		 */
		try {
			Caffeine.newBuilder()
					.maximumSize(10_000)
					.expireAfterWrite(Duration.ofMinutes(5))
					.refreshAfterWrite(Duration.ofMinutes(1))
					.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



	@Test
	public  void testCache(){
		wl.loadWorkSheet(1,1213);
	}

	@Test
	public  void testWorkLogic(){
		long id = 39;
		int loginId = 1;
		PlanProxy planProxy = workService.loadPlan(loginId, id);
		System.out.println(JSON.toJSONString(planProxy));
		System.out.println(JSON.toJSONString(planProxy.plan.getTags()));
		workService.resetPlanTags(id,loginId,Arrays.asList("哈哈"));
		planProxy = workService.loadPlan(loginId, id);
		System.out.println(JSON.toJSONString(planProxy.plan.getTags()));
	}

	@Test
	public  void testWorkLogic2(){
		long id = 39;
		int loginId = 1;
		workService.resetPlanTags(loginId,id,Arrays.asList(""));
	}
}
