package com.misc.prescriptioncamera;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    EditText patientUidEditText;
    RadioButton radioButton1;
    RadioButton radioButton2;
    Button showCameraButton;


    //Prescription Camera File Access
    static final String APP_KEY = "bcx7k2nj34wlaqo";
    static final String APP_SECRET = "vwr8d0kimgvn8fn";
    static final int REQUEST_LINK_TO_DBX = 0;  // This value is up to you
    DbxAccountManager mAccountManager;
    private DbxAccount mAccount;

    static final int CAMERA_REQUEST = 20;
    String selectedType = "Prescription";


    Integer imageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        mAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);


        patientUidEditText= (EditText)findViewById(R.id.patientUidEditText);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);


        showCameraButton = (Button) findViewById(R.id.showCameraButton);


        patientUidEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length()>0){
                    showCameraButton.setEnabled(true);
                }else{
                    showCameraButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        radioButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (radioButton1.isChecked()) {
                    selectedType = "Prescription";
                    radioButton2.setChecked(false);

                }
            }

        });
        radioButton2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (radioButton2.isChecked()){
                    selectedType = "InspectionReport";
                    radioButton1.setChecked(false);

                }
            }

        });

        showCameraButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


                patientUidEditText.setText(patientUidEditText.getText().toString().toUpperCase());
                imageNum = 0;

                Intent intent = new Intent(MainActivity.this, CameraActivity.class);

                Bundle bundle=new Bundle();
                //bundle.putString("CameraDirectory", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + selectedType + "/" + patientUidEditText.getText() + "/");
                bundle.putString("SelectedType",selectedType);
                bundle.putString("PatientUidEditText",patientUidEditText.getText().toString());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String currentDateAndTime = sdf.format(new Date());

                Integer rocYear = Integer.parseInt(currentDateAndTime.substring(0,4))-1911;

                currentDateAndTime = currentDateAndTime.substring(4,8);
                currentDateAndTime = rocYear.toString() + currentDateAndTime;

                bundle.putString("Date",currentDateAndTime);

                bundle.putString("CurrentImageNumber",imageNum.toString());

                intent.putExtras(bundle);
                //startActivityForResult(intent,0);
                startActivityForResult(intent, CAMERA_REQUEST);
            }

        });



        if (mAccountManager.hasLinkedAccount()) {
            mAccount = mAccountManager.getLinkedAccount();


        } else {
            mAccountManager.startLink((Activity)MainActivity.this, REQUEST_LINK_TO_DBX);
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == -1) {


            Intent intent = new Intent(MainActivity.this, CameraActivity.class);

            Bundle bundle=(Bundle) data.getExtras();
            bundle.remove("CurrentImageNumber");

            imageNum += 1;

            bundle.putString("CurrentImageNumber",imageNum.toString());

            intent.putExtras(bundle);
            startActivityForResult(intent,CAMERA_REQUEST);
        }
    }
    
}
