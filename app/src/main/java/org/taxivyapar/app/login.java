package org.taxivyapar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class login extends AppCompatActivity {

//    RecyclerView imagerecycler;
//    ArrayList<String> arrImg;
//    ContactsAdapter1 adapter1;
    CardView login;
    EditText mobile;
    TextView signup;
    Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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

        login = findViewById(R.id.login);
        mobile = findViewById(R.id.name);
        signup = findViewById(R.id.singup);

//        imagerecycler = findViewById(R.id.imagerecycler);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

//        LinearLayoutManager linearLayout1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        imagerecycler.setLayoutManager(linearLayout1);
//        arrImg = new ArrayList<>();
//        arrImg.add("1");
//        arrImg.add("2");
//        arrImg.add("3");
//        adapter1 = new ContactsAdapter1(arrImg);
//        imagerecycler.setAdapter(adapter1);
//
//        LinearSnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(imagerecycler);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (linearLayout1.findLastCompletelyVisibleItemPosition() < (adapter1.getItemCount() - 1)) {
//                    linearLayout1.smoothScrollToPosition(imagerecycler, new RecyclerView.State(), linearLayout1.findLastCompletelyVisibleItemPosition() + 1);
//                } else {
//                    linearLayout1.smoothScrollToPosition(imagerecycler, new RecyclerView.State(), 0);
//                }
//            }
//        }, 0, 3000);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                signup.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), signup.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                if (mobile.getText().toString().trim().equals("")) {
                    mobile.setError("Please enter mobile number");
                    mobile.setFocusable(true);
                    mobile.setFocusableInTouchMode(true);
                    mobile.requestFocus();
                } else if (mobile.getText().toString().length() != 10) {
                    mobile.setError("Invalid mobile number");
                    mobile.setFocusable(true);
                    mobile.setFocusableInTouchMode(true);
                    mobile.requestFocus();
                } else {
                    loading.show();
                    profileContainer.userMobileNo = "+91" + mobile.getText().toString().trim();
                    FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    loading.dismiss();
                                    if (documentSnapshot.exists()) {
                                        if (documentSnapshot.getString("UserStatus").equals("active")) {
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
                                            profileContainer.userFreeTrial = documentSnapshot.getString("freeTrial");
                                            profileContainer.sampleMobileNo = profileContainer.userMobileNo;
                                            startActivity(new Intent(getApplicationContext(), otpverification.class));  
                                        }else {
                                            Toast.makeText(login.this, "Account is inactive. Please contact support.", Toast.LENGTH_SHORT).show();
                                        }
                                        
                                    } else {
                                        profileContainer.sampleMobileNo = profileContainer.userMobileNo;
                                        startActivity(new Intent(getApplicationContext(), signup.class));
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

    }

//    public class ContactsAdapter1 extends RecyclerView.Adapter<ContactsAdapter1.ViewHolder> {
//        private ArrayList<String> datamodel;
//
//        public ContactsAdapter1() {
//        }
//
//        public ContactsAdapter1(ArrayList<String> datamodel) {
//            this.datamodel = datamodel;
//        }
//
//        @NonNull
//        @Override
//        public ContactsAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.introslider, parent, false);
//            return new ContactsAdapter1.ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ContactsAdapter1.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//            if (position == 0) {
//                holder.topslider.setImageResource(R.drawable.intro1);
//                holder.textView.setText("Request Ride");
//                holder.textView1.setText("Request a ride get picked up by a nearby community driver");
//            } else if (position == 1) {
//                holder.topslider.setImageResource(R.drawable.intro2);
//                holder.textView.setText("Confirm Your Driver");
//                holder.textView1.setText("Huge drivers network helps you find comforable, safe and cheap ride");
//            } else {
//                holder.topslider.setImageResource(R.drawable.intro3);
//                holder.textView.setText("Track your ride");
//                holder.textView1.setText("Know your driver in advance and be able to view current location in real time on the map");
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return datamodel.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            ImageView topslider;
//            TextView textView, textView1;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                topslider = itemView.findViewById(R.id.sliderimage);
//                textView = itemView.findViewById(R.id.textView);
//                textView1 = itemView.findViewById(R.id.textView1);
//            }
//        }
//    }

}