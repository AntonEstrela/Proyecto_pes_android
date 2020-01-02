package com.pes.proyecto;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context con = this;

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
        recyclerView.setAdapter(new CantantsRecyclerViewAdapter(null, this));


        txtNom.setText("Name: " + nomCantant);


        request();
    }


    public void request(View... view){
        new Thread(new Runnable() {
            InputStream stream = null;
            String str = "";
            String result = null;
            Handler handler = new Handler();
            public void run() {

                try {

                    String query = String.format("http://192.168.0.16:9000/Application/GetCantant?cantant=" + nomCantant);
                    URL url = new URL(query);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setReadTimeout(10000 );
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(false);


                    stream = conn.getInputStream();

                    BufferedReader reader = null;

                    StringBuilder sb = new StringBuilder();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    result = sb.toString();
                    // Mostrar resultat en el quadre de text.
                    // Codi incorrecte
                    // EditText n = (EditText) findViewById (R.id.edit_message);
                    //n.setText(result);

                    //Codi correcte

                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                paisCantant = jsonObject.getString("pais");
                                txtPais.setText("Country: " + paisCantant);

                                JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("cansons"));

                                recyclerView.setAdapter(new CansonsRecyclerViewAdapter(jsonArray, con));
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
