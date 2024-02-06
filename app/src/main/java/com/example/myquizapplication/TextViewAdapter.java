package com.example.myquizapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TextViewAdapter extends RecyclerView.Adapter<TextViewAdapter.TextViewHolder> {

    private final Context context;
    private final List<String> textList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public TextViewAdapter(Context context, List<String> textList) {
        this.context = context;
        this.textList = textList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        String text = textList.get(position);
        holder.textViewText.setText(text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    mListener.onClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewText;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewText = itemView.findViewById(R.id.textViewTextItem);
        }
    }
}
