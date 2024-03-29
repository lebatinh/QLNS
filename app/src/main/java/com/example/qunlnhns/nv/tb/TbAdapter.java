package com.example.qunlnhns.nv.tb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qunlnhns.R;

import java.util.ArrayList;

public class TbAdapter extends BaseAdapter {
    private final Context context;
    private final int layout;
    private final ArrayList<Tb> tbList;

    public TbAdapter(Context context, int layout, ArrayList<Tb> tbList) {
        this.context = context;
        this.layout = layout;
        this.tbList = tbList;
    }

    @Override
    public int getCount() {
        return tbList.size();
    }

    @Override
    public Object getItem(int position) {
        return tbList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder {
        TextView tvTitle, tvTb, tvTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvTb = convertView.findViewById(R.id.tvTb);
            holder.tvTime = convertView.findViewById(R.id.tvTime);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tb tb = tbList.get(position);

        holder.tvTitle.setText(tb.getTitle());
        holder.tvTb.setText(tb.getTb());
        holder.tvTime.setText(tb.getTime());

        return convertView;
    }
}
