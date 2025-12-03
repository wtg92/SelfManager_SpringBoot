package manager.cache;


import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import manager.booster.longRunningTasks.LongRunningTasksMessage;
import manager.entity.general.FileRecord;
import manager.entity.general.User;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanBalance;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.entity.general.career.WorkSheet;
import manager.solr.books.SharingLink;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 为了可以在一个地方配置所有缓存参数而设计该类
 */
@Component
public class CaffeineCollection {

    @Value("${cache.common.max-size-in-m}")
    private Long COMMON_MAX_SIZE_IN_M;
    @Value("${cache.temp.max-size-in-m}")
    private Long COMMON_MAX_SIZE_IN_M_FOR_TEMP;

    @Value("${cache.perms.max-num}")
    private Integer PERMS_MAX_NUM;

    @Value("${cache.users.max-num}")
    private Integer USERS_MAX_NUM;

    @Value("${cache.plans.max-num}")
    private Integer PLANS_MAX_NUM;
    @Value("${cache.plan-balance.max-num}")
    private Integer PLAN_BALANCE_MAX_NUM;
    @Value("${cache.common.expiration-of-min}")
    public Integer COMMON_EXPIRATION_OF_MIN;
    @Value("${cache.long-running-tasks.expiration-of-min}")
    public Integer LONG_RUNNING_TASKS_EXPIRATION_OF_MIN;

    @Value("${cache.temp-users.max-num}")
    private Integer TEMP_USERS_MAX_NUM;

    @Value("${cache.worksheets.max-size-in-m}")
    private Long WORKSHEETS_MAX_SIZE_IN_M;

    @Value("${cache.temp.expiration-of-min}")
    private Integer TEMP_EXPIRATION_OF_MIN;

    @Value("${cache.books.max-size-in-m}")
    private Long BOOKS_MAX_SIZE_IN_M;

    @Value("${cache.links.max-size-in-m}")
    private Long LINkS_MAX_SIZE_IN_M;

    @Value("${cache.page-nodes.max-size-in-m}")
    private Long PAGE_NODES_MAX_SIZE_IN_M;
    @Value("${cache.file-records.max-num}")
    private Integer FILE_RECORDS_MAX_NUM;

    @Value("${cache.closed-book-ids.max-num}")
    private Integer CLOSED_BOOK_IDS_MAX_NUM;

    @Value("${cache.long-running-tasks.max-num}")
    private Integer LONG_RUNNING_TASKS_MAX_NUM;
    @PostConstruct
    public void init() {
        Common_Cache = generateCommonCache();
        Common_Temp_Cache = generateCommonTempCache();

        Perms_Cache = generateEnumCache(PERMS_MAX_NUM);
        Users_Cache = generateSpecificEntityCache(USERS_MAX_NUM,COMMON_EXPIRATION_OF_MIN);
        Plans_Cache = generateSpecificEntityCache(PLANS_MAX_NUM,COMMON_EXPIRATION_OF_MIN);
        PlanBalance_Cache = generateSpecificEntityCache(PLAN_BALANCE_MAX_NUM,COMMON_EXPIRATION_OF_MIN);
        Worksheet_Cache = createCacheByMaxSizeInM(WORKSHEETS_MAX_SIZE_IN_M,COMMON_EXPIRATION_OF_MIN);
        Temp_Users_Cache = generateSpecificEntityCache(TEMP_USERS_MAX_NUM, TEMP_EXPIRATION_OF_MIN);
        Books_Cache = createCacheByMaxSizeInM(BOOKS_MAX_SIZE_IN_M,COMMON_EXPIRATION_OF_MIN);
        Links_Cache = createCacheByMaxSizeInM(LINkS_MAX_SIZE_IN_M,COMMON_EXPIRATION_OF_MIN);
        Page_Nodes_Cache = createCacheByMaxSizeInM(PAGE_NODES_MAX_SIZE_IN_M,COMMON_EXPIRATION_OF_MIN);
        File_Records_Cache = generateSpecificEntityCache(FILE_RECORDS_MAX_NUM,COMMON_EXPIRATION_OF_MIN);
        Closed_Book_Ids_Cache = generateSpecificEntityCache(CLOSED_BOOK_IDS_MAX_NUM,COMMON_EXPIRATION_OF_MIN);
        Long_Running_Tasks_Cache = generateSpecificEntityCache(LONG_RUNNING_TASKS_MAX_NUM,LONG_RUNNING_TASKS_EXPIRATION_OF_MIN);
    }

