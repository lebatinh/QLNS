package com.example.qunlnhns.ql.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.example.qunlnhns.nv.dsnv.NVAdapter;
import com.example.qunlnhns.nv.dsnv.NhanVien;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChangeListNV extends AppCompatActivity {

    ListView lvNv;
    ArrayList<NhanVien> arrNv;
    NVAdapter adapter;
    String localhost = DKActivity.localhost;
    String url = "http://"+localhost+"/user/getdata_nv.php";
    String url1 = "http://"+localhost+"/user/delete_nv.php";

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

        lvNv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (arrNv != null && position >= 0 && position < arrNv.size()) {
                    Object item = parent.getItemAtPosition(position);
                    NhanVien nhanVien = (NhanVien) item;
                    String manv = nhanVien.getMaNv();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeListNV.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn muốn xóa hay sửa nhân viên có mã nhân viên " + manv + " này?");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("success")) {
                                        showAlertDialog(ChangeListNV.this, "Thông báo", "Xóa nhân viên thành công! Bạn đã có thể xem lại danh sách.");
                                    } else if (response.equals("fail")) {
                                        showAlertDialog(ChangeListNV.this, "Cảnh báo!", "Xóa nhân viên không thành công.");
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ChangeListNV.this, "Lỗi Xóa: " + error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Nullable
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    // Truyền tham số cho yêu cầu POST
                                    Map<String, String> params = new HashMap<>();
                                    params.put("manv", manv);

                                    return params;
                                }
                            };

                            // Thêm yêu cầu vào hàng đợi Volley
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                            arrNv.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Sửa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ChangeListNV.this, SuaNV.class);

                            intent.putExtra("MaNv", manv);
                            startActivity(intent);
                        }
                    });
                    builder.setNeutralButton("Thoát", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }else {
                    showAlertDialog(ChangeListNV.this, "Error", "Không có phần tử trong bảng");
                }
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void GetData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(ChangeListNV.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
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
                            String hinhBase64 = object.optString("HinhAnh", ""); // Thay đổi tên trường thành "hinhanh"

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
                Toast.makeText(ChangeListNV.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }
}