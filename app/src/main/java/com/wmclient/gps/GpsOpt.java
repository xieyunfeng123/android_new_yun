package com.wmclient.gps;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;

@SuppressLint("HandlerLeak")
public class GpsOpt implements Runnable
{
    
    private static GpsOpt sIns = null;
    
    private Context mContext = null;
    
    private LocationManager mLocationManager = null;
    
    private boolean bInit = false;
    
    private volatile boolean mIsStarting;
    
    private final byte[] mLockStatus = new byte[0];
    
    private String TAG = "";
    
    public interface OnLocationChangedListener
    {
        boolean OnLocationChangedCallBack(Location location);
    }
    
    private OnLocationChangedListener mOnLocationChangedListener = null;
    
    public void SetLocationChangedListener(OnLocationChangedListener listener)
    {
        mOnLocationChangedListener = listener;
    }
    
    private LocationListener mLocationListener = new LocationListener()
    {
        
        public void onLocationChanged(Location location)
        {
            if (null != mOnLocationChangedListener)
            {
                mOnLocationChangedListener.OnLocationChangedCallBack(location);
            }
        }
        
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            switch (status)
            {
            
                case LocationProvider.AVAILABLE:
                    
                    break;
                
                case LocationProvider.OUT_OF_SERVICE:
                    
                    break;
                
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    
                    break;
            }
        }
        
        /**
         * GPS寮�惎鏃惰Е鍙�
         */
        @SuppressWarnings("unused")
        public void onProviderEnabled(String provider)
        {
            Location location = mLocationManager.getLastKnownLocation(provider);
            int i = 0;
            i++;
            // updateView(location);
        }
        
