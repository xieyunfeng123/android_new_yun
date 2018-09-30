package com.ityun.zhihuiyun.base;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.login.LoginActivity;


public class ProtectService extends Service implements MediaPlayer.OnCompletionListener {
    private boolean mPausePlay = false;//控制是否播放音频
    private MediaPlayer mediaPlayer;

    private static final int NOTIFICATION_ID = 20112;

    private Notification fadeNotification(Context context) {
//        /*Notification notification = new Notification();
//        // 随便给一个icon，反正不会显示，只是假装自己是合法的Notification而已
//        notification.icon = R.mipmap.ic_launcher;
//        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);*/
//        // 在API11之后构建Notification的方式
//        Notification.Builder builder = new Notification.Builder(context.getApplicationContext()); //获取一个Notification构造器
//        Intent nfIntent = new Intent(context, LoginActivity.class);
//        String CHANNEL_ONE_ID = "com.primedu.cn";
//        String CHANNEL_ONE_NAME = "Channel One";
//        NotificationChannel notificationChannel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setShowBadge(true);
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            NotificationManager manager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(notificationChannel);
//            builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
//                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
//                    .setContentTitle("指挥云") // 设置下拉列表里的标题
//                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
//                    .setContentText("指挥云") // 设置上下文内容
//                    .setChannelId(CHANNEL_ONE_ID)
//                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
//        } else {
//            builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
//                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
//                    .setContentTitle("指挥云") // 设置下拉列表里的标题
//                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
//                    .setContentText("指挥云") // 设置上下文内容
//                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
//        }
//        Notification notification = builder.build(); // 获取构建好的Notification
//        notification.defaults = Notification.DEFAULT_LIGHTS; //设置为默认的声音


                /*Notification notification = new Notification();
        // 随便给一个icon，反正不会显示，只是假装自己是合法的Notification而已
        notification.icon = R.mipmap.ic_launcher;
        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);*/
// 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(context, LoginActivity.class);

        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                    .setContentTitle("") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("") // 设置上下文内容
                    .setChannelId(CHANNEL_ONE_ID)
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        }else{
            builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                    .setContentTitle("") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        return notification;
    }

    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        notification = fadeNotification(this);
        startForeground(NOTIFICATION_ID, notification);
        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
       /* windowManager = (WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height =  WindowManager.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT>=26) {//8.0新特性
            layoutParams.type= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            layoutParams.type= WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.format= PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                |WindowManager.LayoutParams.FLAG_DIM_BEHIND
                |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.gravity = Gravity.CENTER;
        textView = new TextView(this.getApplicationContext());
        textView.setText("test");
        textView.setTextColor(this.getResources().getColor(R.color.red));
        windowManager.addView(textView,layoutParams);*/
    }

    /**
     * START_NOT_STICKY：当Service因为内存不足而被系统kill后，接下来未来的某个时间内，即使系统内存足够可用，系统也不会尝试重新创建此Service。
     * 除非程序中Client明确再次调用startService(...)启动此Service。
     * <p>
     * START_STICKY：当Service因为内存不足而被系统kill后，接下来未来的某个时间内，当系统内存足够可用的情况下，系统将会尝试重新创建此Service，
     * 一旦创建成功后将回调onStartCommand(...)方法，但其中的Intent将是null，pendingintent除外。
     * <p>
     * START_REDELIVER_INTENT：与START_STICKY唯一不同的是，回调onStartCommand(...)方法时，
     * 其中的Intent将是非空，将是最后一次调用startService(...)中的intent。
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.static_vol);
            mediaPlayer.setOnCompletionListener(this);
        }
        play();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 播放音频
     * 亮屏：播放保活
     * 锁屏：已连接，播音乐；未连接，不播放
     */
    private void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && !mPausePlay) {
            mediaPlayer.start();
        }
    }

    /**
     * 停止播放
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        mPausePlay = true;
    }

    //播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("insert","============onCompletion=============");
        play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
