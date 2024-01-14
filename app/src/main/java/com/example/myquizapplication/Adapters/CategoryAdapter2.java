package com.example.myquizapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myquizapplication.Models.CategoryModel2;
import com.example.myquizapplication.R;
import com.example.myquizapplication.SetsActivity;
import com.example.myquizapplication.SetsActivity2;
import com.example.myquizapplication.databinding.ItemCategory2Binding;
import com.example.myquizapplication.databinding.ItemCategoryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.viewHolder>{

    Context context;
    ArrayList<CategoryModel2>list;

    public CategoryAdapter2(Context context, ArrayList<CategoryModel2> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category2,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        CategoryModel2 model = list.get(position);

        holder.binding.categoryName.setText(model.getCategoryName());

        Picasso.get()
                .load(model.getCategoryImage())
                .placeholder(R.drawable.quiz2)
                .into(holder.binding.categoryImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SetsActivity2.class);
                intent.putExtra("category",model.getCategoryName());
                intent.putExtra("sets",model.getSetNum());
                intent.putExtra("key",model.getKey());

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ItemCategory2Binding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemCategory2Binding.bind(itemView);
        }
    }
}
