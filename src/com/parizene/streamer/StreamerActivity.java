
package com.parizene.streamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parizene.streamer.StreamerApplication.OnServiceConnectedListener;

import java.util.List;

public class StreamerActivity extends Activity implements OnServiceConnectedListener {
    private static final String TAG = "StreamerActivity";

    private SharedPreferences mPrefs;
    
    private CameraPreview mPreview;

    private Camera mCamera;
    private Streamer mStreamer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (StreamerApplication.DEBUG)
            Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamer);

        StreamerApplication.getInstance().setOnServiceConnectedListener(this);
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        Camera.Parameters params = mCamera.getParameters();

        List<Integer> supportedPreviewFormats = params.getSupportedPreviewFormats();
        for (int i = 0; i < supportedPreviewFormats.size(); i++) {
            Log.d(TAG, "supportedPreviewFormats[" + i + "]="
                    + getImageFormatString(supportedPreviewFormats.get(i)));
        }
        params.setPreviewFormat(ImageFormat.NV21);

        List<Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < supportedPreviewSizes.size(); i++) {
            Log.d(TAG, "supportedPreviewSizes[" + i + "]=" + supportedPreviewSizes.get(i).width
                    + "x" + supportedPreviewSizes.get(i).height);
        }
        params.setPreviewSize(Streamer.WIDTH, Streamer.HEIGHT);

        List<Integer> supportedPreviewFrameRates = params.getSupportedPreviewFrameRates();
        for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
            Log.d(TAG, "supportedPreviewFrameRates[" + i + "]=" + supportedPreviewFrameRates.get(i));
        }
        params.setPreviewFrameRate(Streamer.FRAME_RATE);

        mCamera.setParameters(params);

        mStreamer = new Streamer();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, mStreamer);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button captureButton = (Button) findViewById(R.id.record);
        captureButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!mStreamer.isStarted()) {
                            showAddrDialog(v);
                        } else {
                            mStreamer.stop();
                            ((Button) v).setText("Record");
                        }
                    }
                }
                );

        Button focusButton = (Button) findViewById(R.id.focus);
        focusButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCamera.autoFocus(new AutoFocusCallback() {

                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                            }
                        });
                    }
                }
                );
    }

    private void showAddrDialog(final View v) {
        final EditText input = new EditText(this);
        input.setText(mPrefs.getString("addr", ""));
        new AlertDialog.Builder(this)
                .setTitle("Destination IP: *.*.*.*")
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String addr = input.getText().toString();
                        if (addr.matches("^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$")) {
                            mPrefs.edit().putString("addr", addr).commit();
                            mStreamer.start(addr);
                            ((Button) v).setText("Stop");
                        } else {
                            Toast.makeText(StreamerActivity.this, "Check IP!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    @Override
    protected void onDestroy() {
        if (StreamerApplication.DEBUG)
            Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_streamer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                finish();
                StreamerApplication.getInstance().requestExit();
                return true;
        }
        return false;
    }

    @Override
    public void onServiceConnected() {
        if (StreamerApplication.DEBUG)
            Log.d(TAG, "onServiceConnected()");
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    public static String getImageFormatString(int imageFormat) {
        switch (imageFormat) {
            case ImageFormat.JPEG:
                return "JPEG";
            case ImageFormat.NV16:
                return "NV16";
            case ImageFormat.NV21:
                return "NV21";
            case ImageFormat.RGB_565:
                return "RGB_565";
            case ImageFormat.YUY2:
                return "YUY2";
            case ImageFormat.YV12:
                return "YV12";
            default:
                return "UNKNOWN";
        }
    }
}
