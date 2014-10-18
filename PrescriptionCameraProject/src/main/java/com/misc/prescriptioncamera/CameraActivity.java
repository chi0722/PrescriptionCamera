package com.misc.prescriptioncamera;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;

import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraActivity extends Activity {

    ImageButton takePictureButton;
    Preview preview;

    //Prescription Camera File Access
    static final String APP_KEY = "bcx7k2nj34wlaqo";
    static final String APP_SECRET = "vwr8d0kimgvn8fn";
    static final int REQUEST_LINK_TO_DBX = 0;  // This value is up to you
    DbxAccountManager mAccountManager;
    DbxAccount mAccount;
    DbxFileSystem dbxFs;


    TextView patientUidEditText;
    TextView selectedType;
    TextView imageNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        //this.setRequestedOrientation(1);

        mAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

        patientUidEditText = (TextView) findViewById(R.id.patientUidEditText);
        selectedType = (TextView) findViewById(R.id.selectedType);
        imageNum = (TextView) findViewById(R.id.imageNum);


        patientUidEditText.setText(getIntent().getStringExtra("PatientUidEditText"));
        selectedType.setText(getIntent().getStringExtra("SelectedType"));
        imageNum.setText(getIntent().getStringExtra("CurrentImageNumber"));

        takePictureButton = (ImageButton) findViewById(R.id.takePictureButton);

        preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);



        takePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takePictureButton.setEnabled(false);
                preview.camera.takePicture(shutterCallback, rawCallback,
                        jpegCallback);
            }
        });


        preview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preview.camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {

                    }
                });
            }

        });


        if (mAccountManager.hasLinkedAccount()) {
            mAccount = mAccountManager.getLinkedAccount();

            try {
                dbxFs = DbxFileSystem.forAccount(mAccountManager.getLinkedAccount());

            } catch (DbxException.Unauthorized unauthorized) {
                unauthorized.printStackTrace();
            }

        } else {
            mAccountManager.startLink((Activity)CameraActivity.this, REQUEST_LINK_TO_DBX);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }





    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {

        }
    };

    /** Handles data for raw picture */
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };



    /** Handles data for jpeg picture */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            FileOutputStream outStream = null;
            DbxFile jpegFile = null;
            try {
                try {
                dbxFs.createFolder(new DbxPath("/" + getIntent().getStringExtra("SelectedType") + "/"));

                    DbxPath file = new DbxPath("/" + getIntent().getStringExtra("SelectedType") + "/" + getIntent().getStringExtra("PatientUidEditText") + "-" + getIntent().getStringExtra("Date") + "-" + getIntent().getStringExtra("CurrentImageNumber") + ".jpg");

                if (dbxFs.exists(file)){
                    dbxFs.delete(file);
                }
                jpegFile= dbxFs.create(file);

                outStream = jpegFile.getWriteStream();
                outStream.write(data);
                outStream.close();
                }finally {
                    if (jpegFile!=null){
                        jpegFile.close();
                    }
                }

                // write to local sandbox file system
                // outStream =
                // CameraDemo.this.openFileOutput(String.format("%d.jpg",
                // System.currentTimeMillis()), 0);
                // Or write to sdcard

                //File exMobileDir = new File(getIntent().getStringExtra("CameraDirectory"));

                //if(!exMobileDir.exists()){
                //    exMobileDir.mkdirs();
                //}

                //outStream = new FileOutputStream(
                //        String.format(exMobileDir.getPath() + "/%d.jpg", System.currentTimeMillis())
                //);

                //outStream.write(data);
                //outStream.close();


                Toast.makeText(CameraActivity.this, "Saved", Toast.LENGTH_LONG).show();

                preview.camera.stopPreview();
                preview.camera.release();
                preview.camera = null;

                Intent intent = new Intent();
                intent.putExtras(getIntent().getExtras());
                CameraActivity.this.setResult(RESULT_OK, intent);
                CameraActivity.this.finish();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

        }
    };

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        Toast.makeText(this, "onConfigurationChanged", Toast.LENGTH_LONG).show();
//
//        Camera.Parameters parameters = preview.camera.getParameters();
//        preview.camera.stopPreview();
//
//        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
//        {
//            parameters.set("orientation","portrait");
//            Toast.makeText(this, "|", Toast.LENGTH_LONG).show();
//            preview.camera.setDisplayOrientation(90);
//            parameters.setRotation(90);
//        }
//
//        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
//        {
//            parameters.set("orientation","landscape");
//            Toast.makeText(this, "-", Toast.LENGTH_LONG).show();
//
//            preview.camera.setDisplayOrientation(0);
//
//            parameters.setRotation(0);
//        }
//
//
//        preview.camera.setParameters(parameters);
//
//        preview.camera.startPreview();
//        super.onConfigurationChanged(newConfig);
//    }



}
