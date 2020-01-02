package com.pes.proyecto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CansonsRecyclerViewAdapter extends RecyclerView.Adapter<CansonsRecyclerViewAdapter.ViewHolder> {
    private JSONArray values;
    private Context context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtNom;
        public TextView txtData;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtNom = (TextView) v.findViewById(R.id.textView);
            txtData = (TextView) v.findViewById(R.id.textView3);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CansonsRecyclerViewAdapter(JSONArray myDataset, Context context) {
        values = myDataset;
        this.context = context;
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
        if(values == null){
            return;
        }
        try {
            JSONObject jsonObject = values.getJSONObject(position);
            String name = jsonObject.getString("nom");
            int data = jsonObject.getInt("data");
            holder.txtNom.setText(name);
            holder.txtData.setText(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    //public void Click(Track track){
    //Intent intent = new Intent(context, TrackInfo.class);
    //intent.putExtra(EXTRA_MESSAGE, track);
    //context.startActivity(intent);
    //}
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(values == null){
            return 0;
        }
        return values.length();
    }

}