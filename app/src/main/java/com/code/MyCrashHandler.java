package com.code;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Nbad 异常处理类,对异常进行捕获
 * 
 */
public class MyCrashHandler implements UncaughtExceptionHandler
{
    // 1.私有化构造方法
    private static UncaughtExceptionHandler defaulthandler;
    private static MyCrashHandler myCrashHandler;
    private static Context context;
    String errorinfo = null;
    private MyCrashHandler()
    {
    };
    // 2.提供一个静态的方法 获取到 当前类的一个实例
    public synchronized static MyCrashHandler getInstance(Context c)
    {
        Thread.currentThread();
        defaulthandler = Thread.getDefaultUncaughtExceptionHandler();
        if (myCrashHandler == null)
        {
            myCrashHandler = new MyCrashHandler();
            context = c;
        }
        return myCrashHandler;
    }
  
    /**
     * 对异常进行处理
     */
    public void uncaughtException(Thread thread, final Throwable ex)
    {
        try
        {
//            StringBuilder sb = new StringBuilder();
//            String path = ClientPlay.getFileDir();
//            File file = new File(path, "error.txt");
//            // FileOutputStream fos = new FileOutputStream(file);
//            FileWriter fw = new FileWriter(file, true);
//            // 1.获取当前应用程序的版本号,版本信息.
//            PackageManager pm = context.getPackageManager();
//            PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);
//            String versionname = packinfo.versionName;
//            sb.append("版本号" + versionname);
//            sb.append("\n");
//            sb.append("时间" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
//
//            // 2.获取当前手机的操作系统的信息,手机的一些硬件信息
//            Field[] fields = Build.class.getDeclaredFields();
//            for (Field field : fields)
//            {
//                field.setAccessible(true);// 暴力反射 获取私有的字段
//                sb.append(field.getName());
//                sb.append("=");
//                sb.append(field.get(null).toString());
//                sb.append("\n");
//            }
//
//            StringWriter writer = new StringWriter();
//            PrintWriter printWriter = new PrintWriter(writer);
//            ex.printStackTrace(printWriter);
//            String errorlog = writer.toString();
//            sb.append(errorlog);
//            errorinfo = sb.toString();
//            fw.write(errorinfo);
//            fw.flush();
//            fw.close();
            // byte[] result = errorinfo.getBytes();
            // fos.write(result, 0, result.length);
            // fos.flush();
            // fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                };
            }.start();
        }
        finally
        {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        myCrashHandler.uncaughtException(thread, ex);
        // 调用系统的异常处理的代码
    }
}
