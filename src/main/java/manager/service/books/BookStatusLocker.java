package manager.service.books;

import manager.cache.CacheOperator;
import manager.exception.LogicException;
import manager.solr.books.SharingBook;
import manager.system.SelfXErrors;
import manager.system.books.SharingBookStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Component
public class BookStatusLocker {

    private Set<String> COPYING_BOOKS = new HashSet<>();

    private Set<String> DELETING_BOOKS = new HashSet<>();

    private Set<String> PAGE_DELETING_BOOKS = new HashSet<>();


    private static String generateKey(long loginId, String bookId){
        return loginId+"_"+bookId;
    }

    public void startDeletingPage(long loginId, String bookId){
        checkDoingOperations(loginId,bookId);
        PAGE_DELETING_BOOKS.add(generateKey(loginId,bookId));
    }
    public void endDeletingPage(long loginId,String bookId){
        PAGE_DELETING_BOOKS.remove(generateKey(loginId,bookId));
    }

    public void startDeletingBook(long loginId, String bookId){
        checkDoingOperations(loginId,bookId);
        DELETING_BOOKS.add(generateKey(loginId,bookId));
    }

    public void endDeletingBook(long loginId,String bookId){
        DELETING_BOOKS.remove(generateKey(loginId,bookId));
    }

    public void startCopying(long loginId, String bookId){
        checkDoingOperations(loginId,bookId);
        COPYING_BOOKS.add(generateKey(loginId,bookId));
    }
    public void endCopying(long loginId,String bookId){
        COPYING_BOOKS.remove(generateKey(loginId,bookId));
    }


    public void fill(long loginId,SharingBook book){
        String key = generateKey(loginId,book.getId());
        if(DELETING_BOOKS.contains(key)){
            book.setStatus(SharingBookStatus.DELETING);
        }
        if(PAGE_DELETING_BOOKS.contains(key)){
            book.setStatus(SharingBookStatus.PAGES_DELETING);
        }
        if(COPYING_BOOKS.contains(key)){
            book.setStatus(SharingBookStatus.COPYING);
        }
    }

    private void checkDoingOperations(long loginId,String bookId){
        String key = generateKey(loginId,bookId);
        if(DELETING_BOOKS.contains(key)){
            throw new LogicException(SelfXErrors.CANNOT_OPERATE_BOOK_IN_ILLEGAL_STATUS,SharingBookStatus.DELETING);
        }
        if(PAGE_DELETING_BOOKS.contains(key)){
            throw new LogicException(SelfXErrors.CANNOT_OPERATE_BOOK_IN_ILLEGAL_STATUS,SharingBookStatus.PAGES_DELETING);
        }
        if(COPYING_BOOKS.contains(key)){
            throw new LogicException(SelfXErrors.CANNOT_OPERATE_BOOK_IN_ILLEGAL_STATUS,SharingBookStatus.COPYING);
        }
    }

}
