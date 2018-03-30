package com.sp.letsvote;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sp.letsvote.Data.ResultData;
import com.sp.letsvote.Data.ResultDatabaseHelper;

public class Result extends AppCompatActivity {

    private ImageView candidateImage;
    private TextView tvName, tvNoOfVotes, noResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        init();
        noResult.setVisibility(View.VISIBLE);

        ResultData resultData = new ResultData();
        ResultDatabaseHelper db = new ResultDatabaseHelper(getBaseContext());
        try {
            db.open();
            resultData = db.getData("winner");
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (resultData.isDeclared()) {
            noResult.setVisibility(View.GONE);
            candidateImage.setImageDrawable(getResources().getDrawable(resultData.getCandidateImage()));
            tvName.setText(resultData.getWinningCandidate());
            tvNoOfVotes.setText("" + resultData.getNoOfVotes());
        }
    }

    private void init() {
        candidateImage = (ImageView) findViewById(R.id.candidateImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvNoOfVotes = (TextView) findViewById(R.id.tvNoOfVotes);
        noResult = (TextView) findViewById(R.id.noResult);
    }
}
