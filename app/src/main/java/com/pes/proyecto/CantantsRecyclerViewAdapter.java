package com.pes.proyecto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        public TextView txtNom;
        public TextView txtPais;
        //public ImageView imageView;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtNom = (TextView) v.findViewById(R.id.textView);
            txtPais = (TextView) v.findViewById(R.id.textView2);
            //imageView = (ImageView) v.findViewById(R.id.imageView1);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CantantsRecyclerViewAdapter(JSONArray myDataset, Context context, boolean admin) {
        values = myDataset;
        this.context = context;
        this.admin = admin;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CantantsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            //Picasso.with(context).load(values.get(position).getImatge().get(0)).into(holder.imageView);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        name2 = name.toString();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click(name2);
            }
        });

    }
    public void Click(String name){
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