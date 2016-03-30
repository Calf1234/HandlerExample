
package com.example.handlerexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/*
 * 
 * @author Calf1234
 *
 * Handler实例化，会去给mLooper、mQueue、mCallback、mAsynchronous赋值。
 * 在Activity中，实例化Handler对象，不许要指定Looper。这是因为在ActivityThread#main中有做相关设置。
 * 而在其他自定义的线程中，一般要指定Looper，才可以生效。
 */
public class main1 extends Activity implements View.OnClickListener {

    Button button1, button2, button3, button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        android.util.Log.v(main1.class.getName(), "button1 :" + button1 + " button2 :" + button2
                + " ...");
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    MyHandler mHandler;
    Message m;

    protected void dealOnClick(int i) {
        // TODO Auto-generated method stub
        switch (i) {
            case R.id.button1:
                //主线程中创建Handler和Message对象，最后Handler发送消息也在主线程中完成
                mHandler = new MyHandler();
                m = mHandler
                        .obtainMessage(1, (Object) "Main thread send message by Message Object");
                m.sendToTarget();
                break;
                
            case R.id.button2:
                //在子线程中异步创建Handler和Message对象，并发送Handler消息
                InnerMyThread imt = new InnerMyThread();
//                android.util.Log.v(main1.this.getPackageName(), "button2 OnClicked Looper");
                imt.start();
                break;
                
            case R.id.button3:
                //在主线程中创建Handler对象，子线程创建Message消息并发送Handler消息
                mHandler = new MyHandler();
                Toast.makeText(getApplicationContext(), "mHandler :" + mHandler.hashCode(), 1).show();
                MyThread mt = new MyThread(mHandler);
                mt.start();
                break;
                
            case R.id.button4:
                //在子线程中更新主线程的界面
                UpdateThread ut = new UpdateThread();
                ut.start();
                break;
                
            default:
                break;
        }
    }
    
    class UpdateThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //报错
            //错误信息
            /*
             * android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread
             * that created a view hierarchy can touch its views.
             */
            button4.setText("Update UI");
        }
    }
    
    class MyThread extends Thread {
        Handler mHandler;

        public MyThread(Handler mHandler) {
            // TODO Auto-generated constructor stub
            this.mHandler = mHandler;
            Toast.makeText(getApplicationContext(), "this.mHandler :" + this.mHandler.hashCode(), 1).show();
        }
        
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message m =  mHandler.obtainMessage(3, (Object)"Ohter thread send message by mHandler");
            m.sendToTarget();
        }
    }
    
    class InnerMyThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //报错
            //仅能在Activity主线程内使用不带Looper对象创建Handler对象,ActivityThread中创建了Looper
            /* 异常信息
             * java.lang.RuntimeException: Can't create handler inside thread that has not called
             * Looper.prepare()
             */
//            mHandler = new MyHandler();
            
            //报错
            //Looper.myLooper <-- null
            // 一个线程对应一个Looper。先去调用prepare，不然myLooper获取到的为null值
//            android.util.Log.v(main1.this.getPackageName(),
//                    "Looper.myLooper() :" + Looper.myLooper() + "Looper.myLooper().myQueue() :" + Looper.myLooper().myQueue());
//            mHandler = new MyHandler(Looper.myLooper());
            
            //运行正常
            /* Looper.getMainLooper()中获取到的Looper，在ActivityThread中进行prepareMainLooper中进行了相关设置 */
            mHandler =new MyHandler(Looper.getMainLooper());
            Message m = mHandler.obtainMessage(2,
                    (Object) "Inner thread send message by Message Object");
            m.sendToTarget();
        }
    }

    class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            // TODO Auto-generated constructor stub
            super(looper);
        }

        public MyHandler() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            String str = "";
            switch (msg.what) {
                case 1:
                    str = "1:" + msg.obj;
                    break;
                case 2:
                    str = "2:" + msg.obj;
                    break;
                case 3:
                    str = "3:" + msg.obj;
                    break;
                case 4:
                    str = "4:" + msg.obj;
                    break;
                default:
                    break;
            }
            Toast toast = Toast.makeText(main1.this, str, 1);
            toast.show();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        dealOnClick(v.getId());
    }
}
