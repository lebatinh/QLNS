package com.example.qunlnhns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HDSD extends AppCompatActivity {

    DatabaseSQlite databaseSQlite;
    CardView cardViewAdmin;
    ImageView btnHome;
    private TextView[] tvArray1 = new TextView[17];
    private TextView[] tvArray2 = new TextView[17];
    private boolean[] isTv2Visible = new boolean[17];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdsd);

        // Khởi tạo đối tượng Database
        databaseSQlite = new DatabaseSQlite(this, "main.sqlite", null, 1);
        Pair<String, String> result = databaseSQlite.SELECT_MANV_MAIN();
        String admin = result.second;

        cardViewAdmin = findViewById(R.id.cardViewAdmin);
        if (admin.equals("1")) {
            cardViewAdmin.setVisibility(View.VISIBLE);
        } else if (admin.equals("0")) {
            cardViewAdmin.setVisibility(View.GONE);
        }
        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> onBackPressed());
        for (int i = 0; i < 17; i++) {
            int resId1 = getResources().getIdentifier("tv" + (i + 1), "id", getPackageName());
            int resId2 = getResources().getIdentifier("tv_" + (i + 1), "id", getPackageName());

            tvArray1[i] = findViewById(resId1);
            tvArray2[i] = findViewById(resId2);

            if (tvArray1[i] != null) {
                final int finalI = i;
                tvArray1[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleVisibility(finalI);
                    }
                });
            } else {
                Log.e("Error", "TextView tv" + (i + 1) + " is null");
            }
        }
    }

    private void toggleVisibility(int index) {
        // Hiển thị hoặc ẩn tv_1 tương ứng
        isTv2Visible[index] = !isTv2Visible[index];
        tvArray2[index].setVisibility(isTv2Visible[index] ? View.VISIBLE : View.GONE);
    }
}