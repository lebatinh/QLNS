package com.example.qunlnhns.nv;

import static com.example.qunlnhns.user.DNActivity.AlertDialogHelper.showAlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.DatabaseSQlite;
import com.example.qunlnhns.MainActivity;
import com.example.qunlnhns.R;
import com.example.qunlnhns.nv.message.msg;
import com.example.qunlnhns.nv.message.msgAdapter;
import com.example.qunlnhns.ql.dslich.list_nv.List_Nv;
import com.example.qunlnhns.ql.dslich.list_nv.List_Nv_Adapter;
import com.example.qunlnhns.user.DKActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    ArrayList<List_Nv> arrNv;
    List_Nv_Adapter adapter;
    ListView lvNv;
    String manv, hoten, hinh;
    private ImageButton btnHome;
    private SearchView searchView;
    private RecyclerView recyclerView;
    DatabaseReference reference;
    ArrayList<msg> arrMsg;
    msgAdapter msgAdapter;
    DatabaseSQlite databaseSQlite;
    String mainId;
    String otherUserId;
    String localhost = DKActivity.localhost;
    String url = "http://" + localhost + "/user/getdata_nv.php";
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
                Toast.makeText(MessageActivity.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        AnhXa();

        // Khởi tạo DatabaseReference
        reference = FirebaseDatabase.getInstance().getReference();

        // Khởi tạo đối tượng Database
        databaseSQlite = new DatabaseSQlite(this, "main.sqlite", null, 1);
        Pair<String, String> result = databaseSQlite.SELECT_MANV_MAIN();
        mainId = result.first;

        arrNv = new ArrayList<>();
        adapter = new List_Nv_Adapter(MessageActivity.this, R.layout.nv, arrNv);
        lvNv.setAdapter(adapter);

        // Khởi tạo RecyclerView và adapter
        arrMsg = new ArrayList<>();
        msgAdapter = new msgAdapter(arrMsg, MessageActivity.this);

        // Thiết lập LayoutManager cho RecyclerView (LinearLayoutManager là một ví dụ)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        msgAdapter.setItemClickListener((view, position, id, ten) -> {
            Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
            intent.putExtra("ID", id);
            intent.putExtra("Ten", ten);
            startActivity(intent);
        });

        // Thiết lập adapter cho RecyclerView
        recyclerView.setAdapter(msgAdapter);
        lvNv.setOnItemClickListener((parent, view, position, id) -> {
            if (arrNv != null && position >= 0 && position < arrNv.size()) {
                Object item = parent.getItemAtPosition(position);
                List_Nv list_nv = (List_Nv) item;
                manv = list_nv.getMaNV();
                hoten = list_nv.getHoTen();
            }

            Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
            intent.putExtra("ID", manv);
            intent.putExtra("Ten", hoten);
            startActivity(intent);
        });
        btnHome.setOnClickListener(v -> onBackPressed());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Thực hiện tìm kiếm khi người dùng nhấn nút tìm kiếm trên bàn phím
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Thực hiện tìm kiếm theo mỗi ký tự thay đổi trong ô tìm kiếm
                if (!newText.isEmpty()) {
                    performSearch(newText);
                } else {
                    lvNv.setVisibility(View.GONE);
                }

                return true;
            }
        });

        // Gọi hàm để lấy thông tin từ tất cả các nút con của "Chats"
        getMessageFromFirebase();

        fetchData();
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

    private void getMessageFromFirebase() {
        DatabaseReference chatsReference = reference.child("Chats");
        chatsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String lastSenderId = chatSnapshot.child("lastSenderId").getValue(String.class);
                    String lastSenderTime = chatSnapshot.child("lastSenderTime").getValue(String.class);

                    // Lấy giá trị từ nút UID
                    String user1 = chatSnapshot.child("UID").child("user1").getValue(String.class);
                    String user2 = chatSnapshot.child("UID").child("user2").getValue(String.class);

                    // Kiểm tra xem mainId có trùng với user1 hay user2 không
                    if (mainId.equals(user1)) {
                        otherUserId = user2;
                    } else if (mainId.equals(user2)) {
                        otherUserId = user1;
                    }

                    String lastMsg;
                    // Kiểm tra nếu lastMessageId bằng mainId
                    if (lastSenderId.equals(mainId)) {
                        // Nếu lastMessageId bằng mainId, đặt lastMessage là "You :" + giá trị của nhánh lastMessage
                        lastMsg = "You :" + chatSnapshot.child("lastMessage").getValue(String.class);
                    } else {
                        // Ngược lại, đặt lastMessage là giá trị từ nút lastMessage
                        lastMsg = chatSnapshot.child("lastMessage").getValue(String.class);
                    }

                    // Gọi hàm để lấy thông tin hình ảnh và tên từ URL
                    getTT(url, otherUserId, lastMsg, lastSenderId, lastSenderTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void getTT(String url, String otherUserId, String lastMessage, String lastSenderId, String lastSenderTime) {
        // Gọi Volley để lấy thông tin từ URL
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(MessageActivity.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String maNv = object.optString("MaNv", "");

                            if (otherUserId.equals(maNv)) {
                                String hoTen = object.optString("HoTen", "");

                                // Lấy chuỗi Base64 từ JSON
                                String hinhBase64 = object.optString("HinhAnh", "");
                                // Chuyển chuỗi Base64 thành mảng byte
                                byte[] hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                                // Sau khi lấy được thông tin hình ảnh và tên
                                // Gọi hàm để đặt dữ liệu vào RecyclerView
                                MessageCreateList(hinhBytes, otherUserId, hoTen, lastMessage, lastSenderTime);
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MessageCreateList(byte[] hinhAnh, String id, String name, String lastMessage, String lastSenderTime) {
        // Tạo đối tượng msg và thêm vào RecyclerView
        msg newMsg = new msg(hinhAnh, id, name, lastMessage, lastSenderTime);
        arrMsg.add(newMsg);
        msgAdapter.notifyDataSetChanged();
    }

    private void fetchData() {
        arrNv.clear(); // Xóa dữ liệu cũ trước khi lấy dữ liệu mới
        GetData(url);
    }

    private void GetData(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() == 0) {
                    // Hiển thị thông báo nếu không có nhân viên nào
                    showAlertDialog(MessageActivity.this, "Cảnh báo!", "Không có nhân viên trong danh sách!");
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String maNv = object.optString("MaNv", "");
                            String hoTen = object.optString("HoTen", "");

                            // Lấy chuỗi Base64 từ JSON
                            String hinhBase64 = object.optString("HinhAnh", "");
                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

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
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void performSearch(String query) {
        ArrayList<List_Nv> searchResults = new ArrayList<>();

        for (List_Nv nv : arrNv) {
            if (nv.getHoTen().toLowerCase().contains(query.toLowerCase()) ||
                    nv.getMaNV().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(nv);
            }
        }

        if (searchResults.isEmpty() && query.isEmpty()) {
            // Nếu danh sách tìm kiếm trống và người dùng chưa nhập gì, ẩn ListView
            lvNv.setVisibility(View.GONE);
        } else {
            // Ngược lại, hiển thị ListView và cập nhật adapter
            lvNv.setVisibility(View.VISIBLE);
            adapter = new List_Nv_Adapter(MessageActivity.this, R.layout.nv, searchResults);
            lvNv.setAdapter(adapter);
        }
    }

    // Nếu người dùng ấn nút quay lại
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn có chắc muốn quay lại màn hình chính không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(MessageActivity.this, MainActivity.class));
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
        searchView = findViewById(R.id.searchView);
        lvNv = findViewById(R.id.lvNv);
        recyclerView = findViewById(R.id.recyclerView);
    }
}