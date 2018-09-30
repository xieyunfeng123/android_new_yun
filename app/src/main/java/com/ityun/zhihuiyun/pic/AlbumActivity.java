package com.ityun.zhihuiyun.pic;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.pic.adapter.AlbumAdapter;
import com.ityun.zhihuiyun.util.DataUtils;
import com.ityun.zhihuiyun.util.PermissionUtil;
import com.ityun.zhihuiyun.util.addpic.LocalMedia;
import com.ityun.zhihuiyun.util.addpic.LocalMediaFolder;
import com.ityun.zhihuiyun.util.addpic.LocalMediaLoader;
import com.ityun.zhihuiyun.util.screen.Eyes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/22 0022.
 */

public class AlbumActivity extends BaseActivity {


    @BindView(R.id.picture_file_name)
    TextView picture_file_name;

    @BindView(R.id.picture_file_name_ll)
    RelativeLayout picture_file_name_ll;

    @BindView(R.id.picture_gridview)
    GridView picture_gridview;

    @BindView(R.id.picture_date)
    TextView picture_date;

    @BindView(R.id.send_pic)
    TextView send_pic;

    @BindView(R.id.select_back)
    ImageView select_back;

    private List<LocalMediaFolder> mediaFolderList;

    private List<LocalMedia> selectedList = new ArrayList<>();

    private List<LocalMedia> localMediaList;
    private int sType = 0;

    public int MY_PERMISSIONS_REQUEST_OPEN_ALBUM = 10;

    private AlbumAdapter adapter;

    private int REQUEST_CODE_PHONE_IMAGE = 22;

    private PopupWindow mPopupWindow;

    private AlbumPopwindow albumPopwindow;

    private boolean isShowTime;

    @BindView(R.id.select_num)
    public TextView select_num;


    private Handler mHideHandler = new Handler();
    private Runnable mHide = new Runnable() {
        @Override
        public void run() {
            hideTime();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        adapter = new AlbumAdapter(this);
        picture_gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        select_num.setText("0/9");
        if (PermissionUtil.readFile(this, MY_PERMISSIONS_REQUEST_OPEN_ALBUM) == 1) {
            initView();
        }
    }

    private void initView() {
        mediaFolderList = new ArrayList<>();
        localMediaList = new ArrayList<>();
        albumPopwindow = new AlbumPopwindow(this);
        albumPopwindow.setOnItemOnClick(new AlbumPopwindow.OnItemOnClick() {
            @Override
            public void onClick(int position) {
                sType = position;
                localMediaList.clear();
                localMediaList.addAll(mediaFolderList.get(sType).getImages());
                picture_file_name.setText(mediaFolderList.get(sType).getName());
                adapter.notifyDataSetChanged();
                mPopupWindow.dismiss();
            }
        });
        //获取本地所有图片
        new LocalMediaLoader(this, 0, false).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                localMediaList.clear();
                mediaFolderList.addAll(folders);
                localMediaList.addAll(mediaFolderList.get(sType).getImages());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaFolderList.get(0).getImages() != null && mediaFolderList.get(0).getImages().size() > 1) {
                            picture_date.setText(DataUtils.longToString(mediaFolderList.get(0).getImages().get(1).getLastUpdateAt(), "yyyy-MM-dd"));
                        }
                        picture_file_name.setText(mediaFolderList.get(sType).getName());
                        adapter.setData(localMediaList);
                        mPopupWindow = albumPopwindow.create(mediaFolderList);
                    }
                });
            }
        });
        picture_gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                changeTime();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                changeTime();
            }
        });

        adapter.setOnItemCheckListener(new AlbumAdapter.OnCheckListener() {
            @Override
            public void onClick(int position) {
                chooseImage(position);
            }
        });


        picture_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    /**
     * 选择或者删除选中的图片
     *
     * @param position
     */
    private void chooseImage(int position) {
        if (localMediaList.get(position).isChecked()) {
            selectedList.add(localMediaList.get(position));
        } else {
            selectedList.remove(localMediaList.get(position));
        }
        adapter.setCount(selectedList.size());
        adapter.notifyDataSetChanged();
        select_num.setText(selectedList.size() + "/9");
    }

    /**
     * 点击 换文件夹
     */
    @OnClick(R.id.picture_file_name)
    public void PopShow() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            int[] location = new int[2];
            picture_file_name_ll.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            Display display = getWindowManager().getDefaultDisplay();
            int m = display.getHeight() - mPopupWindow.getHeight() - picture_file_name_ll.getHeight();
            mPopupWindow.showAtLocation(picture_file_name_ll, Gravity.NO_GRAVITY, 0, m);
        }
    }

    /**
     * 发送图片
     */
    @OnClick(R.id.send_pic)
    public void sendPic() {
        if (selectedList.size() == 0) {
            Tost("请选择图片");
            return;
        } else {
            Intent intent = getIntent();
            intent.putExtra("pic", (Serializable) selectedList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @OnClick(R.id.select_back)
    public void finishActivity() {
        finish();
    }


    /**
     * 改变时间条显示的时间（显示图片列表中的第一个可见图片的时间）
     */
    private void changeTime() {
        int firstVisibleItem = getFirstVisibleItem();
        if (firstVisibleItem >= 0 && firstVisibleItem < localMediaList.size()) {
            String time = DataUtils.longToString(localMediaList.get(firstVisibleItem).getLastUpdateAt() * 1000, "yyyy-MM-dd");
            picture_date.setText(time);
            showTime();
            mHideHandler.removeCallbacks(mHide);
            mHideHandler.postDelayed(mHide, 1500);
        }
    }

    private int getFirstVisibleItem() {
        return picture_gridview.getFirstVisiblePosition();
    }

    /**
     * 显示时间条
     */
    private void showTime() {
        if (!isShowTime) {
            ObjectAnimator.ofFloat(picture_date, "alpha", 0, 1).setDuration(250).start();
            isShowTime = true;
        }
    }

    /**
     * 隐藏时间条
     */
    private void hideTime() {
        if (isShowTime) {
            ObjectAnimator.ofFloat(picture_date, "alpha", 1, 0).setDuration(250).start();
            isShowTime = false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_OPEN_ALBUM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //授权成功
                initView();
            } else {
                //授权失败
                Toast.makeText(this, "请打开相册权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHONE_IMAGE) {

        }
    }
}
