package com.example.qunlnhns.ql.dslich;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.qunlnhns.Notification;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.MainActivity;
import com.example.qunlnhns.ql.dslich.t1.Lich;
import com.example.qunlnhns.ql.dslich.t1.LichAdapter;
import com.example.qunlnhns.ql.dslich.t2.LichDk;
import com.example.qunlnhns.ql.dslich.t2.LichDkAdapter;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XepLichLv extends AppCompatActivity {
    ListView lvDkLich;
    ListView lvLich;
    ArrayList<LichDk> arrDkLich;
    ArrayList<Lich> arrLich;
    LichDkAdapter adapterDk;
    LichAdapter adapter;
    String localhost = DKActivity.localhost;
    String url1 = "http://" + localhost + "/user/getdata_dk_lich.php";
    String url2 = "http://" + localhost + "/user/getdata_lich_lv.php";
    String url3 = "http://" + localhost + "/user/delete_lich.php";
    String url4 = "http://" + localhost + "/user/gui_tb.php";
    private LinearLayout lnrLich, lnrDk, lnrXn;
    private TextView tvDsDk, tvLichLv, tvLichTime;
    private ImageButton btnHome, btnThem;
    private Button btnGuiTb;
    String tg, timeStart, timeEnd, startDate, endDate;
    private Date selectedStartDate;
    private Date selectedEndDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xep_lich_lv);

        AnhXa();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(XepLichLv.this, Them_Lich_Nv.class));
            }
        });

        tvLichLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrDk.setVisibility(View.VISIBLE);
                lnrLich.setVisibility(View.GONE);
                lnrXn.setVisibility(View.GONE);
                GdDkLich();
            }
        });
        tvDsDk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrDk.setVisibility(View.GONE);
                lnrLich.setVisibility(View.VISIBLE);
                lnrXn.setVisibility(View.VISIBLE);
            }
        });

        btnGuiTb.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // Lấy thời gian hiện tại
                LocalDateTime currentTime = LocalDateTime.now();

                // Định dạng thời gian theo định dạng mong muốn
                DateTimeFormatter formatter = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                }
                String formattedTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formattedTime = currentTime.format(formatter);
                }

                // Đặt giá trị cho biến tg
                tg = formattedTime;
                Notification.showNotification(XepLichLv.this, "Thông báo!", "Lịch của bạn đã có hãy vào xem ngay nhé!");

                QueryData(url4);
            }
        });

        lvLich.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrLich != null && position >= 0 && position < arrLich.size()) {
                    Object item = parent.getItemAtPosition(position);
                    Lich lich = (Lich) item;
                    String maNv = lich.getMaNv();
                    String hoTen = lich.getHoTen();

                    AlertDialog.Builder builder = new AlertDialog.Builder(XepLichLv.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn muốn xóa hay sửa lịch làm việc của " + hoTen + " có mã số " + maNv + "?");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(XepLichLv.this);
                            builder1.setTitle("Cảnh báo");
                            builder1.setMessage("Bạn có chắc muốn xóa lịch làm việc của " + hoTen + " có mã số " + maNv + "?");
                            builder1.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response.equals("success")) {
                                                showAlertDialog(XepLichLv.this, "Thông báo", "Xóa nhân viên thành công! \nBạn đã có thể xem lại danh sách.");
                                            } else if (response.equals("fail")) {
                                                showAlertDialog(XepLichLv.this, "Cảnh báo!", "Xóa nhân viên không thành công.");
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(XepLichLv.this, "Lỗi Xóa: " + error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Nullable
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            // Truyền tham số cho yêu cầu POST
                                            Map<String, String> params = new HashMap<>();
                                            params.put("manv", maNv);
                                            return params;
                                        }
                                    };
                                    // Thêm yêu cầu vào hàng đợi Volley
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    requestQueue.add(stringRequest);
                                    arrLich.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            builder1.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder1.show();
                        }
                    });
                    builder.setNegativeButton("Sửa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(XepLichLv.this, SuaLich.class);
                            intent.putExtra("MaNv", maNv);
                            startActivity(intent);
                        }
                    });
                    builder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    showAlertDialog(XepLichLv.this, "Error", "Không có lịch");
                }
                return false;
            }
        });
        // Gọi hàm showDatePickerDialog() khi TextView được click

        // Gọi hàm để lấy dữ liệu của tuần hiện tại
        getAndDisplayDefaultWeekData();

        tvLichTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAndDisplayDefaultWeekData() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Tính toán ngày bắt đầu của tuần hiện tại
        LocalDate startOfCurrentWeek = currentDate.with(DayOfWeek.MONDAY);

        // Tính toán ngày chủ nhật của tuần hiện tại
        LocalDate endOfCurrentWeek = startOfCurrentWeek.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        // Lấy dữ liệu của tuần hiện tại hoặc các tuần trong tương lai
        startDate = startOfCurrentWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        endDate = endOfCurrentWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Hiển thị thông tin đã chọn trong TextView
        tvLichTime.setText("Từ " + startDate + " đến " + endDate);

        // Gọi hàm để lấy dữ liệu
        LichLv();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void QueryData(String url4) {
        AnhXa();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    showAlertDialog(XepLichLv.this, "Thông báo", "Gửi thông báo thành công!");
                } else if (response.equals("fail")) {
                    showAlertDialog(XepLichLv.this, "Cảnh báo!", "Gửi thông báo thất bại!.\nVui lòng kiểm tra lại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(XepLichLv.this, "Lỗi đăng ký: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                String tb = "Đã có lịch làm việc cho tuần này!";
                params.put("tb", tb);
                params.put("tg", tg);
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        AnhXa();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);

            // Ngăn chọn ngày trong quá khứ
            if (selectedDate.isBefore(LocalDate.now())) {
                Toast.makeText(this, "Vui lòng chọn ngày trong tương lai.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tìm ngày thứ 2 đầu tiên của tuần chứa ngày được chọn
            LocalDate startOfSelectedWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            // Tìm ngày chủ nhật cuối cùng của tuần chứa ngày được chọn
            LocalDate endOfSelectedWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            try {
                selectedStartDate = Date.from(startOfSelectedWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
                selectedEndDate = Date.from(endOfSelectedWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

                // Chuyển đổi thành kiểu String ngay sau khi chọn ngày
                startDate = startOfSelectedWeek.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                endDate = endOfSelectedWeek.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                displaySelectedDates();
                LichLv();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth()
        );

        // Ngăn chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void LichLv() {
        AnhXa();
        lvLich = findViewById(R.id.lvLich);
        arrLich = new ArrayList<>();

        adapter = new LichAdapter(this, R.layout.nv_lich, arrLich);
        lvLich.setAdapter(adapter);

        // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
        arrLich.clear();

        Log.d("LichLv", "LichLv method is called");
        // Lấy dữ liệu cho khoảng thời gian cụ thể
        GetData1(url2);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displaySelectedDates() {
        AnhXa();
        // Gán giá trị cho startDate và endDate
        // Đảm bảo startDate và endDate là kiểu String
        startDate = selectedStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        endDate = selectedEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // Hiển thị thông tin đã chọn trong TextView
        tvLichTime.setText("Từ " + startDate + " đến " + endDate);
    }
    private void GetData1(String url2) {
        Log.d("GetData1", "startDate: " + startDate + ", endDate: " + endDate);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("GetData1", "Response: " + response);
                // Tắt ProgressDialog khi có phản hồi
                progressDialog.dismiss();

                // Xử lý dữ liệu khi có phản hồi
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(XepLichLv.this, "Cảnh báo!", "Danh sách lịch làm việc trống!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            // So sánh startDate và endDate từ JSON với giá trị đã gán mặc định
                            if (startDate.equals(object.optString("StartDate")) && endDate.equals(object.optString("EndDate"))) {
                                // Xử lý thông tin từ JSON
                                String hoTen = object.optString("HoTen", "");
                                String maNv = object.optString("MaNv", "");
                                String t2 = object.optString("T2", "");
                                String t3 = object.optString("T3", "");
                                String t4 = object.optString("T4", "");
                                String t5 = object.optString("T5", "");
                                String t6 = object.optString("T6", "");
                                String t7 = object.optString("T7", "");
                                String cn = object.optString("Cn", "");
                                timeStart = object.optString("StartDate", "");
                                timeEnd = object.optString("EndDate", "");

                                tvLichTime.setText("Từ " + timeStart + " đến " + timeEnd);
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
                                arrLich.add(new Lich(hinhBytes, hoTen, maNv, t2, t3, t4, t5, t6, t7, cn));
                                Log.d("Gỡ lỗi", "Dữ liệu: " + arrLich.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Lỗi JSON", "Lỗi phân tích dữ liệu JSON: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi khi có lỗi trong quá trình gửi yêu cầu
                Toast.makeText(XepLichLv.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e ("Lỗi Volley", "Lỗi trong yêu cầu mạng: " + error.toString ());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void GdDkLich() {
        AnhXa();
        lvDkLich = findViewById(R.id.lvDkLich);
        arrDkLich = new ArrayList<>();

        adapterDk = new LichDkAdapter(this, R.layout.nv_dk, arrDkLich);
        lvDkLich.setAdapter(adapterDk);

        GetData(url1);
    }
    private void GetData(String url1) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(XepLichLv.this, "Cảnh báo!", "Danh sách trống!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String maNv = object.optString("MaNv", "");
                            String hoTen = object.optString("HoTen", "");
                            String lyDo = object.optString("LyDo", "");
                            String t2 = object.optString("T2", "");
                            String t3 = object.optString("T3", "");
                            String t4 = object.optString("T4", "");
                            String t5 = object.optString("T5", "");
                            String t6 = object.optString("T6", "");
                            String t7 = object.optString("T7", "");
                            String cn = object.optString("Cn", "");

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
                            arrDkLich.add(new LichDk(hinhBytes, maNv, hoTen, lyDo, t2, t3, t4, t5, t6, t7, cn));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapterDk.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(XepLichLv.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d("TAG", error.toString());
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }
    private void AnhXa() {
        lnrLich = findViewById(R.id.lnrLich);
        tvDsDk = findViewById(R.id.tvDsDk);
        tvLichLv = findViewById(R.id.tvLichLv);
        lnrDk = findViewById(R.id.lnrDk);
        lnrLich = findViewById(R.id.lnrLich);
        lnrDk.setVisibility(View.GONE);
        lnrXn = findViewById(R.id.lnrXn);
        btnHome = findViewById(R.id.btnHome);
        btnThem = findViewById(R.id.btnThem);
        btnGuiTb = findViewById(R.id.btnGuiTb);
        lvLich = findViewById(R.id.lvLich);
        tvLichTime = findViewById(R.id.tvLichTime);
    }
}