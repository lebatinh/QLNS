package com.example.qunlnhns.nv.luong;

public class Luong {
    private String MaNv;
    private String HoTen;
    private String Thuong;
    private String Phat;
    private String Luong;

    public Luong(String maNv, String hoTen, String thuong, String phat, String luong) {
        MaNv = maNv;
        HoTen = hoTen;
        Thuong = thuong;
        Phat = phat;
        Luong = luong;
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

    public String getThuong() {
        return Thuong;
    }

    public void setThuong(String thuong) {
        Thuong = thuong;
    }

    public String getPhat() {
        return Phat;
    }

    public void setPhat(String phat) {
        Phat = phat;
    }

    public String getLuong() {
        return Luong;
    }

    public void setLuong(String luong) {
        Luong = luong;
    }
}
