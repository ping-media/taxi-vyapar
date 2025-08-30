package org.taxivyapar.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

public class chat extends AppCompatActivity {
    ConstraintLayout home, booking, addpost, chat, profile;
    CardView addbooking, addvehicle;
    View closeaddpost;
    BottomSheetDialog addpostPopup;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        home = findViewById(R.id.home);
        booking = findViewById(R.id.booking);
        addpost = findViewById(R.id.addpost);
        chat = findViewById(R.id.chat);
        profile = findViewById(R.id.profile);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.pageviewer);

        addpostPopup = new BottomSheetDialog(this);
        addpostPopup.setContentView(R.layout.ui_addpost);
        addbooking = addpostPopup.findViewById(R.id.addbooking);
        addvehicle = addpostPopup.findViewById(R.id.addvehicle);
        closeaddpost = addpostPopup.findViewById(R.id.viewmanu);

        tabLayout.setupWithViewPager(viewPager);

        VPadaptor vPadaptor = new VPadaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(vPadaptor);

        chat1Fragment fragment_1 = new chat1Fragment();
        vPadaptor.addfram(fragment_1, "Chat Posted");

        chat2Fragment fragment_2 = new chat2Fragment();
        vPadaptor.addfram(fragment_2, "Chat Received");

        vPadaptor.notifyDataSetChanged();

        closeaddpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                closeaddpost.startAnimation(animation);
                addpostPopup.dismiss();
            }
        });
        addbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addbooking.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), postbooking.class));
            }
        });
        addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addvehicle.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), postvehicle.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), mybooking.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });
        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addpost.startAnimation(animation);
                addpostPopup.show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), profile.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    public void support(View view) {
        startActivity(new Intent(getApplicationContext(), support.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }
}