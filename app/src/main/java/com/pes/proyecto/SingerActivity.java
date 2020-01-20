package com.pes.proyecto;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class SingerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Context con = this;
    private boolean admin = false;

    private TextView txtNom;
    private TextView txtPais;

    private String nomCantant;
    private String paisCantant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);

        Intent intent = getIntent();
        nomCantant = intent.getStringExtra("cantant");
        admin = intent.getBooleanExtra("admin", false);

        txtNom = findViewById(R.id.textView4);
        txtPais = findViewById(R.id.textView5);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CansonsRecyclerViewAdapter(null, this, false));

        swipeRefreshLayout = findViewById(R.id.swiperefresh2);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        onResume();
                    }
                }
        );


        txtNom.setText("Name: " + nomCantant);



        //request();
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        new GetCantant().SendRequest("/Application/GetCantant?cantant=" + nomCantant);
        new GetCansons().SendRequest("/Application/GetCansonsByCantant?cantant=" + nomCantant);
    }

    public void AddSong(View view){
        if(!admin){
            Toast.makeText(SingerActivity.this, "Registration requiered", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), AddSongActivity.class);
        intent.putExtra("singer", nomCantant);
        startActivity(intent);
    }

    private class GetCantant extends HttpGet{
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                paisCantant = jsonObject.getString("pais");
                txtPais.setText("Country: " + paisCantant);

            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetCansons extends HttpGet{
        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jsonArray = new JSONArray(result);
                recyclerView.setAdapter(new CansonsRecyclerViewAdapter(jsonArray, con, admin));
                swipeRefreshLayout.setRefreshing(false);
            }
            catch (Exception e ){
                e.printStackTrace();
            }

        }
    }


}
