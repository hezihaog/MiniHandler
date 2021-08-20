package com.zh.android.minihandler;

public class MiniHandler {
    /**
     * 消息队列
     */
    private final MessageQueue mMessageQueue;

    public MiniHandler() {
        this(Looper.myLooper());
    }

    public MiniHandler(Looper looper) {
        //Looper没有绑定
        if (looper == null) {
            throw new RuntimeException("请先调用Looper.prepare()，创建Looper");
        }
        mMessageQueue = looper.mMessageQueue;
    }

    /**
     * 发送消息到消息队列中
     */
    public void sendMessage(Message message) {
        message.target = this;
        mMessageQueue.enqueueMessage(message);
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