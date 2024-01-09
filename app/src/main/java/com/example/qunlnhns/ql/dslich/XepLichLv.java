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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.qunlnhns.Database;
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
import org.threeten.bp.format.DateTimeParseException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
    String tg, startDate, endDate, selectedDate, notificationContent;
    private Date selectedStartDate,selectedEndDate;
    Database database;
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
                Toast.makeText(XepLichLv.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xep_lich_lv);

        AnhXa();

        // Khởi tạo đối tượng Database
        database = new Database(this, "detail.sqlite", null, 1);
        GdDkLich();
        tvLichTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Gán giá trị cho selectedDate
        selectedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
        tvDsDk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrDk.setVisibility(View.GONE);
                lnrLich.setVisibility(View.VISIBLE);
                lnrXn.setVisibility(View.VISIBLE);
            }
        });
        tvLichLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrDk.setVisibility(View.VISIBLE);
                lnrLich.setVisibility(View.GONE);
                lnrXn.setVisibility(View.GONE);
            }
        });
        btnGuiTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationDialog();
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
                                    final ProgressDialog progressDialog = new ProgressDialog(XepLichLv.this);
                                    progressDialog.setMessage("Loading...");
                                    progressDialog.show();
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            if (response.equals("success")) {
                                                arrLich.remove(position);
                                                adapter.notifyDataSetChanged();
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
                                            Log.d("manv", maNv.toString());
                                            return params;
                                        }
                                    };
                                    // Thêm yêu cầu vào hàng đợi Volley
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    requestQueue.add(stringRequest);
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
                            final ProgressDialog progressDialog = new ProgressDialog(XepLichLv.this);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                            Intent intent = new Intent(XepLichLv.this, SuaLich.class);
                            database.INSERT_MANV_DELTAIL(null, maNv);

                            progressDialog.dismiss();
                            startActivity(new Intent(XepLichLv.this, SuaLich.class));
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

        // Gọi hàm để lấy dữ liệu của tuần hiện tại
        getAndDisplayDefaultWeekData();

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

    private void showNotificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(XepLichLv.this);
        builder.setTitle("Thông báo");

        // Layout cho dialog
        LinearLayout layout = new LinearLayout(XepLichLv.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Ô nhập liệu
        final EditText input = new EditText(XepLichLv.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nhập nội dung thông báo");
        layout.addView(input);

        builder.setView(layout);

        // Nút "Gửi"
        builder.setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy nội dung từ ô nhập liệu
                notificationContent = input.getText().toString();

                if (!notificationContent.isEmpty()) {
                    // Lấy thời gian hiện tại
                    LocalDateTime currentTime = LocalDateTime.now();

                    // Định dạng thời gian theo định dạng mong muốn
                    DateTimeFormatter formatter = null;
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    String formattedTime = null;
                    formattedTime = currentTime.format(formatter);


                    // Đặt giá trị cho biến tg
                    tg = formattedTime;

                    // Gọi hàm QueryData để gửi thông báo và cập nhật SQL
                    QueryData(url4);
                } else {
                    // Hiển thị thông báo lỗi nếu nội dung trống
                    Toast.makeText(XepLichLv.this, "Vui lòng nhập nội dung thông báo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút "Hủy"
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog nếu người dùng ấn "Hủy"
                dialog.cancel();
            }
        });

        // Hiển thị dialog
        builder.show();
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
        LichLv(selectedDate);
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
                startActivity(new Intent(XepLichLv.this, MainActivity.class));
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
    private void QueryData(String url4) {
        final ProgressDialog progressDialog = new ProgressDialog(XepLichLv.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("success")) {
                    Notification.showNotification(XepLichLv.this, "Quản lý nhân sự", "Bạn có thông báo mới, Vào xem ngay nhé!");
                    showAlertDialog(XepLichLv.this, "Thông báo", "Gửi thông báo thành công!");
                } else if (response.equals("fail")) {
                    showAlertDialog(XepLichLv.this, "Cảnh báo!", "Gửi thông báo thất bại!.\nVui lòng kiểm tra lại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(XepLichLv.this, "Lỗi gửi thông báo!", Toast.LENGTH_SHORT).show();
                Log.d("Lỗi gửi thông báo: ", error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("tb", notificationContent);
                params.put("tg", tg);
                params.put("phamvi", "all");
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
                this.selectedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                selectedStartDate = Date.from(startOfSelectedWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
                selectedEndDate = Date.from(endOfSelectedWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

                // Chuyển đổi thành kiểu String ngay sau khi chọn ngày
                startDate = startOfSelectedWeek.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                endDate = endOfSelectedWeek.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                displaySelectedDates();

                // Gọi hàm LichLv với ngày được chọn
                LichLv(this.selectedDate);
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
    private void LichLv(String selectedDate) {
        AnhXa();
        lvLich = findViewById(R.id.lvLich);
        arrLich = new ArrayList<>();

        adapter = new LichAdapter(this, R.layout.nv_lich, arrLich);
        lvLich.setAdapter(adapter);

        // Gọi hàm để lấy dữ liệu từ server
        GetData1(url2, selectedDate);
    }

    private void GetData1(String url2, String selectedDate) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();

                if (response.length() == 0) {
                    showAlertDialog(XepLichLv.this, "Cảnh báo!", "Danh sách lịch làm việc trống!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
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
                            String timeStart = object.optString("StartDate", "");
                            String timeEnd = object.optString("EndDate", "");

                            // Kiểm tra xem ngày bắt đầu và kết thúc có khớp với ngày được chọn không
                            if (isDateInRange(timeStart, timeEnd, selectedDate)) {
                                // Lấy chuỗi Base64 từ JSON
                                String hinhBase64 = object.optString("HinhAnh", "");
                                // Chuyển chuỗi Base64 thành mảng byte
                                byte[] hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                                // Kiểm tra xem mảng byte có dữ liệu không
                                if (hinhBytes != null && hinhBytes.length > 0) {
                                    // Chuyển mảng byte thành Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                }

                                // Thêm vào danh sách arrLich
                                arrLich.add(new Lich(hinhBytes, maNv, hoTen, t2, t3, t4, t5, t6, t7, cn));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Lỗi JSON", "Lỗi phân tích dữ liệu JSON: " + e.getMessage());
                        }
                    }

                    // Cập nhật adapter sau khi có dữ liệu mới
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(XepLichLv.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("Lỗi Volley", "Lỗi trong yêu cầu mạng: " + error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInRange(String startDate, String endDate, String selectedDate) {
        try {
            if (selectedDate == null || selectedDate.isEmpty()) {
                // Chuỗi selectedDate là null hoặc trống, không thể phân tích được
                return false;
            }

            LocalDate startLocalDate = LocalDate.parse(startDate);
            LocalDate endLocalDate = LocalDate.parse(endDate);
            LocalDate selectedLocalDate = LocalDate.parse(selectedDate);

            // Kiểm tra xem ngày được chọn có nằm trong khoảng từ ngày bắt đầu đến ngày kết thúc không
            return !selectedLocalDate.isBefore(startLocalDate) && !selectedLocalDate.isAfter(endLocalDate);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return false;
        }
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
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