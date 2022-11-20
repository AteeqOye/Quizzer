package com.example.quizzer.activities;

import static com.example.quizzer.activities.QuestionActivity.FILE_NAME;
import static com.example.quizzer.activities.QuestionActivity.KEY_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.quizzer.R;
import com.example.quizzer.adapters.BookMarkAdapter;
import com.example.quizzer.models.QuestionModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookMarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookMarkAdapter bookMarkAdapter;


    private List<QuestionModel> bookMarkList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_book_mark);

        Toolbar toolbar = findViewById (R.id.bookMarkToolBar);
        setSupportActionBar (toolbar);
        getSupportActionBar ().setTitle ("BookMarks");
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);


        preferences = getSharedPreferences (FILE_NAME , Context.MODE_PRIVATE);
        editor = preferences.edit ();
        gson = new Gson ();

        //method calling
        getBookMarks ();

        recyclerView = findViewById (R.id.bookMarkRv);

        LinearLayoutManager layoutManager = new LinearLayoutManager (this);
        layoutManager.setOrientation (RecyclerView.VERTICAL);

        recyclerView.setLayoutManager (layoutManager);

        bookMarkAdapter = new BookMarkAdapter (bookMarkList);

        recyclerView.setAdapter (bookMarkAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause ();
        //method calling
        storeBookMarks ();
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

    private void getBookMarks()
    {
        String json =  preferences.getString (KEY_NAME , "");

        Type type = new TypeToken<List<QuestionModel>> (){}.getType ();

        bookMarkList = gson.fromJson (json , type);
        if(bookMarkList == null)
        {
            bookMarkList = new ArrayList<> ();
        }
    }

    private void storeBookMarks()
    {
        String json = gson.toJson (bookMarkList);
        editor.putString ( KEY_NAME , json);
        editor.commit ();
    }
}