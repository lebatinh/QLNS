package com.example.qunlnhns.nv.dsnv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.MainActivity;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DSNVActivity extends AppCompatActivity {
    ListView lvNv;
    ArrayList<NhanVien> arrNv;
    NVAdapter adapter;
    String localhost = DKActivity.localhost;
    String url = "http://"+localhost+"/user/getdata_nv.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsnv);

        lvNv = findViewById(R.id.lvNv);
        arrNv = new ArrayList<>();

        adapter = new NVAdapter(this, R.layout.nv_defaut, arrNv);
        lvNv.setAdapter(adapter);

        GetData(url);
        ImageButton btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Thực hiện tìm kiếm khi người dùng nhấn nút tìm kiếm trên bàn phím
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Thực hiện tìm kiếm theo mỗi ký tự thay đổi trong ô tìm kiếm
                performSearch(newText);
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void performSearch(String query) {
        ArrayList<NhanVien> searchResults = new ArrayList<>();

        for (NhanVien employee : arrNv) {
            if (employee.getMaNv().toLowerCase().contains(query.toLowerCase()) ||
                    employee.getHoTen().toLowerCase().contains(query.toLowerCase()) ||
                    employee.getChucVu().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(employee);
            }
        }
        adapter = new NVAdapter(this, R.layout.nv_defaut, searchResults);
        lvNv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void GetData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(DSNVActivity.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String maNv = object.optString("MaNv", "");
                            String hoTen = object.optString("HoTen", "");
                            String ngaySinh = object.optString("NgaySinh", "");
                            String gioiTinh = object.optString("GioiTinh", "");
                            String queQuan = object.optString("QueQuan", "");
                            String diaChi = object.optString("DiaChi", "");
                            String chucVu = object.optString("ChucVu", "");
                            String sdt = object.optString("SDT", "");

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
                            arrNv.add(new NhanVien(maNv, hoTen, ngaySinh, gioiTinh, queQuan, diaChi, chucVu, sdt, hinhBytes));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DSNVActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }
}