package com.example.qunlnhns.ql;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.MainActivity;
import com.example.qunlnhns.ql.dslich.Them_Lich_Nv;
import com.example.qunlnhns.ql.dslich.XepLichLv;
import com.example.qunlnhns.ql.dslich.t3.List_Nv;
import com.example.qunlnhns.ql.dslich.t3.List_Nv_Adapter;
import com.example.qunlnhns.ql.dslich.t4.ThuongPhat;
import com.example.qunlnhns.ql.dslich.t4.ThuongPhatAdapter;
import com.example.qunlnhns.ql.nv.ThemNV;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LuongThuongPhat extends AppCompatActivity {

    private ImageButton btnHome, btnThem;
    private LinearLayout lnrNv, lnrXn;
    ListView lvNv, lvThuong, lvPhat;
    private FrameLayout frmXet;
    private TextView tvMaNvHoTen, tvNgay, tvTong;
    private EditText edtLuong, edtChiPhi;
    private Button btnGuiTb;
    private String maNv, hoTen, luongCoBan, lyDoThuong, tienThuong, lyDoPhat, tienPhat, chiPhiKhac, tong, thoiGian;
    ArrayList<ThuongPhat> arrThuongPhat;
    ArrayList<List_Nv> arrNv;
    ThuongPhatAdapter adapterTp;
    List_Nv_Adapter adapterNv;String localhost = DKActivity.localhost;
    String url1 = "http://" + localhost + "/user/get_nv_luong.php";
    String url2 = "http://" + localhost + "/user/insert_luong.php";
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
                Toast.makeText(LuongThuongPhat.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luong_thuong_phat);

        AnhXa();
        lvNv = findViewById(R.id.lvNv);
        arrNv = new ArrayList<>();

        adapterNv = new List_Nv_Adapter(this, R.layout.nv, arrNv);
        lvNv.setAdapter(adapterNv);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        GetNv(url2);

        lvNv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrNv != null && position >= 0 && position < arrNv.size()) {
                    Object item = parent.getItemAtPosition(position);
                    List_Nv list_nv = (List_Nv) item;
                    maNv = list_nv.getMaNV();
                    hoTen = list_nv.getHoTen();
                    tvMaNvHoTen.setText(maNv+" - "+hoTen);

                    AlertDialog.Builder builder = new AlertDialog.Builder(LuongThuongPhat.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn có chắc chắn muốn xét lương cho " + hoTen + " có mã nhân viên là " + maNv + "?");
                    builder.setPositiveButton("Đúng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lnrNv.setVisibility(View.GONE);
                            frmXet.setVisibility(View.VISIBLE);
                            lnrXn.setVisibility(View.VISIBLE);
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Hiển thị AlertDialog
                    builder.show();
                }
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvNgay.getText().toString().trim().isEmpty()){
                    showAlertDialog(LuongThuongPhat.this, "Cảnh báo!", "Bạn quên chưa chọn ngày cho lịch của lương!");
                }else {
                    QueryLuong(url1);
                }
            }
        });
        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);
    }

    private void QueryLuong(String url1) {
        final ProgressDialog progressDialog = new ProgressDialog(LuongThuongPhat.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("success")) {
                    showAlertDialog(LuongThuongPhat.this, "Thông báo", "Xét lương thành công! Bạn đã có thể xem danh sách lương.");
                } else if (response.equals("fail")) {
                    showAlertDialog(LuongThuongPhat.this, "Cảnh báo!", "Xét lương không thành công.\nThông tin nhân viên này đã tồn tại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LuongThuongPhat.this, "Lỗi Thêm nhân viên: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("maNv", maNv);
                params.put("hoTen", hoTen);
                luongCoBan = edtLuong.getText().toString().trim();
                params.put("luongCoBan", luongCoBan);
                params.put("lyDoThuong", lyDoThuong);
                params.put("tienThuong", tienThuong);
                params.put("lyDoPhat", lyDoPhat);
                params.put("tienPhat", tienPhat);
                chiPhiKhac = edtChiPhi.getText().toString().trim();
                params.put("chiPhiKhac", chiPhiKhac);
                tong = tvTong.getText().toString().trim();
                params.put("tong", tong);
                thoiGian = tvNgay.getText().toString().trim();
                params.put("thoiGian", thoiGian);

                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void GetNv(String url2) {
        final ProgressDialog progressDialog = new ProgressDialog(LuongThuongPhat.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(LuongThuongPhat.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String maNv = object.optString("MaNv", "");
                            String hoTen = object.optString("HoTen", "");

                            // Lấy chuỗi Base64 từ JSON
                            String hinhBase64 = object.optString("HinhAnh", "");
                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = new byte[0];

                            hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);

                            }
                            arrNv.add(new List_Nv(hinhBytes, maNv, hoTen, 0));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapterNv.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LuongThuongPhat.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        );
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
                startActivity(new Intent(LuongThuongPhat.this, MainActivity.class));
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
        btnThem = findViewById(R.id.btnThem);
        lnrNv = findViewById(R.id.lnrNv);
        lnrXn = findViewById(R.id.lnrXn);
        lvNv = findViewById(R.id.lvNv);
        lvPhat = findViewById(R.id.lvPhat);
        lvThuong = findViewById(R.id.lvThuong);
        frmXet = findViewById(R.id.frmXet);
        tvMaNvHoTen = findViewById(R.id.tvMaNvHoTen);
        tvNgay = findViewById(R.id.tvNgay);
        tvTong = findViewById(R.id.tvTong);
        edtLuong = findViewById(R.id.edtLuong);
        edtChiPhi = findViewById(R.id.edtChiPhi);
        btnGuiTb = findViewById(R.id.btnGuiTb);
    }

}