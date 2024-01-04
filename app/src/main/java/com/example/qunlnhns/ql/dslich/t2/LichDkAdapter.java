package com.example.qunlnhns.ql.dslich.t2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qunlnhns.R;
import com.example.qunlnhns.ql.dslich.t2.LichDk;

import java.util.List;

public class LichDkAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<LichDk> lichDkList;

    public LichDkAdapter(Context context, int layout, List<LichDk> lichDkList) {
        this.context = context;
        this.layout = layout;
        this.lichDkList = lichDkList;
    }

    @Override
    public int getCount() {
        return lichDkList.size();
    }

    @Override
    public Object getItem(int position) {
        return lichDkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        ImageView hinhanh;
        TextView hoten, manv, lydo, t2, t3, t4, t5, t6, t7, cn;
    }

    private static Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.hinhanh = convertView.findViewById(R.id.hinhanh);
            holder.hoten = convertView.findViewById(R.id.hoten);
            holder.manv = convertView.findViewById(R.id.manv);
            holder.lydo = convertView.findViewById(R.id.lydo);
            holder.t2 = convertView.findViewById(R.id.t2);
            holder.t3 = convertView.findViewById(R.id.t3);
            holder.t4 = convertView.findViewById(R.id.t4);
            holder.t5 = convertView.findViewById(R.id.t5);
            holder.t6 = convertView.findViewById(R.id.t6);
            holder.t7 = convertView.findViewById(R.id.t7);
            holder.cn = convertView.findViewById(R.id.cn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LichDk lichDk = lichDkList.get(position);

        // Lấy chuỗi Base64 từ đối tượng nhanVien
        String base64String = lichDk.getHinhBase64();

        // Kiểm tra xem chuỗi Base64 có hợp lệ không trước khi giải mã
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);

            // Sử dụng đối tượng Bitmap cho mục đích hiển thị hoặc xử lý khác
            holder.hinhanh.setImageBitmap(bitmap);
        }

        holder.hoten.setText(lichDk.getHoTen());
        holder.manv.setText(lichDk.getMaNv());
        holder.lydo.setText(lichDk.getLyDo());
        holder.t2.setText(lichDk.getT2());
        holder.t3.setText(lichDk.getT3());
        holder.t4.setText(lichDk.getT4());
        holder.t5.setText(lichDk.getT5());
        holder.t6.setText(lichDk.getT6());
        holder.t7.setText(lichDk.getT7());
        holder.cn.setText(lichDk.getCn());

        return convertView;
    }
}
