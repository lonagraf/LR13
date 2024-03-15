package com.example.lr13;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private EditText titleEditText;
    private TextView characterTextView;
    private TextView quoteTextView;
    private Button loadAnimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.titleEditText);
        characterTextView = findViewById(R.id.characterTextView);
        quoteTextView = findViewById(R.id.quoteTextView);
        loadAnimeButton = findViewById(R.id.loadAnimeButton);

        loadAnimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                new FetchAnimeDataTask().execute(title);
            }
        });
    }

    private class FetchAnimeDataTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String[] data = new String[2];
            try {
                String titleEncoded = URLEncoder.encode(params[0], "UTF-8");
                String apiUrl = "https://animechan.xyz/api/random/anime?title=" + titleEncoded;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String jsonString = convertStreamToString(inputStream);
                inputStream.close();
                connection.disconnect();

                JSONObject jsonObject = new JSONObject(jsonString);
                String animeTitle = jsonObject.getString("anime");
                String character = jsonObject.getString("character");
                String quote = jsonObject.getString("quote");

                data[0] = character;
                data[1] = quote;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                characterTextView.setText(data[0]);
                quoteTextView.setText(data[1]);
            }
        }

        private String convertStreamToString(InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}
