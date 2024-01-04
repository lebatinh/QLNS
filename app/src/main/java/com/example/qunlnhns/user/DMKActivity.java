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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DMKActivity extends AppCompatActivity {

    private EditText edtTk_Dmk, edtMk_Dmk, edtMknew_Dmk, edtMknew1_Dmk;
    private TextView txtDn, txtDk;
    private Button btnDmk;
    private String tk, mkc, mkm, mkm1;

    String localhost = DKActivity.localhost;
    private String URL1 = "http://"+localhost+"/user/getdata.php";
    private String URL2 = "http://"+localhost+"/user/update.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmk);

        AnhXa();

        btnDmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnhXa();
                if (tk.isEmpty() || mkc.isEmpty() || mkm.isEmpty() || mkm1.isEmpty()) {
                    DNActivity.AlertDialogHelper.showAlertDialog(DMKActivity.this, "Cảnh báo!", "Vui lòng nhập đầy đủ thông tin!");
                } else if (mkc.equals(mkm)) {
                    DNActivity.AlertDialogHelper.showAlertDialog(DMKActivity.this, "Cảnh báo!", "Mật khẩu mới không được trùng với mật khẩu cũ!");
                } else {
                    doimatkhau();
                }
            }
        });
        txtDn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DMKActivity.this, DNActivity.class));
            }
        });

        txtDk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DMKActivity.this, DKActivity.class));
            }
        });
    }

    private void doimatkhau() {
        if (!tk.isEmpty() && !mkc.isEmpty() && !mkm.isEmpty() && !mkm1.isEmpty()) {
            if (mkm.equals(mkm1)) {
                GetData();
            } else {
                showAlertDialog(DMKActivity.this, "Cảnh báo!", "Mật khẩu nhập lại phải trùng với mật khẩu trước đó!");
            }
        } else {
            showAlertDialog(DMKActivity.this, "Cảnh báo!", "Bạn phải nhập đầy đủ thông tin!");
        }
    }

    private void GetData() {
        AnhXa();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                boolean check = false;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String tkdb = object.getString("Email");
                        String mkdb = object.getString("Pass");
                        if (tk.equals(tkdb) && mkc.equals(mkdb)) {
                            check = true;
                            break;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (check) {
                    QueryData();
                } else {
                    edtTk_Dmk.setText("");
                    edtMk_Dmk.setText("");
                    DNActivity.AlertDialogHelper.showAlertDialog(DMKActivity.this, "Cảnh báo!", "Tài khoản hoặc mật khẩu không chính xác!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DMKActivity.this, "Máy chủ bị tắt hoặc lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void QueryData() {
        AnhXa();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    showAlertDialog(DMKActivity.this, "Thông báo", "Đổi mật khẩu thành công!\nBạn có thể đăng nhập lại bằng\ntài khoản và mật khẩu mới");
                } else if (response.equals("fail")) {
                    showAlertDialog(DMKActivity.this, "Cảnh báo!", "Đổi mật khẩu không thành công.\nTài khoản hoặc mật khẩu không chính xác\nhoặc không tồn tại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DMKActivity.this, "Lỗi đổi mật khẩu: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("email", tk);
                params.put("pass", mkm);
                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        edtTk_Dmk = findViewById(R.id.edtTk_Dmk);
        edtMk_Dmk = findViewById(R.id.edtMk_Dmk);
        edtMknew_Dmk = findViewById(R.id.edtMknew_Dmk);
        edtMknew1_Dmk = findViewById(R.id.edtMknew1_Dmk);
        txtDn = findViewById(R.id.txtDn);
        txtDk = findViewById(R.id.txtDk);
        btnDmk = findViewById(R.id.btnDmk);

        tk = edtTk_Dmk.getText().toString().trim();
        mkc = edtMk_Dmk.getText().toString().trim();
        mkm = edtMknew_Dmk.getText().toString().trim();
        mkm1 = edtMknew1_Dmk.getText().toString().trim();
    }
}