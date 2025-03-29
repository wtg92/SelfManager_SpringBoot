package manager.service.books;

import manager.data.MultipleItemsResult;
import manager.solr.data.SolrSearchResult;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;

import java.util.List;
import java.util.Map;

public interface BooksService {
    abstract void createBook(long loginId, String name, String defaultLanguage, String comment);


    MultipleItemsResult<SharingBook> getBooks(long loginId, Integer state);

    void updateBookPropsSyncly(long loginId, String bookId, Map<String, Object> updatingAttrs);

    void updatePageNodePropsSyncly(long loginId, String bookId, Map<String, Object> updatingAttrs);

    /**
     * 到时候将有2点
     * 1.移动节点   ---> 只有移动会存在环的问题 我定一个规则 --- 移动 只能移动到根节点上？
     * 2.复制节点引用 ---> 不存在环的问题 （因为是新加的）
     * 这俩其实是一致的 就是把一个节点 加入到另一个节点上 是否可以
     * 我要确保它没有
     * <p>
     * <p>
     * 移动节点的思索
     * 1.我为何会害怕出现环？
     * 因为我担心!服务器!会无限循环
     * 查：单层不可能无限循环 --->不会影响到我的服务器
     * 检索到 找到一条到根节点的路径 -----> 我可以中间用一个缓存 假设遇到了已经走过的 我就不会再走
     * 删： 根据已有的逻辑 就算是环 也不会出现无限循环
     * 会导致无用的数据删不掉吗？
     * 改：
     */
    void changeNode(long loginId, String nodeId, List<String> parentId, List<Double> indexes);

    void closeBook(long loginId, String id);

    SharingBook getBook(long loginId, String id);

    void createPage(long loginId, String bookId, String name, String lang, String parentId, Boolean isRoot, Double index);

    PageNode getPageNode(long loginId, String id);

    MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId, Boolean isRoot);

    void deletePageNode(long loginId, String bookId, String parentId, Boolean isRoot, String id);

    void deleteBook(long loginId, String id);

    SolrSearchResult<SharingBook> searchBooks(long loginId, String searchInfo, Integer pageNum, Boolean searchAllVersions,
                                              List<String> searchVersions, Integer fragSize);
}
