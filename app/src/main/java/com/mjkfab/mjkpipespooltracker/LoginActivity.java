package com.mjkfab.mjkpipespooltracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    String username, password,userAccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        usernameEditText = (EditText) findViewById(R.id.login_username_editText);
        passwordEditText = (EditText) findViewById(R.id.login_password_editText);
    }

    public void credentialsCheck(View view) {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        new ValidateCredentials().execute();
    }

    private void login() {
        SharedPreferences pref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
        pref.edit().putString("USERNAME",username).apply();
        pref.edit().putString("PASSWORD",password).apply();
        pref.edit().putString("ACCESS",userAccess).apply();
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
    private  void loginFailed(){
        Toast.makeText(this,"Invalid Username or Password\nTry Again...",
                Toast.LENGTH_LONG).show();
    }
    private void goStart(){
        Toast.makeText(LoginActivity.this, "Failed to connect to Server.\n Reconnecting...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, StartActivity.class));
    }
    //SEND DATA TO SERVER
    private class ValidateCredentials extends AsyncTask<Void,Void,String> {
        //***
        private String serverCommand = "validateCredentials";
        //***
        private String statusMessage;
        boolean validCredentials = false;

        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private Socket socket;

        @Override
        protected String doInBackground(Void... params) {
            SharedPreferences pref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
            String serverAddress = pref.getString("SERVER_IP","NOT_SET");
            int serverPort = 60101;
            String serverData;
            try{
                //connection to server
                statusMessage += "Attempting to connect... \n";
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 1000);
                statusMessage += "Connected to: " + socket.getInetAddress().getHostName();
                //Send Server Command
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(serverCommand);
                //Send Credentials
                outputStream.writeObject(username);
                outputStream.writeObject(password);
                outputStream.flush();
                // Read validation
                inputStream = new ObjectInputStream(socket.getInputStream());
                serverData = (String) inputStream.readObject();
                serverCommand = serverData;
                statusMessage += "\n Credentials =" + serverData;
            }catch(SocketTimeoutException socketTimeout){
                statusMessage += "\n Server connection timed out";
                serverCommand = "time_out";
            }
            catch(EOFException eofException){
                statusMessage +="\nError Connecting: E";
                eofException.printStackTrace();
            }catch(IOException ioException) {
                statusMessage += "\nError Connecting: I";
                ioException.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally{
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
            return serverCommand;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("time_out")){
                goStart();
            }else if(result.equals("false")) {
                loginFailed();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            } else {
                userAccess = result;
                login();
            }
            super.onPostExecute(result);
        }

    }
}
