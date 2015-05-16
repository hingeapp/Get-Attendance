package com.weone.attendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class AttendanceActivity extends ActionBarActivity {

    protected String loginId;
    protected String password;
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

        Log.i("name is : ",name);
        Log.i("Att is : ",attendance);
        Log.i("Subjectnamesample : ",holders.get(0).getSubjectName()+" " + holders.get(0).getPercentAttendance());

        mDetailsList = (ListView)findViewById(R.id.subject_list);
        mName = (TextView) findViewById(R.id.name);
        mAttendance = (TextView) findViewById(R.id.percentage);
        mQuote = (TextView) findViewById(R.id.quote);
        mHeader = (RelativeLayout) findViewById(R.id.header_view);
        mShare = (TextView) findViewById(R.id.share);

        mName.setText(name);
        mAttendance.setText(attendance);

        Log.i("SUbSTRING is",attendance.substring(0,2));

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
                saveImage(bmScreen);
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

        Log.i("Saving","File");
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


    public class response extends AsyncTask {
        protected String name;
        protected String returnedLine;
        protected String attendance;
        protected ArrayList<SubjectHolder> holders = new ArrayList<SubjectHolder>();
        //        protected ArrayList<String> subject= new ArrayList<String>();
//        protected ArrayList<String> conducted= new ArrayList<String>();
//        protected ArrayList<String> attended= new ArrayList<String>();
//        protected ArrayList<String> percent= new ArrayList<String>();
        protected int count = 0;


        public String makeRequest(String dest) throws IOException {
            URL url = new URL(dest);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            String urlParameters = "loginid="+loginId + "&password=" + password + "&dbConnVar=PICT&service_id=";
            connection.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(urlParameters);

            InputStream inputStream = connection.getInputStream();

            String returnValue = "error";
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while( (line = reader.readLine() )!= null ) {
                if(line.contains("<tr class=\"child\"><td align=left  class = \"MTTD2\" colspan=\"1\"><b>")) {
                    //Log.i("YOLO","Line found"+line.substring(12));
                    line = reader.readLine();
                    int a = line.indexOf("colspan=\"2\">");
                    Log.i("Name", line.substring(a + 12, line.length() - 1));
                    name = line.substring(a+12,line.length()-1);
                }
                else if(line.contains("<td align=left  class=MTTD8 colspan= ")){
                    SubjectHolder tempHolder = new SubjectHolder();
                    line = reader.readLine();       //this contains subject.
                    String parts[] = line.split(" ");
                    StringBuilder builder = new StringBuilder();
                    int j = 0;
                    for(int i = 0; i < parts.length; i++) {
                        if(parts[i].length() > 1)       //check for "-"
                        {
                            if(! parts[i].equalsIgnoreCase("of") && !parts[i].equalsIgnoreCase("and")) {
                                if (parts[i].equalsIgnoreCase("th") || parts[i].equalsIgnoreCase("pr")) {
                                    //Log.i("Split", parts[i].substring(0, 2));
                                    //no need to add pr/th. its CG or CGL. selfexplanatory
                                    //builder.append( " " + parts[i].substring(0,2) );
                                }
                                else if(parts[i].contains("III") && !parts[i].contains("-III")){
                                    builder.append("3");
                                }
                                else if(parts[i].contains("TUT")) {
                                    builder.append(" TUT");
                                }
                                else {
                                    //Log.i("Split: ", parts[i].substring(0, 1));
                                    builder.append(parts[i].substring(0, 1));
                                }
                            }
                        }
                    }
                    //Log.i("Subject please: ", builder.toString());
                    //subject.add(line);
                    tempHolder.setSubjectName(builder.toString());
                    //subject.add(builder.toString());
                    line = reader.readLine();
                    line = reader.readLine();
                    line = reader.readLine();       //this contains conducted lectures
                    //conducted.add(line.trim());
                    tempHolder.setConductedLectures(line.trim());

                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    line = reader.readLine();   //this contains attended lectures
                    //attended.add(line.trim());
                    tempHolder.setAttendedLectures(line.trim());

                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    line = reader.readLine();   //this contains percent att
                    //percent.add(line.trim());
                    tempHolder.setPercentAttendance(line.trim());

                    holders.add(tempHolder);
                    count++;
//                    if( !conducted.get(count - 1).equalsIgnoreCase("0"))    //filters out zero conducted
//                        Log.i("Summary", subject.get(count - 1)+" " + conducted.get(count - 1) + " "
//                            + attended.get(count - 1) +" " + percent.get(count - 1));

                    if( ! (holders.get(count -1)).getConductedLectures().equalsIgnoreCase("0"))    //filters out zero conducted
                        Log.i("Summary", (holders.get(count -1)).getSubjectName()+" " +
                                (holders.get(count -1)).getConductedLectures() + " "+
                                (holders.get(count -1)).getAttendedLectures() +" " +
                                (holders.get(count -1)).getPercentAttendance());
                }
                else if(line.contains("Average :")){
                    line = reader.readLine();
                    line = reader.readLine();

                    int a = line.indexOf("<b>");
                    int b = line.indexOf("</b>");
                    attendance = line.substring(a+3, b);//+3 GETS RID OF <B>
                    //String att = line;
                    attendance = attendance+ "%";
                    //Log.i("YOLO","Line found"+att);
                    returnValue = attendance;

                }
                else {
                    //not this line
                }
            }
            if(returnValue == null)
                return "An Error occured.";
            return returnValue;
            //return "Emmm..dunno";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            try{
                String yolo = makeRequest("http://pict.ethdigitalcampus.com:80/DCWeb/authenticate.do");
            }catch (IOException e){
                e.printStackTrace();
            }

            try{
                attendance = makeRequest("http://pict.ethdigitalcampus.com/DCWeb/form/jsp_sms/StudentsPersonalFolder_pict.jsp?dashboard=1");
                Log.i("YOLO","Attendance is "+ attendance);

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //setProgressBarIndeterminateVisibility(false);
            Log.i("onPostExecute","Attendance of " + name + " is " + attendance);
            // Toast.makeText(MainActivity.this,"Now what?",Toast.LENGTH_LONG).show();

        }
    }
}
