package com.zh.android.minihandler;

public class Message {
    /**
     * 消息的标识
     */
    public int what;
    /**
     * 消息的附件
     */
    public Object obj;
    /**
     * 消息的处理器
     */
    public MiniHandler target;

    /**
     * 创建一个Message对象
     */
    public static Message obtain() {
        return new Message();
    }

    /**
     * 创建一个Message对象，并绑定处理它的Handler
     */
    public static Message obtain(MiniHandler handler) {
        Message message = obtain();
        message.target = handler;
        return message;
    }

    /**
     * 创建一个Message对象，并绑定处理它的Handler、消息标识what
     */
    public static Message obtain(MiniHandler handler, int what) {
        Message message = obtain();
        message.target = handler;
        message.what = what;
        return message;
    }

    /**
     * 创建一个Message对象，绑定消息标识what、消息附件obj
     */
    public static Message obtain(int what, Object obj) {
        Message message = obtain();
        message.what = what;
        message.obj = obj;
        return message;
    }

    /**
     * 创建一个Message对象，并绑定处理它的Handler、消息标识what、消息附件obj
     */
    public static Message obtain(MiniHandler handler, int what, Object obj) {
        Message message = obtain();
        message.target = handler;
        message.what = what;
        message.obj = obj;
        return message;
    }
}