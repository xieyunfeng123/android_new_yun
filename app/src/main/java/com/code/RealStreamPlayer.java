package com.code;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;

@SuppressLint("SimpleDateFormat")
public class RealStreamPlayer
{
//    private int playerId = Constants.WMPLAYERID_INVALID;
//    private boolean isRecording;
//    private boolean isStoped = true;
//    private PlayRunnable pendingPlayRunnable;
//    private StreamPlayer currentPlayer;
//    private Context context;
//    private PlayListener playListener;
//    public interface PlayListener
//    {
//        void onPlaySuccess();
//        void onPlayFailed();
//    }
//    public void addPlayListener(PlayListener l)
//    {
//        this.playListener = l;
//    }
//    public RealStreamPlayer(Context context)
//    {
//        this.context = context;
//    }
//    public void startPlay(final int deviceId, final int channelId, final StreamPlayer player)
//    {
//        if (!isStoped)
//        {
//            DebugLogger.i("is not stoped, add pending runnable");
//            pendingPlayRunnable = new PlayRunnable(deviceId, channelId, player);
//            return;
//        }
//        new Thread(new PlayRunnable(deviceId, channelId, player)).start();
//    }
//    public void destroyPlayer(final StreamPlayer player)
//    {
////        ClientPlay.getInstance().GetSdkInterface().DestroyPlayer(player);
//    }
//
//    class PlayRunnable implements Runnable
//    {
//        int deviceId;
//        int channelId;
//        StreamPlayer player;
//        public PlayRunnable(int deviceId, int channelId, StreamPlayer player)
//        {
//            this.deviceId = deviceId;
//            this.channelId = channelId;
//            this.player = player;
//        }
//        @Override
//        public void run()
//        {
//            try
//            {
//                isStoped = false;
//                DebugLogger.i("start play in sub thread");
////                playerId = ClientPlay.getInstance().GetSdkInterface().startRealPlay(deviceId, channelId, player, Constants.STREAM_TYPE_SUB);
//                currentPlayer = player;
//                DebugLogger.i("start play:" + deviceId + ":" + channelId + "  playId:" + playerId + ":" + player);
//                if (playerId == Constants.WMPLAYERID_INVALID)
//                {
//                    isStoped = true;
//                    notifyFailed();
//                    return;
//                }
//                notifySuccess();
//                // wait for stop
//                synchronized (RealStreamPlayer.this)
//                {
//                    RealStreamPlayer.this.wait();
//                }
//                DebugLogger.i("stop in sub thread start:" + playerId);
////                ClientPlay.getInstance().GetSdkInterface().stopRealPlay(playerId);
//                destroyPlayer(player);
//                DebugLogger.i("stop in sub thread end:" + playerId);
//                if (pendingPlayRunnable != null)
//                {
//                    DebugLogger.i("pending runnable running");
//                    new Thread(pendingPlayRunnable).start();
//                    pendingPlayRunnable = null;
//                }
//                isStoped = true;
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//    public void stopPlay()
//    {
//        if (playerId == Constants.WMPLAYERID_INVALID)
//        {
//            DebugLogger.i("playId is wrong, just return!" + playerId);
//            return;
//        }
//        synchronized (this)
//        {
//            notify();
//        }
//    }
//    public boolean hasStoped()
//    {
//        return isStoped;
//    }
////    public void openSound()
////    {
////        ClientPlay.getInstance().GetSdkInterface().openSound(playerId);
////    }
////    public void clonseSound()
////    {
////        ClientPlay.getInstance().GetSdkInterface().closeSound(playerId);
////    }
//    public void captureImage(Activity context, String deviceName, int channelId)
//    {
//        if (playerId == Constants.WMPLAYERID_INVALID)
//        {
//            return;
//        }
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//        {
//            toast(context, "无效的输入!");
//            return;
//        }
////        String directoryName = ClientPlay.getInstance().getCaptureImagePath();
////        File directory = new File(directoryName);
////        if (!directory.exists() && !directory.mkdir())
////        {
////            toast(context, "创建存储目录失败!");
////            return;
////        }
////        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
////        String fileName = directoryName + File.separator + deviceName + "-" + channelId + "-" + sDateFormat.format(new java.util.Date());
////        if (Constants.success == ClientPlay.getInstance().GetSdkInterface().saveSnapshot(playerId, fileName))
////        {
////            toast(context, "已截图，图片保存路径：" + fileName + ".jpg");
////            // toast(context, "截图成功!");
////            Log.d("insert", "已截图，图片保存路径：" + fileName + ".jpg");
////        }
//    }
//    public void toast(final Activity activity, final String str)
//    {
//        activity.runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                // ContextToast.show(activity, str, Toast.LENGTH_SHORT);
//            }
//        });
//    }
//    public void recordVedio(Activity context, String deviceName, int channelId)
//    {
//        if (playerId == Constants.WMPLAYERID_INVALID)
//        {
//            toast(context, "无效的输入!");
//            return;
//        }
//        if (!isRecording)
//        {
//            DebugLogger.i("start recording");
//            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//            {
//                toast(context, "无效的输入!");
//                return;
//            }
////            String directoryName = ClientPlay.getInstance().getVideoPath();
////            File directory = new File(directoryName);
////            if (!directory.exists() && !directory.mkdir())
////            {
////                toast(context, "创建存储目录失败!");
////                return;
////            }
////            SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd-hh-mm-ss");
////            String fileName = directoryName + File.separator + deviceName + "-" + channelId + "-" + sDateFormat.format(new java.util.Date());
////            int nRet = ClientPlay.getInstance().GetSdkInterface().startRecord(playerId, fileName);
////            if (nRet != Constants.success)
////            {
////                toast(context, "开始录像失败!");
////                return;
////            }
////            else
////            {
////                toast(context, "正在开始录像...");
////            }
//            isRecording = true;
//        }
//        else
//        {
//            DebugLogger.i("stop recording");
////            int nRet = ClientPlay.getInstance().GetSdkInterface().stopRecord(playerId);
////            if (nRet != Constants.success)
////            {
////                toast(context, "停止录像失败!");
////                return;
////            }
////            else
////            {
////                toast(context, "已停止录像");
////            }
//            isRecording = false;
//        }
//    }
//    public int getPlayTime()
//    {
//        if (currentPlayer == null)
//        {
//            return 0;
//        }
//        return currentPlayer.GetPlayTime();
//    }
//    public long getAllRecvLen()
//    {
//        if (currentPlayer == null)
//        {
//            return 0;
//        }
//        return currentPlayer.GetAllRecvLen();
//    }
//    public int getReate()
//    {
//        if (currentPlayer == null)
//        {
//            return 0;
//        }
//        return currentPlayer.GetCodeRate();
//    }
//    public void notifyFailed()
//    {
//        if (playListener != null)
//        {
//            playListener.onPlayFailed();
//        }
//    }
//    public void notifySuccess()
//    {
//        if (playListener != null)
//        {
//            playListener.onPlaySuccess();
//        }
//    }
//    public int startVoiceTalk(int deviceId, int channelId, AudioCaptureCallBack callBack)
//    {
//        if (currentPlayer == null)
//        {
//            return Constants.fail;
//        }
//
////        int nRet = ClientPlay.getInstance().GetSdkInterface().StartVoiceTalk(deviceId, channelId, playerId);
////        if (nRet != Constants.success)
////        {
////            return nRet;
////        }
////        nRet = currentPlayer.StartVoiceTalk(callBack);
////        if (nRet != Constants.success)
////        {
////            ClientPlay.getInstance().GetSdkInterface().StopVoiceTalk(deviceId, channelId, playerId);
////            return Constants.fail;
////        }
//        return Constants.success;
//    }
//    public int stopVoiceTalk(int deviceId, int channelId)
//    {
//        if (currentPlayer == null)
//        {
//            return Constants.fail;
//        }
//        currentPlayer.StopVoiceTalk();
////        ClientPlay.getInstance().GetSdkInterface().StopVoiceTalk(deviceId, channelId, playerId);
//        return Constants.success;
//    }
}
