package com.wmclient.capsdk;

import android.content.Context;
import android.location.Location;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.gson.Gson;
import com.wmclient.gps.ContextOpt;
import com.wmclient.gps.GpsOpt;
import com.wmclient.gps.GpsOpt.OnLocationChangedListener;

public class WMCapSdk implements OnLocationChangedListener
{
    private static WMCapSdk m_sdkinstance = new WMCapSdk();
    private Api m_api = new Api();
    private VideoCapturer mVideoCapturer = new VideoCapturer();
    private AudioCapturer mAudioCapturer = new AudioCapturer();
    private Context mContext = null;
    private String mImei = null;
    private ConfigInfo mConfigInfo = null;
    public static WMCapSdk getInstance()
    {
        return m_sdkinstance;
    }
    public int Init(Context context, int nLogLevel)
    {
        TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        mImei = "a_" + TelephonyMgr.getDeviceId();
        int nResult = m_api.Init(nLogLevel);
        if (Api.Api_Code_Result_OK != nResult)
        {
            return nResult;
        }
        m_api.SetStatusCB(this);
        m_api.SetRealPlayCB(this);
        m_api.SetStopRealPlayCB(this);
        mContext = context;
        return nResult;
    }
    public String GetDeviceId()
    {
        return mImei;
    }
    public int Uninit()
    {
        mVideoCapturer.stopAllRealPlay();
        mVideoCapturer.stopVideo();
        mAudioCapturer.stopAllRealPlay();
        mAudioCapturer.stopAudio();
        m_api.Stop();
        return m_api.Uninit();
    }
    public int SetConfigInfo(String mUserName, String mPassWord, String mSvrName, int mSvrType, String mSvrIP, int mSvrPort, int nWidth, int nHight, int nFrameRate, int nSampleRate, int nChannels)
    {
        mConfigInfo = new ConfigInfo(mImei, mUserName, mPassWord, mSvrName, mSvrType, mSvrIP, mSvrPort);
        mConfigInfo.mWidth = nWidth;
        mConfigInfo.mHight = nHight;
        mConfigInfo.mFrameRate = nFrameRate;
        mConfigInfo.mSampleRate = nSampleRate;
        mConfigInfo.mChannels = nChannels;
        Gson gson = new Gson();
        String jsonString = gson.toJson(mConfigInfo, ConfigInfo.class);
        return m_api.SetConfigInfo(jsonString);
    }
    public int Start()
    {
        int  nResult = m_api.Start();
        if (Api.Api_Code_Result_OK != nResult)
        {
            return nResult;
        }
        return nResult;
    }
    public int Stop()
    {
        return m_api.Stop();
    }
    public int StartCapturer(SurfaceHolder holder, boolean bFront)
    {
        //
        mVideoCapturer.setUpload(false);
        mVideoCapturer.setSurfaceView(holder);
        if (!mVideoCapturer.startVideo(mConfigInfo.mWidth, mConfigInfo.mHight, mConfigInfo.mFrameRate, mConfigInfo.mBitRate, bFront, null))
        {
            mConfigInfo.mHasVideo = 0;
        }
        //
        mVideoCapturer.setUpload(false);
        if (!mAudioCapturer.startAudio(mConfigInfo.mSampleRate, mConfigInfo.mChannels, 0))
        {
            mConfigInfo.mHasAudio = 0;
        }
        if (mConfigInfo.mHasVideo == 0 && mConfigInfo.mHasAudio == 0)
        {
            return Api.Api_Code_Result_Error;
        }
        return Api.Api_Code_Result_OK;
    }
    public int StopCapturer()
    {
        mVideoCapturer.stopVideo();
        mAudioCapturer.stopAudio();
        return Api.Api_Code_Result_OK;
    }
    @SuppressWarnings("unused")
    public boolean OnLocationChangedCallBack(double longitude, double latitude, double altitude)
    {
        int m = 0;
        long nLongitude = (long)(longitude * Api.Api_Code_Gps_Prec);
        long nLatitude = (long)(latitude * Api.Api_Code_Gps_Prec);
        long nAltitude = (long)(altitude * Api.Api_Code_Gps_Prec);
        m = m_api.InputGps(System.currentTimeMillis() * 1000, nLongitude, nLatitude, nAltitude);
        return true;
    }
    public boolean OpenGps()
    {
        if (null == mContext)
        {
            return false;
        }
        ContextOpt.getInstance().setAppContext(mContext);
        GpsOpt.getInstance().SetLocationChangedListener(this);
        if (!GpsOpt.getInstance().initGps())
        {
            return false;
        }
        return true;
    }
    public int InputData(int streamType, int nDataType, int nFrameType, byte[] dataBuf, int nBufSize, long nPts)
    {
        return m_api.InputData(streamType, nDataType, nFrameType, dataBuf, nBufSize, nPts);
    }
    public int setVideoUpload(boolean b)
    {
        if (null == mVideoCapturer)
        {
            return Api.Api_Code_Result_Error;
        }
        mVideoCapturer.setUpload(b);
        return Api.Api_Code_Result_OK;
    }
    public int setAudioUpload(boolean b)
    {
        if (null == mAudioCapturer)
        {
            return Api.Api_Code_Result_Error;
        }
        mAudioCapturer.setUpload(b);
        return Api.Api_Code_Result_OK;
    }
    public int OnStatusCB(int nSatus)
    {
        switch (nSatus)
        {
            case Api.Api_Code_Status_ConnError:
            case Api.Api_Code_Status_RigisterError:
            {
                mVideoCapturer.stopAllRealPlay();
                mAudioCapturer.stopAllRealPlay();
            }
                break;
            default:
                break;
        }
        return 0;
    }
    public RealPlayRet OnRealPlayCB(int nStreamType, int nMark)
    {
        RealPlayRet ret = new RealPlayRet();
        Log.d("cap_talk", "RealPlayRet, streamType="+nStreamType + ", mHasVideo=" +mConfigInfo.mHasVideo
        + ", mHasAudio"+mConfigInfo.mHasAudio);
        if (mConfigInfo.mHasVideo == 0 && mConfigInfo.mHasAudio == 0)
        {
            Log.d("cap_talk", "1");

            return null;
        }
        if (mConfigInfo.mHasVideo != 0)
        {
            if (Api.Api_Code_Result_OK != mVideoCapturer.startRealPlay(nStreamType, nMark))
            {
                return null;
            }
        }
        if (mConfigInfo.mHasAudio != 0)
        {
            if (Api.Api_Code_Result_OK != mAudioCapturer.startRealPlay(nStreamType, nMark))
            {
                mVideoCapturer.stopRealPlay(nStreamType, nMark);
                return null;
            }
        }
        
        ret.mResult = 0;
        ret.mHasVideo = mConfigInfo.mHasVideo;
        ret.mWidth = mConfigInfo.mWidth;
        ret.mHight = mConfigInfo.mHight;
        ret.mFrameRate = mConfigInfo.mFrameRate;
        ret.mBitRate = mConfigInfo.mBitRate;
        ret.mHasAudio = mConfigInfo.mHasAudio;
        ret.mSampleRate = mConfigInfo.mSampleRate;
        ret.mChannels = mConfigInfo.mChannels;
        return ret;
    }
    public int OnStopRealPlayCB(int nStreamType, int nMark)
    {
        mVideoCapturer.stopRealPlay(nStreamType, nMark);
        mAudioCapturer.stopRealPlay(nStreamType, nMark);
        return Api.Api_Code_Result_OK;
    }
    @Override
    public boolean OnLocationChangedCallBack(Location location)
    {
        if (null == location)
        {
            return false;
        }
        long nLongitude = (long)(location.getLongitude() * Api.Api_Code_Gps_Prec);
        long nLatitude = (long)(location.getLatitude() * Api.Api_Code_Gps_Prec);
        long nAltitude = (long)(location.getAltitude() * Api.Api_Code_Gps_Prec);
        m_api.InputGps(System.currentTimeMillis() * 1000, nLongitude, nLatitude, nAltitude);
        return true;
    }
    public boolean IsNeedVideoData()
    {
    	return mVideoCapturer.getStartStatus()&& mVideoCapturer.getUpload();
    }
}
