package com.example.voice_reco;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class AsyncSlack extends AsyncTask<String, Integer, Integer> {
    @Override
    protected Integer doInBackground(String... params) {
        String message = params[0];

        int ret = 0;

        Log.d("TestLog", "slack : " + message);

        final String BR = System.getProperty("line.separator");

        RequestBody requestBody = new FormEncodingBuilder()
                .add("token", "xoxp-xxxxxx")       // トークンIDを定義する
                .add("channel", "#test")           // Channel名を定義する(ex. #general, @username(Direct Message))
                .add("text", message)               // Message
                .add("username", "hogehoge")      // Bot name (Slack上での投稿者名になる)　
                .build();

        Request request = new Request.Builder()
                .url("https://slack.com/api/chat.postMessage")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        try{
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            Log.d("TestLog", body);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ret;
    }
}