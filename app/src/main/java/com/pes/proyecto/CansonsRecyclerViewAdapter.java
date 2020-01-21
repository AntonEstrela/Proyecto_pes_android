package com.pes.proyecto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class CansonsRecyclerViewAdapter extends RecyclerView.Adapter<CansonsRecyclerViewAdapter.ViewHolder> {
    private JSONArray values;
    private SingerActivity context;
    private boolean admin = false;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtNom;
        TextView txtData;
        TextView txtCantants;
        public View layout;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtNom = v.findViewById(R.id.textView);
            txtCantants = v.findViewById(R.id.textView2);
            txtData = v.findViewById(R.id.textView3);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    CansonsRecyclerViewAdapter(JSONArray myDataset, SingerActivity context, boolean admin) {
        values = myDataset;
        this.context = context;
        this.admin = admin;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CansonsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.recycler_view_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) { //aixo s'executa per cada linea de la llista per omplir-la
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = null;
        String lyrics = null;
        if(values == null){
            return;
        }
        try {
            JSONObject jsonObject = values.getJSONObject(position);
            name = jsonObject.getString("nom");
            String data = jsonObject.getString("data");
            lyrics = jsonObject.getString("lletra");
            holder.txtNom.setText(name);
            holder.txtData.setText(data);
            new GetCantants(holder).SendRequest("/Application/GetCantantsByCanso?canso=" + name);//demanar llista de cantants d'aquella canço
        }
        catch (Exception e){
            e.printStackTrace();
        }
        final String name2 = name;
        final String lyrics2 = lyrics;
        holder.itemView.setOnClickListener(new View.OnClickListener() {//fer-ho clicable
            @Override
            public void onClick(View v) {//obrir el dialog amb lyrics i esborrar
                if(name2 != null){
                    new AlertDialog.Builder(context)
                            .setTitle(name2 + " lyrics:")
                            .setMessage(lyrics2)
                            .setPositiveButton(android.R.string.cancel, null)
                            .setNegativeButton("Delete song", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Delete(name2);
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void Delete(String name){//esborrar la canço
        if(!admin){
            Toast.makeText(context, "Registration requiered", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nom", name);
            new PostDelete(context).SendRequest(jsonObject, "/Application/DeleteCanso");//petició d'esborrar
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class PostDelete extends HttpPost{
        private WeakReference<SingerActivity> singerActivityWeakReference;
        PostDelete(SingerActivity singerActivity) {
            super(singerActivity.getApplicationContext());
            singerActivityWeakReference = new WeakReference<>(singerActivity);
        }

        @Override
        protected void onPostExecute(String s){
            Toast.makeText(contextWeakReference.get(), s, Toast.LENGTH_SHORT).show();
            singerActivityWeakReference.get().onResume();

        }
    }

    private class GetCantants extends HttpGet{
        private ViewHolder holder;
        GetCantants(ViewHolder holder){
            this.holder = holder;
        }
        @Override
        protected void onPostExecute(String result) {//quan arribi la resposta muntar un String amb els noms dels cantants d'aquella canço
            try{
                JSONArray jsonArray = new JSONArray(result);
                int size = jsonArray.length();
                StringBuilder sb = new StringBuilder();
                sb.append("Cantants: ");
                for(int i = 0; i < size; i++){
                    if(i != 0){
                        sb.append(", ");
                    }
                    sb.append(jsonArray.getJSONObject(i).getString("nom"));
                }
                holder.txtCantants.setText(sb.toString());//i guardar-ho al TextView que toca
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public int getItemCount() {
        if(values == null){
            return 0;
        }
        return values.length();
    }

}