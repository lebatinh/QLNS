package com.example.qunlnhns.user;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.qunlnhns.Success;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DKActivity extends AppCompatActivity {
    private EditText edtTk_Dk, edtMaNv_Dk, edtMk_Dk, edtMk_Dk1;
    private TextView txtDangNhap;
    private Button btnDangKy;
    public static String localhost = "192.168.3.48";
    private String URL = "http://" + localhost + "/user/insert.php";
    private String url1 = "http://" + localhost + "/user/check_user.php";
    private String tk, manv, mk, mk1;
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
                Toast.makeText(DKActivity.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dk);

        AnhXa();
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangky();
            }
        });

        txtDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DKActivity.this, DNActivity.class));
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

    // Nếu người dùng ấn nút quay lại
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn có muốn quay lại màn hình đăng nhập không?");
        builder.setMessage("Khi quay lại sẽ hủy những gì bạn nhập vào!");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                edtTk_Dk.setText("");
                edtMk_Dk.setText("");
                edtMk_Dk1.setText("");
                edtMaNv_Dk.setText("");
                startActivity(new Intent(DKActivity.this, DNActivity.class));
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
    private void dangky() {
        AnhXa();
        if (!tk.isEmpty() && !manv.isEmpty() && !mk.isEmpty() && !mk1.isEmpty()) {
            if (mk.equals(mk1)) {
                QueryData();
            } else {
                showAlertDialog(DKActivity.this, "Cảnh báo!", "Mật khẩu nhập lại phải trùng với mật khẩu trước đó!");
            }
        } else {
            showAlertDialog(DKActivity.this, "Cảnh báo!", "Bạn phải nhập đầy đủ thông tin!");
        }
    }

    private void QueryData() {
        final ProgressDialog progressDialog = new ProgressDialog(DKActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        AnhXa();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("success")) {
                    edtTk_Dk.setText("");
                    edtMk_Dk.setText("");
                    edtMk_Dk1.setText("");
                    edtMaNv_Dk.setText("");
                    showAlertDialog(DKActivity.this, "Thông báo", "Đăng ký thành công!\nBạn có thể dùng tài khoản đó để đăng nhập");
                } else if (response.equals("fail")) {
                    showAlertDialog(DKActivity.this, "Cảnh báo!", "Đăng ký không thành công.\nTài khoản hoặc mã nhân viên đã tồn tại!");
                } else if (response.equals("not_exists")) {
                    showAlertDialog(DKActivity.this, "Cảnh báo!", "Đăng ký không thành công.\nBạn không phải nhân viên trong công ty!");
                } else {
                    showAlertDialog(DKActivity.this, "Cảnh báo!", "Đăng ký không thành công!.\nTài khoản hoặc mã nhân viên đã tồn tại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DKActivity.this, "Lỗi đăng ký: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("manv", manv);
                params.put("email", tk);
                params.put("pass", mk);
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        edtTk_Dk = findViewById(R.id.edtTk_Dk);
        edtMaNv_Dk = findViewById(R.id.edtMaNv_Dk);
        edtMk_Dk = findViewById(R.id.edtMk_Dk);
        edtMk_Dk1 = findViewById(R.id.edtMk_Dk1);
        txtDangNhap = findViewById(R.id.txtDangNhap);
        btnDangKy = findViewById(R.id.btnDangKy);

        tk = edtTk_Dk.getText().toString().trim();
        manv = edtMaNv_Dk.getText().toString().trim();
        mk = edtMk_Dk.getText().toString().trim();
        mk1 = edtMk_Dk1.getText().toString().trim();
    }
}

