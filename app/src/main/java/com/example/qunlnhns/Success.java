package com.example.qunlnhns;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.example.qunlnhns.nv.DK_Lich_Lv_Activity;
import com.example.qunlnhns.nv.MainActivity;

public class Success extends AppCompatActivity {

    private String manv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent1 = new Intent(Success.this, MainActivity.class);
                intent1.putExtra("manv", manv);
                startActivity(intent1);
            }
        }, 2000);
    }
    @Override
    public void onBackPressed() {
        //nothing
    }

}