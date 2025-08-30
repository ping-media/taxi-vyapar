package org.taxivyapar.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class imageview extends AppCompatActivity {
    View back;
    ImageView image;
    TextView avatar;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX = 0;
    private float mPosY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags = getWindow().getDecorView().getSystemUiVisibility();
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().getDecorView().setSystemUiVisibility(flags);
            }
        }

        back = findViewById(R.id.back);

        image = findViewById(R.id.image);
        avatar = findViewById(R.id.avatar);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


        if (profileContainer.imageviewuserurl != null) {
            if (!profileContainer.imageviewuserurl.equals("")) {
                avatar.setText("");
                try {
                    Glide.with(imageview.this).load(profileContainer.imageviewuserurl).into(image);
                } catch (Exception e) {
                }
            }else {
                try {
                    avatar.setText(profileContainer.imageviewuser.substring(0, 1).toUpperCase());
                } catch (Exception e) {
                    avatar.setText("A");
                }
            }
        } else {
            try {
                avatar.setText(profileContainer.imageviewuser.substring(0, 1).toUpperCase());
            } catch (Exception e) {
                avatar.setText("A");
            }
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageview.super.onBackPressed();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;
                mLastTouchY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastTouchX;
                float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

                image.setTranslationX(mPosX);
                image.setTranslationY(mPosY);

                mLastTouchX = x;
                mLastTouchY = y;
                break;
        }
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