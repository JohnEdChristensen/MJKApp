package com.mjkfab.mjkpipespooltracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class StartActivity extends AppCompatActivity {
    TextView connectingTextView;
    Button retryButton;
    private static final String publicServerAddress ="72.250.220.135";
    private static final String localServerAddress = "192.168.10.113";
    private static final int versionCode = BuildConfig.VERSION_CODE;
    private SharedPreferences pref;
    private boolean externalServerFail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        pref.edit().putString("SERVER_IP",publicServerAddress ).apply();
        new serverCheck().execute();
        connectingTextView = (TextView) findViewById(R.id.connecting_TextView);
        connectingTextView.setText(" Connecting to server...");
        retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setVisibility(View.INVISIBLE);

    }
    public void serverSuccess(){
        String sPCredentials = pref.getString("USERNAME","NOT_SET");
        if(sPCredentials.equals("NOT_SET")){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }else{
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
    public void serverFailed(){
        if(!externalServerFail){
            pref.edit().putString("SERVER_IP",localServerAddress ).apply();
            new serverCheck().execute();
        }else{
            connectingTextView.setText("Server Connection Failed");
            retryButton.setVisibility(View.VISIBLE);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        }
        externalServerFail = true;
    }
    public void updateRequired(){
        connectingTextView.setText("Update Required");
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.update_required_dialog, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
    public void retry(View v){
        recreate();
    }

    private class serverCheck extends AsyncTask<Void,Void,String> {
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private Socket socket;
        private String serverResponse = "";
        private String serverAddress;

        @Override
        protected String doInBackground(Void... params) {
            int serverPort = 60101;
            try {
                serverAddress = pref.getString("SERVER_IP", "localHost" );
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 1000);
                //Send Server Command
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject("Test");
                outputStream.writeInt(versionCode);
                outputStream.flush();

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
            switch (result) {
                case "Up To Date":
                    serverSuccess();
                    break;
                case "Not Up To Date":
                    updateRequired();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    break;
                case "time_out":
                    serverFailed();
                    break;
                default:
                    Toast.makeText(StartActivity.this, "Fatal Error.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }
            super.onPostExecute(result);
        }
    }
}
