package com.parizene.streamer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.parizene.streamer.StreamerService.IServiceDestroyListener;

import java.util.ArrayList;
import java.util.List;

public class StreamerApplication extends Application {
    
    public interface OnServiceConnectedListener {
        void onServiceConnected();
    }
    
    private static final String TAG = "StreamerApplication";
    
    public static final boolean DEBUG = true;
    
    private StreamerService mService;
    
    public static Context sContext;

    public static StreamerApplication getInstance() {
        return (StreamerApplication) sContext;
    }
    
    private final List<OnServiceConnectedListener> mListeners = new ArrayList<OnServiceConnectedListener>();
    
    static {
        System.loadLibrary("streamer");
    }
    
    @Override
    public void onCreate() {
        sContext = this;

        if (DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate();
        
        bindService(new Intent(this, StreamerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DEBUG) Log.d(TAG, "onServiceConnected()");
            
            StreamerService.TestBinder binder = (StreamerService.TestBinder) service;
            binder.setListener(mServiceDestroyListener);
            mService = binder.getService();
            
            notifyServiceConnected();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG) Log.d(TAG, "onServiceDisconnected()");
            
            exit();
        }
    };
    
    private IServiceDestroyListener mServiceDestroyListener = new IServiceDestroyListener() {
        
        @Override
        public void onServiceDestroyed() {
            if (DEBUG) Log.d(TAG, "onServiceDestroyed()");

            exit();
        }
    };
    
    private void exit() {
        if (DEBUG) Log.d(TAG, "exit()");
        
        mService = null;
        
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
    
    public void requestExit() {
        unbindService(mServiceConnection);
    }
    
    public StreamerService getService() {
        return mService;
    }
    
    public void setOnServiceConnectedListener(OnServiceConnectedListener listener) {
        if (mService != null) {
            listener.onServiceConnected();
        } else {
            synchronized (mListeners) {
                mListeners.add(listener);
            }
        }
    }
    
    private void notifyServiceConnected() {
        ArrayList<OnServiceConnectedListener> listeners;
        synchronized (mListeners) {
            listeners = new ArrayList<OnServiceConnectedListener>(mListeners);
            mListeners.clear();
        }
        for (OnServiceConnectedListener l : listeners) {
            l.onServiceConnected();
        }
    }
}
