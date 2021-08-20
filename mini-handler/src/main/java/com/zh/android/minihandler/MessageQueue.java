package com.zh.android.minihandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageQueue {
    /**
     * 是否退出
     */
    volatile boolean isQuit;
    /**
     * 消息队列
     */
    private final BlockingQueue<Message> mMessageQueue = new ArrayBlockingQueue<Message>(50);

    /**
     * 消息入队
     */
    public void enqueueMessage(Message message) {
        try {
            mMessageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拿出一条消息
     */
    public Message next() {
        try {
            //如果队列中没有消息，就会阻塞在这里
            return mMessageQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void quit() {
        mMessageQueue.clear();
        isQuit = true;
    }
}