package org.taxivyapar.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class requestpay extends AppCompatActivity {
    ConstraintLayout loading;
    Dialog load;
    EditText amount, commision;
    CardView login;
    View rat1, rat2, rat3, rat4, rat5, verify;
    TextView mobile, name, avatar, review;
    ImageView profileimage;
    String transId = "", custno = "", custname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestpay);

        loading = findViewById(R.id.loading);
        login = findViewById(R.id.login);

        name = findViewById(R.id.textView14);
        mobile = findViewById(R.id.mobilenumber);
        avatar = findViewById(R.id.avatar);
        profileimage = findViewById(R.id.profileimage);
        review = findViewById(R.id.review);

        amount = findViewById(R.id.amount);
        commision = findViewById(R.id.commision);

        rat1 = findViewById(R.id.rat1);
        rat2 = findViewById(R.id.rat2);
        rat3 = findViewById(R.id.rat3);
        rat4 = findViewById(R.id.rat4);
        rat5 = findViewById(R.id.rat5);
        verify = findViewById(R.id.view10);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        Intent i = getIntent();
        transId = i.getStringExtra("transId");
        custno = i.getStringExtra("custno");
        custname = i.getStringExtra("custname");

        loading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("postBooking").document(transId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loading.setVisibility(View.GONE);
                        mobile.setVisibility(View.GONE);
                        try {
                            avatar.setText(custname.substring(0, 1).toUpperCase());
                        } catch (Exception e) {
                            avatar.setText("A");
                        }
                        ArrayList<String> commissionRequest = new ArrayList<>();
                        ArrayList<String> CommissionPay = new ArrayList<>();
                        ArrayList<String> commissionAmount = new ArrayList<>();
                        if (documentSnapshot.get("CommissionRequest") != null) {
                            commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                            CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                            commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");
                            if (commissionRequest.contains(custno)) {
                                int ind = commissionRequest.indexOf(custno);
                                amount.setText(commissionAmount.get(ind));
                                commision.setText(CommissionPay.get(ind));
                            }
                        } else {
                            if (documentSnapshot.getString("PaymentSystem").equals("booking")) {
                                amount.setText(documentSnapshot.getString("PaymentAmount"));
                                commision.setText(documentSnapshot.getString("PaymentCommission"));
                            }
                        }

                        review.setText("(0 Reviews)");
                        FirebaseFirestore.getInstance().collection("users")
                                .document(custno)
                                .collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        review.setText("(" + queryDocumentSnapshots.size() + " Reviews)");
                                    }
                                });

                        name.setText(custname);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(custno).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        name.setText(documentSnapshot.getString("UserName"));

                                        if (documentSnapshot.getString("userCompany") != null) {
                                            if (!documentSnapshot.getString("userCompany").equals("")) {
                                                mobile.setVisibility(View.VISIBLE);
                                                mobile.setText(documentSnapshot.getString("userCompany"));
                                            }
                                        }

                                        if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                            verify.setVisibility(View.VISIBLE);
                                        }
                                        try {
                                            Glide.with(getApplicationContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                                    .into(profileimage);
                                        } catch (Exception e) {
                                        }
                                        double prrat = 0;
                                        if (documentSnapshot.get("userRating") != null) {
                                            prrat = ((Number) documentSnapshot.get("userRating")).doubleValue();
                                        }
                                        if (prrat > 4) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_24);
                                        } else if (prrat > 3) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else if (prrat > 2) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else if (prrat > 1) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else if (prrat > 0) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        }
                                    }
                                });

                    }
                });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), reviewuser.class);
                i.putExtra("custno", custno);
                i.putExtra("custname", custname);
                startActivity(i);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), reviewuser.class);
                i.putExtra("custno", custno);
                i.putExtra("custname", custname);
                startActivity(i);
            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), reviewuser.class);
                i.putExtra("custno", custno);
                i.putExtra("custname", custname);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(getApplication(), R.anim.bounce);
                view.startAnimation(animation);

                if (amount.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter booking amount", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Double.valueOf(amount.getText().toString().trim()) == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter booking amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                load.show();

                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(transId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                load.dismiss();
                                ArrayList<String> commissionRequest = new ArrayList<>();
                                ArrayList<String> CommissionPay = new ArrayList<>();
                                ArrayList<String> commissionAmount = new ArrayList<>();
                                if (documentSnapshot.get("CommissionRequest") != null) {
                                    commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                    CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                    commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                    if (commissionRequest.contains(custno)) {
                                        int ind = commissionRequest.indexOf(custno);
                                        CommissionPay.set(ind, commision.getText().toString().trim());
                                        commissionAmount.set(ind, amount.getText().toString().trim());
                                    }
                                } else {
                                    commissionRequest.add(custno);
                                    CommissionPay.add(commision.getText().toString().trim());
                                    commissionAmount.add(amount.getText().toString().trim());
                                }

                                Map<String, Object> user = new HashMap<>();
                                user.put("CommissionRequest", commissionRequest);
                                user.put("CommissionPay", CommissionPay);
                                user.put("CommissionAmount", commissionAmount);

                                FirebaseFirestore.getInstance().collection("postBooking")
                                        .document(transId).update(user);
                                FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                                        .collection("postBooking")
                                        .document(transId).update(user);

                                profileContainer.shareDetail = "Agent has requested commission of ₹" + commision.getText().toString().trim() + " in advance to secure this booking.";
                                requestpay.super.onBackPressed();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                load.dismiss();
                                Toast.makeText(requestpay.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    public void back(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        view.startAnimation(animation);
        requestpay.super.onBackPressed();
    }
}