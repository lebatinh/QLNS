package com.example.qunlnhns;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.qunlnhns.chat.Chat;
import com.example.qunlnhns.chat.ChatAdapter;
import com.example.qunlnhns.user.DKActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ImageButton imgBack, btnSend;
    private ImageView imgHinh;
    private TextView tvTen;
    private TextInputEditText edtMessage;
    String receiverId, receiverName, senderId;
    Database database;
    private DatabaseReference reference;
    ChatAdapter adapter;
    List<Chat> chatList;
    private RecyclerView recyclerView;
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
                Toast.makeText(ChatActivity.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();

                // Nếu không có kết nối, lặp lại kiểm tra ngay sau một khoảng thời gian
                handler.postDelayed(this, INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        AnhXa();
        GetData(url);

        // Khởi tạo đối tượng Database
        database = new Database(this, "main.sqlite", null, 1);
        Pair<String, String> result = database.SELECT_MANV_MAIN();
        senderId = result.first;

        Intent intent = getIntent();
        receiverId = intent.getStringExtra("ID");
        receiverName = intent.getStringExtra("Ten");

        tvTen.setText(receiverName);

        imgBack.setOnClickListener(v -> onBackPressed());

        btnSend.setOnClickListener(v -> {
            String msg = edtMessage.getText().toString().trim();

            // Lấy thời gian hiện tại đã được định dạng
            String datetime = DateTimeUtils.getCurrentFormattedDateTime();

            if (!msg.isEmpty()) {
                sendMessage(senderId, receiverId, msg, datetime);
                edtMessage.setText("");
            } else {
                edtMessage.setHint("Không được bỏ trống");
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Kiểm tra layoutManager có tồn tại và có ít nhất một item hay không
        if (layoutManager != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            // Sử dụng smoothScrollToPosition để cuộn xuống item cuối cùng
            layoutManager.smoothScrollToPosition(recyclerView, null, recyclerView.getAdapter().getItemCount() - 1);
        }

        readMessage(senderId, receiverId);
        // Bắt đầu kiểm tra ngay sau khi hoạt động được tạo
        handler.post(runnable);
    }

    private void sendMessage(String sender, String receiver, String message, String time) {
        // Thêm tin nhắn vào bảng sendId_receiveId
        addMessageToChatsTable(sender, receiver, message, time);
    }

    private void addMessageToChatsTable(String sender, String receiver, String message, String time) {
        // Tạo key cho bảng Chats
        String chatsKey = generateChatsKey(sender, receiver);

        // Tạo nút UID nếu chưa tồn tại
        DatabaseReference uidRef = reference.child("Chats").child(chatsKey).child("UID");
        uidRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Nếu nút UID chưa tồn tại, thêm vào
                    uidRef.child("user1").setValue(senderId);
                    uidRef.child("user2").setValue(receiverId);
                }

                // Thêm tin nhắn vào nút ChatDetail
                addMessageToChatDetail(chatsKey, sender, receiver, message, time);

                // Kiểm tra xem bảng Chats đã tồn tại hay không
                DatabaseReference chatsRef = reference.child("Chats").child(chatsKey);
                chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Nếu bảng Chats chưa tồn tại, tạo mới
                            createChatsNode(chatsKey, sender, receiver, message, time);
                        } else {
                            // Nếu bảng Chats đã tồn tại, cập nhật tin nhắn mới nhất trong nút Chats
                            updateLastMessage(chatsKey, message, sender, time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private String generateChatsKey(String sender, String receiver) {
        // Sắp xếp chuỗi ID người gửi và người nhận để có thứ tự nhất định
        return sender.compareTo(receiver) < 0 ? sender + "_" + receiver : receiver + "_" + sender;
    }

    private void addMessageToChatDetail(String chatsKey, String sender, String receiver, String message, String time) {
        // Thêm tin nhắn vào nút ChatDetail
        DatabaseReference chatDetailRef = reference.child("Chats").child(chatsKey).child("ChatDetail").push();
        HashMap<String, Object> chatDetailMap = new HashMap<>();
        chatDetailMap.put("sender", sender);
        chatDetailMap.put("receiver", receiver);
        chatDetailMap.put("message", message);
        chatDetailMap.put("time", time);

        chatDetailRef.setValue(chatDetailMap);
    }

    private void createChatsNode(String chatsKey, String sender, String receiver, String message, String time) {
        // Tạo nút Chats
        DatabaseReference chatsRef = reference.child("Chats").child(chatsKey);

        // Tạo nút sendId_receiveId
        DatabaseReference sendIdReceiveIdRef = chatsRef.child("sendId_receiveId");
        sendIdReceiveIdRef.child("ChatDetail").push().setValue(new Chat(sender, receiver, message, time));

        // Cập nhật lastMessage, lastSenderId, lastSenderTime
        chatsRef.child("lastMessage").setValue(message);
        chatsRef.child("lastSenderId").setValue(sender);
        chatsRef.child("lastSenderTime").setValue(time);
    }

    private void updateLastMessage(String chatsKey, String message, String sender, String time) {
        // Cập nhật tin nhắn mới nhất trong nút Chats
        DatabaseReference chatsRef = reference.child("Chats").child(chatsKey);
        chatsRef.child("lastMessage").setValue(message);
        chatsRef.child("lastSenderId").setValue(sender);
        chatsRef.child("lastSenderTime").setValue(time);
    }

    private void readMessage(String sender, String receiver) {
        chatList = new ArrayList<>();

        // Đảm bảo thứ tự nhất định khi nối chuỗi ID người gửi và người nhận
        String chatKey = generateChatsKey(sender, receiver);

        DatabaseReference chatDetailRef = reference.child("Chats").child(chatKey).child("ChatDetail");

        chatDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if (chat != null) {
                        chatList.add(chat);
                    }
                }
                adapter = new ChatAdapter(getApplicationContext(), (ArrayList<Chat>) chatList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private static class DateTimeUtils {
        public static String getCurrentFormattedDateTime() {
            // Lấy thời gian hiện tại
            Calendar calendar = Calendar.getInstance();
            // Lấy ngày và giờ hiện tại
            Date datetime = calendar.getTime();

            // Định dạng ngày và giờ
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

            // Định dạng thời gian theo chuỗi
            return dateFormat.format(datetime);
        }
    }

    private void GetData(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
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
                        String maNv = object.optString("MaNv", "");
                        String hoTen = object.optString("HoTen", "");

                        if (receiverId.equals(maNv) && receiverName.equals(hoTen)) {
                            // Lấy chuỗi Base64 từ JSON
                            String hinhBase64 = object.optString("HinhAnh", "");
                            // Chuyển chuỗi Base64 thành mảng byte
                            byte[] hinhBytes = Base64.decode(hinhBase64, Base64.DEFAULT);

                            // Kiểm tra xem mảng byte có dữ liệu không
                            if (hinhBytes != null && hinhBytes.length > 0) {
                                // Chuyển mảng byte thành Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhBytes, 0, hinhBytes.length);
                                imgHinh.setImageBitmap(bitmap);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
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
        builder.setTitle("Bạn có chắc muốn quay lại màn hình chính không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chuyển về màn hình đăng nhập (DNActivity)
                startActivity(new Intent(ChatActivity.this, MessageActivity.class));
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
        imgBack = findViewById(R.id.imgBack);
        btnSend = findViewById(R.id.btnSend);
        imgHinh = findViewById(R.id.imgHinh);
        tvTen = findViewById(R.id.tvTen);
        recyclerView = findViewById(R.id.recyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        reference = FirebaseDatabase.getInstance().getReference();
    }
}
