package com.example.qunlnhns.nv.tb;

public class Tb {
    private String title;
    private String tb;
    private String time;

    public Tb(String title, String tb, String time) {
        this.title = title;
        this.tb = tb;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTb() {
        return tb;
    }

    public void setTb(String tb) {
        this.tb = tb;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
