package com.sp.letsvote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class LoginPage extends AppCompatActivity {
    private Button bVoterLogin, bCanLogin, bAdminLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        bVoterLogin = (Button) findViewById(R.id.bVoterLogin);
        bVoterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), VoterLogin.class);
                startActivity(intent);

            }
        });
        bCanLogin = (Button) findViewById(R.id.bCanLogin);
        bCanLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CandidateLogin.class);
                startActivity(intent);
            }
        });
        bAdminLogin = (Button) findViewById(R.id.bAdminLogin);
        bAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AdminLogin.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
