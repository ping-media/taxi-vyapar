package org.taxivyapar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class signup extends AppCompatActivity {
    View back;
    TextView privacy, signup;
    CheckBox checkbox;
    EditText name, mobile;
    CardView login;
    Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        back = findViewById(R.id.viewmanu);
        privacy = findViewById(R.id.privacy);
        signup = findViewById(R.id.singup);
        checkbox = findViewById(R.id.checkbox);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        login = findViewById(R.id.login);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        mobile.setText(profileContainer.sampleMobileNo.replace("+91", ""));

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                signup.startAnimation(animation);
                signup.super.onBackPressed();
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                privacy.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), privacy.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Please enter name");
                    name.setFocusable(true);
                    name.setFocusableInTouchMode(true);
                    name.requestFocus();
                } else if (mobile.getText().toString().trim().equals("")) {
                    mobile.setError("Please enter mobile number");
                    mobile.setFocusable(true);
                    mobile.setFocusableInTouchMode(true);
                    mobile.requestFocus();
                } else if (mobile.getText().toString().length() != 10) {
                    mobile.setError("Invalid mobile number");
                    mobile.setFocusable(true);
                    mobile.setFocusableInTouchMode(true);
                    mobile.requestFocus();
                } else if (!checkbox.isChecked()) {
                    Toast.makeText(signup.this, "Please accept our privacy policy and term & condition.", Toast.LENGTH_SHORT).show();
                } else {
                    loading.show();
                    profileContainer.userMobileNo = "+91" + mobile.getText().toString().trim();
                    FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        profileContainer.loginType = "signup";
                                        profileContainer.userName = name.getText().toString().trim();
                                        profileContainer.sampleMobileNo = profileContainer.userMobileNo;
                                        startActivity(new Intent(getApplicationContext(), otpverification.class));
                                    } else {
                                        profileContainer.loginType = "login";
                                        profileContainer.userName = documentSnapshot.getString("UserName");
                                        profileContainer.userMobileNo = documentSnapshot.getString("UserPhoneNumber");
                                        profileContainer.userMessageToken = documentSnapshot.getString("UserMessageToken");
                                        profileContainer.userProfileImageUrl = documentSnapshot.getString("UserProfileImageUri");
                                        profileContainer.userVerify = documentSnapshot.getString("UserVerify");
                                        profileContainer.userEmail = documentSnapshot.getString("UserEmail");
                                        profileContainer.userCompany = documentSnapshot.getString("userCompany");
                                        profileContainer.userType = documentSnapshot.getString("userType");
                                        profileContainer.userWallet = ((Number) documentSnapshot.get("userWallet")).doubleValue();
                                        profileContainer.userLicense = documentSnapshot.getString("userLicense");
                                        profileContainer.userAddress = documentSnapshot.getString("UserAddress");
                                        profileContainer.userAddLat = documentSnapshot.getString("AddressLat");
                                        profileContainer.userAddCity = documentSnapshot.getString("AddressCity");
                                        profileContainer.userAddLng = documentSnapshot.getString("AddressLng");
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
                                        profileContainer.sampleMobileNo = profileContainer.userMobileNo;
                                        startActivity(new Intent(getApplicationContext(), otpverification.class));
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(getApplicationContext(), "Your internet is not working!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                signup.super.onBackPressed();
            }
        });
    }
}