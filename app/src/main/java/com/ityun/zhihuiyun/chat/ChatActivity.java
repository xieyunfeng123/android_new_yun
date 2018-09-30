package com.ityun.zhihuiyun.chat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.CallIngBack;
import com.ityun.zhihuiyun.bean.CallStatus;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.bean.UserStatus;
import com.ityun.zhihuiyun.chat.adapter.ChatMessageAdapter;
import com.ityun.zhihuiyun.chat.adapter.FaceGVAdapter;
import com.ityun.zhihuiyun.chat.adapter.FaceVPAdapter;
import com.ityun.zhihuiyun.db.HomeMessageUtil;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.event.FinishEvent;
import com.ityun.zhihuiyun.event.HomeEvent;
import com.ityun.zhihuiyun.event.HttpEvent;
import com.ityun.zhihuiyun.event.IsTalkingEvent;
import com.ityun.zhihuiyun.event.ReceiveMessageEvent;
import com.ityun.zhihuiyun.group.DetailActivity;
import com.ityun.zhihuiyun.group.GroupManagerActivity;
import com.ityun.zhihuiyun.intercom.IntercomActivity;
import com.ityun.zhihuiyun.locate.BaiduMapActivity;
import com.ityun.zhihuiyun.pic.AlbumActivity;
import com.ityun.zhihuiyun.pic.TakePhotoActivity;
import com.ityun.zhihuiyun.talk.GroupTalkActivity;
import com.ityun.zhihuiyun.talk.RobTalkActivity;
import com.ityun.zhihuiyun.talk.SingleTalkActivity;
import com.ityun.zhihuiyun.talk.StrongPullTalkActivity;
import com.ityun.zhihuiyun.talk.TrailRobActivity;
import com.ityun.zhihuiyun.teamviewer.SendVideoActivity;
import com.ityun.zhihuiyun.util.CameraUtil;
import com.ityun.zhihuiyun.util.FileUtil;
import com.ityun.zhihuiyun.util.GroupChatUtil;
import com.ityun.zhihuiyun.util.KeyboartUtil;
import com.ityun.zhihuiyun.util.MediaUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.VoiceUtil;
import com.ityun.zhihuiyun.util.addpic.LocalMedia;
import com.ityun.zhihuiyun.util.screen.AppFileUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.ActionSheetDialog;
import com.noober.menu.FloatMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.vomont.fileloadsdk.WMFileLoadSdk;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2018/5/28 0028.
 * 1.群组语音直接接通，没有同意
 */

public class ChatActivity extends BaseActivity implements View.OnTouchListener {

    @BindView(R.id.chat_top)
    RelativeLayout chat_top;

