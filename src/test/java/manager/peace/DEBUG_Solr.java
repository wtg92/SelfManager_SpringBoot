package manager.peace;

import manager.SelfManagerSpringbootApplication;
import manager.entity.general.career.NoteBook;
import manager.solr.SolrInvoker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SelfManagerSpringbootApplication.class)
public class DEBUG_Solr {

    @Resource
    SolrInvoker invoker;

    String coreName = "canI";

    @Test
    public void test1(){
        invoker.createCore("SharingBook3","E:\\solr\\solr-9.6.1\\server\\solr\\configsets\\SharingBook");
    }

    @Test
    public void test11(){
        System.out.println(invoker.getCoreStatus("mynewzz"));
    }

    @Test
    public void test2(){
        invoker.addAutoIncrementalID(coreName);
    }

    @Test
    public void test3(){
        NoteBook book = new NoteBook();
        book.setName("hoho");
        invoker.add(coreName,book);
    }

}
