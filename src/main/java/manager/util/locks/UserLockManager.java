package manager.util.locks;
import manager.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class UserLockManager {

    private static final Logger log = LoggerFactory.getLogger(UserLockManager.class);

    private ConcurrentHashMap<String, LockProxy> userLocks = new ConcurrentHashMap<>();

    @Value("${lock.expirationSeconds}")
    private Integer expirationSeconds;

    private final static String Default_User_Event = "default";

    private static class LockProxy{
        long activatedMills;
        Lock lock;
    }

    @Scheduled(cron = "${lock.expirationCheckCron}")
    public void clearExpiredLockAutomatically(){
        boolean someRemoved = userLocks.entrySet()
                .removeIf(entry -> (System.currentTimeMillis() - entry.getValue().activatedMills) > expirationSeconds);

        if(someRemoved){
            log.info("Some User Locker Cleaned Automatically,which means it works!");
        }
    }

    public void lockByUserAndEvent(long userId,String event,Runnable run) {
        String userKey = LockKeyGenerator.generateUserKey(userId,event);
        LockProxy userLock = userLocks.computeIfAbsent(userKey, key -> {
            LockProxy lock = new LockProxy();
            lock.lock = new ReentrantLock();
            lock.activatedMills = System.currentTimeMillis();
            return lock;
        });
        userLock.lock.lock();
        userLock.activatedMills = System.currentTimeMillis();
        try {
            run.run();
        } finally {
            userLock.lock.unlock();
        }
    }

    public void lockByUserAndEvent(long userId,Runnable run) {
        String eventName = ReflectUtil.getInvokerMethodName();
        lockByUserAndEvent(userId,eventName,run);
    }

    public void lockByUser(long userId,Runnable run) {
        lockByUserAndEvent(userId,Default_User_Event,run);
    }

}
