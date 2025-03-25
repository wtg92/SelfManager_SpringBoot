package manager.solr.data;

import manager.data.MultipleItemsResult;
import manager.solr.constants.SolrConfig;

import java.util.List;
import java.util.Map;

public class SolrSearchResult<T> extends MultipleItemsResult<T> {

    public static int pageSize = SolrConfig.SEARCH_PAGE_SIZE;

    public Map<String, Map<String, List<String>>> highlighting;


}
