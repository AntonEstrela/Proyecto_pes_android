package com.pes.proyecto;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class AddSingerActivity extends AppCompatActivity {

    EditText EditName;
    EditText EditPais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_singer);
        EditName = findViewById(R.id.editText4);
        EditPais = findViewById(R.id.editText5);
    }

    public void onClick(View view){

        try {
            String nom = EditName.getText().toString();
            if(nom.equals("")){
                Toast.makeText(getApplicationContext(), "Anonymous singer?", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject obj = new JSONObject();
            obj.put("nom", nom);
            obj.put("pais", EditPais.getText().toString());
            new PostSinger(this, this).SendRequest(obj, "/Application/AddCantant");
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private static class PostSinger extends HttpPost{
        private WeakReference<AddSingerActivity> addSingerActivityWeakReference;
        PostSinger(Context context, AddSingerActivity activity){
            super(context);
            addSingerActivityWeakReference = new WeakReference<>(activity);
        }
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(contextWeakReference.get(), s, Toast.LENGTH_SHORT).show();
            if(s.equals("OK")){
                addSingerActivityWeakReference.get().finish();
            }
        }
    }
}
