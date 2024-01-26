package com.example.qunlnhns.ql;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.qunlnhns.MainActivity;
import com.example.qunlnhns.R;
import com.example.qunlnhns.user.DNActivity;

public class Notification {
    private static final String CHANNEL_ID = "Thông báo!";
    private static final String CHANNEL_NAME = "Nội dung thông báo!";

    // Thay đổi NOTIFICATION_ID thành một giá trị duy nhất cho mỗi thông báo
    private static int notificationIdCounter = 1;

    public static void showNotification(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Kiểm tra phiên bản Android để xác định xem cần tạo NotificationChannel hay không
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        boolean userLoggedIn = checkUserLoggedInStatus(context);  // Hãy thay đổi hàm này theo cách bạn lưu trạng thái đăng nhập

        // Nếu người dùng đã đăng nhập, tạo PendingIntent để mở MainActivity
        // Nếu chưa đăng nhập, có thể chuyển hướng đến màn hình đăng nhập hoặc thực hiện các hành động khác
        Intent intent;
        if (userLoggedIn) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            // Thực hiện các hành động khác tùy thuộc vào yêu cầu của ứng dụng
            // Ví dụ: Chuyển hướng đến màn hình đăng nhập
            intent = new Intent(context, DNActivity.class);
        }

        // Tăng NOTIFICATION_ID cho mỗi thông báo
        int notificationId = notificationIdCounter++;

        // Tạo PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Xây dựng thông báo với PendingIntent và NOTIFICATION_ID duy nhất
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo với NOTIFICATION_ID duy nhất
        notificationManager.notify(notificationId, builder.build());
    }

    public static boolean checkUserLoggedInStatus(Context context) {
        // Sử dụng SharedPreferences để kiểm tra trạng thái đăng nhập
        SharedPreferences sharedPreferences = context.getSharedPreferences("QLNS", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
