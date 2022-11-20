package com.example.quizzer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.quizzer.R;
import com.example.quizzer.adapters.CategoryAdapter;
import com.example.quizzer.models.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryModel> list;

    private Dialog loadingDialog;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_categories);

        Toolbar toolbar = findViewById (R.id.CatToolBar);
        setSupportActionBar (toolbar);
        getSupportActionBar ().setTitle ("Categories");
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

        loadingDialog= new Dialog (this);
        loadingDialog.setContentView (R.layout.loading);
        loadingDialog.getWindow ().setBackgroundDrawable (getDrawable (R.drawable.rounded_corners_bg));
        loadingDialog.getWindow ().setLayout (LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable (false);

        recyclerView = findViewById (R.id.rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager (this);
        linearLayoutManager.setOrientation (RecyclerView.VERTICAL);
        recyclerView.setLayoutManager (linearLayoutManager);

        list = new ArrayList<> ();
        adapter = new CategoryAdapter (list);
        recyclerView.setAdapter (adapter);

        loadingDialog.show ();
        myRef.child ("Categories").addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren ()){
                    list.add (dataSnapshot.getValue (CategoryModel.class));
                }
                // for refresh adapter
                adapter.notifyDataSetChanged ();
                loadingDialog.dismiss ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText (CategoriesActivity.this, error.getMessage (), Toast.LENGTH_SHORT).show ();
                loadingDialog.dismiss ();
                finish ();
            }
        });
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