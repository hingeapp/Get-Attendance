package com.weone.attendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class AttendanceActivity extends ActionBarActivity {

    protected String name;
    protected String attendance;

    protected ListView mDetailsList;
    protected TextView mName;
    protected TextView mAttendance;
    protected TextView mQuote;
    protected RelativeLayout mHeader;
    protected TextView mShare;

    protected SubjectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_attendance);
        ArrayList<SubjectHolder> holders = new ArrayList<SubjectHolder>();
        Intent intent = getIntent();
        holders = intent.getParcelableArrayListExtra("holder");
        name = intent.getStringExtra("name");
        attendance = intent.getStringExtra("attendance");

//        Log.i("name is : ",name);
//        Log.i("Att is : ",attendance);
//        Log.i("Subjectnamesample : ",holders.get(0).getSubjectName()+" " + holders.get(0).getPercentAttendance());

        mDetailsList = (ListView)findViewById(R.id.subject_list);
        mName = (TextView) findViewById(R.id.name);
        mAttendance = (TextView) findViewById(R.id.percentage);
        mQuote = (TextView) findViewById(R.id.quote);
        mHeader = (RelativeLayout) findViewById(R.id.header_view);
        mShare = (TextView) findViewById(R.id.share);
        mName.setText(name);
        mAttendance.setText(attendance);

        if(attendance.length() == 2){
            /**
             * 2 digit number like 22 or 78
             */
            if (Integer.parseInt(attendance.substring(0,2)) < 75) {
                mQuote.setText("Defaulter!");
                mHeader.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (Integer.parseInt(attendance.substring(0,2)) < 80) {
                mQuote.setText("On the Brink!");
                mHeader.setBackgroundColor(getResources().getColor(R.color.orange));
            } else {
                mQuote.setText("Can afford to bunk!");
            }
        }
        else if(attendance.charAt(2) == '.' ) {
            //2 digit attendance ex- 82.1 , i.e. not 100

            if (Integer.parseInt(attendance.substring(0,2)) < 75) {
                mQuote.setText("Defaulter!");
                mHeader.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (Integer.parseInt(attendance.substring(0,2)) < 80) {
                mQuote.setText("On the Brink!");
                mHeader.setBackgroundColor(getResources().getColor(R.color.orange));
            } else {
                mQuote.setText("Can afford to bunk!");
            }
        }
        else {
            mQuote.setText("Seriously?");
        }

        mAdapter = new SubjectAdapter(AttendanceActivity.this,
                R.layout.subject_item,
                holders);

        mDetailsList.setAdapter(mAdapter);

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bmScreen;
                mHeader.setDrawingCacheEnabled(true);
                bmScreen = mHeader.getDrawingCache();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmScreen.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f=new File(Environment.getExternalStorageDirectory() + File.separator,"Attendance");
                if (!f.exists())
                    f.mkdir();
                File f1 = new File(Environment.getExternalStorageDirectory() +"/"+"Attendance/","temp.jpg");
                try {
                    f1.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f1);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard//Attendance//temp.jpg"));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
    }


    /**
     * Used to save screenshot
     * @param
     * @return
     */
    protected void saveImage(Bitmap bmScreen2) {
        // TODO Auto-generated method stub

        //Log.i("Saving","File");
        // String fname = "Upload.png";
        File saved_image_file = new File(
                Environment.getExternalStorageDirectory()
                        + "/captured_Bitmap.png");
        if (saved_image_file.exists())
            saved_image_file.delete();
        try {
            FileOutputStream out = new FileOutputStream(saved_image_file);
            bmScreen2.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
