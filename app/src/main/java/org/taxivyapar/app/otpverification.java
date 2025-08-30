package org.taxivyapar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class otpverification extends AppCompatActivity {

    PinView otp;
    TextView timer, mobilenumber, resend;
    Dialog loading;
    Dialog verifyLoading;
    CardView verifyButton;
    String otpnumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        mobilenumber = findViewById(R.id.textView6);
        timer = findViewById(R.id.textView15);
        otp = findViewById(R.id.otp);
        verifyButton = findViewById(R.id.login);
        resend = findViewById(R.id.singup);

        verifyLoading = new Dialog(otpverification.this);
        verifyLoading.setContentView(R.layout.ui_verify);
        verifyLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyLoading.setCancelable(false);

        mobilenumber.setText(profileContainer.sampleMobileNo);
        initialotp();

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        profileContainer.userMessageToken = s;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                resend.startAnimation(animation);
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                verifyButton.startAnimation(animation);
                if (otp.getText().toString().trim().isEmpty()) {
                } else if (otp.getText().toString().trim().length() < 6) {
                } else {
                    if (!otp.getText().toString().trim().equals(otpnumber)) {
                        Toast.makeText(otpverification.this, "Invalid OTP.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (profileContainer.loginType.equals("signup")) {

                        verifyLoading.dismiss();

                        String currentDate = new SimpleDateFormat("dd MMM, yyyy", Locale.CANADA).format(new Date());
                        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());

                        Map<String, Object> user = new HashMap<>();
                        user.put("UserName", profileContainer.userName);
                        user.put("UserPhoneNumber", profileContainer.userMobileNo);
                        user.put("UserMessageToken", profileContainer.userMessageToken);
                        user.put("UserStatus", "active");
                        user.put("RegistrationDate", currentDate);
                        user.put("TimeStamp", timestmp);
                        user.put("UserProfileImageUri", "");
                        user.put("userWallet", 0);
                        user.put("UserVerify", "no");
                        String dvid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        user.put("deviceId", dvid);
                        user.put("deviceName", Build.MANUFACTURER + " " + Build.MODEL);

                        FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo).set(user);

                        SharedPreferences roomdbusermobileno = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = roomdbusermobileno.edit();
                        editor.putString("userMobileNo", profileContainer.userMobileNo);
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(), home.class));
                        finishAffinity();
                    } else {
                        Map<String, Object> trans1 = new HashMap<>();
                        trans1.put("UserMessageToken", profileContainer.userMessageToken);
                        String dvid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        trans1.put("deviceId", dvid);
                        trans1.put("deviceName", Build.MANUFACTURER + " " + Build.MODEL);

                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).update(trans1);

                        SharedPreferences roomdbusermobileno = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = roomdbusermobileno.edit();
                        editor.putString("userMobileNo", profileContainer.userMobileNo);
                        editor.apply();

                        startActivity(new Intent(getApplicationContext(), home.class));
                        finishAffinity();
                    }
                }
            }
        });
    }

    private void initialotp() {
        if (profileContainer.demoPhone.contains(profileContainer.userMobileNo.replace("+91", ""))) {
            otpnumber = profileContainer.demoOTP;
            return;
        }
        loading.show();
        otpnumber = new DecimalFormat("000000").format(new Random().nextInt(999999));

        String postUrl = profileContainer.fast2sms_url;
        JSONObject notiObject = new JSONObject();
        try {
            notiObject.put("route", "dlt");
            notiObject.put("sender_id", profileContainer.fast2sms_sender_id);
            notiObject.put("message", profileContainer.fast2sms_message_id);
            notiObject.put("variables_values", otpnumber);
            notiObject.put("flash", "0");
            notiObject.put("numbers", profileContainer.userMobileNo.replace("+91", ""));
        } catch (Exception e) {

        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, notiObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    String msg = response.getString("return");
                    if (msg.equals("true")) {
                        counter();
                    } else {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("authorization", profileContainer.fast2sms_key);
                header.put("Content-Type", "application/json");
                return header;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);

    }

    private void counter() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                timer.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + " sec");
            }

            public void onFinish() {
                timer.setText("");
            }
        }.start();
    }

}