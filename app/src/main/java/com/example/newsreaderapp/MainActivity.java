package com.example.newsreaderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> newsList;
    static ArrayList<String> urlsList;
    ArrayAdapter arrayAdapter;
    Intent intent;

    SQLiteDatabase sqLiteDatabase;

    public void insertEntry(String titleString, String urlString){
        sqLiteDatabase.execSQL("INSERT INTO news (title, url) VALUES (" + titleString + "," +urlString+")");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteDatabase = this.openOrCreateDatabase("News", MODE_PRIVATE, null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS news (title VARCHAR, url VARCHAR)");

        listView = findViewById(R.id.listView);

        newsList = new ArrayList<>();
        urlsList = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newsList);

        listView.setAdapter(arrayAdapter);

        DownloadTask task = new DownloadTask();
        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        intent = new Intent(getApplicationContext(), WebViewActivtiy.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

    }


    public class newDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Did not work! 3", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {


                JSONObject jsonObject = new JSONObject(s);

                String newsTitle = jsonObject.getString("title");
                String newsUrl = jsonObject.getString("url");

                newsList.add(newsTitle);
                urlsList.add(newsUrl);

                //insertEntry(newsTitle, newsUrl);

                /*


                JSONArray arr = new JSONArray(s);

                String message = "";

                for (int i=0; i<arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String newsTitle = jsonPart.getString("title");
                    String newsUrl = jsonPart.getString("url");

                    newsList.add(newsTitle);
                    urlsList.add(newsUrl);

                    insertEntry(newsTitle, newsUrl);

                }


                 */

                if (!newsList.isEmpty() && !urlsList.isEmpty()){
                    listView.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Did Not work! 4", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Did not work! 5", Toast.LENGTH_SHORT).show();
            }
        }

    }






    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Did not work! 1", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray newsCodeArray = new JSONArray(s);

                for (int i=0; i<7; i++) {

                    int newscode = (int) newsCodeArray.get(i);

                    String url = "https://hacker-news.firebaseio.com/v0/item/"+Integer.toString(newscode)+".json?print=pretty";

                    newDownloadTask taskNew = new newDownloadTask();
                    taskNew.execute(url);


                }

                /*
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for (int i=0; i<arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")){
                        message = main + " : " + description;
                    }

                }



                if (!message.equals("")){
                    //resultView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_SHORT).show();
                }


                 */


            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Did not work! 2", Toast.LENGTH_SHORT).show();
            }
        }
    }

}