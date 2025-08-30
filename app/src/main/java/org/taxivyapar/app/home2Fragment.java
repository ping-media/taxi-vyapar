package org.taxivyapar.app;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class home2Fragment extends Fragment {
    View view;
    TextView showAddress, showtrip, sample;
    ImageView addressImage;
    EditText addressEt;
    SwipeRefreshLayout homerefresh;
    RecyclerView addressRecycler;
    addressAdeptor addressadeptor;
    Dialog addressPopUp, loading;
    ArrayList<String> arrAddress, arrAddId;
    ConstraintLayout barloading, editaddress, edittrip;
    NestedScrollView nested;
    RecyclerView homepostscreen;
    recycleradepter adeptro;
    ArrayList<Modelpost> postmodels, data, modelpass;
    String str_trip = "all", lat = "", lng = "", address = "no", isActive = "no";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home2, container, false);

        barloading = view.findViewById(R.id.loading);

        nested = view.findViewById(R.id.nested);

        sample = view.findViewById(R.id.sample);

        homerefresh = view.findViewById(R.id.refresher);

        homepostscreen = view.findViewById(R.id.recyclercutomer);
        homepostscreen.setLayoutManager(new LinearLayoutManager(getContext()));
        postmodels = new ArrayList<>();
        data = new ArrayList<>();
        modelpass = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        homepostscreen.setAdapter(adeptro);

        showAddress = view.findViewById(R.id.location);
        editaddress = view.findViewById(R.id.editlocation);

        showtrip = view.findViewById(R.id.trip);
        edittrip = view.findViewById(R.id.edittrip);

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

