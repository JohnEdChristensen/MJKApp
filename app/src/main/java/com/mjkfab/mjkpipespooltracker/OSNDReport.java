package com.mjkfab.mjkpipespooltracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class OSNDReport extends AppCompatActivity {
    long pipeID;
    CheckBox check;
    EditText report;
    String reportString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent pastIntent = getIntent();
        pipeID = pastIntent.getLongExtra(PipePage.PIPE_ID,-1);

        setContentView(R.layout.activity_osndreport);
        setTitle("OS&D Report");
        check = (CheckBox) findViewById(R.id.check);
        report = (EditText) findViewById(R.id.report);
    }
    public void sendReport(View v){
        if(check.isChecked() && !report.getText().toString().trim().isEmpty()) {
            reportString = report.getText().toString();
            new OSNDReport.sendOSNDReport().execute();
        }else{
            Toast.makeText(this, "Please complete report", Toast.LENGTH_SHORT).show();
        }
    }
    private class sendOSNDReport extends AsyncTask<Void,Void,String> {
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private Socket socket;
        private String serverResponse;
        private String serverAddress;

        @Override
        protected String doInBackground(Void... params) {
            int serverPort = 60101;
            SharedPreferences pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
            serverAddress = pref.getString("SERVER_IP","NOT_SET");
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 1000);
                //Send Server Command
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject("sendReport");
                outputStream.writeLong(pipeID);
                outputStream.writeObject(reportString);
                outputStream.flush();
                //read result
                inputStream = new ObjectInputStream(socket.getInputStream());
                serverResponse = (String) inputStream.readObject();


            } catch (SocketTimeoutException socketTimeout) {
                serverResponse = "time_out";
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                    if (inputStream != null)
                        inputStream.close();
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return serverResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(OSNDReport.this, result, Toast.LENGTH_SHORT).show();
            Intent pipePageIntent = new Intent(OSNDReport.this,PipePage.class);
            pipePageIntent.putExtra(PipePage.PIPE_ID, pipeID);
            startActivity(pipePageIntent);
            super.onPostExecute(result);
        }
    }
    //Action Bar - Back
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent backIntent = new Intent(this,PipePage.class);
                backIntent.putExtra(PipePage.PIPE_ID, pipeID);
                startActivity(backIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
