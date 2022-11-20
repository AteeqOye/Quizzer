package com.example.quizzer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizzer.R;
import com.example.quizzer.models.QuestionModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QUIZZER";
    public static final String KEY_NAME = "QUESTION";

    private TextView question , noIndicator;
    private FloatingActionButton bookMarkBtn;
    private LinearLayout optionsContainer;
    private Button shareBtn , nextBtn;

    private int count = 0;
    private int position = 0;
    private int score = 0;
    private String category;
    private int setNo ;

    private List<QuestionModel> bookMarkList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuestionPosition;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog;

    List<QuestionModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_question);

//        Toolbar toolbar = findViewById (R.id.questionToolBar);
//        setSupportActionBar (toolbar);

        question = findViewById (R.id.question);
        noIndicator = findViewById (R.id.no_indicator);
        bookMarkBtn = findViewById (R.id.bookMarkBtn);
        optionsContainer = findViewById (R.id.options_containers);
        shareBtn = findViewById (R.id.shareBtn);
        nextBtn = findViewById (R.id.nextBtn);

        preferences = getSharedPreferences (FILE_NAME , Context.MODE_PRIVATE);
        editor = preferences.edit ();
        gson = new Gson ();

        //method calling
        getBookMarks ();

        bookMarkBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                //method calling
                if(modelMatch ())
                {
                    bookMarkList.remove (matchedQuestionPosition);
                    bookMarkBtn.setImageDrawable (getDrawable (R.drawable.bookmark));
                }
                else
                {
                    bookMarkList.add (list.get (position));
                    bookMarkBtn.setImageDrawable (getDrawable (R.drawable.filledbookmark));
                }
            }
        });

        category = getIntent ().getStringExtra ("category" );
        setNo = getIntent ().getIntExtra ("setNo" ,1 );


        loadingDialog= new Dialog (this);
        loadingDialog.setContentView (R.layout.loading);
        loadingDialog.getWindow ().setBackgroundDrawable (getDrawable (R.drawable.rounded_corners_bg));
        loadingDialog.getWindow ().setLayout (LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable (false);


        list = new ArrayList<> ();

        loadingDialog.show ();
        myRef.child ("SETS").child (category).child ("questions").orderByChild ("setNo").equalTo (setNo).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren ())
                {
                    list.add (dataSnapshot.getValue (QuestionModel.class));
                }
                if(list.size () > 0)
                {
                    for(int i = 0 ; i < 4 ; i++)
                    {
                        optionsContainer.getChildAt (i).setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                checkANS (((Button)view));
                            }
                        });
                    }
                    playAnim (question , 0 , list.get (position).getQuestion ());

                    //Next Btn
                    nextBtn.setOnClickListener (new View.OnClickListener () {
                        @Override
                        public void onClick(View view) {
                            nextBtn.setEnabled (false);
                            nextBtn.setAlpha (0.7f);
                            enableOption (true);
                            position++;
                            if(position == list.size ())
                            {
                                //score activity
                                Intent scoreIntent = new Intent (QuestionActivity.this , ScoreActivity.class);
                                scoreIntent.putExtra ("score" , score);
                                scoreIntent.putExtra ("total" , list.size ());
                                startActivity (scoreIntent);
                                finish ();
                                return;
                            }
                            count = 0;
                            playAnim (question , 0 , list.get (position).getQuestion ());
                        }
                    });

                    //Share Btn
                    shareBtn.setOnClickListener (new View.OnClickListener () {
                        @Override
                        public void onClick(View view) {
                            String body = list.get (position).getQuestion() + "\n" +
                                          list.get (position).getOptionA () + "\n" +
                                          list.get (position).getOptionB () + "\n" +
                                          list.get (position).getOptionC () + "\n" +
                                          list.get (position).getOptionD ();
                            Intent shareIntent = new Intent (Intent.ACTION_SEND);
                            shareIntent.setType ("plain/text");
                            shareIntent.putExtra (Intent.EXTRA_SUBJECT , "Quizzer challenge");
                            shareIntent.putExtra (Intent.EXTRA_TEXT , body);
                            startActivity (Intent.createChooser (shareIntent , "Share via"));
                        }
                    });
                }
                else
                {
                    finish ();
                    Toast.makeText (QuestionActivity.this, "no questions", Toast.LENGTH_SHORT).show ();
                }
                loadingDialog.dismiss ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText (QuestionActivity.this, error.getMessage (), Toast.LENGTH_SHORT).show ();
                loadingDialog.dismiss ();
                finish ();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause ();
        //method calling
        storeBookMarks ();
    }

    //method for animation
    private void playAnim(View view , int value , final String data)
    {
        view.animate ().alpha (value).scaleX (value).scaleY (value).setDuration (500).setStartDelay (100)
                .setInterpolator (new DecelerateInterpolator ()).setListener (new Animator.AnimatorListener () {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        if(value == 0 && count < 4){

                            String option = "";

                            // question 1
                            if(count == 0)
                            {
                                option = list.get (position).getOptionA ();
                            }
                            // question 2
                            else if(count == 1)
                            {
                                option = list.get (position).getOptionB ();
                            }
                            // question 3
                            else if(count == 2)
                            {
                                option = list.get (position).getOptionC ();
                            }
                            // question 4
                            else if(count == 3)
                            {
                                option = list.get (position).getOptionD ();
                            }
                            playAnim (optionsContainer.getChildAt (count) , 0 , option);
                            count++;
                        }

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        // data change
                        if(value == 0){
                            try {

                                ((TextView)view).setText (data);
                                noIndicator.setText (position+1+"/"+list.size ());
                                if(modelMatch ())
                                {
                                    bookMarkBtn.setImageDrawable (getDrawable (R.drawable.filledbookmark));
                                }
                                else
                                {
                                    bookMarkBtn.setImageDrawable (getDrawable (R.drawable.bookmark));
                                }

                            }catch (ClassCastException ex)
                            {
                                ((Button)view).setText (data);
                            }
                            view.setTag (data);
                            playAnim (view , 1 , data);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }
    private void checkANS(Button selectedOption)
    {
        enableOption (false);
        nextBtn.setEnabled (true);
        nextBtn.setAlpha (1);
        if(selectedOption.getText ().toString ().equals (list.get (position).getCorrectANS ()))
        {
            //correct
            score ++;
            selectedOption.setBackgroundTintList (ColorStateList.valueOf (Color.parseColor ("#4CAF50")));
        }
        else
        {
            //incorrect
            selectedOption.setBackgroundTintList (ColorStateList.valueOf (Color.parseColor ("#FF0000")));
            Button correctOption = (Button) optionsContainer.findViewWithTag (list.get (position).getCorrectANS ());
            correctOption.setBackgroundTintList (ColorStateList.valueOf (Color.parseColor ("#4CAF50")));

        }


    }

    private void enableOption(boolean enable )
    {
        for(int i = 0 ; i < 4 ; i++)
        {
            optionsContainer.getChildAt (i).setEnabled (enable);
            if(enable)
            {
                optionsContainer.getChildAt (i).setBackgroundTintList (ColorStateList.valueOf (Color.parseColor ("#989898")));
            }
        }
    }

    private void getBookMarks()
    {
       String json =  preferences.getString (KEY_NAME , "");

       Type type = new TypeToken<List<QuestionModel>>(){}.getType ();

       bookMarkList = gson.fromJson (json , type);
       if(bookMarkList == null)
       {
           bookMarkList = new ArrayList<> ();
       }
    }

    private boolean modelMatch()
    {
        boolean matched = false;
        int i = 0;
        for(QuestionModel model : bookMarkList)
        {
            if(model.getQuestion ().equals (list.get (position).getQuestion ())
            && model.getCorrectANS ().equals (list.get (position).getCorrectANS ())
            && model.getSetNo () == list.get (position).getSetNo ()){
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }
        return matched;
    }

    private void storeBookMarks()
    {
        String json = gson.toJson (bookMarkList);
        editor.putString ( KEY_NAME , json);
        editor.commit ();
    }
}