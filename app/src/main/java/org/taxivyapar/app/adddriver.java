package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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
import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class adddriver extends AppCompatActivity {
    View back;
    CardView login;
    EditText name, number, company, adhar;
    TextView location;
    Dialog loading, addressPopUp;
    ImageView profileimage, addressImage, rcfront, rcback, adharfront, adharback;
    EditText addressEt;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    ArrayList<String> arrAddress, arrAddId;

    String address = "New Delhi, India", locality = "New Delhi", lat = "28.6139391", lng = "77.2090212",
            timestmp = "", transId = "", attachment = "", type = "", rcattachment1 = "", rcattachment2 = "",
            adharattachment1 = "", adharattachment2 = "";

    Uri imageuri;
    StorageReference storageReference;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddriver);

        back = findViewById(R.id.view);

        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        company = findViewById(R.id.company);
        location = findViewById(R.id.location);
        adhar = findViewById(R.id.adhar);

        profileimage = findViewById(R.id.profileimage);
        rcfront = findViewById(R.id.rcfont);
        rcback = findViewById(R.id.rcback);
        adharfront = findViewById(R.id.adharfont);
        adharback = findViewById(R.id.adharback);

        login = findViewById(R.id.login);

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

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        transId = profileContainer.userMobileNo + "_" + timestmp;

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                imageuri = result.getUriContent();
                updaloadingprofile();
            } else {
                Exception exception = result.getError();
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

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "profile";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        rcfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "licfront";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        rcback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "licback";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        adharfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "adharfront";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        adharback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "adharback";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                String licenseNumber = company.getText().toString().trim();
                boolean isValid = licenseNumber.toUpperCase().matches("^[A-Z]{2}[0-9]{2,4}[0-9]{5,10}$");

                if (name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(adddriver.this, "Please enter name", Toast.LENGTH_SHORT).show();
                } else if (number.getText().toString().trim().isEmpty()) {
                    Toast.makeText(adddriver.this, "Please enter number", Toast.LENGTH_SHORT).show();
                } else if (number.getText().toString().length() != 10) {
                    Toast.makeText(adddriver.this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                } else if (location.getText().toString().trim().isEmpty()) {
                    Toast.makeText(adddriver.this, "Please enter address", Toast.LENGTH_SHORT).show();
                } else if (attachment.equals("")) {
                    Toast.makeText(adddriver.this, "Upload profile image", Toast.LENGTH_SHORT).show();
                } else if (company.getText().toString().trim().isEmpty()) {
                    Toast.makeText(adddriver.this, "Please enter license number", Toast.LENGTH_SHORT).show();
                } else if (!isValid) {
                    Toast.makeText(adddriver.this, "Invalid license number", Toast.LENGTH_SHORT).show();
                } else if (rcattachment1.equals("")) {
                    Toast.makeText(adddriver.this, "Upload License font image", Toast.LENGTH_SHORT).show();
                } else if (rcattachment2.equals("")) {
                    Toast.makeText(adddriver.this, "Upload License back image", Toast.LENGTH_SHORT).show();
                } else if (adhar.getText().toString().trim().isEmpty()) {
                    Toast.makeText(adddriver.this, "Please enter Aadhar number", Toast.LENGTH_SHORT).show();
                } else if (adhar.getText().toString().length() != 12) {
                    Toast.makeText(adddriver.this, "Invalid Aadhar number", Toast.LENGTH_SHORT).show();
                } else if (adharattachment1.equals("")) {
                    Toast.makeText(adddriver.this, "Upload Aadhar font image", Toast.LENGTH_SHORT).show();
                } else if (adharattachment2.equals("")) {
                    Toast.makeText(adddriver.this, "Upload Aadhar back image", Toast.LENGTH_SHORT).show();
                } else {
                    String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());

                    String mob = "+91" + number.getText().toString().trim();
                    String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Double.valueOf(lat),
                            Double.valueOf(lng)));

                    Map<String, Object> user = new HashMap<>();
                    user.put("UserName", profileContainer.userName);
                    user.put("SenderName", name.getText().toString().trim());
                    user.put("UserPhoneNumber", profileContainer.userMobileNo);
                    user.put("SenderMobileNo", mob);
                    user.put("LicenseNumber", company.getText().toString().trim().toUpperCase());
                    user.put("LicenseFront", rcattachment1);
                    user.put("LicenseBack", rcattachment2);
                    user.put("AdharNumber", adhar.getText().toString().trim().toUpperCase());
                    user.put("AdharFront", adharattachment1);
                    user.put("AdharBack", adharattachment2);
                    user.put("Attachment1", attachment);
                    user.put("AddressLat", lat);
                    user.put("AddressLng", lng);
                    user.put("AddressHash", hash);
                    user.put("Address", address);
                    user.put("AddressCity", locality);
                    user.put("TimeStamp", timestmp);

                    loading.show();

                    FirebaseFirestore.getInstance().collection("drivers").document(mob)
                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loading.dismiss();
                                    Toast.makeText(adddriver.this, "driver add successfully.", Toast.LENGTH_SHORT).show();
                                    adddriver.super.onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(adddriver.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
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
                adddriver.super.onBackPressed();
            }
        });
    }

    private void updaloadingprofile() {
        String filename = transId + "_" + type;
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImage/" + profileContainer.userMobileNo + "/" + filename);
        loading.show();
        storageReference.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                loading.dismiss();
                                if (type.equals("profile")) {
                                    attachment = uri.toString();
                                } else if (type.equals("licfront")) {
                                    rcattachment1 = uri.toString();
                                } else if (type.equals("licback")) {
                                    rcattachment2 = uri.toString();
                                } else if (type.equals("adharfront")) {
                                    adharattachment1 = uri.toString();
                                } else if (type.equals("adharback")) {
                                    adharattachment2 = uri.toString();
                                }

                                Glide.with(adddriver.this).load(attachment).into(profileimage);
                                Glide.with(adddriver.this).load(rcattachment1).into(rcfront);
                                Glide.with(adddriver.this).load(rcattachment2).into(rcback);
                                Glide.with(adddriver.this).load(adharattachment1).into(adharfront);
                                Glide.with(adddriver.this).load(adharattachment2).into(adharback);

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "Uploading Error!", Toast.LENGTH_SHORT).show();
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