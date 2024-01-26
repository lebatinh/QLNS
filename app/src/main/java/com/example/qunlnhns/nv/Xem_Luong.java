package com.example.qunlnhns.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.DatabaseSQlite;
import com.example.qunlnhns.MainActivity;
import com.example.qunlnhns.R;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Xem_Luong extends AppCompatActivity {

    private ImageButton btnHome;
    private TextView tvNhanVien, tvNgay, tvLuong, tvTongThuong, tvTongPhat, tvChiPhi, tvTong, tvThuong, tvPhat;
    private String manv, thuong, phat;
    String localhost = DKActivity.localhost;
    private String url = "http://" + localhost + "/user/get_luong_nv.php";
    DatabaseSQlite databaseSQlite;
    private static final long INTERVAL = 5000; // Thời gian giữa các lần kiểm tra (5 giây)
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Kiểm tra kết nối Internet
            if (isNetworkConnected()) {
                // Thực hiện các yêu cầu mạng ở đây

                // Sau khi hoàn thành, lặp lại kiểm tra sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            } else {
                Toast.makeText(Xem_Luong.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_luong);
        AnhXa();
        // Khởi tạo đối tượng Database
        databaseSQlite = new DatabaseSQlite(this, "main.sqlite", null, 1);
        Pair<String, String> result = databaseSQlite.SELECT_MANV_MAIN();
        manv = result.first;

        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);

        GetLuong(url);

        tvThuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(Xem_Luong.this, "Lý do thưởng", thuong);
            }
        });
        tvPhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(Xem_Luong.this, "Lý do phạt", phat);
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void GetLuong(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(Xem_Luong.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();

                if (response.length() == 0) {
                    showAlertDialog(Xem_Luong.this, "Thông báo!", "Bạn chưa có bảng lương!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            // Xử lý thông tin từ JSON
                            String maNv = object.optString("MaNv", "");
                            String hoTen = object.optString("HoTen", "");
                            String luongCoBan = object.optString("LuongCoBan", "");
                            String lyDoThuong = object.optString("LyDoThuong", "");
                            String tienThuong = object.optString("TienThuong", "");
                            String lyDoPhat = object.optString("LyDoPhat", "");
                            String tienPhat = object.optString("TienPhat", "");
                            String chiPhiKhac = object.optString("ChiPhiKhac", "");
                            String tong = object.optString("Tong", "");
                            String thoiGian = object.optString("ThoiGian", "");

                            if (manv.equals(maNv)) {
                                tvNhanVien.setText(maNv + " - " + hoTen);
                                tvNgay.setText(thoiGian);
                                tvLuong.setText(luongCoBan);
                                thuong = lyDoThuong;
                                tvTongThuong.setText(tienThuong);
                                phat = lyDoPhat;
                                tvTongPhat.setText(tienPhat);
                                tvChiPhi.setText(chiPhiKhac);
                                tvTong.setText(tong);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Xem_Luong.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ngừng kiểm tra khi hoạt động được hủy
        handler.removeCallbacks(runnable);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận quay lại");
        builder.setMessage("Bạn có muốn quay lại màn hình chính không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(Xem_Luong.this, MainActivity.class));
                finish(); // Đóng màn hình hiện tại nếu cần
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Hủy bỏ dialog và tiếp tục ở màn hình hiện tại
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void AnhXa() {
        btnHome = findViewById(R.id.btnHome);
        tvNhanVien = findViewById(R.id.tvNhanVien);
        tvNgay = findViewById(R.id.tvNgay);
        tvLuong = findViewById(R.id.tvLuong);
        tvTongThuong = findViewById(R.id.tvTongThuong);
        tvTongPhat = findViewById(R.id.tvTongPhat);
        tvChiPhi = findViewById(R.id.tvChiPhi);
        tvTong = findViewById(R.id.tvTong);
        tvThuong = findViewById(R.id.tvThuong);
        tvPhat = findViewById(R.id.tvPhat);
    }

}