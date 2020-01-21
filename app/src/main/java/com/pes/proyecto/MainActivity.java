package com.pes.proyecto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

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
        EditName = findViewById(R.id.editText);
        EditPass = findViewById(R.id.editText2);
        EditServer = findViewById(R.id.editText3);
        context = this;

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        EditName.setText(sharedPref.getString("User", ""));
        EditPass.setText(sharedPref.getString("Pass", ""));
        EditServer.setText(sharedPref.getString("Server", ""));

    }

    public void LoginGuest(View view){//login com a guest (boto guest)
        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
        intent.putExtra("admin", false);//sense ser admin
        startActivity(intent);
    }

    public void LoginClick2(View view){//login com a admin (boto login)
        new PostLogin(this, this).SendRequest(getJson(), "/Application/loginandroid");
    }
    private JSONObject getJson(){//generar un json amb usuari i contrasenya, ja que s'utilitza per login d'admin i per registre
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

    public void GotLogin(String result){//processar el resultat del login
        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        if(result.equals("OK")){//si ok, guardar les coses i cap a la nova activity
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("User", EditName.getText().toString());
            editor.putString("Pass", EditPass.getText().toString());
            editor.putString("Server", ConfigSingleton.getInstance().ServerAddress);
            editor.commit();//si, commit i no apply, perque amb apply no sempre guardava si es tanca rapid l'app i tampoc frena tant


            Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
            intent.putExtra("admin", true);//activity amb drets d'editar
            startActivity(intent);
        }
        if(result.equals("FAIL")){//si fail ofereix registrar l'usuari
            new AlertDialog.Builder(context)
                    .setTitle("Login failed")
                    .setMessage("Register user " + EditName.getText().toString() + "?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new PostRegister(context).SendRequest(getJson(), "/Application/registerandroid");//i ho fa si vols
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }


    private static class PostLogin extends HttpPost{
        WeakReference<MainActivity> mainActivityWeakReference;
        PostLogin(Context context, MainActivity mainActivity) {
            super(context);
            mainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        protected void onPostExecute(String result) {

            mainActivityWeakReference.get().GotLogin(result);

        }
    }

    private static class PostRegister extends HttpPost{
        PostRegister(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(contextWeakReference.get(), s, Toast.LENGTH_SHORT).show();
        }
    }

}
