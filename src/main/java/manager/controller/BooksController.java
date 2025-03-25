package manager.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import manager.data.MultipleItemsResult;
import manager.solr.data.SolrSearchResult;
import manager.entity.general.books.PageNode;
import manager.entity.general.books.SharingBook;
import manager.service.books.BooksService;
import manager.util.UIUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Map;

import static manager.system.SelfXParams.*;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Resource
    private BooksService service;

    private static final String BOOKS_PATH = "/books";

    private static final String PAGES_PATH = "/pageNodes";

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
        Double index = param.getDouble(INDEX);
        String bookId = param.getString(BOOK_ID);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        service.createPage(loginId,bookId,name,lang,parentId,isRoot,index);
    }

    @GetMapping(PAGES_PATH+"/list")
    private MultipleItemsResult<PageNode> getPages(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(PARENT_ID)String parentId
            , @RequestParam(IS_ROOT)Boolean isRoot
            , @RequestParam(BOOK_ID)String bookId
            ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getPages(loginId,bookId,parentId,isRoot);
    }

    @GetMapping(PAGES_PATH)
    private PageNode getPageNode(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(ID)String id){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getPageNode(loginId,id);
    }

    @DeleteMapping(PAGES_PATH)
    private void deletePageNode(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        String parentId = param.getString(PARENT_ID);
        String bookId = param.getString(BOOK_ID);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        service.deletePageNode(loginId,bookId,parentId,isRoot,id);
    }

    @DeleteMapping(BOOKS_PATH)
    private void deleteBook(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        service.deleteBook(loginId,id);
    }

    @GetMapping(BOOKS_PATH+"/list")
    private MultipleItemsResult<SharingBook> getBooks(@RequestHeader("Authorization") String authorizationHeader
    ,@RequestParam(STATE)Integer state){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getBooks(loginId,state);
    }

    @GetMapping(BOOKS_PATH+"/search")
    private SolrSearchResult<SharingBook> searchBooks(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(SEARCH_INFO)String searchInfo
            , @RequestParam(PAGE_NUM)Integer pageNum
            ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.searchBooks(loginId,searchInfo,pageNum);
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
        service.updateBookPropsSyncly(loginId,id,updatingProps);
    }

    @PatchMapping(PAGES_PATH)
    private PageNode patchPageNode( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        Map<String,Object> updatingProps = JSON.parseObject(param.getString(JSON_OBJ));
        service.updatePageNodePropsSyncly(loginId,id,updatingProps);
        return service.getPageNode(loginId,id);
    }
    @PatchMapping(BOOKS_PATH+"/close")
    private void closeBook( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        service.closeBook(loginId,id);
    }
}
