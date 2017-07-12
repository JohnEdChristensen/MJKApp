package com.mjkfab.mjkpipespooltracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    String userAccess;
    TextView welcomeTextView;
    TextView userAccessTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        userAccess = pref.getString("ACCESS","NOT_SET");
        if(userAccess.equals("NOT_SET")){
            startActivity(new Intent(this,LoginActivity.class));
        }
        setTitle("Home");
        setContentView(R.layout.activity_home);
        welcomeTextView = (TextView) findViewById(R.id.welcome_text);
        userAccessTextView = (TextView) findViewById(R.id.user_access_textView);

        welcomeTextView.append(" " + pref.getString("USERNAME", "ERROR"));
        userAccessTextView.append(userAccess);
    }


    public void onScanClick(View v) {
        requestCameraPermission();
    }


    public void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            startActivity(new Intent(this, ScanActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(this, ScanActivity.class));

                } else {

                    Toast.makeText(this, "Permission Required to Scan", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //ACTION BAR
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.action_sign_out:
                SharedPreferences pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
                pref.edit().putString("USERNAME", "NOT_SET").apply();
                pref.edit().putString("PASSWORD", "NOT_SET").apply();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}