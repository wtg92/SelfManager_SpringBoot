package manager.data.general;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class FinalIntegerTempStorageCalculator {

    public int initialHandler = 0;
    public String generatingPrefix = "common";

    public Map<String,Object> values = new HashMap<>();

    public FinalIntegerTempStorageCalculator(int initialHandler, String generatingPrefix) {
        this.initialHandler = initialHandler;
        this.generatingPrefix = generatingPrefix;
    }

    public void consumerValues(BiConsumer<String,Object> consumer){
        values.forEach(consumer);
    }

    public String getAndIncrementHandlerAndStorage(Object val) {
        final String generatorKey = generatingPrefix+(initialHandler++);
        values.put(generatorKey,val);
        return generatorKey;
    }
}
