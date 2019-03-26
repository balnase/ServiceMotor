package com.jiuj.servicemotor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.jiuj.servicemotor.*;
import com.jiuj.servicemotor.Adapter.ServiceAdapter;
import com.jiuj.servicemotor.Adapter.ServiceList;
import com.jiuj.servicemotor.Database.myDBClass;
import com.shamanland.fab.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db=null;
    private SQLiteDatabase sqlite=null;
    private myDBClass dbx=null;
    FloatingActionButton fab;
    private static String sTgl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        File sd = Environment.getExternalStorageDirectory();
        dbx = new myDBClass(this);
        db = dbx.getWritableDatabase();
        fab.setOnClickListener(new MainActivity.ButtonClickHandler());
        long size = 0;
        size += getDirSize(this.getCacheDir());
        displayLv();
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view){
            Intent i = new Intent(MainActivity.this, InputServiceActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void displayLv(){
        final ArrayList<ServiceList> imageArry = new ArrayList<ServiceList>();
        ServiceAdapter adapter;
        String selectQuery = "";
        selectQuery = "SELECT * from db_service";
        final Cursor csr = db.rawQuery(selectQuery, null);
        if (csr.moveToFirst()) {
            do
            {
                ServiceList KFL = new ServiceList(csr.getString(0), csr.getString(1)+" ("+csr.getString(6)+")", csr.getString(2)+" ("+csr.getString(4)+")", csr.getString(3), csr.getString(5));
                imageArry.add(KFL);
            } while (csr.moveToNext());
        }
        adapter = new ServiceAdapter(this, R.layout.list_item, imageArry);
        ListView dataList = (ListView) this.findViewById(R.id.list);
        dataList.setAdapter(adapter);
    }

    public void showAlert(){
        new AwesomeInfoDialog(this)
                .setTitle(R.string.app_name)
                .setMessage("Anda yakin akan keluar dari aplikasi ?")
                .setColoredCircle(R.color.dialogNoticeBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_notice, R.color.white)
                .setCancelable(false)
                .setPositiveButtonText(getString(R.string.dialog_yes_button))
                .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonText(getString(R.string.dialog_no_button))
                .setNegativeButtonbackgroundColor(R.color.colorAccent)
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        finish();
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //click
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    public static void backupDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String packageName = context.getApplicationInfo().packageName;
            sTgl = getDateTime();
            if (sd.canWrite()) {
                String currentDBPath = String.format("//data//%s//databases//%s",
                        packageName, databaseName);
                String backupDBPath = String.format("backup_"+sTgl+"_%s.db", packageName);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                Toast.makeText(context,"Backup Success to "+backupDBPath,Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                backupDatabase(MainActivity.this,"servicemotor.db");
                break;

            case R.id.restore:
                restoreDB();
                break;

            case R.id.cache:
                deleteCache(this);
                //clearApplicationData();
                break;

        }
        return true;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));

                }
            }
        }
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void restoreDB(){
        String path = Environment.getExternalStorageDirectory()+ "/com.jiuj.servicemotor.db";
        File file = new File(path);
        if(file.exists()){
            sqlite = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            //Toast.makeText(MainActivity.this, "ada",Toast.LENGTH_LONG).show();
            String selectQuery = "SELECT * from db_service";
            final Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();
            if(count>0){
            }else{
                String selectQuery2 = "SELECT * from db_service";
                final Cursor csr = sqlite.rawQuery(selectQuery2, null);
                if (csr.moveToFirst()) {
                    do
                    {
                        ContentValues values = new ContentValues();
                        values.put("noref", csr.getString(0));
                        values.put("judul", csr.getString(1));
                        values.put("tempat", csr.getString(2));
                        values.put("keterangan", csr.getString(3));
                        values.put("motortype", csr.getString(4));
                        values.put("image", csr.getString(5));
                        values.put("tglservice", csr.getString(6));
                        values.put("createtime", csr.getString(7));
                        db.insert("db_service", null, values);
                    } while (csr.moveToNext());
                }
                file.delete();
                displayLv();
            }
        }else{
            Toast.makeText(MainActivity.this, "File : com.jiuj.servicemotor.db tidak ada !!",Toast.LENGTH_LONG).show();
        }

        /*
        String selectQuery = "SELECT * from db_service";
        String a = "";
        final Cursor csr = sqlite.rawQuery(selectQuery, null);
        if (csr.moveToFirst()) {
            do
            {
                //a += csr.getString(1);
                //Toast.makeText(MainActivity.this, a,Toast.LENGTH_LONG).show();
                ContentValues values = new ContentValues();
                values.put("noref", csr.getString(0));
                values.put("judul", csr.getString(1));
                values.put("tempat", csr.getString(2));
                values.put("keterangan", csr.getString(3));
                values.put("motortype", csr.getString(4));
                values.put("image", csr.getString(5));
                values.put("tglservice", csr.getString(6));
                values.put("createtime", csr.getString(7));
                db.insert("db_service", null, values);
            } while (csr.moveToNext());
        }
        */
    }
}
