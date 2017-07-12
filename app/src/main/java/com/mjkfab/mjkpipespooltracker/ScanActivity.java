package com.mjkfab.mjkpipespooltracker;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
    private void goHome(){
        startActivity(new Intent(this, HomeActivity.class));
        Toast.makeText(this, "Invalid Barcode: Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
    @Override
    public void handleResult(Result result) {
        long pipeID = -1;
        Intent pipePageIntent = new Intent(this,PipePage.class);
        String resultInfo = result.getText();

        try {
            pipeID = Long.parseLong(resultInfo);
        }catch(NumberFormatException e){
            goHome();
        }
        pipePageIntent.putExtra(PipePage.PIPE_ID,pipeID);
        startActivity(pipePageIntent);

    }
}
