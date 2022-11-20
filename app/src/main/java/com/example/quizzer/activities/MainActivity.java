package com.example.quizzer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.quizzer.R;

public class MainActivity extends AppCompatActivity {

    private Button startBtn , bookMarkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        startBtn = findViewById (R.id.start_btn);
        startBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent (MainActivity.this , CategoriesActivity.class);
                startActivity (categoryIntent);
            }
        });
        bookMarkBtn = findViewById (R.id.bookMarks_btn);
        bookMarkBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent bookMarksIntent = new Intent (MainActivity.this , BookMarkActivity.class);
                startActivity (bookMarksIntent);
            }
        });
    }
}