package org.taxivyapar.app;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OSInAppMessage;
import com.onesignal.OSInAppMessageAction;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class home extends AppCompatActivity {
    ConstraintLayout home, booking, addpost, chat, profile;
    CardView addbooking, addvehicle, notification;
    TextView balance;
    View closeaddpost;
    Dialog loading;
    BottomSheetDialog addpostPopup;
    TabLayout tabLayout;
    ViewPager viewPager;
    DecimalFormat df = new DecimalFormat("0.00");
    private home1Fragment fragment_1;
    private home2Fragment fragment_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            if (profileContainer.playReview.equals("inreview")) {
                if (!profileContainer.updateVersion.equals("")) {
                    if (profileContainer.updateVersion.equals(String.valueOf(BuildConfig.VERSION_CODE))) {

                    } else if (profileContainer.appVersion.equals(String.valueOf(BuildConfig.VERSION_CODE))) {

                    } else {
                        startActivity(new Intent(getApplicationContext(), newupdate.class));
                        finishAffinity();
                    }
                }
            } else {
                if (!profileContainer.appVersion.equals("")) {
                    if (!profileContainer.appVersion.equals(String.valueOf(BuildConfig.VERSION_CODE))) {
                        startActivity(new Intent(getApplicationContext(), newupdate.class));
                        finishAffinity();
                    }
                }
            }
        } catch (Exception e) {
            startActivity(new Intent(getApplicationContext(), newupdate.class));
            finishAffinity();
        }

        balance = findViewById(R.id.balance);

        home = findViewById(R.id.home);
        booking = findViewById(R.id.booking);
        addpost = findViewById(R.id.addpost);
        chat = findViewById(R.id.chat);
        profile = findViewById(R.id.profile);

        notification = findViewById(R.id.notification);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.pageviewer);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        addpostPopup = new BottomSheetDialog(this);
        addpostPopup.setContentView(R.layout.ui_addpost);
        addbooking = addpostPopup.findViewById(R.id.addbooking);
        addvehicle = addpostPopup.findViewById(R.id.addvehicle);
        closeaddpost = addpostPopup.findViewById(R.id.viewmanu);

        OneSignal.setExternalUserId(profileContainer.userMobileNo.replace("+91", ""));
        OneSignal.addTrigger("home_screen_ready", "true");
        OneSignal.addTrigger("test_trigger", "true");

        tabLayout.setupWithViewPager(viewPager);

        VPadaptor vPadaptor = new VPadaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(vPadaptor);

        fragment_1 = new home1Fragment();
        vPadaptor.addfram(fragment_1, "Booking");

        fragment_2 = new home2Fragment();
        vPadaptor.addfram(fragment_2, "Free Vehicles");

        vPadaptor.notifyDataSetChanged();

        if (profileContainer.postRefresh.equals("freeVehicle")) {
            profileContainer.postRefresh = "";
            viewPager.setCurrentItem(1);
        } else if (profileContainer.postRefresh.equals("bookVehicle")) {
            profileContainer.postRefresh = "";
            viewPager.setCurrentItem(0);
        }


        initial();

        FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                .collection("reviewRequest").whereEqualTo("request", "pending")
                .orderBy("TimeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> list = value.getDocuments();
                        for (DocumentSnapshot d : list) {
                            Intent i = new Intent(getApplicationContext(), send_review2.class);
                            i.putExtra("transId", d.getString("TransactionId"));
                            i.putExtra("type", "agent");
                            i.putExtra("custno", d.getString("SenderMobileNo"));
                            i.putExtra("custname", d.getString("SenderName"));
                            startActivity(i);
                            finishAffinity();
                            break;
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            profileContainer.userWallet = ((Number) value.get("userWallet")).doubleValue();
                            profileContainer.isActive = value.getString("isActive");
                            profileContainer.userFreeTrial = value.getString("freeTrial");
                            profileContainer.activePlan = value.getString("activePlan");
                            profileContainer.planExpiry = value.getString("planExpiry");
                        }
                    }
                });
        FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot value) {
                if (value.exists()) {
                    balance.setText("₹" + df.format(profileContainer.userWallet));
                }
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                notification.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), notifications.class));
            }
        });

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

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), mybooking.class));
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

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), chat.class));
                overridePendingTransition(0, 0);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), profile.class));
                overridePendingTransition(0, 0);
            }
        });

    }

    public void myTransaction(View view) {
        startActivity(new Intent(getApplicationContext(), transaction.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        balance.setText("₹" + df.format(profileContainer.userWallet));
    }

    private void initial() {
        if (ContextCompat.checkSelfPermission(home.this,
                POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(home.this, new String[]{POST_NOTIFICATIONS}, 1);
        }
    }

}