package manager.service.books;

import manager.data.MultipleItemsResult;
import manager.solr.books.SharingLink;
import manager.solr.data.ParentNode;
import manager.solr.data.SolrSearchRequest;
import manager.solr.data.SolrSearchResult;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;

import java.util.List;
import java.util.Map;

public interface BooksService {
    abstract String createBook(long loginId, String name, String defaultLanguage, String comment);


    MultipleItemsResult<SharingBook> getBooks(long loginId,List<Integer> states);

    void updateBookPropsInSync(long loginId, String bookId, Map<String, Object> updatingAttrs);

    void updatePageNodeProps(long loginId,String bookId, String pageId, Map<String, Object> updatingAttrs);

    void updateLinkProps(long loginId,Boolean isCommunityLink, String linkId, Map<String, Object> updatingAttrs);


    void closeBook(long loginId, String id);

    SharingBook getBook(long loginId, String id);

    String createPage(long loginId, String bookId, String name, String lang, String parentId,  Double index);

    void addPageParentNode(long loginId, String id, String bookId, String parentId, Double index);


    PageNode getPageNode(long loginId, String id);

    MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId);

    void deletePageNode(long loginId, String bookId, String parentId, String id);

    void deleteBook(long loginId, String id);

    SolrSearchResult<SharingBook> searchBooks(long loginId, SolrSearchRequest searchRequest);

    SolrSearchResult<PageNode> searchPageNodes(long loginId, SolrSearchRequest searchRequest);


    List<ParentNode<?>> getAllParentNodes(long loginId, String id);

    List<PageNode> calculatePath(long loginId, String id);

    void copySinglePageNodeFromTheOwner(long loginId, String srcId, String bookId, String parentId, Double index);

    void movePageNodeAndSub(long loginId, String srcId, String srcBookId, String srcParentId, String targetBookId, String targetParentId, Double targetIndex);

    void copyPageNodeAndSubFromTheOwner(long loginId, String srcId, String srcBookId, String targetBookId, String targetParentId, Double targetIndex);

    long getTotalPagesOfOwn(long loginId, String bookId);

    void emptyBookPages(long loginId, String id);

    String createLink(long loginId, String name, String defaultLanguage, String bookId, Boolean isCommunityLink);

    MultipleItemsResult<SharingLink> getLinks(long loginId, String bookId, Boolean isCommunityLink);

    void deleteLink(long loginId, Boolean isCommunityLink, String id);

    SharingLink getLink(long loginId, Boolean isCommunityLink, String id);
}
