package com.example.wolf.homework1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnToC;
    Button btnToF;
    EditText nrInput;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        btnToC = findViewById(R.id.btnToC);
        btnToF = findViewById(R.id.btnToF);
        nrInput = findViewById(R.id.nrInput);
        txtResult = findViewById(R.id.txtResult);

        btnToC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = nrInput.getText().toString();
                if (!value.matches("")) {
                    Log.i("value", value);
                    double fahrenheit = Double.parseDouble(value);
                    Log.i("fahrenheit", "fahrenheit " + fahrenheit);
                    double celsius = (fahrenheit - 32) * 5 / 9;
                    txtResult.setText("Result:\n" + String.format("%.2f", celsius) + "°C");
                } else {
                    txtResult.setText("Result:");
                }
            }
        });

        btnToF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = nrInput.getText().toString();

                if (!value.matches("")) {
                    Log.i("value",value);
                    double celsius = Double.parseDouble(value);
                    Log.i("celsius","celsius " + celsius);
                    double fahrenheit = celsius * 9/5 + 32;
                    txtResult.setText("Result:\n" + String.format("%.2f", fahrenheit) + "°F" );
                } else {
                    txtResult.setText("Result:");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause","onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onResume");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop","onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","onDestroy");

    }
}
