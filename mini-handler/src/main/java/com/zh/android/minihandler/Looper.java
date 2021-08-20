package com.zh.android.minihandler;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;

public class Looper implements EventHandler<Message> {
    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    final Disruptor<Message> disruptor;

    /**
     * 一个线程，只有一个Looper，一个Looper也只有一个消息队列
     */
    private Looper() {
        //RingBuffer 大小，必须是 2 的 N 次方；
        int ringBufferSize = 1024 * 1024;
        //事件工厂
        MessageFactory messageFactory = new MessageFactory();
        //用于事件处理的执行器
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
        //等待策略
        //BusySpinWaitStrategy：自旋等待，类似Linux Kernel使用的自旋锁。低延迟但同时对CPU资源的占用也多。
        //BlockingWaitStrategy：使用锁和条件变量。CPU资源的占用少，延迟大。
        //SleepingWaitStrategy：在多次循环尝试不成功后，选择让出CPU，等待下次调度，多次调度后仍不成功，尝试前睡眠一个纳秒级别的时间再尝试。这种策略平衡了延迟和CPU资源占用，但延迟不均匀。
        //YieldingWaitStrategy：在多次循环尝试不成功后，选择让出CPU，等待下次调。平衡了延迟和CPU资源占用，但延迟也比较均匀。
        //PhasedBackoffWaitStrategy ： 上面多种策略的综合，CPU资源的占用少，延迟大。
        WaitStrategy waitStrategy = new BlockingWaitStrategy();
        disruptor = new Disruptor<>(messageFactory, ringBufferSize,
                executor, ProducerType.SINGLE, waitStrategy);
        disruptor.handleEventsWith(this);
    }

    /**
     * 事件工厂
     */
    private static class MessageFactory implements EventFactory<Message> {
        @Override
        public Message newInstance() {
            return Message.obtain();
        }
    }

    /**
     * 把当前线程和Looper进行绑定
     */
    public static void prepare() {
        Looper looper = sThreadLocal.get();
        if (looper != null) {
            throw new RuntimeException("一个线程只能绑定一个Looper，请确保prepare方法在一个线程中只调用一次");
        }
        sThreadLocal.set(new Looper());
    }

    /**
     * 获取当前线程的Looper
     */
    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    /**
     * 开始循环从队列中取出消息
     */
    public static void loop() {
        //获取当前线程的轮询器
        Looper looper = myLooper();
        Disruptor<Message> disruptor = looper.disruptor;
        disruptor.start();
    }

    /**
     * 安全退出，会等所有事件都执行完，再关闭
     */
    public void quitSafely() {
        disruptor.shutdown();
    }

    @Override
    public void onEvent(Message event, long sequence, boolean endOfBatch) throws Exception {
        //分发消息到Handler
        event.target.dispatchMessage(event);
        //回收Message对象
        event.recycleUnchecked();
    }
}