    @BindView(R.id.chat_name)
    TextView chat_name;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.chat_list_recyclerview)
    RecyclerView chat_list_recyclerview;

    @BindView(R.id.send_keyboard)
    ImageView send_keyboard;

    @BindView(R.id.send_voice)
    ImageView send_voice;

    @BindView(R.id.edit_voice)
    TextView edit_voice;

    @BindView(R.id.edit)
    EditText edit;

    @BindView(R.id.add)
    ImageView add;

    @BindView(R.id.send)
    Button send;

    @BindView(R.id.include_fuction)
    TableLayout include_fuction;

    @BindView(R.id.pic)
    ImageView pic;

    @BindView(R.id.voice)
    ImageView voice;
    @BindView(R.id.voice_size_ll)
    LinearLayout voice_size_ll;

    @BindView(R.id.imageVolume)
    ImageView imageVolume;

    @BindView(R.id.textHint)
    TextView textHint;

    @BindView(R.id.talk)
    LinearLayout talk;

    @BindView(R.id.chat_setting)
    ImageView chat_setting;

    @BindView(R.id.sharemore_sight)
    ImageView sharemore_sight;

    @BindView(R.id.share_talk_ll)
    LinearLayout share_talk_ll;

    @BindView(R.id.emonji)
    ImageView emonji;

    @BindView(R.id.voice_send_cancel)
    LinearLayout voice_cancel;

    @BindView(R.id.chat_face_container)
    LinearLayout chat_face_container;

    @BindView(R.id.face_viewpager)
    ViewPager face_viewpager;

    @BindView(R.id.face_dots_container)
    LinearLayout face_dots_container;

    @BindView(R.id.teamViewer)
    LinearLayout teamViewer;

    @BindView(R.id.strong_pull)
    LinearLayout strong_pull;

    @BindView(R.id.call_number)
    RelativeLayout call_number;

    @BindView(R.id.tv_callnumber)
    TextView tv_callnumber;

    // 7列3行
    private int columns = 6;
    private int rows = 4;
    private List<View> views = new ArrayList<View>();
    private List<String> staticFacesList;

    private ChatMessageAdapter adapter;

    //0单聊 1群聊
    private int type = 0;

    //好友
    private Account account;

    //群组
    private GroupInfo groupInfo;

    private SysConfBean bean;

    //当前时间
    private long nowTime;

    //聊天消息
    private List<IMMessage> mlist = new ArrayList<>();

    private MediaUtil mediaUtil;

    //音频的路径
    private String voicePath;

    //是否在录音
    private boolean isMedia;

    //计时
    private int mTime;

    private int SELECT_PIC = 0;

    private Context context;

    private float startY = 0;

    private boolean isSend = true;

    String sendContent = "";

    //49.4.84.249
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ChatActivity.this;
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        bean = App.getInstance().getSysConfBean();
        account = (Account) getIntent().getSerializableExtra("account");
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupinfo");
        type = getIntent().getIntExtra("type", 0);
        if (groupInfo != null) {
            chat_setting.setImageResource(R.mipmap.group_top);
        }
        if (type == 1) {
            isTalking();
        }
        mediaUtil = new MediaUtil(this);
        mediaUtil.setOnAudioStateChangeListener(i -> runOnUiThread(() -> {
            switch (i) {
                case 0:
                    imageVolume.setImageResource(R.drawable.icon_volume_1);
                    break;
                case 1:
                    imageVolume.setImageResource(R.drawable.icon_volume_2);
                    break;
                case 2:
                    imageVolume.setImageResource(R.drawable.icon_volume_3);
                    break;
                case 3:
                    imageVolume.setImageResource(R.drawable.icon_volume_4);
                    break;
                case 4:
                    imageVolume.setImageResource(R.drawable.icon_volume_5);
                    break;
                case 5:
                    imageVolume.setImageResource(R.drawable.icon_volume_6);
                    break;
                case 6:
                    imageVolume.setImageResource(R.drawable.icon_volume_7);
                    break;
                case 7:
                    imageVolume.setImageResource(R.drawable.icon_volume_7);
                    break;
            }
        }));
        adapter = new ChatMessageAdapter(this);
        adapter.setData(mlist);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chat_list_recyclerview.setLayoutManager(manager);
        chat_list_recyclerview.setAdapter(adapter);
        adapter.setOnItemLongClick(new ChatMessageAdapter.OnItemLongClick() {
            @Override
            public void onClick(int position) {
                FloatMenu floatMenu = new FloatMenu(ChatActivity.this);
                floatMenu.items("删除");
                floatMenu.setOnItemClickListener((v, position1) -> {
                    IMUtil.getInstance().deleteMessage(mlist.get(position));
                    mlist.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyDataSetChanged();
                });
                floatMenu.show(point);
            }

            @Override
            public void onRestartClick(int position) {
                onRestartSend(mlist.get(position));
            }
        });


        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    add.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);
                } else {
                    add.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);

        edit_voice.setOnTouchListener((v, event) -> {
            if (!AppFileUtil.getInstance().permissionAudio(ChatActivity.this)) {

                if (SingleTalkActivity.hasTalk || GroupChatUtil.getInstance().isCalling()) {
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        voice_size_ll.setVisibility(View.VISIBLE);
                        mTime = 0;
                        voicePath = mediaUtil.startRecorder();
                        isMedia = true;
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }

                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mTime += 1;
                                runOnUiThread(() -> {
                                    if (mTime > 105 && isMedia) {
                                        voice_size_ll.setVisibility(View.GONE);
                                        isMedia = false;
                                        mediaUtil.stopRecorder();
                                        sendMessage(1, 0);
                                        Tost("语音不能超过10秒");
                                        if (timer != null) {
                                            timer.cancel();
                                            timer = null;
                                        }
                                    }
                                });
                            }
                        }, 0, 100);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveY = event.getY();
                        int instance = (int) Math.abs(moveY - startY);
                        if (instance > 100) {
                            isSend = false;
                            isMedia = false;
                            voice_size_ll.setVisibility(View.GONE);
                            voice_cancel.setVisibility(View.VISIBLE);
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            break;
                        }
                        if (mTime > 100 && isMedia) {
                            voice_size_ll.setVisibility(View.GONE);
                            isMedia = false;
                            isSend = false;
                            mediaUtil.stopRecorder();
                            sendMessage(1, 0);
                            Tost("语音不能超过10秒");
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            break;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isSend) {
                            if (mTime < 15 && isMedia) {
                                mediaUtil.cancel();
                                voice_size_ll.setVisibility(View.GONE);
                                voice_cancel.setVisibility(View.GONE);
                                Tost("语音不能少于1秒");
                            } else if (isMedia) {
                                mediaUtil.stopRecorder();
                                sendMessage(1, 0);
                                voice_size_ll.setVisibility(View.GONE);
                            }
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            isMedia = false;
                        } else {
                            mediaUtil.cancel();
                            voice_size_ll.setVisibility(View.GONE);
                            voice_cancel.setVisibility(View.GONE);
                            isSend = true;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (mTime < 15 && isMedia) {
                            mediaUtil.cancel();
                        } else if (isMedia) {
                            mediaUtil.stopRecorder();
                            sendMessage(1, 0);
                        }
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        isMedia = false;
                        voice_size_ll.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            } else {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Tost("请去设置界面打开录音权限！");
                }
            }
            return true;
        });

        edit.setOnClickListener(v -> {
            include_fuction.setVisibility(View.GONE);
        });
        edit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                include_fuction.setVisibility(View.GONE);
            }
        });
        searchMeeage();
        face_viewpager.addOnPageChangeListener(new PageChange());
        initStaticFaces();
        InitViewPager();
        chat_list_recyclerview.setOnTouchListener(this);
        hintView();
    }


    private void hintView() {
        chat_face_container.setVisibility(View.GONE);
        include_fuction.setVisibility(View.GONE);
        KeyboartUtil.hide(ChatActivity.this, edit);
    }


    private void onRestartSend(IMMessage message) {
        message.setSendState(1);
        if (message.getMeessageTpye() == 2 || message.getMeessageTpye() == 3) {
            int id = WMFileLoadSdk.getInstance().WM_VFile_UploadBigFile(message.getImagePath());
            message.setLoadId(id);
            IMUtil.getInstance().upDataIM(message);
            adapter.notifyDataSetChanged();
        } else {
            nowTime = message.getCreateTime();
            if (message.getMeessageTpye() == 0 || message.getMeessageTpye() == 4) {
                sendContent = message.getMessage();
            } else if (message.getMeessageTpye() == 1) {
                byte[] file = FileUtil.File2byte(message.getVoicePath());
                sendContent = Base64.encodeToString(file, Base64.DEFAULT);
            }
            IMUtil.getInstance().upDataIM(message);
            adapter.notifyDataSetChanged();
            sendMessage(message.getMeessageTpye(), 1);
        }
    }


    /**
     * 初始化表情
     */
    private void InitViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            face_dots_container.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        face_viewpager.setAdapter(mVpAdapter);
        face_dots_container.getChildAt(0).setSelected(true);
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }


    /**
     * 初始化表情列表staticFacesList
     */
    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < face_dots_container.getChildCount(); i++) {
                face_dots_container.getChildAt(i).setSelected(false);
            }
            face_dots_container.getChildAt(arg0).setSelected(true);
        }
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判断当前图片是哪一个
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(ChatActivity.this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
        GridView gridview = layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener((parent, view, position1, id) -> {
            try {
                String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                    insert(getFace(png));
                } else {
                    delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return gridview;
    }

    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((edit.getText()));
        int iCursorEnd = Selection.getSelectionEnd((edit.getText()));
        if (iCursorStart != iCursorEnd) {
            (edit.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((edit.getText()));
        (edit.getText()).insert(iCursor, text);
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private void delete() {
        if (edit.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(edit.getText());
            int iCursorStart = Selection.getSelectionStart(edit.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        (edit.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        (edit.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    (edit.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st = "#[face/png/f_static_000.png]#";
        String content = edit.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    int id;

    /**
     * 获取头部文件名称
     */
    private void getTopName() {
        teamViewer.setVisibility(View.INVISIBLE);
        teamViewer.setClickable(false);
        share_talk_ll.setVisibility(View.INVISIBLE);
        share_talk_ll.setClickable(false);
        strong_pull.setVisibility(View.INVISIBLE);
        strong_pull.setClickable(false);

        id = 0;
        if (account == null && groupInfo == null) {
            return;
        }
        if (type == 0 && account != null) {
            teamViewer.setVisibility(View.INVISIBLE);
            teamViewer.setClickable(false);
            //单人聊天
            if (!TextUtils.isEmpty(account.getRemarkName())) {
                chat_name.setText(account.getRemarkName());
            } else {
                chat_name.setText(account.getName());
            }
            id = account.getId();
            share_talk_ll.setVisibility(View.INVISIBLE);
            sharemore_sight.setEnabled(false);
        } else if (type == 1 && groupInfo != null) {
            //群组聊天
            String name = "";
            try {
                name = URLDecoder.decode(groupInfo.getName(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            chat_name.setText(name);
            id = groupInfo.getId();
        }
    }

    /**
     * 根据id查找对应的聊天消息
     */
    private void searchMeeage() {
        getTopName();
        new Thread(() -> {
            List<IMMessage> messages = IMUtil.getInstance().selectMeaage(bean.getAccountid(), id);
            if (messages != null) {
                for (IMMessage message : messages) {
                    message.setIsRead(1);
                    IMUtil.getInstance().upDataIM(message);
                }
                mlist.clear();
                mlist.addAll(messages);
                runOnUiThread(() -> {
                    //当有新消息时，刷新RecyclerView中的显示
                    adapter.notifyDataSetChanged();
                    chat_list_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
                    chat_list_recyclerview.postDelayed(() -> chat_list_recyclerview.scrollToPosition(adapter.getItemCount() - 1), 300);
                });
            }
        }).start();
    }

    @OnClick({R.id.chat_top, R.id.send_keyboard, R.id.send_voice, R.id.add, R.id.send, R.id.talk, R.id.pic, R.id.chat_setting, R.id.sharemore_sight, R.id.location_share, R.id.emonji, R.id.teamViewer, R.id.strong_pull})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.chat_top:
                //返回
                finish();
                break;
            case R.id.send_keyboard:
                //切换到键盘
                send_keyboard.setVisibility(View.GONE);
                send_voice.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                edit_voice.setVisibility(View.GONE);
                chat_face_container.setVisibility(View.GONE);
                edit.setFocusable(true);
                edit.setFocusableInTouchMode(true);
                edit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edit, 0);
                break;
            case R.id.send_voice:
                //切换到语音
                send_keyboard.setVisibility(View.VISIBLE);
                send_voice.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                edit_voice.setVisibility(View.VISIBLE);
                chat_face_container.setVisibility(View.GONE);
                break;
            case R.id.add:
                //其他功能
                if (include_fuction.getVisibility() == View.VISIBLE) {
                    include_fuction.setVisibility(View.GONE);
                } else {
                    include_fuction.setVisibility(View.VISIBLE);
                    KeyboartUtil.hide(ChatActivity.this, edit);
                }
                chat_face_container.setVisibility(View.GONE);
                break;
            case R.id.emonji:
                //表情
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                } else {
                    chat_face_container.setVisibility(View.VISIBLE);
                    KeyboartUtil.hide(ChatActivity.this, edit);
                }
                include_fuction.setVisibility(View.GONE);
                break;
            // 发送文字消息
            case R.id.send:
                include_fuction.setVisibility(View.GONE);
                chat_face_container.setVisibility(View.GONE);
                sendMessage(0, 0);
//                IMMessage message;
//                message = IMClient.getInstance().createTxtSendMessage(edit.getText().toString(), account.getId(), false);
//               IMClient.getInstance().sendMessage(message);
                edit.setText("");
                break;
            case R.id.talk:
                if (SingleTalkActivity.hasTalk || GroupChatUtil.getInstance().isCalling()) {
                    return;
                }
                if (type == 0) {
                    Intent intent = new Intent(ChatActivity.this, SingleTalkActivity.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                } else {
                    GroupChatUtil.getInstance().hasGroupChat(groupInfo.getId(), new GroupChatUtil.HasChatListener() {
                        @Override
                        public void HasChat(int size, int type, int chatId) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int callType = com.wm.Constants.WM_GetCallType(type);

                                    if (callType == com.wm.Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice) {
                                        TalkRecieve talkRecieve = new TalkRecieve();
                                        talkRecieve.setChatid(chatId);
                                        talkRecieve.setChattype(type);
                                        talkRecieve.setJoin(true);
                                        Intent groupintent = new Intent(ChatActivity.this, GroupTalkActivity.class);
                                        groupintent.putExtra("receive", talkRecieve);
                                        startActivity(groupintent);
                                    }
                                    if (callType == com.wm.Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice_RobMic) {
                                        TalkRecieve talkRecieve = new TalkRecieve();
                                        talkRecieve.setChatid(chatId);
                                        talkRecieve.setChattype(type);
                                        talkRecieve.setJoin(true);
                                        Intent groupintent = new Intent(ChatActivity.this, RobTalkActivity.class);
                                        groupintent.putExtra("receive", talkRecieve);
                                        startActivity(groupintent);
                                    }
                                    if (callType == 12) {
                                        TalkRecieve talkRecieve = new TalkRecieve();
                                        talkRecieve.setChatid(chatId);
                                        talkRecieve.setChattype(type);
                                        talkRecieve.setJoin(true);
                                        Intent groupintent = new Intent(ChatActivity.this, SendVideoActivity.class);
                                        groupintent.putExtra("receive", talkRecieve);
                                        startActivity(groupintent);
                                    }
                                    if (callType == 13) {
                                        TalkRecieve talkRecieve = new TalkRecieve();
                                        talkRecieve.setChatid(chatId);
                                        talkRecieve.setJoin(true);
                                        talkRecieve.setChattype(type);
                                        Intent groupintent = new Intent(ChatActivity.this, StrongPullTalkActivity.class);
                                        groupintent.putExtra("receive", talkRecieve);
                                        startActivity(groupintent);
                                    }
                                    if (callType == 14) {
                                        TalkRecieve talkRecieve = new TalkRecieve();
                                        talkRecieve.setChatid(chatId);
                                        talkRecieve.setChattype(type);
                                        talkRecieve.setJoin(true);
                                        Intent groupintent = new Intent(ChatActivity.this, TrailRobActivity.class);
                                        groupintent.putExtra("receive", talkRecieve);
                                        startActivity(groupintent);
                                    }
                                    isCalling = false;
                                }
                            });
                        }

                        @Override
                        public void NoChat() {
                            Intent intent = new Intent(ChatActivity.this, IntercomActivity.class);
                            intent.putExtra("group", groupInfo);
                            startActivity(intent);
                        }
                    });
                }
                include_fuction.setVisibility(View.GONE);
                break;
            // 图片
            case R.id.pic:
                new ActionSheetDialog(context)
                        .builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addSheetItem("相机", ActionSheetDialog.SheetItemColor.Blue,
                                which -> {
                                    Intent intent = new Intent(ChatActivity.this, TakePhotoActivity.class);
                                    startActivityForResult(intent, SELECT_PIC);
                                })
                        .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue,
                                which -> {
                                    Intent intent = new Intent(ChatActivity.this, AlbumActivity.class);
                                    startActivityForResult(intent, SELECT_PIC);
                                    include_fuction.setVisibility(View.GONE);
                                }).show();
                include_fuction.setVisibility(View.GONE);
                break;
            case R.id.location_share:
                // 发送位置
                Intent intent1 = new Intent(ChatActivity.this, BaiduMapActivity.class);
                startActivityForResult(intent1, 1);
                include_fuction.setVisibility(View.GONE);
                break;
            case R.id.chat_setting:
                if (account != null) {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                }
                if (groupInfo != null) {
                    Intent intent = new Intent(this, GroupManagerActivity.class);
                    intent.putExtra("group", groupInfo);
                    startActivityForResult(intent, 2);
                }
                break;
            case R.id.sharemore_sight:
                //对讲
                if (SingleTalkActivity.hasTalk || GroupChatUtil.getInstance().isCalling()) {
                    include_fuction.setVisibility(View.GONE);
                    return;
                }
                if (type == 0) {

                } else {
                    Intent intent = new Intent(ChatActivity.this, RobTalkActivity.class);
                    intent.putExtra("group", groupInfo);
                    startActivity(intent);
                }
                include_fuction.setVisibility(View.GONE);
                break;
            case R.id.teamViewer:
                // 远程协助
                Intent intent = new Intent(ChatActivity.this, SendVideoActivity.class);
                intent.putExtra("group", groupInfo);
                startActivity(intent);
                break;
            case R.id.strong_pull:
                //强拉模式
                if (groupInfo != null) {
                    Intent intentStrong = new Intent(this, StrongPullTalkActivity.class);
                    intentStrong.putExtra("group", groupInfo);
                    startActivityForResult(intentStrong, 2);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (requestCode == SELECT_PIC && data != null) {
                        List<LocalMedia> mlist = (List<LocalMedia>) data.getSerializableExtra("pic");
                        updataImage(mlist);
                    }
                    break;
                case 1:
                    final double latitude = data.getDoubleExtra("latitude", 0);
                    final double longitude = data.getDoubleExtra("longitude", 0);
                    final String locationAddress = data.getStringExtra("address");
                    final String paths = data.getStringExtra("path");
                    final double h = longitude;
                    final double l = latitude;
                    Bitmap bitmap = CameraUtil.getImageThumbnail(paths, 150, 90);
                    String imageString = CameraUtil.convertIconToString(bitmap);
                    sendContent = h + "-" + l + "-" + locationAddress + "-" + imageString;
                    sendMessage(4, 0);
                    break;
                case 2:
                    groupInfo = (GroupInfo) data.getSerializableExtra("groups");
                    searchMeeage();
                    break;
                default:
                    break;
            }
        }
        if (resultCode == 100) {
            List<LocalMedia> mlist = (List<LocalMedia>) data.getSerializableExtra("video");
            updateVideo(mlist);
        }
    }

    /**
     * 上传照片
     *
     * @param mlist
     */
    private void updataImage(List<LocalMedia> mlist) {
        for (LocalMedia localMedia : mlist) {
            int id = WMFileLoadSdk.getInstance().WM_VFile_UploadBigFile(localMedia.getPath());
            long nowtime = new Date().getTime();
            addSendMessage(2, nowtime, id, localMedia.getPath());
        }
    }

    /**
     * 上传视频
     *
     * @param list
     */
    private void updateVideo(List<LocalMedia> list) {
        for (LocalMedia media : list) {
            int id = WMFileLoadSdk.getInstance().WM_VFile_UploadBigFile(media.getPath());
            IMMessage imMessage = new IMMessage();
            imMessage.setSendState(1);
            long nowtime = new Date().getTime();
            addSendMessage(3, nowtime, id, media.getPath());
        }
    }

    private boolean isCalling;


    private void isCalling(CallStatus callStatus) {
        if (isCalling) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setTitle("title")
                    .setMessage("确定要加入吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            call_number.setVisibility(View.GONE);
                            int callType = com.wm.Constants.WM_GetCallType(callStatus.getCalltype());
                            if (callType == com.wm.Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice) {
                                TalkRecieve talkRecieve = new TalkRecieve();
                                talkRecieve.setChatid(callStatus.getCallid());
                                talkRecieve.setChattype(callStatus.getCalltype());
                                talkRecieve.setJoin(true);
                                Intent groupintent = new Intent(ChatActivity.this, GroupTalkActivity.class);
                                groupintent.putExtra("receive", talkRecieve);
                                startActivity(groupintent);
                            }
                            if (callType == com.wm.Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice_RobMic) {
                                TalkRecieve talkRecieve = new TalkRecieve();
                                talkRecieve.setChatid(callStatus.getCallid());
                                talkRecieve.setChattype(callStatus.getCalltype());
                                talkRecieve.setJoin(true);
                                Intent groupintent = new Intent(ChatActivity.this, RobTalkActivity.class);
                                groupintent.putExtra("receive", talkRecieve);
                                startActivity(groupintent);
                            }
                            if (callType == 12) {
                                TalkRecieve talkRecieve = new TalkRecieve();
                                talkRecieve.setChatid(callStatus.getCallid());
                                talkRecieve.setChattype(callStatus.getCalltype());
                                talkRecieve.setJoin(true);
                                Intent groupintent = new Intent(ChatActivity.this, SendVideoActivity.class);
                                groupintent.putExtra("receive", talkRecieve);
                                startActivity(groupintent);
                            }
                            if (callType == 13) {
                                TalkRecieve talkRecieve = new TalkRecieve();
                                talkRecieve.setChatid(callStatus.getCallid());
                                talkRecieve.setJoin(true);
                                talkRecieve.setChattype(callStatus.getCalltype());
                                Intent groupintent = new Intent(ChatActivity.this, StrongPullTalkActivity.class);
                                groupintent.putExtra("receive", talkRecieve);
                                startActivity(groupintent);
                            }
                            if (callType == 14) {
                                TalkRecieve talkRecieve = new TalkRecieve();
                                talkRecieve.setChatid(callStatus.getCallid());
                                talkRecieve.setJoin(true);
                                talkRecieve.setChattype(callStatus.getCalltype());
                                Intent groupintent = new Intent(ChatActivity.this, TrailRobActivity.class);
                                groupintent.putExtra("receive", talkRecieve);
                                startActivity(groupintent);
                            }
                            isCalling = false;
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * 发送消息
     *
     * @param messageType 消息的类型  0-文本，1-录音，2-图片，3-视频，4-位置
     * @param isResend    是否是重复发送 0不是 1是的
     */
    private void sendMessage(int messageType, int isResend) {
        if (isResend == 0) {
            nowTime = new Date().getTime();
            addSendMessage(messageType, nowTime, 0, "");
            if (messageType == 0) {
                sendContent = edit.getText().toString();
            } else if (messageType == 1) {
                byte[] file = FileUtil.File2byte(voicePath);
                sendContent = Base64.encodeToString(file, Base64.DEFAULT);
            }
        }
        String url = "http://" + bean.getImsvrip() + ":" + bean.getImsvrhttpport() + "/?";
        int userid = bean.getAccountid();
        String sendType = (type == 0) ? "dstuserid" : "groupid";
        String msgid = (type == 0) ? "257" : "258";
        int dstuserid = (type == 0) ? account.getId() : groupInfo.getId();
        OkHttpUtils
                .post()
                .url(url)
                .addParams("msgid", msgid)
                .addParams("userid", userid + "")
                .addParams(sendType, dstuserid + "")
                .addParams("type", messageType + "")
                .addParams("seq", nowTime + "")
                .addParams("content", sendContent)
                .addParams("resend", 0 + "")
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int i) {
                EventBus.getDefault().post(new HttpEvent(-1, ""));
            }

            @Override
            public void onResponse(String s, int i) {
                EventBus.getDefault().post(new HttpEvent(0, s));
            }
        });
    }

    /**
     * 判断当前群内是否有人聊天
     */
    private void isTalking() {
        GroupChatUtil.getInstance().hasGroupChat(groupInfo.getId(), new GroupChatUtil.HasChatListener() {
            @Override
            public void HasChat(int size, int type, int chatId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call_number.setVisibility(View.VISIBLE);
                        tv_callnumber.setText("当前正有" + size + "人通话中");
                        call_number.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                builder.setTitle("title")
                                        .setMessage("确定要加入吗？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                call_number.setVisibility(View.GONE);
                                                int callType = com.wm.Constants.WM_GetCallType(type);
                                                if (callType == com.wm.Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice) {
                                                    TalkRecieve talkRecieve = new TalkRecieve();
                                                    talkRecieve.setChatid(chatId);
                                                    talkRecieve.setChattype(type);
                                                    talkRecieve.setJoin(true);
                                                    Intent groupintent = new Intent(ChatActivity.this, GroupTalkActivity.class);
                                                    groupintent.putExtra("receive", talkRecieve);
                                                    startActivity(groupintent);
                                                }
                                                if (callType == com.wm.Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice_RobMic) {
                                                    TalkRecieve talkRecieve = new TalkRecieve();
                                                    talkRecieve.setChatid(chatId);
                                                    talkRecieve.setChattype(type);
                                                    talkRecieve.setJoin(true);
                                                    Intent groupintent = new Intent(ChatActivity.this, RobTalkActivity.class);
                                                    groupintent.putExtra("receive", talkRecieve);
                                                    startActivity(groupintent);
                                                }
                                                if (callType == 12) {
                                                    TalkRecieve talkRecieve = new TalkRecieve();
                                                    talkRecieve.setChatid(chatId);
                                                    talkRecieve.setChattype(type);
                                                    talkRecieve.setJoin(true);
                                                    Intent groupintent = new Intent(ChatActivity.this, SendVideoActivity.class);
                                                    groupintent.putExtra("receive", talkRecieve);
                                                    startActivity(groupintent);
                                                }
                                                if (callType == 13) {
                                                    TalkRecieve talkRecieve = new TalkRecieve();
                                                    talkRecieve.setChatid(chatId);
                                                    talkRecieve.setJoin(true);
                                                    talkRecieve.setChattype(type);
                                                    Intent groupintent = new Intent(ChatActivity.this, StrongPullTalkActivity.class);
                                                    groupintent.putExtra("receive", talkRecieve);
                                                    startActivity(groupintent);
                                                }
                                                if (callType == 14) {
                                                    TalkRecieve talkRecieve = new TalkRecieve();
                                                    talkRecieve.setChatid(chatId);
                                                    talkRecieve.setChattype(type);
                                                    talkRecieve.setJoin(true);
                                                    Intent groupintent = new Intent(ChatActivity.this, TrailRobActivity.class);
                                                    groupintent.putExtra("receive", talkRecieve);
                                                    startActivity(groupintent);
                                                }
                                                isCalling = false;
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                builder.create().show();
                            }
                        });
                    }
                });
            }
            @Override
            public void NoChat() {

            }
        });

    }

    public static boolean isFiish = true;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFiish = false;
        getTopName();
        if (type == 1) {
            isTalking();
        }
    }

    @Override
    protected void onStop() {
        if (isMedia) {
            isMedia = false;
            mediaUtil.cancel();
            voice_size_ll.setVisibility(View.GONE);
        }
        KeyboartUtil.hide(this, edit);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveIMMessage(ReceiveMessageEvent receiveMessageEvent) {
        searchMeeage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void httpEvent(HttpEvent event) {
        //发送后接受的返回事件
        if (event.result == -1) {
            Tost("发送失败,请检查网络");
            IMUtil.getInstance().upDataSendState(bean.getAccountid(), nowTime, 2);
        } else {
            try {
                JSONObject object = new JSONObject(event.call);
                int result = object.getInt("result");
                if (result == 0) {
                    IMUtil.getInstance().upDataSendState(bean.getAccountid(), nowTime, 0);
                } else {
                    IMUtil.getInstance().upDataSendState(bean.getAccountid(), nowTime, 2);
                    Tost("发送失败");
                }
            } catch (JSONException e) {
                IMUtil.getInstance().upDataSendState(bean.getAccountid(), nowTime, 2);
            }
        }
        searchMeeage();
    }

    CallStatus callStatus;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isTalkingEvent(IsTalkingEvent event) {
        if (event.result == -1) {
        } else {
            if (!TextUtils.isEmpty(event.s)) {
                Gson gson = new Gson();
                CallIngBack callIngBack = gson.fromJson(event.s, CallIngBack.class);
                if (callIngBack != null && callIngBack.getCallstatus() != null && callIngBack.getCallstatus().size() != 0) {
                    callStatus = callIngBack.getCallstatus().get(0);
                    if (callStatus.getUsercallstatus() != null && callStatus.getUsercallstatus().size() != 0) {
                        isCalling = true;
                        call_number.setVisibility(View.VISIBLE);
                        int size = 0;
                        for (UserStatus userStatus : callStatus.getUsercallstatus()) {
                            if (userStatus.getCallstatus() == 1) {
                                size++;
                            }
                        }
                        tv_callnumber.setText("当前正有" + size + "人通话中");
                        call_number.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isCalling(callStatus);
                            }
                        });
                    }
                }

            } else {
                Log.e("insert", "------无人通话--");
            }
        }
    }

    /**
     * 添加到消息数据库中
     *
     * @param messageType
     * @param nowTime
     */
    private void addSendMessage(int messageType, long nowTime, int loadId, String path) {
        HomeMessage homeMessage = new HomeMessage();
        homeMessage.setUsrId(bean.getAccountid());
        homeMessage.setUserName(SpUtil.getUser().getUserName());
        homeMessage.setTime(nowTime);
        homeMessage.setMessageType(messageType);
        IMMessage message = new IMMessage();
        message.setUserId(bean.getAccountid());
        message.setImType(type);
        if (type == 0) {
            homeMessage.setImType(0);
            message.setImId(account.getId());
            homeMessage.setId(account.getId());
            homeMessage.setName(account.getName());
        } else {
            homeMessage.setImType(1);
            message.setImId(groupInfo.getId());
            homeMessage.setId(groupInfo.getId());
            String groupName = "";
            try {
                groupName = java.net.URLDecoder.decode(groupInfo.getName(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            homeMessage.setName(groupName);
        }
        message.setMeessageTpye(messageType);
        message.setCreateTime(nowTime);
        if (messageType == 0) {
            String msg = edit.getText().toString();
            message.setMessage(msg);
            homeMessage.setContent(msg);
        } else if (messageType == 1) {
            message.setVoicePath(voicePath);
        } else if (messageType == 2) {
            message.setImagePath(path);
            message.setLoadId(loadId);
        } else if (messageType == 3) {
            message.setImagePath(path);
            message.setLoadId(loadId);
        } else if (messageType == 4) {
            message.setMessage(sendContent);
        }
        HomeMessageUtil.getInstance().insertMessage(homeMessage);
        message.setSendState(1);
        IMUtil.getInstance().insertMessage(message);
        EventBus.getDefault().post(new HomeEvent());
        mlist.add(message);
        adapter.notifyDataSetChanged();
        chat_list_recyclerview.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.chat_list_recyclerview) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hintView();
            }
        }
        return false;
    }

    private Timer timer;

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        VoiceUtil.getInstance().stopVoice();
        isFiish = true;
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishOnMessage(FinishEvent finishEvent) {
        if (finishEvent.activity.equals("ChatActivity") && finishEvent.id == -10) {
            finish();
        }
        if (groupInfo != null) {
            if (finishEvent.activity.equals("ChatActivity") && finishEvent.id == groupInfo.getId()) {
                finish();
            }
        }
    }

    private Point point = new Point();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            point.x = (int) ev.getRawX();
            point.y = (int) ev.getRawY();
        }
        return super.dispatchTouchEvent(ev);
    }
}
