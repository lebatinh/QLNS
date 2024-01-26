package com.example.qunlnhns.ql;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.example.qunlnhns.DatabaseSQlite;
import com.example.qunlnhns.MainActivity;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.luong.Luong;
import com.example.qunlnhns.nv.luong.LuongAdapter;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Bang_Luong extends AppCompatActivity {
    private ImageButton btnHome;
    private TextView tvTime;
    ListView lvBangLuong;
    ArrayList<Luong> arrLuong;
    LuongAdapter adapter;
    DatabaseSQlite databaseSQlite;
    private Calendar calendar;
    String localhost = DKActivity.localhost;
    String url = "http://" + localhost + "/user/get_luong_nv.php";
    String url1 = "http://" + localhost + "/user/delete_luong.php";
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
                Toast.makeText(Bang_Luong.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_luong);

        AnhXa();

        // Khởi tạo đối tượng Database
        databaseSQlite = new DatabaseSQlite(this, "detail.sqlite", null, 1);

        arrLuong = new ArrayList<Luong>();
        adapter = new LuongAdapter(this, R.layout.nv_luong, arrLuong);
        lvBangLuong.setAdapter(adapter);


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lvBangLuong.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrLuong != null && position >= 0 && position < arrLuong.size()) {
                    Object item = parent.getItemAtPosition(position);
                    Luong luong = (Luong) item;
                    String maNv = luong.getMaNv();
                    String hoTen = luong.getHoTen();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Bang_Luong.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn muốn xóa bảng lương của " + hoTen + " có mã số " + maNv + "?");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Bang_Luong.this);
                            builder1.setTitle("Cảnh báo");
                            builder1.setMessage("Bạn có chắc muốn xóa bảng lương của " + hoTen + " có mã số " + maNv + "?");
                            builder1.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final ProgressDialog progressDialog = new ProgressDialog(Bang_Luong.this);
                                    progressDialog.setMessage("Loading...");
                                    progressDialog.show();
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            if (response.equals("success")) {
                                                arrLuong.remove(position);
                                                adapter.notifyDataSetChanged();
                                                showAlertDialog(Bang_Luong.this, "Thông báo", "Xóa lương của " + hoTen + " thành công! \nBạn đã có thể xem lại bảng lương.");
                                            } else if (response.equals("fail")) {
                                                showAlertDialog(Bang_Luong.this, "Cảnh báo!", "Xóa lương của " + hoTen + " không thành công!");
                                            } else {
                                                showAlertDialog(Bang_Luong.this, "Cảnh báo!", "Xóa lương của " + hoTen + " không thành công!");
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(Bang_Luong.this, "Lỗi Xóa: " + error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Nullable
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            // Truyền tham số cho yêu cầu POST
                                            Map<String, String> params = new HashMap<>();
                                            params.put("maNv", maNv);
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
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    showAlertDialog(Bang_Luong.this, "Error", "Không có bảng lương");
                }
                return true;
            }
        });

        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);

        // Khởi tạo ngày hiện tại
        calendar = Calendar.getInstance();
        // Hiển thị tháng hiện tại trong TextView và lấy dữ liệu từ database
        updateTextView();
        GetLuong();
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                        // Lưu giá trị ngày đã chọn vào Calendar
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, monthOfYear);

                        // Hiển thị tháng đã chọn trong TextView
                        updateTextView();

                        // Lấy dữ liệu từ database khi người dùng chọn tháng mới
                        GetLuong();
                    }
                },
                year,
                month,
                0 // Giá trị của ngày không quan trọng trong trường hợp này
        );
        datePickerDialog.getDatePicker().updateDate(year, month, 1);
        // Đặt tiêu đề cho DatePickerDialog
        datePickerDialog.setTitle("Hãy chọn ngày bất kì ở tháng cần dùng để hiện bảng lương");

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    private void updateTextView() {
        // Format ngày để hiển thị trong TextView
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());

        // Đặt giá trị cho TextView
        tvTime.setText(formattedDate);
    }

    private void GetLuong() {
        final ProgressDialog progressDialog = new ProgressDialog(Bang_Luong.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                arrLuong.clear();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(Bang_Luong.this, "Cảnh báo!", "Không có bảng lương!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String maNv = object.optString("MaNv", "");
                            String hoTen = object.optString("HoTen", "");
                            String thuong = object.optString("TienThuong", "");
                            String phat = object.optString("TienPhat", "");
                            String luong = object.optString("Tong", "");
                            String thoiGian = object.optString("ThoiGian", "");

                            String tg = tvTime.getText().toString().trim();
                            if (tg.equals(thoiGian)) {
                                arrLuong.add(new Luong(maNv, hoTen, thuong, phat, luong));
                            }
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
                Toast.makeText(Bang_Luong.this, "Error", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(Bang_Luong.this, MainActivity.class));
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
        lvBangLuong = findViewById(R.id.lvBangLuong);
        btnHome = findViewById(R.id.btnHome);
        tvTime = findViewById(R.id.tvTime);
    }
}