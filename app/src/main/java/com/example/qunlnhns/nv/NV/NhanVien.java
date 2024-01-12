package com.example.qunlnhns.nv.NV;

import android.util.Base64;

public class NhanVien {
    private String MaNv;
    private String HoTen;
    private String NgaySinh;
    private String GioiTinh;
    private String QueQuan;
    private String DiaChi;
    private String ChucVu;
    private String Sdt;
    private byte[] hinh;

    public NhanVien(String maNv, String hoTen, String ngaySinh, String gioiTinh, String queQuan, String diaChi, String chucVu, String sdt, byte[] hinh) {
        MaNv = maNv;
        HoTen = hoTen;
        NgaySinh = ngaySinh;
        GioiTinh = gioiTinh;
        QueQuan = queQuan;
        DiaChi = diaChi;
        ChucVu = chucVu;
        Sdt = sdt;
        this.hinh = hinh;
    }


    public String getMaNv() {
        return MaNv;
    }

    public void setMaNv(String maNv) {
        MaNv = maNv;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getNgaySinh() {
        return NgaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        NgaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return GioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        GioiTinh = gioiTinh;
    }

    public String getQueQuan() {
        return QueQuan;
    }

    public void setQueQuan(String queQuan) {
        QueQuan = queQuan;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public String getChucVu() {
        return ChucVu;
    }

    public void setChucVu(String chucVu) {
        ChucVu = chucVu;
    }

    public String getSdt() {
        return Sdt;
    }

    public void setSdt(String sdt) {
        Sdt = sdt;
    }

    public byte[] getHinh() {
        return hinh;
    }

    public void setHinh(byte[] hinh) {
        this.hinh = hinh;
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
