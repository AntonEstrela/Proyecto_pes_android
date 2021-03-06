package com.pes.proyecto;

import android.os.Handler;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpGet {//classe universal per fer gets
    private String path;
    void SendRequest(String path){//executar la petició
        this.path = path;
        if(path.equals("")){
            return;
        }
        request();

    }
    private void request(){
        new Thread(new Runnable() {
            InputStream stream = null;
            String result = null;
            Handler handler = new Handler();
            public void run() {

                try {

                    String query = ConfigSingleton.getInstance().ServerAddress + path;
                    URL url = new URL(query);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setReadTimeout(10000 );
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(false);


                    stream = conn.getInputStream();

                    BufferedReader reader;

                    StringBuilder sb = new StringBuilder();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    result = sb.toString();
                    handler.post(new Runnable() {
                        public void run() {
                            onPostExecute(result);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    protected abstract void onPostExecute(String result);//com si fos l'AsyncTask

}
