package manager.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import manager.booster.SecurityBooster;
import manager.data.MultipleItemsResult;
import manager.service.books.BooksService;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.solr.books.SharingLink;
import manager.solr.data.ParentNode;
import manager.solr.data.SharingLinkDetail;
import manager.solr.data.SharingLinkPatchReq;
import manager.solr.data.SolrSearchRequest;
import manager.solr.data.SolrSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static manager.system.SelfXParams.BOOK_ID;
import static manager.system.SelfXParams.COMMENT;
import static manager.system.SelfXParams.DEFAULT_LANGUAGE;
import static manager.system.SelfXParams.ENCODING;
import static manager.system.SelfXParams.ID;
import static manager.system.SelfXParams.INDEX;
import static manager.system.SelfXParams.IS_COMMUNITY_LINK;
import static manager.system.SelfXParams.JSON_OBJ;
import static manager.system.SelfXParams.LANGUAGE;
import static manager.system.SelfXParams.NAME;
import static manager.system.SelfXParams.PARENT_ID;
import static manager.system.SelfXParams.SRC_BOOK_ID;
import static manager.system.SelfXParams.SRC_ID;
import static manager.system.SelfXParams.SRC_PARENT_ID;
import static manager.system.SelfXParams.STATES;
import static manager.system.SelfXParams.STATUS;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BooksService service;

    @Autowired private SecurityBooster securityBooster;
    
    private static final String BOOKS_PATH = "/books";

    private static final String PAGES_PATH = "/pageNodes";

    private static final String LINKS_PATH = "/links";

    private static final String SHARING_PATH = "/sharing";

    private static final String SHARING_LINK_PATH = LINKS_PATH+SHARING_PATH;
    
    @PostMapping(BOOKS_PATH)
    private String postBook(HttpServletRequest request, @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String name = param.getString(NAME);
        String defaultLanguage = param.getString(DEFAULT_LANGUAGE);
        String comment = param.getString(COMMENT);
        return service.createBook(loginId,name,defaultLanguage,comment);
    }
    @DeleteMapping(LINKS_PATH)
    private void deleteLink(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        Boolean isCommunityLink = param.getBoolean(IS_COMMUNITY_LINK);
        service.deleteLink(loginId,isCommunityLink,id);
    }
    @PostMapping(LINKS_PATH)
    private String postLink( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        Boolean isCommunityLink = param.getBoolean(IS_COMMUNITY_LINK);
        String bookId = param.getString(BOOK_ID);
        String defaultLanguage = param.getString(DEFAULT_LANGUAGE);
        String name = param.getString(NAME);
        return service.createLink(loginId,name,defaultLanguage,bookId,isCommunityLink);
    }

    @PostMapping(LINKS_PATH+"/switchLinkStatus")
    private void switchLinkStatus( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        Boolean isCommunityLink = param.getBoolean(IS_COMMUNITY_LINK);
        Integer status = param.getInteger(STATUS);
        service.switchLinkStatus(loginId,isCommunityLink,id,status);
    }

    @PatchMapping(LINKS_PATH)
    private void patchLink( HttpServletRequest request
            , @RequestBody SharingLinkPatchReq param ){
        long loginId = securityBooster.requireUserId(request);
        service.updateLink(loginId,param);
    }

    @GetMapping(LINKS_PATH+"/list")
    private MultipleItemsResult<SharingLink> getLinks(HttpServletRequest request
            , @RequestParam(BOOK_ID)String bookId
            , @RequestParam(IS_COMMUNITY_LINK)Boolean isCommunityLink
    ){
        long loginId = securityBooster.requireUserId(request);
        return service.getLinks(loginId,bookId,isCommunityLink);
    }

    @GetMapping(LINKS_PATH)
    private SharingLink getLink(HttpServletRequest request
            ,@RequestParam(ID)String id
            , @RequestParam(IS_COMMUNITY_LINK)Boolean isCommunityLink){
        long loginId = securityBooster.requireUserId(request);
        return service.getLinkByOwner(loginId,isCommunityLink,id);
    }

    @GetMapping(SHARING_LINK_PATH+"/detail")
    private SharingLinkDetail getLinkDetail(HttpServletRequest request, @RequestParam(ENCODING)String encoding){
        return service.getLinkDetail(securityBooster.requireOptionalUserId(request),encoding);
    }

    @GetMapping(SHARING_LINK_PATH+"/page")
    private PageNode getSharingLinkPage(HttpServletRequest request,@RequestParam(ENCODING)String encoding , @RequestParam(value = ID,required = false)String id){
        return service.getSharingLinkPage(securityBooster.requireOptionalUserId(request),encoding,id);
    }

    @GetMapping(SHARING_LINK_PATH+"/list")
    private MultipleItemsResult<PageNode> getSharingLinkPages(HttpServletRequest request, @RequestParam(PARENT_ID)String parentId
            , @RequestParam(ENCODING)String encoding
    ){
        return service.getSharingLinkPages(securityBooster.requireOptionalUserId(request),encoding,parentId);
    }


    @PostMapping(PAGES_PATH)
    private String postPage( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String name = param.getString(NAME);
        String lang = param.getString(LANGUAGE);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        String bookId = param.getString(BOOK_ID);
        return service.createPage(loginId,bookId,name,lang,parentId,index);
    }

    @PostMapping(PAGES_PATH+"/calculatePath")
    private List<String> calculatePath(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        return service.calculatePath(loginId,id).stream().map(PageNode::getId).toList();
    }

    @PostMapping(PAGES_PATH+"/parentNode")
    private void addPageParentNode(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        String id = param.getString(ID);
        String bookId = param.getString(BOOK_ID);
        service.addPageParentNode(loginId,id,bookId,parentId,index);
    }

    @PostMapping(PAGES_PATH+"/copySinglePageNodeFromTheOwner")
    private void copySinglePageNodeFromTheOwner(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String srcId = param.getString(SRC_ID);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        String bookId = param.getString(BOOK_ID);
        service.copySinglePageNodeFromTheOwner(loginId,srcId,bookId,parentId,index);
    }
    @PostMapping(SHARING_LINK_PATH+"/copySinglePageNodeFromLink")
    private void copySinglePageNodeFromLink(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String encoding = param.getString(ENCODING);
        String srcID = param.getString(SRC_ID);
        String parentId = param.getString(PARENT_ID);
        Double index = param.getDouble(INDEX);
        String bookId = param.getString(BOOK_ID);
        service.copySinglePageNodeFromLink(loginId,encoding,srcID,bookId,parentId,index);
    }
    @PostMapping(PAGES_PATH+"/copyPageNodeAndSubFromTheOwner")
    private void copyPageNodeAndSubFromTheOwner(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String srcId = param.getString(SRC_ID);
        String srcBookId = param.getString(SRC_BOOK_ID);
        String targetBookId = param.getString(BOOK_ID);
        String targetParentId = param.getString(PARENT_ID);
        Double targetIndex = param.getDouble(INDEX);
        service.copyPageNodeAndSubFromTheOwner(loginId,srcId,srcBookId,targetBookId,targetParentId,targetIndex);
    }


    @PostMapping(PAGES_PATH+"/movePageNodeAndSub")
    private void movePageNodeAndSub(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String srcId = param.getString(SRC_ID);
        String srcParentId = param.getString(SRC_PARENT_ID);
        String srcBookId = param.getString(SRC_BOOK_ID);

        String targetBookId = param.getString(BOOK_ID);
        String targetParentId = param.getString(PARENT_ID);
        Double targetIndex = param.getDouble(INDEX);

        service.movePageNodeAndSub(loginId,srcId,srcBookId,srcParentId,targetBookId,targetParentId,targetIndex);
    }


    @GetMapping(PAGES_PATH+"/parentNodes")
    private List<ParentNode<?>> getAllParentNodes(HttpServletRequest request
            , @RequestParam(ID)String id){
        long loginId = securityBooster.requireUserId(request);
        return service.getAllParentNodes(loginId,id);
    }

    @GetMapping(PAGES_PATH+"/totalPagesOfOwn")
    private long getTotalPagesOfOwn(HttpServletRequest request
            , @RequestParam(ID)String id){
        long loginId = securityBooster.requireUserId(request);
        return service.getTotalPagesOfOwn(loginId,id);
    }

    @GetMapping(PAGES_PATH+"/list")
    private MultipleItemsResult<PageNode> getPages(HttpServletRequest request
            , @RequestParam(PARENT_ID)String parentId
            , @RequestParam(BOOK_ID)String bookId
            ){
        long loginId = securityBooster.requireUserId(request);
        return service.getPages(loginId,bookId,parentId);
    }

    @GetMapping(PAGES_PATH)
    private PageNode getPageNode(HttpServletRequest request
            , @RequestParam(ID)String id){
        long loginId = securityBooster.requireUserId(request);
        return service.getPageNode(loginId,id);
    }



    @DeleteMapping(PAGES_PATH)
    private void deletePageNode(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        String parentId = param.getString(PARENT_ID);
        String bookId = param.getString(BOOK_ID);
        service.deletePageNode(loginId,bookId,parentId,id);
    }

    @DeleteMapping(BOOKS_PATH)
    private void deleteBook(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        service.deleteBook(loginId,id);
    }

    @GetMapping(BOOKS_PATH+"/list")
    private MultipleItemsResult<SharingBook> getBooks(HttpServletRequest request
    ,@RequestParam(STATES)List<Integer> states){
        long loginId = securityBooster.requireUserId(request);
        return service.getBooks(loginId,states);
    }

    @PostMapping(BOOKS_PATH+"/search")
    private SolrSearchResult<SharingBook> searchBooks(HttpServletRequest request
            , @RequestBody SolrSearchRequest searchRequest
    ){
        long loginId = securityBooster.requireUserId(request);
        return service.searchBooks(loginId,searchRequest);
    }

    @PostMapping(PAGES_PATH+"/search")
    private SolrSearchResult<PageNode> searchPageNodes(HttpServletRequest request
            , @RequestBody SolrSearchRequest searchRequest
    ){
        long loginId = securityBooster.requireUserId(request);
        return service.searchPageNodes(loginId,searchRequest);
    }

    @GetMapping(BOOKS_PATH)
    private SharingBook getBook(HttpServletRequest request
            ,@RequestParam(ID)String id){
        long loginId = securityBooster.requireUserId(request);
        return service.getBook(loginId,id);
    }



    @PostMapping(BOOKS_PATH+"/emptyBookPages")
    private void emptyBookPages(HttpServletRequest request
            , @RequestBody JSONObject param){
        String id = param.getString(ID);
        long loginId = securityBooster.requireUserId(request);
        service.emptyBookPages(loginId,id);
    }

    @PatchMapping(BOOKS_PATH)
    private void patchBook( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        Map<String,Object> updatingProps = JSON.parseObject(param.getString(JSON_OBJ));
        service.updateBookPropsInSync(loginId,id,updatingProps);
    }


    @PatchMapping(PAGES_PATH)
    private PageNode patchPageNode( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        String bookId = param.getString(BOOK_ID);
        Map<String,Object> updatingProps = JSON.parseObject(param.getString(JSON_OBJ));
        service.updatePageNodeProps(loginId,bookId,id,updatingProps);
        return service.getPageNode(loginId,id);
    }
    @PatchMapping(BOOKS_PATH+"/close")
    private void closeBook( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String id = param.getString(ID);
        service.closeBook(loginId,id);
    }
}
