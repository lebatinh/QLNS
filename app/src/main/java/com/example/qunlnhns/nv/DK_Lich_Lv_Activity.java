package com.example.qunlnhns.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.qunlnhns.R;
import com.example.qunlnhns.user.DKActivity;
import com.example.qunlnhns.user.DNActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DK_Lich_Lv_Activity extends AppCompatActivity {

    LinearLayout lnrDkl;
    ImageView imgHinh, btnHome;
    TextView tvMaNv, tvHoTen, tvChucVu, tvSdt, tvNgay;
    EditText edtLyDo;
    Button btnDk, btnSua;
    String getmanv, manv, manv1, hoten, chucvu, sdt, lydo, time1, time2, time3, time4, time5, time6, time7, t2, t3, t4, t5, t6, t7, cn, startDate, endDate;
    String localhost = DKActivity.localhost;
    String url1 = "http://" + localhost + "/user/get_nv.php";
    String url2 = "http://" + localhost + "/user/dk_lich.php";
    String url3 = "http://" + localhost + "/user/update_dk_lich.php";
    String url4 = "http://" + localhost + "/user/check_lich_dk.php";
    String url5 = "http://" + localhost + "/user/get_lich_dk.php";
    private final TextView[] tvLuaChonArray = new TextView[7];
    private Date selectedStartDate;
    private Date selectedEndDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dk_lich_lv);

        AnhXa();
        Intent intent = getIntent();
        if (intent.hasExtra("manv")) {
            getmanv = intent.getStringExtra("manv");
            manv1 = getmanv;
        }
        check();
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnDk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryData();
            }
        });

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryData1();
            }
        });

        // Gán giá trị cho mảng TextView từ tvLuaChon1 đến tvLuaChon7
        for (int i = 1; i <= 7; i++) {
            int resId = getResources().getIdentifier("tvLuaChon" + i, "id", getPackageName());
            tvLuaChonArray[i - 1] = findViewById(resId);
            tvLuaChonArray[i - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMenu((TextView) v); // Truyền TextView được click vào hàm ShowMenu
                }
            });
        }
        // Gọi hàm showDatePickerDialog() khi TextView được click
        tvNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Tính toán ngày bắt đầu và kết thúc trong tuần
        LocalDate startOfCurrentWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfCurrentWeek = startOfCurrentWeek.plusDays(6);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Xử lý khi người dùng chọn ngày
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);

                    // Ngăn chọn tuần hiện tại và quá khứ
                    if (selectedDate.isBefore(endOfCurrentWeek.plusDays(1))) {
                        Toast.makeText(this, "Vui lòng chọn tuần từ tuần tiếp theo trở đi.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lấy ngày đầu tiên của tuần của ngày được chọn
                    LocalDate startOfSelectedWeek = selectedDate.with(DayOfWeek.MONDAY);
                    LocalDate endOfSelectedWeek = startOfSelectedWeek.plusDays(6);

                    try {
                        // Chuyển sang Date
                        Date startDateObj = Date.from(startOfSelectedWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        Date endDateObj = Date.from(endOfSelectedWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

                        // Set thời gian bắt đầu là thứ 2 và kết thúc là chủ nhật
                        selectedStartDate = startDateObj;
                        selectedEndDate = endDateObj;

                        // Hiển thị thông tin đã chọn
                        displaySelectedDates();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                currentDate.getYear(),
                currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth()
        );

        // Thiết lập ngày bắt đầu cho DatePickerDialog (không cho chọn từ tuần hiện tại trở về trước)
        datePickerDialog.getDatePicker().setMinDate(endOfCurrentWeek.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displaySelectedDates() {
        // Hiển thị thông tin đã chọn trong TextView
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        startDate = selectedStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);
        endDate = selectedEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);

        tvNgay.setText("Từ " + startDate + " đến " + endDate);
    }

    // Nếu người dùng ấn nút quay lại
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận quay lại");
        builder.setMessage("Bạn có muốn quay lại màn hình chính không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(DK_Lich_Lv_Activity.this, MainActivity.class));
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

    private void check() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    // Nhân viên đã tồn tại trong bảng dk_lich
                    // Thực hiện các bước liên quan (lấy dữ liệu từ bảng dk_lich)
                    Toast.makeText(DK_Lich_Lv_Activity.this, "Bạn đã từng đăng ký lịch!\n Giờ bạn có thể sửa lịch nếu muốn!", Toast.LENGTH_SHORT).show();
                    btnDk.setEnabled(false); // Sử dụng setEnabled thay vì setClickable
                    GetData(url5);
                } else if (response.equals("fail")) {
                    // Nhân viên chưa tồn tại trong bảng dk_lich
                    // Lấy dữ liệu từ bảng nhanvien
                    Toast.makeText(DK_Lich_Lv_Activity.this, "Bạn hãy đăng ký lịch mới!", Toast.LENGTH_SHORT).show();
                    getDataFromNhanVienTable(url1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DK_Lich_Lv_Activity.this, "Lỗi: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("manv", getmanv);
                return params;
            }
        };

        // Thêm yêu cầu kiểm tra vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getDataFromNhanVienTable(String url1) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Log.d("TAG", response.toString());
                        String manv = object.optString("MaNv", "");
                        String hoten = object.optString("HoTen", "");
                        String chucvu = object.optString("ChucVu", "");
                        String sdt = object.optString("SDT", "");

                        // Lấy chuỗi Base64 từ JSON
                        String hinhBase64 = object.getString("HinhAnh");
                        // Chuyển chuỗi Base64 thành mảng byte
                        byte[] hinhBytes = new byte[0];
                        hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                        // Lấy dữ liệu từ bảng nhanvien thành công, thực hiện các bước tiếp theo
                        if (manv.equals(manv1)) {
                            tvMaNv.setText(manv1);
                            tvHoTen.setText(hoten);
                            tvChucVu.setText(chucvu);
                            tvSdt.setText(sdt);

                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                imgHinh.setImageBitmap(bitmap);
                            }
                            break;
                        } else {
                            Toast.makeText(DK_Lich_Lv_Activity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DK_Lich_Lv_Activity.this, "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                Log.d("error", error.toString());
            }
        });

        // Thêm yêu cầu lấy dữ liệu vào hàng đợi Volley
        requestQueue.add(jsonArrayRequest);
    }

    private void GetData(String url5) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url5, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        manv = object.optString("MaNv", "");
                        hoten = object.optString("HoTen", "");
                        chucvu = object.optString("ChucVu", "");
                        sdt = object.optString("SDT", "");
                        lydo = object.optString("LyDo", "");
                        t2 = object.optString("T2", "");
                        t3 = object.optString("T3", "");
                        t4 = object.optString("T4", "");
                        t5 = object.optString("T5", "");
                        t6 = object.optString("T6", "");
                        t7 = object.optString("T7", "");
                        cn = object.optString("Cn", "");

                        // Lấy chuỗi Base64 từ JSON
                        String hinhBase64 = object.getString("HinhAnh");
                        // Chuyển chuỗi Base64 thành mảng byte
                        byte[] hinhBytes = new byte[0];

                        hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                        if (manv.equals(manv1)) {

                            tvMaNv.setText(manv1);
                            tvHoTen.setText(hoten);
                            tvChucVu.setText(chucvu);
                            tvSdt.setText(sdt);
                            edtLyDo.setText(lydo);

                            // Lặp qua mảng TextView để lấy dữ liệu cho time1 đến time7
                            for (i = 0; i < tvLuaChonArray.length; i++) {
                                if (!t2.isEmpty() || !t3.isEmpty() || !t4.isEmpty() || !t5.isEmpty() || !t6.isEmpty() || !t7.isEmpty() || !cn.isEmpty()) {
                                    switch (i) {
                                        case 0:
                                            tvLuaChonArray[i].setText(cn);
                                            break;
                                        case 1:
                                            tvLuaChonArray[i].setText(t2);
                                            break;
                                        case 2:
                                            tvLuaChonArray[i].setText(t3);
                                            break;
                                        case 3:
                                            tvLuaChonArray[i].setText(t4);
                                            break;
                                        case 4:
                                            tvLuaChonArray[i].setText(t5);
                                            break;
                                        case 5:
                                            tvLuaChonArray[i].setText(t6);
                                            break;
                                        case 6:
                                            tvLuaChonArray[i].setText(t7);
                                            break;
                                    }
                                }
                            }

                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                imgHinh.setImageBitmap(bitmap);
                            }
                            break;
                        }
                        break;

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DK_Lich_Lv_Activity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                Log.d("Error", error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void ShowMenu(TextView textView) {
        PopupMenu popupMenu = new PopupMenu(DK_Lich_Lv_Activity.this, textView);
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


    private void QueryData() {
        // Lặp qua mảng TextView để lấy dữ liệu cho time1 đến time7
        for (int i = 0; i < tvLuaChonArray.length; i++) {
            String timeValue = tvLuaChonArray[i].getText().toString().trim();
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
        hoten = tvHoTen.getText().toString().trim();
        chucvu = tvHoTen.getText().toString().trim();
        sdt = tvSdt.getText().toString().trim();
        lydo = edtLyDo.getText().toString().trim();
        if (!time1.isEmpty() && !time2.isEmpty() && !time3.isEmpty() && !time4.isEmpty() && !time5.isEmpty() && !time6.isEmpty() && !time7.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        showAlertDialog(DK_Lich_Lv_Activity.this, "Thông báo", "Đăng ký lịch thành công.");
                    } else if (response.equals("fail")) {
                        showAlertDialog(DK_Lich_Lv_Activity.this, "Cảnh báo!", "Đăng ký lịch không thành công!");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DK_Lich_Lv_Activity.this, "Lỗi đăng ký lịch: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Truyền tham số cho yêu cầu POST
                    Map<String, String> params = new HashMap<>();
                    params.put("manv", manv1);
                    params.put("hoten", hoten);
                    params.put("chucvu", chucvu);
                    params.put("sdt", sdt);
                    params.put("lydo", lydo);
                    params.put("t2", time2);
                    params.put("t3", time3);
                    params.put("t4", time4);
                    params.put("t5", time5);
                    params.put("t6", time6);
                    params.put("t7", time7);
                    params.put("cn", time1);

                    // Chuyển đổi ảnh từ ImageView sang mảng byte
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imgHinh.getDrawable();
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
        } else {
            showAlertDialog(DK_Lich_Lv_Activity.this, "Cảnh báo!", "Không được bỏ trống!");
        }

    }

    private void QueryData1() {
        // Lặp qua mảng TextView để lấy dữ liệu cho time1 đến time7
        for (int i = 0; i < tvLuaChonArray.length; i++) {
            String timeValue = tvLuaChonArray[i].getText().toString().trim();
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
        lydo = edtLyDo.getText().toString().trim();

        if (!time1.isEmpty() && !time2.isEmpty() && !time3.isEmpty() && !time4.isEmpty() && !time5.isEmpty() && !time6.isEmpty() && !time7.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        showAlertDialog(DK_Lich_Lv_Activity.this, "Thông báo", "Sửa lịch thành công.");
                    } else if (response.equals("fail")) {
                        showAlertDialog(DK_Lich_Lv_Activity.this, "Cảnh báo!", "Bạn chưa đăng ký lịch nên chưa thể sửa! Hãy đăng ký lịch trước");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DK_Lich_Lv_Activity.this, "Lỗi Sửa lịch: " + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Error:", error.toString());
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Truyền tham số cho yêu cầu POST
                    Map<String, String> params = new HashMap<>();
                    params.put("manv", manv1);
                    params.put("lydo", lydo);
                    params.put("t2", time2);
                    params.put("t3", time3);
                    params.put("t4", time4);
                    params.put("t5", time5);
                    params.put("t6", time6);
                    params.put("t7", time7);
                    params.put("cn", time1);

                    // Chuyển đổi ảnh từ ImageView sang mảng byte
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imgHinh.getDrawable();
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
        } else {
            showAlertDialog(DK_Lich_Lv_Activity.this, "Cảnh báo!", "Không được bỏ trống ô nào!");
        }

    }

    // Hàm để chuyển đổi Bitmap sang chuỗi Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void AnhXa() {
        imgHinh = findViewById(R.id.imgHinh);
        tvMaNv = findViewById(R.id.tvMaNv);
        tvHoTen = findViewById(R.id.tvHoTen);
        tvChucVu = findViewById(R.id.tvChucVu);
        tvSdt = findViewById(R.id.tvSdt);
        edtLyDo = findViewById(R.id.edtLyDo);
        btnDk = findViewById(R.id.btnDk);
        btnSua = findViewById(R.id.btnSua);
        btnHome = findViewById(R.id.btnHome);
        tvNgay = findViewById(R.id.tvNgay);
        lnrDkl = findViewById(R.id.lnrDkl);
    }
}