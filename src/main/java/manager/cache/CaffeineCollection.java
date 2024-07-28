package manager.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.instrument.Instrumentation;
import java.time.Duration;

@Component
public class CaffeineCollection {

    @Value("cache.common.max-size-in-m")
    private Long COMMON_MAX_SIZE_OF_M;

    @Value("cache.common.expiration-of-min")
    private Integer COMMON_EXPIRATION_OF_MIN;

    final public Cache<String, String>  Common_Cache = generateCommonCache();

    private Cache<String, String>  generateCommonCache() {
        return Caffeine.newBuilder()
                .maximumWeight(COMMON_MAX_SIZE_OF_M * 1024 * 1024)
                .weigher((String key, String value) -> {
                    // 计算每个条目的权重，假设每个条目的权重是其占用的内存大小（字节）
                    return estimateStringMemoryUsage(value);
                })
                .expireAfterWrite(Duration.ofMinutes(COMMON_EXPIRATION_OF_MIN))
                .refreshAfterWrite(Duration.ofMinutes(COMMON_EXPIRATION_OF_MIN))
                .build();
    }



    /**
     * 估算字符串占用的内存大小
     * @param str 要估算的字符串
     * @return 估算的内存大小（字节）
     */
    public static int estimateStringMemoryUsage(String str) {
        if (str == null) {
            return 0;
        }

        final int STRING_OBJECT_OVERHEAD = 24;
        final int ARRAY_OBJECT_OVERHEAD = 16;
        final int CHAR_SIZE = 2;

        int length = str.length();


        return STRING_OBJECT_OVERHEAD + ARRAY_OBJECT_OVERHEAD + (CHAR_SIZE * length);
    }


}
