package com.wmclient.capsdk;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.wmclient.capsdk.SpeexEncoder.OnEncodeDataCallBack;

public class AudioCapturer implements OnEncodeDataCallBack
{
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static final int mSampleRate = 8000;
    public static final int mChannels = 1;
    public static final int mAudiobitRate = mSampleRate * mChannels * 200 * 8 / 1000;
    // 音频获取源
    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    @SuppressWarnings("unused")
    private static int mChannelConfig = (mChannels == 1) ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
    private AudioRecord mAudioRecord = null;
    private boolean mbRecord = false;
    private volatile boolean mbStart = false;
    private volatile int mStreamType = 0;
    private String TAG = "AudioCapturer";
    private int mFrameSize = 160;
    private boolean mbUpload = false;
    private boolean mbStartAudio = false;
    // speex
    private SpeexEncoder mSpeexEncoder = new SpeexEncoder();
    public AudioCapturer()
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
    public boolean startAudio(int samplingRate, int nChannels, int nbitRate)
    {
        Log.i(TAG, "startAudio");
        return startSpeexCoded(samplingRate, nChannels, nbitRate);
    }
    public void stopAudio()
    {
        stopSpeexCoded();
    }
    //
    private boolean startRecord()
    {
        // 创建AudioRecord对象
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
        if (null == mAudioRecord)
        {
            Log.e(TAG, "AudioRecord init fail, mSampleRate:" + mSampleRate + " mChannelConfig:" + mChannelConfig + " mAudioFormat:" + mAudioFormat + " mBufferSizeInBytes:%d" + mBufferSizeInBytes);
            return false;
        }
        mbRecord = true;
        mAudioRecord.startRecording();
        new Thread(new AudioRecordThread()).start();
        return true;
    }
    private void stopRecord()
    {
        if (mAudioRecord != null)
        {
            mbRecord = false;
            mAudioRecord.stop();
            mAudioRecord.release();// 释放资源
            mAudioRecord = null;
        }
    }
    
    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     */
    class AudioRecordThread implements Runnable
    {
        @Override
        public void run()
        {
            short[] audiodata = new short[mFrameSize];
            while (mbRecord)
            {
                int readsize = mAudioRecord.read(audiodata, 0, audiodata.length);
                if (readsize > 0 && null != mSpeexEncoder && mbUpload && mbStart)
                {
                    long curTime = System.currentTimeMillis();
                    mSpeexEncoder.putRecordData(audiodata, audiodata.length, curTime * 1000);
                }
            }
        }
    }
    public void AudioTrackData(short[] data, int nSize, long pts)
    {
        if (null != mSpeexEncoder)
        {
            mSpeexEncoder.putTrackData(data, nSize, pts);
        }
    }
    private boolean startSpeexCoded(int samplingRate, int nChannels, int nbitRate)
    {
        mSpeexEncoder.setEncodeDataCallBack(this);
        if (!mSpeexEncoder.startEncoder(samplingRate, nChannels, nbitRate))
        {
            Log.e(TAG, "startEncoder fail");
            return false;
        }
        if (!startRecord())
        {
            mSpeexEncoder.stopEncoder();
            ;
            mFrameSize = 0;
            
            return false;
        }
        Thread aecThread = new Thread(mSpeexEncoder);
        mSpeexEncoder.setRecording(true);
        aecThread.start();
        return true;
    }
    private void stopSpeexCoded()
    {
        stopRecord();
        mSpeexEncoder.setEncodeDataCallBack(null);
        
        if (null != mSpeexEncoder)
        {
            mSpeexEncoder.setRecording(false);
            mSpeexEncoder.stopEncoder();
        }
    }
    public int startRealPlay(int nStreamType, int nMark)
    {
        mbStart = true;
        mStreamType = nStreamType;
        return Api.Api_Code_Result_OK;
    }
    public int stopRealPlay(int nStreamType, int nMark)
    {
        mbStart = false;
        return Api.Api_Code_Result_OK;
    }
    public void stopAllRealPlay()
    {
        mbStart = false;
    }
    @Override
    public boolean OnEncodeDataCallBack(byte[] data, int nSize, long nPts)
    {
        // TODO Auto-generated method stub
        if (nSize > 0 && getUpload())
        {
            WMCapSdk.getInstance().InputData(mStreamType, Api.Api_Code_DataType_Speex, 0, data, nSize, nPts);
        }
        return true;
    }
}
