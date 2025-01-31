package manager.service.books;


import manager.booster.MultipleLangHelper;
import manager.cache.CacheOperator;
import manager.data.career.MultipleItemsResult;
import manager.entity.SMSolrDoc;
import manager.entity.general.books.BookPage;
import manager.entity.general.books.SharingBook;
import manager.exception.LogicException;
import manager.system.Language;
import manager.system.SMCores;
import manager.system.SMError;
import manager.system.SolrFields;
import manager.system.books.SharingBookDisplayPatterns;
import manager.system.books.BooksMultipleFields;
import manager.system.books.SharingBookStatus;
import manager.system.career.BookStyle;
import manager.util.SecurityUtil;
import manager.util.locks.UserLockManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class BooksServiceImpl implements BooksService{

    @Resource
    private UserLockManager locker;

    @Resource
    private BooksSolrOperator operator;

    @Resource
    CacheOperator cache;


    private static final Integer BOOK_DEFAULT_STATUS = SharingBookStatus.OPENED;

    private static final Integer BOOK_DEFAULT_DISPLAY_PATTERN = SharingBookDisplayPatterns.LIST;

    private static final Integer BOOK_DEFAULT_STYLE = BookStyle.GREEN.getDbCode();
    @Override
    public void createBook(long loginId,String name,String defaultLanguage,String comment) {
        /**
         * unnecessary but do not harm
         */
        if(Language.get(defaultLanguage)==Language.UNKNOWN){
            throw new LogicException(SMError.UNEXPECTED_ERROR,"IMPOSSIBLE lang " + defaultLanguage);
        }

        locker.lockByUserAndClass(loginId,()->{
            SharingBook book = new SharingBook();
            book.setDefaultLang(defaultLanguage);

            /**
             * 生成对应值
             */
            book = MultipleLangHelper.setFiledValue(book, BooksMultipleFields.NAME,defaultLanguage,name);
            book = MultipleLangHelper.setFiledValue(book, BooksMultipleFields.COMMENT,defaultLanguage,comment);

            /**
             * 确定一系列初始值
             */
            book.setStatus(BOOK_DEFAULT_STATUS);
            book.setDisplayPattern(BOOK_DEFAULT_DISPLAY_PATTERN);
            book.setSeqWeight(0);
            book.setStyle(BOOK_DEFAULT_STYLE);

            operator.insertBook(book,loginId);
        });
    }

    @Override
    public MultipleItemsResult<SharingBook> getBooks(long loginId,Integer state) {
        MultipleItemsResult<SharingBook> books = operator.getBooks(loginId, state);
        fill(books);
        return books;
    }

    private static void fill(MultipleItemsResult<SharingBook> books) {
        books.items.forEach(item->{
            item.setUpdaterEncodedId(SecurityUtil.encodeInfo(item.getUpdaterId()));
            item.setUpdaterId(null);
        });
    }

    @Override
    public void updateBookProps(long loginId, String bookId, Map<String, Object> updatingAttrs) {
        locker.lockByUserAndClass(loginId,()->{
            cache.saveBook(loginId,bookId,()->operator.updateBook(bookId,loginId,loginId,updatingAttrs));
        });
    }

    @Override
    public void closeBook(long loginId, String id) {
        //确定封存吗？（如果该笔记本不包含笔记页且不含备注，会被<em>直接删除</em>
        //if ..... delete
        Map<String,Object> param = new HashMap<>();
        param.put(SolrFields.STATUS,SharingBookStatus.CLOSED);
        updateBookProps(loginId,id,param);

    }

    @Override
    public SharingBook getBook(long loginId, String id) {
        return cache.getBook(loginId,id,()->operator.getBook(loginId,id));
    }

    @Override
    public void createPage(long loginId,String bookId, String name, String lang, String parentId,Boolean isRoot,Integer index) {
        locker.lockByUserAndClass(loginId,()->{
            BookPage page = new BookPage();
            page.setBookId(bookId);
            page.setParentIds(Collections.singletonList(generatePageParentId(parentId,isRoot)));
            page.setIndexes(Collections.singletonList(index));
            page = MultipleLangHelper.setFiledValue(page, BooksMultipleFields.NAME,lang,name);
            operator.insertPage(page,loginId);
        });
    }

    private static String generatePageParentId(String parentId,boolean isRoot){
        return (isRoot ? SMCores.SHARING_BOOK : SMCores.BOOK_PAGE) + "_" + parentId;
    }

    @Override
    public BookPage getPage(long loginId, String id) {
        return null;
    }

    @Override
    public MultipleItemsResult<BookPage> getPages(long loginId, String parentId, Boolean isRoot) {
        return operator.getPages(loginId, generatePageParentId(parentId,isRoot));
    }
}
