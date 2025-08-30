package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class postvehicle extends AppCompatActivity {

    View back;
    CardView submit;
    Spinner spinner;
    EditText remark;
    CheckBox check;
    TextView startdate, starttime, enddate, endtime, location;
    Dialog loading, addressPopUp;
    ImageView addressImage;
    EditText addressEt;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    ArrayList<String> arrAddress, arrAddId;
    ArrayAdapter ad;
    String hrNm = "";
    String lat = "28.6139391", lng = "77.2090212", address = "New Delhi, India", locality = "",
            st_date = "", st_time = "", ed_date = "", ed_time = "",isActive = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postvehicle);

        back = findViewById(R.id.view);

        submit = findViewById(R.id.login);
        spinner = findViewById(R.id.spinner);
        remark = findViewById(R.id.remark);
        check = findViewById(R.id.check);
        startdate = findViewById(R.id.startdate);
        starttime = findViewById(R.id.starttime);
        enddate = findViewById(R.id.enddate);
        endtime = findViewById(R.id.endtime);
        location = findViewById(R.id.location);

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

        ad = new ArrayAdapter(this, R.layout.simple_spinner_item, vehicle);
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

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        postvehicle.this, new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(postvehicle.this,
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

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        postvehicle.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ed_date = year + "_" + String.format("%02d", (month + 1)) + "_" + String.format("%02d", dayOfMonth);
                        if (month == 0) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Jan, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Jan, " + year);
                            }
                        }
                        if (month == 1) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Feb, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Feb, " + year);
                            }
                        }
                        if (month == 2) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Mar, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Mar, " + year);
                            }
                        }
                        if (month == 3) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Apr, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Apr, " + year);
                            }
                        }
                        if (month == 4) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " May, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " May, " + year);
                            }
                        }
                        if (month == 5) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Jun, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Jun, " + year);
                            }
                        }
                        if (month == 6) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Jul, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Jul, " + year);
                            }
                        }
                        if (month == 7) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Aug, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Aug, " + year);
                            }
                        }
                        if (month == 8) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Sep, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Sep, " + year);
                            }
                        }
                        if (month == 9) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Oct, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Oct, " + year);
                            }
                        }
                        if (month == 10) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Nov, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Nov, " + year);
                            }
                        }
                        if (month == 11) {
                            if (dayOfMonth < 10) {
                                enddate.setText("0" + dayOfMonth + " Dec, " + year);
                            } else {
                                enddate.setText(dayOfMonth + " Dec, " + year);
                            }
                        }

                    }
                }, year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(postvehicle.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                SimpleDateFormat format1 = new SimpleDateFormat("HH_mm_00", Locale.getDefault());
                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                calendar.set(Calendar.MINUTE, i1);
                                String formattedTime = format.format(calendar.getTime());
                                ed_time = format1.format(calendar.getTime());
                                endtime.setText(formattedTime);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                if (isActive.equals("no")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(postvehicle.this);
                    builder.setTitle("Not Subscribe");
                    builder.setMessage("You're not subscribed! Unlock exclusive feature by subscribing.");
                    builder.create();
                    builder.setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            startActivity(new Intent(getApplicationContext(), subscribe.class));
                        }
                    });
                    builder.show();
                    return;
                }

                if (startdate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(postvehicle.this, "Please add vehicle start date", Toast.LENGTH_SHORT).show();
                } else if (starttime.getText().toString().trim().isEmpty()) {
                    Toast.makeText(postvehicle.this, "Please add vehicle start time", Toast.LENGTH_SHORT).show();
                } else if (enddate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(postvehicle.this, "Please add vehicle end date", Toast.LENGTH_SHORT).show();
                } else if (endtime.getText().toString().trim().isEmpty()) {
                    Toast.makeText(postvehicle.this, "Please add vehicle end time", Toast.LENGTH_SHORT).show();
                } else if (location.getText().toString().trim().isEmpty()) {
                    Toast.makeText(postvehicle.this, "Please add vehicle location", Toast.LENGTH_SHORT).show();
                } else {
                    String st_timestamp = st_date + "_" + st_time;
                    String ed_timestamp = ed_date + "_" + ed_time;

                    SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

                    try {
                        Date date1 = format.parse(st_timestamp);
                        Date date2 = format.parse(ed_timestamp);

                        if (date1.compareTo(date2) >= 0) {
                            Toast.makeText(postvehicle.this, "invalid vehicle end time", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (Exception e) {
                        return;
                    }
                    String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                    String timeStamp1 = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    String fladid = timeStamp1 + profileContainer.userMobileNo.substring(profileContainer.userMobileNo.length() - 4);

                    loading.show();
                    String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Double.valueOf(lat),
                            Double.valueOf(lng)));
                    Map<String, Object> user = new HashMap<>();
                    user.put("VehicleName", hrNm);
                    user.put("StartDate", startdate.getText().toString().trim());
                    user.put("StartTime", starttime.getText().toString().trim());
                    user.put("StartTimeStamp", st_date + "_" + st_time);
                    user.put("EndDate", enddate.getText().toString().trim());
                    user.put("EndTime", endtime.getText().toString().trim());
                    user.put("EndTimeStamp", ed_date + "_" + ed_time);
                    user.put("Remark", remark.getText().toString().trim());
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
                    if (check.isChecked()) {
                        user.put("PickAnyLocation", "yes");
                    } else {
                        user.put("PickAnyLocation", "no");
                    }
                    FirebaseFirestore.getInstance().collection("users")
                            .document(profileContainer.userMobileNo).collection("postFreeVehicle")
                            .document(fladid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseFirestore.getInstance().collection("postFreeVehicle")
                                            .document(fladid).set(user);
                                    loading.dismiss();
                                    profileContainer.postRefresh = "freeVehicle";
                                    Toast.makeText(postvehicle.this, "Posted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), home.class));
                                    finishAffinity();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(postvehicle.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
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
                postvehicle.super.onBackPressed();
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
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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
                                            locality = component.getString("long_name");
                                            break;
                                        } else if (types.getString(j).equals("administrative_area_level_1")
                                                && cityName == null) {
                                            cityName = component.getString("long_name");
                                            locality = component.getString("long_name");
                                            break;
                                        }
                                    }
                                }
                                if (!locality.equals("")) {
                                    lat = jsonObject2.getString("lat");
                                    lng = jsonObject2.getString("lng");
                                    address = postmodels.get(position);
                                    location.setText(address);
                                }


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