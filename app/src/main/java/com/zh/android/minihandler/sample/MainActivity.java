package com.zh.android.minihandler.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zh.android.minihandler.Message;
import com.zh.android.minihandler.MiniHandler;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MiniHandler";

    /**
     * Toast任务
     */
    public static final int ACTION_TOAST = 1;
    /**
     * 延时任务
     */
    public static final int ACTION_DELAY = 2;

    private Button vSendMsgToEventThread;
    private Button vSendDelayMsg;
    private TextView vCurrentTime;

    private MiniHandler mEventHandler;
    private MiniHandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startEventLoop();
        findView();
        bindView();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
    }

    private void startEventLoop() {
        //事件处理线程
        if (mHandlerThread == null) {
            mHandlerThread = new MiniHandlerThread("handler-thread");
            mHandlerThread.start();
        }
        if (mEventHandler == null) {
            mEventHandler = new MiniHandler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    long action = message.what;
                    if (action == ACTION_TOAST) {
                        String msg = message.obj.toString();
                        ToastUtil.toast(getApplicationContext(), msg);
                    } else if (action == ACTION_DELAY) {
                        String delayMsg = message.obj.toString();
                        ToastUtil.toast(getApplicationContext(), delayMsg);
                    }
                }
            };
        }
    }

    private void findView() {
        vSendMsgToEventThread = findViewById(R.id.send_msg_to_event_thread);
        vSendDelayMsg = findViewById(R.id.send_delay_msg);
        vCurrentTime = findViewById(R.id.current_time);
    }

    private void bindView() {
        vSendMsgToEventThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息到事件线程
                mEventHandler.sendMessage(Message.obtain(ACTION_TOAST, "Toast~"));
            }
        });
        vSendDelayMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventHandler.sendMessageDelayed(Message.obtain(ACTION_DELAY, "延时消息~"), 2000);
            }
        });
    }

    /**
     * 开启定时器
     */
    private void startTimer() {
        mEventHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStr = format.format(System.currentTimeMillis());
                Log.d(TAG, "当前时间：" + timeStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vCurrentTime.setText(timeStr);
                    }
                });
                mEventHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}