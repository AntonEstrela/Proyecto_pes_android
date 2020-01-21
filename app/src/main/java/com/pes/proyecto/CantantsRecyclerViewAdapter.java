package com.pes.proyecto;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CantantsRecyclerViewAdapter extends RecyclerView.Adapter<CantantsRecyclerViewAdapter.ViewHolder> {
    private JSONArray values;
    private Context context;
    private boolean admin;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtNom;
        TextView txtPais;
        //public ImageView imageView;
        public View layout;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtNom = v.findViewById(R.id.textView);
            txtPais = v.findViewById(R.id.textView2);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    CantantsRecyclerViewAdapter(JSONArray myDataset, Context context, boolean admin) {
        values = myDataset;
        this.context = context;
        this.admin = admin;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CantantsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {//aixó s'executa per omplir de dades cada linea
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = "null";
        final String name2;
        if(values == null){
            return;
        }
        try {
            JSONObject jsonObject = values.getJSONObject(position);
            name = jsonObject.getString("nom");
            String pais = jsonObject.getString("pais");
            holder.txtNom.setText(name);
            holder.txtPais.setText(pais);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        name2 = name;
        holder.itemView.setOnClickListener(new View.OnClickListener() {//que sigui clicable el cantant per obrir l'activity
            @Override
            public void onClick(View v) {
                Click(name2);
            }
        });

    }
    private void Click(String name){//obrir l'activity del cantant
        Intent intent = new Intent(context, SingerActivity.class);
        intent.putExtra("cantant", name);
        intent.putExtra("admin", admin);
        context.startActivity(intent);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(values == null){
            return 0;
        }
        return values.length();
    }

}