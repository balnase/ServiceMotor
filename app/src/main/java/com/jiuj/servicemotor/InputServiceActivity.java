package com.jiuj.servicemotor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiuj.servicemotor.Database.myDBClass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;

public class InputServiceActivity extends AppCompatActivity {
    private DatePickerEditText datePickerEditText;
    ImageView img;
    SQLiteDatabase db;
    myDBClass dbx;
    EditText  edTitle, edPlace, edType, edDetail;
    Button btnSubmit;
    private static final int CAMERA_REQUEST = 1888;
    protected static final String PHOTO_TAKEN = "photo_taken";
    protected String path;
    private File file = null;
    private byte[] byteImg = null;
    private Bitmap SaveGambar = null;
    String path2, sNoref, sTgl;
    String sDate ="";
    String sTitle ="";
    String sPlace ="";
    String sKet ="";
    String sType ="";
    File file2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_service);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ke List Service");

        edTitle = (EditText) findViewById(R.id.edTitle);
        edPlace = (EditText) findViewById(R.id.edPlace);
        edType = (EditText) findViewById(R.id.edJenis);
        edDetail = (EditText) findViewById(R.id.edKet);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        img = (ImageView) findViewById(R.id.img);

        img.setVisibility(View.GONE);

        datePickerEditText = (DatePickerEditText) findViewById(R.id.datePickerEditText);

        datePickerEditText.setManager(getSupportFragmentManager());
        //datePickerEditText.setDateFormat(DateFormat.getLongDateFormat(getApplicationContext()));
        datePickerEditText.setDate(Calendar.getInstance());

        dbx = new myDBClass(this);
        db = dbx.getWritableDatabase();

        if (android.os.Build.VERSION.SDK_INT > 22) {
            StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(newbuilder.build());
        }

        path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/service_camera_rslt.jpg";
        btnSubmit.setOnClickListener(new InputServiceActivity.ButtonClickHandler());
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view){
            //sDate = datePickerEditText.getText().toString();
            //sDate = sDate+" 00:00:00";
            //Toast.makeText(InputServiceActivity.this,sDate,Toast.LENGTH_LONG).show();
            startCameraActivity();
        }
    }

    protected void startCameraActivity() {
        //checkPermission();
        file = new File(path);
        //file = new File(cDir,"msurvey_camera_rslt.jpg");
        //file = new File(getActivity().getApplicationContext().getCacheDir(), "msurvey_camera_rslt.jpg");
        if(file.exists()){file.delete();}
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        //startActivityForResult(intent, 0);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			/*
			switch (resultCode) {
			case 0:
				break;
			case -1:
				onPhotoTaken();
				break;
			}
			*/
        if(resultCode != RESULT_CANCELED){
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                onPhotoTaken();
            }
        }

    }

    public void onPhotoTaken()  {
        boolean taken = true;
        boolean imgCapFlag = true;
        path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/service_camera_rslt.jpg";
        file2 =  new File(path2);
        FileInputStream instream;
        try {
            //File f = new File(file);
            ExifInterface exif = new ExifInterface(file2.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int angle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            SaveGambar = BitmapFactory.decodeFile(path2, options);
            Bitmap bitmap3 = Bitmap.createBitmap(SaveGambar, 0, 0, SaveGambar.getWidth(),
                    SaveGambar.getHeight(), mat, true);
            //Bitmap bitmap2 = mark(bitmap3, strNopk+" / "+strTgl, "Nama : " + strNama, "Penjamin : " + strPenjamin );
            FileOutputStream savebos=new FileOutputStream(file2);
            //FileOutputStream savebos = getActivity().getApplicationContext().openFileOutput("msurvey_camera_rslt.jpg", Context.MODE_PRIVATE);
            //bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, savebos);
            bitmap3.compress(Bitmap.CompressFormat.JPEG, 50, savebos);
            instream = new FileInputStream(file2);
            BufferedInputStream bif = new BufferedInputStream(instream);
            byteImg = new byte[bif.available()];
            bif.read(byteImg);

            sTgl = dbx.getDateTime();
            String strTgl = sTgl.replace("-","").replace(" ","").replace(":","");

            sNoref = strTgl;
            sTitle = edTitle.getText().toString();
            sPlace = edPlace.getText().toString();
            sKet = edDetail.getText().toString();
            sType = edType.getText().toString();

            sDate = datePickerEditText.getText().toString();
            sDate = sDate+" 00:00:00";

            String encodedImage = Base64.encodeToString(byteImg, Base64.DEFAULT);

            ContentValues values = new ContentValues();
            values.put("noref", sNoref);
            values.put("judul", sTitle);
            values.put("tempat", sPlace);
            values.put("keterangan", sKet);
            values.put("motortype", sType);
            values.put("image", encodedImage);
            values.put("tglservice", sDate);
            values.put("createtime", sTgl);
            db.insert("db_service", null, values);

            img.setVisibility(View.VISIBLE);
            img.setImageBitmap(SaveGambar);
            btnSubmit.setVisibility(View.GONE);

            Toast.makeText(this,"Foto berhasil disimpan !!", Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed(){
        db.close();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            db.close();
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
