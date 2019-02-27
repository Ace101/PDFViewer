package com.kdanmobile.pdfviewer.utils.threadpools;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param:
 * @author: luozhipeng
 * @Decreption: 线程池按以下行为执行任务
 * 1. 当线程数小于核心线程数时，创建线程。
 * 2. 当线程数大于等于核心线程数，且任务队列未满时，将任务放入任务队列。
 * 3. 当线程数大于等于核心线程数，且任务队列已满 - 若线程数小于最大线程数，创建线程 - 若线程数等于最大线程数，抛出异常，拒绝任务
 * @create at: 20/6/16 16:50
 **/
public class ThreadPoolUtils {
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /******  核心线程数
     核心线程会一直存活，及时没有任务需要执行
     当线程数小于核心线程数时，即使有线程空闲，线程池也会优先创建新线程处理
     设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭 ******/
    //private final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private final int CORE_POOL_SIZE = Math.max(1, CPU_COUNT - 1);

    /****** 最大线程数
     当线程数>=corePoolSize，且任务队列已满、
     。线程池会创建新线程来处理任务
     当线程数=maxPoolSize，且任务队列已满时，线程池会拒绝处理任务而抛出异常******/
    private final int MAX_POOL_SIZE = (CPU_COUNT * 2) + 1;

    /****** 线程空闲时间
     当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
     如果allowCoreThreadTimeout=true，则会直到线程数量=0******/
    private final int KEEP_ALIVE_TIME = 15;

    /******任务队列（阻塞队列）。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程******/
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(64);

    /****** 线程工厂 ******/
    private final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ThreadPoolUtils thread:" + integer.getAndIncrement());
        }
    };

    /******线程池******/
    public final ThreadPoolExecutor poolExecutor;

    private ThreadPoolUtils() {
        /****** 当线程池中的线程数量达到上限的时候,这个时候如果在添加线程的时候没有添加拒绝行为的时候会报出RejectExecutorException******/
        ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
                threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        mThreadPoolExecutor.allowCoreThreadTimeOut(true);
        this.poolExecutor = mThreadPoolExecutor;
    }

    private final static class SingleTon {
        private final static ThreadPoolUtils instance = new ThreadPoolUtils();
    }

    /**
     * The instance gets created only when it is called for first time. Lazy-loading
     */
    public static synchronized ThreadPoolUtils getInstance() {
        return SingleTon.instance;
    }

    /**
     * 在未来某个时间执行给定的命令
     * <p>该命令可能在新的线程、已入池的线程或者正调用的线程中执行，这由 Executor 实现决定。</p>
     *
     * @param command 命令
     */
    public void execute(final Runnable command) {
        poolExecutor.execute(command);
    }

    /**
     * 在未来某个时间执行给定的命令链表
     * <p>该命令可能在新的线程、已入池的线程或者正调用的线程中执行，这由 Executor 实现决定。</p>
     *
     * @param commands 命令链表
     */
    public void execute(final List<Runnable> commands) {
        for (Runnable command : commands) {
            poolExecutor.execute(command);
        }
    }

    /**
     * @param:
     * @author: luozhipeng
     * @Decreption: 移除任务
     * @create at: 20/6/16 16:49
     **/
    public void remove(Runnable task) {
        poolExecutor.remove(task);
    }

    /**
     * 提交一个Callable任务用于执行
     * <p>如果想立即阻塞任务的等待，则可以使用{@code result = exec.submit(aCallable).get();}形式的构造。</p>
     *
     * @param task 任务
     * @param <T>  泛型
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public <T> Future<T> submit(final Callable<T> task) {
        return poolExecutor.submit(task);
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task   任务
     * @param result 返回的结果
     * @param <T>    泛型
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public <T> Future<T> submit(final Runnable task, final T result) {
        return poolExecutor.submit(task, result);
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task 任务
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回null结果。
     */
    public Future<?> submit(final Runnable task) {
        return poolExecutor.submit(task);
    }

    /**
     * 执行给定的任务
     * <p>当所有任务完成时，返回保持任务状态和结果的Future列表。
     * 返回列表的所有元素的{@link Future#isDone}为{@code true}。
     * 注意，可以正常地或通过抛出异常来终止已完成任务。
     * 如果正在进行此操作时修改了给定的 collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks 任务集合
     * @param <T>   泛型
     * @return 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同，每个任务都已完成。
     * @throws InterruptedException 如果等待时发生中断，在这种情况下取消尚未完成的任务。
     */
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return poolExecutor.invokeAll(tasks);
    }

    /**
     * 执行给定的任务
     * <p>当所有任务完成或超时期满时(无论哪个首先发生)，返回保持任务状态和结果的Future列表。
     * 返回列表的所有元素的{@link Future#isDone}为{@code true}。
     * 一旦返回后，即取消尚未完成的任务。
     * 注意，可以正常地或通过抛出异常来终止已完成任务。
     * 如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks   任务集合
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同。如果操作未超时，则已完成所有任务。如果确实超时了，则某些任务尚未完成。
     * @throws InterruptedException 如果等待时发生中断，在这种情况下取消尚未完成的任务
     */
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws
            InterruptedException {
        return poolExecutor.invokeAll(tasks, timeout, unit);
    }

    /**
     * 执行给定的任务
     * <p>如果某个任务已成功完成（也就是未抛出异常），则返回其结果。
     * 一旦正常或异常返回后，则取消尚未完成的任务。
     * 如果此操作正在进行时修改了给定的collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks 任务集合
     * @param <T>   泛型
     * @return 某个任务返回的结果
     * @throws InterruptedException 如果等待时发生中断
     * @throws ExecutionException   如果没有任务成功完成
     */
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return poolExecutor.invokeAny(tasks);
    }

    /**
     * 执行给定的任务
     * <p>如果在给定的超时期满前某个任务已成功完成（也就是未抛出异常），则返回其结果。
     * 一旦正常或异常返回后，则取消尚未完成的任务。
     * 如果此操作正在进行时修改了给定的collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks   任务集合
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 某个任务返回的结果
     * @throws InterruptedException 如果等待时发生中断
     * @throws ExecutionException   如果没有任务成功完成
     * @throws TimeoutException     如果在所有任务成功完成之前给定的超时期满
     */
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws
            InterruptedException, ExecutionException, TimeoutException {
        return poolExecutor.invokeAny(tasks, timeout, unit);
    }

    /**
     * 待以前提交的任务执行完毕后关闭线程池
     * <p>启动一次顺序关闭，执行以前提交的任务，但不接受新任务。
     * 如果已经关闭，则调用没有作用。</p>
     */
    public void shutDown() {
        poolExecutor.shutdown();
    }

    /**
     * 试图停止所有正在执行的活动任务
     * <p>试图停止所有正在执行的活动任务，暂停处理正在等待的任务，并返回等待执行的任务列表。</p>
     * <p>无法保证能够停止正在处理的活动执行任务，但是会尽力尝试。</p>
     *
     * @return 等待执行的任务的列表
     */
    public List<Runnable> shutDownNow() {
        return poolExecutor.shutdownNow();
    }

    /**
     * 判断线程池是否已关闭
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isShutDown() {
        return poolExecutor.isShutdown();
    }

    /**
     * 关闭线程池后判断所有任务是否都已完成
     * <p>注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isTerminated() {
        return poolExecutor.isTerminated();
    }

    /**
     * 请求关闭、发生超时或者当前线程中断
     * <p>无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。</p>
     *
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @return {@code true}: 请求成功<br>{@code false}: 请求超时
     * @throws InterruptedException 终端异常
     */
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return poolExecutor.awaitTermination(timeout, unit);
    }
}