    private Cache<String, String> generateCommonTempCache() {
        return Caffeine.newBuilder()
                .maximumWeight(COMMON_MAX_SIZE_IN_M_FOR_TEMP * 1024 * 1024)
                .weigher((String key, String value) -> {
                    // 计算每个条目的权重，假设每个条目的权重是其占用的内存大小（字节）
                    return estimateStringMemoryUsage(value);
                })
                .expireAfterAccess(Duration.ofMinutes(TEMP_EXPIRATION_OF_MIN))
                .build();
    }

    public Cache<String, String>  Common_Cache;

    public Cache<String, String> Common_Temp_Cache;

    public Cache<Long, Set<Integer>> Perms_Cache;

    public Cache<String, Map<String,String>> Temp_Users_Cache;

    public Cache<Long, User> Users_Cache;

    public Cache<Long, Plan> Plans_Cache;

    public Cache<Long, WorkSheet> Worksheet_Cache;

    //KEY : USER_ID
    public Cache<Long, PlanBalance> PlanBalance_Cache;

    public Cache<Long, FileRecord> File_Records_Cache;

    public Cache<Long, List<String>> Closed_Book_Ids_Cache;

    public Cache<String, SharingBook> Books_Cache;

    public Cache<String, PageNode> Page_Nodes_Cache;

    public Cache<String, SharingLink> Links_Cache;

    public Cache<Long, LongRunningTasksMessage> Long_Running_Tasks_Cache;



    private <T,V> Cache<V, T> generateSpecificEntityCache(int maxSize,int expirationMin) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(Duration.ofMinutes(expirationMin))
                .build();
    }




    private<T> Cache<Long, Set<T>> generateEnumCache(int maxSize) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(Duration.ofMinutes(COMMON_EXPIRATION_OF_MIN))
                .build();
    }

    private <T,V> Cache<V, T> createCacheByMaxSizeInM(long maxSizeInM,int expirationMin) {
        return Caffeine.newBuilder()
                .maximumWeight(maxSizeInM * 1024 * 1024)
                .weigher((V key, T value) -> {
                    // 计算每个条目的权重，假设每个条目的权重是其占用的内存大小（字节）
                    return estimateStringMemoryUsage(value);
                })
                .expireAfterAccess(Duration.ofMinutes(expirationMin))
                .build();
    }

    private Cache<String, String>  generateCommonCache() {
        return Caffeine.newBuilder()
                .maximumWeight(COMMON_MAX_SIZE_IN_M * 1024 * 1024)
                .weigher((String key, String value) -> {
                    // 计算每个条目的权重，假设每个条目的权重是其占用的内存大小（字节）
                    return estimateStringMemoryUsage(value);
                })
                .expireAfterAccess(Duration.ofMinutes(COMMON_EXPIRATION_OF_MIN))
                .build();
    }

    public static int estimateStringMemoryUsage(Object obj) {
        return estimateStringMemoryUsage(JSON.toJSONString(obj));
    }

    /**
     * 估算字符串占用的内存大小
     * @param str 要估算的字符串
     * @return 估算的内存大小（字节）
     */
    public static int estimateStringMemoryUsage(String str) {
        if (str == null) return 0;

        final int STRING_OBJECT_OVERHEAD = 24;
        final int ARRAY_OBJECT_OVERHEAD = 16;

        boolean isAscii = true;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 255) {
                isAscii = false;
                break;
            }
        }

        int perChar = isAscii ? 1 : 2;

        return STRING_OBJECT_OVERHEAD + ARRAY_OBJECT_OVERHEAD + (perChar * str.length());
    }


}
