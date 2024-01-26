package com.example.qunlnhns.ql.dslich.list_nv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qunlnhns.R;

import java.util.List;

public class List_Nv_Adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<List_Nv> nvList;

    public List_Nv_Adapter(Context context, int layout, List<List_Nv> nvList) {
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

    private class ViewHolder{
        ImageView imgHinhAnh;
        TextView textMaNV, textHoTen;
        CheckBox checkBox;
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

        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.imgHinhAnh = convertView.findViewById(R.id.imgHinhAnh);
            holder.textMaNV = convertView.findViewById(R.id.textMaNV);
            holder.textHoTen = convertView.findViewById(R.id.textHoTen);
            holder.checkBox = convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        List_Nv listNv = nvList.get(position);

        // Lấy chuỗi Base64 từ đối tượng nhanVien
        String base64String = listNv.getHinhBase64();

        // Kiểm tra xem chuỗi Base64 có hợp lệ không trước khi giải mã
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);

            // Sử dụng đối tượng Bitmap cho mục đích hiển thị hoặc xử lý khác
            holder.imgHinhAnh.setImageBitmap(bitmap);
        }

        holder.textMaNV.setText(listNv.getMaNV());
        holder.textHoTen.setText(listNv.getHoTen());

        // Lấy giá trị layoutType
        int layoutType = listNv.getLayoutType();

        // Kiểm tra giá trị layoutType để ẩn hiện CheckBox
        if (layoutType == 0) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        // Thiết lập trạng thái của CheckBox dựa trên giá trị isChecked của List_Nv
        holder.checkBox.setChecked(listNv.isChecked());

        // Sự kiện lắng nghe cho CheckBox để cập nhật trạng thái của List_Nv khi thay đổi
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listNv.setChecked(isChecked);
            }
        });

        return convertView;
    }
}
