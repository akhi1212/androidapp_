package com.theindiecorp.khelodapp.Adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.theindiecorp.khelodapp.Model.venueForEvents;
import com.theindiecorp.khelodapp.R;

import java.util.List;

public class LocationSpinnerAdapter extends ArrayAdapter<venueForEvents> {

    LayoutInflater inflater;

    public LocationSpinnerAdapter(Activity context, int resourceId, int textviewId , List<venueForEvents> data){
        super(context,resourceId,textviewId,data);
        inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        venueForEvents venue = getItem(position);

        View rowView = inflater.inflate(R.layout.list_item_layout,null,true);

        TextView txtTitle = rowView.findViewById(R.id.title);
        txtTitle.setText(venue.getName());

        return rowView;
    }
}
