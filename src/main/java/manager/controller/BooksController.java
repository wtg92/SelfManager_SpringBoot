package manager.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import manager.data.career.MultipleItemsResult;
import manager.entity.general.books.BookPage;
import manager.entity.general.books.SharingBook;
import manager.service.books.BooksService;
import manager.util.UIUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static manager.system.SMParm.*;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Resource
    private BooksService service;

    private static final String BOOKS_PATH = "/books";

    private static final String PAGES_PATH = "/pages";

    @PostMapping(BOOKS_PATH)
    private void postBook( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String name = param.getString(NAME);
        String defaultLanguage = param.getString(DEFAULT_LANGUAGE);
        String comment = param.getString(COMMENT);
        service.createBook(loginId,name,defaultLanguage,comment);
    }

    @PostMapping(PAGES_PATH)
    private void postPage( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String name = param.getString(NAME);
        String lang = param.getString(LANGUAGE);
        String parentId = param.getString(PARENT_ID);
        Integer index = param.getIntValue(INDEX);
        String bookId = param.getString(BOOK_ID);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        service.createPage(loginId,bookId,name,lang,parentId,isRoot,index);
    }

    @GetMapping(PAGES_PATH+"/list")
    private MultipleItemsResult<BookPage> getPages(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(PARENT_ID)String parentId
            , @RequestParam(IS_ROOT)Boolean isRoot
            ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getPages(loginId,parentId,isRoot);
    }

    @GetMapping(PAGES_PATH)
    private BookPage getPage(@RequestHeader("Authorization") String authorizationHeader
            ,@RequestParam(ID)String id){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getPage(loginId,id);
    }

    @GetMapping(BOOKS_PATH+"/list")
    private MultipleItemsResult<SharingBook> getBooks(@RequestHeader("Authorization") String authorizationHeader
    ,@RequestParam(STATE)Integer state){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getBooks(loginId,state);
    }

    @GetMapping(BOOKS_PATH)
    private SharingBook getBook(@RequestHeader("Authorization") String authorizationHeader
            ,@RequestParam(ID)String id){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getBook(loginId,id);
    }



    @PatchMapping(BOOKS_PATH)
    private void patchBook( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        Map<String,Object> updatingProps = JSON.parseObject(param.getString(JSON_OBJ));
        service.updateBookProps(loginId,id,updatingProps);
    }
    @PatchMapping(BOOKS_PATH+"/close")
    private void closeBook( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        service.closeBook(loginId,id);
    }
}
