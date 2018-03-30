package com.sp.letsvote;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sp.letsvote.Data.CandidateData;
import com.sp.letsvote.Data.CandidateDatabaseHelper;
import com.sp.letsvote.Data.ResultData;
import com.sp.letsvote.Data.ResultDatabaseHelper;

public class AdminProfile extends AppCompatActivity {

    private Button bViewVoter;
    private Button bViewCan;
    private Button bDeclareResult;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        getSupportActionBar().setTitle("Administrator Profile");
        context = getBaseContext();
        init();

        bViewCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ViewCandidates.class);
                intent.putExtra("buttonDisabled", true);
                startActivity(intent);
            }
        });
        bViewVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ViewVoters.class);
                intent.putExtra("buttonDisabled", true);
                startActivity(intent);
            }
        });

        bDeclareResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CandidateData candidateData = new CandidateData();
                CandidateDatabaseHelper db = new CandidateDatabaseHelper(context);
                try {
                    db.open();
                    candidateData = db.getWinnerCandidate();
                    db.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (candidateData.getNoOfVotes() != 0) {
                    Toast.makeText(getBaseContext(), candidateData.getName() + " has won the election!!!", Toast.LENGTH_LONG).show();
                    ResultData resultData = new ResultData();
                    resultData.setCandidateData(candidateData);
                    resultData.setDeclared(true);
                    resultData.setNoOfVotes(candidateData.getNoOfVotes());
                    switch (candidateData.getName()) {
                        case "R Sheela":
                            resultData.setCandidateImage(R.drawable.can1);
                            resultData.setWinningCandidate("R Sheela");
                            break;
                        case "Dr. V.S. Vijay":
                            resultData.setCandidateImage(R.drawable.can2);
                            resultData.setWinningCandidate("Dr. V.S. Vijay");
                            break;
                        case "A. Aslam Basha":
                            resultData.setCandidateImage(R.drawable.can3);
                            resultData.setWinningCandidate("A. Aslam Basha");
                            break;
                        case "Duraimurgan":
                            resultData.setCandidateImage(R.drawable.can4);
                            resultData.setWinningCandidate("Duraimurgan");
                            break;
                    }
                    ResultDatabaseHelper rdb = new ResultDatabaseHelper(context);
                    try {
                        rdb.open();
                        rdb.deleteData("winner");
                        rdb.putData(resultData);
                        rdb.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getBaseContext(), Result.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "Results cannot be calculated right now due to lack of Majority!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void init() {
        bViewVoter = (Button) findViewById(R.id.bViewVoter);
        bViewCan = (Button) findViewById(R.id.bViewCan);
        bDeclareResult = (Button) findViewById(R.id.bDeclareResult);
    }
}
