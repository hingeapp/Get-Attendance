package com.weone.attendance;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class MainActivity extends ActionBarActivity  {
    public TextView tv1,tv2;
    public EditText ed1,ed2;
    public Button bt1;
    public String loginId ;
    public String password ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed1=(EditText)findViewById(R.id.ed1);
        ed2=(EditText)findViewById(R.id.ed2);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        bt1=(Button)findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginId=ed1.getText().toString();
                password=ed2.getText().toString();
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

        public String makeRequest(String dest) throws IOException {
            URL url = new URL(dest);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            String urlParameters = "loginid="+loginId + "&password=" + password + "&dbConnVar=PICT&service_id=";
            connection.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(urlParameters);

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while( (line = reader.readLine() )!= null ) {
                if(line.contains("Hi !")) {
                    //Log.i("YOLO","Line found"+line.substring(12));
                    return line.substring(12);
                }
                else if(line.contains("Average :")){
                    int a = line.indexOf("<b>");
                    int b = line.indexOf("</b>");
                    String att = line.substring(a, b);
                    att = att+ "%";
                    //Log.i("YOLO","Line found"+att);
                    return att;

                }
                else {
                    //Log.i("YOLO","Not this line "+line);
                }
            }
            return null;
            //return "Emmm..dunno";
        }

        @Override
        protected Object doInBackground(Object[] params) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                String returnedLine;
                String attendance;
            try{
                returnedLine = makeRequest("http://pict.ethdigitalcampus.com:80/DCWeb/authenticate.do");
                Log.i("YOLO","ReturnedLine is:" + returnedLine);
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
            Toast.makeText(MainActivity.this,"Now what?",Toast.LENGTH_LONG).show();
        }
    }
}
