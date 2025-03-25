package manager.solr;

import manager.exception.DBException;
import manager.solr.constants.CustomProcessors;
import manager.system.SelfXErrors;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SolrUtil {

    /**
     * Bad Design Of SolrJ causes this.
     */
    public static DocumentObjectBinder binder = new DocumentObjectBinder();

    public static String getMultipleFieldParam(String ...params){
        return String.join(",", params);
    }

    public static String getMultipleFieldParam(Collection<String> params){
        return getMultipleFieldParam(params.toArray(new String[0]));
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
        if(e.getMessage().contains("too large")) {
            throw new DBException(SelfXErrors.DATA_TOO_LONG);
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

    public static String buildSearchQuery(List<String> fieldNames, String searchInfo) {
        if (fieldNames == null || fieldNames.isEmpty() || searchInfo == null || searchInfo.isEmpty()) {
            throw new RuntimeException("Why Call This?");
        }

        StringBuilder queryBuilder = new StringBuilder("(");
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            queryBuilder.append(fieldName).append(":").append(searchInfo).append("~");
            if (i < fieldNames.size() - 1) {
                queryBuilder.append(" OR ");
            }
        }
        queryBuilder.append(")");
        return queryBuilder.toString();
    }
}
