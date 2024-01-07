package com.example.qunlnhns.ql.dslich;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.example.qunlnhns.Database;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.DK_Lich_Lv_Activity;
import com.example.qunlnhns.nv.MainActivity;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SuaLich extends AppCompatActivity {
    private ImageButton btnHome;
    private Button btnHuy, btnCapNhat;
    private ImageView hinhanh;
    private TextView hoten, manv, tvt2, tvt3, tvt4, tvt5, tvt6, tvt7, tvcn;
    private String maNv, hoTen, t2, t3, t4, t5, t6, t7, cn, time1, time2, time3, time4, time5, time6, time7;
    String localhost = DKActivity.localhost;
    private String url = "http://" + localhost + "/user/getdata_lich_lv.php";
    private String url1 = "http://" + localhost + "/user/update_lich.php";
    private String manv1;
    private TextView[] tvArray = new TextView[7];
    Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_lich);

        AnhXa();

        // Khởi tạo đối tượng Database
        database = new Database(this, "detail.sqlite", null, 1);
        manv1 = database.SELECT_MANV_DELTAIL();

        GetData(url);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuaLich.this, XepLichLv.class));
            }
        });
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suaLich();
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SuaLich.this);
                builder.setTitle("Bạn có chắc chắn hủy thay đổi?");
                builder.setPositiveButton("Đúng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SuaLich.this, XepLichLv.class));
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Show the AlertDialog
                builder.create().show();
            }
        });


        // Gán giá trị cho mảng TextView từ tvLuaChon1 đến tvLuaChon7
        for (int i = 1; i <= 7; i++) {
            int resId = getResources().getIdentifier("t" + i, "id", getPackageName());
            tvArray[i - 1] = findViewById(resId);
            tvArray[i - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMenu((TextView) v); // Truyền TextView được click vào hàm ShowMenu
                }
            });
        }
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
                startActivity(new Intent(SuaLich.this, MainActivity.class));
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

    private void suaLich() {
        AnhXa();
        QueryData(url1);
    }
    private void ShowMenu(TextView textView) {
        PopupMenu popupMenu = new PopupMenu(SuaLich.this, textView);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemSang) {
                    textView.setText("Ca Sáng");
                } else if (item.getItemId() == R.id.itemChieu) {
                    textView.setText("Ca Tối");
                } else if (item.getItemId() == R.id.itemFull) {
                    textView.setText("Full time");
                } else if (item.getItemId() == R.id.itemNghi) {
                    textView.setText("Nghỉ");
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void QueryData(String url1) {
        // Lặp qua mảng TextView để lấy dữ liệu cho time1 đến time7
        for (int i = 0; i < tvArray.length; i++) {
            String timeValue = tvArray[i].getText().toString().trim();
            switch (i) {
                case 0:
                    time1 = timeValue;
                    break;
                case 1:
                    time2 = timeValue;
                    break;
                case 2:
                    time3 = timeValue;
                    break;
                case 3:
                    time4 = timeValue;
                    break;
                case 4:
                    time5 = timeValue;
                    break;
                case 5:
                    time6 = timeValue;
                    break;
                case 6:
                    time7 = timeValue;
                    break;
            }
        }
        hoTen = hoten.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    showAlertDialog(SuaLich.this, "Thông báo", "Sửa thông tin nhân viên thành công! Bạn đã có thể xem nhân viên trong danh sách.");
                } else if (response.equals("fail")) {
                    showAlertDialog(SuaLich.this, "Cảnh báo!", "Sửa nhân viên không thành công!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SuaLich.this, "Lỗi Sửa nhân viên: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("manv", manv1);
                params.put("hoten", hoTen);
                params.put("t2", time2);
                params.put("t3", time3);
                params.put("t4", time4);
                params.put("t5", time5);
                params.put("t6", time6);
                params.put("t7", time7);
                params.put("cn", time1);

                // Chuyển đổi ảnh từ ImageView sang mảng byte
                BitmapDrawable bitmapDrawable = (BitmapDrawable) hinhanh.getDrawable();
                if (bitmapDrawable != null) {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    String encodedImage = bitmapToBase64(bitmap);
                    params.put("hinhanh", encodedImage);
                }

                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Hàm để chuyển đổi Bitmap sang chuỗi Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private void GetData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        maNv = object.optString("MaNv", "");
                        hoTen = object.optString("HoTen", "");
                        t2 = object.optString("T2", "");
                        t3 = object.optString("T3", "");
                        t4 = object.optString("T4", "");
                        t5 = object.optString("T5", "");
                        t6 = object.optString("T6", "");
                        t7 = object.optString("T7", "");
                        cn = object.optString("Cn", "");

                        // Lấy chuỗi Base64 từ JSON
                        String hinhBase64 = object.optString("HinhAnh", "");
                        // Chuyển chuỗi Base64 thành mảng byte
                        byte[] hinhBytes = new byte[0];

                        hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                        if (manv1.equals(maNv)) {
                            manv.setText(maNv);
                            hoten.setText(hoTen);

                            // Lặp qua mảng TextView để lấy dữ liệu cho time1 đến time7
                            for (i = 0; i < tvArray.length; i++) {
                                if (!t2.isEmpty() || !t3.isEmpty() || !t4.isEmpty() || !t5.isEmpty() || !t6.isEmpty() || !t7.isEmpty() || !cn.isEmpty()) {
                                    switch (i) {
                                        case 0:
                                            tvArray[i].setText(cn);
                                            break;
                                        case 1:
                                            tvArray[i].setText(t2);
                                            break;
                                        case 2:
                                            tvArray[i].setText(t3);
                                            break;
                                        case 3:
                                            tvArray[i].setText(t4);
                                            break;
                                        case 4:
                                            tvArray[i].setText(t5);
                                            break;
                                        case 5:
                                            tvArray[i].setText(t6);
                                            break;
                                        case 6:
                                            tvArray[i].setText(t7);
                                            break;
                                    }
                                }
                                else {
                                    showAlertDialog(SuaLich.this, "Cảnh báo!", "Không được bỏ trống");
                                }
                            }

                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                hinhanh.setImageBitmap(bitmap);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SuaLich.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("manv", manv1);
                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void AnhXa() {
        btnHome = findViewById(R.id.btnHome);
        btnCapNhat = findViewById(R.id.btnCapNhat);
        btnHuy = findViewById(R.id.btnHuy);
        hinhanh = findViewById(R.id.hinhanh);
        hoten = findViewById(R.id.hoten);
        manv = findViewById(R.id.manv);
        tvt2 = findViewById(R.id.t2);
        tvt3 = findViewById(R.id.t3);
        tvt4 = findViewById(R.id.t4);
        tvt5 = findViewById(R.id.t5);
        tvt6 = findViewById(R.id.t6);
        tvt7 = findViewById(R.id.t7);
        tvcn = findViewById(R.id.t1);

        maNv = manv.getText().toString().trim();
        hoTen = hoten.getText().toString().trim();
        t2 = tvt2.getText().toString().trim();
        t3 = tvt3.getText().toString().trim();
        t4 = tvt4.getText().toString().trim();
        t5 = tvt5.getText().toString().trim();
        t6 = tvt6.getText().toString().trim();
        t7 = tvt7.getText().toString().trim();
        cn = tvcn.getText().toString().trim();
    }

}