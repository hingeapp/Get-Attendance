package com.weone.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by Sachin Shinde on 3/6/2015.
 */

public class MainActivity extends ActionBarActivity  {
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected TourGuide tourGuide = null;
    protected ProgressDialog dialog;


    public EditText ed1,ed2;
    public Button bt1;
    public String loginId ;
    public String password ;
    protected Integer i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ed1=(EditText)findViewById(R.id.ed1);
        ed2=(EditText)findViewById(R.id.ed2);
        bt1=(Button)findViewById(R.id.bt1);
        i = 0;

        sharedPreferences = getSharedPreferences("default",MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isEdited",false))
        {
            ed1.setText(sharedPreferences.getString("username","test"));
            ed2.setText(sharedPreferences.getString("password","pass"));
        }

        if(!sharedPreferences.getBoolean("isTaught",false)) {
            tourGuide = new TourGuide(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle("Welcome").setDescription("Enter details & click on Login to get attendance"))
                    .setOverlay(new Overlay())
                    .playOn(bt1);
        }
        else{
            i++;
        }


                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (i == 0) {

                            if(tourGuide != null){
                                tourGuide.cleanUp();
                                editor = sharedPreferences.edit();
                                editor.putBoolean("isTaught",true);
                                editor.commit();

                            }
                            i++;
                        } else {
                            loginId = ed1.getText().toString().toUpperCase();
                            password = ed2.getText().toString();

                            if (loginId.isEmpty() || password.isEmpty()) {
                                Toast.makeText(MainActivity.this, "Fields Cannot be Empty", Toast.LENGTH_LONG).show();
                            }
                            else {

                                editor = sharedPreferences.edit();
                                editor.putBoolean("isEdited",true);
                                editor.putString("username",loginId);
                                editor.putString("password",password);
                                editor.commit();

                                dialog = ProgressDialog.show(MainActivity.this,"Fetching","Please wait while we get your attendance");

                                // Log.i("Starting: ", "Getting the attendance");
                                    bt1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));

                                if (!hasActiveInternetConnection()) {
                                    Toast.makeText(MainActivity.this, "Network not Available", Toast.LENGTH_LONG).show();
                                    bt1.setBackgroundColor(getResources().getColor(R.color.loginButton));
                                }
                                else {
                                    response task = new response();
                                    task.execute();
                                }
                            }

                        }
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
            }
        } else {
        }
        return false;
    }



    public class response extends AsyncTask {
        protected String name;
        protected String attendance;
        protected ArrayList<SubjectHolder> holders = new ArrayList<SubjectHolder>();

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
                    //Log.i("Name", line.substring(a + 12, line.length() - 1));
                    name = line.substring(a+12,line.length()-1);
                    //Log.i("NAME IS",name);
                }
                else if(line.contains("<td align=left  class=MTTD8 colspan= ")){
                    SubjectHolder tempHolder = new SubjectHolder();
                    line = reader.readLine();       //this contains subject.
                    tempHolder.setFullSubjectName(line.trim());
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

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            int count = 0;
            for(SubjectHolder holder : holders){
                if(!holder.getConductedLectures().equalsIgnoreCase("0")){
                    Collections.swap(holders,holders.indexOf(holder),count);
                    count++;
                }
                if(holder.getSubjectName().equalsIgnoreCase("DMS")){
                    holder.setSubjectName("DBMS");
                }
                if(holder.getSubjectName().equalsIgnoreCase("DMSL")){
                    holder.setSubjectName("DLAB");
                }
            }

            if(dialog != null){
                dialog.dismiss();
                dialog = null;
            }
            Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
            intent.putParcelableArrayListExtra("holder",holders);
            intent.putExtra("name",name);
            intent.putExtra("attendance", attendance);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


//    private boolean isAppInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                            //Toast.makeText(context,"In foreground",Toast.LENGTH_SHORT).show();
//                           // Log.i("STATUS","FOREGROUND");
//                        }
//                        else{
//                           // Toast.makeText(context,"In background",Toast.LENGTH_SHORT).show();
//                            //Log.i("STATUS","Background");
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//                //Toast.makeText(context,"In foreground",Toast.LENGTH_SHORT).show();
//                //Log.i("STATUS","FOREGROUND");
//            }
//            else{
//                //Toast.makeText(context,"In background",Toast.LENGTH_SHORT).show();
//               // Log.i("STATUS","Background");
//            }
//        }
//
//        return isInBackground;
//    }

}

