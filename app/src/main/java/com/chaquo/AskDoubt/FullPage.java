package com.chaquo.AskDoubt;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class FullPage extends AppCompatActivity {
    private ImageView image;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_page);
        image=findViewById(R.id.imageV);
        Intent intent=getIntent();
        String im=intent.getStringExtra("image");
        Glide.with(FullPage.this).load(im).into(image);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            image.setScaleX(mScaleFactor);
            image.setScaleY(mScaleFactor);
            return true;
        }
    }

}