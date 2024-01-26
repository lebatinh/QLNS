package com.example.qunlnhns.ql;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.qunlnhns.MainActivity;
import com.example.qunlnhns.ql.dslich.list_nv.List_Nv;
import com.example.qunlnhns.ql.dslich.list_nv.List_Nv_Adapter;
import com.example.qunlnhns.ql.dslich.thuongphat.ThuongPhat;
import com.example.qunlnhns.ql.dslich.thuongphat.ThuongPhatAdapter;
import com.example.qunlnhns.user.DKActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class XetLuong extends AppCompatActivity {

    private ImageButton btnHome, btnThem;
    private LinearLayout lnrNv, lnrXn, lnrXet, lnrThuong, lnrPhat, lnrRieng;
    ListView lvNv, lvThuong, lvPhat;
    private TextView tvMaNvHoTen, tvNgay, tvTotalThuong, tvTotalPhat, tvTong, tvThuong, tvPhat, tvThuong1, tvPhat1;
    private EditText edtLuong, edtChiPhi;
    private Button btnGui, btnTong;
    private String maNv, hoTen, luongCoBan, lyDoThuong, tienThuong, lyDoPhat, tienPhat, chiPhiKhac, tong, thoiGian;
    ArrayList<ThuongPhat> arrThuong, arrPhat;
    ArrayList<List_Nv> arrNv;
    ThuongPhatAdapter adapterThuong, adapterPhat;
    List_Nv_Adapter adapterNv;
    private boolean isLvThuongVisible = false;
    private boolean isLvPhatVisible = false;
    private Calendar calendar;
    String localhost = DKActivity.localhost;
    String url1 = "http://" + localhost + "/user/get_nv_luong.php";
    String url2 = "http://" + localhost + "/user/insert_luong.php";
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
                Toast.makeText(XetLuong.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luong);

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                builder.setTitle("Bạn muốn thưởng hay phạt?");
                builder.setPositiveButton("Thưởng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Hiển thị một AlertDialog khác để yêu cầu người dùng nhập thông tin
                        AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                        builder.setTitle("Thêm thưởng");
                        LinearLayout layout = new LinearLayout(XetLuong.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText editText1 = new EditText(XetLuong.this);
                        editText1.setHint("Nhập lí do thưởng");
                        layout.addView(editText1);
                        final EditText editText2 = new EditText(XetLuong.this);
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
                                    Toast.makeText(XetLuong.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                        builder.setTitle("Thêm phạt");
                        LinearLayout layout = new LinearLayout(XetLuong.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText editText1 = new EditText(XetLuong.this);
                        editText1.setHint("Nhập lí do");
                        layout.addView(editText1);
                        final EditText editText2 = new EditText(XetLuong.this);
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
                                    Toast.makeText(XetLuong.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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
                            showAlertDialog(XetLuong.this, "Cảnh báo!", "Bạn phải tính tổng lương trước!");
                        }
                    } else {
                        showAlertDialog(XetLuong.this, "Cảnh báo!", "Bạn quên chưa ghi lương cơ bản!");
                    }
                } else {
                    showAlertDialog(XetLuong.this, "Cảnh báo!", "Bạn quên chưa chọn ngày cho lịch của lương!");
                }
            }
        });
        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);

        // Khởi tạo ngày hiện tại
        calendar = Calendar.getInstance();
        tvNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
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
        tvThuong1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrThuong.setVisibility(View.GONE);
                lnrRieng.setVisibility(View.VISIBLE);
            }
        });
        tvPhat1.setOnClickListener(new View.OnClickListener() {
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
        lvThuong.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrThuong != null && position >= 0 && position < arrThuong.size()) {
                    Object item = parent.getItemAtPosition(position);
                    ThuongPhat thuongPhat = (ThuongPhat) item;
                    String lido = thuongPhat.getLiDo();
                    String sotien = thuongPhat.getSoTien();

                    AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn muốn thay đổi thưởng " + lido + " có số tiền là " + sotien + " như thế nào?");
                    builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Hiển thị một AlertDialog khác để yêu cầu người dùng nhập thông tin
                            AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                            builder.setTitle("Thay đổi thưởng");
                            LinearLayout layout = new LinearLayout(XetLuong.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            final EditText edt1 = new EditText(XetLuong.this);
                            edt1.setText(lido);
                            layout.addView(edt1);
                            final EditText edt2 = new EditText(XetLuong.this);
                            edt2.setText(sotien);
                            setupEditText(edt2);
                            layout.addView(edt2);
                            builder.setView(layout);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Lấy giá trị mới từ EditText
                                    String newLiDo = edt1.getText().toString().trim();
                                    String newSoTien = edt2.getText().toString().trim();
                                    if (!newLiDo.isEmpty() && !newSoTien.isEmpty()) {
                                        // Sửa item ở vị trí position trong danh sách dữ liệu
                                        ThuongPhat selectedItem = arrThuong.get(position);
                                        selectedItem.setLiDo(newLiDo);
                                        selectedItem.setSoTien(newSoTien);
                                        // Cập nhật ListView
                                        adapterThuong.notifyDataSetChanged();

                                        TotalThuong();
                                    } else {
                                        Toast.makeText(XetLuong.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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
                    builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            arrThuong.remove(item);
                            adapterThuong.notifyDataSetChanged();
                            TotalThuong();
                        }
                    });
                    builder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Hiển thị AlertDialog
                    builder.show();
                }
                return true;
            }
        });
        lvPhat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrPhat != null && position >= 0 && position < arrPhat.size()) {
                    Object item = parent.getItemAtPosition(position);
                    ThuongPhat thuongPhat = (ThuongPhat) item;
                    String lydo = thuongPhat.getLiDo();
                    String tienphat = thuongPhat.getSoTien();

                    AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn muốn thay đổi phạt " + lydo + " có số tiền là " + tienphat + " như thế nào?");
                    builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Hiển thị một AlertDialog khác để yêu cầu người dùng nhập thông tin
                            AlertDialog.Builder builder = new AlertDialog.Builder(XetLuong.this);
                            builder.setTitle("Thay đổi phạt");
                            LinearLayout layout = new LinearLayout(XetLuong.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            final EditText edtLydo = new EditText(XetLuong.this);
                            edtLydo.setText(lydo);
                            layout.addView(edtLydo);
                            final EditText edtTp = new EditText(XetLuong.this);
                            edtTp.setText(tienphat);
                            setupEditText(edtTp);
                            layout.addView(edtTp);
                            builder.setView(layout);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Lấy giá trị mới từ EditText
                                    String newLyDo = edtLydo.getText().toString().trim();
                                    String newTienPhat = edtTp.getText().toString().trim();
                                    if (!newLyDo.isEmpty() && !newTienPhat.isEmpty()) {
                                        // Sửa item ở vị trí position trong danh sách dữ liệu
                                        ThuongPhat selectedItem = arrPhat.get(position);
                                        selectedItem.setLiDo(newLyDo);
                                        selectedItem.setSoTien(newTienPhat);
                                        // Cập nhật ListView
                                        adapterPhat.notifyDataSetChanged();

                                        TotalPhat();
                                    } else {
                                        Toast.makeText(XetLuong.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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
                    builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            arrPhat.remove(item);
                            adapterPhat.notifyDataSetChanged();
                            TotalPhat();
                        }
                    });
                    builder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Hiển thị AlertDialog
                    builder.show();
                }
                return true;
            }
        });
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

    private void showDatePickerDialog() {
        // Lấy thời gian hiện tại
        long currentTimeMillis = System.currentTimeMillis();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTimeMillis);

        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                        // Lưu giá trị ngày đã chọn vào Calendar
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, monthOfYear);

                        // Hiển thị tháng đã chọn trong TextView
                        updateTextView();
                    }
                },
                year,
                month,
                0
        );

        datePickerDialog.getDatePicker().updateDate(year, month, 1);

        // Đặt tiêu đề cho DatePickerDialog
        datePickerDialog.setTitle("Hãy chọn ngày bất kì ở tháng dùng để xét lương");

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }


    private void updateTextView() {
        // Format ngày để hiển thị trong TextView
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());

        // Đặt giá trị cho TextView
        tvNgay.setText(formattedDate);
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
                        Toast.makeText(XetLuong.this, "Bạn chỉ có thể dùng các kí tự từ 0 đến 9!", Toast.LENGTH_SHORT).show();
                    }
                }
                return ""; // Không chấp nhận ký tự này
            }
        }});
    }

    private void QueryLuong(String url2) {
        final ProgressDialog progressDialog = new ProgressDialog(XetLuong.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("success")) {
                    showAlertDialog(XetLuong.this, "Thông báo", "Xét lương thành công! Bạn đã có thể xem danh sách lương.");
                } else if (response.equals("fail")) {
                    showAlertDialog(XetLuong.this, "Cảnh báo!", "Xét lương không thành công.\nThông tin nhân viên này đã tồn tại!");
                } else {
                    showAlertDialog(XetLuong.this, "Cảnh báo!", "Xét lương không thành công!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(XetLuong.this, "Lỗi Thêm nhân viên: " + error.toString(), Toast.LENGTH_SHORT).show();
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
        final ProgressDialog progressDialog = new ProgressDialog(XetLuong.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(XetLuong.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
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
                Toast.makeText(XetLuong.this, "Error", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(XetLuong.this, MainActivity.class));
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
        tvThuong1 = findViewById(R.id.tvThuong1);
        tvPhat1 = findViewById(R.id.tvPhat1);
        edtLuong = findViewById(R.id.edtLuong);
        edtChiPhi = findViewById(R.id.edtChiPhi);
        btnGui = findViewById(R.id.btnGui);
        btnTong = findViewById(R.id.btnTong);
    }
}