package com.example.user.QRCodeTool;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    static class ViewHolder {
        TextView textView;
        TextView textView2;
        TextView textView3;

    }

    private LayoutInflater inflater;
    private int itemLayoutId;
    private List<String> titles;

    private List<String> ID;
    private List<String> subtitle;


    ListViewAdapter(Context context, int itemLayoutId, List<String> itemNames , List<String> itemID , List<String>  itemTag) {
        super();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemLayoutId = itemLayoutId;
        this.titles = itemNames;
        this.ID = itemID;
        this.subtitle = itemTag;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(itemLayoutId, parent, false);
            // ViewHolder
            holder = new ViewHolder();

            holder.textView = convertView.findViewById(R.id.textView);

            holder.textView2 = convertView.findViewById(R.id.textView2);

            holder.textView3 = convertView.findViewById(R.id.textView3);

            convertView.setTag(holder);

//            if( subtitle.get(position).toString() == "1"){
//
//            }
            if(  Integer.valueOf(subtitle.get(position).toString()) == 1){

                convertView.setBackgroundColor(Color.rgb(255, 0, 102));

            }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.textView.setText(titles.get(position));

        holder.textView2.setText(ID.get(position));

        holder.textView3.setText(subtitle.get(position));
       // holder.textView3.setVisibility(View.GONE);;
            // ;
        }


        return convertView;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}