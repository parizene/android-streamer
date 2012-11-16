package com.parizene.streamer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class StreamerService extends Service/* implements SurfaceHolder.Callback */{

    public class TestBinder extends Binder {
        private IServiceDestroyListener mListener;
        
        public StreamerService getService() {
            return StreamerService.this;
        }

        public void setListener(IServiceDestroyListener listener) {
            mListener = listener;
        }
        
        public IServiceDestroyListener getListener() {
            return mListener;
        }
    }
    
    public interface IServiceDestroyListener {
        void onServiceDestroyed();
    }
    
    private static final String TAG = "StreamerService";
    
    private final TestBinder mBinder = new TestBinder();
    
    @Override
    public IBinder onBind(Intent intent) {
        if (StreamerApplication.DEBUG) Log.d(TAG, "onBind()");
        
        return mBinder;
    }
    
//    private Streamer mStreamer;
//    private MediaRecorder mMediaRecorder;
//    
//    private SurfaceView mSurfaceView;
//    private Camera mCamera;
//    private SurfaceHolder mSurfaceHolder;
    
    @Override
    public void onCreate() {
        if (StreamerApplication.DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate();
        
//        mStreamer = new Streamer();
//        new Thread(mStreamer).start();

//        mCamera = getCameraInstance();
//        
//        // don't set mSurfaceHolder here. We have it set ONLY within
//        // surfaceCreated / surfaceDestroyed, other parts of the code
//        // assume that when it is set, the surface is also set.
//        mSurfaceView = new SurfaceView(this);
//        addView(this, mSurfaceView);
//        SurfaceHolder holder = mSurfaceView.getHolder();
//        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onDestroy() {
//        stopRecording();
//        
//        removeView(this, mSurfaceView);
//        
//        if (mCamera != null) {
//            try {
//                mCamera.stopPreview();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mCamera.release();
//            mCamera = null;
//        }
        
        if (StreamerApplication.DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
        
        mBinder.getListener().onServiceDestroyed();
    }
    
//    private void startStreaming() {
//        if (TestApplication.DEBUG) Log.d(TAG, "startStreaming()");
//        
//        
//        startRecording();
//        
//        mStreamer = new Streamer();
//        new Thread(mStreamer).start();
//        
//       
//    }
//    
//    private void stopStreaming() {
//        if (TestApplication.DEBUG) Log.d(TAG, "stopStreaming()");
//        
////        mStreamer.getVideoPipe().closeOutput();
////        mStreamer.getAudioPipe().closeOutput();
//        mStreamer = null;
//        
//        stopRecording();
//    }
        
//    private void initializeRecorder() {
////        mAudioRecorder = new MediaRecorder();
////        
////        mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
////        mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
////        mAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
////        
////        mAudioRecorder.setOutputFile("/sdcard/test.amr"/*mStreamer.getAudioPipe().getOutput()*/);
////        
////        try {
////            mAudioRecorder.prepare();
////        } catch (IOException e) {
////            Log.e(TAG, "recorder prepare() failed");
////            e.printStackTrace();
////            releaseRecorder();
////        }
//        
//        mMediaRecorder = new MediaRecorder();
//        
//        mCamera.unlock();
//        mMediaRecorder.setCamera(mCamera);
//        
////        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
////        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // default H263
//        mMediaRecorder.setVideoSize(640, 480);
//        
//        mMediaRecorder.setOutputFile("/sdcard/h264.mp4");
//        
//        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
//
//        try {
//            mMediaRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(TAG, "recorder prepare() failed");
//            e.printStackTrace();
//            releaseRecorder();
//        }
//    }
//    
//    private void startRecording() {
//        initializeRecorder();
//        if (mMediaRecorder == null) {
//            Log.e(TAG, "failed to initialize recorder");
//            return;
//        }
//        
//        try {
//            mMediaRecorder.start();
//        } catch (RuntimeException e) {
//            Log.e(TAG, "recorder start() failed");
//            e.printStackTrace();
//            releaseRecorder();
//            
//            mCamera.lock();
//        }
//    }
//    
//    private void stopRecording() {
//        try {
//            mMediaRecorder.stop();
//        } catch (RuntimeException e) {
//            Log.e(TAG, "recorder stop() failed");
//            e.printStackTrace();
//        }
//        
//        releaseRecorder();
//    }
//    
//    private void releaseRecorder() {
//        if (mMediaRecorder != null) {
//            mMediaRecorder.reset();
//            mMediaRecorder.release();
//            mMediaRecorder = null;
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        if (TestApplication.DEBUG) Log.d(TAG, "surfaceChanged() width=" + width + ", height=" + height);
//        
//        if (holder.getSurface() == null) {
//            if (TestApplication.DEBUG) Log.w(TAG, "holder.getSurface() == null");
//            return;
//        }
//        
//        mSurfaceHolder = holder;
// 
//        try {
//            mCamera.setPreviewDisplay(mSurfaceHolder);
//        } catch (IOException e) {
//            Log.e(TAG, "camera setPreviewDisplay() failed");
//            e.printStackTrace();
//        }
//        mCamera.startPreview();
//        
//        startRecording();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        if (TestApplication.DEBUG) Log.d(TAG, "surfaceCreated()");
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (TestApplication.DEBUG) Log.d(TAG, "surfaceDestroyed()");
//        
//        mSurfaceHolder = null;
//    }
    
//    private void setPreviewDisplay(SurfaceHolder holder) {
//        try {
//            mCamera.setPreviewDisplay(holder);
//        } catch (Throwable ex) {
//            closeCamera();
//            throw new RuntimeException("setPreviewDisplay failed", ex);
//        }
//    }
//    
//    private boolean mPreviewing;
//    
//    private void startPreview() {
//        Log.v(TAG, "startPreview");
//
//        if (mPreviewing == true) {
//            mCamera.stopPreview();
//            mPreviewing = false;
//        }
//
//        setPreviewDisplay(mSurfaceHolder);
//        try {
//            mCamera.startPreview();
//        } catch (Throwable ex) {
//            closeCamera();
//            throw new RuntimeException("startPreview failed", ex);
//        }
//        mPreviewing = true;
//    }
//    
//    private void closeCamera() {
//        Log.v(TAG, "closeCamera");
//        if (mCamera == null) {
//            Log.d(TAG, "already stopped.");
//            return;
//        }
//        mCamera.release();
//        mCamera = null;
//        mPreviewing = false;
//    }
    
//    private static void addView(Context context, View view) {
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        params.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
//        params.format = PixelFormat.TRANSLUCENT;
//        params.height = 320;
//        params.width = 320;
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        wm.addView(view, params);
//    }
//    
//    private static void removeView(Context context, View view) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        wm.removeView(view);
//    }
//    
//    @TargetApi(9)
//    public static Camera getCameraInstance(int cameraId) {
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
//            Camera.getCameraInfo(i, info);
//            if (info.facing == cameraId) {
//                Camera c = null;
//                try {
//                    c = Camera.open(i);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return c;
//            }
//        }
//        return getCameraInstance();
//    }
//    
//    public static Camera getCameraInstance() {
//        Camera c = null;
//        try {
//            c = Camera.open();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return c;
//    }
}
