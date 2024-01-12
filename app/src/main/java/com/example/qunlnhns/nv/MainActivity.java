package com.example.qunlnhns.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.qunlnhns.Database;
import com.example.qunlnhns.R;
import com.example.qunlnhns.api.Youtube;
import com.example.qunlnhns.nv.tb.Tb;
import com.example.qunlnhns.nv.tb.TbAdapter;
import com.example.qunlnhns.ql.GuiThongBao;
import com.example.qunlnhns.ql.XetLuong;
import com.example.qunlnhns.ql.nv.ChangeListNV;
import com.example.qunlnhns.ql.nv.ThemNV;
import com.example.qunlnhns.ql.dslich.XepLichLv;
import com.example.qunlnhns.user.DKActivity;
import com.example.qunlnhns.user.DNActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ListView lvTbAll, lvTbChoice;
    ArrayList<Tb> arrTbAll;
    ArrayList<Tb> arrTbChoice;
    TbAdapter adapterTbAll;
    TbAdapter adapterTbChoice;
    private ImageButton dsnv, dkl, xemllv, xltp, xetltp, gtn, tnv, sttnv, xnv, xepllv, vtb, home, thongbao, person, ytb, luong;
    private ImageView profile;
    private TextView tvhoten, tvChucVu;
    private String manv1;
    private ScrollView scrollView;
    private LinearLayout lnrTbChung, lnrTbRieng, lnrTb;
    private View view1, view2;
    String localhost = DKActivity.localhost;
    private String url = "http://" + localhost + "/user/get_manv.php";
    String url2 = "http://" + localhost + "/user/get_tb.php";
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
                Toast.makeText(MainActivity.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo đối tượng Database
        database = new Database(this, "main.sqlite", null, 1);
        manv1 = database.SELECT_MANV_MAIN();

        GetData();

        lvTbAll = findViewById(R.id.lvTbAll);
        arrTbAll = new ArrayList<>();
        adapterTbAll = new TbAdapter(this, R.layout.thong_bao, arrTbAll);
        lvTbAll.setAdapter(adapterTbAll);

        lvTbChoice = findViewById(R.id.lvTbChoice);
        arrTbChoice = new ArrayList<>();
        adapterTbChoice = new TbAdapter(this, R.layout.thong_bao, arrTbChoice);
        lvTbChoice.setAdapter(adapterTbChoice);

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
                lnrTb.setVisibility(View.GONE);
            }
        });
        thongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.setVisibility(View.GONE);
                lnrTb.setVisibility(View.VISIBLE);
            }
        });

        GetTbAll(url2);
        GetTbChoice(url2);

        lnrTbChung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.GONE);
                lvTbAll.setVisibility(View.VISIBLE);
                lvTbChoice.setVisibility(View.GONE);
            }
        });
        lnrTbRieng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                lvTbAll.setVisibility(View.GONE);
                lvTbChoice.setVisibility(View.VISIBLE);
            }
        });
        xemllv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Xem_Lich_Lv.class));
            }
        });
        vtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GuiThongBao.class));
            }
        });
        xetltp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, XetLuong.class));
            }
        });
        xltp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Xem_Luong.class));
            }
        });
        luong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Bang_Luong.class));
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

    private void GetTbChoice(String url2) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(MainActivity.this, "Cảnh báo!", "Không có thông báo nào!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String tb = object.optString("ThongBao", "");
                            String time = object.optString("ThoiGian", "");
                            String phamvi = object.optString("PhamVi", "");
                            if (Arrays.asList(phamvi.split(",")).contains(manv1.trim())) {
                                arrTbChoice.add(new Tb(tb, time));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapterTbChoice.notifyDataSetChanged();
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

    // Nếu người dùng ấn nút quay lại
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận quay lại");
        builder.setMessage("Bạn có muốn quay lại màn hình đăng nhập không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(MainActivity.this, DNActivity.class));
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

    private void GetTbAll(String url2) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(MainActivity.this, "Cảnh báo!", "Không có thông báo nào!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String tb = object.optString("ThongBao", "");
                            String time = object.optString("ThoiGian", "");
                            String phamvi = object.optString("PhamVi", "");
                            if (phamvi.equals("all")){
                                arrTbAll.add(new Tb(tb, time));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapterTbAll.notifyDataSetChanged();
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
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
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
        xetltp = findViewById(R.id.xetltp);
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
        luong = findViewById(R.id.luong);
        tvhoten = findViewById(R.id.tvhoten);
        tvChucVu = findViewById(R.id.tvChucVu);
        scrollView = findViewById(R.id.scrollView);
        lnrTbChung = findViewById(R.id.lnrTbChung);
        lnrTbRieng = findViewById(R.id.lnrTbRieng);
        lnrTb = findViewById(R.id.lnrTb);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
    }
}