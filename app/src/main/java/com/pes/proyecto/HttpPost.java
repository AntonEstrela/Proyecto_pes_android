package com.pes.proyecto;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public abstract class HttpPost extends AsyncTask<JSONObject, Void, String> {//classe universal per fer posts
    private String path;

    WeakReference<Context> contextWeakReference;//per si despres fa falta fer algun toast, passar-li el context

    HttpPost(Context context){

        contextWeakReference = new WeakReference<>(context);//i poder fer la classe estatica per evitar fugues de memoria
    }
    void SendRequest(JSONObject params, String path){//executar la petició
        if(params == null){
            return;
        }
        if(path.equals("")){
            return;
        }
        this.path = path;
        execute(params);
    }

    protected void onPreExecute(){

    }

    protected String doInBackground(JSONObject... arg0) {

        try{
            URL url = new URL(ConfigSingleton.getInstance().ServerAddress + path);

            JSONObject postDataParams = arg0[0];

            Log.e("params",postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            }
            else {
                return "false : " + responseCode;
            }
        }
        catch(Exception e){
            return "Exception: " + e.getMessage();
        }
    }


    private String getPostDataString(JSONObject params) throws Exception {//sense comentaris

        //return params.toString();//no cuela
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }



}