//        if (profileContainer.userAddLng != null) {
//            if (!profileContainer.userAddLng.equals("")) {
//                profileContainer.lat = profileContainer.userAddLat;
//                profileContainer.lng = profileContainer.userAddLng;
//                profileContainer.address = profileContainer.userAddress;
//            }
//        }

        if (address.equals("no")) {
            showAddress.setText("All Over India");
        } else {
            showAddress.setText(address);
        }


        if (str_trip.equals("all")) {
            showtrip.setText("All Vehicles");
        } else {
            showtrip.setText(str_trip);
        }

        initial();

        homerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initial();
                homerefresh.setRefreshing(false);
            }
        });

        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (postmodels.size() < modelpass.size()) {
                        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                        int run = 50;
                        int siz = postmodels.size();
                        if ((modelpass.size() - postmodels.size()) < 50) {
                            run = modelpass.size() - postmodels.size();
                        }
                        int k = postmodels.size() + run;
                        for (int i = postmodels.size(); i < k; i++) {
                            postmodels.add(modelpass.get(i));

                        }
//                        adeptro.notifyItemRangeInserted(siz, k);
                        adeptro.preloadUserData(new Runnable() {
                            @Override
                            public void run() {
                                adeptro.notifyItemRangeInserted(siz, k);
                            }
                        });
                    }
                }
            }
        });

        edittrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                edittrip.startAnimation(animation);
                PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(getContext(), R.style.CustomPopupMenuStyle), edittrip);
                popupMenu.inflate(R.menu.vehicle_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popupMenu.setGravity(Gravity.END);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.all) {
                            str_trip = "all";
                            showtrip.setText("All Vehicles");
                            progress();
                            return true;
                        } else if (itemId == R.id.vh1) {
                            str_trip = "Hatchback";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh2) {
                            str_trip = "Sedan";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh3) {
                            str_trip = "Ertiga";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh4) {
                            str_trip = "Suv";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh9) {
                            str_trip = "Kia Cerens";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh5) {
                            str_trip = "Innova";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh6) {
                            str_trip = "Innova Crysta";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh10) {
                            str_trip = "Innova Hycross";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh7) {
                            str_trip = "Force Traveller";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        } else if (itemId == R.id.vh8) {
                            str_trip = "Bus";
                            showtrip.setText(str_trip);
                            progress();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        editaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                editaddress.startAnimation(animation);
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
                }, 300);
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
        return view;
    }

    public void progress() {
        modelpass.clear();
        sample.setVisibility(View.VISIBLE);

        if (adeptro != null) {
            adeptro.clearCache();
        }

        if (address.equals("no")) {
            for (Modelpost doc : data) {
                if (str_trip.equals("all")) {
                    modelpass.add(new Modelpost(doc.getAddress(), doc.getAddress(),
                            doc.getAddressLat(), doc.getAddressLng(),
                            doc.getEndDate(), doc.getEndTime(),
                            doc.getPickAnyLocation(), doc.getRemark(),
                            doc.getSenderMobileNo(), doc.getSenderName(),
                            doc.getStartDate(), doc.getStartTime(),
                            doc.getStatus(), doc.getTimeStamp(),
                            doc.getTransactionId(), doc.getAddressCity(), doc.getVehicleName(), 0));
                } else {
                    if (doc.getVehicleName().equals(str_trip)) {
                        modelpass.add(new Modelpost(doc.getAddress(), doc.getAddress(),
                                doc.getAddressLat(), doc.getAddressLng(),
                                doc.getEndDate(), doc.getEndTime(),
                                doc.getPickAnyLocation(), doc.getRemark(),
                                doc.getSenderMobileNo(), doc.getSenderName(),
                                doc.getStartDate(), doc.getStartTime(),
                                doc.getStatus(), doc.getTimeStamp(),
                                doc.getTransactionId(), doc.getAddressCity(), doc.getVehicleName(), 0));
                    }
                }
            }
        } else {
            GeoLocation center = new GeoLocation(Double.valueOf(lat), Double.valueOf(lng));
            for (Modelpost doc : data) {
                double lat = Double.valueOf(doc.getAddressLat());
                double lng = Double.valueOf(doc.getAddressLng());
                GeoLocation docLocation = new GeoLocation(lat, lng);
                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                if (str_trip.equals("all")) {
                    modelpass.add(new Modelpost(doc.getAddress(), doc.getAddress(),
                            doc.getAddressLat(), doc.getAddressLng(),
                            doc.getEndDate(), doc.getEndTime(),
                            doc.getPickAnyLocation(), doc.getRemark(),
                            doc.getSenderMobileNo(), doc.getSenderName(),
                            doc.getStartDate(), doc.getStartTime(),
                            doc.getStatus(), doc.getTimeStamp(),
                            doc.getTransactionId(), doc.getAddressCity(), doc.getVehicleName(), distanceInM));
                } else {
                    if (doc.getVehicleName().equals(str_trip)) {
                        modelpass.add(new Modelpost(doc.getAddress(), doc.getAddress(),
                                doc.getAddressLat(), doc.getAddressLng(),
                                doc.getEndDate(), doc.getEndTime(),
                                doc.getPickAnyLocation(), doc.getRemark(),
                                doc.getSenderMobileNo(), doc.getSenderName(),
                                doc.getStartDate(), doc.getStartTime(),
                                doc.getStatus(), doc.getTimeStamp(),
                                doc.getTransactionId(), doc.getAddressCity(), doc.getVehicleName(), distanceInM));
                    }
                }
            }
            if (!address.equals("no")) {
                Collections.sort(modelpass);
            }
        }

        postmodels.clear();
        int run = 50;
        if (modelpass.size() < 50) {
            run = modelpass.size();
        }
        for (int i = 0; i < run; i++) {
            postmodels.add(modelpass.get(i));
            sample.setVisibility(View.GONE);
        }
//        adeptro.notifyDataSetChanged();
        if (adeptro != null) {
            adeptro.preloadUserData(new Runnable() {
                @Override
                public void run() {
                    adeptro.notifyDataSetChanged();
                }
            });
        } else {
            adeptro.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initial();
    }

    private void initial() {
        barloading.setVisibility(View.VISIBLE);
        isActive = "no";
        try {
            if (profileContainer.freeTrial.equals("yes")) {
                isActive = "yes";
            } else if (profileContainer.userFreeTrial != null) {
                if (profileContainer.userFreeTrial.equals("yes")) {
                    isActive = "yes";
                }
            } else {
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

        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        FirebaseFirestore.getInstance().collection("postFreeVehicle")
                .whereGreaterThan("EndTimeStamp", timestmp)
                .orderBy("EndTimeStamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        barloading.setVisibility(View.GONE);
                        data.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            Modelpost obj = d.toObject(Modelpost.class);
                            data.add(obj);
                        }
                        progress();
                    }
                });
    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<Modelpost> postmodels;
        private java.util.Map<String, DocumentSnapshot> userDataCache = new java.util.HashMap<>();

        public recycleradepter(ArrayList<Modelpost> postmodels) {
            this.postmodels = postmodels;
        }

        public void preloadUserData(final Runnable onComplete) {
            userDataCache.clear();
            java.util.Set<String> uniqueMobileNumbers = new java.util.HashSet<>();

            // Collect unique mobile numbers
            for (Modelpost post : postmodels) {
                uniqueMobileNumbers.add(post.getSenderMobileNo());
            }

            // Handle empty list case
            if (uniqueMobileNumbers.isEmpty()) {
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }

            final int[] completedQueries = {0};
            final int totalQueries = uniqueMobileNumbers.size();

            for (String mobileNo : uniqueMobileNumbers) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(mobileNo).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userDataCache.put(mobileNo, documentSnapshot);
                                completedQueries[0]++;

                                // Only notify once when all queries are complete
                                if (completedQueries[0] == totalQueries) {
                                    if (onComplete != null) {
                                        onComplete.run();
                                    }
                                }
                            }
                        });
            }
        }

        // Method to clear cache
        public void clearCache() {
            userDataCache.clear();
        }

        // Method to update UI with user data
        private void updateUserUI(recycleradepter.postviewholder holder, DocumentSnapshot documentSnapshot) {
            if (documentSnapshot == null) return;

            holder.name.setText(documentSnapshot.getString("UserName"));

            if (documentSnapshot.getString("userCompany") != null) {
                if (!documentSnapshot.getString("userCompany").equals("")) {
                    holder.mobile.setVisibility(View.VISIBLE);
                    holder.mobile.setText(documentSnapshot.getString("userCompany"));
                }
            }

            if (documentSnapshot.getString("UserVerify") != null &&
                    documentSnapshot.getString("UserVerify").equals("yes")) {
                holder.verify.setVisibility(View.VISIBLE);
            }

            try {
                Glide.with(getContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                        .into(holder.profileimage);
            } catch (Exception e) {
            }

            double prrat = 0;
            if (documentSnapshot.get("userRating") != null) {
                prrat = ((Number) documentSnapshot.get("userRating")).doubleValue();
            }

            if (prrat > 4) {
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_24);
            } else if (prrat > 3) {
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
            } else if (prrat > 2) {
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
            } else if (prrat > 1) {
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
            } else if (prrat > 0) {
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
            } else {
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
            }
        }

        @NonNull
        @Override
        public postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_freevehicle, parent, false);
            return new postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.vehiclename.setText(postmodels.get(position).getVehicleName());
            holder.mobile.setVisibility(View.GONE);
            holder.verify.setVisibility(View.GONE);

            if (postmodels.get(position).getVehicleName().equals("Hatchback")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_hatchback);
            } else if (postmodels.get(position).getVehicleName().equals("Sedan")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_sedan);
            } else if (postmodels.get(position).getVehicleName().equals("Ertiga")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_eartiga);
            } else if (postmodels.get(position).getVehicleName().equals("Suv")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_suv);
            } else if (postmodels.get(position).getVehicleName().equals("Kia Cerens")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_kia);
            } else if (postmodels.get(position).getVehicleName().equals("Innova")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_innova);
            } else if (postmodels.get(position).getVehicleName().equals("Innova Crysta")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
            } else if (postmodels.get(position).getVehicleName().equals("Innova Hycross")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
            } else if (postmodels.get(position).getVehicleName().equals("Force Traveller")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_force_traveller);
            } else if (postmodels.get(position).getVehicleName().equals("Bus")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_bus);
            }

            holder.startdate.setText(postmodels.get(position).getStartDate()
                    + ", " + postmodels.get(position).getStartTime());
            holder.enddate.setText(postmodels.get(position).getEndDate()
                    + ", " + postmodels.get(position).getEndTime());
            holder.location.setText(postmodels.get(position).getAddress());
            holder.remark.setText("-");
            if (!postmodels.get(position).getRemark().equals("")) {
                holder.remark.setText(postmodels.get(position).getRemark());
            }
            holder.pickanylocation.setVisibility(view.GONE);
            if (postmodels.get(position).getPickAnyLocation().equals("yes")) {
                holder.pickanylocation.setVisibility(view.VISIBLE);
            }
            // Set initial values
            try {
                holder.avatar.setText(postmodels.get(position).getSenderName().substring(0, 1).toUpperCase());
            } catch (Exception e) {
                holder.avatar.setText("A");
            }
            holder.name.setText(postmodels.get(position).getSenderName());
            holder.mobile.setVisibility(View.GONE);

            // Use cached user data if available
            String mobileNo = postmodels.get(position).getSenderMobileNo();
            DocumentSnapshot cachedUserData = userDataCache.get(mobileNo);

            if (cachedUserData != null) {
                // Use cached data immediately
                updateUserUI(holder, cachedUserData);

                // Set review count from cached data
                holder.review.setText("(0 Reviews)");
                if (cachedUserData.contains("reviewCount")) {
                    int reviewCount = cachedUserData.getLong("reviewCount") != null ?
                            cachedUserData.getLong("reviewCount").intValue() : 0;
                    holder.review.setText("(" + reviewCount + " Reviews)");
                }
            } else {
                // Show fallback data
                holder.name.setText(postmodels.get(position).getSenderName());
                holder.mobile.setVisibility(View.GONE);
                holder.verify.setVisibility(View.GONE);
                holder.review.setText("(0 Reviews)");

                // Set default star ratings
                holder.rat1.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
            }

