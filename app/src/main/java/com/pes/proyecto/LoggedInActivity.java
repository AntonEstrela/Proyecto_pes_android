package com.pes.proyecto;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class LoggedInActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context con = this;
    private boolean admin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Intent intent = getIntent();
        admin = intent.getBooleanExtra("admin", false);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CantantsRecyclerViewAdapter(null, this, getAdmin()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetList().SendRequest("/Application/GetCantants");
    }

    public void AddSinger(View view){
        if(!admin){
            Toast.makeText(LoggedInActivity.this, "Registration requiered", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), AddSingerActivity.class);
        startActivity(intent);
    }

    private class GetList extends HttpGet{
        @Override
        protected void onPostExecute(String result) {
            try {
                //Toast.makeText(con, result, Toast.LENGTH_LONG);
                JSONArray jsonArray = new JSONArray(result);

                recyclerView.setAdapter(new CantantsRecyclerViewAdapter(jsonArray, con, getAdmin()));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean getAdmin(){
        return admin;
    }

}
