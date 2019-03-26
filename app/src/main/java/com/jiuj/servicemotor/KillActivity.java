package com.jiuj.servicemotor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class KillActivity extends AppCompatActivity {
    private Context mContext;

    private Button mButton;
    private Button mButtonKill;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill);

        // Get the application context
        mContext = getApplicationContext();

        // Set the action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));

        // Get the widgets reference from XML layout
        mButton = (Button) findViewById(R.id.btn);
        mButtonKill = (Button) findViewById(R.id.btn_kill);
        mTextView = (TextView) findViewById(R.id.tv);

        // Set a click listener for button widget
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Populate the TextView with running processes
                populateTextViewWithRunningProcesses();
            }
        });

        // Set a click listener for kill button
        mButtonKill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KillBackgroundProcessesTask().execute();
            }
        });
    }

    protected void populateTextViewWithRunningProcesses(){
        // Empty the TextView
        mTextView.setText("");

        // Initialize a new instance of ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        // Get a list of RunningAppProcessInfo
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        // Display the number of running processes
        Toast.makeText(mContext,"Running processes : " +
                runningProcesses.size(),Toast.LENGTH_SHORT).show();

        // Loop through the running processes
        for(ActivityManager.RunningAppProcessInfo processInfo: runningProcesses ){
            // Get the process name
            mTextView.setText(mTextView.getText() + processInfo.processName + "\n");
        }
    }

    // AsyncTask to kill background processes
    private class KillBackgroundProcessesTask extends AsyncTask<Void,Integer,Integer> {
        @Override
        protected Integer doInBackground(Void...Void){
            // Get an instance of PackageManager
            PackageManager pm = getPackageManager();

            // Get an instance of ActivityManager
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

            // Get a list of RunningAppProcessInfo
            List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

            // Count the number of running processes
            int initialRunningProcessesSize = list.size();

            // Iterate over the RunningAppProcess list
            for(ActivityManager.RunningAppProcessInfo process: list){
                // Ignore, if the process contains package list is empty
                if(process.pkgList.length == 0) continue;

                try{
                    // Get the PackageInfo for current process
                    PackageInfo packageInfo = pm.getPackageInfo(process.pkgList[0],PackageManager.GET_ACTIVITIES);

                    // Ignore the self app package
                    if(!packageInfo.packageName.equals(mContext.getPackageName())){
                        // Try to kill other background processes
                        // System processes are ignored
                        am.killBackgroundProcesses(packageInfo.packageName);
                    }
                }catch(PackageManager.NameNotFoundException e){
                    // Catch the exception
                    e.printStackTrace();
                }
            }

            // Get the running processes after killing some
            int currentRunningProcessesSize = am.getRunningAppProcesses().size();

            // Return the number of killed processes
            return initialRunningProcessesSize - currentRunningProcessesSize;
        }

        protected void onPostExecute(Integer result){
            // Show the number of killed processes
            Toast.makeText(mContext,"Killed : " + result + " processes",Toast.LENGTH_SHORT).show();

            // Refresh the TextView with running processes
            populateTextViewWithRunningProcesses();
        }
    }
}
