package com.parizene.streamer;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Environment;

public class Streamer implements PreviewCallback {
    private static final String TAG = "Streamer";

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final int FRAME_RATE = 15;
    
    private boolean mIsStarted;
    private byte[] mData;
    
    public void start(final String addr) {
        init(Environment.getExternalStorageDirectory() + "/streamer.h264", WIDTH, HEIGHT, FRAME_RATE);
        mIsStarted = true;
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                loop(addr);
            }
        }).start();
    }
    
    public void stop() {
        mIsStarted = false;
        deinit();
    }
    
    public boolean isStarted() {
        return mIsStarted;
    }

    private native void init(String filename, int width, int height, int frameRate);
    private native void encode(byte[] data);
    private native void deinit();
    private native void loop(String addr);

    public int frameCount = 0;
    public long lastTimestamp = System.currentTimeMillis();
    
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mIsStarted) {
        	encode(data);
        }
        
//        frameCount++;
//        
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastTimestamp > 1000) {
//            lastTimestamp = currentTime;
//            Log.d(TAG, "onPreviewFrame() frames=" + frameCount + ", bytes=" + data.length);
//            
//            frameCount = 0;
//        }
    }
}
