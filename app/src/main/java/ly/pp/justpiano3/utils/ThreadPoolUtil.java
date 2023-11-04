package ly.pp.justpiano3.utils;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {

    /**
     * 线程池核心线程数
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * 线程池最大线程数
     */
    private static final int MAX_POOL_SIZE = 100;

    /*
     * 额外线程空状态生存时间
     */
    private static final int KEEP_ALIVE_TIME = 10000;

    /**
     * 阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程
     */
    private static final BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(10);

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger atomicInteger = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ThreadPool thread:" + atomicInteger.getAndIncrement());
        }
    };

    /**
     * 拒绝策略
     */
    private static class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Log.e("ThreadPoolUtil", "Task rejected: " + r.toString());
        }
    }

    /**
     * 线程池
     */
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, WORK_QUEUE, THREAD_FACTORY, new LoggingRejectedExecutionHandler());

    /**
     * 从线程池中抽取线程，执行指定的Runnable对象
     *
     * @param runnable 线程
     */
    public static Future<?> execute(Runnable runnable) {
        if (runnable != null) {
            return THREAD_POOL_EXECUTOR.submit(runnable);
        }
        return null;
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        if (callable != null) {
            return THREAD_POOL_EXECUTOR.submit(callable);
        }
        return null;
    }

    /**
     * 线程池执行指定的Runnable对象
     *
     * @param runnable 执行内容
     * @param delay    延迟执行的毫秒数
     */
    public static Future<?> executeWithDelay(Runnable runnable, long delay) {
        if (runnable != null) {
            return THREAD_POOL_EXECUTOR.submit(() -> {
                try {
                    Thread.sleep(delay);
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return null;
    }
}
