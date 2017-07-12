package com.mjkfab.mjkpipespooltracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final String SEARCH_MODE = "com.mjkfab.mjkpipespooltracker.SEARCH_MODE";
    public static final String SEARCH_TEXT = "com.mjkfab.mjkpipespooltracker.SEARCH_TEXT";
    Spinner spinner;
    EditText searchText;
    String searchMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        spinner = (Spinner) findViewById(R.id.spinner);
        searchText = (EditText) findViewById(R.id.searchEditText);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.searchParameters,android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }
    public void searchClick(){
        if(!searchText.getText().toString().isEmpty()){
            Intent searchResultIntent = new Intent(this,ViewDrawing.class);
            searchResultIntent.putExtra(SEARCH_MODE, searchMode);
            searchResultIntent.putExtra(SEARCH_TEXT, searchText.getText().toString());
            startActivity(searchResultIntent);
        }else{
            Toast.makeText(this, "Please Enter a Search Term", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView label = (TextView) view;
        searchMode = label.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
