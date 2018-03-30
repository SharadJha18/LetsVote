package com.sp.letsvote;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sp.letsvote.Data.VoterData;
import com.sp.letsvote.Data.VoterDatabaseHelper;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class VoterSignUp extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private EditText etVoterName;
    private EditText etVoterDob;
    private EditText etVoterAdd;
    private EditText etVoterMob;
    private EditText etVoterPass;
    private EditText etVoterConPass;
    private Button bSubmit;
    private LinearLayout datePickerContainer;
    private DatePicker datePicker;
    private ImageButton bDatePicker;
    private String date;
    private Button bOK;
    private TextInputLayout voterPassLabel;
    private TextInputLayout voterNameLabel;
    private TextInputLayout voterIdLabel;
    private TextInputLayout voterDobLabel;
    private TextInputLayout voterMobLabel;
    private TextInputLayout voterConPassLabel;
    private TextView tvGenderLabel;
    private int centerX;
    private int centerY;
    private TextInputLayout voterAddLabel;
    private String gender;
    private EditText etVoterId;
    private RadioGroup rgGender;
    private Context context;
    private Toast toast;
    private boolean isEditing;
    private String voterId;
    private String voterIdToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            //getWindow().setAllowEnterTransitionOverlap(false);
//            getWindow().setAllowReturnTransitionOverlap(false);
//            Explode explode = new Explode();
//            explode.setDuration(1000);
//            getWindow().setEnterTransition(explode);
//            explode.setDuration(1000);
//            getWindow().setReturnTransition(explode);
//        }
        setContentView(R.layout.activity_voter_signup);
        init();

        if (ContextCompat.checkSelfPermission(VoterSignUp.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            bSubmit.setEnabled(false);
            ActivityCompat.requestPermissions(VoterSignUp.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);

        } else {
            fetchLocation();
        }
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        voterId = getIntent().getStringExtra("voterId");
        if (isEditing) {
            voterIdToEdit = getIntent().getStringExtra("voterId");
            VoterDatabaseHelper db = new VoterDatabaseHelper(context);
            VoterData voterData = new VoterData();
            try {
                db.open();
                voterData = db.getData(voterIdToEdit);
                db.close();
            } catch (SQLException e) {
                Snackbar.make(getCurrentFocus(), e.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
            etVoterName.setText(voterData.getName());
            etVoterId.setText(voterData.getVoterId());
            etVoterDob.setText(voterData.getDob());
            etVoterMob.setText(voterData.getMobile());
            etVoterAdd.setText(voterData.getAddress());
            etVoterPass.setText(voterData.getPassword());
            etVoterConPass.setText(voterData.getPassword());
        }


        bDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                makeCircularRevealAppear();
            }
        });

        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = "";
                date = getStrMonth(datePicker.getMonth()+1) + " " +
                        datePicker.getDayOfMonth() + ", " +
                        datePicker.getYear();
                makeCircularRevealDisappear();
                etVoterDob.setText(date);
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                VoterData voterData = new VoterData();
                putDataInVoter(voterData);
                if (isDataValid(voterData)) {
                    VoterDatabaseHelper db = new VoterDatabaseHelper(context);
                    try {
                        db.open();
                        db.putData(voterData);
//                        if (isEditing) {
//                            db.deletePrevious(previousUsername);
//                            db.editData(voterData);
//                        } else {
//                            db.putData(voterData);
//                        }
                        db.close();
                    } catch (SQLException e) {
                        Snackbar.make(getCurrentFocus(), e.getMessage(),
                                Snackbar.LENGTH_LONG).show();
                    }
                    Toast.makeText(getBaseContext(), "Sign Up Successful!!!\nYou would be now" +
                            " redirected to the Login Page", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), LoginPage.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(getCurrentFocus(), "Please fill the details properly",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_LONG);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                toast.cancel();
                toast = Toast.makeText(getBaseContext(), radioButton.getText() + " selected", Toast.LENGTH_LONG);
                gender = radioButton.getText().toString();
                toast.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchLocation();

                } else {
                    Toast.makeText(getApplicationContext(), "Registration cannot be done without location access", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void fetchLocation() {
        GPSTracker gps = new GPSTracker(VoterSignUp.this);
        if (gps.canGetLocation()) {
            String output = "";
            double lat = gps.getLatitude();
            double lon = gps.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String location = "";
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                Address obj = addresses.get(0);//.getAddressLine(0);
                location = obj.getLocality();

                etVoterAdd.setText(location);
                etVoterAdd.setEnabled(false);
                Snackbar.make(findViewById(R.id.bSubmit),
                        "Location has been auto-fetched and cannot be altered",
                        Snackbar.LENGTH_LONG);
                bSubmit.setEnabled(true);
//                startActivity(intent);
//                finish();
            } catch (IOException | IndexOutOfBoundsException e) {
                e.printStackTrace();

            }
        }
    }

    private void putDataInVoter(VoterData voterData) {
        voterData.setName(etVoterName.getText().toString().trim());
        voterData.setVoterId(etVoterId.getText().toString().trim());
        voterData.setAddress(etVoterAdd.getText().toString());
        voterData.setDob(etVoterDob.getText().toString().trim());
        voterData.setGender(gender);
        voterData.setMobile(etVoterMob.getText().toString().trim());
        voterData.setPassword(etVoterPass.getText().toString());
    }

    public boolean isDataValid(VoterData voterData) {

        boolean flag = true;

        voterNameLabel.setErrorEnabled(false);
        voterIdLabel.setErrorEnabled(false);
        voterDobLabel.setErrorEnabled(false);
        voterAddLabel.setErrorEnabled(false);
        voterMobLabel.setErrorEnabled(false);
        voterPassLabel.setErrorEnabled(false);
        voterConPassLabel.setErrorEnabled(false);


        if (!(voterData.getName().length() != 0)) {
            voterNameLabel.setError("Field can't be empty");
            flag = false;
        }
        if (!(voterData.getVoterId().length() != 0)) {
            voterIdLabel.setError("Field can't be empty");
            flag = false;
        } else if (isEditing == false && (!checkUsernameWithDatabase(voterData.getVoterId()))) {
            voterIdLabel.setError("Username already taken");
            flag = false;
        }
        if (!(voterData.getAddress().length() != 0)) {
            voterAddLabel.setError("Field can't be empty");
            flag = false;
        }
        if (!(voterData.getDob().length() != 0)) {
            voterDobLabel.setError("Field can't be empty");
            flag = false;
        }
        if (!(voterData.getMobile().length() != 0)) {
            voterMobLabel.setError("Field can't be empty");
            flag = false;
        } else if (!(voterData.getMobile().matches("[0-9]+") &&
                voterData.getMobile().length() == 10)) {
            voterMobLabel.setError("Please enter a valid Phone No");
            flag = false;
        }
        if (!(voterData.getPassword().length() != 0)) {
            voterPassLabel.setError("Field can't be empty");
            flag = false;
        }
        if (!(etVoterConPass.getText().toString().length() != 0)) {
            voterConPassLabel.setError("Field can't be empty");
            flag = false;
        } else if (!(voterData.getPassword().equals(etVoterConPass.getText().toString()))) {
            this.etVoterConPass.setText("");
            voterConPassLabel.setError("Passwords do not match");
            flag = false;
        }
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getBaseContext(), "Please select your Gender", Toast.LENGTH_LONG).show();
        }
        return flag;
    }

    private boolean checkUsernameWithDatabase(String voterId) {
        VoterDatabaseHelper db = new VoterDatabaseHelper(context);
        String[] voterIds = new String[100];
        try {
            db.open();
            voterIds = db.retrieveVoterIds();
            db.close();
        } catch (SQLException e) {
            Snackbar.make(getCurrentFocus(), e.getMessage(),
                    Snackbar.LENGTH_LONG).show();
        }
        for (int i = 0; i < voterIds.length; i++) {
            if (voterId.equals(voterIds[i])) {
                return false;
            }
        }
        return true;
    }

    private String getStrMonth(int mth) {
        String month = "";
        switch (mth) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        return month;
    }

    private void makeCircularRevealAppear() {
        Log.e("APPEAR", "APPEAR");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        float radius = Math.max(width, height) * 2.0f;

        if (datePickerContainer.getVisibility() == View.GONE) {
            datePickerContainer.setVisibility(View.VISIBLE);
            ViewAnimationUtils.createCircularReveal(datePickerContainer, centerX, centerY, 0, radius)
                    .setDuration(1000).start();

        }
    }

    private void makeCircularRevealDisappear() {
        Log.e("DISAPPEAR", "DISAPPEAR");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        float radius = Math.max(width, height) * 2.0f;

        if (datePickerContainer.getVisibility() == View.VISIBLE) {
            Animator reverse = ViewAnimationUtils.createCircularReveal(datePickerContainer, centerX, centerY,
                    radius, 0).setDuration(1000);
            reverse.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    datePickerContainer.setVisibility(View.GONE);
                }
            });
            reverse.start();
        }
    }
    private void init() {
        context = getBaseContext();
        isEditing = false;
        voterNameLabel = (TextInputLayout) findViewById(R.id.voterNameLabel);
        voterIdLabel = (TextInputLayout) findViewById(R.id.voterIdLabel);
        voterDobLabel = (TextInputLayout) findViewById(R.id.voterDobLabel) ;
        voterAddLabel = (TextInputLayout) findViewById(R.id.voterAddLabel);
        voterMobLabel = (TextInputLayout) findViewById(R.id.votermobLabel);
        voterPassLabel = (TextInputLayout) findViewById(R.id.passwordLabel);
        voterConPassLabel = (TextInputLayout) findViewById(R.id.conpassLabel );
        
        etVoterName = (EditText) findViewById(R.id.etVoterName);
        etVoterId = (EditText) findViewById(R.id.etVoterId);
        etVoterDob = (EditText) findViewById(R.id.etVoterDob);
        etVoterAdd = (EditText) findViewById(R.id.etVoterAdd);
        etVoterMob = (EditText) findViewById(R.id.etVoterMob);
        etVoterPass = (EditText) findViewById(R.id.etVoterPass);
        etVoterConPass = (EditText) findViewById(R.id.etVoterConPass);

        rgGender = (RadioGroup) findViewById(R.id.rgGender);

        bDatePicker = (ImageButton) findViewById(R.id.bDatePicker);
        datePickerContainer = (LinearLayout) findViewById(R.id.datePickerContainer);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        bOK = (Button) findViewById(R.id.bSubmit);
        View view = bDatePicker.getRootView();
        centerX = (view.getLeft() + view.getRight()) / 2;
        centerY = (view.getTop() + view.getBottom()) / 2;
        datePickerContainer.setVisibility(View.GONE);
    }
}
