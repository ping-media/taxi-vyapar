package org.taxivyapar.app;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class mykyc extends AppCompatActivity {
    View back;
    CardView login;
    EditText upi, account, bank, ifsc, holder;
    ConstraintLayout bankcont;
    RadioButton rdupi, rdbank;
    Dialog loading;
    String type = "upi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mykyc);

        back = findViewById(R.id.view);
        login = findViewById(R.id.login);

        upi = findViewById(R.id.upi);
        account = findViewById(R.id.account);
        bank = findViewById(R.id.bank);
        ifsc = findViewById(R.id.ifsc);
        holder = findViewById(R.id.holder);

        bankcont = findViewById(R.id.bankcont);

        rdupi = findViewById(R.id.rdupi);
        rdbank = findViewById(R.id.rdbank);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        upi.setText(profileContainer.upiId);
        bank.setText(profileContainer.bankName);
        ifsc.setText(profileContainer.bankIfsc);
        account.setText(profileContainer.bankAccount);
        holder.setText(profileContainer.bankHolder);

        rdupi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdupi.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdbank.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdupi.setTextColor(Color.WHITE);
                rdbank.setTextColor(Color.parseColor("#858585"));
                rdupi.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdbank.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                type = "upi";
                upi.setVisibility(view.VISIBLE);
                bankcont.setVisibility(view.GONE);
            }
        });
        rdbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdbank.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdupi.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdbank.setTextColor(Color.WHITE);
                rdupi.setTextColor(Color.parseColor("#858585"));
                rdbank.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdupi.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                type = "bank";
                bankcont.setVisibility(view.VISIBLE);
                upi.setVisibility(view.GONE);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                if(type.equals("upi")){
                    if(upi.getText().toString().trim().isEmpty()){
                        Toast.makeText(mykyc.this, "Please enter upi id", Toast.LENGTH_SHORT).show();
                    }else {
                        loading.show();
                        profileContainer.upiId = upi.getText().toString().trim();
                        Map<String, Object> user = new HashMap<>();
                        user.put("upiId", profileContainer.upiId);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loading.dismiss();
                                        Toast.makeText(mykyc.this, "Payment Profile Save.", Toast.LENGTH_SHORT).show();
                                        mykyc.super.onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loading.dismiss();
                                        Toast.makeText(mykyc.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }else {
                    if(account.getText().toString().trim().isEmpty()){
                        Toast.makeText(mykyc.this, "Please enter account number", Toast.LENGTH_SHORT).show();
                    }else if(bank.getText().toString().trim().isEmpty()){
                        Toast.makeText(mykyc.this, "Please enter bank name", Toast.LENGTH_SHORT).show();
                    }else if(ifsc.getText().toString().trim().isEmpty()){
                        Toast.makeText(mykyc.this, "Please enter bank ifsc code", Toast.LENGTH_SHORT).show();
                    }else if(holder.getText().toString().trim().isEmpty()){
                        Toast.makeText(mykyc.this, "Please enter account holder name", Toast.LENGTH_SHORT).show();
                    }else {
                        loading.show();
                        profileContainer.bankAccount = account.getText().toString().trim();
                        profileContainer.bankName = bank.getText().toString().trim();
                        profileContainer.bankIfsc = ifsc.getText().toString().trim();
                        profileContainer.bankHolder = holder.getText().toString().trim();
                        Map<String, Object> user = new HashMap<>();
                        user.put("bankAccount", profileContainer.bankAccount);
                        user.put("bankName", profileContainer.bankName);
                        user.put("bankIfsc", profileContainer.bankIfsc);
                        user.put("bankHolder", profileContainer.bankHolder);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loading.dismiss();
                                        Toast.makeText(mykyc.this, "Payment Profile Save.", Toast.LENGTH_SHORT).show();
                                        mykyc.super.onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loading.dismiss();
                                        Toast.makeText(mykyc.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                mykyc.super.onBackPressed();
            }
        });
    }
}