package com.misc.prescriptioncamera;

/**
 * Created by Rich on 6/5/13.
 */

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


class Preview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";

    SurfaceHolder mHolder;
    public Camera camera;

    Preview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.

        try {
            camera = Camera.open();

            camera.setPreviewDisplay(holder);



            //camera.setPreviewCallback(new PreviewCallback() {

                //public void onPreviewFrame(byte[] data, Camera arg1) {
                    //FileOutputStream outStream = null;
                    //try {
                    //    outStream = new FileOutputStream(String.format(
                    //            "/sdcard/%d.jpg", System.currentTimeMillis()));
                    //    outStream.write(data);
                    //    outStream.close();
                    //    Log.d(TAG, "onPreviewFrame - wrote bytes: "
                    //            + data.length);
                    //} catch (FileNotFoundException e) {
                    //    e.printStackTrace();
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //} finally {
                    //}
                    //Preview.this.invalidate();
                //}
            //});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.

        if(camera!=null)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }



    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.

        resetView();
    }

    public void resetView(){

        Camera.Parameters parameters = camera.getParameters();

        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();


        int maxPictureSizeIndex=-1;
        int maxPictureSize=0;

        for (int i=0; i<pictureSizes.size();i++){
            int currentPictureSize = pictureSizes.get(i).height * pictureSizes.get(i).width;

            if (currentPictureSize > maxPictureSize) {
                maxPictureSize =currentPictureSize ;
                maxPictureSizeIndex = i;
            }
        }

        int maxPreviewSizeIndex=-1;
        int maxPreviewSize=0;

        for (int i=0; i<previewSizes.size();i++){
            int currentPreviewSize = previewSizes.get(i).height * previewSizes.get(i).width;

            if (currentPreviewSize > maxPreviewSize) {
                maxPreviewSize =currentPreviewSize ;
                maxPreviewSizeIndex = i;
            }
        }

        //parameters.setPreviewSize(viewWidth, viewHeight);
        parameters.setPictureSize(pictureSizes.get(maxPictureSizeIndex).width, pictureSizes.get(maxPictureSizeIndex).height); // mac dinh solution 0
        parameters.setPreviewSize(previewSizes.get(maxPreviewSizeIndex).width, previewSizes.get(maxPreviewSizeIndex).height);

        camera.stopPreview();
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            parameters.set("orientation","portrait");
            camera.setDisplayOrientation(getScreenOrientation());
            parameters.setRotation(getScreenOrientation());
        }

        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            parameters.set("orientation","landscape");

            camera.setDisplayOrientation(getScreenOrientation());
            parameters.setRotation(getScreenOrientation());
        }


        parameters.setFocusMode("auto");
        camera.setParameters(parameters);

        camera.startPreview();

    }


    private int getScreenOrientation() {
        int rotation = ((WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    orientation = 90;
                    break;
                case Surface.ROTATION_90:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    orientation = 0;
                    break;
                case Surface.ROTATION_180:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    orientation = 270;
                    break;
                case Surface.ROTATION_270:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    orientation = 180;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    orientation = 0;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    orientation = 0;
                    break;
                case Surface.ROTATION_90:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    orientation = 90;
                    break;
                case Surface.ROTATION_180:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    orientation = 180;
                    break;
                case Surface.ROTATION_270:
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    orientation = 270;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    //orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    orientation = 0;
                    break;
            }
        }

        return orientation;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint p = new Paint(Color.RED);
        Log.d(TAG, "draw");
        canvas.drawText("PREVIEW", canvas.getWidth() / 2,
                canvas.getHeight() / 2, p);
    }






}