package com.example.qunlnhns.ql.dslich.t1;

import android.util.Base64;

public class Lich {
    private byte[] hinh;
    private String HoTen, MaNv;
    private String T2, T3, T4, T5, T6, T7, Cn;

    public byte[] getHinh() {
        return hinh;
    }

    public void setHinh(byte[] hinh) {
        this.hinh = hinh;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getMaNv() {
        return MaNv;
    }

    public void setMaNv(String maNv) {
        MaNv = maNv;
    }

    public String getT2() {
        return T2;
    }

    public void setT2(String t2) {
        T2 = t2;
    }

    public String getT3() {
        return T3;
    }

    public void setT3(String t3) {
        T3 = t3;
    }

    public String getT4() {
        return T4;
    }

    public void setT4(String t4) {
        T4 = t4;
    }

    public String getT5() {
        return T5;
    }

    public void setT5(String t5) {
        T5 = t5;
    }

    public String getT6() {
        return T6;
    }

    public void setT6(String t6) {
        T6 = t6;
    }

    public String getT7() {
        return T7;
    }

    public void setT7(String t7) {
        T7 = t7;
    }

    public String getCn() {
        return Cn;
    }

    public void setCn(String cn) {
        Cn = cn;
    }

    public Lich(byte[] hinh, String maNv, String hoTen, String t2, String t3, String t4, String t5, String t6, String t7, String cn) {
        this.hinh = hinh;
        HoTen = hoTen;
        MaNv = maNv;
        T2 = t2;
        T3 = t3;
        T4 = t4;
        T5 = t5;
        T6 = t6;
        T7 = t7;
        Cn = cn;
    }

    // Thêm phương thức này để trả về chuỗi Base64 của hình ảnh
    public String getHinhBase64() {
        if (hinh != null && hinh.length > 0) {
            return Base64.encodeToString(hinh, Base64.DEFAULT);
        } else {
            return null; // hoặc trả về một giá trị mặc định nếu hình ảnh là null hoặc rỗng
        }
    }

    // Thêm phương thức để thiết lập hình ảnh từ chuỗi Base64
    public void setHinhBase64(String base64String) {
        if (base64String != null && !base64String.isEmpty()) {
            hinh = Base64.decode(base64String, Base64.DEFAULT);
        }
    }
}
