package com.example.qunlnhns.user;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.qunlnhns.Success;
import com.example.qunlnhns.nv.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DNActivity extends AppCompatActivity {

    private EditText edtTk_Dn, edtMk_Dn;
    private Button btnDangNhap;
    private TextView txtDangKy, txtDoiMk;
    private String tk, mk, manvvalue;
    String localhost = DKActivity.localhost;
    String URL = "http://"+localhost+"/user/getdata.php";
    Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dn);

        // Khởi tạo đối tượng Database
        database = new Database(this, "main.sqlite", null, 1);

        AnhXa();
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangnhap();
            }
        });

        txtDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DNActivity.this, DKActivity.class));
            }
        });

        txtDoiMk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DNActivity.this, DMKActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thoát ứng dụng");
        builder.setMessage("Bạn có muốn thoát ứng dụng không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng tất cả các Activity trong stack
                finishAffinity();
                // Cần thiết: Sử dụng System.exit(0) để đảm bảo ứng dụng đóng ngay lập tức
                System.exit(0);
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

    private void dangnhap() {
        tk = edtTk_Dn.getText().toString().trim();
        mk = edtMk_Dn.getText().toString().trim();
        if (!tk.isEmpty() && !mk.isEmpty()) {
            GetData();
        } else {
            edtTk_Dn.clearComposingText();
            edtMk_Dn.setText("");
            AlertDialogHelper.showAlertDialog(DNActivity.this, "Cảnh báo!", "Bạn phải nhập đầy đủ tài khoản và mật khẩu!");
        }
    }

    private void GetData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                boolean check = false;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String tkdb = object.getString("Email");
                        String manv = object.getString("Manv");
                        String mkdb = object.getString("Pass");
                        if (tk.equals(tkdb) && mk.equals(mkdb)) {
                            manvvalue = manv;
                            check = true;
                            break;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (check) {
                    edtTk_Dn.setText("");
                    edtMk_Dn.setText("");

                    database.CREATE_TABLE_MAIN();
                    database.INSERT_MANV_MAIN(null, manvvalue);
                    startActivity(new Intent(DNActivity.this, Success.class));
                } else {
                    edtTk_Dn.setText("");
                    edtMk_Dn.setText("");
                    AlertDialogHelper.showAlertDialog(DNActivity.this, "Cảnh báo!", "Tài khoản hoặc mật khẩu không chính xác!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DNActivity.this, "Máy chủ bị tắt hoặc lỗi mạng!", Toast.LENGTH_SHORT).show();
                Log.d("TAG", error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public static class AlertDialogHelper {
        // Hàm để hiển thị dialog cảnh báo
        public static void showAlertDialog(Context context, String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Thiết lập tiêu đề và nội dung của dialog
            builder.setTitle(title);
            builder.setMessage(message);

            // Thiết lập nút OK và xử lý sự kiện khi nút được nhấn
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Xử lý khi nút OK được nhấn
                    dialog.dismiss(); // Đóng dialog
                }
            });

            // Tạo và hiển thị dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    private void AnhXa() {
        edtTk_Dn = findViewById(R.id.edtTk_Dn);
        edtMk_Dn = findViewById(R.id.edtMk_Dn);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        txtDangKy = findViewById(R.id.txtDangKy);
        txtDoiMk = findViewById(R.id.txtDoiMk);
    }
}