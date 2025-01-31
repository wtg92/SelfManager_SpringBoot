package manager.service.books;

import manager.data.career.MultipleItemsResult;
import manager.entity.general.books.BookPage;
import manager.entity.general.books.SharingBook;

import java.util.List;
import java.util.Map;

public interface BooksService {
    abstract void createBook(long loginId,String name,String defaultLanguage,String comment);


    MultipleItemsResult<SharingBook> getBooks(long loginId, Integer state);

    void updateBookProps(long loginId, String bookId, Map<String,Object> updatingAttrs);

    void closeBook(long loginId, String id);

    SharingBook getBook(long loginId, String id);

    void createPage(long loginId,String bookId, String name, String lang, String parentId,Boolean isRoot, Integer index);

    BookPage getPage(long loginId, String id);

    MultipleItemsResult<BookPage> getPages(long loginId, String parentId, Boolean isRoot);
}
