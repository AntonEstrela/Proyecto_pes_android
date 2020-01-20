package com.pes.proyecto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText EditName;
    EditText EditPass;
    EditText EditServer;
    Context context;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditName = (EditText) findViewById(R.id.editText);
        EditPass = (EditText) findViewById(R.id.editText2);
        EditServer = (EditText) findViewById(R.id.editText3);
        context = this;

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        EditName.setText(sharedPref.getString("User", ""));
        EditPass.setText(sharedPref.getString("Pass", ""));
        EditServer.setText(sharedPref.getString("Server", ""));

    }

    public void LoginGuest(View view){
        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
        intent.putExtra("admin", false);
        startActivity(intent);
    }

    public void LoginClick2(View view){
        new PostLogin().SendRequest(getJson(), "/Application/loginandroid");
    }
    private JSONObject getJson(){
        try {
            ConfigSingleton.getInstance().ServerAddress = EditServer.getText().toString();
            if(ConfigSingleton.getInstance().ServerAddress.equals("")){
                Toast.makeText(getApplicationContext(), "Server empty", Toast.LENGTH_SHORT).show();
                return null;
            }
            String nom = EditName.getText().toString();
            if(nom.equals("")){
                Toast.makeText(getApplicationContext(), "User name empty", Toast.LENGTH_SHORT).show();
                return null;
            }
            JSONObject obj = new JSONObject();
            obj.put("user.nom", nom);
            obj.put("user.password", EditPass.getText().toString());
            return obj;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private class PostLogin extends HttpPost{
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if(result.equals("OK")){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("User", EditName.getText().toString());
                editor.putString("Pass", EditPass.getText().toString());
                editor.putString("Server", ConfigSingleton.getInstance().ServerAddress);
                editor.commit();


                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                intent.putExtra("admin", true);
                startActivity(intent);
            }
            if(result.equals("FAIL")){
                new AlertDialog.Builder(context)
                        .setTitle("Login failed")
                        .setMessage("Register user " + EditName.getText().toString() + "?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new PostRegister().SendRequest(getJson(), "/Application/registerandroid");
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }


        }
    }

    private class PostRegister extends HttpPost{
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }

}
