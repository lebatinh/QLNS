package com.example.qunlnhns.nv.luong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.tb.Tb;
import com.example.qunlnhns.nv.tb.TbAdapter;

import java.util.ArrayList;

public class LuongAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Luong> luongList;

    public LuongAdapter(Context context, int layout, ArrayList<Luong> luongList) {
        this.context = context;
        this.layout = layout;
        this.luongList = luongList;
    }

    @Override
    public int getCount() {
        return luongList.size();
    }

    @Override
    public Object getItem(int position) {
        return luongList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView tvManv, tvHoten, tvThuong, tvPhat, tvLuong;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.tvManv = convertView.findViewById(R.id.tvManv);
            holder.tvHoten = convertView.findViewById(R.id.tvHoten);
            holder.tvThuong = convertView.findViewById(R.id.tvThuong);
            holder.tvPhat = convertView.findViewById(R.id.tvPhat);
            holder.tvLuong = convertView.findViewById(R.id.tvLuong);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Luong luong = luongList.get(position);

        holder.tvManv.setText(luong.getMaNv());
        holder.tvHoten.setText(luong.getHoTen());
        holder.tvThuong.setText(luong.getThuong());
        holder.tvPhat.setText(luong.getPhat());
        holder.tvLuong.setText(luong.getLuong());

        return convertView;
    }
}
