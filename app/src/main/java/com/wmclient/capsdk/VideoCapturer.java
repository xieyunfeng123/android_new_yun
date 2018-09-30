package com.wmclient.capsdk;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoCapturer implements Camera.PreviewCallback, SurfaceHolder.Callback
{
    public static final int mFrameRate = 8;
    public static final int mMaxFrameRate = 60;
    // 640 480
    public static final int mDisWidth = 240;
    public static final int mDisHight = 320;
    public static final int mCapWidth = mDisHight;
    public static final int mCapHight = mDisWidth;
    public static final int mBitRate = 300 * 1024;
    private int nDiffTime = 1000 / mFrameRate; // ms
    private int nOffTime = 1000 / mMaxFrameRate;
    //
    private Camera mCamera = null;
    private SurfaceHolder mSurfaceHolder = null;
    private boolean mCameraPreview = false;
    private VideoEncoder mVideoEncoder = new VideoEncoder();
    private String TAG = "VideoCapturer";
    //
    private int m_nCurFrameNumber = 0;
    private long m_nLastTime = 0;
    private Handler mHandler = null;
    private long mLastInputTime = 0;
    private boolean mbFront = false;
    private volatile boolean mbNeedStart = false;
    private volatile int mStreamType = 0;
    private int mWidth = mDisWidth;
    private int mHight = mDisHight;
    private int m_nFrameRate = mFrameRate;
    private boolean mbUpload = false;
    private boolean mbStartVideo = false;
	static public boolean mbLandscape = false;
    public VideoCapturer()
    {
    }
    public void setUpload(boolean b)
    {
        mbUpload = b;
    }
    public boolean getUpload()
    {
        return mbUpload;
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        // TODO Auto-generated method stub
        if (null == data)
        {
            return;
        }
        if (!mbNeedStart)
        {
            return;
        }
        if (getUpload()) {
        	try {
        		Camera.Parameters p= mCamera.getParameters();

            	mVideoEncoder.decodeData(data, data.length, p.getPreviewSize().width, 
            			p.getPreviewSize().height, mStreamType, mbFront);
			} catch (Exception e) {
			}
        }
        else {
        }
    }
    public void setSurfaceView(SurfaceHolder holder)
    {
        if (null == holder)
        {
            return;
        }
        holder.addCallback(this);
        mSurfaceHolder = holder;
    }
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
    public boolean startVideo(int nWidth, int nHight, int nFrameRate, int nBitRate, boolean bFront, Handler handler)
    {
        //
        mHandler = handler;
        if (!mVideoEncoder.isStart())
        {
            mVideoEncoder.startEncoder(nWidth, nHight, nFrameRate, nBitRate);
        }
        int nCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        if (bFront)
        {
            nCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else
        {
            nCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        //
        try
        {
            if (mCamera == null)
            {
                mCamera = Camera.open(nCameraFacing);
            }
//            else
//            {
////                mCamera.setPreviewCallback(null);
////                mCamera.stopPreview();//停掉原来摄像头的预览
////                mCamera.release();//释放资源
////                mCamera = null;//取消原来摄像头
////                stopVideo();
//                mCamera = Camera.open(nCameraFacing);//打开当前选中的摄像头
//            }
            
            if (mCamera == null)
            {
                mVideoEncoder.stopEncoder();
                return false;
            }
            Camera.Parameters p = mCamera.getParameters();            
            p.setPreviewSize(nHight, nWidth);
            if(!bFront){
            	p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            p.setPreviewFormat(ImageFormat.NV21);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewCallback(this);
            mCamera.setParameters(p);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }
        catch (Exception e)
        {
            mVideoEncoder.stopEncoder();
            return false;
        }
        mCameraPreview = true;
        mbFront = bFront;
        mWidth = nWidth;
        mHight = nHight;
        m_nFrameRate = nFrameRate;
        mbStartVideo = true;
        return true;
    }
    public void stopVideo()
    {
        if (mCamera != null)
        {
            if (mCameraPreview)
            {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCameraPreview = false;
            }
            mCamera.release();
            mCamera = null;
        }
        mVideoEncoder.stopEncoder();
        mbStartVideo = false;
    }
    public void setStartStatus(boolean bStart)
    {
        mbNeedStart = bStart;
    }
    public boolean getStartStatus()
    {
        return mbNeedStart;
    }
    public int startRealPlay(int nStreamType, int nMark)
    {
        setStartStatus(true);
        mStreamType = nStreamType;

        Log.d("cap_talk", "VideoCapturer startRealPlay, mStreamType="+mStreamType);
        return Api.Api_Code_Result_OK;
    }
    public int stopRealPlay(int nStreamType, int nMark)
    {
        setStartStatus(false);
        return Api.Api_Code_Result_OK;
    }
    public void stopAllRealPlay()
    {
        setStartStatus(false);
    }
    public boolean CheckIn(long nCurrentTime)
    {
        if (mLastInputTime <= 0)
        {
            mLastInputTime = nCurrentTime;
            return true;
        }
        else
        {
            long nDiff = mLastInputTime + nDiffTime - nCurrentTime;
            if (nDiff > nOffTime)
            {
                return false;
            }
            else if (nDiff < -nOffTime)
            {
                mLastInputTime = nCurrentTime;
            }
            else
            {
                mLastInputTime += nDiffTime;
            }
        }
        return true;
    }
    @SuppressWarnings("unused")
    private void PostMsg(int nMessageId, int arg1, int arg2, Object object)
    {
        if (null == mHandler)
        {
            return;
        }
        Message msg = new Message();
        msg.what = nMessageId;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = object;
        mHandler.sendMessage(msg);
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        if (mbStartVideo && null == mCamera)
        {
            Log.d("holder", holder.toString());
            startVideo(mWidth, mHight, m_nFrameRate, VideoCapturer.mBitRate, true, null);
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        stopVideo();
    }
}
