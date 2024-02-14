package com.example.qunlnhns;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.giaitri.Chorme;
import com.example.qunlnhns.giaitri.News;
import com.example.qunlnhns.giaitri.Youtube;
import com.example.qunlnhns.ql.Bang_Luong;
import com.example.qunlnhns.nv.DK_Lich_Lv_Activity;
import com.example.qunlnhns.nv.DSNVActivity;
import com.example.qunlnhns.nv.MessageActivity;
import com.example.qunlnhns.nv.Xem_Lich_Lv;
import com.example.qunlnhns.nv.Xem_Luong;
import com.example.qunlnhns.nv.tb.Tb;
import com.example.qunlnhns.nv.tb.TbAdapter;
import com.example.qunlnhns.ql.PutNotification.GuiThongBao;
import com.example.qunlnhns.ql.XetLuong;
import com.example.qunlnhns.ql.ds_nv.ChangeListNV;
import com.example.qunlnhns.ql.ds_nv.ThemNV;
import com.example.qunlnhns.ql.dslich.XepLichLv;
import com.example.qunlnhns.user.DKActivity;
import com.example.qunlnhns.user.DMKActivity;
import com.example.qunlnhns.user.DNActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView lvTbAll, lvTbChoice;
    ArrayList<Tb> arrTbAll;
    ArrayList<Tb> arrTbChoice;
    TbAdapter adapterTbAll;
    TbAdapter adapterTbChoice;
    private ImageButton dsnv, dkl, xemllv, xltp, xetltp, gtn, tnv, xnv, xepllv, vtb, home, thongbao, person, ytb, luong, chorme, spotify, news;
    private ImageView profile, imgAcount;
    private TextView tvhoten, tvChucVu, text, text1, tvName, tvAcount, tvHDSD, tvDangXuat, tvDMK;
    private String manv1, admin, savedToken;
    private ScrollView scrollView;
    private LinearLayout lnrTbChung, lnrTbRieng, lnrTb, lnrQl, lnrTbc, lnrTbr, lnrHead;
    private LinearLayout lnrPerson;
    private Drawable drawableHome, drawableTb, drawablePerson;
    private View view1, view2;
    String localhost = DKActivity.localhost;
    String url = "http://" + localhost + "/user/get_manv.php";
    String url1 = "http://" + localhost + "/user/insert_update_token.php";
    String url2 = "http://" + localhost + "/user/get_tb.php";
    DatabaseSQlite databaseSQlite;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();
        // Khởi tạo đối tượng Database
        databaseSQlite = new DatabaseSQlite(this, "main.sqlite", null, 1);
        Pair<String, String> result = databaseSQlite.SELECT_MANV_MAIN();
        manv1 = result.first;
        admin = result.second;

        String tk = databaseSQlite.SELECT_TK_MAIN();
        tvAcount.setText(tk);

        if (admin.equals("1")) {
            lnrQl.setVisibility(View.VISIBLE);
        } else if (admin.equals("0")) {
            lnrQl.setVisibility(View.GONE);
        }

        GetData();

        lvTbAll = findViewById(R.id.lvTbAll);
        arrTbAll = new ArrayList<>();
        adapterTbAll = new TbAdapter(this, R.layout.thong_bao, arrTbAll);
        lvTbAll.setAdapter(adapterTbAll);

        lvTbChoice = findViewById(R.id.lvTbChoice);
        arrTbChoice = new ArrayList<>();
        adapterTbChoice = new TbAdapter(this, R.layout.thong_bao, arrTbChoice);
        lvTbChoice.setAdapter(adapterTbChoice);


        spotify.setOnClickListener(v -> openSpotify());
        dsnv.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DSNVActivity.class)));
        tnv.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ThemNV.class)));
        xnv.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChangeListNV.class)));
        xepllv.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, XepLichLv.class)));
        ytb.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Youtube.class)));
        news.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, News.class)));
        chorme.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Chorme.class)));
        xemllv.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Xem_Lich_Lv.class)));
        vtb.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GuiThongBao.class)));
        xetltp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, XetLuong.class)));
        xltp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Xem_Luong.class)));
        luong.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Bang_Luong.class)));
        gtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MessageActivity.class)));
        tvDangXuat.setOnClickListener(v -> onBackPressed());
        tvDMK.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DMKActivity.class)));
        tvHDSD.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HDSD.class)));

        dkl.setOnClickListener(v -> {
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
        });

        drawableHome = getResources().getDrawable(R.drawable.home);
        drawablePerson = getResources().getDrawable(R.drawable.person);
        drawableTb = getResources().getDrawable(R.drawable.notifications);

        // Tạo một PorterDuffColorFilter để chuyển màu trắng thành màu đen
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        drawableHome.setColorFilter(null);
        drawableTb.setColorFilter(colorFilter);
        drawablePerson.setColorFilter(colorFilter);
        home.setImageDrawable(drawableHome);
        thongbao.setImageDrawable(drawableTb);
        person.setImageDrawable(drawablePerson);

        home.setOnClickListener(v -> {
            drawableHome.setColorFilter(null);
            drawableTb.setColorFilter(colorFilter);
            drawablePerson.setColorFilter(colorFilter);
            home.setImageDrawable(drawableHome);
            thongbao.setImageDrawable(drawableTb);
            person.setImageDrawable(drawablePerson);

            scrollView.setVisibility(View.VISIBLE);
            lnrTb.setVisibility(View.GONE);
            lnrPerson.setVisibility(View.GONE);
            lnrHead.setVisibility(View.VISIBLE);
        });
        thongbao.setOnClickListener(v -> {
            drawableHome.setColorFilter(colorFilter);
            drawableTb.setColorFilter(null);
            drawablePerson.setColorFilter(colorFilter);
            home.setImageDrawable(drawableHome);
            thongbao.setImageDrawable(drawableTb);
            person.setImageDrawable(drawablePerson);

            scrollView.setVisibility(View.GONE);
            lnrTb.setVisibility(View.VISIBLE);
            lnrPerson.setVisibility(View.GONE);
            lnrHead.setVisibility(View.VISIBLE);
        });
        person.setOnClickListener(v -> {
            drawableHome.setColorFilter(colorFilter);
            drawableTb.setColorFilter(colorFilter);
            drawablePerson.setColorFilter(null);
            home.setImageDrawable(drawableHome);
            thongbao.setImageDrawable(drawableTb);
            person.setImageDrawable(drawablePerson);

            scrollView.setVisibility(View.GONE);
            lnrTb.setVisibility(View.GONE);
            lnrPerson.setVisibility(View.VISIBLE);
            lnrHead.setVisibility(View.GONE);
        });
        GetTbAll(url2);
        GetTbChoice(url2);

        lnrTbChung.setOnClickListener(v -> {
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
            lnrTbc.setVisibility(View.VISIBLE);
            lnrTbr.setVisibility(View.GONE);
        });
        lnrTbRieng.setOnClickListener(v -> {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
            lnrTbc.setVisibility(View.GONE);
            lnrTbr.setVisibility(View.VISIBLE);
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                finish();  // Kết thúc Activity hiện tại
                startActivity(intent);
            }
        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }

            // Get new FCM registration token
            String newToken = task.getResult();

            onNewToken(newToken);

            // Kiểm tra xem token có thay đổi hay không
            if (!newToken.equals(savedToken)) {
                // Lưu trữ token mới vào SharedPreferences hoặc nơi bạn lưu trữ thông tin
                savedToken = newToken;

                // Gửi token mới đến server
                sendRegistrationToServer(newToken);
            }
        });

        // Đăng ký thiết bị để nhận thông báo từ FCM
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        // Đăng ký thiết bị để nhận thông báo từ FCM
        FirebaseMessaging.getInstance().subscribeToTopic(manv1);

        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);
    }

    public void onNewToken(String token) {
        // Lưu trữ token mới vào SharedPreferences hoặc nơi bạn lưu trữ thông tin
        savedToken = token;

        // Gửi token mới đến server
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("manv", manv1);
                params.put("token", token);
                return params;
            }
        };
        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void openSpotify() {
        // Tạo Intent để mở ứng dụng Spotify
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.spotify.music");

        // Kiểm tra xem ứng dụng Spotify đã được cài đặt trên thiết bị hay không
        PackageManager packageManager = getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null) {
            // Mở ứng dụng Spotify
            startActivity(intent);
        } else {
            // Nếu Spotify chưa được cài đặt, bạn có thể xử lý theo cách khác hoặc thông báo cho người dùng
            // Ví dụ: Mở trang cài đặt Spotify trên Google Play Store
            openSpotifyInPlayStore();
        }
    }

    private void openSpotifyInPlayStore() {
        // Tạo Intent để mở trang cài đặt Spotify trên Google Play Store
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music"));

        // Mở trang cài đặt trên Play Store
        startActivity(intent);
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
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String title = object.optString("Title", "");
                            String tb = object.optString("ThongBao", "");
                            String time = object.optString("ThoiGian", "");
                            String phamvi = object.optString("PhamVi", "");
                            if (Arrays.asList(phamvi.split(",")).contains(manv1.trim())) {
                                arrTbChoice.add(new Tb(title, tb, time));
                                text1.setVisibility(View.GONE); // Ẩn TextView khi ListView không rỗng
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
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String title = object.optString("Title", "");
                            String tb = object.optString("ThongBao", "");
                            String time = object.optString("ThoiGian", "");
                            String phamvi = object.optString("PhamVi", "");
                            if (phamvi.equals("all")) {
                                arrTbAll.add(new Tb(title, tb, time));
                                text.setVisibility(View.GONE); // Ẩn TextView khi ListView không rỗng
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
                            tvName.setText(hoten);
                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                profile.setImageBitmap(bitmap);
                                imgAcount.setImageBitmap(bitmap);
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
        news = findViewById(R.id.news);
        tvhoten = findViewById(R.id.tvhoten);
        tvChucVu = findViewById(R.id.tvChucVu);
        chorme = findViewById(R.id.chorme);
        spotify = findViewById(R.id.spotify);
        scrollView = findViewById(R.id.scrollView);
        lnrTbChung = findViewById(R.id.lnrTbChung);
        lnrTbRieng = findViewById(R.id.lnrTbRieng);
        lnrTbc = findViewById(R.id.lnrTbc);
        lnrTbr = findViewById(R.id.lnrTbr);
        lnrHead = findViewById(R.id.lnrHead);
        lnrPerson = findViewById(R.id.lnrPerson);
        imgAcount = findViewById(R.id.imgAcount);
        tvName = findViewById(R.id.tvName);
        tvAcount = findViewById(R.id.tvAcount);
        tvHDSD = findViewById(R.id.tvHDSD);
        tvDangXuat = findViewById(R.id.tvDangXuat);
        tvDMK = findViewById(R.id.tvDMK);
        text = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);
        lnrTb = findViewById(R.id.lnrTb);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        lnrQl = findViewById(R.id.lnrQl);
    }
}