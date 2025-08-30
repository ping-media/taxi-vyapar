package org.taxivyapar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class notifications extends AppCompatActivity {
    View back;
    RadioButton rdupi, rdbank;
    CheckBox vh1, vh2, vh3, vh4, vh5, vh6, vh7, vh8, vh9, vh10;
    CardView card1, card2, card3;
    LinearLayout notifcont;
    RecyclerView recyler, recyler2;
    ArrayList<String> arrpick, arrdrop;
    pickAdeptor pickadeptor;
    dropAdeptor dropadeptor;
    TextView location, droplocation, text1, text2, text3;
    Dialog loading, addressPopUp;
    ImageView addressImage;
    EditText addressEt;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    ArrayList<String> arrAddress, arrAddId;
    ArrayList<String> vehicle;
    String set_loc = "", locality = "", locality2 = "", str_type = "",
            vehicleTypes = "", tripType = "", pickupCities = "", dropCities = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        back = findViewById(R.id.view);

        rdupi = findViewById(R.id.rdupi);
        rdbank = findViewById(R.id.rdbank);

        notifcont = findViewById(R.id.notifcont);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        vh1 = findViewById(R.id.vh1);
        vh2 = findViewById(R.id.vh2);
        vh3 = findViewById(R.id.vh3);
        vh4 = findViewById(R.id.vh4);
        vh5 = findViewById(R.id.vh5);
        vh6 = findViewById(R.id.vh6);
        vh7 = findViewById(R.id.vh7);
        vh8 = findViewById(R.id.vh8);
        vh9 = findViewById(R.id.vh9);
        vh10 = findViewById(R.id.vh10);

        vehicle = new ArrayList<>();

        recyler = findViewById(R.id.recyclerpickup);
        recyler2 = findViewById(R.id.recyclerdrop);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(this);
        layoutManager2.setFlexDirection(FlexDirection.ROW);
        layoutManager2.setFlexWrap(FlexWrap.WRAP);

        recyler.setLayoutManager(layoutManager);
        arrpick = new ArrayList<>();
        pickadeptor = new pickAdeptor(arrpick);
        recyler.setAdapter(pickadeptor);

        recyler2.setLayoutManager(layoutManager2);
        arrdrop = new ArrayList<>();
        dropadeptor = new dropAdeptor(arrdrop);
        recyler2.setAdapter(dropadeptor);

        location = findViewById(R.id.location);
        droplocation = findViewById(R.id.droplocation);

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

        SharedPreferences gettheme = getSharedPreferences("user", MODE_PRIVATE);
        String noti = gettheme.getString("notification", "on");

        vehicleTypes = gettheme.getString("vehicle", "all");
        tripType = gettheme.getString("trip", "all");
        pickupCities = gettheme.getString("pickup", "all");
        dropCities = gettheme.getString("drop", "all");

        if (noti.equals("on")) {
            rdupi.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            rdbank.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            rdupi.setTextColor(Color.WHITE);
            rdbank.setTextColor(Color.parseColor("#858585"));
            rdupi.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            rdbank.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
            rdupi.setChecked(true);
            rdbank.setChecked(false);
            notifcont.setVisibility(View.VISIBLE);

            getNotification();
            activeNot();

        } else {
            rdbank.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
            rdupi.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            rdbank.setTextColor(Color.WHITE);
            rdupi.setTextColor(Color.parseColor("#858585"));
            rdbank.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            rdupi.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
            rdbank.setChecked(true);
            rdupi.setChecked(false);
            notifcont.setVisibility(View.GONE);

            deteleNot();
        }

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text1.setTextColor(Color.WHITE);
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text2.setTextColor(Color.parseColor("#858585"));
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text3.setTextColor(Color.parseColor("#858585"));
                str_type = "oneWay";
                activeNot();
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text2.setTextColor(Color.WHITE);
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text1.setTextColor(Color.parseColor("#858585"));
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text3.setTextColor(Color.parseColor("#858585"));
                str_type = "roundWay";
                activeNot();
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text3.setTextColor(Color.WHITE);
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text2.setTextColor(Color.parseColor("#858585"));
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text1.setTextColor(Color.parseColor("#858585"));
                str_type = "all";
                activeNot();
            }
        });

        vh1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh1.isChecked()) {
                    if (!vehicle.contains("Hatchback")) {
                        vehicle.add("Hatchback");
                    }
                } else {
                    vehicle.remove("Hatchback");
                }
                activeNot();
            }
        });
        vh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh2.isChecked()) {
                    if (!vehicle.contains("Sedan")) {
                        vehicle.add("Sedan");
                    }
                } else {
                    vehicle.remove("Sedan");
                }
                activeNot();
            }
        });
        vh3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh3.isChecked()) {
                    if (!vehicle.contains("Ertiga")) {
                        vehicle.add("Ertiga");
                    }
                } else {
                    vehicle.remove("Ertiga");
                }
                activeNot();
            }
        });
        vh4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh4.isChecked()) {
                    if (!vehicle.contains("Suv")) {
                        vehicle.add("Suv");
                    }
                } else {
                    vehicle.remove("Suv");
                }
                activeNot();
            }
        });
        vh9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh9.isChecked()) {
                    if (!vehicle.contains("Kia Cerens")) {
                        vehicle.add("Kia Cerens");
                    }
                } else {
                    vehicle.remove("Kia Cerens");
                }
                activeNot();
            }
        });
        vh5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh5.isChecked()) {
                    if (!vehicle.contains("Innova")) {
                        vehicle.add("Innova");
                    }
                } else {
                    vehicle.remove("Innova");
                }
                activeNot();
            }
        });
        vh6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh6.isChecked()) {
                    if (!vehicle.contains("Innova Crysta")) {
                        vehicle.add("Innova Crysta");
                    }
                } else {
                    vehicle.remove("Innova Crysta");
                }
                activeNot();
            }
        });
        vh10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh10.isChecked()) {
                    if (!vehicle.contains("Innova Hycross")) {
                        vehicle.add("Innova Hycross");
                    }
                } else {
                    vehicle.remove("Innova Hycross");
                }
                activeNot();
            }
        });
        vh7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh7.isChecked()) {
                    if (!vehicle.contains("Force Traveller")) {
                        vehicle.add("Force Traveller");
                    }
                } else {
                    vehicle.remove("Force Traveller");
                }
                activeNot();
            }
        });
        vh8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh8.isChecked()) {
                    if (!vehicle.contains("Bus")) {
                        vehicle.add("Bus");
                    }
                } else {
                    vehicle.remove("Bus");
                }
                activeNot();
            }
        });

        rdupi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdupi.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                rdbank.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdupi.setTextColor(Color.WHITE);
                rdbank.setTextColor(Color.parseColor("#858585"));
                rdupi.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdbank.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                SharedPreferences roomdbusermobileno = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = roomdbusermobileno.edit();
                editor.putString("notification", "on");
                editor.apply();
                notifcont.setVisibility(View.VISIBLE);
                getNotification();
                activeNot();
            }
        });

        rdbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdbank.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                rdupi.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                rdbank.setTextColor(Color.WHITE);
                rdupi.setTextColor(Color.parseColor("#858585"));
                rdbank.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                rdupi.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#858585")));
                SharedPreferences roomdbusermobileno = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = roomdbusermobileno.edit();
                editor.putString("notification", "off");
                editor.apply();
                notifcont.setVisibility(View.GONE);

                deteleNot();
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
                RequestQueue queue = Volley.newRequestQueue(notifications.this);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                notifications.super.onBackPressed();
            }
        });
    }

    public void getNotification() {
        vh1.setChecked(false);
        vh2.setChecked(false);
        vh3.setChecked(false);
        vh4.setChecked(false);
        vh5.setChecked(false);
        vh6.setChecked(false);
        vh7.setChecked(false);
        vh8.setChecked(false);
        vh9.setChecked(false);
        vh10.setChecked(false);

        vehicle.clear();
        if (vehicleTypes.equals("all")) {
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
        } else {
            if (vehicleTypes.contains(",")) {
                String[] vehicleArray = vehicleTypes.split(",");
                for (String v : vehicleArray) {
                    if (!v.trim().isEmpty()) {
                        vehicle.add(v.trim());
                    }
                }
            } else {
                vehicle.add(vehicleTypes.trim());
            }
        }

        if (vehicle.contains("Hatchback")) {
            vh1.setChecked(true);
        }
        if (vehicle.contains("Sedan")) {
            vh2.setChecked(true);
        }
        if (vehicle.contains("Ertiga")) {
            vh3.setChecked(true);
        }
        if (vehicle.contains("Suv")) {
            vh4.setChecked(true);
        }
        if (vehicle.contains("Kia Cerens")) {
            vh9.setChecked(true);
        }
        if (vehicle.contains("Innova")) {
            vh5.setChecked(true);
        }
        if (vehicle.contains("Innova Crysta")) {
            vh6.setChecked(true);
        }
        if (vehicle.contains("Innova Hycross")) {
            vh10.setChecked(true);
        }
        if (vehicle.contains("Force Traveller")) {
            vh7.setChecked(true);
        }
        if (vehicle.contains("Bus")) {
            vh8.setChecked(true);
        }

        if (tripType.equals("all")) {
            str_type = "all";
            card3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
            text3.setTextColor(Color.WHITE);
            card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            text2.setTextColor(Color.parseColor("#858585"));
            card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            text1.setTextColor(Color.parseColor("#858585"));
        } else if (tripType.equals("oneWay")) {
            str_type = "oneWay";
            card1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
            text1.setTextColor(Color.WHITE);
            card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            text2.setTextColor(Color.parseColor("#858585"));
            card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            text3.setTextColor(Color.parseColor("#858585"));
        } else if (tripType.equals("roundWay")) {
            str_type = "roundWay";
            card2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
            text2.setTextColor(Color.WHITE);
            card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            text1.setTextColor(Color.parseColor("#858585"));
            card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            text3.setTextColor(Color.parseColor("#858585"));
        }

        arrpick.clear();
        arrdrop.clear();
        if (!pickupCities.equals("all")) {
            if (pickupCities.contains(",")) {
                String[] vehicleArray = pickupCities.split(",");
                for (String v : vehicleArray) {
                    if (!v.trim().isEmpty()) {
                        arrpick.add(v.trim());
                    }
                }
            } else {
                arrpick.add(pickupCities.trim());
            }
        }

        if (!dropCities.equals("all")) {
            if (dropCities.contains(",")) {
                String[] vehicleArray = dropCities.split(",");
                for (String v : vehicleArray) {
                    if (!v.trim().isEmpty()) {
                        arrdrop.add(v.trim());
                    }
                }
            } else {
                arrdrop.add(dropCities.trim());
            }
        }
        pickadeptor.notifyDataSetChanged();
        dropadeptor.notifyDataSetChanged();

    }

    public void activeNot() {

        String setVehicle = "";
        String setTrip = str_type;
        String setPick = "";
        String setDrop = "";

        ArrayList<String> vehicle2 = new ArrayList<>();
        vehicle2.add("Hatchback");
        vehicle2.add("Sedan");
        vehicle2.add("Ertiga");
        vehicle2.add("Suv");
        vehicle2.add("Kia Cerens");
        vehicle2.add("Innova");
        vehicle2.add("Innova Crysta");
        vehicle2.add("Innova Hycross");
        vehicle2.add("Force Traveller");
        vehicle2.add("Bus");

        if (vehicle.size() <= 0) {
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
            setVehicle = "all";
        } else {
            for (String v : vehicle) {
                setVehicle = setVehicle + v + ",";
            }
            if (setVehicle.endsWith(",")) {
                setVehicle = setVehicle.substring(0, setVehicle.length() - 1);
            }
        }

        for (String v : vehicle2) {
            if(vehicle.contains(v)){
                OneSignal.sendTag("vehicle_" + v, "true");
            }else {
                OneSignal.deleteTag("vehicle_" + v);
            }

        }

        if (str_type.equals("all")) {
            OneSignal.sendTag("trip_oneWay", "true");
            OneSignal.sendTag("trip_roundWay", "true");
        } else if (str_type.equals("oneWay")) {
            OneSignal.sendTag("trip_oneWay", "true");
            OneSignal.deleteTag("trip_roundWay");
        } else {
            OneSignal.sendTag("trip_roundWay", "true");
            OneSignal.deleteTag("trip_oneWay");
        }

        if (arrpick.isEmpty()) {
            setPick = "all";
            OneSignal.sendTag("pick_all", "true");
        } else {
            OneSignal.deleteTag("pick_all");
            for (String v : arrpick) {
                OneSignal.sendTag("pick_" + v, "true");
                setPick = setPick + v + ",";
            }
            if (setPick.endsWith(",")) {
                setPick = setPick.substring(0, setPick.length() - 1);
            }
        }

        if (arrdrop.isEmpty()) {
            setDrop = "all";
            OneSignal.sendTag("drop_all", "true");
        } else {
            OneSignal.deleteTag("drop_all");
            for (String v : arrdrop) {
                OneSignal.sendTag("drop_" + v, "true");
                setDrop = setDrop + v + ",";
            }
            if (setDrop.endsWith(",")) {
                setDrop = setDrop.substring(0, setDrop.length() - 1);
            }
        }

        SharedPreferences roomdbusermobileno = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = roomdbusermobileno.edit();
        editor.putString("vehicle", setVehicle);
        editor.putString("trip", setTrip);
        editor.putString("pickup", setPick);
        editor.putString("drop", setDrop);
        editor.apply();
    }

    public void deteleNot() {
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
                    RequestQueue queue = Volley.newRequestQueue(notifications.this);
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
                                    if (!arrpick.contains(locality) && !locality.equals("")) {
                                        arrpick.add(locality);
                                        pickadeptor.notifyDataSetChanged();
                                        activeNot();
                                    }
                                } else {
                                    if (!arrdrop.contains(locality2) && !locality2.equals("")) {
                                        arrdrop.add(locality2);
                                        dropadeptor.notifyDataSetChanged();
                                        activeNot();
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

    public class pickAdeptor extends RecyclerView.Adapter<pickAdeptor.postviewholder> {
        ArrayList<String> postmodels;

        public pickAdeptor(ArrayList<String> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public pickAdeptor.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_city, parent, false);
            return new pickAdeptor.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull pickAdeptor.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            holder.name.setText(postmodels.get(position));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    arrpick.remove(position);
                    pickadeptor.notifyDataSetChanged();
                    deteleNot();
                    activeNot();
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView name;
            View delete;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                delete = itemView.findViewById(R.id.view);
            }
        }

    }

    public class dropAdeptor extends RecyclerView.Adapter<dropAdeptor.postviewholder> {
        ArrayList<String> postmodels;

        public dropAdeptor(ArrayList<String> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public dropAdeptor.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_city, parent, false);
            return new dropAdeptor.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull dropAdeptor.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            holder.name.setText(postmodels.get(position));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    arrdrop.remove(position);
                    dropadeptor.notifyDataSetChanged();
                    deteleNot();
                    activeNot();
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView name;
            View delete;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                delete = itemView.findViewById(R.id.view);
            }
        }

    }
}