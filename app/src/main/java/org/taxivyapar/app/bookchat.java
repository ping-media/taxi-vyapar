package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.getstream.photoview.dialog.PhotoViewDialog;
import io.getstream.photoview.dialog.listeners.OnImageChangeListener;

public class bookchat extends AppCompatActivity implements PaymentResultListener {
    EditText messgetext;
    View mice, back;
    ConstraintLayout loading;
    CardView send;
    SwipeRefreshLayout homerefresh;
    RecyclerView homepostscreen;
    ArrayList<Modelinfo> postmodels;
    recycleradepter adeptro;
    TextView vehiclename, startdate, bookingid, trip, location, droplocation, remark, negotiable,
            amount, commision, company;
    ImageView vehicleimage;
    Dialog load;
    LinearLayout laycommision;
    CardView hidden, paybutton, selectbutton, cancelbutton, pickupbutton, callbutton;
    TextView warring;
    ListenerRegistration transactionsrealtime, realtime;
    String transId = "", custno = "", custname = "", custurl = "", str_bkid = "", str_com = "", str_amt = "", pay_type = "pay";
    int paypost = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookchat);

        mice = findViewById(R.id.record);
        messgetext = findViewById(R.id.ccd);

        back = findViewById(R.id.view);
        loading = findViewById(R.id.loading);
        company = findViewById(R.id.company);

        send = findViewById(R.id.send);
        warring = findViewById(R.id.textView2);

        paybutton = findViewById(R.id.paybutton);
        selectbutton = findViewById(R.id.sharebutton);
        pickupbutton = findViewById(R.id.pickupbutton);
        cancelbutton = findViewById(R.id.cancelbutton);
        callbutton = findViewById(R.id.call);

        homerefresh = findViewById(R.id.refresher);
        homepostscreen = findViewById(R.id.recyclercutomer);

        vehiclename = findViewById(R.id.vehiclename);
        vehicleimage = findViewById(R.id.vehicleimage);
        startdate = findViewById(R.id.startdate);
        location = findViewById(R.id.location);
        remark = findViewById(R.id.remark);
        bookingid = findViewById(R.id.bookingid);
        trip = findViewById(R.id.trip);
        droplocation = findViewById(R.id.droplocation);
        amount = findViewById(R.id.amount);
        commision = findViewById(R.id.commision);
        hidden = findViewById(R.id.securetag);
        laycommision = findViewById(R.id.laycommi);
        negotiable = findViewById(R.id.negotiable);

        LinearLayoutManager ln = new LinearLayoutManager(bookchat.this);
        ln.setReverseLayout(true);
        homepostscreen.setLayoutManager(ln);
        postmodels = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        homepostscreen.setAdapter(adeptro);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);


        Intent i = getIntent();
        transId = i.getStringExtra("transId");
        custno = i.getStringExtra("custno");
        custname = i.getStringExtra("custname");

        initial();

        paybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                if (pay_type.equals("pay")) {
                    profileContainer.rechargeAmount = str_com;
                    checkout();
                } else {
                    startActivity(new Intent(getApplicationContext(), selectdrvier.class));
                }

            }
        });

        pickupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                Map<String, Object> user = new HashMap<>();
                user.put("Status", "pickup");
                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(transId).update(user);
                FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                        .collection("postBooking")
                        .document(transId).update(user);

                sendmsg("Booking is Pickup by Driver");

                initial();

            }
        });

        selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), selectdrvier.class));
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                AlertDialog.Builder builder = new AlertDialog.Builder(bookchat.this);
                builder.setTitle("Cancel Booking");
                builder.setMessage("Are you sure, you want to cancel this booking?");
                builder.create();
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        load.show();

                        Map<String, Object> updateBooking = new HashMap<>();
                        updateBooking.put("Status", "cancel");
                        updateBooking.put("cancelBy", profileContainer.userName);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("postBooking")
                                .document(transId).update(updateBooking);

                        db.collection("users").document(custno)
                                .collection("postBooking")
                                .document(transId).update(updateBooking);

                        sendmsg("Booking is Cancelled by Driver");

                        initial();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });


        mice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                mice.startAnimation(animation);
                startSpeechRecognition();
            }
        });

        homerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initial();
                homerefresh.setRefreshing(false);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                send.startAnimation(animation);
                if (messgetext.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "write message", Toast.LENGTH_SHORT).show();
                } else {
                    sendmsg(messgetext.getText().toString().trim());
                }
            }
        });

        callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                callbutton.startAnimation(animation);
                try {
                    String url = "tel: " + custno;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    transactionsrealtime.remove();
                } catch (Exception e) {

                }
                try {
                    realtime.remove();
                } catch (Exception e) {

                }
                bookchat.super.onBackPressed();
            }
        });
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something");

        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (resultList != null && !resultList.isEmpty()) {
                String recognizedText = resultList.get(0);
                messgetext.setText(recognizedText);
            }
        }
    }

    public void sendmsg(String msg) {
        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        String id = profileContainer.userMobileNo + "_" + timestmp;
        String currentDate = new SimpleDateFormat("dd MMM, yyyy", Locale.CANADA).format(new Date());
        String ho = new SimpleDateFormat("KK", Locale.CANADA).format(new Date());
        String min = new SimpleDateFormat("mm", Locale.CANADA).format(new Date());
        String AM = new SimpleDateFormat("a", Locale.CANADA).format(new Date());
        String currentTime;
        if (ho.equals("00")) {
            currentTime = "12" + ":" + min + " " + AM;
        } else {
            currentTime = ho + ":" + min + " " + AM;
        }
        Map<String, Object> user = new HashMap<>();
        user.put("SenderMobileNo", profileContainer.userMobileNo);
        user.put("ReceiverMobileNo", custno);
        user.put("Description", msg);
        user.put("Date", currentDate);
        user.put("Time", currentTime);
        user.put("TimeStamp", timestmp);
        user.put("Attachment1", "");
        user.put("Attachment2", "");
        user.put("Attachment3", "");
        user.put("Attachment4", "");
        user.put("Attachment5", "");
        user.put("BookingId", str_bkid);
        user.put("SenderName", profileContainer.userName);
        user.put("ReceiverName", custname);
        user.put("TransactionId", transId);
        user.put("Id", id);

        Map<String, Object> user1 = new HashMap<>();
        user1.put("SenderMobileNo", profileContainer.userMobileNo);
        user1.put("SenderName", profileContainer.userName);
        user1.put("UserName", profileContainer.userName);
        user1.put("UserPhoneNumber", profileContainer.userMobileNo);
        user1.put("UserProfileImageUri", profileContainer.userProfileImageUrl);
        user1.put("Description", msg);
        user1.put("Date", currentDate);
        user1.put("Time", currentTime);
        user1.put("TimeStamp", timestmp);
        user1.put("BookingId", str_bkid);
        user1.put("Attachment1", "");
        user1.put("Attachment2", "");
        user1.put("Attachment3", "");
        user1.put("Attachment4", "");
        user1.put("Attachment5", "");
        user1.put("TransactionId", transId);
        user1.put("Id", id);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("SenderMobileNo", profileContainer.userMobileNo);
        user2.put("SenderName", profileContainer.userName);
        user2.put("UserName", custname);
        user2.put("UserPhoneNumber", custno);
        user2.put("UserProfileImageUri", custurl);
        user2.put("Description", msg);
        user2.put("Date", currentDate);
        user2.put("Time", currentTime);
        user2.put("TimeStamp", timestmp);
        user2.put("BookingId", str_bkid);
        user2.put("Attachment1", "");
        user2.put("Attachment2", "");
        user2.put("Attachment3", "");
        user2.put("Attachment4", "");
        user2.put("Attachment5", "");
        user2.put("TransactionId", transId);
        user2.put("Id", id);

        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).collection("message").document(profileContainer.userMobileNo)
                .collection("transection").document(id).set(user);

        FirebaseFirestore.getInstance().collection("message").document(id)
                .set(user);

        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).collection("message").document(profileContainer.userMobileNo)
                .set(user1);

        FirebaseFirestore.getInstance().collection("userChat")
                .document(custno).collection("chatPost")
                .document(profileContainer.userMobileNo + "_" + str_bkid)
                .set(user1);

        FirebaseFirestore.getInstance().collection("userChat")
                .document(profileContainer.userMobileNo).collection("chatReceive")
                .document(custno + "_" + str_bkid)
                .set(user2);

        try {
            JSONObject body = new JSONObject();
            body.put("app_id", profileContainer.oneSignal);

            JSONArray externalUserIds = new JSONArray();
            externalUserIds.put(custno.replace("+91", ""));
            body.put("include_external_user_ids", externalUserIds);

            JSONObject headings = new JSONObject();
            headings.put("en", "Message From " + profileContainer.userName);
            body.put("headings", headings);

            JSONObject contents = new JSONObject();
            contents.put("en", msg);
            body.put("contents", contents);

            body.put("small_icon", "logo");
            body.put("android_channel_id", profileContainer.oneSignalChannelMsg);

            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(body,
                    getApplicationContext(), bookchat.this);
            notificationsSender.SendNotifications();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        messgetext.setText("");

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            transactionsrealtime.remove();
        } catch (Exception e) {

        }
        try {
            realtime.remove();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!profileContainer.shareDetail.equals("")) {
            String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
            String id = profileContainer.userMobileNo + "_" + timestmp;
            String currentDate = new SimpleDateFormat("dd MMM, yyyy", Locale.CANADA).format(new Date());
            String ho = new SimpleDateFormat("KK", Locale.CANADA).format(new Date());
            String min = new SimpleDateFormat("mm", Locale.CANADA).format(new Date());
            String AM = new SimpleDateFormat("a", Locale.CANADA).format(new Date());
            String currentTime;
            if (ho.equals("00")) {
                currentTime = "12" + ":" + min + " " + AM;
            } else {
                currentTime = ho + ":" + min + " " + AM;
            }
            Map<String, Object> user = new HashMap<>();
            user.put("SenderMobileNo", profileContainer.userMobileNo);
            user.put("ReceiverMobileNo", custno);
            user.put("Description", profileContainer.shareDetail);
            user.put("Date", currentDate);
            user.put("Time", currentTime);
            user.put("TimeStamp", timestmp);
            user.put("Attachment1", profileContainer.shareAttach1);
            user.put("Attachment2", profileContainer.shareAttach2);
            user.put("Attachment3", profileContainer.shareAttach3);
            user.put("Attachment4", profileContainer.shareAttach4);
            user.put("Attachment5", profileContainer.shareAttach5);
            user.put("BookingId", str_bkid);
            user.put("SenderName", profileContainer.userName);
            user.put("ReceiverName", custname);
            user.put("TransactionId", transId);
            user.put("Id", id);

            Map<String, Object> user1 = new HashMap<>();
            user1.put("SenderMobileNo", profileContainer.userMobileNo);
            user1.put("SenderName", profileContainer.userName);
            user1.put("UserName", profileContainer.userName);
            user1.put("UserPhoneNumber", profileContainer.userMobileNo);
            user1.put("UserProfileImageUri", profileContainer.userProfileImageUrl);
            user1.put("Description", profileContainer.shareDetail);
            user1.put("Date", currentDate);
            user1.put("Time", currentTime);
            user1.put("TimeStamp", timestmp);
            user1.put("BookingId", str_bkid);
            user1.put("Attachment1", profileContainer.shareAttach1);
            user1.put("Attachment2", profileContainer.shareAttach2);
            user1.put("Attachment3", profileContainer.shareAttach3);
            user1.put("Attachment4", profileContainer.shareAttach4);
            user1.put("Attachment5", profileContainer.shareAttach5);
            user1.put("TransactionId", transId);
            user1.put("Id", id);

            Map<String, Object> user2 = new HashMap<>();
            user2.put("SenderMobileNo", profileContainer.userMobileNo);
            user2.put("SenderName", profileContainer.userName);
            user2.put("UserName", custname);
            user2.put("UserPhoneNumber", custno);
            user2.put("UserProfileImageUri", custurl);
            user2.put("Description", profileContainer.shareDetail);
            user2.put("Date", currentDate);
            user2.put("Time", currentTime);
            user2.put("TimeStamp", timestmp);
            user2.put("BookingId", str_bkid);
            user2.put("Attachment1", profileContainer.shareAttach1);
            user2.put("Attachment2", profileContainer.shareAttach2);
            user2.put("Attachment3", profileContainer.shareAttach3);
            user2.put("Attachment4", profileContainer.shareAttach4);
            user2.put("Attachment5", profileContainer.shareAttach5);
            user2.put("TransactionId", transId);
            user2.put("Id", id);

            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).collection("message").document(profileContainer.userMobileNo)
                    .collection("transection").document(id).set(user);

            FirebaseFirestore.getInstance().collection("message").document(id)
                    .set(user);

            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).collection("message").document(profileContainer.userMobileNo)
                    .set(user1);

            FirebaseFirestore.getInstance().collection("userChat")
                    .document(custno).collection("chatPost")
                    .document(profileContainer.userMobileNo + "_" + str_bkid)
                    .set(user1);

            FirebaseFirestore.getInstance().collection("userChat")
                    .document(profileContainer.userMobileNo).collection("chatReceive")
                    .document(custno + "_" + str_bkid)
                    .set(user2);

            try {
                JSONObject body = new JSONObject();
                body.put("app_id", profileContainer.oneSignal);

                JSONArray externalUserIds = new JSONArray();
                externalUserIds.put(custno.replace("+91", ""));
                body.put("include_external_user_ids", externalUserIds);

                JSONObject headings = new JSONObject();
                headings.put("en", "Message From " + profileContainer.userName);
                body.put("headings", headings);

                JSONObject contents = new JSONObject();
                contents.put("en", profileContainer.shareDetail);
                body.put("contents", contents);

                body.put("small_icon", "logo");
                body.put("android_channel_id", profileContainer.oneSignalChannelMsg);

                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(body,
                        getApplicationContext(), bookchat.this);
                notificationsSender.SendNotifications();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            profileContainer.shareDetail = "";
            profileContainer.shareAttach1 = "";
            profileContainer.shareAttach2 = "";
            profileContainer.shareAttach3 = "";
            profileContainer.shareAttach4 = "";
            profileContainer.shareAttach5 = "";

            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<String> ShareDriverDetail = new ArrayList<>();
                            if (documentSnapshot.get("ShareDriverDetail") != null) {
                                ShareDriverDetail = (ArrayList<String>) documentSnapshot.get("ShareDriverDetail");
                                if (!ShareDriverDetail.contains(profileContainer.userMobileNo)) {

                                    ShareDriverDetail.add(profileContainer.userMobileNo);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("ShareDriverDetail", ShareDriverDetail);
                                    FirebaseFirestore.getInstance().collection("postBooking")
                                            .document(transId).update(user);
                                    FirebaseFirestore.getInstance().collection("users").document(custno)
                                            .collection("postBooking")
                                            .document(transId).update(user);
                                }
                            } else {
                                ShareDriverDetail.add(profileContainer.userMobileNo);
                                Map<String, Object> user = new HashMap<>();
                                user.put("ShareDriverDetail", ShareDriverDetail);
                                FirebaseFirestore.getInstance().collection("postBooking")
                                        .document(transId).update(user);
                                FirebaseFirestore.getInstance().collection("users").document(custno)
                                        .collection("postBooking")
                                        .document(transId).update(user);
                            }

                            initial();
                        }
                    });
        }


    }

    @Override
    public void onBackPressed() {
        try {
            transactionsrealtime.remove();
        } catch (Exception e) {

        }
        try {
            realtime.remove();
        } catch (Exception e) {

        }
        super.onBackPressed();
    }

    private void checkout() {
        // Show payment processing popup
        showPaymentProcessingPopup();

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", Integer.valueOf(profileContainer.rechargeAmount) * 100);
            orderRequest.put("currency", "INR");

            String url = "https://api.razorpay.com/v1/orders";
            RequestQueue requestQueue = Volley.newRequestQueue(bookchat.this);
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,
                    orderRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                profileContainer.referralNo = response.getString("id");
                                Checkout checkout = new Checkout();
                                checkout.setKeyID(profileContainer.key_razor);

                                try {
                                    JSONObject options = new JSONObject();
                                    options.put("name", "Taxi Vyapar");
                                    options.put("description", "Payment for Order " + profileContainer.referralNo);
                                    options.put("image", "https://play-lh.googleusercontent.com/t-U_JJbYnbk_abdXV5SOkti4ejILEsDjIW17zo5pcf8beTEclaM_26c4DqETXJ_-sw=w480-h960-rw");
                                    options.put("order_id", profileContainer.referralNo);
                                    options.put("currency", "INR");
                                    options.put("amount", Integer.valueOf(profileContainer.rechargeAmount) * 100);
                                    JSONObject options1 = new JSONObject();
                                    options1.put("name", profileContainer.userName);
                                    options1.put("contact", profileContainer.userMobileNo.replace("+91", ""));
                                    options.put("prefill", options1);
                                    JSONObject options2 = new JSONObject();
                                    options2.put("color", "#2B2E6E");
                                    options.put("theme", options2);
                                    checkout.open(bookchat.this, options);
                                } catch (Exception e) {
                                    Log.i("sdf", "Error in Razorpay Checkout", e);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(bookchat.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(bookchat.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("sdf", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    String keyId = profileContainer.key_razor;
                    String keySecret = profileContainer.secret_razor;
                    String credentials = keyId + ":" + keySecret;
                    String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    header.put("Authorization", "Basic " + encodedCredentials);
                    return header;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {

        }
    }

    private void initial() {
        try {
            loading.setVisibility(View.VISIBLE);
            realtime = FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                            loading.setVisibility(View.GONE);
                            if (documentSnapshot.exists()) {
                                str_bkid = documentSnapshot.getString("BookingId");
                                bookingid.setText("ID:" + documentSnapshot.getString("BookingId")
                                        + " (" + documentSnapshot.getString("BookingPlatform") + ")");

                                String date = documentSnapshot.getString("StartTimeStamp").substring(0, 10);

                                String timestmp = new SimpleDateFormat("yyyy_MM_dd", Locale.CANADA).format(new Date());

                                if (date.equals(timestmp)) {
                                    startdate.setText("Today, " + documentSnapshot.getString("StartTime"));
                                } else {
                                    startdate.setText(documentSnapshot.getString("StartDate")
                                            + ", " + documentSnapshot.getString("StartTime"));
                                }
                                vehiclename.setText(documentSnapshot.getString("VehicleName"));

                                if (documentSnapshot.getString("VehicleName").equals("Hatchback")) {
                                    vehicleimage.setImageResource(R.drawable.ic_hatchback);
                                } else if (documentSnapshot.getString("VehicleName").equals("Sedan")) {
                                    vehicleimage.setImageResource(R.drawable.ic_sedan);
                                } else if (documentSnapshot.getString("VehicleName").equals("Ertiga")) {
                                    vehicleimage.setImageResource(R.drawable.ic_eartiga);
                                } else if (documentSnapshot.getString("VehicleName").equals("Suv")) {
                                    vehicleimage.setImageResource(R.drawable.ic_suv);
                                } else if (documentSnapshot.getString("VehicleName").equals("Kia Cerens")) {
                                    vehicleimage.setImageResource(R.drawable.ic_kia);
                                } else if (documentSnapshot.getString("VehicleName").equals("Innova")) {
                                    vehicleimage.setImageResource(R.drawable.ic_innova);
                                } else if (documentSnapshot.getString("VehicleName").equals("Innova Crysta")) {
                                    vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
                                } else if (documentSnapshot.getString("VehicleName").equals("Innova Hycross")) {
                                    vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
                                } else if (documentSnapshot.getString("VehicleName").equals("Force Traveller")) {
                                    vehicleimage.setImageResource(R.drawable.ic_force_traveller);
                                } else if (documentSnapshot.getString("VehicleName").equals("Bus")) {
                                    vehicleimage.setImageResource(R.drawable.ic_bus);
                                }

                                if (documentSnapshot.getString("ProfileHide").equals("yes")) {
                                    hidden.setVisibility(View.VISIBLE);
                                } else {
                                    hidden.setVisibility(View.GONE);
                                }
                                str_com = "0";
                                str_amt = "0";

                                if (documentSnapshot.getString("PaymentSystem").equals("booking")) {

                                    str_com = documentSnapshot.getString("PaymentCommission");
                                    str_amt = documentSnapshot.getString("PaymentAmount");

                                    long amt = Long.valueOf(documentSnapshot.getString("PaymentAmount"));
                                    long com = 0;
                                    if (!documentSnapshot.getString("PaymentCommission").equals("")) {
                                        com = Long.valueOf(documentSnapshot.getString("PaymentCommission"));
                                    }
                                    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                    amount.setText("₹" + numberFormat.format(amt));
                                    commision.setText("₹" + numberFormat.format(com));
                                    laycommision.setVisibility(View.VISIBLE);

                                    if (documentSnapshot.getString("PaymentNegotiable").equals("yes")) {
                                        negotiable.setVisibility(View.VISIBLE);
                                    } else {
                                        negotiable.setVisibility(View.GONE);
                                    }
                                } else {
                                    negotiable.setVisibility(View.GONE);
                                    laycommision.setVisibility(View.GONE);
                                    amount.setText("Bid Best Price");
                                }

                                location.setText(documentSnapshot.getString("AddressCity"));
                                droplocation.setText(documentSnapshot.getString("DropAddressCity"));

                                if (documentSnapshot.getString("BookingType").equals("oneWay")) {
                                    trip.setText("One Way");
                                } else {
                                    trip.setText("Round Trip");
                                }

                                String msg = "";
                                if (documentSnapshot.getString("Diesel").equals("yes")) {
                                    msg += "only diesel, ";
                                }
                                if (documentSnapshot.getString("Carrier").equals("yes")) {
                                    msg += "with carrier, ";
                                }
                                if (documentSnapshot.getString("Extra").equals("include")) {
                                    if (!documentSnapshot.getString("Remark").equals("")) {
                                        msg += "All inclusive, " + documentSnapshot.getString("Remark");
                                    } else {
                                        msg += "All inclusive";
                                    }
                                } else {
                                    if (!documentSnapshot.getString("Remark").equals("")) {
                                        msg += "All exclusive, " + documentSnapshot.getString("Remark");
                                    } else {
                                        msg += "All exclusive";
                                    }
                                }
                                remark.setText(msg);

                                callbutton.setVisibility(View.GONE);
                                paybutton.setVisibility(View.GONE);
                                selectbutton.setVisibility(View.GONE);
                                pickupbutton.setVisibility(View.GONE);
                                cancelbutton.setVisibility(View.GONE);
                                warring.setVisibility(View.GONE);
                                bookingid.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
                                warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                if (documentSnapshot.getString("Status").equals("open")) {
                                    selectbutton.setVisibility(View.VISIBLE);
                                    if (documentSnapshot.getString("BookingSecure").equals("yes")) {
                                        if (!str_com.equals("0")) {
                                            paybutton.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Open)");

                                    if (documentSnapshot.getString("PaymentSystem").equals("bidding")) {
                                        paybutton.setVisibility(View.GONE);
                                    }

                                    if (documentSnapshot.get("ShareDriverDetail") != null) {
                                        ArrayList<String> ShareDriverDetail = (ArrayList<String>) documentSnapshot.get("ShareDriverDetail");
                                        if (ShareDriverDetail.contains(profileContainer.userMobileNo)) {
                                            pay_type = "pay";
                                        } else {
                                            pay_type = "driver";
                                        }
                                    } else {
                                        pay_type = "driver";
                                    }


                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");
                                        if (commissionRequest.contains(profileContainer.userMobileNo)) {
                                            paybutton.setVisibility(View.VISIBLE);
                                            pay_type = "pay";
                                            int ind = commissionRequest.indexOf(profileContainer.userMobileNo);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            str_amt = commissionAmount.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                    String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                                    if (timestmp1.compareTo(documentSnapshot.getString("StartTimeStamp")) > 0) {
                                        bookingid.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                        warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                        bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Expire)");
                                        warring.setVisibility(View.VISIBLE);
                                        warring.setText("*This booking is Expired");
                                        paybutton.setVisibility(View.GONE);
                                        selectbutton.setVisibility(View.GONE);
                                    }

                                } else if (documentSnapshot.getString("Status").equals("assign")) {
                                    callbutton.setVisibility(View.VISIBLE);
                                    cancelbutton.setVisibility(View.VISIBLE);
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Assign)");
                                    warring.setVisibility(View.VISIBLE);
                                    warring.setText("*This booking is assign to " + documentSnapshot.getString("BookingAssignName"));
                                    String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                                    if (timestmp1.compareTo(documentSnapshot.getString("StartTimeStamp")) > 0) {
                                        pickupbutton.setVisibility(View.VISIBLE);
                                    }
                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(profileContainer.userMobileNo)) {
                                            int ind = commissionRequest.indexOf(profileContainer.userMobileNo);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            str_amt = commissionAmount.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }

                                } else if (documentSnapshot.getString("Status").equals("pickup")) {
                                    callbutton.setVisibility(View.VISIBLE);
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Picked)");
                                    warring.setVisibility(View.VISIBLE);
                                    warring.setText("*This booking is pickup by " + documentSnapshot.getString("BookingAssignName"));


                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(profileContainer.userMobileNo)) {
                                            int ind = commissionRequest.indexOf(profileContainer.userMobileNo);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            str_amt = commissionAmount.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (documentSnapshot.getString("Status").equals("complete")) {
                                    callbutton.setVisibility(View.VISIBLE);
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Complete)");
                                    warring.setVisibility(View.VISIBLE);
                                    warring.setText("This booking is complete by " + documentSnapshot.getString("BookingAssignName"));
                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(profileContainer.userMobileNo)) {
                                            int ind = commissionRequest.indexOf(profileContainer.userMobileNo);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            str_amt = commissionAmount.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (documentSnapshot.getString("Status").equals("cancel")) {
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Cancel)");
                                    bookingid.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                    warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                    warring.setVisibility(View.VISIBLE);
                                    warring.setText("*This booking is cancelled by " + documentSnapshot.getString("cancelBy"));
                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(profileContainer.userMobileNo)) {
                                            int ind = commissionRequest.indexOf(profileContainer.userMobileNo);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            str_amt = commissionAmount.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                company.setText(custname);
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(documentSnapshot.getString("SenderMobileNo")).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                company.setText(documentSnapshot.getString("UserName"));
                                                custurl = documentSnapshot.getString("UserProfileImageUri");
                                                adeptro.notifyDataSetChanged();
                                            }
                                        });


                            } else {
                                bookchat.super.onBackPressed();
                                Toast.makeText(bookchat.this, "booking is deleted", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } catch (Exception e) {
        }


        try {
            transactionsrealtime = FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).collection("message").document(profileContainer.userMobileNo)
                    .collection("transection").orderBy("TimeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentSnapshot> list = value.getDocuments();
                            postmodels.clear();
                            paypost = -1;
                            for (DocumentSnapshot d : list) {
                                Modelinfo obj = d.toObject(Modelinfo.class);
                                postmodels.add(obj);
                            }
                            adeptro.notifyDataSetChanged();
                        }
                    });
        } catch (Exception e) {

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        load.show();
        String currentTime;
        String currentDate = new SimpleDateFormat("dd MMM, yyyy", Locale.CANADA).format(new Date());
        String ho = new SimpleDateFormat("KK", Locale.CANADA).format(new Date());
        String min = new SimpleDateFormat("mm", Locale.CANADA).format(new Date());
        String AM = new SimpleDateFormat("a", Locale.CANADA).format(new Date());
        if (ho.equals("00")) {
            currentTime = "12" + ":" + min + " " + AM;
        } else {
            currentTime = ho + ":" + min + " " + AM;
        }
        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        Map<String, Object> user = new HashMap<>();
        user.put("Amount", profileContainer.rechargeAmount);
        user.put("userMobileNo", profileContainer.userMobileNo);
        user.put("userName", profileContainer.userName);
        user.put("SenderName", profileContainer.userName);
        user.put("SenderMobileNo", profileContainer.userMobileNo);
        user.put("ReceiverName", custname);
        user.put("ReceiverMobileNo", custno);
        user.put("Type", "commissionPay");
        user.put("BookingId", str_bkid);
        user.put("TransactionId", transId);
        user.put("PaymentAmount", str_amt);
        user.put("PaymentCommission", str_com);
        user.put("description", "Booking Id: #" + str_bkid
                + "\nBooking Amount: " + str_amt
                + "\nCommission paid by you: " + profileContainer.rechargeAmount);
        user.put("Status", "accept");
        user.put("Date", currentDate);
        user.put("Time", currentTime);
        user.put("Id", profileContainer.referralNo);
        user.put("TimeStamp", timestmp);

        Map<String, Object> user1 = new HashMap<>();
        user1.put("Amount", profileContainer.rechargeAmount);
        user1.put("userMobileNo", custname);
        user1.put("userName", custno);
        user1.put("SenderName", profileContainer.userName);
        user1.put("SenderMobileNo", profileContainer.userMobileNo);
        user1.put("ReceiverName", custname);
        user1.put("ReceiverMobileNo", custno);
        user1.put("Type", "commissionReceive");
        user1.put("BookingId", str_bkid);
        user1.put("TransactionId", transId);
        user1.put("PaymentAmount", str_amt);
        user1.put("PaymentCommission", str_com);
        user1.put("description", "Booking Id: #" + str_bkid
                + "\nBooking Amount: " + str_amt
                + "\nCommission paid by Driver: " + profileContainer.rechargeAmount);
        user1.put("Status", "pending");
        user1.put("Date", currentDate);
        user1.put("Time", currentTime);
        user1.put("Id", profileContainer.referralNo);
        user1.put("TimeStamp", timestmp);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("Status", "assign");
        user2.put("BookingAssignName", profileContainer.userName);
        user2.put("BookingAssignNo", profileContainer.userMobileNo);
        user2.put("BookingAssignPayId", profileContainer.referralNo);

        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).collection("transaction")
                .document(profileContainer.referralNo).set(user);

        FirebaseFirestore.getInstance().collection("users")
                .document(custno).collection("postBooking")
                .document(transId).update(user2);

        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).update(user2);

        FirebaseFirestore.getInstance().collection("transaction")
                .document(profileContainer.referralNo).set(user);

        FirebaseFirestore.getInstance().collection("users")
                .document(custno).collection("transaction")
                .document(profileContainer.referralNo).set(user1);

        load.dismiss();

        // Show payment success popup
        showPaymentSuccessPopup();

        sendmsg("Booking commission is Paid");

        initial();
    }

    @Override
    public void onPaymentError(int i, String s) {
        // Show payment error popup
        showPaymentErrorPopup(s);
    }

    private void showPaymentSuccessPopup() {
        Dialog paymentSuccessDialog = new Dialog(this);
        paymentSuccessDialog.setContentView(R.layout.popup_payment_success);
        paymentSuccessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        paymentSuccessDialog.setCancelable(false);

        TextView successTitle = paymentSuccessDialog.findViewById(R.id.successTitle);
        TextView successMessage = paymentSuccessDialog.findViewById(R.id.successMessage);
        TextView amountText = paymentSuccessDialog.findViewById(R.id.amountText);
        TextView bookingIdText = paymentSuccessDialog.findViewById(R.id.bookingIdText);
        CardView okButton = paymentSuccessDialog.findViewById(R.id.okButton);
        ImageView successIcon = paymentSuccessDialog.findViewById(R.id.successIcon);

        // Set success icon animation
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        successIcon.startAnimation(bounceAnimation);

        // Set the payment details
        successTitle.setText("Payment Successful!");
        successMessage.setText("Your commission payment has been processed successfully.");
        amountText.setText("₹" + profileContainer.rechargeAmount);
        bookingIdText.setText("Booking ID: " + str_bkid);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                paymentSuccessDialog.dismiss();
            }
        });

        paymentSuccessDialog.show();
    }

    private void showPaymentErrorPopup(String error) {
        Dialog paymentErrorDialog = new Dialog(this);
        paymentErrorDialog.setContentView(R.layout.popup_payment_error);
        paymentErrorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        paymentErrorDialog.setCancelable(false);

        TextView errorTitle = paymentErrorDialog.findViewById(R.id.errorTitle);
        TextView errorMessage = paymentErrorDialog.findViewById(R.id.errorMessage);
        CardView retryButton = paymentErrorDialog.findViewById(R.id.retryButton);
        CardView cancelButton = paymentErrorDialog.findViewById(R.id.cancelButton);
        ImageView errorIcon = paymentErrorDialog.findViewById(R.id.errorIcon);

        // Set error icon animation
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        errorIcon.startAnimation(shakeAnimation);

        // Set the error details
        errorTitle.setText("Payment Failed");
        errorMessage.setText("Sorry, your payment could not be processed.\n\nError: " + error);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                paymentErrorDialog.dismiss();
                // Retry payment
                checkout();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                paymentErrorDialog.dismiss();
            }
        });

        paymentErrorDialog.show();
    }

    private void showPaymentProcessingPopup() {
//        Dialog processingDialog = new Dialog(this);
//        processingDialog.setContentView(R.layout.popup_payment_processing);
//        processingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        processingDialog.setCancelable(false);
//
//        ImageView processingIcon = processingDialog.findViewById(R.id.processingIcon);
//        TextView processingTitle = processingDialog.findViewById(R.id.processingTitle);
//        TextView processingMessage = processingDialog.findViewById(R.id.processingMessage);
//
//        // Set processing icon animation
//        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
//        processingIcon.startAnimation(rotateAnimation);
//
//        // Set the processing details
//        processingTitle.setText("Processing Payment");
//        processingMessage.setText("Please wait while we process your payment...");
//
//        processingDialog.show();
    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<Modelinfo> postmodels;

        public recycleradepter(ArrayList<Modelinfo> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradepter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chating, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            holder.date.setVisibility(View.GONE);
            holder.sendermsg.setVisibility(View.GONE);
            holder.receivermsg.setVisibility(View.GONE);

            holder.senderImageContainer.setVisibility(View.GONE);
            holder.receiverImageContainer.setVisibility(View.GONE);

            holder.senderadd.setVisibility(View.GONE);
            holder.receiveradd.setVisibility(View.GONE);

            holder.timedate.setText(postmodels.get(position).getTime());
            holder.timedate2.setText(postmodels.get(position).getTime());
            holder.date.setText(postmodels.get(position).getDate());

            if (position == postmodels.size() - 1) {
                holder.date.setVisibility(View.VISIBLE);
            } else {
                String date1 = postmodels.get(position + 1).getTimeStamp();
                String date2 = postmodels.get(position).getTimeStamp();
                date1 = date1.substring(0, 10);
                date2 = date2.substring(0, 10);
                try {
                    Date userDob = new SimpleDateFormat("yyyy_MM_dd").parse(date1);
                    Date curtDob = new SimpleDateFormat("yyyy_MM_dd").parse(date2);
                    long diff = userDob.getTime() - curtDob.getTime();
                    long diffInDays = diff / (24 * 60 * 60 * 1000);
                    long absDiffInDays = Math.abs(diffInDays);
                    if (absDiffInDays >= 1) {
                        holder.date.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                }
            }

            if (postmodels.get(position).getSenderMobileNo().equals(profileContainer.userMobileNo)) {
                holder.sendermsg.setVisibility(View.VISIBLE);
                holder.receivermsg.setVisibility(View.GONE);
                holder.discriptionsender.setText(postmodels.get(position).getDescription());

                holder.senderimage1.setVisibility(View.GONE);
                holder.senderimage2.setVisibility(View.GONE);
                holder.senderimage3.setVisibility(View.GONE);
                holder.senderimage4.setVisibility(View.GONE);

                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment1())
                        .into(holder.senderimage1);
                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment2())
                        .into(holder.senderimage2);
                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment3())
                        .into(holder.senderimage3);
                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment4())
                        .into(holder.senderimage4);

                if (!postmodels.get(position).getAttachment1().equals("")
                        || !postmodels.get(position).getAttachment2().equals("")
                        || !postmodels.get(position).getAttachment3().equals("")
                        || !postmodels.get(position).getAttachment4().equals("")
                        || !postmodels.get(position).getAttachment5().equals("")) {
                    holder.senderImageContainer.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment1().equals("")) {
                    holder.senderimage1.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment2().equals("")) {
                    holder.senderimage2.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment3().equals("")) {
                    holder.senderimage3.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment4().equals("")) {
                    holder.senderimage4.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment5().equals("")) {
                    holder.senderadd.setVisibility(View.VISIBLE);
                }

            } else {
                holder.sendermsg.setVisibility(View.GONE);
                holder.receivermsg.setVisibility(View.VISIBLE);
                holder.discriptionrecevier.setText(postmodels.get(position).getDescription());

                try {
                    holder.avatar.setText(company.getText().toString().trim().substring(0, 1).toUpperCase());
                } catch (Exception e) {
                    holder.avatar.setText("A");
                }
                try {
                    Glide.with(getApplicationContext()).load(custurl).into(holder.imagesender);
                } catch (Exception e) {
                }

                holder.receiverimage1.setVisibility(View.GONE);
                holder.receiverimage2.setVisibility(View.GONE);
                holder.receiverimage3.setVisibility(View.GONE);
                holder.receiverimage4.setVisibility(View.GONE);

                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment1())
                        .into(holder.receiverimage1);
                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment2())
                        .into(holder.receiverimage2);
                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment3())
                        .into(holder.receiverimage3);
                Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment4())
                        .into(holder.receiverimage4);

                if (!postmodels.get(position).getAttachment1().equals("")
                        || !postmodels.get(position).getAttachment2().equals("")
                        || !postmodels.get(position).getAttachment3().equals("")
                        || !postmodels.get(position).getAttachment4().equals("")
                        || !postmodels.get(position).getAttachment5().equals("")) {
                    holder.receiverImageContainer.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment1().equals("")) {
                    holder.receiverimage1.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment2().equals("")) {
                    holder.receiverimage2.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment3().equals("")) {
                    holder.receiverimage3.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment4().equals("")) {
                    holder.receiverimage4.setVisibility(View.VISIBLE);
                }

                if (!postmodels.get(position).getAttachment5().equals("")) {
                    holder.receiveradd.setVisibility(View.VISIBLE);
                }
            }
            holder.paycommission.setVisibility(View.GONE);
            if (paypost == -1) {
                if (postmodels.get(position).getRemark() != null) {
                    if (postmodels.get(position).getRemark().equals("paymentRequest")) {
                        if (bookingid.getText().toString().trim().contains("Open")) {
                            holder.paycommission.setVisibility(View.VISIBLE);
                            paypost = position;
                        }
                    }
                }
            } else if (paypost == position) {
                if (postmodels.get(position).getRemark() != null) {
                    if (postmodels.get(position).getRemark().equals("paymentRequest")) {
                        if (bookingid.getText().toString().trim().contains("Open")) {
                            holder.paycommission.setVisibility(View.VISIBLE);
                            paypost = position;
                        }
                    }
                }
            }

            holder.paycommission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    view.startAnimation(animation);
                    profileContainer.rechargeAmount = str_com;
                    checkout();
                }
            });
            holder.senderImageContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> imageUrls = new ArrayList<>();
                    if (!postmodels.get(position).getAttachment1().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment1());
                    }

                    if (!postmodels.get(position).getAttachment2().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment2());
                    }

                    if (!postmodels.get(position).getAttachment3().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment3());
                    }

                    if (!postmodels.get(position).getAttachment4().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment4());
                    }

                    if (!postmodels.get(position).getAttachment5().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment5());
                    }
                    new PhotoViewDialog.Builder(bookchat.this, imageUrls, (imageView, url) -> {
                        Glide.with(bookchat.this)
                                .load(url)
                                .into(imageView);
                    }).build().show();
                }
            });

            holder.receiverImageContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> imageUrls = new ArrayList<>();
                    if (!postmodels.get(position).getAttachment1().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment1());
                    }

                    if (!postmodels.get(position).getAttachment2().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment2());
                    }

                    if (!postmodels.get(position).getAttachment3().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment3());
                    }

                    if (!postmodels.get(position).getAttachment4().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment4());
                    }

                    if (!postmodels.get(position).getAttachment5().equals("")) {
                        imageUrls.add(postmodels.get(position).getAttachment5());
                    }
                    new PhotoViewDialog.Builder(bookchat.this, imageUrls, (imageView, url) -> {
                        Glide.with(bookchat.this)
                                .load(url)
                                .into(imageView);
                    }).build().show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView timedate, timedate2, discriptionsender, discriptionrecevier, date, avatar;
            ImageView imagesender;
            CardView paycommission;
            ConstraintLayout sendermsg, receivermsg, senderadd, receiveradd;
            GridLayout senderImageContainer, receiverImageContainer;
            ImageView receiverimage1, receiverimage2, receiverimage3, receiverimage4;
            ImageView senderimage1, senderimage2, senderimage3, senderimage4;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                timedate = itemView.findViewById(R.id.customertime);
                timedate2 = itemView.findViewById(R.id.customertime2);
                date = itemView.findViewById(R.id.date);
                sendermsg = itemView.findViewById(R.id.sendermsg);
                receivermsg = itemView.findViewById(R.id.receivermsg);
                discriptionsender = itemView.findViewById(R.id.discriptionsender);
                discriptionrecevier = itemView.findViewById(R.id.discriptionrecevier);
                imagesender = itemView.findViewById(R.id.profileimage);
                avatar = itemView.findViewById(R.id.avatar);
                senderImageContainer = itemView.findViewById(R.id.senderImageContainer);
                receiverImageContainer = itemView.findViewById(R.id.receiverImageContainer);
                receiverimage1 = itemView.findViewById(R.id.receiverimage1);
                receiverimage2 = itemView.findViewById(R.id.receiverimage2);
                receiverimage3 = itemView.findViewById(R.id.receiverimage3);
                receiverimage4 = itemView.findViewById(R.id.receiverimage4);
                senderimage1 = itemView.findViewById(R.id.senderimage1);
                senderimage2 = itemView.findViewById(R.id.senderimage2);
                senderimage3 = itemView.findViewById(R.id.senderimage3);
                senderimage4 = itemView.findViewById(R.id.senderimage4);
                senderadd = itemView.findViewById(R.id.senderadd);
                receiveradd = itemView.findViewById(R.id.receiveradd);
                paycommission = itemView.findViewById(R.id.addbooking);
            }
        }
    }
}