package manager.peace;

import com.alibaba.fastjson2.JSON;
import manager.SelfXManagerSpringbootApplication;
import manager.dao.FilesDAO;
import manager.dao.UserDAO;
import manager.entity.general.FileRecord;
import manager.entity.general.SystemMapping;
import manager.entity.general.UserGroup;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.service.UserLogicImpl;
import manager.service.books.BooksService;
import manager.service.books.BooksSolrOperator;
import manager.solr.SolrInvoker;
import manager.solr.SolrOperator;
import manager.system.Language;
import manager.system.DBConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SelfXManagerSpringbootApplication.class)
public class DEBUG_Solr{

    @Resource
    SolrInvoker invoker;
    @Resource
    SolrOperator operator;

    @Resource
    BooksService booksService;

    @Resource
    BooksSolrOperator booksSolrOperator;
    String coreName = "canI";

    @Test
    public void test1(){
        invoker.createCore("SharingBook3","E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\SharingBook");
    }

    @Test
    public void test11(){
        String docName = DBConstants.E_SHARING_BOOK;
        String doc2 = DBConstants.E_SHARING_BOOK+"TEST";
        invoker.getCoreStatus(docName).forEach((key,val)->{
            System.out.println(key+":"+val);
        });
        System.out.println(invoker.getCoreStatus(doc2).size());
    }


    @Test
    public void test3(){
        String docName = DBConstants.E_SHARING_BOOK+"six";
        invoker.createCore(docName,"E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\shareB");
    }

    @Autowired
    UserLogicImpl ul;

    @Test
    public void testUser(){
        System.out.println(JSON.toJSONString(ul.getUserAllPerms(26)));
        ;
    }

    @Test
    public void testInsert(){
        String docName = DBConstants.E_SHARING_BOOK;
        boolean flag = operator.initCoreIfNotExist(docName,"E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\shareB");
        System.out.println(flag);
        SharingBook book = new SharingBook();
        book.setStatus(5);
        book.setCreateUtc(System.currentTimeMillis());
        book.setDefaultLang(Language.ENGLISH.name);
        book.set_version_((long)-1);
        List<String> vars = Arrays.asList("TestV1;;;V2","Oh my God");
        book.setDisplayPattern(1);
        //Reindex
    }

    @Test
    public void testGet(){
        String docName = DBConstants.E_SHARING_BOOK;
//        operator.getDocById("1c0b100a-b689-4a2d-a878-c16e32afad30");
    }

    @Test
    public void testDelete(){
    }


    @Test
    public void testQuery(){
        invoker.testSolrClient();
    }

    @Test
    public void testBook(){
        SharingBook book = new SharingBook();
        book.setCreateUtc(System.currentTimeMillis());
        book.set_version_((long)-1);
        List<String> vars = Arrays.asList("TestV1;;;V2","Oh my God");
        book.setId("abc");
        book.setDefaultLang("ukrainian");
        book.setDisplayPattern(1);
        book.setComment_ukrainian("oh");
        long userId = 6661;
        booksSolrOperator.insertBook(book,userId);
    }

    @Test
    public void testQueryBook(){
        String id = "PAGE_NODE__v1_f864834b-1ef0-4070-9d2a-cfcc369988fb";
        booksSolrOperator.countPagesForSpecificParentId(id,"c33221a7-cc2d-4139-9a01-d34b7fbc1435",1);
    }

    @Resource
    private UserDAO userDAO;
    @Resource
    private FilesDAO filesDAO;

    @Test
    public void testS3(){
        UserGroup group = new UserGroup();
        group.setName("ss");
        userDAO.insertUserGroup(group);
        SystemMapping mapping =new SystemMapping();
        mapping.setDesc("5");
        userDAO.insertSystemMapping(mapping);

        FileRecord record = new FileRecord();
        record.setPublic(false);
        filesDAO.insertFileRecord(record);


    }
}
