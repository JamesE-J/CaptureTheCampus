package com.edmondson_jones.james.capturethecampus;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class CreateAccountActivity extends AppCompatActivity {

    String username;
    String password;
    String confirmPassword;
    String teamPreference;

    ProgressBar progressBar = null;
    Button createAccountButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_create_account);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        createAccountButton = (Button)findViewById(R.id.menuButtonOne);
        final EditText usernameEntry = (EditText)findViewById(R.id.usernameEntry);
        final EditText passwordEntry = (EditText)findViewById(R.id.passwordEntry);
        final EditText confirmPasswordEntry = (EditText)findViewById(R.id.confirmPasswordEntry);
        final Spinner teamPreferenceSpinner = (Spinner)findViewById(R.id.teamPreferenceSpinner);

        createAccountButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        username = usernameEntry.getText().toString();
                        password = passwordEntry.getText().toString();
                        confirmPassword = confirmPasswordEntry.getText().toString();
                        teamPreference = teamPreferenceSpinner.getSelectedItem().toString();
                        if(password.equals(confirmPassword)) {
                            new Post().execute();
                        }else{
                            Toast.makeText(getApplicationContext(), "Passwords do not match",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public class Post extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            createAccountButton.setEnabled(false);
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

                data += "&" + URLEncoder.encode("team", "UTF-8") + "="
                        + URLEncoder.encode(teamPreference, "UTF-8");

                url = new URL("http://10.0.2.2:56412/api/user/new");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr =
                        new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
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
            createAccountButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
