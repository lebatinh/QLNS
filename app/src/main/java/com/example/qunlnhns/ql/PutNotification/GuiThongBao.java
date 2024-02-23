package com.example.qunlnhns.ql.PutNotification;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;
import com.example.qunlnhns.MainActivity;
import com.example.qunlnhns.ql.dslich.list_nv.List_Nv;
import com.example.qunlnhns.ql.dslich.list_nv.List_Nv_Adapter;
import com.example.qunlnhns.user.DKActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuiThongBao extends AppCompatActivity {

    private ImageButton btnHome;
    private EditText edtTb, edtTitle;
    private Button btnGuiTb;
    private RadioGroup radiogr;
    private RadioButton rdAll, rdChoice;
    String tt, tb, tg, manv, maNv, hoTen;
    private ListView lvCheck;
    private ArrayList<List_Nv> arrListNv;
    private List_Nv_Adapter adapter;
    private ArrayList<String> selectedMaNvList;
    String localhost = DKActivity.localhost;
    String url = "http://" + localhost + "/user/gui_tb.php";
    String url1 = "http://" + localhost + "/user/get_nv.php";
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
                Toast.makeText(GuiThongBao.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui_thong_bao);
        AnhXa();

        GetData(url1);
        arrListNv = new ArrayList<>();

        adapter = new List_Nv_Adapter(this, R.layout.nv, arrListNv);
        lvCheck.setAdapter(adapter);

        selectedMaNvList = new ArrayList<>();

        radiogr.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdAll) {
                lvCheck.setVisibility(View.GONE);
            } else if (checkedId == R.id.rdChoice) {
                lvCheck.setVisibility(View.VISIBLE);
            }
        });
        btnHome.setOnClickListener(v -> onBackPressed());

        btnGuiTb.setOnClickListener(v -> {
            getLocalTime();
            // Update the value of tb here
            tt = edtTitle.getText().toString().trim();
            tb = edtTb.getText().toString().trim();
            if (!tt.isEmpty() && !tb.isEmpty()) {
                if (rdAll.isChecked()) {
                    //gửi thông báo
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", tt, tb, getApplicationContext(), GuiThongBao.this);
                    notificationsSender.SendNotifications();
                    GuiTbAll(url);
                } else if (rdChoice.isChecked()) {
                    lvCheck();
                } else {
                    showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Bạn chưa chọn gửi thông báo với ai!");
                }
            } else {
                showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Bạn chưa viết thông báo mà!");
            }
        });

        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);
    }

    private void GetData(String url1) {
        final ProgressDialog progressDialog = new ProgressDialog(GuiThongBao.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
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

                            arrListNv.add(new List_Nv(hinhBytes, maNv, hoTen, 1));
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
                Toast.makeText(GuiThongBao.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void lvCheck() {
        // Lọc ra danh sách các maNv đã được chọn
        selectedMaNvList.clear();
        for (List_Nv listNv : arrListNv) {
            if (listNv.isChecked()) {
                selectedMaNvList.add(listNv.getMaNV());
            }
        }

        if (!selectedMaNvList.isEmpty()) {
            for (String maNv : selectedMaNvList) {
                // Gọi hàm GuiTbChoice và truyền danh sách maNV đã được chọn
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/" + maNv, tt, tb, getApplicationContext(), GuiThongBao.this);
                notificationsSender.SendNotifications();
            }
            GuiTbChoice(url, selectedMaNvList);
        } else {
            showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Bạn chưa chọn nhân viên nhận thông báo!");
        }
    }

    private void GuiTbAll(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(GuiThongBao.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            progressDialog.dismiss();
            if (response.equals("success")) {
                showAlertDialog(GuiThongBao.this, "Thông báo", "Gửi thông báo thành công!");
            } else if (response.equals("fail")) {
                showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Gửi thông báo thất bại!.\nVui lòng kiểm tra lại!");
            } else if (response.equals("rong")) {
                showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Lỗi danh sách chọn!");
            } else if (response.equals("error")) {
                showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Lỗi danh sách không hợp lệ!");
            } else {
                showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Lỗi bất định!");
                Log.d("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GuiThongBao.this, "Lỗi gửi thông báo!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                tt = edtTitle.getText().toString().trim();
                tb = edtTb.getText().toString().trim();
                params.put("title", tt);
                params.put("tb", tb);
                params.put("tg", tg);
                params.put("phamvi", "all");
                return params;
            }
        };
        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void GuiTbChoice(String url, ArrayList<String> selectedMaNvList) {
        final ProgressDialog progressDialog = new ProgressDialog(GuiThongBao.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("success")) {
                    tb = edtTb.getText().toString().trim();
                    showAlertDialog(GuiThongBao.this, "Thông báo", "Gửi thông báo thành công!");
                } else if (response.equals("fail")) {
                    showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Gửi thông báo thất bại!.\nVui lòng kiểm tra lại!");
                } else if (response.equals("rong")) {
                    showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Lỗi danh sách chọn!");
                } else if (response.equals("error")) {
                    showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Lỗi danh sách không hợp lệ!");
                } else {
                    showAlertDialog(GuiThongBao.this, "Cảnh báo!", "Lỗi bất định!");
                    Log.d("Response", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GuiThongBao.this, "Lỗi gửi thông báo!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                // Cập nhật giá trị của tb trong getParams

                tt = edtTitle.getText().toString().trim();
                tb = edtTb.getText().toString().trim();
                params.put("title", tt);
                params.put("tb", tb);
                params.put("tg", tg);

                // Chuyển danh sách maNV đã chọn thành chuỗi và truyền lên server
                String selectedMaNvJson = new Gson().toJson(selectedMaNvList);
                params.put("manv", selectedMaNvJson);

                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getLocalTime() {
        // Lấy thời gian hiện tại
        LocalDateTime currentTime = LocalDateTime.now();

        // Định dạng thời gian theo định dạng mong muốn
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyy");

        String formattedTime = null;
        formattedTime = currentTime.format(formatter);

        // Đặt giá trị cho biến tg
        tg = formattedTime;
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
                startActivity(new Intent(GuiThongBao.this, MainActivity.class));
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
        edtTb = findViewById(R.id.edtTb);
        edtTitle = findViewById(R.id.edtTitle);
        btnGuiTb = findViewById(R.id.btnGuiTb);
        rdAll = findViewById(R.id.rdAll);
        radiogr = findViewById(R.id.radiogr);
        rdChoice = findViewById(R.id.rdChoice);
        lvCheck = findViewById(R.id.lvCheck);
    }
}
