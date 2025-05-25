package manager.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import manager.data.MultipleItemsResult;
import manager.solr.data.ParentNode;
import manager.solr.data.SolrSearchRequest;
import manager.solr.data.SolrSearchResult;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.service.books.BooksService;
import manager.util.UIUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;
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
    private String postBook( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String name = param.getString(NAME);
        String defaultLanguage = param.getString(DEFAULT_LANGUAGE);
        String comment = param.getString(COMMENT);
        return service.createBook(loginId,name,defaultLanguage,comment);
    }

    @PostMapping(PAGES_PATH)
    private String postPage( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String name = param.getString(NAME);
        String lang = param.getString(LANGUAGE);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        String bookId = param.getString(BOOK_ID);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        return service.createPage(loginId,bookId,name,lang,parentId,isRoot,index);
    }

    @PostMapping(PAGES_PATH+"/calculatePath")
    private List<PageNode> calculatePath(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        return service.calculatePath(loginId,id);
    }

    @PostMapping(PAGES_PATH+"/parentNode")
    private void addPageParentNode(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        String id = param.getString(ID);
        String bookId = param.getString(BOOK_ID);
        service.addPageParentNode(loginId,id,bookId,parentId,isRoot,index);
    }

    @PostMapping(PAGES_PATH+"/copySinglePageNodeFromTheOwner")
    private void copySinglePageNodeFromTheOwner(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String srcId = param.getString(SRC_ID);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        String bookId = param.getString(BOOK_ID);
        service.copySinglePageNodeFromTheOwner(loginId,srcId,bookId,parentId,isRoot,index);
    }

    @PostMapping(PAGES_PATH+"/copyPageNodeAndSubFromTheOwner")
    private void copyPageNodeAndSubFromTheOwner(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String srcId = param.getString(SRC_ID);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        Boolean isRoot = param.getBoolean(IS_ROOT);
        String bookId = param.getString(BOOK_ID);
        service.copyPageNodeAndSubFromTheOwner(loginId,srcId,bookId,parentId,isRoot,index);
    }


    @PostMapping(PAGES_PATH+"/movePageNodeAndSub")
    private void movePageNodeAndSub(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String srcId = param.getString(SRC_ID);
        String srcParentId = param.getString(SRC_PARENT_ID);
        String srcBookId = param.getString(SRC_BOOK_ID);
        Boolean srcIsRoot = param.getBoolean(SRC_IS_ROOT);

        String targetBookId = param.getString(BOOK_ID);
        String targetParentId = param.getString(PARENT_ID);
        Double targetIndex = param.getDouble(INDEX);
        Boolean targetIsRoot = param.getBoolean(IS_ROOT);

        service.movePageNodeAndSub(loginId,srcId,srcBookId,srcParentId,srcIsRoot,targetBookId,targetParentId,targetIndex,targetIsRoot);
    }


    @GetMapping(PAGES_PATH+"/parentNodes")
    private List<ParentNode<?>> getAllParentNodes(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(ID)String id){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getAllParentNodes(loginId,id);
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
    ,@RequestParam(STATES)List<Integer> states){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getBooks(loginId,states);
    }

    @PostMapping(BOOKS_PATH+"/search")
    private SolrSearchResult<SharingBook> searchBooks(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody SolrSearchRequest searchRequest
    ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.searchBooks(loginId,searchRequest);
    }

    @PostMapping(PAGES_PATH+"/search")
    private SolrSearchResult<PageNode> searchPageNodes(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody SolrSearchRequest searchRequest
    ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.searchPageNodes(loginId,searchRequest);
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
        service.updateBookPropsInSync(loginId,id,updatingProps);
    }

    @PatchMapping(PAGES_PATH)
    private PageNode patchPageNode( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        String bookId = param.getString(BOOK_ID);
        Map<String,Object> updatingProps = JSON.parseObject(param.getString(JSON_OBJ));
        service.updatePageNodeProps(loginId,bookId,id,updatingProps);
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
