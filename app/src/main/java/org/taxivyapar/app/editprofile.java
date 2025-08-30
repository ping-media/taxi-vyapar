package org.taxivyapar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class editprofile extends AppCompatActivity {
    View back;
    ImageView addressImage;
    EditText addressEt;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    ArrayList<String> arrAddress, arrAddId;
    EditText name, company, email, driving;
    TextView location, text1, text2, text3;
    CardView login, card1, card2, card3;
    Dialog loading, addressPopUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", type = "agent",
            lat = "28.6139391", lng = "77.2090212", address = "New Delhi, India", locality = "New Delhi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        back = findViewById(R.id.view);

        name = findViewById(R.id.name);
        company = findViewById(R.id.company);
        email = findViewById(R.id.email);
        driving = findViewById(R.id.driving);
        location = findViewById(R.id.location);
        login = findViewById(R.id.login);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

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

        name.setText(profileContainer.userName);

        location.setText(profileContainer.userAddress);
        email.setText(profileContainer.userEmail);
        driving.setText(profileContainer.userLicense);
        company.setText(profileContainer.userCompany);
        if (profileContainer.userType != null) {
            if (profileContainer.userType.equals("agent")) {
                type = "agent";
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text1.setTextColor(Color.WHITE);
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text2.setTextColor(Color.parseColor("#858585"));
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text3.setTextColor(Color.parseColor("#858585"));
            } else if (profileContainer.userType.equals("owner")) {
                type = "owner";
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text2.setTextColor(Color.WHITE);
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text1.setTextColor(Color.parseColor("#858585"));
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text3.setTextColor(Color.parseColor("#858585"));
            } else if (profileContainer.userType.equals("driver")) {
                type = "driver";
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text3.setTextColor(Color.WHITE);
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text2.setTextColor(Color.parseColor("#858585"));
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text1.setTextColor(Color.parseColor("#858585"));
            }
        }

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "agent";
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text1.setTextColor(Color.WHITE);
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text2.setTextColor(Color.parseColor("#858585"));
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text3.setTextColor(Color.parseColor("#858585"));
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "owner";
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text2.setTextColor(Color.WHITE);
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text1.setTextColor(Color.parseColor("#858585"));
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text3.setTextColor(Color.parseColor("#858585"));
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "driver";
                card3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FE9307")));
                text3.setTextColor(Color.WHITE);
                card2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text2.setTextColor(Color.parseColor("#858585"));
                card1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                text1.setTextColor(Color.parseColor("#858585"));
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

        addressRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrAddress.size() <= 0) {
                    addressEt.setFocusable(true);
                    addressEt.setFocusableInTouchMode(true);
                    addressEt.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(addressEt, InputMethodManager.SHOW_FORCED);
                    }
                }
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                if (name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(editprofile.this, "Please enter full name", Toast.LENGTH_SHORT).show();
                } else if (location.getText().toString().trim().isEmpty()) {
                    Toast.makeText(editprofile.this, "Please select location", Toast.LENGTH_SHORT).show();
                } else if (company.getText().toString().trim().isEmpty()) {
                    Toast.makeText(editprofile.this, "Please enter company name", Toast.LENGTH_SHORT).show();
                } else {
                    if (!email.getText().toString().trim().isEmpty()) {
                        if (!emailPattern.matches(email.getText().toString().trim())) {
                            Toast.makeText(editprofile.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    loading.show();
                    profileContainer.userAddress = address;
                    profileContainer.userAddLat = lat;
                    profileContainer.userAddLng = lng;
                    profileContainer.userAddCity = locality;
                    profileContainer.userType = type;
                    profileContainer.userCompany = company.getText().toString().trim();
                    profileContainer.userEmail = email.getText().toString().trim();
                    profileContainer.userLicense = driving.getText().toString().trim();
                    Map<String, Object> user = new HashMap<>();
                    user.put("UserAddress", profileContainer.userAddress);
                    user.put("AddressLat", profileContainer.userAddLat);
                    user.put("AddressLng", profileContainer.userAddLng);
                    user.put("AddressCity", profileContainer.userAddCity);
                    user.put("userCompany", profileContainer.userCompany);
                    user.put("userType", profileContainer.userType);
                    if (!email.getText().toString().trim().isEmpty()) {
                        user.put("UserEmail", profileContainer.userEmail);
                    }
                    if (!driving.getText().toString().trim().isEmpty()) {
                        user.put("userLicense", profileContainer.userLicense);
                    }
                    FirebaseFirestore.getInstance().collection("users")
                            .document(profileContainer.userMobileNo).update(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loading.dismiss();
                                    Toast.makeText(editprofile.this, "Profile Save.", Toast.LENGTH_SHORT).show();
                                    editprofile.super.onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(editprofile.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
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
                editprofile.super.onBackPressed();
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

                                for (int i = 0; i < addressComponents.length(); i++) {
                                    JSONObject component = addressComponents.getJSONObject(i);
                                    JSONArray types = component.getJSONArray("types");

                                    for (int j = 0; j < types.length(); j++) {
                                        if (types.getString(j).equals("locality")) {
                                            locality = component.getString("long_name");
                                            break;
                                        }
                                    }
                                }
                                lat = jsonObject2.getString("lat");
                                lng = jsonObject2.getString("lng");
                                address = postmodels.get(position);
                                location.setText(address);

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