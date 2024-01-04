package com.example.qunlnhns.ql.dslich.t3;

import android.util.Base64;

public class List_Nv {
    private byte[] hinhanh;
    private String maNV;
    private String hoTen;

    public List_Nv(byte[] hinhanh, String maNV, String hoTen) {
        this.hinhanh = hinhanh;
        this.maNV = maNV;
        this.hoTen = hoTen;
    }

    public byte[] getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(byte[] hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    // Thêm phương thức này để trả về chuỗi Base64 của hình ảnh
    public String getHinhBase64() {
        if (hinhanh != null && hinhanh.length > 0) {
            return Base64.encodeToString(hinhanh, Base64.DEFAULT);
        } else {
            return null; // hoặc trả về một giá trị mặc định nếu hình ảnh là null hoặc rỗng
        }
    }

    // Thêm phương thức để thiết lập hình ảnh từ chuỗi Base64
    public void setHinhBase64(String base64String) {
        if (base64String != null && !base64String.isEmpty()) {
            hinhanh = Base64.decode(base64String, Base64.DEFAULT);
        }
    }
}
