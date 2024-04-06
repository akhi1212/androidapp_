package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.theindiecorp.khelodadmin.R;

public class SportsSpinnerAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> sports;
    LayoutInflater inflater;

    public SportsSpinnerAdapter(Context context, ArrayList<String> sports){
        this.context = context;
        this.sports = sports;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sports.size();
    }

    @Override
    public Object getItem(int i) {
        return sports.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.location_spinner_layout, null);
        TextView sport = view.findViewById(R.id.text1);
        sport.setText(sports.get(i));
        return view;
    }
}
