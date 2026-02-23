package manager.booster;

import manager.cache.CacheOperator;
import manager.exception.LogicException;
import manager.system.SelfXErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptGuard {

    @Value("${security.login.max-attempts.per-account}")
    private int maxAttemptsPerAccount;

    @Value("${security.login.max-attempts.per-ip}")
    private int maxAttemptsPerIp;

    @Value("${cache.login-attempt.expiration-of-min}")
    private Integer LOGIN_ATTEMPT_EXPIRATION_OF_MIN;

    private final CacheOperator cache;

    public LoginAttemptGuard(CacheOperator cache) {
        this.cache = cache;
    }

    /* ===================== public API ===================== */

    /** 登录前调用 */
    public void checkAllowed(String identifier, String ip) {
        LogicException exp = new LogicException(
                SelfXErrors.LOGIN_TOO_MANY_FAILED,
                LOGIN_ATTEMPT_EXPIRATION_OF_MIN
        );

        if (isAccountLimitExceeded(identifier)) {
            throw exp;
        }

        if (isIpLimitExceeded(ip)) {
            throw exp;
        }
    }

    /** 登录失败时调用 */
    public void recordFail(String account, String ip) {
        cache.incrLoginFailByAccount(account);
        cache.incrLoginFailByIp(ip);
    }

    /** 登录成功时调用 */
    public void clear(String account, String ip) {
        cache.clearLoginFailByAccount(account);
        cache.clearLoginFailByIp(ip);
    }

    /* ===================== internal ===================== */

    private boolean isAccountLimitExceeded(String account) {
        int count = cache.getLoginFailCountByAccount(account);
        return count >= maxAttemptsPerAccount;
    }

    private boolean isIpLimitExceeded(String ip) {
        int count = cache.getLoginFailCountByIp(ip);
        return count >= maxAttemptsPerIp;
    }
}