package com.edmondson_jones.james.capturethecampus;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    String username;
    String password;

    ProgressBar progressBar = null;
    Button createAccountActivityButton = null;
    Button loginButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        final EditText usernameEntry = (EditText)findViewById(R.id.usernameEntry);
        final EditText passwordEntry = (EditText)findViewById(R.id.passwordEntry);

        final Intent loginIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        createAccountActivityButton = (Button)findViewById((R.id.menuButtonTwo));
        createAccountActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(loginIntent);
            }
        });

        loginButton = (Button)findViewById(R.id.menuButtonOne);
        loginButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        username = usernameEntry.getText().toString();
                        password = passwordEntry.getText().toString();
                        new Login().execute();
                    }
                });
    }
    public class Login extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            createAccountActivityButton.setEnabled(false);
        }
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection client = null;
            String response = null;
            try {
                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(username, "UTF-8");

                data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                        + URLEncoder.encode(password, "UTF-8");

                url = new URL("http://10.0.2.2:56412/api/user/login");
                URL uriUrl = new URL (url.toString() + "?" + data);
                client = (HttpURLConnection) uriUrl.openConnection();
                client.setRequestMethod("GET");

                URLConnection conn = uriUrl.openConnection();

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
            progressBar.setVisibility(View.INVISIBLE);
            loginButton.setEnabled(true);
            createAccountActivityButton.setEnabled(true);
            result = result.replace("\"","");
            result = result.replace("\n","");
            String[] stringArray = result.split(",");
            result = stringArray[0];
            if(result.length() == 36)
            {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra("UserKey", result);
                intent.putExtra("UserName", username);
                intent.putExtra("Team", stringArray[1]);
                LoginActivity.this.startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
