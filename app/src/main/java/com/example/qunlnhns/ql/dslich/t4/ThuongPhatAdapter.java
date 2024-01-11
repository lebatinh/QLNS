package com.example.qunlnhns.ql.dslich.t4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.qunlnhns.R;

import java.util.List;

public class ThuongPhatAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ThuongPhat> thuongPhatList;

    public ThuongPhatAdapter(Context context, int layout, List<ThuongPhat> thuongPhatList) {
        this.context = context;
        this.layout = layout;
        this.thuongPhatList = thuongPhatList;
    }

    @Override
    public int getCount() {
        return thuongPhatList.size();
    }

    @Override
    public Object getItem(int position) {
        return thuongPhatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        TextView tvLiDo, tvSoTien;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.tvLiDo = convertView.findViewById(R.id.tvLiDo);
            holder.tvSoTien = convertView.findViewById(R.id.tvSoTien);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ThuongPhat thuongPhat = thuongPhatList.get(position);

        holder.tvLiDo.setText(thuongPhat.getLiDo());
        holder.tvSoTien.setText(thuongPhat.getSoTien());

        return convertView;
    }
}
