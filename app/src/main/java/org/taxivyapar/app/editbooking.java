package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class editbooking extends AppCompatActivity {
    CardView submit;
    Spinner spinner;
    EditText remark, amount, commission, days, tourdescription;
    Switch butsecure;
    LinearLayout laybook;
    RadioButton rdbooking, rdbid, rdpublic, rdmynetwork, rdinclusive, rdexclusive;
    CheckBox checknego, checkhide, checkdiesel, checkcarrier;
    TextView startdate, starttime, location, droplocation, idname;
    Dialog loading, addressPopUp, postPopup;
    RadioButton radio1, radio2;
    Button btn1, btn2;
    ImageView addressImage;
    EditText addressEt;
    LinearLayout roundtrip;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    ArrayList<String> arrAddress, arrAddId;
    ArrayList<String> networkContact;
    ArrayAdapter ad;
    String hrNm = "", preferenceContact = "call", preferenceDriver = "commission";
    String lat = "28.6139391", lng = "77.2090212", address = "New Delhi, India", locality = "", trip = "oneWay",
            lat2 = "28.6139391", lng2 = "77.2090212", address2 = "New Delhi, India", locality2 = "",
            st_date = "", st_time = "", set_loc = "pickup", payment = "booking", post_vis = "public", extra = "include";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbooking);
        submit = findViewById(R.id.login);
        spinner = findViewById(R.id.spinner);
        remark = findViewById(R.id.remark);
        startdate = findViewById(R.id.startdate);
        starttime = findViewById(R.id.starttime);
        location = findViewById(R.id.location);
        droplocation = findViewById(R.id.droplocation);

        idname = findViewById(R.id.textView);

        amount = findViewById(R.id.amount);
        commission = findViewById(R.id.commision);

        butsecure = findViewById(R.id.butsecure);
        laybook = findViewById(R.id.laybook);

        roundtrip = findViewById(R.id.roundtrip);
        days = findViewById(R.id.days);
        tourdescription = findViewById(R.id.tourdescription);

        rdbooking = findViewById(R.id.rdbooking);
        rdbid = findViewById(R.id.rdbid);
        rdpublic = findViewById(R.id.rdpublic);
        rdmynetwork = findViewById(R.id.rdmynetwork);
        rdinclusive = findViewById(R.id.rdinclusive);
        rdexclusive = findViewById(R.id.rdexclusive);

        checknego = findViewById(R.id.checknego);
        checkhide = findViewById(R.id.checkhide);
        checkdiesel = findViewById(R.id.checkdiesel);
        checkcarrier = findViewById(R.id.checkcarrier);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        addressPopUp = new Dialog(this);
        addressPopUp.setContentView(R.layout.popup_address);
        addressPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressRecycler = addressPopUp.findViewById(R.id.recycler);
        addressEt = addressPopUp.findViewById(R.id.searchcustomer);
        addressImage = addressPopUp.findViewById(R.id.searchimage);

        addressRecycler.setLayoutManager(new LinearLayoutManager(this));
        arrAddress = new ArrayList<>();
        arrAddId = new ArrayList<>();
        addressadeptor = new addressAdeptor(arrAddress);
        addressRecycler.setAdapter(addressadeptor);

        postPopup = new Dialog(this);
        postPopup.setContentView(R.layout.popup_booking);
        postPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        postPopup.setCancelable(false);
        radio1 = postPopup.findViewById(R.id.radio1);
        radio2 = postPopup.findViewById(R.id.radio2);
        btn1 = postPopup.findViewById(R.id.btn1);
        btn2 = postPopup.findViewById(R.id.btn2);


        networkContact = new ArrayList<>();

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

        ad = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, vehicle);
        ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                hrNm = vehicle.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loading.show();
        FirebaseFirestore.getInstance().collection("postBooking")
                .document(profileContainer.productId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loading.dismiss();
                        String selectedVehicle = documentSnapshot.getString("VehicleName");
                        int position = vehicle.indexOf(selectedVehicle);
                        if (position >= 0) {
                            spinner.setSelection(position);
                        }
                        idname.setText("ID: " + documentSnapshot.getString("BookingId"));
                        startdate.setText(documentSnapshot.getString("StartDate"));
                        starttime.setText(documentSnapshot.getString("StartTime"));
                        st_date = documentSnapshot.getString("StartTimeStamp").substring(0, 10);
                        st_time = documentSnapshot.getString("StartTimeStamp").substring(11);
                        remark.setText(documentSnapshot.getString("Remark"));
                        lat = documentSnapshot.getString("AddressLat");
                        lng = documentSnapshot.getString("AddressLng");
                        address = documentSnapshot.getString("Address");
                        locality = documentSnapshot.getString("AddressCity");
                        location.setText(address);
                        lat2 = documentSnapshot.getString("DropAddressLat");
                        lng2 = documentSnapshot.getString("DropAddressLng");
                        address2 = documentSnapshot.getString("DropAddress");
                        locality2 = documentSnapshot.getString("DropAddressCity");
                        droplocation.setText(address2);

                        tourdescription.setText(documentSnapshot.getString("Description"));
                        days.setText(documentSnapshot.getString("TourDays"));

                        if (documentSnapshot.getString("BookingType").equals("oneWay")) {
                            roundtrip.setVisibility(View.GONE);
                            trip = "oneWay";
                        } else {
                            roundtrip.setVisibility(View.VISIBLE);
                            trip = "roundWay";
                        }

                        if (documentSnapshot.getString("BookingSecure").equals("yes")) {
                            butsecure.setChecked(true);
                        }

                        if (documentSnapshot.getString("ProfileHide").equals("yes")) {
                            checkhide.setChecked(true);
                        }

                        if (documentSnapshot.getString("PaymentSystem").equals("booking")) {
                            rdbooking.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                            rdbid.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            rdbooking.setTextColor(Color.WHITE);
                            rdbid.setTextColor(Color.parseColor("#858585"));
                            rdbooking.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            rdbid.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                            payment = "booking";
                            laybook.setVisibility(View.VISIBLE);
                            butsecure.setEnabled(true);
                        } else {
                            rdbid.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                            rdbooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            rdbid.setTextColor(Color.WHITE);
                            rdbid.setChecked(true);
                            rdbooking.setTextColor(Color.parseColor("#858585"));
                            rdbid.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            rdbooking.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                            rdbooking.setChecked(false);
                            payment = "bid";
                            laybook.setVisibility(View.GONE);
                            butsecure.setChecked(false);
                            butsecure.setEnabled(false);
                        }
                        if (documentSnapshot.get("PaymentAmount") != null) {
                            amount.setText(documentSnapshot.getString("PaymentAmount"));
                        }
                        if (documentSnapshot.get("PaymentCommission") != null) {
                            commission.setText(documentSnapshot.getString("PaymentCommission"));
                        }

                        if (documentSnapshot.get("PaymentNegotiable") != null) {
                            if (documentSnapshot.getString("PaymentNegotiable").equals("yes")) {
                                checknego.setChecked(true);
                            }
                        }

                        if (documentSnapshot.getString("Network").equals("public")) {
                            rdpublic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                            rdmynetwork.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            rdpublic.setTextColor(Color.WHITE);
                            rdmynetwork.setTextColor(Color.parseColor("#858585"));
                            rdpublic.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            rdmynetwork.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                            post_vis = "public";
                        } else {
                            rdmynetwork.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                            rdpublic.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            rdmynetwork.setTextColor(Color.WHITE);
                            rdpublic.setTextColor(Color.parseColor("#858585"));
                            rdmynetwork.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            rdpublic.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                            post_vis = "private";
                            rdpublic.setChecked(false);
                            rdmynetwork.setChecked(true);
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(profileContainer.userMobileNo).collection("network")
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot d : list) {
                                                networkContact.add(d.getString("UserPhoneNumber"));
                                            }
                                        }
                                    });
                        }



                        if (documentSnapshot.getString("Extra").equals("include")) {
                            rdinclusive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                            rdexclusive.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            rdinclusive.setTextColor(Color.WHITE);
                            rdexclusive.setTextColor(Color.parseColor("#858585"));
                            rdinclusive.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            rdexclusive.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                            extra = "include";
                            rdinclusive.setChecked(true);
                        } else {
                            rdexclusive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                            rdinclusive.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                            rdexclusive.setTextColor(Color.WHITE);
                            rdinclusive.setTextColor(Color.parseColor("#858585"));
                            rdexclusive.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            rdinclusive.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                            extra = "exclude";
                            rdexclusive.setChecked(true);
                        }

                        if (documentSnapshot.getString("Diesel").equals("yes")) {
                            checkdiesel.setChecked(true);
                        }

                        if (documentSnapshot.getString("Carrier").equals("yes")) {
                            checkcarrier.setChecked(true);
                        }
                    }
                });


        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceDriver = "commission";
            }
        });
        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceDriver = "manual";
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceContact = "call";
                loading.show();
                Map<String, Object> user = new HashMap<>();
                user.put("PreferenceContact", preferenceContact);
                user.put("PreferenceDriver", preferenceDriver);


                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(profileContainer.productId).update(user);
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("postBooking")
                        .document(profileContainer.productId).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                                editbooking.super.onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(getApplicationContext(), "your internet is not working.", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceContact = "msg";
                loading.show();
                Map<String, Object> user = new HashMap<>();
                user.put("PreferenceContact", preferenceContact);
                user.put("PreferenceDriver", preferenceDriver);


                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(profileContainer.productId).update(user);
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("postBooking")
                        .document(profileContainer.productId).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                                editbooking.super.onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(getApplicationContext(), "your internet is not working.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        rdbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdbooking.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdbid.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdbooking.setTextColor(Color.WHITE);
                rdbid.setTextColor(Color.parseColor("#858585"));
                rdbooking.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdbid.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                payment = "booking";
                laybook.setVisibility(View.VISIBLE);
                butsecure.setEnabled(true);
            }
        });
        rdbid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdbid.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdbooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdbid.setTextColor(Color.WHITE);
                rdbooking.setTextColor(Color.parseColor("#858585"));
                rdbid.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdbooking.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                payment = "bid";
                laybook.setVisibility(View.GONE);
                butsecure.setChecked(false);
                butsecure.setEnabled(false);
            }
        });

        rdpublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdpublic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdmynetwork.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdpublic.setTextColor(Color.WHITE);
                rdmynetwork.setTextColor(Color.parseColor("#858585"));
                rdpublic.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdmynetwork.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                post_vis = "public";
                networkContact.clear();
            }
        });
        rdmynetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdmynetwork.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdpublic.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdmynetwork.setTextColor(Color.WHITE);
                rdpublic.setTextColor(Color.parseColor("#858585"));
                rdmynetwork.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdpublic.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                post_vis = "private";
                loading.show();
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("network")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                loading.dismiss();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    networkContact.add(d.getString("UserPhoneNumber"));
                                }
                                if (networkContact.size() <= 0) {
                                    Toast.makeText(getApplicationContext(), "0 drivers in your network.", Toast.LENGTH_SHORT).show();
                                    networkContact.clear();
                                    rdpublic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                                    rdmynetwork.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                                    rdpublic.setTextColor(Color.WHITE);
                                    rdmynetwork.setTextColor(Color.parseColor("#858585"));
                                    rdpublic.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                                    rdmynetwork.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                                    post_vis = "public";
                                    rdpublic.setChecked(true);
                                    rdmynetwork.setChecked(false);

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(getApplicationContext(), "your internet is not working", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        rdinclusive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdinclusive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdexclusive.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdinclusive.setTextColor(Color.WHITE);
                rdexclusive.setTextColor(Color.parseColor("#858585"));
                rdinclusive.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdexclusive.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                extra = "include";
            }
        });
        rdexclusive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdexclusive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                rdinclusive.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdexclusive.setTextColor(Color.WHITE);
                rdinclusive.setTextColor(Color.parseColor("#858585"));
                rdexclusive.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdinclusive.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                extra = "exclude";
            }
        });

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        editbooking.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        st_date = year + "_" + String.format("%02d", (month + 1)) + "_" + String.format("%02d", dayOfMonth);
                        if (month == 0) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Jan, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Jan, " + year);
                            }
                        }
                        if (month == 1) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Feb, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Feb, " + year);
                            }
                        }
                        if (month == 2) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Mar, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Mar, " + year);
                            }
                        }
                        if (month == 3) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Apr, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Apr, " + year);
                            }
                        }
                        if (month == 4) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " May, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " May, " + year);
                            }
                        }
                        if (month == 5) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Jun, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Jun, " + year);
                            }
                        }
                        if (month == 6) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Jul, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Jul, " + year);
                            }
                        }
                        if (month == 7) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Aug, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Aug, " + year);
                            }
                        }
                        if (month == 8) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Sep, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Sep, " + year);
                            }
                        }
                        if (month == 9) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Oct, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Oct, " + year);
                            }
                        }
                        if (month == 10) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Nov, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Nov, " + year);
                            }
                        }
                        if (month == 11) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Dec, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Dec, " + year);
                            }
                        }

                    }
                }, year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(editbooking.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                SimpleDateFormat format1 = new SimpleDateFormat("HH_mm_00", Locale.getDefault());
                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                calendar.set(Calendar.MINUTE, i1);
                                String formattedTime = format.format(calendar.getTime());
                                st_time = format1.format(calendar.getTime());
                                starttime.setText(formattedTime);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_loc = "pickup";
                addressPopUp.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addressEt.setFocusable(true);
                        addressEt.setFocusableInTouchMode(true);
                        addressEt.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(addressEt, InputMethodManager.SHOW_FORCED);
                        }
                    }
                }, 100);
            }
        });
        droplocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_loc = "drop";
                addressPopUp.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addressEt.setFocusable(true);
                        addressEt.setFocusableInTouchMode(true);
                        addressEt.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(addressEt, InputMethodManager.SHOW_FORCED);
                        }
                    }
                }, 100);
            }
        });

        addressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String loc = addressEt.getText().toString().trim();
                String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + loc + "&components=country:IN&key=AIzaSyCELxmgPiNOhUWqjbFE-F-wqJIr20OY5jQ";
                RequestQueue queue = Volley.newRequestQueue(editbooking.this);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        arrAddress.clear();
                        arrAddId.clear();
                        addressImage.setVisibility(View.VISIBLE);
                        try {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray = response.getJSONArray("predictions");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject = jsonArray.getJSONObject(i);
                                arrAddress.add(jsonObject.getString("description"));
                                arrAddId.add(jsonObject.getString("place_id"));
                            }
                            addressadeptor.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(req);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                submit.startAnimation(animation);

                if (startdate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select pick date", Toast.LENGTH_SHORT).show();
                } else if (starttime.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select pick time", Toast.LENGTH_SHORT).show();
                } else if (location.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please add pickup location", Toast.LENGTH_SHORT).show();
                } else if (droplocation.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please add drop location", Toast.LENGTH_SHORT).show();
                } else {
                    if (payment.equals("booking")) {
                        if (amount.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please enter booking amount", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (Double.valueOf(amount.getText().toString().trim()) == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter booking amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                    loading.show();
                    String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Double.valueOf(lat),
                            Double.valueOf(lng)));
                    String hash2 = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Double.valueOf(lat2),
                            Double.valueOf(lng2)));
                    Map<String, Object> user = new HashMap<>();
                    user.put("VehicleName", hrNm);
                    user.put("StartDate", startdate.getText().toString().trim());
                    user.put("StartTime", starttime.getText().toString().trim());
                    user.put("StartTimeStamp", st_date + "_" + st_time);
                    user.put("Remark", remark.getText().toString().trim());
                    user.put("AddressLat", lat);
                    user.put("AddressLng", lng);
                    user.put("AddressHash", hash);
                    user.put("Address", address);
                    user.put("AddressCity", locality);
                    user.put("DropAddressLat", lat2);
                    user.put("DropAddressLng", lng2);
                    user.put("DropAddressHash", hash2);
                    user.put("DropAddress", address2);
                    user.put("DropAddressCity", locality2);
                    user.put("PreferenceContact", preferenceContact);
                    user.put("PreferenceDriver", preferenceDriver);
                    if (payment.equals("booking")) {
                        user.put("PaymentSystem", "booking");
                        user.put("PaymentAmount", amount.getText().toString().trim());
                        user.put("PaymentCommission", commission.getText().toString().trim());
                        if (checknego.isChecked()) {
                            user.put("PaymentNegotiable", "yes");
                        } else {
                            user.put("PaymentNegotiable", "no");
                        }
                    } else {
                        user.put("PaymentSystem", "bidding");
                    }
                    user.put("Network", post_vis);
                    user.put("NetworkContact", networkContact);

                    if (butsecure.isChecked()) {
                        user.put("BookingSecure", "yes");
                    } else {
                        user.put("BookingSecure", "no");
                    }
                    if (checkhide.isChecked()) {
                        user.put("ProfileHide", "yes");
                    } else {
                        user.put("ProfileHide", "no");
                    }

                    user.put("Extra", extra);
                    if (checkdiesel.isChecked()) {
                        user.put("Diesel", "yes");
                    } else {
                        user.put("Diesel", "no");
                    }
                    if (checkcarrier.isChecked()) {
                        user.put("Carrier", "yes");
                    } else {
                        user.put("Carrier", "no");
                    }

                    if (trip.equals("roundWay")) {
                        user.put("Description", tourdescription.getText().toString().trim());
                        user.put("TourDays", days.getText().toString().trim());
                    }

                    FirebaseFirestore.getInstance().collection("postBooking")
                            .document(profileContainer.productId).update(user);
                    FirebaseFirestore.getInstance().collection("users")
                            .document(profileContainer.userMobileNo).collection("postBooking")
                            .document(profileContainer.productId).update(user);

                    loading.dismiss();
                    postPopup.show();

                }
            }
        });
    }

    public void back(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        view.startAnimation(animation);
        editbooking.super.onBackPressed();
    }

    public class addressAdeptor extends RecyclerView.Adapter<addressAdeptor.postviewholder> {
        ArrayList<String> postmodels;

        public addressAdeptor(ArrayList<String> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public addressAdeptor.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_address, parent, false);
            return new addressAdeptor.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull addressAdeptor.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            addressImage.setVisibility(View.GONE);
            holder.detail.setText(postmodels.get(position));
            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addressPopUp.dismiss();
                    loading.show();
                    String loc = arrAddId.get(position);
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?place_id=" + loc + "&key=AIzaSyCELxmgPiNOhUWqjbFE-F-wqJIr20OY5jQ";
                    RequestQueue queue = Volley.newRequestQueue(editbooking.this);
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loading.dismiss();
                            try {
                                JSONArray jsonArray = new JSONArray();
                                jsonArray = response.getJSONArray("results");

                                JSONObject jsonObject = new JSONObject();
                                JSONObject jsonObject1 = new JSONObject();
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject = jsonArray.getJSONObject(0);
                                jsonObject1 = jsonObject.getJSONObject("geometry");
                                jsonObject2 = jsonObject1.getJSONObject("location");
                                JSONArray addressComponents = jsonObject.getJSONArray("address_components");
                                String cityName = null;
                                for (int i = 0; i < addressComponents.length(); i++) {
                                    JSONObject component = addressComponents.getJSONObject(i);
                                    JSONArray types = component.getJSONArray("types");
                                    for (int j = 0; j < types.length(); j++) {
                                        if (types.getString(j).equals("locality")) {
                                            cityName = component.getString("long_name");
                                            if (set_loc.equals("pickup")) {
                                                locality = component.getString("long_name");
                                            } else {
                                                locality2 = component.getString("long_name");
                                            }
                                            break;
                                        } else if (types.getString(j).equals("administrative_area_level_1")
                                                && cityName == null) {
                                            cityName = component.getString("long_name");
                                            if (set_loc.equals("pickup")) {
                                                locality = component.getString("long_name");
                                            } else {
                                                locality2 = component.getString("long_name");
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (set_loc.equals("pickup")) {
                                    if (!locality.equals("")) {
                                        lat = jsonObject2.getString("lat");
                                        lng = jsonObject2.getString("lng");
                                        address = postmodels.get(position);
                                        location.setText(address);
                                    }

                                } else {
                                    if (!locality2.equals("")) {
                                        lat2 = jsonObject2.getString("lat");
                                        lng2 = jsonObject2.getString("lng");
                                        address2 = postmodels.get(position);
                                        droplocation.setText(address2);
                                    }
                                }
                                addressEt.setText("");
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(req);

                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView detail;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                detail = itemView.findViewById(R.id.detail);
            }
        }

    }
}