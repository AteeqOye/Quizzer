package com.example.quizzer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.quizzer.R;
import com.example.quizzer.adapters.GridAdapter;

public class SetsActivity extends AppCompatActivity {

    private GridView gridView;
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sets);

        Toolbar toolbar = findViewById (R.id.SetToolBar);
        setSupportActionBar (toolbar);
        getSupportActionBar ().setTitle (getIntent ().getStringExtra ("title"));
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

        gridView = findViewById (R.id.gridView);

        adapter = new GridAdapter (getIntent ().getIntExtra ("sets" , 0) , getIntent ().getStringExtra ("title"));
        gridView.setAdapter (adapter);
    }

    //for back btn
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId () == android.R.id.home)
        {
            finish ();
        }

        return super.onOptionsItemSelected (item);
    }
}