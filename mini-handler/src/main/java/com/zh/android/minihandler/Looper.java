package com.zh.android.minihandler;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
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
        disruptor = new Disruptor<>(messageFactory, ringBufferSize, executor, ProducerType.SINGLE,
                new YieldingWaitStrategy());
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
        Looper looper = myLooper();
        Disruptor<Message> disruptor = looper.disruptor;
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