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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class postbook2Fragment extends Fragment {
    View view;
    CardView submit;
    Spinner spinner;
    EditText remark, amount, commission, days, tourdescription;
    Switch butsecure;
    LinearLayout laybook;
    RadioButton rdbooking, rdbid, rdpublic, rdmynetwork, rdinclusive, rdexclusive;
    CheckBox checknego, checkhide, checkdiesel, checkcarrier;
    TextView startdate, starttime, location, droplocation;
    Dialog loading, addressPopUp, postPopup;
    RadioButton radio1, radio2;
    Button btn1, btn2;
    ImageView addressImage;
    EditText addressEt;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    ArrayList<String> arrAddress, arrAddId;
    ArrayList<String> networkContact;
    ArrayAdapter ad;
    String hrNm = "", preferenceContact = "call", preferenceDriver = "commission", fladid = "";
    String lat = "28.6139391", lng = "77.2090212", address = "New Delhi, India", locality = "New Delhi", isActive = "no",
            lat2 = "28.6139391", lng2 = "77.2090212", address2 = "New Delhi, India", locality2 = "New Delhi",
            st_date = "", st_time = "", set_loc = "pickup", payment = "booking", post_vis = "public", extra = "include";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_postbook2, container, false);

        submit = view.findViewById(R.id.login);
        spinner = view.findViewById(R.id.spinner);
        remark = view.findViewById(R.id.remark);
        startdate = view.findViewById(R.id.startdate);
        starttime = view.findViewById(R.id.starttime);
        location = view.findViewById(R.id.location);
        droplocation = view.findViewById(R.id.droplocation);

        days = view.findViewById(R.id.days);
        tourdescription = view.findViewById(R.id.tourdescription);

        amount = view.findViewById(R.id.amount);
        commission = view.findViewById(R.id.commision);

        butsecure = view.findViewById(R.id.butsecure);
        laybook = view.findViewById(R.id.laybook);

        rdbooking = view.findViewById(R.id.rdbooking);
        rdbid = view.findViewById(R.id.rdbid);
        rdpublic = view.findViewById(R.id.rdpublic);
        rdmynetwork = view.findViewById(R.id.rdmynetwork);
        rdinclusive = view.findViewById(R.id.rdinclusive);
        rdexclusive = view.findViewById(R.id.rdexclusive);

        checknego = view.findViewById(R.id.checknego);
        checkhide = view.findViewById(R.id.checkhide);
        checkdiesel = view.findViewById(R.id.checkdiesel);
        checkcarrier = view.findViewById(R.id.checkcarrier);

        loading = new Dialog(getContext());
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        addressPopUp = new Dialog(getContext());
        addressPopUp.setContentView(R.layout.popup_address);
        addressPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressRecycler = addressPopUp.findViewById(R.id.recycler);
        addressEt = addressPopUp.findViewById(R.id.searchcustomer);
        addressImage = addressPopUp.findViewById(R.id.searchimage);

        addressRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        arrAddress = new ArrayList<>();
        arrAddId = new ArrayList<>();
        addressadeptor = new addressAdeptor(arrAddress);
        addressRecycler.setAdapter(addressadeptor);

        postPopup = new Dialog(getContext());
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

        ad = new ArrayAdapter(getContext(), R.layout.simple_spinner_item, vehicle);
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

                try {
                    JSONObject body = new JSONObject();
                    body.put("app_id", "c6d75888-f3ba-4c5a-8f2c-f61700cdb3f9");

                    JSONObject headings = new JSONObject();
                    headings.put("en", "Round Trip Booking");
                    body.put("headings", headings);

                    JSONObject contents = new JSONObject();
                    contents.put("en", locality + " <---> " + locality2);
                    body.put("contents", contents);

                    JSONArray filters = new JSONArray();

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_" + locality).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_" + locality2).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("operator", "OR"));

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_all").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_" + locality2).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("operator", "OR"));

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_" + locality).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_all").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("operator", "OR"));

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_all").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_all").put("relation", "=").put("value", "true"));

                    body.put("filters", filters);

                    JSONArray buttons = new JSONArray();
                    JSONObject bookNowButton = new JSONObject();
                    bookNowButton.put("id", "book_now");
                    bookNowButton.put("text", "Book Now");
                    buttons.put(bookNowButton);
                    body.put("buttons", buttons);

                    body.put("big_picture", "https://maps.googleapis.com/maps/api/staticmap?" +
                            "size=600x300&path=color:0x0000ff|weight:5|" +
                            address +
                            "|" +
                            address2 +
                            "&markers=" +
                            address +
                            "|" +
                            address2 +
                            "&key=AIzaSyCELxmgPiNOhUWqjbFE-F-wqJIr20OY5jQ");

                    String icon = "ic_hatchback";
                    if (hrNm.equals("Hatchback")) {
                        icon = "ic_hatchback";
                    } else if (hrNm.equals("Sedan")) {
                        icon = "ic_sedan";
                    } else if (hrNm.equals("Ertiga")) {
                        icon = "ic_eartiga";
                    } else if (hrNm.equals("Suv")) {
                        icon = "ic_suv";
                    } else if (hrNm.equals("Kia Cerens")) {
                        icon = "ic_kia";
                    } else if (hrNm.equals("Innova")) {
                        icon = "ic_innova";
                    } else if (hrNm.equals("Innova Crysta")) {
                        icon = "ic_innova_crysta";
                    } else if (hrNm.equals("Innova Hycross")) {
                        icon = "ic_innova_crysta";
                    } else if (hrNm.equals("Force Traveller")) {
                        icon = "ic_force_traveller";
                    } else if (hrNm.equals("Bus")) {
                        icon = "ic_bus";
                    }

                    body.put("small_icon", "logo");
                    body.put("large_icon", icon);
                    body.put("android_channel_id", profileContainer.oneSignalChannelSound);

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(body, getContext(), getActivity());
                    notificationsSender.SendNotifications();

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(fladid).update(user);
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("postBooking")
                        .document(fladid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                profileContainer.postRefresh = "bookVehicle";
                                Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getContext(), home.class));
                                getActivity().finishAffinity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(getContext(), "your internet is not working.", Toast.LENGTH_SHORT).show();
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

                try {
                    JSONObject body = new JSONObject();
                    body.put("app_id", "c6d75888-f3ba-4c5a-8f2c-f61700cdb3f9");

                    JSONObject headings = new JSONObject();
                    headings.put("en", "Round Trip Booking");
                    body.put("headings", headings);

                    JSONObject contents = new JSONObject();
                    contents.put("en", locality + " <---> " + locality2);
                    body.put("contents", contents);

                    JSONArray filters = new JSONArray();

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_" + locality).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_" + locality2).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("operator", "OR"));

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_all").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_" + locality2).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("operator", "OR"));

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_" + locality).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_all").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("operator", "OR"));

                    filters.put(new JSONObject().put("field", "tag").put("key", "vehicle_" + hrNm).put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "trip_oneWay").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "pick_all").put("relation", "=").put("value", "true"));
                    filters.put(new JSONObject().put("field", "tag").put("key", "drop_all").put("relation", "=").put("value", "true"));

                    body.put("filters", filters);

                    JSONArray buttons = new JSONArray();
                    JSONObject bookNowButton = new JSONObject();
                    bookNowButton.put("id", "book_now");
                    bookNowButton.put("text", "Book Now");
                    buttons.put(bookNowButton);
                    body.put("buttons", buttons);

                    body.put("big_picture", "https://maps.googleapis.com/maps/api/staticmap?" +
                            "size=600x300&path=color:0x0000ff|weight:5|" +
                            address +
                            "|" +
                            address2 +
                            "&markers=" +
                            address +
                            "|" +
                            address2 +
                            "&key=AIzaSyCELxmgPiNOhUWqjbFE-F-wqJIr20OY5jQ");

                    String icon = "ic_hatchback";
                    if (hrNm.equals("Hatchback")) {
                        icon = "ic_hatchback";
                    } else if (hrNm.equals("Sedan")) {
                        icon = "ic_sedan";
                    } else if (hrNm.equals("Ertiga")) {
                        icon = "ic_eartiga";
                    } else if (hrNm.equals("Suv")) {
                        icon = "ic_suv";
                    } else if (hrNm.equals("Kia Cerens")) {
                        icon = "ic_kia";
                    } else if (hrNm.equals("Innova")) {
                        icon = "ic_innova";
                    } else if (hrNm.equals("Innova Crysta")) {
                        icon = "ic_innova_crysta";
                    } else if (hrNm.equals("Innova Hycross")) {
                        icon = "ic_innova_crysta";
                    } else if (hrNm.equals("Force Traveller")) {
                        icon = "ic_force_traveller";
                    } else if (hrNm.equals("Bus")) {
                        icon = "ic_bus";
                    }

                    body.put("small_icon", "logo");
                    body.put("large_icon", icon);
                    body.put("android_channel_id", profileContainer.oneSignalChannelSound);

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(body, getContext(), getActivity());
                    notificationsSender.SendNotifications();

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(fladid).update(user);
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("postBooking")
                        .document(fladid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                profileContainer.postRefresh = "bookVehicle";
                                Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getContext(), home.class));
                                getActivity().finishAffinity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(getContext(), "your internet is not working.", Toast.LENGTH_SHORT).show();
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
                laybook.setVisibility(view.VISIBLE);
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
                laybook.setVisibility(view.GONE);
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
                                    Toast.makeText(getContext(), "0 drivers in your network.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "your internet is not working", Toast.LENGTH_SHORT).show();
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
                        getContext(), new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                RequestQueue queue = Volley.newRequestQueue(getContext());
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
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                submit.startAnimation(animation);

                isActive = "no";
                try {
                    if (profileContainer.freeTrial.equals("yes")) {
                        isActive = "yes";
                    } else if (profileContainer.userFreeTrial != null) {
                        if (profileContainer.userFreeTrial.equals("yes")) {
                            isActive = "yes";
                        }
                    }else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                        Date now = new Date();
                        Date currentTime = sdf.parse(sdf.format(now));

                        if (profileContainer.isActive != null && profileContainer.isActive.equals("yes")) {
                            try {
                                Date expiryDate = sdf.parse(profileContainer.planExpiry);
                                if (expiryDate != null && expiryDate.after(currentTime)) {
                                    isActive = "yes";
                                }
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                if (isActive.equals("no")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Not Subscribe");
                    builder.setMessage("You're not subscribed! Unlock exclusive feature by subscribing.");
                    builder.create();
                    builder.setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            startActivity(new Intent(getContext(), subscribe.class));
                        }
                    });
                    builder.show();
                    return;
                }
                if (startdate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please select pick date", Toast.LENGTH_SHORT).show();
                } else if (starttime.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please select pick time", Toast.LENGTH_SHORT).show();
                } else if (location.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please add pickup location", Toast.LENGTH_SHORT).show();
                } else if (droplocation.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please add drop location", Toast.LENGTH_SHORT).show();
                } else if (days.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please add tour days", Toast.LENGTH_SHORT).show();
                } else if (Long.valueOf(days.getText().toString().trim()) == 0) {
                    Toast.makeText(getContext(), "Please add tour days", Toast.LENGTH_SHORT).show();
                } else {
                    if (payment.equals("booking")) {
                        if (amount.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getContext(), "Please enter booking amount", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (Double.valueOf(amount.getText().toString().trim()) == 0) {
                            Toast.makeText(getContext(), "Please enter booking amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                    String timeStamp1 = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    fladid = timeStamp1 + profileContainer.userMobileNo.substring(profileContainer.userMobileNo.length() - 4);

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
                    user.put("Description", tourdescription.getText().toString().trim());
                    user.put("TourDays", days.getText().toString().trim());
                    user.put("TimeStamp", timestmp);
                    user.put("SenderMobileNo", profileContainer.userMobileNo);
                    user.put("SenderName", profileContainer.userName);
                    user.put("AddressLat", lat);
                    user.put("AddressLng", lng);
                    user.put("AddressHash", hash);
                    user.put("TransactionId", fladid);
                    user.put("Address", address);
                    user.put("AddressCity", locality);
                    user.put("Status", "open");
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
                    user.put("BookingType", "roundWay");
                    user.put("BookingPlatform", "VPR");

                    FirebaseFirestore.getInstance().collection("LatestBookingId")
                            .document("BookingId").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String userid = documentSnapshot.getString("BookingId");

                                    long num = Long.valueOf(userid);
                                    long usid = num + 1;

                                    user.put("BookingId", String.valueOf(usid));

                                    Map<String, Object> trans1 = new HashMap<>();
                                    trans1.put("BookingId", String.valueOf(usid));

                                    FirebaseFirestore.getInstance().collection("LatestBookingId")
                                            .document("BookingId").set(trans1);
                                    FirebaseFirestore.getInstance().collection("postBooking")
                                            .document(fladid).set(user);
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(profileContainer.userMobileNo).collection("postBooking")
                                            .document(fladid).set(user);

                                    loading.dismiss();
                                    postPopup.show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(getContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            }
        });

        return view;
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
                    RequestQueue queue = Volley.newRequestQueue(getContext());
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
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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