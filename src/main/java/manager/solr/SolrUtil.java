package manager.solr;

import manager.exception.DBException;
import manager.solr.constants.CustomProcessors;
import manager.solr.data.SolrSearchRequest;
import manager.system.SelfXErrors;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SolrUtil {

    /**
     * Bad Design Of SolrJ causes this.
     */
    public static DocumentObjectBinder binder = new DocumentObjectBinder();

    public static String getMultipleFieldParam(String ...params){
        return String.join(",", params);
    }

    public static String getAutoGenerateIdConfig(){
        return getMultipleFieldParam(CustomProcessors.IGNORE_ID, CustomProcessors.AUTO_GENERATE_ID);
    }

    public static String buildQueryString(Map<String, Object> fieldsFilter) {
        if (fieldsFilter == null || fieldsFilter.isEmpty()) {
            return "*:*"; // 没有过滤条件时返回全部查询
        }

        return fieldsFilter.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof String) {
                        return key + ":\"" + escapeSpecialChars((String) value) + "\"";
                    } else if (value instanceof Number || value instanceof Boolean) {
                        return key + ":" + value;
                    } else if (value instanceof Range<?> range) {
                        return key + ":[" + range.getStart() + " TO " + range.getEnd() + "]";
                    } else{
                        throw new RuntimeException("BLOCKED");
                    }
                })
                .collect(Collectors.joining(" AND ")); // 使用 AND 拼接
    }

    // 处理 Solr 特殊字符转义
    private static String escapeSpecialChars(String value) {
        return value.replaceAll("([+\\-!(){}\\[\\]^\"~*?:\\\\/])", "\\\\$1");
    }
    private static void detectDataTooLongExceptionAndThrowIfAny(Exception e) throws DBException {
        if(e.getMessage().contains("boost must be a positive float")){
            throw new DBException(SelfXErrors.SOLR_SEARCH_PARAMS_Error);
        }

        if(e.getMessage().contains("too large")) {
            throw new DBException(SelfXErrors.DATA_TOO_LONG);
        }
        if(e.getMessage().contains("URI Too Long")) {
            throw new DBException(SelfXErrors.SOLR_SEARCH_URI_TOO_LANG);
        }
        if(e.getMessage().contains("org.apache.solr.search.SyntaxError")){
            throw new DBException(SelfXErrors.SOLR_SEARCH_Syntax_Error);
        }
    }
    public static DBException processSolrException(Exception e){
        e.printStackTrace();
        try {
            detectDataTooLongExceptionAndThrowIfAny(e);
        }catch(DBException dataTooLong) {
            assert dataTooLong.type == SelfXErrors.DATA_TOO_LONG : dataTooLong.type;
            return dataTooLong;
        }
        return new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
    }

    public static<T>  String buildCollectionsQuery(String fieldName, List<T> states){
        return buildCollectionsQuery(fieldName,states, Objects::toString);
    }

    public static<T>  String buildCollectionsQuery(String fieldName, List<T> states, Function<T,String> mapper){
        return states.stream()
                .map(mapper)
                .collect(Collectors.joining(" OR ", fieldName + ":(", ")"));
    }


    // 内部类处理范围查询
    public static class Range<T> {
        private final T start;
        private final T end;

        public Range(T start, T end) {
            this.start = start;
            this.end = end;
        }

        public T getStart() {
            return start;
        }

        public T getEnd() {
            return end;
        }
    }



    public static String buildLuceneSearchQuery(List<String> fieldNames, SolrSearchRequest request ) {
        final String searchInfo = request.searchInfo;
        if (fieldNames == null || fieldNames.isEmpty() || searchInfo == null || searchInfo.isEmpty()) {
            throw new RuntimeException("Why Call This?");
        }

        StringBuilder queryBuilder = new StringBuilder("(");

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            /**
             * 字段级别 一定是OR的关系
             * 如 书本 我会既查它的名字 也 查它的内容
             */
            if (i > 0) {
                queryBuilder.append(" OR ");
            }
            queryBuilder.append("(");
            queryBuilder.append(fieldName).append(":(").append(searchInfo).append(")");
            queryBuilder.append(")");
        }
        queryBuilder.append(")");
        System.out.println(queryBuilder.toString());
        return queryBuilder.toString();
    }
}
