package manager.system.auth;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlipayConfig {

    @Value("${alipay.app_id}")
    public String APP_ID;
    @Value("${alipay.app_private_key}")
    public String APP_PRIVATE_KEY;
    @Value("${alipay.app_public_key}")
    public String ALIPAY_PUBLIC_KEY;
    public static final String CHARSET = "UTF-8";
    public static final String SIGN_TYPE = "RSA2";
    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    public static final String RETURN_URL = "your_return_url";

    public AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    }
}
