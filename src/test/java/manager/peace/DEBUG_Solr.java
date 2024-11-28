package manager.peace;

import com.alibaba.fastjson2.JSON;
import manager.SelfManagerSpringbootApplication;
import manager.entity.general.books.SharingBook;
import manager.entity.general.career.NoteBook;
import manager.solr.SolrInvoker;
import manager.solr.SolrOperator;
import manager.system.Language;
import manager.system.SMDB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SelfManagerSpringbootApplication.class)
public class DEBUG_Solr{

    @Resource
    SolrInvoker invoker;
    @Resource
    SolrOperator operator;

    String coreName = "canI";

    @Test
    public void test1(){
        invoker.createCore("SharingBook3","E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\SharingBook");
    }

    @Test
    public void test11(){
        String docName = SMDB.E_SHARING_BOOK;
        String doc2 = SMDB.E_SHARING_BOOK+"TEST";
        invoker.getCoreStatus(docName).forEach((key,val)->{
            System.out.println(key+":"+val);
        });
        System.out.println(invoker.getCoreStatus(doc2).size());
    }


    @Test
    public void test3(){
        String docName = SMDB.E_SHARING_BOOK;
        invoker.createCore(SMDB.E_SHARING_BOOK,"E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\shareB");
    }


    @Test
    public void testInsert(){
        String docName = SMDB.E_SHARING_BOOK;
        boolean flag = operator.initCoreIfNotExist(docName,"E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\shareB");
        System.out.println(flag);
        SharingBook book = new SharingBook();
        book.setName_ch("ChineseName");
        book.setComment_en("EnglishName");
        book.setStatus(5);
        book.setCreateUtc(System.currentTimeMillis());
        book.setDefaultLang(Language.EN.name);
        book.set_version_((long)-1);
        List<String> vars = Arrays.asList("TestV1;;;V2","Oh my God");
        book.setVariables_en(vars);
        book.setDisplayPattern(1);
        //Reindex
        operator.insertDoc(book);
    }

    @Test
    public void testGet(){
        String docName = SMDB.E_SHARING_BOOK;
        operator.getDocById("1c0b100a-b689-4a2d-a878-c16e32afad30");
    }

    @Test
    public void testDelete(){
        operator.deleteById("lovely");
    }




}
