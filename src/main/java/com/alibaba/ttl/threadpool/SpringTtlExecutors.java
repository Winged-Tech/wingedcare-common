package com.alibaba.ttl.threadpool;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.alibaba.ttl.threadpool.agent.TtlAgent;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * @author taozhou
 * @date 2021/3/25
 */
public class SpringTtlExecutors {
    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link Executor},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable}
     * to the execution time of {@link Runnable}.
     * <p>
     * NOTE: sine v2.12.0 the idempotency of return wrapped Executor is changed to true,
     * so the wrapped Executor can be cooperated with the usage of "Decorate Runnable and Callable".
     * <p>
     * About idempotency: if is idempotent,
     * it's allowed to submit the {@link com.alibaba.ttl.TtlRunnable}/{@link com.alibaba.ttl.TtlCallable} to the wrapped Executor;
     * otherwise throw {@link IllegalStateException}.
     *
     * @param executor input Executor
     * @return wrapped Executor
     * @see com.alibaba.ttl.TtlRunnable#get(Runnable, boolean, boolean)
     * @see com.alibaba.ttl.TtlCallable#get(Callable, boolean, boolean)
     */
    @Nullable
    public static Executor getAsyncTtlExecutor(@Nullable AsyncTaskExecutor executor) {
        if (TtlAgent.isTtlAgentLoaded() || null == executor || executor instanceof TtlEnhanced) {
            return executor;
        }
        return new AsyncExecutorTtlWrapper(executor, true);
    }
}
