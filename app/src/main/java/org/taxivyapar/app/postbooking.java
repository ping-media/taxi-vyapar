package org.taxivyapar.app;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class postbooking extends AppCompatActivity {
    View back;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postbooking);

        back = findViewById(R.id.view);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.pageviewer);

        tabLayout.setupWithViewPager(viewPager);

        VPadaptor vPadaptor = new VPadaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(vPadaptor);

        postbook1Fragment fragment_1 = new postbook1Fragment();
        vPadaptor.addfram(fragment_1, "One Way");

        postbook2Fragment fragment_2 = new postbook2Fragment();
        vPadaptor.addfram(fragment_2, "Round Trip");

        vPadaptor.notifyDataSetChanged();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                postbooking.super.onBackPressed();
            }
        });
    }
}