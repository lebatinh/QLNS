package com.example.qunlnhns.nv.message;

import android.graphics.Bitmap;
import android.util.Base64;

public class msg {
    byte[] hinh;
    String id;
    String name;
    String lastMessage;
    String lastMessageTime;

    public msg(Bitmap hinhanh, String id, String name, String lastMessage) {
    }

    public msg(byte[] hinh, String id, String name, String lastMessage, String lastMessageTime) {
        this.hinh = hinh;
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public byte[] getHinh() {
        return hinh;
    }

    public void setHinh(byte[] hinh) {
        this.hinh = hinh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
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
