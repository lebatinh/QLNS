package com.example.qunlnhns.ql.dslich;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.qunlnhns.ql.dslich.t3.List_Nv;
import com.example.qunlnhns.ql.dslich.t3.List_Nv_Adapter;
import com.example.qunlnhns.user.DKActivity;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Them_Lich_Nv extends AppCompatActivity {

    ListView listNv;
    ArrayList<List_Nv> arrListNv;
    List_Nv_Adapter adapterNv;
    String localhost = DKActivity.localhost;
    String url1 = "http://" + localhost + "/user/get_lich.php";
    String url2 = "http://" + localhost + "/user/insert_lich_lv.php";
    private ImageView hinhanh;
    private TextView hoten, manv, tvt2, tvt3, tvt4, tvt5, tvt6, tvt7, tvcn, tvNgay;
    private String maNv, hoTen, t2, t3, t4, t5, t6, t7, cn, time1, time2, time3, time4, time5, time6, time7, startDate, endDate;
    private Button btnThem, btnThoat;
    private ImageButton btnHome;
    private LinearLayout lnrList, lnrLich, lnrThem;
    private TextView[] tvLuaChonArray = new TextView[7];
    private String m1;
    private Date selectedStartDate;
    private Date selectedEndDate;
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
                Toast.makeText(Them_Lich_Nv.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_lich_nv);

        listNv = findViewById(R.id.listNv);
        arrListNv = new ArrayList<>();

        adapterNv = new List_Nv_Adapter(this, R.layout.nv, arrListNv);
        listNv.setAdapter(adapterNv);

        AnhXa();
        GetData(url1);
        AndroidThreeTen.init(this);

        listNv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrListNv != null && position >= 0 && position < arrListNv.size()) {
                    Object item = parent.getItemAtPosition(position);
                    List_Nv list_nv = (List_Nv) item;
                    maNv = list_nv.getMaNV();
                    hoTen = list_nv.getHoTen();

                    m1 = maNv;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Them_Lich_Nv.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn có chắc chắn muốn thêm lịch làm việc cho " + hoTen + " có mã nhân viên là " + maNv + "?");
                    builder.setPositiveButton("Đúng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lnrList.setVisibility(View.GONE);
                            lnrLich.setVisibility(View.VISIBLE);
                            lnrThem.setVisibility(View.VISIBLE);
                            GetData1(url1);
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

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Gọi hàm showDatePickerDialog() khi TextView được click
        tvNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Gán giá trị cho mảng TextView từ tvLuaChon1 đến tvLuaChon7
        for (int i = 1; i <= 7; i++) {
            int resId = getResources().getIdentifier("t" + i, "id", getPackageName());
            tvLuaChonArray[i - 1] = findViewById(resId);
            tvLuaChonArray[i - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowMenu((TextView) v); // Truyền TextView được click vào hàm ShowMenu
                }
            });
        }
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if tvNgay is empty
                if (tvNgay.getText().toString().trim().isEmpty()) {
                    showAlertDialog(Them_Lich_Nv.this, "Cảnh báo!", "Bạn quên chưa chọn ngày cho lịch!");
                } else {
                    boolean anyDayEmpty = false; // Flag to track if any day is empty

                    // tvNgay is not empty, proceed to the loop
                    for (int i = 0; i < 7; i++) {
                        String time = tvLuaChonArray[i].getText().toString().trim();

                        // Set the day based on the value of i
                        String t;
                        if (i == 0) {
                            t = "Chủ nhật";
                        } else {
                            t = "Thứ " + (i + 1);
                        }

                        if (time.isEmpty()) {
                            showAlertDialog(Them_Lich_Nv.this, "Cảnh báo!", "Bạn quên chưa chọn Ca cho " + t + "!");
                            anyDayEmpty = true; // Set the flag to true if any day is empty
                        }
                    }

                    // Check the flag before proceeding
                    if (!anyDayEmpty) {
                        QueryData(url2);
                    }
                }
            }
        });
        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);
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
        builder.setMessage("Bạn có chắc muốn thoát không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(Them_Lich_Nv.this, XepLichLv.class));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Tính toán ngày bắt đầu và kết thúc trong tuần
        LocalDate startOfCurrentWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfCurrentWeek = startOfCurrentWeek.plusDays(6);

        // Tính toán ngày bắt đầu và kết thúc của tuần sau
        LocalDate startOfNextWeek = startOfCurrentWeek.plusWeeks(1);
        LocalDate endOfNextWeek = startOfNextWeek.plusDays(6);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Xử lý khi người dùng chọn ngày
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);

                    // Ngăn chọn các ngày sau tuần sau
                    if (selectedDate.isAfter(endOfNextWeek)) {
                        // Không cho phép chọn và thoát khỏi hàm
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
        datePickerDialog.getDatePicker().setMinDate(startOfCurrentWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        // Thiết lập ngày kết thúc cho DatePickerDialog (không cho chọn từ tuần sau trở đi)
        datePickerDialog.getDatePicker().setMaxDate(endOfNextWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

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

    private void ShowMenu(TextView textView) {
        PopupMenu popupMenu = new PopupMenu(Them_Lich_Nv.this, textView);
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

    private void QueryData(String url2) {
        final ProgressDialog progressDialog = new ProgressDialog(Them_Lich_Nv.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

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
        maNv = manv.getText().toString().trim();
        hoTen = hoten.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("success")) {
                    showAlertDialog(Them_Lich_Nv.this, "Thông báo", "Thêm thông tin nhân viên thành công! Bạn đã có thể xem nhân viên trong danh sách.");
                    startActivity(new Intent(Them_Lich_Nv.this, XepLichLv.class));
                } else if (response.equals("fail")) {
                    showAlertDialog(Them_Lich_Nv.this, "Cảnh báo!", "Thêm nhân viên không thành công.\nThông tin nhân viên này đã tồn tại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Them_Lich_Nv.this, "Lỗi Thêm nhân viên: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("manv", m1);
                params.put("hoten", hoTen);
                params.put("t2", time2);
                params.put("t3", time3);
                params.put("t4", time4);
                params.put("t5", time5);
                params.put("t6", time6);
                params.put("t7", time7);
                params.put("cn", time1);
                params.put("startDate", startDate);
                params.put("endDate", endDate);

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

    private void GetData1(String url1) {
        final ProgressDialog progressDialog = new ProgressDialog(Them_Lich_Nv.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(Them_Lich_Nv.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            maNv = object.optString("MaNv", "");
                            hoTen = object.optString("HoTen", "");

                            // Lấy chuỗi Base64 từ JSON
                            String hinhBase64 = object.optString("HinhAnh", "");
                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = new byte[0];

                            hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);
                            if (maNv.equals(m1)) {
                                // Kiểm tra xem mảng byte có dữ liệu không
                                if (hinhBytes != null && hinhBytes.length > 0) {
                                    // Chuyển mảng byte thành Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                    hinhanh.setImageBitmap(bitmap);
                                }
                                manv.setText(m1);
                                hoten.setText(hoTen);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Them_Lich_Nv.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void GetData(String url1) {
        final ProgressDialog progressDialog = new ProgressDialog(Them_Lich_Nv.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(Them_Lich_Nv.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
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
                            arrListNv.add(new List_Nv(hinhBytes, maNv, hoTen, 0));
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
                Toast.makeText(Them_Lich_Nv.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void AnhXa() {
        btnThem = findViewById(R.id.btnThem);
        btnThoat = findViewById(R.id.btnThoat);
        btnHome = findViewById(R.id.btnHome);
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
        tvNgay = findViewById(R.id.tvNgay);
        lnrList = findViewById(R.id.lnrList);
        lnrLich = findViewById(R.id.lnrLich);
        lnrThem = findViewById(R.id.lnrThem);

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