package com.example.qunlnhns.ql.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.DK_Lich_Lv_Activity;
import com.example.qunlnhns.nv.dsnv.DSNVActivity;
import com.example.qunlnhns.nv.MainActivity;
import com.example.qunlnhns.nv.dsnv.NhanVien;
import com.example.qunlnhns.user.DKActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ThemNV extends AppCompatActivity {

    private ImageButton btnHome;
    private ImageView imgHinh;
    private EditText edtMaNv, edtHoTen, edtNgaySinh, edtGioiTinh, edtQueQuan, edtDiaChi, edtChucVu, edtSdt;
    private Button btnThem, btnXemDs;
    private String manv, hoten, ngaysinh, gioitinh, quequan, diachi, chucvu, sdt;
    private NhanVien nhanVien;
    String localhost = DKActivity.localhost;
    String url = "http://"+localhost+"/user/insert_nv.php";

    final int REQUEST_CODE_CAMERA = 123;
    final int REQUEST_CODE_FOLDER = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nv);

        AnhXa();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themNv();
            }
        });

        btnXemDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThemNV.this, DSNVActivity.class));
            }
        });

        imgHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ThemNV.this);
                builder.setTitle("Bạn muốn tải ảnh lên bằng gì?");
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                ThemNV.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CODE_CAMERA
                        );
                    }
                });
                builder.setNegativeButton("Thư viện ảnh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                ThemNV.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_FOLDER
                        );
                    }
                });
                builder.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Xác nhận quay lại");
        builder.setMessage("Bạn có muốn quay lại màn hình chính không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(ThemNV.this, MainActivity.class));
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

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void themNv() {
        AnhXa();
        if (!manv.isEmpty() && !hoten.isEmpty() && !ngaysinh.isEmpty() && !gioitinh.isEmpty() && !quequan.isEmpty() && !diachi.isEmpty() && !chucvu.isEmpty() && !sdt.isEmpty()) {
            // Chuyển đổi ảnh từ ImageView sang mảng byte
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imgHinh.getDrawable();
            String encodedImage = null;
            if (bitmapDrawable != null) {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                encodedImage = bitmapToBase64(bitmap);
            }

            if (encodedImage != null) {
                // Gọi phương thức setHinhBase64 của đối tượng NhanVien
                nhanVien.setHinhBase64(encodedImage);

                // Gọi phương thức QueryData()
                QueryData();
            } else {
                showAlertDialog(ThemNV.this, "Cảnh báo!", "Bạn phải chọn ảnh!");
            }
        } else {
            showAlertDialog(ThemNV.this, "Cảnh báo!", "Bạn phải nhập đầy đủ thông tin!");
        }
    }

    private void QueryData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    showAlertDialog(ThemNV.this, "Thông báo", "Thêm thông tin nhân viên thành công! Bạn đã có thể xem nhân viên trong danh sách.");
                } else if (response.equals("fail")) {
                    showAlertDialog(ThemNV.this, "Cảnh báo!", "Thêm nhân viên không thành công.\nThông tin nhân viên này đã tồn tại!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThemNV.this, "Lỗi Thêm nhân viên: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Truyền tham số cho yêu cầu POST
                Map<String, String> params = new HashMap<>();
                params.put("manv", manv);
                params.put("hoten", hoten);
                params.put("ngaysinh", ngaysinh);
                params.put("gioitinh", gioitinh);
                params.put("quequan", quequan);
                params.put("diachi", diachi);
                params.put("chucvu", chucvu);
                params.put("sdt", sdt);

                // Chuyển đổi ảnh từ ImageView sang mảng byte
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgHinh.getDrawable();
                if (bitmapDrawable != null) {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    String encodedImage = bitmapToBase64(bitmap);
                    params.put("hinhanh", encodedImage);
                }

                return params;
            }
        };

        // Thêm yêu cầu vào hàng đợi Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Hàm để chuyển đổi Bitmap sang chuỗi Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Mở camera để chụp ảnh
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }else {
                    Toast.makeText(this, "Bạn vừa hủy quyền truy cập camera", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_FOLDER:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Mở thư viện ảnh để chọn ảnh
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_FOLDER);
                }else {
                    Toast.makeText(this, "Bạn vừa hủy quyền truy cập thư viện ảnh", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgHinh.setImageBitmap(bitmap);
        }
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgHinh.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void AnhXa() {
        btnHome = findViewById(R.id.btnHome);
        imgHinh = findViewById(R.id.imgHinh);

        edtMaNv = findViewById(R.id.edtMaNv);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtGioiTinh = findViewById(R.id.edtGioiTinh);
        edtQueQuan = findViewById(R.id.edtQueQuan);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        edtChucVu = findViewById(R.id.edtChucVu);
        edtSdt = findViewById(R.id.edtSdt);

        btnThem = findViewById(R.id.btnThem);
        btnXemDs = findViewById(R.id.btnXemDs);

        manv = edtMaNv.getText().toString().trim();
        hoten = edtHoTen.getText().toString().trim();
        ngaysinh = edtNgaySinh.getText().toString().trim();
        gioitinh = edtGioiTinh.getText().toString().trim();
        quequan = edtQueQuan.getText().toString().trim();
        diachi = edtDiaChi.getText().toString().trim();
        chucvu = edtChucVu.getText().toString().trim();
        sdt = edtSdt.getText().toString().trim();

        nhanVien = new NhanVien(manv, hoten, ngaysinh, gioitinh, quequan, diachi, chucvu, sdt, null);
    }
}