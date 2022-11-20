package com.example.quizzer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzer.R;
import com.example.quizzer.models.QuestionModel;

import java.util.List;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {

    private List<QuestionModel> list;

    public BookMarkAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.bookmark_item , parent , false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData (list.get (position).getQuestion () , list.get (position).getCorrectANS () , position);

    }

    @Override
    public int getItemCount() {
        return list.size ();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView questionTxt , answerTxt;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            questionTxt = itemView.findViewById (R.id.questionText);
            answerTxt = itemView.findViewById (R.id.answerText);
            deleteBtn = itemView.findViewById (R.id.deleteBtn);
        }
        private void setData(String questionTxt , String answerTxt , int position)
        {
            this.questionTxt.setText (questionTxt);
            this.answerTxt.setText (answerTxt);

            deleteBtn.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    list.remove (position);
                    notifyItemRemoved (position);
                }
            });
        }
    }

}
