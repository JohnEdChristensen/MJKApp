package com.mjkfab.mjkpipespooltracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SearchResultActivity extends AppCompatActivity {
    String searchMode;
    String searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent searchIntent = getIntent();
        searchMode = searchIntent.getStringExtra(SearchActivity.SEARCH_MODE);
        searchText = searchIntent.getStringExtra(SearchActivity.SEARCH_TEXT);
    }
    private class searchQuery extends AsyncTask<Void,Void,String> {
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
                outputStream.writeObject("searchQuery");
                outputStream.writeObject(searchMode);
                outputStream.writeObject(searchText);
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

        }
    }
}
