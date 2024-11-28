package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.entity.SMSolrDoc;
import manager.entity.general.books.SharingBook;
import manager.system.SMDB;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SolrOperator {

    @Resource
    private SolrInvoker invoker;

    public boolean initCoreIfNotExist(String coreName,String configDir){
        if(!invoker.coreExists(coreName)){
            invoker.createCore(coreName,configDir);
            return true;
        }
        return false;
    }

    public void insertDoc(SMSolrDoc book) {
        invoker.insertDoc(SMDB.E_SHARING_BOOK,book).jsonStr();
    }

    public void getDocById(String id) {
        System.out.println(
                JSON.toJSONString(JSON.parseObject(invoker.getDocument(SMDB.E_SHARING_BOOK,id).jsonStr(),SharingBook.class))
                 );
    }

    public void deleteById(String id) {
        invoker.deleteById(SMDB.E_SHARING_BOOK,id);
    }
}
