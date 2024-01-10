package com.example.qunlnhns.ql.dslich.t4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

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
        EditText edtLiDo, edtSoTien;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.edtLiDo = convertView.findViewById(R.id.edtLiDo);
            holder.edtSoTien = convertView.findViewById(R.id.edtSoTien);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ThuongPhat thuongPhat = thuongPhatList.get(position);

        holder.edtLiDo.setText(thuongPhat.getLiDo());
        holder.edtSoTien.setText(thuongPhat.getSoTien());

        return convertView;
    }
}
