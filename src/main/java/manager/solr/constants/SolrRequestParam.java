package manager.solr.constants;

public class SolrRequestParam {
    public static String PROCESSOR = "processor";
    public static String UPDATE_CHAIN = "update.chain";




    /**
     * QUERY Params
     */
    public static String RELEVANT_QUERY = "q";
    public static String PURE_QUERY = "fq";
    public static String QUERY_ASC = "asc";

    public static String QUERY_DESC = "desc";
    
    public static String QUERY_FIELDS = "fl";
    public static String QUERY_START = "start";
    public static String QUERY_SORT = "sort";

    public static String QUERY_LIMIT = "rows";

    /**
     * TODO 空闲的时候 加上 因为有一系列的前台UI 需要处理
     */
    public static String QUERY_TIME_ALLOWED = "timeAllowed";


    public static String SET = "set";

}
