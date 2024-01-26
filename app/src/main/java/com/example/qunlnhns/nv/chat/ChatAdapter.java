package com.example.qunlnhns.nv.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunlnhns.DatabaseSQlite;
import com.example.qunlnhns.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT = 0;
    private Context context;
    ArrayList<Chat> chatList;

    DatabaseSQlite databaseSQlite;

    public ChatAdapter(Context context, ArrayList<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        // Hiển thị dữ liệu trong ViewHolder
        holder.tvMessage.setText(chat.getMessage());
        holder.tvTime.setText(chat.getTime());

        // Xử lý sự kiện khi ấn vào tvMessage
        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTimeVisibility(holder.tvTime);
            }
        });
    }

    private void toggleTimeVisibility(TextView tvTime) {
        if (tvTime.getVisibility() == View.VISIBLE) {
            tvTime.setVisibility(View.GONE); // Ẩn tvTime nếu đang hiện
        } else {
            tvTime.setVisibility(View.VISIBLE); // Hiện tvTime nếu đang ẩn
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        LinearLayout lnrMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            lnrMessage = itemView.findViewById(R.id.lnrMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {

        // Khởi tạo đối tượng Database
        databaseSQlite = new DatabaseSQlite(context.getApplicationContext(), "main.sqlite", null, 1);
        Pair<String, String> result = databaseSQlite.SELECT_MANV_MAIN();
        String manv = result.first;

        Chat chat = chatList.get(position);

        if (chat != null && chat.getSender() != null && chat.getSender().equals(manv)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
