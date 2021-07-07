package com.zh.android.minihandler.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zh.android.minihandler.Looper;
import com.zh.android.minihandler.Message;
import com.zh.android.minihandler.MiniHandler;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MiniHandler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //主线程
        Thread mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mMainHandler = new MiniHandler() {
                    @Override
                    public void handleMessage(Message message) {
                        super.handleMessage(message);
                        Log.d(TAG, "MiniHandler 处于的线程：" + Thread.currentThread());
                        Log.d(TAG, "MiniHandler 要处理的消息附件：" + message.obj.toString());
                    }
                };
                Looper.loop();
            }
        });
        mainThread.setName("main-thread");
        mainThread.start();

        Button sendMsgToMain = findViewById(R.id.send_msg_to_main);
        sendMsgToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //子线程发消息到主线程
                Thread childThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "子线程：" + Thread.currentThread());
                        mMainHandler.sendMessage(Message.obtain(1, "点击"));
                    }
                });
                childThread.setName("child-thread");
                childThread.start();
            }
        });
    }
}