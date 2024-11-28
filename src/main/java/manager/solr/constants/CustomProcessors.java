package manager.solr.constants;

public class CustomProcessors {

    /**
     * 自动生成ID 两个配合使用
     * 似乎是由于固有BUG 导致需要IGNORE_ID 来取消unique校验
     */
    public static String AUTO_GENERATE_ID = "auto_generate_id";
    public static String IGNORE_ID = "ignore_id";

}
