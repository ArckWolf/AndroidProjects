package com.example.wolf.hw2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class DetailsActivity extends AppCompatActivity {

    EditText editName;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        editName = findViewById(R.id.txtEditName);
        spinner = findViewById(R.id.spinner);
    }

    public void clickSave(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("name",editName.getText().toString());
        returnIntent.putExtra("position",spinner.getSelectedItem().toString());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
