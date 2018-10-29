package com.example.wolf.ha1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

public class ContactDetails extends AppCompatActivity {
    TextView txtName ;
    TextView txtEmail;
    TextView txtPhone;
    long _id;
    String current_title="";
    String current_message="";
    EditText txtEditMailTitle;
    EditText txtEditMailMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);

        _id = getIntent().getLongExtra("_id", 0);

        txtName.setText(getIntent().getStringExtra("txtName"));
        txtEmail.setText(getIntent().getStringExtra("txtEmail"));
        txtPhone.setText(getIntent().getStringExtra("txtPhone"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("state","back");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.e("Contacts","onPause: ");
        getState();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e("Contacts","onDestroy: ");
        saveState();
        super.onDestroy();
    }

    public void saveState() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();


        txtEditMailTitle = findViewById(R.id.txtEditMailTitle);
        txtEditMailMessage = findViewById(R.id.txtEditMailMessage);

        String title = txtEditMailTitle.getText().toString();
        if (!title.isEmpty() || !title.equals(""))
            editor.putString("title", title);

        String message = txtEditMailMessage.getText().toString();
        if (!message.isEmpty() || !message.equals(""))
            editor.putString("message", message);

        editor.commit();
    }

    public void getState() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (prefs.contains("title")){
            String title = prefs.getString("title","");
            Log.e("Contacts","title + " + title);
            Log.e("Contacts","title + " + prefs.contains("title"));
            current_title=title;
        }
        if (prefs.contains("message")){
            String message = prefs.getString("message","");
            Log.e("Contacts","search + " + message);
            Log.e("Contacts","search + " + prefs.contains("message"));
            current_message=message;
        }

        editor.clear();
        editor.apply();
    }

    public void sendEmail(View view) {
        txtEditMailTitle = findViewById(R.id.txtEditMailTitle);
        txtEditMailMessage = findViewById(R.id.txtEditMailMessage);

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{getIntent().getStringExtra("txtEmail")});
        email.putExtra(Intent.EXTRA_SUBJECT, txtEditMailTitle.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, txtEditMailMessage.getText().toString());
        email.setType("message/rfc822");
        //startActivity(Intent.createChooser(email, "Choose an Email client :"));
        startActivityForResult(Intent.createChooser(email, "Choose an Email client :"),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("state","email sent");
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }
}
