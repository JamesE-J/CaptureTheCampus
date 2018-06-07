package com.edmondson_jones.james.capturethecampus;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.Circle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ScoreboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_scoreboard);
        new Get().execute();
    }
    public class Get extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection client = null;
            String response = null;
            try {
                url = new URL("http://10.0.2.2:56412/api/user/getscores");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");

                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                response = sb.toString();
            }catch(Exception e)
            {
                response = e.toString();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            result = result.substring(2, result.length() - 2);
            String[] resultArray = result.split(",");
            String[] nameArray = new String[10];
            int nameIndex = 0;
            String[] teamArray = new String[10];
            int teamIndex = 0;
            String[] scoreArray = new String[10];
            int scoreIndex = 0;
            for(int i = 0; i < resultArray.length; i++){
                switch(i) {
                    case 0:
                    case 3:
                    case 6:
                    case 9:
                    case 12:
                    case 15:
                    case 18:
                    case 21:
                    case 24:
                    case 27:
                        nameArray[nameIndex] = resultArray[i];
                        nameIndex++;
                        break;
                    case 1:
                    case 4:
                    case 7:
                    case 10:
                    case 13:
                    case 16:
                    case 19:
                    case 22:
                    case 25:
                    case 28:
                        teamArray[teamIndex] = resultArray[i];
                        teamIndex++;
                        break;
                    case 2:
                    case 5:
                    case 8:
                    case 11:
                    case 14:
                    case 17:
                    case 20:
                    case 23:
                    case 26:
                    case 29:
                        scoreArray[scoreIndex] = resultArray[i];
                        scoreIndex++;
                        break;
                }
            }
            ArrayAdapter ad=new ArrayAdapter(getApplicationContext(), R.layout.scoreboard_list_view, nameArray);
            ListView list1 = (ListView)findViewById(R.id.listView1);
            list1.setEnabled(false);
            list1.setAdapter(ad);
            ad=new ArrayAdapter(getApplicationContext(), R.layout.scoreboard_list_view, teamArray);
            ListView list2 = (ListView)findViewById(R.id.listView2);
            list2.setEnabled(false);
            list2.setAdapter(ad);
            ad=new ArrayAdapter(getApplicationContext(), R.layout.scoreboard_list_view, scoreArray);
            ListView list3 = (ListView)findViewById(R.id.listView3);
            list3.setEnabled(false);
            list3.setAdapter(ad);
        }
    }
}
