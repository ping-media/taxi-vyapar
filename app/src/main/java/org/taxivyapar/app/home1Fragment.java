package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class home1Fragment extends Fragment {
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
    EditText bookid;
    NestedScrollView nested;
    RecyclerView homepostscreen;
    recycleradepter adeptro;
    ArrayList<ModelpostBook> postmodels, data, modelpass;
    BottomSheetDialog chatpopup;
    View closechat;
    RecyclerView recyclerchat;
    recycleradepterchat adeptrochat;
    ArrayList<Modelinfo> modelchat;
    TextView chatno;
    String str_trip = "all", lat = "", lng = "", address = "no", isActive = "no";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home1, container, false);
        barloading = view.findViewById(R.id.loading);

        nested = view.findViewById(R.id.nested);

        sample = view.findViewById(R.id.sample);

        bookid = view.findViewById(R.id.searchcustomer);
        homerefresh = view.findViewById(R.id.refresher);

        showAddress = view.findViewById(R.id.location);
        editaddress = view.findViewById(R.id.editlocation);

        showtrip = view.findViewById(R.id.trip);
        edittrip = view.findViewById(R.id.edittrip);

        homepostscreen = view.findViewById(R.id.recyclercutomer);
        homepostscreen.setLayoutManager(new LinearLayoutManager(getContext()));
        postmodels = new ArrayList<>();
        data = new ArrayList<>();
        modelpass = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        homepostscreen.setAdapter(adeptro);

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

        chatpopup = new BottomSheetDialog(getContext());
        chatpopup.setContentView(R.layout.ui_listchat);
        closechat = chatpopup.findViewById(R.id.viewmanu);
        chatno = chatpopup.findViewById(R.id.sample);
        recyclerchat = chatpopup.findViewById(R.id.recyclercutomer);

        recyclerchat.setLayoutManager(new LinearLayoutManager(getContext()));
        modelchat = new ArrayList<>();
        adeptrochat = new recycleradepterchat(modelchat);
        recyclerchat.setAdapter(adeptrochat);

        if (address.equals("no")) {
            showAddress.setText("All Over India");
        } else {
            showAddress.setText(address);
        }

        if (str_trip.equals("all")) {
            showtrip.setText("All Trip");
        } else if (str_trip.equals("oneWay")) {
            showtrip.setText("One Way");
        } else {
            showtrip.setText("Round Trip");
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

        closechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                closechat.startAnimation(animation);
                chatpopup.dismiss();
            }
        });
        bookid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bookid.getWindowToken(), 0);

                    if (isActive.equals("yes")) {
                        progress();
                    } else {
                        bookid.setText("");
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
                    }

                    return true;
                }
                return false;
            }
        });

        edittrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                edittrip.startAnimation(animation);
                PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(getContext(), R.style.CustomPopupMenuStyle), edittrip);
                popupMenu.inflate(R.menu.trip_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popupMenu.setGravity(Gravity.END);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.all) {
                            str_trip = "all";
                            showtrip.setText("All Trip");
                            progress();
                            return true;
                        } else if (itemId == R.id.oneway) {
                            str_trip = "oneWay";
                            showtrip.setText("One Way");
                            progress();
                            return true;
                        } else if (itemId == R.id.round) {
                            str_trip = "roundWay";
                            showtrip.setText("Round Trip");
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
            for (ModelpostBook doc : data) {
                if (bookid.getText().toString().trim().isEmpty()) {
                    if (str_trip.equals("all")) {
                        modelpass.add(new ModelpostBook(doc.getVehicleName(), doc.getStartDate(),
                                doc.getStartTime(), doc.getStartTimeStamp(), doc.getRemark(), doc.getDescription(),
                                doc.getTourDays(), doc.getTimeStamp(), doc.getSenderMobileNo(), doc.getSenderName(),
                                doc.getAddressLat(), doc.getAddressLng(), doc.getAddressHash(), doc.getTransactionId(),
                                doc.getAddress(), doc.getStatus(), doc.getDropAddressLat(), doc.getDropAddressLng(),
                                doc.getDropAddressHash(), doc.getDropAddress(), doc.getPaymentSystem(), doc.getPaymentAmount(),
                                doc.getPaymentCommission(), doc.getPaymentNegotiable(), doc.getBookingSecure(), doc.getProfileHide(),
                                doc.getDiesel(), doc.getCarrier(), doc.getBookingType(), doc.getBookingId(),
                                doc.getAddressCity(), doc.getDropAddressCity(), doc.getExtra(),
                                doc.getBookingPlatform(), doc.getPreferenceContact(), doc.getPreferenceDriver(),
                                0));

                    } else {
                        if (doc.getBookingType().equals(str_trip)) {
                            modelpass.add(new ModelpostBook(doc.getVehicleName(), doc.getStartDate(),
                                    doc.getStartTime(), doc.getStartTimeStamp(), doc.getRemark(), doc.getDescription(),
                                    doc.getTourDays(), doc.getTimeStamp(), doc.getSenderMobileNo(), doc.getSenderName(),
                                    doc.getAddressLat(), doc.getAddressLng(), doc.getAddressHash(), doc.getTransactionId(),
                                    doc.getAddress(), doc.getStatus(), doc.getDropAddressLat(), doc.getDropAddressLng(),
                                    doc.getDropAddressHash(), doc.getDropAddress(), doc.getPaymentSystem(), doc.getPaymentAmount(),
                                    doc.getPaymentCommission(), doc.getPaymentNegotiable(), doc.getBookingSecure(), doc.getProfileHide(),
                                    doc.getDiesel(), doc.getCarrier(), doc.getBookingType(), doc.getBookingId(),
                                    doc.getAddressCity(), doc.getDropAddressCity(), doc.getExtra(),
                                    doc.getBookingPlatform(), doc.getPreferenceContact(), doc.getPreferenceDriver(),
                                    0));
                        }
                    }
                } else {
                    if (doc.getBookingId().contains(bookid.getText().toString().trim())) {
                        modelpass.add(new ModelpostBook(doc.getVehicleName(), doc.getStartDate(),
                                doc.getStartTime(), doc.getStartTimeStamp(), doc.getRemark(), doc.getDescription(),
                                doc.getTourDays(), doc.getTimeStamp(), doc.getSenderMobileNo(), doc.getSenderName(),
                                doc.getAddressLat(), doc.getAddressLng(), doc.getAddressHash(), doc.getTransactionId(),
                                doc.getAddress(), doc.getStatus(), doc.getDropAddressLat(), doc.getDropAddressLng(),
                                doc.getDropAddressHash(), doc.getDropAddress(), doc.getPaymentSystem(), doc.getPaymentAmount(),
                                doc.getPaymentCommission(), doc.getPaymentNegotiable(), doc.getBookingSecure(), doc.getProfileHide(),
                                doc.getDiesel(), doc.getCarrier(), doc.getBookingType(), doc.getBookingId(),
                                doc.getAddressCity(), doc.getDropAddressCity(), doc.getExtra(),
                                doc.getBookingPlatform(), doc.getPreferenceContact(), doc.getPreferenceDriver(),
                                0));
                    }
                }

            }
        } else {
            GeoLocation center = new GeoLocation(Double.valueOf(lat), Double.valueOf(lng));
            for (ModelpostBook doc : data) {
                double lat = Double.valueOf(doc.getAddressLat());
                double lng = Double.valueOf(doc.getAddressLng());
                GeoLocation docLocation = new GeoLocation(lat, lng);
                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                if (bookid.getText().toString().trim().isEmpty()) {
                    if (str_trip.equals("all")) {
                        modelpass.add(new ModelpostBook(doc.getVehicleName(), doc.getStartDate(),
                                doc.getStartTime(), doc.getStartTimeStamp(), doc.getRemark(), doc.getDescription(),
                                doc.getTourDays(), doc.getTimeStamp(), doc.getSenderMobileNo(), doc.getSenderName(),
                                doc.getAddressLat(), doc.getAddressLng(), doc.getAddressHash(), doc.getTransactionId(),
                                doc.getAddress(), doc.getStatus(), doc.getDropAddressLat(), doc.getDropAddressLng(),
                                doc.getDropAddressHash(), doc.getDropAddress(), doc.getPaymentSystem(), doc.getPaymentAmount(),
                                doc.getPaymentCommission(), doc.getPaymentNegotiable(), doc.getBookingSecure(), doc.getProfileHide(),
                                doc.getDiesel(), doc.getCarrier(), doc.getBookingType(), doc.getBookingId(),
                                doc.getAddressCity(), doc.getDropAddressCity(), doc.getExtra(),
                                doc.getBookingPlatform(), doc.getPreferenceContact(), doc.getPreferenceDriver(),
                                distanceInM));

                    } else {
                        if (doc.getBookingType().equals(str_trip)) {
                            modelpass.add(new ModelpostBook(doc.getVehicleName(), doc.getStartDate(),
                                    doc.getStartTime(), doc.getStartTimeStamp(), doc.getRemark(), doc.getDescription(),
                                    doc.getTourDays(), doc.getTimeStamp(), doc.getSenderMobileNo(), doc.getSenderName(),
                                    doc.getAddressLat(), doc.getAddressLng(), doc.getAddressHash(), doc.getTransactionId(),
                                    doc.getAddress(), doc.getStatus(), doc.getDropAddressLat(), doc.getDropAddressLng(),
                                    doc.getDropAddressHash(), doc.getDropAddress(), doc.getPaymentSystem(), doc.getPaymentAmount(),
                                    doc.getPaymentCommission(), doc.getPaymentNegotiable(), doc.getBookingSecure(), doc.getProfileHide(),
                                    doc.getDiesel(), doc.getCarrier(), doc.getBookingType(), doc.getBookingId(),
                                    doc.getAddressCity(), doc.getDropAddressCity(), doc.getExtra(),
                                    doc.getBookingPlatform(), doc.getPreferenceContact(), doc.getPreferenceDriver(),
                                    distanceInM));
                        }
                    }
                } else {
                    if (doc.getBookingId().contains(bookid.getText().toString().trim())) {
                        modelpass.add(new ModelpostBook(doc.getVehicleName(), doc.getStartDate(),
                                doc.getStartTime(), doc.getStartTimeStamp(), doc.getRemark(), doc.getDescription(),
                                doc.getTourDays(), doc.getTimeStamp(), doc.getSenderMobileNo(), doc.getSenderName(),
                                doc.getAddressLat(), doc.getAddressLng(), doc.getAddressHash(), doc.getTransactionId(),
                                doc.getAddress(), doc.getStatus(), doc.getDropAddressLat(), doc.getDropAddressLng(),
                                doc.getDropAddressHash(), doc.getDropAddress(), doc.getPaymentSystem(), doc.getPaymentAmount(),
                                doc.getPaymentCommission(), doc.getPaymentNegotiable(), doc.getBookingSecure(), doc.getProfileHide(),
                                doc.getDiesel(), doc.getCarrier(), doc.getBookingType(), doc.getBookingId(),
                                doc.getAddressCity(), doc.getDropAddressCity(), doc.getExtra(),
                                doc.getBookingPlatform(), doc.getPreferenceContact(), doc.getPreferenceDriver(),
                                distanceInM));
                    }
                }

            }
            Collections.sort(modelpass);
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

        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());

        FirebaseFirestore.getInstance().collection("postBooking")
                .whereEqualTo("Status", "open")
                .whereGreaterThan("StartTimeStamp", timestmp)
                .orderBy("StartTimeStamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        barloading.setVisibility(View.GONE);
                        data.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
//                            if (!d.getString("SenderMobileNo").equals(profileContainer.userMobileNo)) {
                            if (d.getString("Network").equals("private")) {
                                ArrayList<String> net = (ArrayList<String>) d.get("NetworkContact");
                                if (net.contains(profileContainer.userMobileNo)) {
                                    ModelpostBook obj = d.toObject(ModelpostBook.class);
                                    data.add(obj);
                                }
                            } else {
                                ModelpostBook obj = d.toObject(ModelpostBook.class);
                                data.add(obj);
                            }
//                            }
                        }
                        progress();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<ModelpostBook> postmodels;
        private java.util.Map<String, DocumentSnapshot> userDataCache = new java.util.HashMap<>();

        public recycleradepter(ArrayList<ModelpostBook> postmodels) {
            this.postmodels = postmodels;
        }

        public void preloadUserData(final Runnable onComplete) {
            userDataCache.clear();
            java.util.Set<String> uniqueMobileNumbers = new java.util.HashSet<>();

            // Collect unique mobile numbers
            for (ModelpostBook post : postmodels) {
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
        private void updateUserUI(postviewholder holder, DocumentSnapshot documentSnapshot) {
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
        public recycleradepter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_booking, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.mobile.setVisibility(View.GONE);
            holder.verify.setVisibility(View.GONE);

            if (isActive.equals("yes")) {
                holder.bookingid.setText("ID: " + postmodels.get(position).getBookingId()
                        + " (" + postmodels.get(position).getBookingPlatform() + ")");
            } else {
                holder.bookingid.setText("ID: *****");
            }

            String date = postmodels.get(position).getStartTimeStamp().substring(0, 10);

            String timestmp = new SimpleDateFormat("yyyy_MM_dd", Locale.CANADA).format(new Date());

            if (date.equals(timestmp)) {
                holder.startdate.setText("Today, " + postmodels.get(position).getStartTime());
            } else {
                holder.startdate.setText(postmodels.get(position).getStartDate()
                        + ", " + postmodels.get(position).getStartTime());
            }

            holder.vehiclename.setText(postmodels.get(position).getVehicleName());

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

            if (postmodels.get(position).getProfileHide().equals("yes")) {
                holder.hidden.setVisibility(View.VISIBLE);
                holder.profileview.setVisibility(View.GONE);
            } else {
                holder.hidden.setVisibility(View.GONE);
                holder.profileview.setVisibility(View.VISIBLE);
            }

            holder.call.setVisibility(View.GONE);
            if (postmodels.get(position).getPreferenceContact().equals("call")) {
                holder.call.setVisibility(View.VISIBLE);
            }

            if (postmodels.get(position).getPaymentSystem().equals("booking")) {
                long amt = Long.valueOf(postmodels.get(position).getPaymentAmount());
                long com = 0;
                if (!postmodels.get(position).getPaymentCommission().equals("")) {
                    com = Long.valueOf(postmodels.get(position).getPaymentCommission());
                }
                long earn = amt - com;
                NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                holder.amount.setText("₹" + numberFormat.format(amt));
                holder.earning.setText("₹" + numberFormat.format(earn));
                holder.commision.setText("₹" + numberFormat.format(com));
                holder.laycommision.setVisibility(View.VISIBLE);
                holder.layearning.setVisibility(View.VISIBLE);

                if (postmodels.get(position).getPaymentNegotiable().equals("yes")) {
                    holder.negotiable.setVisibility(View.VISIBLE);
                } else {
                    holder.negotiable.setVisibility(View.GONE);
                }
            } else {
                holder.negotiable.setVisibility(View.GONE);
                holder.laycommision.setVisibility(View.GONE);
                holder.layearning.setVisibility(View.GONE);
                holder.amount.setText("Bid Best Price");
            }
            holder.location.setText(postmodels.get(position).getAddressCity());
            holder.droplocation.setText(postmodels.get(position).getDropAddressCity());
            if (postmodels.get(position).getBookingType().equals("oneWay")) {
                holder.trip.setText("――One Way―▶");
            } else {
                holder.trip.setText("◀―Round―▶");
            }

            String msg = "";
            if (postmodels.get(position).getDiesel().equals("yes")) {
                msg += "only diesel, ";
            }
            if (postmodels.get(position).getCarrier().equals("yes")) {
                msg += "with carrier, ";
            }
            if (postmodels.get(position).getExtra().equals("include")) {
                if (!postmodels.get(position).getRemark().equals("")) {
                    msg += "All inclusive, " + postmodels.get(position).getRemark();
                } else {
                    msg += "All inclusive";
                }
            } else {
                if (!postmodels.get(position).getRemark().equals("")) {
                    msg += "All exclusive, " + postmodels.get(position).getRemark();
                } else {
                    msg += "All exclusive";
                }
            }
            holder.remark.setText(msg);

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

            // Simplified review count - load once per user
            // holder.review.setText("(0 Reviews)");
            // if (cachedUserData != null && cachedUserData.contains("reviewCount")) {
            //     int reviewCount = cachedUserData.getLong("reviewCount") != null ? 
            //         cachedUserData.getLong("reviewCount").intValue() : 0;
            //     holder.review.setText("(" + reviewCount + " Reviews)");
            // } else {
            //     // Load review count only if not cached
            //     FirebaseFirestore.getInstance().collection("users")
            //             .document(mobileNo)
            //             .collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            //                 @Override
            //                 public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            //                     holder.review.setText("("+queryDocumentSnapshots.size()+" Reviews)");
            //                 }
            //             });
            // }
            holder.distance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.distance.startAnimation(animation);
                    String map = "https://www.google.com/maps/dir/?api=1" +
                            "&origin=" + postmodels.get(position).getAddressLat() + "," + postmodels.get(position).getAddressLng() +
                            "&destination=" + postmodels.get(position).getDropAddressLat() + "," + postmodels.get(position).getDropAddressLng();
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(map));
                        startActivity(i);
                    } catch (Exception e) {
                    }
                }
            });
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
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.chat.startAnimation(animation);

                    if (postmodels.get(position).getSenderMobileNo().equals(profileContainer.userMobileNo)) {
                        loading.show();
                        FirebaseFirestore.getInstance().collection("postBooking")
                                .document(postmodels.get(position).getTransactionId())
                                .collection("message")
                                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        loading.dismiss();
                                        chatno.setVisibility(View.VISIBLE);
                                        modelchat.clear();
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot d : list) {
                                            Modelinfo obj = d.toObject(Modelinfo.class);
                                            modelchat.add(obj);
                                        }
                                        adeptrochat.notifyDataSetChanged();
                                        chatpopup.show();
                                    }
                                });
                    } else {
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
//                        loading.show();
//                        FirebaseFirestore.getInstance().collection("users")
//                                .document(profileContainer.userMobileNo).collection("vehicles")
//                                .whereEqualTo("VehicleName", postmodels.get(position).getVehicleName())
//                                .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                        loading.dismiss();
//                                        if (queryDocumentSnapshots.size() > 0) {
                        Intent i = new Intent(getContext(), bookchat.class);
                        i.putExtra("transId", postmodels.get(position).getTransactionId());
                        i.putExtra("custno", postmodels.get(position).getSenderMobileNo());
                        i.putExtra("custname", postmodels.get(position).getSenderName());
                        startActivity(i);
//                                        } else {
//                                            Toast.makeText(getContext(), "No " + postmodels.get(position).getVehicleName() + " vehicle found in your account.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        loading.dismiss();
//                                        Toast.makeText(getContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });


                    }
                }
            });
            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileContainer.productId = postmodels.get(position).getTransactionId();
                    Intent i = new Intent(getContext(), bookingdetail.class);
                    i.putExtra("transId", postmodels.get(position).getTransactionId());
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
            TextView vehiclename, startdate, bookingid, trip, location, droplocation, remark, negotiable,
                    mobile, name, avatar, amount, earning, commision, review;
            ImageView profileimage, vehicleimage;
            ConstraintLayout hidden, distance, profileview;
            LinearLayout layearning, laycommision;
            CardView call, chat, securetag, detail;
            View rat1, rat2, rat3, rat4, rat5, verify;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                vehiclename = itemView.findViewById(R.id.vehiclename);
                vehicleimage = itemView.findViewById(R.id.vehicleimage);
                startdate = itemView.findViewById(R.id.startdate);
                location = itemView.findViewById(R.id.location);
                remark = itemView.findViewById(R.id.remark);
                name = itemView.findViewById(R.id.textView14);
                mobile = itemView.findViewById(R.id.mobilenumber);
                avatar = itemView.findViewById(R.id.avatar);
                profileimage = itemView.findViewById(R.id.profileimage);
                call = itemView.findViewById(R.id.call);
                chat = itemView.findViewById(R.id.chat);
                securetag = itemView.findViewById(R.id.securetag);
                bookingid = itemView.findViewById(R.id.bookingid);
                trip = itemView.findViewById(R.id.trip);
                droplocation = itemView.findViewById(R.id.droplocation);
                amount = itemView.findViewById(R.id.amount);
                earning = itemView.findViewById(R.id.earning);
                commision = itemView.findViewById(R.id.commision);
                hidden = itemView.findViewById(R.id.hidden);
                layearning = itemView.findViewById(R.id.layearning);
                laycommision = itemView.findViewById(R.id.laycommi);
                negotiable = itemView.findViewById(R.id.negotiable);
                detail = itemView.findViewById(R.id.detail);
                rat1 = itemView.findViewById(R.id.rat1);
                rat2 = itemView.findViewById(R.id.rat2);
                rat3 = itemView.findViewById(R.id.rat3);
                rat4 = itemView.findViewById(R.id.rat4);
                rat5 = itemView.findViewById(R.id.rat5);
                distance = itemView.findViewById(R.id.distance);
                verify = itemView.findViewById(R.id.view10);
                profileview = itemView.findViewById(R.id.profileview);
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
                                profileContainer.lat = jsonObject2.getString("lat");
                                profileContainer.lng = jsonObject2.getString("lng");
                                profileContainer.address = postmodels.get(position);
                                showAddress.setText(profileContainer.address);
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

    public class recycleradepterchat extends RecyclerView.Adapter<recycleradepterchat.postviewholder> {
        ArrayList<Modelinfo> postmodels;

        public recycleradepterchat(ArrayList<Modelinfo> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradepterchat.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_customer, parent, false);
            return new recycleradepterchat.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepterchat.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            chatno.setVisibility(View.GONE);
            holder.verify.setVisibility(View.GONE);

            holder.number.setText(postmodels.get(position).getDate() + ", " + postmodels.get(position).getTime());

            if (postmodels.get(position).getSenderMobileNo().equals(profileContainer.userMobileNo)) {
                holder.city.setText("you: " + postmodels.get(position).getDescription());
            } else {
                holder.city.setText(postmodels.get(position).getDescription());
            }
            holder.bookid.setText("Booking Id: " + postmodels.get(position).getBookingId());
            try {
                holder.avatar.setText(postmodels.get(position).getUserName().substring(0, 1).toUpperCase());
            } catch (Exception e) {
                holder.avatar.setText("A");
            }

            holder.detail.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(postmodels.get(position).getTransactionId())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.getString("BookingAssignNo") != null) {
                                    if (documentSnapshot.getString("BookingAssignNo")
                                            .equals(postmodels.get(position).getUserPhoneNumber())) {
                                        holder.detail.setBackgroundTintList(ColorStateList
                                                .valueOf(Color.parseColor("#CBFFCF")));
                                    }
                                }
                            }
                        }
                    });

            holder.name.setText(postmodels.get(position).getUserName());
            FirebaseFirestore.getInstance().collection("users")
                    .document(postmodels.get(position).getUserPhoneNumber()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.name.setText(documentSnapshot.getString("UserName"));
                            if (documentSnapshot.getString("UserVerify") != null) {
                                if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                    holder.verify.setVisibility(View.VISIBLE);
                                }
                            }
                            try {
                                Glide.with(getContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                        .into(holder.image);
                            } catch (Exception e) {
                            }

                        }
                    });

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.detail.startAnimation(animation);
                    Intent i = new Intent(getContext(), bookchatmy.class);
                    i.putExtra("transId", postmodels.get(position).getTransactionId());
                    i.putExtra("custno", postmodels.get(position).getUserPhoneNumber());
                    i.putExtra("custname", postmodels.get(position).getUserName());
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            ImageView image;
            ConstraintLayout detail;
            TextView name, number, city, avatar, bookid;
            View verify;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                city = itemView.findViewById(R.id.city);
                number = itemView.findViewById(R.id.mobilenumber);
                detail = itemView.findViewById(R.id.detail);
                image = itemView.findViewById(R.id.profileimage);
                avatar = itemView.findViewById(R.id.avatar);
                bookid = itemView.findViewById(R.id.bookingid);
                verify = itemView.findViewById(R.id.view10);
            }
        }

    }
}