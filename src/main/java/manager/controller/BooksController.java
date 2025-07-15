package manager.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import manager.data.MultipleItemsResult;
import manager.solr.books.SharingLink;
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

    private static final String LINKS_PATH = "/links";

    @PostMapping(BOOKS_PATH)
    private String postBook( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String name = param.getString(NAME);
        String defaultLanguage = param.getString(DEFAULT_LANGUAGE);
        String comment = param.getString(COMMENT);
        return service.createBook(loginId,name,defaultLanguage,comment);
    }
    @DeleteMapping(LINKS_PATH)
    private void deleteLink(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String id = param.getString(ID);
        Boolean isCommunityLink = param.getBoolean(IS_COMMUNITY_LINK);
        service.deleteLink(loginId,isCommunityLink,id);
    }
    @PostMapping(LINKS_PATH)
    private String postLink( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Boolean isCommunityLink = param.getBoolean(IS_COMMUNITY_LINK);
        String bookId = param.getString(BOOK_ID);
        String defaultLanguage = param.getString(DEFAULT_LANGUAGE);
        String name = param.getString(NAME);
        return service.createLink(loginId,name,defaultLanguage,bookId,isCommunityLink);
    }

    @PatchMapping(LINKS_PATH)
    private SharingLink patchLink( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        Boolean isCommunityLink = param.getBoolean(IS_COMMUNITY_LINK);
        String id = param.getString(ID);
        Map<String,Object> updatingProps = JSON.parseObject(param.getString(JSON_OBJ));
        service.updateLinkProps(loginId,isCommunityLink,id,updatingProps);
        return service.getLink(loginId,isCommunityLink,id);
    }

    @GetMapping(LINKS_PATH+"/list")
    private MultipleItemsResult<SharingLink> getLinks(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(BOOK_ID)String bookId
            , @RequestParam(IS_COMMUNITY_LINK)Boolean isCommunityLink
    ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getLinks(loginId,bookId,isCommunityLink);
    }

    @GetMapping(LINKS_PATH)
    private SharingLink getLink(@RequestHeader("Authorization") String authorizationHeader
            ,@RequestParam(ID)String id
            , @RequestParam(IS_COMMUNITY_LINK)Boolean isCommunityLink){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getLink(loginId,isCommunityLink,id);
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
        return service.createPage(loginId,bookId,name,lang,parentId,index);
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
        String id = param.getString(ID);
        String bookId = param.getString(BOOK_ID);
        service.addPageParentNode(loginId,id,bookId,parentId,index);
    }

    @PostMapping(PAGES_PATH+"/copySinglePageNodeFromTheOwner")
    private void copySinglePageNodeFromTheOwner(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String srcId = param.getString(SRC_ID);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        String bookId = param.getString(BOOK_ID);
        service.copySinglePageNodeFromTheOwner(loginId,srcId,bookId,parentId,index);
    }

    @PostMapping(PAGES_PATH+"/copyPageNodeAndSubFromTheOwner")
    private void copyPageNodeAndSubFromTheOwner(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String srcId = param.getString(SRC_ID);
        String srcBookId = param.getString(SRC_BOOK_ID);
        String targetBookId = param.getString(BOOK_ID);
        String targetParentId = param.getString(PARENT_ID);
        Double targetIndex = param.getDouble(INDEX);
        service.copyPageNodeAndSubFromTheOwner(loginId,srcId,srcBookId,targetBookId,targetParentId,targetIndex);
    }


    @PostMapping(PAGES_PATH+"/movePageNodeAndSub")
    private void movePageNodeAndSub(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String srcId = param.getString(SRC_ID);
        String srcParentId = param.getString(SRC_PARENT_ID);
        String srcBookId = param.getString(SRC_BOOK_ID);

        String targetBookId = param.getString(BOOK_ID);
        String targetParentId = param.getString(PARENT_ID);
        Double targetIndex = param.getDouble(INDEX);

        service.movePageNodeAndSub(loginId,srcId,srcBookId,srcParentId,targetBookId,targetParentId,targetIndex);
    }


    @GetMapping(PAGES_PATH+"/parentNodes")
    private List<ParentNode<?>> getAllParentNodes(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(ID)String id){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getAllParentNodes(loginId,id);
    }

    @GetMapping(PAGES_PATH+"/totalPagesOfOwn")
    private long getTotalPagesOfOwn(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(ID)String id){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getTotalPagesOfOwn(loginId,id);
    }

    @GetMapping(PAGES_PATH+"/list")
    private MultipleItemsResult<PageNode> getPages(@RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(PARENT_ID)String parentId
            , @RequestParam(BOOK_ID)String bookId
            ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return service.getPages(loginId,bookId,parentId);
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
        service.deletePageNode(loginId,bookId,parentId,id);
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



    @PostMapping(BOOKS_PATH+"/emptyBookPages")
    private void emptyBookPages(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param){
        String id = param.getString(ID);
        long loginId = UIUtil.getLoginId(authorizationHeader);
        service.emptyBookPages(loginId,id);
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
