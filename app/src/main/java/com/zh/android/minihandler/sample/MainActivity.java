package com.zh.android.minihandler.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zh.android.minihandler.Message;
import com.zh.android.minihandler.MiniHandler;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MiniHandler";

    /**
     * Toast任务
     */
    public static final int ACTION_TOAST = 1;
    /**
     * 倒计时
     */
    public static final int ACTION_COUNT_DOWN = 2;

    private Button vSendMsgToEventThread;
    private TextView vCurrentTime;

    private MiniHandler mEventHandler;
    private MiniHandlerThread mHandlerThread;
    private Timer mTimer;

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
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
    }

    private void startEventLoop() {
        //事件处理线程
        mHandlerThread = new MiniHandlerThread("handler-thread");
        mHandlerThread.start();
        mEventHandler = new MiniHandler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                long action = message.what;
                if (action == ACTION_TOAST) {
                    String msg = message.obj.toString();
                    Log.d(TAG, "MiniHandler 处于的线程：" + Thread.currentThread());
                    ToastUtil.toast(getApplicationContext(), msg);
                } else if (action == ACTION_COUNT_DOWN) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timeStr = format.format(System.currentTimeMillis());
                    Log.d(TAG, "当前时间：" + timeStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vCurrentTime.setText(timeStr);
                        }
                    });
                }
            }
        };
    }

    private void findView() {
        vSendMsgToEventThread = findViewById(R.id.send_msg_to_event_thread);
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
    }

    /**
     * 开启定时器
     */
    private void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mEventHandler.sendMessage(Message.obtain(ACTION_COUNT_DOWN));
            }
        }, 0, 1000);
    }
}