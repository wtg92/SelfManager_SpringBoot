package manager.booster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * TODO 异步任务里出现了错误怎么办？ 应当利用
 *  synchronized ---> 由于是异步任务 所以都synchronized 没关系
 */
@Component
public class LongRunningTasksScheduler {
    @Value("${long-running-tasks.max-num}")
    private Integer MAX_NUM_OF_LONG_RUNNING_TASKS;
    private final Map<Long, Thread> taskMap = new ConcurrentHashMap<>();

    /*
     * TODO 弄一个长耗时机制 使用caffine
     */

    public synchronized String runAsyncTask(Long userId, Runnable task) {
        if (taskMap.containsKey(userId)) {
            Thread existing = taskMap.get(userId);
            if (existing.isAlive()) {
                return "Task already running for user: " + userId;
            } else {
                taskMap.remove(userId); // 清除僵尸线程引用
            }
        }

        // 包装任务，确保任务完成后清除引用
        Runnable wrappedTask = () -> {
            try {
                task.run();
            } catch (Throwable t) {
                System.err.println("Task for user " + userId + " failed: " + t.getMessage());
            } finally {
                taskMap.remove(userId); // 无论成功或失败都自动清理
            }
        };

        Thread thread = Thread.ofVirtual().start(wrappedTask);
        taskMap.put(userId, thread);

        return "Task started for user: " + userId;
    }

    public synchronized String cancelTask(Long userId) {
        Thread thread = taskMap.get(userId);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            return "Task cancelled for user: " + userId;
        }
        return "No running task found for user: " + userId;
    }

    public boolean isTaskRunning(Long userId) {
        Thread thread = taskMap.get(userId);
        return thread != null && thread.isAlive();
    }
}
