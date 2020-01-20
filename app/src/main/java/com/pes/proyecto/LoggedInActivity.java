package com.pes.proyecto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;

public class LoggedInActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context con = this;
    private boolean admin = false;

    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Intent intent = getIntent();
        admin = intent.getBooleanExtra("admin", false);

        recyclerView = findViewById(R.id.recyclerView1);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CantantsRecyclerViewAdapter(null, this, getAdmin()));

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        onStart();
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshLayout.setRefreshing(true);
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
            finally {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    private boolean getAdmin(){
        return admin;
    }

}