//            try {
//                holder.avatar.setText(postmodels.get(position).getSenderName().substring(0, 1).toUpperCase());
//            } catch (Exception e) {
//                holder.avatar.setText("A");
//            }
//            holder.name.setText(postmodels.get(position).getSenderName());
//            FirebaseFirestore.getInstance().collection("users")
//                    .document(postmodels.get(position).getSenderMobileNo()).get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            holder.name.setText(documentSnapshot.getString("UserName"));
//
//                            if (documentSnapshot.getString("userCompany") != null) {
//                                if (!documentSnapshot.getString("userCompany").equals("")) {
//                                    holder.mobile.setVisibility(View.VISIBLE);
//                                    holder.mobile.setText(documentSnapshot.getString("userCompany"));
//                                }
//                            }
//                            if (documentSnapshot.getString("UserVerify").equals("yes")) {
//                                holder.verify.setVisibility(View.VISIBLE);
//                            }
//                            try {
//                                Glide.with(getContext()).load(documentSnapshot.getString("UserProfileImageUri"))
//                                        .into(holder.profileimage);
//                            } catch (Exception e) {
//                            }
//                            double prrat = 0;
//                            if (documentSnapshot.get("userRating") != null) {
//                                prrat = ((Number) documentSnapshot.get("userRating")).doubleValue();
//                            }
//                            if (prrat > 4) {
//                                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat3.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat4.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat5.setBackgroundResource(R.drawable.baseline_star_24);
//                            } else if (prrat > 3) {
//                                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat3.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat4.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                            } else if (prrat > 2) {
//                                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat3.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                            } else if (prrat > 1) {
//                                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat2.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                            } else if (prrat > 0) {
//                                holder.rat1.setBackgroundResource(R.drawable.baseline_star_24);
//                                holder.rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                            } else {
//                                holder.rat1.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                                holder.rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
//                            }
//                        }
//                    });
//            holder.review.setText("(0 Reviews)");
//            FirebaseFirestore.getInstance().collection("users")
//                    .document(postmodels.get(position).getSenderMobileNo())
//                    .collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                            holder.review.setText("("+queryDocumentSnapshots.size()+" Reviews)");
//                        }
//                    });
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.call.startAnimation(animation);
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
                    try {
                        String url = "tel: " + postmodels.get(position).getSenderMobileNo();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (Exception e) {
                    }
                }
            });

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), reviewuser.class);
                    i.putExtra("custno", postmodels.get(position).getSenderMobileNo());
                    i.putExtra("custname", postmodels.get(position).getSenderName());
                    startActivity(i);
                }
            });
            holder.mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), reviewuser.class);
                    i.putExtra("custno", postmodels.get(position).getSenderMobileNo());
                    i.putExtra("custname", postmodels.get(position).getSenderName());
                    startActivity(i);
                }
            });
            holder.profileimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), reviewuser.class);
                    i.putExtra("custno", postmodels.get(position).getSenderMobileNo());
                    i.putExtra("custname", postmodels.get(position).getSenderName());
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            TextView vehiclename, startdate, enddate, location, remark, pickanylocation, mobile, name, avatar, review;
            ImageView profileimage;
            ImageView vehicleimage;
            CardView call;
            View rat1, rat2, rat3, rat4, rat5, verify;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                vehiclename = itemView.findViewById(R.id.vehiclename);
                vehicleimage = itemView.findViewById(R.id.vehicleimage);
                startdate = itemView.findViewById(R.id.startdate);
                enddate = itemView.findViewById(R.id.enddate);
                location = itemView.findViewById(R.id.location);
                remark = itemView.findViewById(R.id.remark);
                pickanylocation = itemView.findViewById(R.id.pickanylocation);
                name = itemView.findViewById(R.id.textView14);
                mobile = itemView.findViewById(R.id.mobilenumber);
                avatar = itemView.findViewById(R.id.avatar);
                profileimage = itemView.findViewById(R.id.profileimage);
                call = itemView.findViewById(R.id.call);
                rat1 = itemView.findViewById(R.id.rat1);
                rat2 = itemView.findViewById(R.id.rat2);
                rat3 = itemView.findViewById(R.id.rat3);
                rat4 = itemView.findViewById(R.id.rat4);
                rat5 = itemView.findViewById(R.id.rat5);
                verify = itemView.findViewById(R.id.view10);
                review = itemView.findViewById(R.id.review);
            }
        }

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
                                lat = jsonObject2.getString("lat");
                                lng = jsonObject2.getString("lng");
                                address = postmodels.get(position);
                                showAddress.setText(address);
                                progress();
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