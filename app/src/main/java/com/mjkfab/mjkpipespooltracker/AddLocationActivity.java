package com.mjkfab.mjkpipespooltracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddLocationActivity extends AppCompatActivity {
    public static final String PIPE_LOCATION = "com.mjkfab.mjkpipespooltracker.PIPE_LOCATION";
    Long pipeID;
    String location;
    String user;
    String loadListNum;
    String trailerNum;
    int userAccess;
    boolean unloadDrawingConfirm = false;
    boolean paintDrawingConfirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        Intent pastIntent = getIntent();
        pipeID = pastIntent.getLongExtra(PipePage.PIPE_ID,-1);
        String location = pastIntent.getStringExtra(PIPE_LOCATION);
        setTitle(pipeID.toString());
        SharedPreferences pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        userAccess = Integer.parseInt(pref.getString("ACCESS","NOT_SET"));
        user = pref.getString("USERNAME","NOT_SET");
        if(location != null) {
            if (location.equals("Unload")) {
                unloadDrawingConfirm = pastIntent.getBooleanExtra(ConfirmDrawing.DRAWING_CONFIRMED, false);
                View v = null;
                unloadClick(v);
            } else if (location.equals("Touch Up")) {
                paintDrawingConfirm = pastIntent.getBooleanExtra(ConfirmDrawing.DRAWING_CONFIRMED, false);
                View v = null;
                touchUpClick(v);
            }
        }

    }
    public void unloadClick(View v){
        if(userAccess>=2) {
            // Alert Dialog
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.unload_alert_dialog, null);
            final CheckBox check1 = (CheckBox) mView.findViewById(R.id.checkBox);
            final CheckBox check2 = (CheckBox) mView.findViewById(R.id.checkBox2);
            Button button = (Button) mView.findViewById(R.id.accept_button);
            check1.setEnabled(false);
            check1.setChecked(unloadDrawingConfirm);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (check1.isChecked() && check2.isChecked()) {
                        dialog.cancel();
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                        location = "Unload";
                        new UpdateLocation().execute();
                    } else {
                        Toast.makeText(AddLocationActivity.this, "Failed - Consult Supervisor", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
            });
        }else{
            Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_SHORT).show();
        }

    }
    public void blastClick(View v){
        if(userAccess>=2) {
            location = "Blast";
            new UpdateLocation().execute();
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_SHORT).show();
        }
    }
    public void paintClick(View v){
        if(userAccess>=3) {
            // Alert Dialog
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.blast_qc_alert_dialog, null);
            final CheckBox check1 = (CheckBox) mView.findViewById(R.id.checkBox);
            Button button = (Button) mView.findViewById(R.id.accept_button);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (check1.isChecked()) {
                        dialog.cancel();
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                        location = "Paint";
                        new UpdateLocation().execute();
                    } else {
                        Toast.makeText(AddLocationActivity.this, "Failed - Pipe Must Meet Spec", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Supervisor Required", Toast.LENGTH_SHORT).show();
        }
    }
    public void touchUpClick(View v){
        if(userAccess>=3) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.paint_report_alert_dialog, null);
            final CheckBox check1 = (CheckBox) mView.findViewById(R.id.checkBox);
            final CheckBox check2 = (CheckBox) mView.findViewById(R.id.checkBox2);
            final CheckBox check3 = (CheckBox) mView.findViewById(R.id.checkBox3);
            final CheckBox check4 = (CheckBox) mView.findViewById(R.id.checkBox4);
            Button button = (Button) mView.findViewById(R.id.accept_button);
            check1.setEnabled(false);
            check1.setChecked(paintDrawingConfirm);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (check1.isChecked() && check2.isChecked() && check3.isChecked() && check4.isChecked()) {
                        dialog.cancel();
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                        location = "Touch Up";
                        new UpdateLocation().execute();
                    } else {
                        Toast.makeText(AddLocationActivity.this, "Failed - All QC Steps Must Be Completed", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Supervisor Required", Toast.LENGTH_SHORT).show();
        }
    }
    public void loadClick(View v){
        if(userAccess>=2) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.load_alert_dialog, null);
            final CheckBox check1 = (CheckBox) mView.findViewById(R.id.checkBox);
            final CheckBox check2 = (CheckBox) mView.findViewById(R.id.checkBox2);
            final EditText loadNumEditText = (EditText) mView.findViewById(R.id.load_num_editText);
            final EditText trailerNumEditText = (EditText) mView.findViewById(R.id.trailer_num_editText);
            Button button = (Button) mView.findViewById(R.id.accept_button);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ugly.
                    if ((!loadNumEditText.getText().toString().trim().isEmpty()) && (!trailerNumEditText.getText().toString().trim().isEmpty())) {
                        dialog.cancel();
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                        location = "Load";
                        loadListNum = loadNumEditText.getText().toString();
                        trailerNum = trailerNumEditText.getText().toString();
                        new UpdateLocation().execute();
                        if(check2.isChecked()) {
                            finalizeLoad(loadListNum, trailerNum);
                        }
                    } else {
                        Toast.makeText(AddLocationActivity.this, "Failed - Please fill out all fields", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
            });
        }else{
            Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_SHORT).show();
        }
    }
    public void unloadConfirmDrawing(View v){
        Intent confirmDrawingIntent = new Intent(this,ConfirmDrawing.class);
        confirmDrawingIntent.putExtra(PipePage.PIPE_ID, pipeID);
        confirmDrawingIntent.putExtra(PIPE_LOCATION, "Unload");
        startActivity(confirmDrawingIntent);
    }
    public void paintConfirmDrawing(View v){
        Intent confirmDrawingIntent = new Intent(this,ConfirmDrawing.class);
        confirmDrawingIntent.putExtra(PipePage.PIPE_ID, pipeID);
        confirmDrawingIntent.putExtra(PIPE_LOCATION, "Touch Up");
        startActivity(confirmDrawingIntent);
    }
    public void finalizeLoad(String loadListNum, String trailerNum){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"aaron@mjkfab.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Load List " + loadListNum);
        i.putExtra(Intent.EXTRA_TEXT   , "Load List " + loadListNum + " on trailer " + trailerNum +
        " finished being loaded at " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime())
        + " by " + user);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goStart(){
        Toast.makeText(AddLocationActivity.this, "Failed to connect to Server.\n Reconnecting...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, StartActivity.class));
    }

    private class UpdateLocation extends AsyncTask<Void,Void,String> {
        private String serverCommand = "updateLocation";
        private String statusMessage;
        private String serverResponse;

        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private Socket socket;

        @Override
        protected String doInBackground(Void... params) {
            SharedPreferences pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
            String serverAddress = pref.getString("SERVER_IP","NOT_SET");
            int serverPort = 60101;
            try{
                //connection to server
                statusMessage += "Attempting to connect... \n";
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 1000);
                statusMessage += "Connected to: " + socket.getInetAddress().getHostName();
                //Send Server Command
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(serverCommand);
                //Send PipeID
                outputStream.writeLong(pipeID);
                //Send Location
                outputStream.writeObject(location);
                //Send Uer
                outputStream.writeObject(user);
                if(location.equals("Load")){
                    outputStream.writeObject(loadListNum);
                    outputStream.writeObject(trailerNum);
                }
                outputStream.flush();
                // Read Pipe Data
                inputStream = new ObjectInputStream(socket.getInputStream());
                serverResponse =(String) inputStream.readObject();
                statusMessage += "\n" + serverResponse;
            }catch(SocketTimeoutException socketTimeout){
                statusMessage += "\n Server connection timed out";
                return "time_out";
            }
            catch(EOFException eofException){
                statusMessage +="\nError Connecting: E";
                eofException.printStackTrace();
            }catch(IOException ioException){
                statusMessage +="\nError Connecting: I";
                ioException.printStackTrace();
            }catch(ClassNotFoundException classNotFoundException){
                statusMessage += "\nUnable to understand message from server";
            }finally{
                try {
                    statusMessage += "\nClosing connection";
                    if(socket!=null)
                        socket.close();
                    if(inputStream!=null)
                        inputStream.close();
                    if(outputStream!=null)
                        outputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return serverResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("time_out")){
                goStart();
            }
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            Toast.makeText(AddLocationActivity.this, result, Toast.LENGTH_SHORT).show();
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
                Intent pipePageIntent = new Intent(this,PipePage.class);
                pipePageIntent.putExtra(PipePage.PIPE_ID, pipeID);
                startActivity(pipePageIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

