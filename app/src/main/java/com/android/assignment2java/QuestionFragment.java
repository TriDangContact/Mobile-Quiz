package com.android.assignment2java;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class QuestionFragment extends Fragment {

    private final static String TAG = "QuestionFragment";
    private final static String EXTRA_SCORE = "com.android.assignment2.score";

    private TextView questionView;
    private RadioGroup radioGroup;
    private RadioButton radio_q1;
    private RadioButton radio_q2;
    private RadioButton radio_q3;
    private RadioButton radio_q4;
    private Button nextButton;

    //interface that allows fragment to pass back data to hosting activity
    QuestionFragmentDataPasser mQuestionFragmentData;

    private String mQuestion = "";
    private String mOption1 = "";
    private String mOption2 = "";
    private String mOption3 = "";
    private String mOption4 = "";
    private String mAnswer = "";
    private int mCurrentScore = 0;
    private int mCurrentQuestion = 0;
    private String mSelectedAnswer = "";

    //array that stores all the questions
    private Questions[] mQuestionArray = new Questions[] {
            new Questions(R.string.question1, R.string.answer1b, R.string.answer1a, R.string.answer1b
                    , R.string.answer1c, R.string.answer1d),
            new Questions(R.string.question2, R.string.answer2d, R.string.answer2a,
                    R.string.answer2b, R.string.answer2c, R.string.answer2d),
            new Questions(R.string.question3, R.string.answer3a, R.string.answer3a,
                    R.string.answer3b, R.string.answer3c, R.string.answer3d),
            new Questions(R.string.question4, R.string.answer4c, R.string.answer4a,
                    R.string.answer4b, R.string.answer4c, R.string.answer4d)
    };


    //interface that allows Fragment to pass data back to the host activity
    //can be modified to pass back any type of data
    //call passDataToActivity() to pass data
    public interface QuestionFragmentDataPasser {
        public void onQuestionFragmentData(int score);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_questions, container, false);

        //wiring up various widgets
        questionView = (TextView) v.findViewById(R.id.questionView);
        radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        radio_q1 = (RadioButton) v.findViewById(R.id.radio_q1);
        radio_q2 = (RadioButton) v.findViewById(R.id.radio_q2);
        radio_q3 = (RadioButton) v.findViewById(R.id.radio_q3);
        radio_q4 = (RadioButton) v.findViewById(R.id.radio_q4);
        nextButton = (Button) v.findViewById(R.id.nextButton);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //check to make sure that the QuestionDataPasser interface was implemented in hosting
        // activity in order to enable data passing from fragment to activity
        try {
            mQuestionFragmentData = (QuestionFragmentDataPasser)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " +
                    "QuestionFragmentDataPasser");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showQuestion();
        showOptions();
        hideNextButton();

        //when next button is clicked, check if selected answer is correct. If it is the last
        // question, then pass the score back to Activity and remove fragment. Else, show next
        // question and hide the button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCorrect(mSelectedAnswer)) {
                    mCurrentScore++;
                    Log.d(TAG, "Current Score: " +mCurrentScore);
                }
                //if it is the last question, finish the fragment
                if (mCurrentQuestion == 3) {
                    //save the score and send it back to the hosting activity
                    passDataToActivity(mCurrentScore);
                    removeFragment();
                }
                else {
                    mCurrentQuestion++;
                    showQuestion();
                    showOptions();
                    hideNextButton();
                }
            }
        });

        //when a radio button is selected, get the value of that radio button
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showNextButton();
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    mSelectedAnswer = checkedRadioButton.getText().toString();
                    Log.d(TAG, "Selected Answer: " +mSelectedAnswer);
                }
            }
        });

    }

    private void showQuestion() {
        mQuestion = getString(mQuestionArray[mCurrentQuestion].getTextRestID());
        questionView.setText(mQuestion);
    }

    private void showOptions() {
        mOption1 = getString(mQuestionArray[mCurrentQuestion].getOption1());
        mOption2 = getString(mQuestionArray[mCurrentQuestion].getOption2());
        mOption3 = getString(mQuestionArray[mCurrentQuestion].getOption3());
        mOption4 = getString(mQuestionArray[mCurrentQuestion].getOption4());

        radio_q1.setText(mOption1);
        radio_q2.setText(mOption2);
        radio_q3.setText(mOption3);
        radio_q4.setText(mOption4);
    }

    private boolean isCorrect(String selectedAnswer) {
        mAnswer = getString(mQuestionArray[mCurrentQuestion].getAnswer());
        Log.d(TAG, "Selected Answer: " +selectedAnswer+ " Correct Answer: " +mAnswer);
        return mAnswer == selectedAnswer;
    }

    private void hideNextButton() {
        nextButton.setVisibility(View.INVISIBLE);
    }

    private void showNextButton() {
        nextButton.setVisibility(View.VISIBLE);
    }

    //method that uses the QuestionFragmentDataPasser interface to pass Fragment data back to
    // Activity
    public void passDataToActivity(int score) {
        if (mQuestionFragmentData != null) {
            Log.d(TAG, "Passing score to activity: " +score);
            mQuestionFragmentData.onQuestionFragmentData(score);

        }
    }

    //have the fragment remove itself from the fragmentmanager stack and Back stack
    private void removeFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().remove(this).commit();
        fm.popBackStack();
    }

}
