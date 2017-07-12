package com.mjkfab.mjkpipespooltracker;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class PipeListAdapter extends ArrayAdapter<String> {

    private final String[] dataList;

    PipeListAdapter(Context context, String[] label, String[] data) {
        super(context, R.layout.pipe_page_listview, label);
        this.dataList = data;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.pipe_page_listview,parent,false);

        String singleLabel = getItem(position);
        TextView labelText = (TextView) customView.findViewById(R.id.label_textView);
        TextView dataText = (TextView) customView.findViewById(R.id.data_TextView);
        labelText.setText(singleLabel);
        dataText.setText(dataList[position]);
        return customView;
    }
}
