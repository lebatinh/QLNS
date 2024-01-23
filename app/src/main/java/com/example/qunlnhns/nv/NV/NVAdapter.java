package com.example.qunlnhns.nv.NV;

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
import com.example.qunlnhns.nv.NV.NhanVien;

import java.util.List;

public class NVAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<NhanVien> nvList;

    public NVAdapter(Context context, int layout, List<NhanVien> nvList) {
        this.context = context;
        this.layout = layout;
        this.nvList = nvList;
    }

    @Override
    public int getCount() {
        return nvList.size();
    }

    @Override
    public Object getItem(int position) {
        return nvList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        ImageView imgNv;
        TextView txtMaNv, txtName, txtCv;
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
            holder.imgNv = convertView.findViewById(R.id.imgNv);
            holder.txtName = convertView.findViewById(R.id.txtName);
            holder.txtCv = convertView.findViewById(R.id.txtCv);
            holder.txtMaNv = convertView.findViewById(R.id.txtMaNv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NhanVien nhanVien = nvList.get(position);

        // Lấy chuỗi Base64 từ đối tượng nhanVien
        String base64String = nhanVien.getHinhBase64();

        // Kiểm tra xem chuỗi Base64 có hợp lệ không trước khi giải mã
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);

            // Sử dụng đối tượng Bitmap cho mục đích hiển thị hoặc xử lý khác
            holder.imgNv.setImageBitmap(bitmap);
        }

        holder.txtName.setText(nhanVien.getHoTen());
        holder.txtCv.setText(nhanVien.getChucVu());
        holder.txtMaNv.setText(nhanVien.getMaNv());
        return convertView;
    }
}
