package com.example.safeu2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    String listfeature[];
    int listimages[];
    LayoutInflater inflater;
    public CustomBaseAdapter(Context ctx, String [] featurearr, int [] featureimages){
        this.context=ctx;
        this.listfeature=featurearr;
        this.listimages=featureimages;
        inflater=LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return listfeature.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view,null);
        TextView txtView = (TextView) convertView.findViewById(R.id.textView);
        ImageView listimg = (ImageView) convertView.findViewById(R.id.image_icon);
        txtView.setText(listfeature[position]);
        listimg.setImageResource(listimages[position]);
        return convertView;
    }
}

