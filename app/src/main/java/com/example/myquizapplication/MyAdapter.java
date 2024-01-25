package com.example.myquizapplication;

import static com.example.myquizapplication.DBstudent.TABLENAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ModelViewHolder> {
    Context context;
    ArrayList<Model>modelArrayList=new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;



    public MyAdapter(Context context, int singledata, ArrayList<Model> modelArrayList, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.modelArrayList = modelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @NonNull
    @Override
    public MyAdapter.ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.singledata, null);
        return new ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ModelViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Model model = modelArrayList.get(position);
        holder.usern.setText("Username: "+ model.getUser());
        holder.passw.setText("Password: "+model.getPass());
        holder.firstn.setText("Firstname: "+model.getFname());
        holder.lastn.setText("Lastname: "+model.getLname());
        holder.ssection.setText("Year & Section: "+model.getYsection());



        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putInt("id",model.getId());
                bundle.putString("user",model.getUser());
                bundle.putString("pass",model.getPass());
                bundle.putString("fname",model.getFname());
                bundle.putString("lname",model.getLname());
                bundle.putString("ysection",model.getYsection());
                Intent intent = new Intent(context,Students.class);
                intent.putExtra("userdata", bundle);
                context.startActivity(intent);

            }
        });
        int id = modelArrayList.get(position).getId();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            DBstudent dBstudent = new DBstudent(context);

            @Override
            public void onClick(View v) {
                sqLiteDatabase = dBstudent.getWritableDatabase();
                String whereClause = "id=?";
                String[] whereArgs = {String.valueOf(id)};

                // Log the values for debugging
                Log.d("DeleteButton", " id " + id);
                Log.d("DeleteButton", "TABLENAME: " + TABLENAME);

                long recdelete = sqLiteDatabase.delete(TABLENAME, whereClause, whereArgs);

                if (recdelete != -1) {
                    Toast.makeText(context, "Deleted Data Successfully", Toast.LENGTH_SHORT).show();
                    modelArrayList.remove(position);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error Deleting Data", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder {
        TextView usern, passw, firstn, lastn, ssection;
        Button edit, delete;
        public ModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usern=(TextView)itemView.findViewById(R.id.usern);
            passw=(TextView)itemView.findViewById(R.id.passw);
            firstn=(TextView)itemView.findViewById(R.id.firstn);
            lastn=(TextView)itemView.findViewById(R.id.lastn);
            ssection=(TextView)itemView.findViewById(R.id.ssection);
            edit = (Button)itemView.findViewById(R.id.edit);
            delete = (Button)itemView.findViewById(R.id.delete);
        }
    }
}