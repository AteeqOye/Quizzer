package com.example.quizzer.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quizzer.R;
import com.example.quizzer.activities.QuestionActivity;

public class GridAdapter extends BaseAdapter {

    private int sets = 0;
    private String category;

    public GridAdapter(int sets , String category) {
        this.category = category;
        this.sets = sets;
    }

    @Override
    public int getCount() {
        return sets;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View ConvertView, ViewGroup parent) {

        View view;
        if(ConvertView == null)
        {
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.set_item , parent , false);
        }
        else {
            view = ConvertView;
        }

        view.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent questionIntent = new Intent (parent.getContext () , QuestionActivity.class);
                questionIntent.putExtra ("category" , category);
                questionIntent.putExtra ("setNo" , position+1);
                parent.getContext ().startActivity (questionIntent);
            }
        });

        ((TextView)view.findViewById (R.id.setTextView)).setText (String.valueOf (position+1));

        return view;
    }
}
