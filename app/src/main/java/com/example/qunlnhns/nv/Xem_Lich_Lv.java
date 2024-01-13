package com.example.qunlnhns.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.Database;
import com.example.qunlnhns.R;
import com.example.qunlnhns.ql.dslich.t1.Lich;
import com.example.qunlnhns.ql.dslich.t1.LichAdapter;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.format.DateTimeParseException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;

public class Xem_Lich_Lv extends AppCompatActivity {

    String manv1;
    Database database;
    ListView lvLich;
    ArrayList<Lich> arrLich;
    LichAdapter adapter;
    ImageButton btnHome;
    TextView tvLichTime;
    String startDate, endDate,selectedDate;
    private Date selectedStartDate,selectedEndDate;
    String localhost = DKActivity.localhost;
    String url = "http://" + localhost + "/user/getdata_lich_lv.php";
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
                Toast.makeText(Xem_Lich_Lv.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_lich_lv);
        AnhXa();

        // Khởi tạo đối tượng Database
        database = new Database(this, "main.sqlite", null, 1);
        Pair<String, String> result = database.SELECT_MANV_MAIN();
        manv1 = result.first;

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Gán giá trị cho selectedDate
        selectedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Gọi hàm để lấy dữ liệu của tuần hiện tại
        getAndDisplayDefaultWeekData();

        tvLichTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
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
        GetData(url, selectedDate);
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
    private void GetData(String url, String selectedDate) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();

                if (response.length() == 0) {
                    showAlertDialog(Xem_Lich_Lv.this, "Cảnh báo!", "Bạn chưa có lịch làm việc!");
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
                            if (isDateInRange(timeStart, timeEnd, selectedDate) && manv1.equals(maNv)) {
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
                Toast.makeText(Xem_Lich_Lv.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
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
                startActivity(new Intent(Xem_Lich_Lv.this, MainActivity.class));
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
        lvLich = findViewById(R.id.lvLich);
        tvLichTime = findViewById(R.id.tvLichTime);
    }
}