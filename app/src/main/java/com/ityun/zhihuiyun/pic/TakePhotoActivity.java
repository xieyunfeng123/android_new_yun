package com.ityun.zhihuiyun.pic;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.lisenter.ErrorLisenter;
import com.cjt2325.cameralibrary.lisenter.JCameraLisenter;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.util.CameraUtil;
import com.ityun.zhihuiyun.util.addpic.LocalMedia;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/22 0022.
 * 拍照界面
 */
public class TakePhotoActivity extends BaseActivity {

    @BindView(R.id.cameraview)
    JCameraView jCameraView;

    // 图片路径
    private String picPath;

    // 视频路径
    private String path;

    private List<LocalMedia> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takephoto);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置保存视频路径
        jCameraView.setSaveVideoPath(CameraUtil.path);
        // 设置只能拍照或者录像（默认两种都可以）
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
//        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        // 设置画面质量
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        // 拍照失败监听
        jCameraView.setErrorLisenter(new ErrorLisenter() {
            public void onError() {
                Toast.makeText(TakePhotoActivity.this, "open camera error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(TakePhotoActivity.this, "audio Permission error", Toast.LENGTH_SHORT).show();
            }
        });
        // 监听
        jCameraView.setJCameraLisenter(new JCameraLisenter() {

            // 拍照成功 返回bitmap
            public void captureSuccess(Bitmap bitmap) {
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd-HH-mm-ss");
                String name = format.format(new Date());
                File file = new File(CameraUtil.getInstance().getPicPath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                picPath = CameraUtil.getInstance().getPicPath() + "/" + name + ".jpg";

                if (bitmap != null && picPath != null && name != null) {
                    LocalMedia media = new LocalMedia();
                    media.setPath(picPath);

                    list.add(media);
                    CameraUtil.getInstance().saveBitmap(bitmap, picPath);
                    sendPic();
                }
            }

            @Override
            public void recordSuccess(String url) {
                Log.e("insert", "-----url---" + url);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String name = format.format(new Date());
//                path = CameraUtil.getInstance().getPath() + "/" + name + ".mp4";
                path = url;
                if (url != null && path != null && name != null) {
                    LocalMedia media = new LocalMedia();
                    media.setPath(path);
                    list.add(media);
                    CameraUtil.getInstance().saveVideo(path);
                    sendVideo();
                }
            }

            @Override
            public void quit() {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    /**
     * 发送图片
     */
    private void sendPic() {
        if (list.size() == 0) {
            Tost("请选择图片");
        } else {
            Intent intent = getIntent();
            intent.putExtra("pic", (Serializable) list);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 发送视频
     */
    private void sendVideo() {
        if (list.size() == 0) {
            Tost("请选择视频");
        } else {
            Intent intent = getIntent();
            intent.putExtra("video", (Serializable) list);
            setResult(100, intent);
            finish();
        }
    }

    public void onConfigurationChanged(Configuration configuartion) {
        super.onConfigurationChanged(configuartion);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
}
