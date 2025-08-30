package org.taxivyapar.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class support extends AppCompatActivity {
    ConstraintLayout whatsapp, gmail, call, tel, social1, social2, social4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        call = findViewById(R.id.call);
        whatsapp = findViewById(R.id.what);
        gmail = findViewById(R.id.emial);
        tel = findViewById(R.id.tel);

        social1 = findViewById(R.id.social1);
        social2 = findViewById(R.id.social2);
        social4 = findViewById(R.id.social4);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                call.startAnimation(animation);
                try {
                    String url = "tel: " + profileContainer.call;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                whatsapp.startAnimation(animation);
                try {
                    String url = "https://api.whatsapp.com/send?phone=" + profileContainer.whatsapp
                            + "&text=" + profileContainer.whatmsg;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                gmail.startAnimation(animation);
                try {
                    String msg = "✋ Hello Taxi Vyapar \uD83C\uDFAE support \nI, " + profileContainer.userName + " have some queries in app.\n\nPlease \uD83D\uDCD9 give guide line for Taxi Vyapar app.";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + profileContainer.mail));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Taxi Vyapar Support");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                tel.startAnimation(animation);
                try {
                    String url = profileContainer.telegram;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });

        social1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                social1.startAnimation(animation);
                try {
                    String url = profileContainer.facebook;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });
        social2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                social2.startAnimation(animation);
                try {
                    String url = profileContainer.instagram;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });
        social4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                social4.startAnimation(animation);
                try {
                    String url = profileContainer.twitter;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });
    }
}