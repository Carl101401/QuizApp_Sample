package com.example.myquizapplication.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.myquizapplication.QuestionActivity;
import com.example.myquizapplication.QuestionActivity2;
import com.example.myquizapplication.R;


public class GrideAdapter2 extends BaseAdapter {

    public int sets = 0;
    private String category;

    public GrideAdapter2(int sets, String category) {
        this.sets = sets;
        this.category = category;
    }

    @Override
    public int getCount() {
        return sets+1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View view1;
        if (view==null){
            view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sets2,viewGroup,false);

        }else{
            view1 = view;
        }
        if (i==0){


            ((CardView)view1.findViewById(R.id.setCard)).setVisibility(View.GONE);

        }
        else {
            ((TextView)view1.findViewById(R.id.setName)).setText(String.valueOf(i));

        }

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    Intent intent = new Intent(viewGroup.getContext(), QuestionActivity2.class);
                    intent.putExtra("setNum",i);
                    intent.putExtra("categoryName",category);
                    viewGroup.getContext().startActivity(intent);



            }
        });

        return view1;
    }



}
