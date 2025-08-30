package org.taxivyapar.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class selectdrvier extends AppCompatActivity {

    Spinner vehicleSpinner, driverSpinner;
    Dialog loading;
    ArrayList<String> vehicle, vehicleIm1, vehicleIm2, vehicleIm3, vehicleIm4, vehicleIm5, vehicleNo,
            driver, driverCont, driverImage;
    String selvehicle = "", selvehicleIm1 = "", selvehicleIm2 = "", selvehicleIm3 = "", selvehicleIm4 = "", selvehicleIm5 = "",
            selvehicleNo = "", seldriver = "", seldriverCont = "", seldriverImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdrvier);

        vehicleSpinner = findViewById(R.id.spinner);
        driverSpinner = findViewById(R.id.spinner2);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        vehicle = new ArrayList<>();
        vehicleIm1 = new ArrayList<>();
        vehicleIm2 = new ArrayList<>();
        vehicleIm3 = new ArrayList<>();
        vehicleIm4 = new ArrayList<>();
        vehicleIm5 = new ArrayList<>();
        vehicleNo = new ArrayList<>();
        driver = new ArrayList<>();
        driverCont = new ArrayList<>();
        driverImage = new ArrayList<>();

        loadVehicle();
        loadDriver();

        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                selvehicle = vehicle.get(i);
                selvehicleNo = vehicleNo.get(i);
                selvehicleIm1 = vehicleIm1.get(i);
                selvehicleIm2 = vehicleIm2.get(i);
                selvehicleIm3 = vehicleIm3.get(i);
                selvehicleIm4 = vehicleIm4.get(i);
                selvehicleIm5 = vehicleIm5.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                seldriver = driver.get(i);
                seldriverCont = driverCont.get(i);
                seldriverImg = driverImage.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void send(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        view.startAnimation(animation);
        if (selvehicle.equals("")) {
            Toast.makeText(this, "There is no vehicle for share", Toast.LENGTH_SHORT).show();
        } else if (seldriver.equals("")) {
            Toast.makeText(this, "There is no driver for share", Toast.LENGTH_SHORT).show();
        } else {
            profileContainer.shareDetail = "Driver Name: " + seldriver +
                    "\nContact Number: " + seldriverCont +
                    "\nVehicle Registration: " + selvehicleNo +
                    "\nVehicle Type: " + selvehicle;
            profileContainer.shareAttach1 = seldriverImg;
            profileContainer.shareAttach2 = selvehicleIm1;
            profileContainer.shareAttach3 = selvehicleIm2;
            profileContainer.shareAttach4 = selvehicleIm3;
            profileContainer.shareAttach5 = selvehicleIm4;
            selectdrvier.super.onBackPressed();
        }
    }

    private void loadVehicle() {
        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).collection("vehicles")
                .orderBy("TimeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            // Check insurance expiry before adding to dropdown
                            String insuranceExpiry = d.getString("InsuranceExpiry");
                            boolean isInsuranceValid = true;
                            
                            if (insuranceExpiry != null && !insuranceExpiry.isEmpty()) {
                                try {
                                    // Parse the date format "01 Jan, 2025"
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                                    Date expiryDate = inputFormat.parse(insuranceExpiry);
                                    Date currentDate = new Date();
                                    
                                    // If insurance is expired, don't add to dropdown
                                    if (expiryDate != null && expiryDate.before(currentDate)) {
                                        isInsuranceValid = false;
                                    }
                                } catch (ParseException e) {
                                    // If parsing fails, assume valid insurance
                                    isInsuranceValid = true;
                                }
                            }
                            
                            // Only add vehicle if insurance is valid
                            if (isInsuranceValid) {
                                vehicle.add(d.getString("VehicleName"));
                                vehicleNo.add(d.getString("VehicleNumber"));
                                vehicleIm1.add(d.getString("Attachment1"));
                                vehicleIm2.add(d.getString("Attachment2"));
                                vehicleIm3.add(d.getString("Attachment3"));
                                vehicleIm4.add(d.getString("Attachment4"));
                                vehicleIm5.add(d.getString("Attachment5"));
                            }
                        }
                        ArrayAdapter ad = new ArrayAdapter<>(selectdrvier.this, R.layout.simple_spinner_item, vehicleNo);
                        ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        vehicleSpinner.setAdapter(ad);
                    }
                });
    }

    private void loadDriver() {
        FirebaseFirestore.getInstance().collection("drivers")
                .whereEqualTo("UserPhoneNumber", profileContainer.userMobileNo)
                .orderBy("TimeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            driver.add(d.getString("SenderName"));
                            driverCont.add(d.getString("SenderMobileNo"));
                            driverImage.add(d.getString("Attachment1"));
                        }
                        ArrayAdapter ad = new ArrayAdapter<>(selectdrvier.this, R.layout.simple_spinner_item, driver);
                        ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        driverSpinner.setAdapter(ad);
                    }
                });
    }


    public void back(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        view.startAnimation(animation);
        selectdrvier.super.onBackPressed();
    }

}