package com.pes.proyecto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.LinkedList;

public class AddSongActivity extends AppCompatActivity {

    private TextView txtNom;
    private TextView txtData;
    private TextView txtLletra;

    private String nomCantant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        Intent intent = getIntent();
        nomCantant = intent.getStringExtra("singer");

        txtNom = findViewById(R.id.editText6);
        txtData = findViewById(R.id.editText7);
        txtLletra = findViewById(R.id.editText8);

        txtData.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

    }
    public void onClickCancel(View view){//boto cancel·lar (cancel) (tanca la activity)
        finish();
    }
    public void onClickSave(View view){//boto guardar
        final String nom = txtNom.getText().toString();
        if(nom.equals("")){
            Toast.makeText(getApplicationContext(), "Anonymous song?", Toast.LENGTH_SHORT).show();
            return;
        }
        final String data = txtData.getText().toString();
        final String lletra = txtLletra.getText().toString();
        try{
            Integer.parseInt(data);//fallara si data no es numero
            new GetCantants().SendRequest("/Application/GetCantants");//peticio per carregar llista de cantants al dialog
        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }
    private void selectSingers(JSONArray jsonArray){//obrir el dialog per seleccionar cantants
        try {
            final CharSequence[] items = new CharSequence[jsonArray.length()];
            boolean[] itemsChecked = new boolean[jsonArray.length()];
            final LinkedList<String> cantants = new LinkedList<>();//Llista de seleccionats

            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("nom");
                items[i] = name;
                if(name.equals(nomCantant)) {
                    itemsChecked[i] = true;
                    cantants.add(name);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Singers:");
            builder.setMultiChoiceItems(items, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position, boolean isChecked) {//actualitzar llista de seleccionats
                    if(isChecked){
                        cantants.add(items[position].toString());
                    }
                    else {
                        cantants.remove(items[position].toString());
                    }
                }
            });
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//muntar un string amb tots els cantants seleccionats
                    if(cantants.size() == 0){
                        Toast.makeText(getApplicationContext(), "Select at least one singer", Toast.LENGTH_LONG).show();
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (String c : cantants) {
                        sb.append(c);
                        sb.append(";");
                    }
                    commit(sb.substring(0, sb.length() - 1));//i cap al server


                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            AlertDialog alert = builder.create();
            alert.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void commit(String cantants){//pujar la canço al server
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nom", txtNom.getText().toString());
            jsonObject.put("data", txtData.getText().toString());
            jsonObject.put("lletra", txtLletra.getText().toString());
            jsonObject.put("cantants", cantants);
            new PostCanso(this, this).SendRequest(jsonObject, "/Application/AddCanso");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private static class PostCanso extends HttpPost{
        private WeakReference<AddSongActivity> activityWeakReference;
        PostCanso(Context context, AddSongActivity activity){
            super(context);
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(activityWeakReference.get(), s, Toast.LENGTH_LONG).show();
            if(s.equals("OK")){
                activityWeakReference.get().finish();//Si tot ha anat be tancar l'activity
            }
        }
    }

    private class GetCantants extends HttpGet{
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                selectSingers(jsonArray);//obrir el dialog
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
