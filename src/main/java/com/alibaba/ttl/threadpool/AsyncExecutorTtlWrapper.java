package com.alibaba.ttl.threadpool;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author taozhou
 * @date 2021/3/25
 */
public class AsyncExecutorTtlWrapper extends ExecutorTtlWrapper implements AsyncTaskExecutor,
    InitializingBean, DisposableBean {
    private final AsyncTaskExecutor executor;
    protected final boolean idempotent;

    AsyncExecutorTtlWrapper(@NonNull AsyncTaskExecutor executor, boolean idempotent) {
        super(executor, idempotent);
        this.executor = executor;
        this.idempotent = idempotent;
    }

    @Override
    public void destroy() throws Exception {
        if (executor instanceof DisposableBean) {
            DisposableBean bean = (DisposableBean) executor;
            bean.destroy();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean) executor;
            bean.afterPropertiesSet();
        }
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        executor.execute(getTtlRunnable(task), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(getTtlRunnable(task));
    }

    private TtlRunnable getTtlRunnable(Runnable task) {
        return TtlRunnable.get(task, false, idempotent);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(TtlCallable.get(task, false, idempotent));
    }
}
