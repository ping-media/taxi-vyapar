package org.taxivyapar.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        OneSignal.initWithContext(this);
        OneSignal.setAppId("c6d75888-f3ba-4c5a-8f2c-f61700cdb3f9");

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.sendTag("all", "true");

//        OneSignal.addTrigger("home_screen_ready", "true");

        SharedPreferences gettheme = getSharedPreferences("user", MODE_PRIVATE);
        String noti = gettheme.getString("notification", "on");

        String vehicleTypes = gettheme.getString("vehicle", "all");
        String tripType = gettheme.getString("trip", "all");
        String pickupCities = gettheme.getString("pickup", "all");
        String dropCities = gettheme.getString("drop", "all");

        ArrayList<String> vehicle = new ArrayList<>();
        vehicle.add("Hatchback");
        vehicle.add("Sedan");
        vehicle.add("Ertiga");
        vehicle.add("Suv");
        vehicle.add("Kia Cerens");
        vehicle.add("Innova");
        vehicle.add("Innova Crysta");
        vehicle.add("Innova Hycross");
        vehicle.add("Force Traveller");
        vehicle.add("Bus");

        if (noti.equals("on")) {
            for (String v : vehicle) {
                if (vehicleTypes.equals("all")) {
                    OneSignal.sendTag("vehicle_" + v, "true");
                } else if (vehicleTypes.contains(v)) {
                    OneSignal.sendTag("vehicle_" + v, "true");
                } else {
                    OneSignal.deleteTag("vehicle_" + v);
                }
            }

            if (tripType.equals("all")) {
                OneSignal.sendTag("trip_oneWay", "true");
                OneSignal.sendTag("trip_roundWay", "true");
            } else if (tripType.equals("oneWay")) {
                OneSignal.sendTag("trip_oneWay", "true");
                OneSignal.deleteTag("trip_roundWay");
            } else {
                OneSignal.sendTag("trip_roundWay", "true");
                OneSignal.deleteTag("trip_oneWay");
            }

            if (pickupCities.equals("all")) {
                OneSignal.sendTag("pick_all", "true");
            } else {
                OneSignal.deleteTag("pick_all");
                String[] pick = pickupCities.split(",");
                for (String v : pick) {
                    OneSignal.sendTag("pick_" + v, "true");
                }
            }

            if (dropCities.equals("all")) {
                OneSignal.sendTag("drop_all", "true");
            } else {
                OneSignal.deleteTag("drop_all");
                String[] pick = dropCities.split(",");
                for (String v : pick) {
                    OneSignal.sendTag("drop_" + v, "true");
                }
            }
        } else {
            OneSignal.getTags(tags -> {
                if (tags != null) {
                    Iterator<String> keys = tags.keys();
                    List<String> tagNames = new ArrayList<>();
                    while (keys.hasNext()) {
                        tagNames.add(keys.next());
                    }
                    tagNames.remove("all");
                    OneSignal.deleteTags(tagNames);
                }
            });
        }


        FirebaseFirestore.getInstance().collection("header").document("header")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        profileContainer.appVersion = value.getString("appversion");
                        profileContainer.updateVersion = value.getString("appupdate");
                        profileContainer.playReview = value.getString("appplay");
                        profileContainer.website = value.getString("website");
                        profileContainer.call = value.getString("call");
                        profileContainer.whatsapp = value.getString("what");
                        profileContainer.mail = value.getString("email");
                        profileContainer.telegram = value.getString("telegram");
                        profileContainer.facebook = value.getString("facebook");
                        profileContainer.instagram = value.getString("instagram");
                        profileContainer.youtube = value.getString("youtube");
                        profileContainer.twitter = value.getString("twitter");
                        profileContainer.demoPhone = value.getString("demoPhone");
                        profileContainer.demoOTP = value.getString("demoOtp");
                        profileContainer.applink = value.getString("applink");
                        profileContainer.key_razor = value.getString("key_razor");
                        profileContainer.secret_razor = value.getString("secret_razor");
                        profileContainer.privacy = value.getString("privacy");
                        profileContainer.oneSignalChannelSound = value.getString("oneSignalChannelSound");
                        profileContainer.oneSignalChannelMsg = value.getString("oneSignalChannelMsg");
                        profileContainer.oneSignal = value.getString("oneSignal");
                        profileContainer.oneSignalToken = value.getString("oneSignalToken");
                        profileContainer.fast2sms_key = value.getString("fast2sms_key");
                        profileContainer.fast2sms_message_id = value.getString("fast2sms_message_id");
                        profileContainer.fast2sms_sender_id = value.getString("fast2sms_sender_id");
                        profileContainer.fast2sms_url = value.getString("fast2sms_url");
                        profileContainer.whatmsg = value.getString("whatmsg");
                        profileContainer.freeTrial = value.getString("freeTrial");
                    }
                });

        profileContainer.userMobileNo = gettheme.getString("userMobileNo", "00000");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!profileContainer.userMobileNo.equals("00000")) {
                    FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        if (documentSnapshot.getString("UserStatus").equals("active")) {
                                            profileContainer.userName = documentSnapshot.getString("UserName");
                                            profileContainer.userMobileNo = documentSnapshot.getString("UserPhoneNumber");
                                            profileContainer.userMessageToken = documentSnapshot.getString("UserMessageToken");
                                            profileContainer.userProfileImageUrl = documentSnapshot.getString("UserProfileImageUri");
                                            profileContainer.userVerify = documentSnapshot.getString("UserVerify");
                                            profileContainer.userEmail = documentSnapshot.getString("UserEmail");
                                            profileContainer.userCompany = documentSnapshot.getString("userCompany");
                                            profileContainer.userType = documentSnapshot.getString("userType");
                                            profileContainer.userLicense = documentSnapshot.getString("userLicense");
                                            profileContainer.userAddress = documentSnapshot.getString("UserAddress");
                                            profileContainer.userAddLat = documentSnapshot.getString("AddressLat");
                                            profileContainer.userAddCity = documentSnapshot.getString("AddressCity");
                                            profileContainer.userAddLng = documentSnapshot.getString("AddressLng");
                                            profileContainer.userWallet = ((Number) documentSnapshot.get("userWallet")).doubleValue();
                                            profileContainer.bankAccount = documentSnapshot.getString("bankAccount");
                                            profileContainer.bankName = documentSnapshot.getString("bankName");
                                            profileContainer.bankIfsc = documentSnapshot.getString("bankIfsc");
                                            profileContainer.bankHolder = documentSnapshot.getString("bankHolder");
                                            profileContainer.upiId = documentSnapshot.getString("upiId");
                                            profileContainer.registrationDate = documentSnapshot.getString("TimeStamp");
                                            profileContainer.deviceId = documentSnapshot.getString("deviceId");
                                            profileContainer.isActive = documentSnapshot.getString("isActive");
                                            profileContainer.activePlan = documentSnapshot.getString("activePlan");
                                            profileContainer.planExpiry = documentSnapshot.getString("planExpiry");
                                            profileContainer.userFreeTrial = documentSnapshot.getString("freeTrial");

                                            String dvid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                                            if (profileContainer.deviceId != null && profileContainer.deviceId.equals(dvid)) {
                                                startActivity(new Intent(MainActivity.this, home.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(MainActivity.this, login.class));
                                                finish();
                                            }
                                        } else {
                                            startActivity(new Intent(MainActivity.this, login.class));
                                            finish();
                                        }

                                    } else {
                                        startActivity(new Intent(MainActivity.this, login.class));
                                        finish();
                                    }
                                }
                            });
                } else {
                    startActivity(new Intent(MainActivity.this, login.class));
                    finish();
                }
            }
        }, 3000);

    }
}