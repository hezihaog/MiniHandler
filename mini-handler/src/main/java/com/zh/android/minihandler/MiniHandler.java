package com.zh.android.minihandler;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class MiniHandler {
    private final Disruptor<Message> mDisruptor;

    public MiniHandler() {
        this(Looper.myLooper());
    }

    public MiniHandler(Looper looper) {
        //Looper没有绑定
        if (looper == null) {
            throw new RuntimeException("请先调用Looper.prepare()，创建Looper");
        }
        mDisruptor = looper.disruptor;
    }

    /**
     * 发送消息到消息队列中
     */
    public void sendMessage(Message message) {
        message.target = this;
        RingBuffer<Message> ringBuffer = mDisruptor.getRingBuffer();
        //请求下一个事件序号
        long sequence = ringBuffer.next();
        try {
            //获取该序号对应的事件对象
            Message event = ringBuffer.get(sequence);
            event.what = message.what;
            event.obj = message.obj;
            event.target = message.target;
        } finally {
            //发布事件
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 分发消息给Handler进行处理
     */
    void dispatchMessage(Message message) {
        handleMessage(message);
    }

    /**
     * 子类重写该方法进行处理消息
     */
    public void handleMessage(Message message) {
    }
}