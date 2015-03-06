package com.weone.attendance;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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


public class MainActivity extends ActionBarActivity  {
    public TextView tv1,tv2;
    public EditText ed1,ed2;
    public Button bt1;
    public String loginId ;
    public String password ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        ed1=(EditText)findViewById(R.id.ed1);
        ed2=(EditText)findViewById(R.id.ed2);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        bt1=(Button)findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginId=ed1.getText().toString().toUpperCase();
                password=ed2.getText().toString();
                setProgressBarIndeterminateVisibility(true);
                response task = new response();
                task.execute();
            }
        });


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

    private class response extends AsyncTask{
        protected String name;
        protected String returnedLine;
        protected String attendance;
        protected ArrayList<String> subject= new ArrayList<String>();
        protected ArrayList<String> conducted= new ArrayList<String>();
        protected ArrayList<String> attended= new ArrayList<String>();
        protected ArrayList<String> percent= new ArrayList<String>();
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
                    Log.i("Name" , line.substring(a+12, line.length()-1));
                    name = line.substring(a+12,line.length()-1);
                }
                else if(line.contains("<td align=left  class=MTTD8 colspan= ")){
                    line = reader.readLine();       //this contains subject.
                    String parts[] = line.split(" ");
                    StringBuilder builder = new StringBuilder();
                    int j = 0;
                    for(int i = 0; i < parts.length; i++) {
                        if(parts[i].length() > 1)       //check for "-"
                        {
                            if(! parts[i].equalsIgnoreCase("of")) {
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
                    subject.add(builder.toString());
                    line = reader.readLine();
                    line = reader.readLine();
                    line = reader.readLine();       //this contains conducted lectures
                    conducted.add(line.trim());

                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    line = reader.readLine();   //this contains attended lectures
                    attended.add(line.trim());

                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    line = reader.readLine();   //this contains percent att
                    percent.add(line.trim());

                    count++;
                    Log.i("Summary", subject.get(count - 1)+" " + conducted.get(count - 1) + " "
                            + attended.get(count - 1) +" " + percent.get(count - 1));
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
        protected Object doInBackground(Object[] params) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            try{
                String yolo = makeRequest("http://pict.ethdigitalcampus.com:80/DCWeb/authenticate.do");
            }catch (IOException e){
                e.printStackTrace();
            }

            try{
                attendance = makeRequest("http://pict.ethdigitalcampus.com/DCWeb/form/jsp_sms/StudentsPersonalFolder_pict.jsp?dashboard=1");
                Log.i("YOLO","Attendance Line is"+ attendance);

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(MainActivity.this,"Attendance of "+name+" is " + attendance,Toast.LENGTH_LONG ).show();
           // Toast.makeText(MainActivity.this,"Now what?",Toast.LENGTH_LONG).show();
        }
    }
}
