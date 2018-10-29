package com.example.wolf.ha2;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageView imBoxM = null;
    ImageView imObstacle = null;
    ImageView imgWarning = null;
    ImageView imgRotate = null;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private boolean right = true;
    private boolean jump = true;
    private boolean onBox = false;
    private boolean overBox = false;
    private boolean wrongAngle = false;

    float slide = 0.5f;
    float jumpSlide = 0f;
    float angle = 90f;
    float speed = 0.005f;

    TextView txtWarning;

    Handler h = new Handler();
    int delay = 1*10;
    Runnable runnable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> mList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        imgWarning  = findViewById(R.id.imgWarning);
        imgRotate  = findViewById(R.id.imgRotate);
        txtWarning  = findViewById(R.id.txtWarning);

        imBoxM  = findViewById(R.id.imBoxM);
        imObstacle  = findViewById(R.id.imObstacle);

        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){

    }


    @Override
    public final void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y= event.values[1];
        angle = (float) (Math.atan2(x, y)/(Math.PI/180f));
        if(angle<0 || angle>180){
            imgWarning.setVisibility(View.VISIBLE);
            txtWarning.setVisibility(View.VISIBLE);
            imgRotate.setVisibility(View.VISIBLE);
            angle = 90;
            wrongAngle = true;

        }else{
            imgWarning.setVisibility(View.INVISIBLE);
            txtWarning.setVisibility(View.INVISIBLE);
            imgRotate.setVisibility(View.INVISIBLE);
            wrongAngle = false;
        }

    }

    @Override
    protected void onResume() {
        h.postDelayed( runnable = new Runnable() {
            public void run() {
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) imBoxM.getLayoutParams();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                if(
                    (imBoxM.getLeft() - imObstacle.getRight()) <= 0 && //RIGHT 100 - 50
                    (imBoxM.getRight() - imObstacle.getLeft()) >= 0 && //LEFT  50 - 100
                    (imBoxM.getBottom() - imObstacle.getTop()) <= 0
                    ){
                        overBox=true;
                }else{
                    if(overBox || onBox){
                        if (angle >= 90) {
                            right = false;
                        } else if (angle < 90) {
                            right = true;
                        }
                    }
                    overBox=false;
                    onBox=false;
                }

                if(right){
                    if(angle>93){
                        if (((imBoxM.getLeft() - imObstacle.getRight()) >= 0) ||  (jump && overBox)) {
                            slide -= speed;
                        }
                    }else if(angle<87){
                            slide += speed;
                    }
                }else{
                    if(angle>93){
                            slide -= speed;
                    }else if(angle<87){
                        if(((imBoxM.getRight() - imObstacle.getLeft()) <= 0 )||  (jump && overBox)){
                            slide += speed;
                        }
                    }
                }

                if(slide>1)
                    slide = 1;
                if(slide<0)
                    slide = 0;

                if(jump && jumpSlide<1f){
                    if(!((imBoxM.getRight() - imObstacle.getLeft()) < 0 )  && !((imBoxM.getLeft() - imObstacle.getRight()) > 0 ))
                    {
                        onBox = (((imBoxM.getBottom() - imObstacle.getTop()) >= 0) && ((imBoxM.getBottom() - imObstacle.getTop()) <= 6));
                        if(!onBox){
                            jumpSlide += speed * 5;
                        }
                    }else{
                        jumpSlide += speed * 5;
                    }
                }else{
                    jump=false;
                }

                newLayoutParams.verticalBias = jumpSlide;
                newLayoutParams.horizontalBias = slide;
                imBoxM.setLayoutParams(newLayoutParams);

                Log.e("Data","jump:"+jump+" onBox:"+onBox+" overBox:"+overBox+" right:"+right +" 1:"+(imBoxM.getRight() - imObstacle.getLeft())+" 2:"+(imBoxM.getLeft() - imObstacle.getRight())
                        +" 3:"+(imBoxM.getBottom() - imObstacle.getTop())+" 4:"+imBoxM.getBottom() +" 5:"+(imObstacle.getTop())
                + " 6:");

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        h.removeCallbacks(runnable);
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void clickOnScreen(View view) {
        if((!jump || onBox) && !wrongAngle) {
            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) imBoxM.getLayoutParams();
            jumpSlide=0;
            newLayoutParams.verticalBias = jumpSlide;
            imBoxM.setLayoutParams(newLayoutParams);
            jump=true;
        }
    }
}
