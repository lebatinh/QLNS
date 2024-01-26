package com.example.qunlnhns.ql.dslich.thuongphat;

public class ThuongPhat {
    private String liDo;
    private String soTien;

    public ThuongPhat(String liDo, String soTien) {
        this.liDo = liDo;
        this.soTien = soTien;
    }

    public String getLiDo() {
        return liDo;
    }

    public void setLiDo(String liDo) {
        this.liDo = liDo;
    }

    public String getSoTien() {
        return soTien;
    }

    public void setSoTien(String soTien) {
        this.soTien = soTien;
    }
}
