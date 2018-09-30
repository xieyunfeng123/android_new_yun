package com.ityun.zhihuiyun.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.ItemFile;
import com.ityun.zhihuiyun.bean.ManageFile;
import com.ityun.zhihuiyun.me.adapter.ShowPhotoAdapter;
import com.ityun.zhihuiyun.util.AnimationUtil;
import com.ityun.zhihuiyun.util.CameraUtil;
import com.ityun.zhihuiyun.view.AlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/13 0013.
 * 图片中心页面
 */

public class ShowPhotoActivity extends BaseActivity {

    @BindView(R.id.picCenter)
    RecyclerView picCenter;

    @BindView(R.id.ll_return)
    LinearLayout ll_return;

    @BindView(R.id.select)
    TextView select;

    @BindView(R.id.select_all)
    LinearLayout selectAll;

    @BindView(R.id.ll_delete)
    LinearLayout delete;

    @BindView(R.id.picCenter_buttom)
    LinearLayout picCenter_buttom;

    @BindView(R.id.manage_file_img)
    ImageView manage_file_img;

    private boolean isEdit = false;

    private List<String> imageName;

    private List<ManageFile> mlist;

    private ShowPhotoAdapter adapter;

    private boolean isChoose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showphoto);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化图片（显示所有图片）
     */
    private void initData() {

        imageName = CameraUtil.getInstance().getImageName(this);
        Log.e("insert", "activity imagename.size():" + imageName.size());
        if (imageName == null) {
            imageName = new ArrayList<>();
        }
        List<String> data = new ArrayList<>();
        mlist = new ArrayList<>();
        for (int i = 0; i < imageName.size(); i++) {
            String[] str = imageName.get(i).split("/");
            File file = new File(CameraUtil.picPath);
            File videoFile = new File(CameraUtil.path);
            boolean isFolder;
            if (file.exists() && videoFile.exists()) {
                isFolder = file.isDirectory();
                if (isFolder) {
                    if (!data.contains(str[str.length - 1].substring(0, 10)))
                    data.add(str[str.length - 1].substring(0, 10));
                }
            }
        }

        for (int i = 0; i < data.size(); i++) {
            ManageFile manageFile = new ManageFile();
            List<ItemFile> itemFiles = new ArrayList<>();
            for (String name : imageName) {
                String[] s = name.split("/");
                if (data.get(i).equals(s[s.length - 1].substring(0, 10))) {
                    ItemFile itemFile = new ItemFile();
                    itemFile.setName(name);
                    itemFile.setChoose(false);
                    itemFiles.add(itemFile);
                }
            }

            manageFile.setData(data.get(i));
            manageFile.setItemFiles(itemFiles);
            mlist.add(manageFile);
        }
        Log.e("insert", "mlist.size():" + mlist.size());
        adapter = new ShowPhotoAdapter(this);
        picCenter.setLayoutManager(new LinearLayoutManager(this));
        picCenter.setAdapter(adapter);
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
        picCenter.setHasFixedSize(true);
        adapter.setOnPicListener(new ShowPhotoAdapter.OnPicListener() {
            public void OnClick(int groupPosition, int childPosition) {
                if (isEdit) {
                    // 编辑
                    boolean result = mlist.get(groupPosition).getItemFiles().get(childPosition).isChoose();
                    mlist.get(groupPosition).getItemFiles().get(childPosition).setChoose(!result);
                    adapter.setData(mlist);
                    adapter.notifyDataSetChanged();
                    picCenter.setHasFixedSize(true);
                } else { // 预览
                    if (CameraUtil.getInstance().getImageName(ShowPhotoActivity.this) != null) {
                        imageName = CameraUtil.getInstance().getImageName(ShowPhotoActivity.this);
                    } else {
                        imageName = new ArrayList<>();
                    }
                    for (int i = 0; i < imageName.size(); i++) {
                        if (imageName.get(i).equals(mlist.get(groupPosition).getItemFiles().get(childPosition).getName())) {
                            //点击图片显示图片详情
//                            Intent intent = new Intent(ShowPhotoActivity.this, TempActivity.class);
//                            startActivity(intent);
//                            Log.e("insert", "-----------------");
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.ll_return, R.id.select_all, R.id.ll_delete, R.id.select})
    public void onClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.ll_return:
                finish();
                break;
            // 选择、取消
            case R.id.select:
                Log.e("insert", "----------选择-------");
                if (isEdit) {

                    select.setText("编辑");
                    AnimationUtil.topToBottom(picCenter_buttom);
                    picCenter_buttom.setVisibility(View.GONE);
                    for (ManageFile file : mlist) {
                        for (ItemFile itemFile : file.getItemFiles()) {
                            itemFile.setChoose(false);
                        }
                    }
                    adapter.isShowView(false);
                    adapter.setData(mlist);
                    adapter.notifyDataSetChanged();
                    Log.e("insert", "----------点击取消-------");

//                    select.setText("取消");
//                    AnimationUtil.buttomToTop(picCenter_buttom);
//                    picCenter_buttom.setVisibility(View.VISIBLE);
//                    adapter.isShowView(true);
//                    adapter.notifyDataSetChanged();
//                    Log.e("insert", "----------点击选择-------");
                } else {

                    select.setText("取消");
                    AnimationUtil.buttomToTop(picCenter_buttom);
                    picCenter_buttom.setVisibility(View.VISIBLE);
                    adapter.isShowView(true);
                    adapter.notifyDataSetChanged();

//                    select.setText("编辑");
//                    AnimationUtil.topToBottom(picCenter_buttom);
//                    picCenter_buttom.setVisibility(View.GONE);
//                    for (ManageFile file : mlist) {
//                        for (ItemFile itemFile : file.getItemFiles()) {
//                            itemFile.setChoose(false);
//                        }
//                    }
//                    adapter.isShowView(false);
//                    adapter.setData(mlist);
//                    adapter.notifyDataSetChanged();
//                    Log.e("insert", "----------点击取消-------");
                }
                isEdit = !isEdit;
                break;
            // 全选
            case R.id.select_all:
                if (mlist != null && mlist.size() != 0) {
                    if (!isChoose) {
                        for (int i = 0; i < mlist.size(); i++) {
                            for (int j = 0; j < mlist.get(i).getItemFiles().size(); j++) {
                                mlist.get(i).getItemFiles().get(j).setChoose(true);
                            }
                            adapter.notifyItemChanged(i);
                        }
                        manage_file_img.setImageResource(R.mipmap.img_checked);
                    } else {
                        for (int i = 0; i < mlist.size(); i++) {
                            for (int j = 0; j < mlist.get(i).getItemFiles().size(); j++) {
                                mlist.get(i).getItemFiles().get(j).setChoose(false);
                            }
                            adapter.notifyItemChanged(i);
                        }
                        manage_file_img.setImageResource(R.mipmap.mange_select);
                    }
                    adapter.setData(mlist);
                    adapter.notifyDataSetChanged();
                    isChoose = !isChoose;
                    picCenter.setHasFixedSize(true);
                }
                break;
            // 删除
            case R.id.ll_delete:
                if (mlist != null && mlist.size() != 0) {
                    AlertDialog dialog = new AlertDialog(this);
                    dialog.builder();
                    dialog.setMsg("确定删除吗？");
                    dialog.setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteFile();
                        }
                    });
                    dialog.setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 删除文件
     */
    private void deleteFile() {
        Iterator<ManageFile> it = mlist.iterator();
        while (it.hasNext()) {
            ManageFile x = it.next();
            Iterator<ItemFile> itemFileIterator = x.getItemFiles().iterator();
            while (itemFileIterator.hasNext()) {
                ItemFile itemFile = itemFileIterator.next();
                if (itemFile.isChoose()) {
                    File file = new File(itemFile.getName());
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    itemFileIterator.remove();
                }
            }
        }
        Iterator<ManageFile> manageFileIterator = mlist.iterator();
        while (manageFileIterator.hasNext()) {
            if (manageFileIterator.next().getItemFiles().size() == 0) {
                manageFileIterator.remove();
            }
        }
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
    }
}
