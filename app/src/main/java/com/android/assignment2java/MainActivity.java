package com.android.assignment2java;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private final static int REQUEST_CODE_QUESTIONS = 0;
    private final static String EXTRA_SCORE = "com.android.assignment2.score";
    private final static String FILENAME = "user_info.txt";

    //widgets
    private Button mDoneButton;
    private Button mQuizButton;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mNickNameView;
    private EditText mAgeView;
    private TextView mScoreView;
    private EditText mReturnedScoreView;

    private int mScore = -1;
    private String mFirstName = "";
    private String mLastName = "";
    private String mNickName = "";
    private int mAge = 0;
    private boolean mQuizTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //wiring up widgets
        mDoneButton = (Button) findViewById(R.id.doneButton);
        mQuizButton = (Button) findViewById(R.id.quizButton);
        mFirstNameView = (EditText) findViewById(R.id.firstNameView);
        mLastNameView = (EditText) findViewById(R.id.lastNameView);
        mNickNameView = (EditText) findViewById(R.id.nickNameView);
        mAgeView = (EditText) findViewById(R.id.ageView);
        mScoreView = (TextView) findViewById(R.id.scoreView);
        mReturnedScoreView = (EditText) findViewById(R.id.returnedscoreView);

        //check if an internal file already exists. If so, retrieve user info from it
        if (fileExist(FILENAME)) {
            Log.d(TAG, "File exists");
            retrieveUserInfo();
            updateUserInfoView();
        }

        //done button should save user info to permanent storage
        mDoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        //Take Quiz button should take user to a new activity
        mQuizButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //make sure user enters all info
                if (userInfoEmpty()) {
                    Toast.makeText(getBaseContext(), R.string.emptyInfo_toast,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_QUESTIONS);
                }
            }
        });
    }

    //this is called after the quiz activity is finished
    //retrieves the score the the quiz result and display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check which Activity its returning from
        if (requestCode == REQUEST_CODE_QUESTIONS) {
            //check if everything went well, and get the score from the quiz result
            if (resultCode == RESULT_OK) {
                mScore = data.getIntExtra(EXTRA_SCORE, 0);
                Log.d(TAG, "onActivityResult called, requestCode = " +requestCode+ ", resultCode " +
                        "= " +resultCode+ ", score = " +mScore);
                //set the score and show it
                mReturnedScoreView.setText(String.valueOf(mScore));
                saveUserInfo();
                showScore();
            }
        }
    }

    //update the View with current data
    private void updateUserInfoView() {
        Log.d(TAG, "updateUserInfoView() called");
        mFirstNameView.setText(mFirstName);
        mLastNameView.setText(mLastName);
        mNickNameView.setText(mNickName);
        mAgeView.setText(String.valueOf(mAge));
    }

    //save entered info into internal storage
    private void saveUserInfo() {
        Log.d(TAG, "saveUserInfo() called");
        mFirstName = mFirstNameView.getText().toString();
        mLastName = mLastNameView.getText().toString();
        mNickName = mNickNameView.getText().toString();
        mAge = parseInt(mAgeView.getText().toString());
        Log.d(TAG,
                "First Name: " + mFirstName + " Last Name: " + mLastName + " Nickname: " + mNickName +
                        " Age: " + mAge);
        try {
            Context context = getApplicationContext();
            FileOutputStream fileOS = context.openFileOutput(FILENAME, MODE_PRIVATE);
            OutputStreamWriter outputSW = new OutputStreamWriter(fileOS);
            BufferedWriter bufferedWriter = new BufferedWriter(outputSW);
            //save the score if a quiz was taken
            if (mScore != -1) {
                bufferedWriter.write("True\n");
                bufferedWriter.write(mScore+"\n");
            }
            bufferedWriter.write(mFirstName+"\n");
            bufferedWriter.write(mLastName+"\n");
            bufferedWriter.write(mNickName+"\n");
            bufferedWriter.write(mAge+"\n");
            bufferedWriter.close();
            Toast.makeText(getBaseContext(), R.string.infoSaved_toast, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //reads data from internal file storage and retrieve it
    private void retrieveUserInfo() {
        Log.d(TAG, "retrieveUserInfo() called");
//        Context context = getApplicationContext();
        try {
            Context context = getApplicationContext();
            FileInputStream fileIS = context.openFileInput(FILENAME);
            InputStreamReader inputSR = new InputStreamReader(fileIS);
            BufferedReader bufferedReader = new BufferedReader(inputSR);
            String firstLine = bufferedReader.readLine();
            //if a quiz was taken, then the next line is the quiz score
            if (firstLine.equals("True")) {
                mScore = parseInt(bufferedReader.readLine());
                mReturnedScoreView.setText(String.valueOf(mScore));
                mFirstName = bufferedReader.readLine();
                showScore();
            }
            //otherwise, the first line is first name
            else {
                mFirstName = firstLine;
            }
            mLastName = bufferedReader.readLine();
            mNickName = bufferedReader.readLine();
            mAge = parseInt(bufferedReader.readLine());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check if an internal file exists already
    private boolean fileExist(String fname) {
        File file = new File(getApplicationContext().getFilesDir(), fname);
        return file.exists();
    }

    private void showScore() {
        mScoreView.setVisibility(View.VISIBLE);
        mReturnedScoreView.setVisibility(View.VISIBLE);
    }

    //check to see if any of the input fields are empty
    private boolean userInfoEmpty() {
        if (mFirstNameView.getText().toString().isEmpty() || mLastNameView.getText().toString().isEmpty()
                || mNickNameView.getText().toString().isEmpty() || mAgeView.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }
}
