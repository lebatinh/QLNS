package com.example.qunlnhns.ql;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
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
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.MainActivity;
import com.example.qunlnhns.ql.dslich.t3.List_Nv;
import com.example.qunlnhns.ql.dslich.t3.List_Nv_Adapter;
import com.example.qunlnhns.ql.dslich.t4.ThuongPhat;
import com.example.qunlnhns.ql.dslich.t4.ThuongPhatAdapter;
import com.example.qunlnhns.user.DKActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LuongThuongPhat extends AppCompatActivity {

    private ImageButton btnHome, btnThem;
    private LinearLayout lnrNv, lnrXn, lnrXet, lnrThuong, lnrPhat, lnrRieng;
    ListView lvNv, lvThuong, lvPhat;
    private TextView tvMaNvHoTen, tvNgay, tvTotalThuong, tvTotalPhat, tvTong, tvThuong, tvPhat;
    private EditText edtLuong, edtChiPhi;
    private Button btnGui, btnTong;
    private String maNv, hoTen, luongCoBan, lyDoThuong, tienThuong, lyDoPhat, tienPhat, chiPhiKhac, tong, thoiGian;
    ArrayList<ThuongPhat> arrThuong, arrPhat;
    ArrayList<List_Nv> arrNv;
    ThuongPhatAdapter adapterThuong, adapterPhat;
    List_Nv_Adapter adapterNv;
    private boolean isLvThuongVisible = false;
    private boolean isLvPhatVisible = false;
    String localhost = DKActivity.localhost;
    String url1 = "http://" + localhost + "/user/get_nv_luong.php";
    String url2 = "http://" + localhost + "/user/insert_luong.php";
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
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

        lvThuong = findViewById(R.id.lvThuong);
        arrThuong = new ArrayList<>();
        adapterThuong = new ThuongPhatAdapter(this, R.layout.nv_thuongphat, arrThuong);
        lvThuong.setAdapter(adapterThuong);

        lvPhat = findViewById(R.id.lvPhat);
        arrPhat = new ArrayList<>();
        adapterPhat = new ThuongPhatAdapter(this, R.layout.nv_thuongphat, arrPhat);
        lvPhat.setAdapter(adapterPhat);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        GetNv(url1);

        lvNv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrNv != null && position >= 0 && position < arrNv.size()) {
                    Object item = parent.getItemAtPosition(position);
                    List_Nv list_nv = (List_Nv) item;
                    maNv = list_nv.getMaNV();
                    hoTen = list_nv.getHoTen();
                    tvMaNvHoTen.setText(maNv + " - " + hoTen);

                    AlertDialog.Builder builder = new AlertDialog.Builder(LuongThuongPhat.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn có chắc chắn muốn xét lương cho " + hoTen + " có mã nhân viên là " + maNv + "?");
                    builder.setPositiveButton("Đúng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lnrNv.setVisibility(View.GONE);
                            lnrXet.setVisibility(View.VISIBLE);
                            lnrXn.setVisibility(View.VISIBLE);
                            btnThem.setVisibility(View.VISIBLE);
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
                // Tạo một AlertDialog để hỏi người dùng muốn thưởng hay phạt
                AlertDialog.Builder builder = new AlertDialog.Builder(LuongThuongPhat.this);
                builder.setTitle("Bạn muốn thưởng hay phạt?");
                builder.setPositiveButton("Thưởng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Hiển thị một AlertDialog khác để yêu cầu người dùng nhập thông tin
                        AlertDialog.Builder builder = new AlertDialog.Builder(LuongThuongPhat.this);
                        builder.setTitle("Thêm thưởng");
                        LinearLayout layout = new LinearLayout(LuongThuongPhat.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText editText1 = new EditText(LuongThuongPhat.this);
                        editText1.setHint("Nhập lí do thưởng");
                        layout.addView(editText1);
                        final EditText editText2 = new EditText(LuongThuongPhat.this);
                        editText2.setHint("Nhập số tiền thưởng");
                        setupEditText(editText2);
                        layout.addView(editText2);
                        builder.setView(layout);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Thêm dữ liệu mới vào ArrayList tương ứng với loại thưởng
                                String lydo = editText1.getText().toString();
                                String sotien = editText2.getText().toString().trim();
                                if (!lydo.isEmpty() && !sotien.isEmpty()) {
                                    arrThuong.add(new ThuongPhat(lydo, sotien));
                                    // Cập nhật ListView
                                    adapterThuong.notifyDataSetChanged();

                                    TotalThuong();
                                } else {
                                    Toast.makeText(LuongThuongPhat.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
                builder.setNegativeButton("Phạt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Hiển thị một AlertDialog khác để yêu cầu người dùng nhập thông tin
                        AlertDialog.Builder builder = new AlertDialog.Builder(LuongThuongPhat.this);
                        builder.setTitle("Thêm phạt");
                        LinearLayout layout = new LinearLayout(LuongThuongPhat.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText editText1 = new EditText(LuongThuongPhat.this);
                        editText1.setHint("Nhập lí do");
                        layout.addView(editText1);
                        final EditText editText2 = new EditText(LuongThuongPhat.this);
                        editText2.setHint("Nhập số tiền");
                        setupEditText(editText2);
                        layout.addView(editText2);
                        builder.setView(layout);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Thêm dữ liệu mới vào ArrayList tương ứng với loại phạt
                                String lydo1 = editText1.getText().toString();
                                String sotien1 = editText2.getText().toString();
                                if (!lydo1.isEmpty() && !sotien1.isEmpty()) {
                                    arrPhat.add(new ThuongPhat(lydo1, sotien1));
                                    // Cập nhật ListView
                                    adapterPhat.notifyDataSetChanged();

                                    TotalPhat();
                                } else {
                                    Toast.makeText(LuongThuongPhat.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
                builder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvNgay.getText().toString().trim().isEmpty()) {
                    if (!edtLuong.getText().toString().trim().isEmpty()) {
                        if (!tvTong.getText().toString().trim().isEmpty()) {
                            QueryLuong(url2);
                            btnGui.setClickable(false);
                        } else {
                            showAlertDialog(LuongThuongPhat.this, "Cảnh báo!", "Bạn phải tính tổng lương trước!");
                        }
                    } else {
                        showAlertDialog(LuongThuongPhat.this, "Cảnh báo!", "Bạn quên chưa ghi lương cơ bản!");
                    }
                } else {
                    showAlertDialog(LuongThuongPhat.this, "Cảnh báo!", "Bạn quên chưa chọn ngày cho lịch của lương!");
                }
            }
        });
        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);
        tvNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
            }
        });
        tvThuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleThuong();
            }
        });
        tvPhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePhat();
            }
        });
        lnrThuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrThuong.setVisibility(View.GONE);
                lnrRieng.setVisibility(View.VISIBLE);
            }
        });
        lnrPhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrPhat.setVisibility(View.GONE);
                lnrRieng.setVisibility(View.VISIBLE);
            }
        });
        btnTong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TotalLuong();
                btnGui.setClickable(true);
            }
        });
        layLyDoThuongTuList();
        layLyDoPhatTuList();
    }

    private void TotalLuong() {
        try {
            // Lấy lương cơ bản từ edtLuong
            double luongCoBan = Double.parseDouble(edtLuong.getText().toString());

            // Lấy tổng thưởng từ tvTotalThuong
            double tongThuong = Double.parseDouble(tvTotalThuong.getText().toString());

            // Lấy tổng phạt từ tvTotalPhat
            double tongPhat = Double.parseDouble(tvTotalPhat.getText().toString());

            // Lấy chi phí khác từ edtChiPhi
            double chiPhiKhac = Double.parseDouble(edtChiPhi.getText().toString());

            // Tính tổng lương
            double tongLuong = luongCoBan + tongThuong - tongPhat - chiPhiKhac;

            // Hiển thị kết quả trong tvTong
            tvTong.setText(String.valueOf(tongLuong));
        } catch (NumberFormatException e) {
            // Xử lý trường hợp giá trị nhập không phải là số hợp lệ
            Toast.makeText(this, "Vui lòng nhập số hợp lệ cho các thành phần lương.", Toast.LENGTH_SHORT).show();
        }
    }
    private String layLyDoThuongTuList() {
        StringBuilder lyDoThuongStringBuilder = new StringBuilder();

        for (ThuongPhat thuongPhat : arrThuong) {
            lyDoThuongStringBuilder.append(thuongPhat.getLiDo()).append("\n");
        }

        return lyDoThuongStringBuilder.toString().trim();
    }

    private void toggleThuong() {
        if (isLvThuongVisible) {
            // Ẩn lnrThuong nếu đang hiển thị
            lnrThuong.setVisibility(View.GONE);
            lnrPhat.setVisibility(View.GONE);
            lnrRieng.setVisibility(View.VISIBLE);
        } else {
            // Hiển thị lnrThuong nếu đang ẩn
            lnrThuong.setVisibility(View.VISIBLE);
            lnrPhat.setVisibility(View.GONE);
            lnrRieng.setVisibility(View.GONE);
        }

        // Đảo ngược giá trị của isLvThuongVisible
        isLvThuongVisible = !isLvThuongVisible;
    }
    private String layLyDoPhatTuList() {
        StringBuilder lyDoPhatStringBuilder = new StringBuilder();

        for (ThuongPhat thuongPhat : arrPhat) {
            lyDoPhatStringBuilder.append(thuongPhat.getLiDo()).append("\n");
        }

        return lyDoPhatStringBuilder.toString().trim();
    }

    private void togglePhat() {
        if (isLvPhatVisible) {
            // Ẩn lnrPhat nếu đang hiển thị
            lnrThuong.setVisibility(View.GONE);
            lnrPhat.setVisibility(View.GONE);
            lnrRieng.setVisibility(View.VISIBLE);
        } else {
            // Hiển thị lnrPhat nếu đang ẩn
            lnrThuong.setVisibility(View.GONE);
            lnrPhat.setVisibility(View.VISIBLE);
            lnrRieng.setVisibility(View.GONE);
        }

        // Đảo ngược giá trị của isLvThuongVisible
        isLvPhatVisible = !isLvPhatVisible;
    }

    // Cập nhật phương thức tính lại tổng số tiền thưởng
    private void TotalThuong() {
        double totalThuong = 0;
        for (ThuongPhat thuongPhat : arrThuong) {
            totalThuong += Double.parseDouble(thuongPhat.getSoTien());
        }
        // Cập nhật TextView hiển thị tổng số tiền thưởng
        tvTotalThuong.setText(String.valueOf(totalThuong));
    }

    private void TotalPhat() {
        double totalPhat = 0;
        for (ThuongPhat thuongPhat : arrPhat) {
            totalPhat += Double.parseDouble(thuongPhat.getSoTien());
        }
        // Cập nhật TextView hiển thị tổng số tiền thưởng
        tvTotalPhat.setText(String.valueOf(totalPhat));
    }

    private void showDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Chọn ngày")
                .setSelection(Pair.create(startDateCalendar.getTimeInMillis(), endDateCalendar.getTimeInMillis()))
                .build();

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                startDateCalendar.setTimeInMillis(selection.first);
                endDateCalendar.setTimeInMillis(selection.second);

                updateNgayTextView();
            }
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void updateNgayTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String startDate = sdf.format(startDateCalendar.getTime());
        String endDate = sdf.format(endDateCalendar.getTime());

        if (TextUtils.equals(startDate, endDate)) {
            tvNgay.setText(startDate);
        } else {
            tvNgay.setText(String.format("%s - %s", startDate, endDate));
        }
    }

    private void setupEditText(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // Kiểm tra từng ký tự trong chuỗi nguồn
                for (int i = start; i < end; i++) {
                    char inputChar = source.charAt(i);
                    // Nếu ký tự là số từ 0 đến 9 và không phải là số 0 ở đầu
                    if (Character.isDigit(inputChar)) {
                        return String.valueOf(inputChar); // Chấp nhận ký tự này
                    } else {
                        Toast.makeText(LuongThuongPhat.this, "Bạn chỉ có thể dùng các kí tự từ 0 đến 9!", Toast.LENGTH_SHORT).show();
                    }
                }
                return ""; // Không chấp nhận ký tự này
            }
        }});
    }

    private void QueryLuong(String url2) {
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
                lyDoThuong = layLyDoThuongTuList();
                params.put("lyDoThuong", lyDoThuong);
                tienThuong = String.valueOf(tvTotalThuong.getText().toString().trim());
                params.put("tienThuong", tienThuong);
                lyDoPhat = layLyDoPhatTuList();
                params.put("lyDoPhat", lyDoPhat);
                tienPhat = String.valueOf(tvTotalPhat.getText().toString().trim());
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

    private void GetNv(String url1) {
        final ProgressDialog progressDialog = new ProgressDialog(LuongThuongPhat.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
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
        lnrXet = findViewById(R.id.lnrXet);
        lnrThuong = findViewById(R.id.lnrThuong);
        lnrPhat = findViewById(R.id.lnrPhat);
        lnrRieng = findViewById(R.id.lnrRieng);
        tvMaNvHoTen = findViewById(R.id.tvMaNvHoTen);
        tvNgay = findViewById(R.id.tvNgay);
        tvTotalThuong = findViewById(R.id.tvTotalThuong);
        tvTotalPhat = findViewById(R.id.tvTotalPhat);
        tvTong = findViewById(R.id.tvTong);
        tvThuong = findViewById(R.id.tvThuong);
        tvPhat = findViewById(R.id.tvPhat);
        edtLuong = findViewById(R.id.edtLuong);
        edtChiPhi = findViewById(R.id.edtChiPhi);
        btnGui = findViewById(R.id.btnGui);
        btnTong = findViewById(R.id.btnTong);
    }
}