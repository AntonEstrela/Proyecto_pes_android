package com.pes.proyecto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class CansonsRecyclerViewAdapter extends RecyclerView.Adapter<CansonsRecyclerViewAdapter.ViewHolder> {
    private JSONArray values;
    private Context context;
    boolean admin = false;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtNom;
        public TextView txtData;
        public TextView txtCantants;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtNom = (TextView) v.findViewById(R.id.textView);
            txtCantants = (TextView) v.findViewById(R.id.textView2);
            txtData = (TextView) v.findViewById(R.id.textView3);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CansonsRecyclerViewAdapter(JSONArray myDataset, Context context, boolean admin) {
        values = myDataset;
        this.context = context;
        this.admin = admin;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CansonsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.recycler_view_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
            new GetCantants(holder).SendRequest("/Application/GetCantantsByCanso?canso=" + name);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        final String name2 = name;
        final String lyrics2 = lyrics;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name2 != null){
                    new AlertDialog.Builder(context)
                            .setTitle(name2 + " lyrics:")
                            .setMessage(lyrics2)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
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

    private void Delete(String name){
        if(!admin){
            Toast.makeText(context, "Registration requiered", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nom", name);
            new PostDelete(context).SendRequest(jsonObject, "/Application/DeleteCanso");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class PostDelete extends HttpPost{
        private WeakReference<Context> SingerActivityWeakReference;
        public PostDelete(Context activity){
            SingerActivityWeakReference = new WeakReference<>(activity);
        }
        @Override
        protected void onPostExecute(String s){
            Toast.makeText(SingerActivityWeakReference.get(), s, Toast.LENGTH_SHORT).show();
        }
    }

    private class GetCantants extends HttpGet{
        private ViewHolder holder;
        GetCantants(ViewHolder holder){
            this.holder = holder;
        }
        @Override
        protected void onPostExecute(String result) {
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
                holder.txtCantants.setText(sb.toString());
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