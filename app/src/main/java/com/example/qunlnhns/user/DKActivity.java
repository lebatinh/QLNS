package com.example.qunlnhns.user;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;

import java.util.HashMap;
import java.util.Map;

public class DKActivity extends AppCompatActivity {
    private EditText edtTk_Dk, edtMaNv_Dk, edtMk_Dk, edtMk_Dk1;
    private TextView txtDangNhap;
    private Button btnDangKy;
    public static String localhost = "192.168.3.14";
    private String URL = "http://"+localhost+"/user/insert.php";
    private String tk, manv, mk, mk1;

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
        AnhXa();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    showAlertDialog(DKActivity.this, "Thông báo", "Đăng ký thành công!\nBạn có thể dùng tài khoản đó để đăng nhập");
                } else if (response.equals("fail")) {
                    showAlertDialog(DKActivity.this, "Cảnh báo!", "Đăng ký không thành công.\nTài khoản hoặc mã nhân viên đã tồn tại!");
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
                params.put("email", tk);
                params.put("manv", manv);
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

