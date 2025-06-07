package manager.booster.longRunningTasks;

import manager.cache.CacheOperator;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.system.SelfXErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/*
 * TODO 异步任务里出现了错误怎么办？ 应当利用
 *  synchronized ---> 由于是异步任务 所以都synchronized 没关系
 */
@Component
public class LongRunningTasksScheduler {
    @Value("${long-running-tasks.max-num}")
    private Integer MAX_NUM_OF_LONG_RUNNING_TASKS;

    @Resource
    private CacheOperator cacheOperator;

    private final Map<Long, Thread> taskMap = new ConcurrentHashMap<>();

    public synchronized void runAsyncTask(Long userId, Runnable task, Integer type,Object ...messageParams) {
        if(taskMap.size() > MAX_NUM_OF_LONG_RUNNING_TASKS){
            throw new LogicException(SelfXErrors.MAX_LONG_RUNNING_TASKS_LIMIT_REACHED);
        }
        if (taskMap.containsKey(userId)) {
            Thread existing = taskMap.get(userId);
            if (existing.isAlive()) {
                throw new LogicException(SelfXErrors.RUNNING_TASK_ALREADY_EXISTED_FOR_THE_USER);
            } else {
                taskMap.remove(userId); // 清除僵尸线程引用
            }
        }

        LongRunningTasksMessage msg = new LongRunningTasksMessage();
        msg.status = LongRunningTasksStatus.RUNNING;
        msg.params = messageParams;
        msg.type = type;
        msg.startUtc = System.currentTimeMillis();

        cacheOperator.pushLongRunningMessage(userId,msg);

        Runnable wrappedTask = () -> {
            try {
                task.run();
                msg.status = LongRunningTasksStatus.SUCCESS;
                msg.endUtc = System.currentTimeMillis();
                cacheOperator.pushLongRunningMessage(userId,msg);
            } catch (SMException t) {
                msg.status = LongRunningTasksStatus.FAIL;
                msg.error = t;
                msg.endUtc = System.currentTimeMillis();
                cacheOperator.pushLongRunningMessage(userId,msg);
            } catch (Throwable e){
                msg.status = LongRunningTasksStatus.FAIL;
                msg.error = new LogicException(SelfXErrors.UNEXPECTED_ERROR) ;
                msg.endUtc = System.currentTimeMillis();
                cacheOperator.pushLongRunningMessage(userId,msg);
            } finally {
                taskMap.remove(userId); // 无论成功或失败都自动清理
            }
        };

        Thread thread = Thread.ofVirtual().start(wrappedTask);
        taskMap.put(userId, thread);
    }

    public synchronized void cancelTask(Long userId) {
        Thread thread = taskMap.get(userId);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    public boolean isTaskRunning(Long userId) {
        Thread thread = taskMap.get(userId);
        return thread != null && thread.isAlive();
    }

    public LongRunningTasksMessage getRunningMsg(Long userId){
        LongRunningTasksMessage longRunningMessage = cacheOperator.getLongRunningMessage(userId);
        return longRunningMessage  == null ? new LongRunningTasksMessage()  : longRunningMessage;
    }
}
