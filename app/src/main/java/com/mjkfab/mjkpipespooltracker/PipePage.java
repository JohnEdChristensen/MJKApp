package com.mjkfab.mjkpipespooltracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class PipePage extends AppCompatActivity {
    public static final String PIPE_ID = "com.mjkfab.mjkpipespooltracker.PIPE_ID";
    Long pipeID;
    String[] parameterLabel = Pipe.getParameterLabel();
    String[] parameterData;
    int userAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe_page);
        Intent getScanResultIntent = getIntent();
        pipeID = getScanResultIntent.getLongExtra(PIPE_ID,-1);
        SharedPreferences pref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
        userAccess = Integer.parseInt(pref.getString("ACCESS","NOT_SET"));
        setTitle(pipeID.toString());
        //get pipe data
        GetPipeData getPipeData = new GetPipeData();
        getPipeData.execute();


    }
    private void createList(){
        ListAdapter pipeListAdapter = new PipeListAdapter(this,parameterLabel,parameterData);
        ListView pipeListView = (ListView) findViewById(R.id.pipe_Page_ListView);
        pipeListView.setAdapter(pipeListAdapter);
    }

    //Handle Back Press
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }


    //SEND DATA TO SERVER
     private class GetPipeData extends AsyncTask<Void,Void,String> {
        private String serverCommand = "getPipeData";
        private String statusMessage;
        private String serverData = "null";

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
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 1000);
                //Send Server Command
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(serverCommand);
                //Send PipeID
                outputStream.writeLong(pipeID);
                outputStream.flush();
                // Read Pipe Data
                inputStream = new ObjectInputStream(socket.getInputStream());
                serverData =(String) inputStream.readObject();

            }catch(SocketTimeoutException socketTimeout){
               serverData = "time_out";
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
            return serverData;
        }
        @Override
        protected void onPostExecute(String result) {

            if(result.equals("time_out")){
                goStart();
            }
            else if(result.equals("Pipe Not Found")){
                Toast.makeText(PipePage.this, "ERROR: PIPE NOT FOUND", Toast.LENGTH_LONG).show();
                goHome();
            }else{
                Pipe scannedPipe = new Pipe(result);
                parameterData = scannedPipe.getParameterData();
                createList();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

    }
    //ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pipe_page,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            case R.id.action_osndreport:
                if(userAccess >=3) {
                    Intent osndIntent = new Intent(this, OSNDReport.class);
                    osndIntent.putExtra(PIPE_ID, pipeID);
                    startActivity(osndIntent);
                }else
                    Toast.makeText(this, "Please See Supervisor", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_view_drawing:
                Intent viewDrawingIntent = new Intent(this,ViewDrawing.class);
                viewDrawingIntent.putExtra(PIPE_ID, pipeID);
                startActivity(viewDrawingIntent);
                return true;
            case R.id.action_Add_Location:
                Intent scanIntent = new Intent(this,AddLocationActivity.class);
                scanIntent.putExtra(PIPE_ID, pipeID);
                startActivity(scanIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void goStart(){
        Toast.makeText(PipePage.this, "Failed to connect to Server.\n Reconnecting...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, StartActivity.class));
    }
    private void goHome(){
        startActivity(new Intent(this, HomeActivity.class));
    }

}
