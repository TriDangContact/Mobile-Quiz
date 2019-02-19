package com.android.assignment2java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.QuestionFragmentDataPasser {

    private final static String EXTRA_SCORE = "com.android.assignment2.score";
    private final static String TAG = "QuestionActivity";

    private int mCurrentScore = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //display the question fragment if it's not the last question yet
        addNewFragment();
    }

    //this is called when the QuestionFragment is done. Retrieves the data from the fragment and
    // remove the fragment from the stack. Then finish the activity and send the data back to
    // original MainActivity
    @Override
    public void onQuestionFragmentData(int score) {
        mCurrentScore = score;
        //once a score is retrieved from fragment, pop the fragment
        getSupportFragmentManager().popBackStack();
        setScoreResult(mCurrentScore);
        Log.d(TAG, "Retrieved score from fragment: " +score);
        finish();
    }

    //save the current score and pass it back to the main activity
    private void setScoreResult(int score) {
        Intent intent = new Intent(this, QuestionsActivity.class);
        intent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, intent);
    }

    //add a new Fragment onto the stack
    private void addNewFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new QuestionFragment();
        fm.beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit();
    }

}
