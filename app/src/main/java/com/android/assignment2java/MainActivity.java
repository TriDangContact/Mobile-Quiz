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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        if (fileExist(FILENAME)) {
            Log.d(TAG, "File exists");
            try {
                retrieveUserInfo();
                updateUserInfoView();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "File doesn't exists");

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
                Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QUESTIONS);
            }
        });
        Log.d(TAG, "Current Score: " +mScore);
    }


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
                if (mScore != -1) {
                    mReturnedScoreView.setText(String.valueOf(mScore));
                    showScore();
                }
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
            FileOutputStream fileOS = openFileOutput(FILENAME, MODE_PRIVATE);
            OutputStreamWriter outputSW = new OutputStreamWriter(fileOS);
            outputSW.write(mFirstName+"\n");
            outputSW.write(mLastName+"\n");
            outputSW.write(mNickName+"\n");
            outputSW.write(mAge+"\n");
            fileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //reads data from internal file storage and retrieve it
    private void retrieveUserInfo() throws FileNotFoundException {
        Log.d(TAG, "retrieveUserInfo() called");
        Context context = getApplicationContext();
        FileInputStream fileIS = context.openFileInput(FILENAME);
        InputStreamReader inputSR = new InputStreamReader(fileIS);
        BufferedReader bufferedReader = new BufferedReader(inputSR);
        try {
            mFirstName = bufferedReader.readLine();
            mLastName = bufferedReader.readLine();
            mNickName = bufferedReader.readLine();
            mAge = parseInt(bufferedReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //check if an internal file exists already
    private boolean fileExist(String fname) {
//        File file = getBaseContext().getFileStreamPath(fname);
        File file = new File(getApplicationContext().getFilesDir(), fname);
        return file.exists();
    }

    private void showScore() {
        mScoreView.setVisibility(View.VISIBLE);
        mReturnedScoreView.setVisibility(View.VISIBLE);
    }


}
