package com.example.qunlnhns.nv.message;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunlnhns.R;

import java.util.List;

public class msgAdapter extends RecyclerView.Adapter<msgAdapter.ViewHolder> {
    private List<msg> messageList;
    private Context context;
    private ItemClickListener itemClickListener;

    public msgAdapter(List<msg> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, String id, String ten);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public msgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull msgAdapter.ViewHolder holder, int position) {
        msg message = messageList.get(position);

        // Hiển thị thông tin của tin nhắn trong ViewHolder
        holder.tvName.setText(message.getName());
        holder.tvMessageLast.setText(message.getLastMessage());
        holder.tvTimeLast.setText(message.getLastMessageTime());
        holder.tvId.setText(message.getId());
        // Nếu có hình ảnh, hiển thị nó
        if (message.getHinh() != null && message.getHinh().length > 0) {
            holder.imgAnh.setImageBitmap(BitmapFactory.decodeByteArray(message.getHinh(), 0, message.getHinh().length));
        } else {
            // Nếu không có hình ảnh, bạn có thể ẩn ImageView hoặc hiển thị một hình ảnh mặc định
            holder.imgAnh.setImageResource(R.drawable.person);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    // Use getAdapterPosition() instead of storing the position
                    int adapterPosition = holder.getAdapterPosition();

                    // Check if the position is valid
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        String ID = messageList.get(adapterPosition).getId();
                        String Ten = messageList.get(adapterPosition).getName();
                        itemClickListener.onItemClick(v, adapterPosition, ID, Ten);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh;
        TextView tvName, tvId, tvMessageLast, tvTimeLast;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnh = itemView.findViewById(R.id.imgAnh);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessageLast = itemView.findViewById(R.id.tvMessageLast);
            tvTimeLast = itemView.findViewById(R.id.tvTimeLast);
            tvId = itemView.findViewById(R.id.tvId);
        }
    }
}
