package com.jiuj.servicemotor;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.jiuj.servicemotor.Database.myDBClass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ImageViewActivity extends AppCompatActivity {
    PhotoView photoView;
    ImageView img;
    SQLiteDatabase db;
    myDBClass dbx;
    Button btnBack;
    byte[] outImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview_send);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ke List Service");

        btnBack = (Button) findViewById(R.id.back);
        //img = (ImageView) findViewById(R.id.image);
        img = (PhotoView) findViewById(R.id.image);

        btnBack.setOnClickListener(new ImageViewActivity.ButtonClickHandler());

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            outImage= getIntent().getByteArrayExtra("key2");
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            img.setImageBitmap(theImage);
        }

    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view){
            Intent i = new Intent(ImageViewActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
