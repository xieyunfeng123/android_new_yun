package com.ityun.zhihuiyun.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.ityun.zhihuiyun.bean.ImageInfo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 * 拍照和图片中心工具类
 */

public class CameraUtil {

    /* 保存视频路径 */
    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yunvideo";
    /* 保存图片路径 */
    public static String picPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yunpic";


    public static CameraUtil instance;

    public static CameraUtil getInstance() {
        if (instance == null)
            instance = new CameraUtil();
        return instance;
    }

    /**
     * 判断文件是否存在，若不存在则新建文件
     *
     * @param path
     */
    public void fileIsExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 获取图片路径
     *
     * @return
     */
    public String getPicPath() {
        return picPath;
    }

    /**
     * 获取视频路径
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * 保存bitmap到sdcard
     *
     * @param bitmap  拍照成功后返回的位图
     * @param picName 图片名称
     */
    public void saveBitmap(Bitmap bitmap, String picName) {
        try {
            FileOutputStream fots = new FileOutputStream(picName);
            BufferedOutputStream bots = new BufferedOutputStream(fots);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bots);
            bots.flush();
            bots.close();
            Log.e("insert", "save bitmap success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("insert", "save bitmap failed");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("insert", "save bitmap failed");
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath = null;
        long name = System.currentTimeMillis();
        try {
            savePath = Environment.getExternalStorageDirectory().getCanonicalPath().toString() + "/c0-y-d" + "/云图片";
        } catch (IOException e) {
            e.printStackTrace();
        }
        File filePic;

        try {
            filePic = new File(savePath, name + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * 保存视频
     *
     * @param path
     * @return
     */
    public String saveVideo(String path) {

        File fileVideo;

        fileVideo = new File(path, ".mp4");
        if (!fileVideo.exists()) {
            try {
                fileVideo.getParentFile().mkdirs();
                fileVideo.createNewFile();
                FileOutputStream fos = new FileOutputStream(fileVideo);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    /**
     * 获取图片名称
     *
     * @return 返回图片名称
     */
    public List<String> getImageName(Context context) {
        String path = picPath;
        File file = new File(path);
        if (!file.exists() && file.mkdir()) {
            Toast.makeText(context, "没有图片！", Toast.LENGTH_SHORT).show();
            return null;
        }
        List<String> mlist = new ArrayList<>();
        File[] str = file.listFiles();
        if (str == null) {
            Toast.makeText(context, "没有图片！", Toast.LENGTH_SHORT).show();
            return null;
        }

        ArrayList<ImageInfo> fileList = new ArrayList<ImageInfo>();

        // 将需要的子信息写到ImageInfo里
        for (int i = 0; i < str.length; i++) {
            File f = str[i];
            ImageInfo info = new ImageInfo();
            info.setName(f.getName());
            info.setPath(f.getPath());
            info.setLastModified(f.lastModified());
            fileList.add(info);
        }
        // 重写Comparator实现类
        Collections.sort(fileList, new FileComparator());
        Collections.reverse(fileList);
        for (int i = 0; i < fileList.size(); i++) {
            mlist.add(fileList.get(i).getPath());
        }

        List<String> send = new ArrayList<>();
        send.addAll(mlist);
        for (int i = 0; i < send.size(); i++) {
            if (send.get(i).substring(send.get(i).length() - 3, send.get(i).length()).equals("mp4")) {
                mlist.remove(send.get(i).replace("mp4", "jpg"));
            }
        }
        return mlist;
    }

    public class FileComparator implements Comparator<ImageInfo> {

        public int compare(ImageInfo i1, ImageInfo i2) {
            if (i1.getLastModified() < i2.getLastModified()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