        /**
         * GPS绂佺敤鏃惰Е鍙�
         */
        public void onProviderDisabled(String provider)
        {
            // updateView(null);
        }
        
    };
    
    // 鐘舵�鐩戝惉
    private GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener()
    {
        @SuppressWarnings("unused")
        public void onGpsStatusChanged(int event)
        {
            switch (event)
            {
            // 绗竴娆″畾浣�
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    
                    break;
                // 鍗槦鐘舵�鏀瑰彉
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "鍗槦鐘舵�鏀瑰彉");
                    // 鑾峰彇褰撳墠鐘舵�
                    GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                    // 鑾峰彇鍗槦棰楁暟鐨勯粯璁ゆ渶澶у�
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 鍒涘缓涓�釜杩唬鍣ㄤ繚瀛樻墍鏈夊崼鏄�
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites)
                    {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    
                    break;
                // 瀹氫綅鍚姩
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "瀹氫綅鍚姩");
                    break;
                // 瀹氫綅缁撴潫
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "瀹氫綅缁撴潫");
                    break;
            }
        };
    };
    
    public static GpsOpt getInstance()
    {
        if (sIns == null)
            sIns = new GpsOpt();
        
        return sIns;
        
    }
    
    private GpsOpt()
    {
        mContext = ContextOpt.getInstance().getAppContext();
        // 閫氳繃getSystemService鎺ュ彛鑾峰彇LocationManager瀹炰緥
        mLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public boolean initGps()
    {
        
        // 鍒ゆ柇GPS鏄惁姝ｅ父鍚姩
        if (!isGpsEnable())
        {
            Toast.makeText(mContext, "璇峰紑鍚疓PS瀵艰埅...", Toast.LENGTH_SHORT).show();
            // 杩斿洖寮�惎GPS瀵艰埅璁剧疆鐣岄潰
            enableGps();
            return false;
        }
        
        if (bInit)
        {
            return true;
        }
        
        // 鐩戝惉鐘舵�
        mLocationManager.addGpsStatusListener(mGpsStatusListener);
        // 缁戝畾鐩戝惉锛屾湁4涓弬鏁�
        // 鍙傛暟1锛岃澶囷細鏈塆PS_PROVIDER鍜孨ETWORK_PROVIDER涓ょ
        // 鍙傛暟2锛屼綅缃俊鎭洿鏂板懆鏈燂紝鍗曚綅姣
        // 鍙傛暟3锛屼綅缃彉鍖栨渶灏忚窛绂伙細褰撲綅缃窛绂诲彉鍖栬秴杩囨鍊兼椂锛屽皢鏇存柊浣嶇疆淇℃伅
        // 鍙傛暟4锛岀洃鍚�
        // 澶囨敞锛氬弬鏁�鍜�锛屽鏋滃弬鏁�涓嶄负0锛屽垯浠ュ弬鏁�涓哄噯锛涘弬鏁�涓�锛屽垯閫氳繃鏃堕棿鏉ュ畾鏃舵洿鏂帮紱涓よ�涓�锛屽垯闅忔椂鍒锋柊
        
        // 1绉掓洿鏂颁竴娆★紝鎴栨渶灏忎綅绉诲彉鍖栬秴杩�绫虫洿鏂颁竴娆★紱
        // 娉ㄦ剰锛氭澶勬洿鏂板噯纭害闈炲父浣庯紝鎺ㄨ崘鍦╯ervice閲岄潰鍚姩涓�釜Thread锛屽湪run涓璼leep(10000);鐒跺悗鎵цhandler.sendMessage(),鏇存柊浣嶇疆
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, mLocationListener);
        
        bInit = true;
        
        return true;
    }
    
    public boolean isGpsEnable()
    {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    public void enableGps()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            mContext.startActivity(intent);
        }
        catch (ActivityNotFoundException ex)
        {
            
            // The Android SDK doc says that the location settings activity
            // may not be found. In that case show the general settings.
            
            // General settings activity
            intent.setAction(Settings.ACTION_SETTINGS);
            try
            {
                mContext.startActivity(intent);
            }
            catch (Exception e)
            {
            }
        }
    }
    
    public Location getCurrentLocation()
    {
        // 涓鸿幏鍙栧湴鐞嗕綅缃俊鎭椂璁剧疆鏌ヨ鏉′欢
        String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
        // 鑾峰彇浣嶇疆淇℃伅
        // 濡傛灉涓嶈缃煡璇㈣姹傦紝getLastKnownLocation鏂规硶浼犱汉鐨勫弬鏁颁负LocationManager.GPS_PROVIDER
        // Location location= lm.getLastKnownLocation(bestProvider);
        
        // Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location = mLocationManager.getLastKnownLocation(bestProvider);
        return location;
    }
    
    public Location getCurrentGps()
    {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        return location;
    }
    
    /**
     * 杩斿洖鏌ヨ鏉′欢
     * 
     * @return
     */
    private Criteria getCriteria()
    {
        Criteria criteria = new Criteria();
        // 璁剧疆瀹氫綅绮剧‘搴�Criteria.ACCURACY_COARSE姣旇緝绮楃暐锛孋riteria.ACCURACY_FINE鍒欐瘮杈冪簿缁�
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 璁剧疆鏄惁瑕佹眰閫熷害
        criteria.setSpeedRequired(false);
        // 璁剧疆鏄惁鍏佽杩愯惀鍟嗘敹璐�
        criteria.setCostAllowed(false);
        // 璁剧疆鏄惁闇�鏂逛綅淇℃伅
        criteria.setBearingRequired(false);
        // 璁剧疆鏄惁闇�娴锋嫈淇℃伅
        criteria.setAltitudeRequired(false);
        // 璁剧疆瀵圭數婧愮殑闇�眰
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
    
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, mLocationListener);
            super.handleMessage(msg);
        }
    };
    
    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        while (getStatus())
        {
            Message msg = new Message();
            mHandler.sendMessage(msg);
            
            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void setStatus(boolean isStarting)
    {
        synchronized (mLockStatus)
        {
            this.mIsStarting = isStarting;
            if (this.mIsStarting)
            {
                mLockStatus.notify();
            }
        }
    }
    
    public boolean getStatus()
    {
        synchronized (mLockStatus)
        {
            return mIsStarting;
        }
    }
}
