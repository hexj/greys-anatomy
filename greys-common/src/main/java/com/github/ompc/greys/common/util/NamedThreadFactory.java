package com.github.ompc.greys.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名线程工厂
 * Created by vlinux on 16/2/13.
 */
public class NamedThreadFactory implements ThreadFactory {

    // 线程名前缀
    private final String prefix;

    // 线程名编号
    private final AtomicInteger countRef = new AtomicInteger();

    // 是否守护线程
    private final boolean daemon;

    /**
     * 构造命名线程工厂
     *
     * @param prefix 线程名前缀
     */
    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    /**
     * 构造命名线程工厂
     *
     * @param prefix 线程名前缀
     * @param daemon 是否守护线程
     */
    public NamedThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        final Thread thread = new Thread(runnable, String.format("%s-%d", prefix, countRef.getAndIncrement()));
        thread.setDaemon(daemon);
        return thread;
    }

}