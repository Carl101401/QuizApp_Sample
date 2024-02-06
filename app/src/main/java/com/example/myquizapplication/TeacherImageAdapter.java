package com.example.myquizapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TeacherImageAdapter extends RecyclerView.Adapter<TeacherImageAdapter.ViewHolder> {
    Context context;
    ArrayList<TeacherImage> arrayList;
    OnItemClickListener onItemClickListener;

    public TeacherImageAdapter(Context context, ArrayList<TeacherImage> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    public interface OnItemLongClickListener {
        void onLongClick(TeacherImage teacherImage);
    }

    private OnItemLongClickListener longClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(arrayList.get(position).getUrl()).into(holder.imageView);
        holder.title.setText(arrayList.get(position).getTitle());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));

        holder.itemView.setOnClickListener(view -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                onItemClickListener.onClick(arrayList.get(clickedPosition));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int longClickedPosition = holder.getAdapterPosition();
                if (longClickedPosition != RecyclerView.NO_POSITION && longClickListener != null) {
                    longClickListener.onLongClick(arrayList.get(longClickedPosition));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ListItemImage); // Update to the correct ImageView ID
            title = itemView.findViewById(R.id.ListImageTitle); // Update to the correct TextView ID
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(TeacherImage teacherImage);
    }
}
