package manager.solr;

import manager.solr.constants.CustomProcessors;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;

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
}
