package com.example.qunlnhns.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;
import com.example.qunlnhns.api.Youtube;
import com.example.qunlnhns.nv.dsnv.DSNVActivity;
import com.example.qunlnhns.ql.dslich.tb.Tb;
import com.example.qunlnhns.ql.dslich.tb.TbAdapter;
import com.example.qunlnhns.ql.nv.ChangeListNV;
import com.example.qunlnhns.ql.nv.ThemNV;
import com.example.qunlnhns.ql.dslich.XepLichLv;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvTb;
    ArrayList<Tb> arrTb;
    TbAdapter adapterTb;
    private ImageButton dsnv, dkl, xemllv, xltp, gtn, tnv, sttnv, xnv, xepllv, vtb, home, thongbao, person, ytb;
    private ImageView profile;
    private TextView tvhoten, tvChucVu;
    private String manv1;
    private ScrollView scrollView;
    String localhost = DKActivity.localhost;
    private String url = "http://" + localhost + "/user/get_manv.php";
    ;
    String url2 = "http://" + localhost + "/user/get_tb.php";
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent.hasExtra("manv")) {
            String getmanv = intent.getStringExtra("manv");
            manv1 = getmanv;
            GetData();
        }

        GetData1(url2);

        lvTb = findViewById(R.id.lvTb);
        arrTb = new ArrayList<>();

        adapterTb = new TbAdapter(this, R.layout.thong_bao, arrTb);
        lvTb.setAdapter(adapterTb);

        AnhXa();

        dsnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DSNVActivity.class));
            }
        });

        dkl.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // Kiểm tra xem có phải là thứ 7 hoặc chủ nhật không
                if (check()) {
                    // Nếu là thứ 7 hoặc chủ nhật, hiển thị thông báo
                    showAlertDialog(MainActivity.this, "Thông báo", "Hiện tại đã quá hạn đăng ký lịch! Lần sau hãy đăng ký sớm hơn\n(Chỉ chấp nhận đăng ký từ thứ 2 đến hết thứ 6)");
                } else {
                    // Nếu không phải thứ 7 hoặc chủ nhật, chuyển tiếp sang view mới
                    // Thực hiện logic chuyển tiếp ở đây, ví dụ:
                    Intent intent = new Intent(MainActivity.this, DK_Lich_Lv_Activity.class);
                    intent.putExtra("manv", manv1);
                    startActivity(intent);
                }
            }
        });

        tnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ThemNV.class));
            }
        });

        xnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChangeListNV.class));
            }
        });

        xepllv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, XepLichLv.class));
            }
        });
        ytb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Youtube.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.VISIBLE);
                lvTb.setVisibility(View.GONE);
            }
        });
        thongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.GONE);
                lvTb.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean check() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Lấy ngày trong tuần (1: Thứ 2, 2: Thứ 3, ..., 7: Chủ nhật)
        int dayOfWeek = currentDate.getDayOfWeek().getValue();

        // Kiểm tra xem có phải là thứ 7 hoặc chủ nhật không
        return (dayOfWeek == DayOfWeek.SATURDAY.getValue());
    }
//     || dayOfWeek == DayOfWeek.SUNDAY.getValue()
    private void GetData1(String url2) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(MainActivity.this, "Cảnh báo!", "Không có thông báo nào!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String tb = object.optString("ThongBao", "");
                            String time = object.optString("ThoiGian", "");

                            arrTb.add(new Tb(tb, time));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapterTb.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void GetData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String manv = object.getString("MaNv");
                        String hoten = object.getString("HoTen");
                        String chucvu = object.getString("ChucVu");

                        // Lấy chuỗi Base64 từ JSON
                        String hinhBase64 = object.getString("HinhAnh");
                        // Chuyển chuỗi Base64 thành mảng byte
                        byte[] hinhBytes = new byte[0];

                        hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);
                        if (manv.equals(manv1)) {
                            tvhoten.setText(hoten);
                            tvChucVu.setText(chucvu);

                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                profile.setImageBitmap(bitmap);
                            }
                            break;
                        } else {
                            tvhoten.setText("Khách");
                            tvChucVu.setText("");
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Máy chủ bị tắt hoặc lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }


    private void AnhXa() {
        dsnv = findViewById(R.id.dsnv);
        dkl = findViewById(R.id.dkl);
        xemllv = findViewById(R.id.xemllv);
        xltp = findViewById(R.id.xltp);
        gtn = findViewById(R.id.gtn);
        tnv = findViewById(R.id.tnv);
        xnv = findViewById(R.id.xnv);
        xepllv = findViewById(R.id.xepllv);
        vtb = findViewById(R.id.vtb);
        home = findViewById(R.id.home);
        thongbao = findViewById(R.id.thongbao);
        person = findViewById(R.id.person);
        profile = findViewById(R.id.profile);
        ytb = findViewById(R.id.ytb);
        tvhoten = findViewById(R.id.tvhoten);
        tvChucVu = findViewById(R.id.tvChucVu);
        scrollView = findViewById(R.id.scrollView);
    }
}