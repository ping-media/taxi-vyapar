package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.getstream.photoview.dialog.PhotoViewDialog;

public class bookchatmy extends AppCompatActivity {
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
    CardView hidden, paybutton, cancelbutton, pickupbutton, endbutton, callbutton;
    TextView warring;
    ListenerRegistration transactionsrealtime, realtime;
    String transId = "", custno = "", custname = "", custurl = "", str_bkid = "", str_com = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookchatmy);

        mice = findViewById(R.id.record);
        messgetext = findViewById(R.id.ccd);

        back = findViewById(R.id.view);
        loading = findViewById(R.id.loading);
        company = findViewById(R.id.company);

        send = findViewById(R.id.send);
        warring = findViewById(R.id.textView2);
        paybutton = findViewById(R.id.paybutton);
        pickupbutton = findViewById(R.id.pickupbutton);
        endbutton = findViewById(R.id.endbutton);
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

        LinearLayoutManager ln = new LinearLayoutManager(bookchatmy.this);
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

        paybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                Intent i = new Intent(getApplicationContext(), requestpay.class);
                i.putExtra("transId", transId);
                i.putExtra("custno", custno);
                i.putExtra("custname", custname);
                startActivity(i);
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

                sendmsg("Booking is Pickup by Agent");

                initial();
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                AlertDialog.Builder builder = new AlertDialog.Builder(bookchatmy.this);
                builder.setTitle("Cancel Booking");
                builder.setMessage("Are you sure, you want to cancel this booking?");
                builder.create();
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        load.show();
                        FirebaseFirestore.getInstance().collection("postBooking")
                                .document(transId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        load.dismiss();

                                        if (documentSnapshot.getString("Status").equals("assign")
                                                || documentSnapshot.getString("Status").equals("open")
                                                || documentSnapshot.getString("Status").equals("pickup")) {

                                            Map<String, Object> updateBooking = new HashMap<>();
                                            updateBooking.put("Status", "cancel");
                                            updateBooking.put("cancelBy", profileContainer.userName);

                                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                                            db.collection("postBooking")
                                                    .document(transId).update(updateBooking);

                                            db.collection("users").document(profileContainer.userMobileNo)
                                                    .collection("postBooking")
                                                    .document(transId).update(updateBooking);

                                            Map<String, Object> updateTransaction = new HashMap<>();
                                            updateTransaction.put("Status", "refund");

                                            Map<String, Object> updateTransaction1 = new HashMap<>();
                                            updateTransaction1.put("Status", "cancel");

                                            if (documentSnapshot.getString("BookingAssignNo") != null) {
                                                db.collection("users")
                                                        .document(documentSnapshot.getString("BookingAssignNo"))
                                                        .collection("transaction")
                                                        .document(documentSnapshot.getString("BookingAssignPayId"))
                                                        .update(updateTransaction);

                                                db.collection("transaction")
                                                        .document(documentSnapshot.getString("BookingAssignPayId"))
                                                        .update(updateTransaction);

                                                db.collection("users")
                                                        .document(profileContainer.userMobileNo)
                                                        .collection("transaction")
                                                        .document(documentSnapshot.getString("BookingAssignPayId"))
                                                        .update(updateTransaction1);

                                                db.runTransaction(new Transaction.Function<Void>() {
                                                    @Override
                                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                        DocumentReference userRef = db.collection("users")
                                                                .document(documentSnapshot.getString("BookingAssignNo"));
                                                        DocumentSnapshot snapshot = transaction.get(userRef);

                                                        double currentWallet = 0.0;
                                                        if (snapshot.contains("userWallet") && snapshot.getDouble("userWallet") != null) {
                                                            currentWallet = snapshot.getDouble("userWallet");
                                                        }

                                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");

                                                        if (commissionRequest.contains(documentSnapshot.getString("BookingAssignNo"))) {
                                                            int ind = commissionRequest.indexOf(documentSnapshot.getString("BookingAssignNo"));
                                                            double commissionValue = Double.parseDouble(CommissionPay.get(ind));
                                                            transaction.update(userRef, "userWallet", currentWallet + commissionValue);
                                                        }

                                                        sendmsg("Booking is Cancelled by Agent");

                                                        return null;
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        initial();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                            } else {
                                                initial();
                                            }

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        load.dismiss();
                                        Toast.makeText(getApplicationContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

        endbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);
                Intent i = new Intent(getApplicationContext(), send_review.class);
                i.putExtra("transId", transId);
                i.putExtra("type", "driver");
                i.putExtra("custno", custno);
                i.putExtra("custname", custname);
                startActivity(i);
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
                bookchatmy.super.onBackPressed();
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
        user1.put("UserName", custname);
        user1.put("UserPhoneNumber", custno);
        user1.put("UserProfileImageUri", custurl);
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
        user2.put("UserName", profileContainer.userName);
        user2.put("UserPhoneNumber", profileContainer.userMobileNo);
        user2.put("UserProfileImageUri", profileContainer.userProfileImageUrl);
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
                .document(transId).collection("message").document(custno)
                .collection("transection").document(id).set(user);

        FirebaseFirestore.getInstance().collection("message").document(id)
                .set(user);

        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).collection("message").document(custno)
                .set(user1);

        FirebaseFirestore.getInstance().collection("userChat")
                .document(profileContainer.userMobileNo).collection("chatPost")
                .document(custno + "_" + str_bkid)
                .set(user1);

        FirebaseFirestore.getInstance().collection("userChat")
                .document(custno).collection("chatReceive")
                .document(profileContainer.userMobileNo + "_" + str_bkid)
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
                    getApplicationContext(), bookchatmy.this);
            notificationsSender.SendNotifications();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        messgetext.setText("");
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
            user.put("Remark", "paymentRequest");
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
            user1.put("UserName", custname);
            user1.put("UserPhoneNumber", custno);
            user1.put("Remark", "paymentRequest");
            user1.put("UserProfileImageUri", custurl);
            user1.put("Description", profileContainer.shareDetail);
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
            user2.put("UserName", profileContainer.userName);
            user2.put("UserPhoneNumber", profileContainer.userMobileNo);
            user2.put("UserProfileImageUri", profileContainer.userProfileImageUrl);
            user2.put("Description", profileContainer.shareDetail);
            user2.put("Remark", "paymentRequest");
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
                    .document(transId).collection("message").document(custno)
                    .collection("transection").document(id).set(user);

            FirebaseFirestore.getInstance().collection("message").document(id)
                    .set(user);

            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).collection("message").document(custno)
                    .set(user1);

            FirebaseFirestore.getInstance().collection("userChat")
                    .document(profileContainer.userMobileNo).collection("chatPost")
                    .document(custno + "_" + str_bkid)
                    .set(user1);

            FirebaseFirestore.getInstance().collection("userChat")
                    .document(custno).collection("chatReceive")
                    .document(profileContainer.userMobileNo + "_" + str_bkid)
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
                        getApplicationContext(), bookchatmy.this);
                notificationsSender.SendNotifications();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            profileContainer.shareDetail = "";
        }

        initial();
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

                                if (documentSnapshot.getString("PaymentSystem").equals("booking")) {
                                    str_com = documentSnapshot.getString("PaymentCommission");
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


                                paybutton.setVisibility(View.GONE);
                                pickupbutton.setVisibility(View.GONE);
                                cancelbutton.setVisibility(View.GONE);
                                endbutton.setVisibility(View.GONE);
                                warring.setVisibility(View.GONE);
                                bookingid.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
                                warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                if (documentSnapshot.getString("Status").equals("open")) {
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Open)");

                                    paybutton.setVisibility(View.VISIBLE);
                                    cancelbutton.setVisibility(View.VISIBLE);

                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(custno)) {
                                            int ind = commissionRequest.indexOf(custno);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                    String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                                    if (timestmp1.compareTo(documentSnapshot.getString("StartTimeStamp")) > 0) {
                                        cancelbutton.setVisibility(View.GONE);
                                        paybutton.setVisibility(View.GONE);
                                        bookingid.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                        bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Expire)");
                                        warring.setVisibility(View.VISIBLE);
                                        warring.setText("*This booking is Expired");
                                        warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                    }
                                } else if (documentSnapshot.getString("Status").equals("assign")) {
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

                                        if (commissionRequest.contains(custno)) {
                                            int ind = commissionRequest.indexOf(custno);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (documentSnapshot.getString("Status").equals("pickup")) {
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Picked)");
                                    warring.setVisibility(View.VISIBLE);
                                    warring.setText("*This booking is pickup by " + documentSnapshot.getString("BookingAssignName"));
                                    endbutton.setVisibility(View.VISIBLE);
                                    cancelbutton.setVisibility(View.VISIBLE);

                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(custno)) {
                                            int ind = commissionRequest.indexOf(custno);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (documentSnapshot.getString("Status").equals("complete")) {
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Complete)");
                                    warring.setText("*This booking is complete by " + documentSnapshot.getString("BookingAssignName"));
                                    warring.setVisibility(View.VISIBLE);
                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(custno)) {
                                            int ind = commissionRequest.indexOf(custno);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                } else if (documentSnapshot.getString("Status").equals("cancel")) {
                                    bookingid.setText("ID:" + documentSnapshot.getString("BookingId") + " (Cancel)");
                                    bookingid.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                    warring.setText("*This booking is cancelled by " + documentSnapshot.getString("cancelBy"));
                                    warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                                    warring.setVisibility(View.VISIBLE);
                                    if (documentSnapshot.get("CommissionRequest") != null) {
                                        ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                        ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                                        ArrayList<String> commissionAmount = (ArrayList<String>) documentSnapshot.get("CommissionAmount");

                                        if (commissionRequest.contains(custno)) {
                                            int ind = commissionRequest.indexOf(custno);
                                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                                            amount.setText("₹" + numberFormat.format(Double.valueOf(commissionAmount.get(ind))));
                                            commision.setText("₹" + numberFormat.format(Double.valueOf(CommissionPay.get(ind))));
                                            laycommision.setVisibility(View.VISIBLE);
                                            str_com = CommissionPay.get(ind);
                                            negotiable.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                company.setText(custname);
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(custno).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                company.setText(documentSnapshot.getString("UserName"));
                                                if (documentSnapshot.getString("userCompany") != null) {
                                                    if (!documentSnapshot.getString("userCompany").equals("")) {
                                                        company.setText(documentSnapshot.getString("userCompany"));
                                                    }
                                                }
                                                custurl = documentSnapshot.getString("UserProfileImageUri");
                                                adeptro.notifyDataSetChanged();
                                            }
                                        });

//                                if (documentSnapshot.getString("Status").equals("complete")) {
//                                    if (documentSnapshot.getString("ReviewAgent") == null) {
//                                        reviewDialog.show();
//                                    }
//                                }

                            }
                        }
                    });
        } catch (Exception e) {

        }

        try {
            transactionsrealtime = FirebaseFirestore.getInstance().collection("postBooking")
                    .document(transId).collection("message").document(custno)
                    .collection("transection").orderBy("TimeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentSnapshot> list = value.getDocuments();
                            postmodels.clear();
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
                    new PhotoViewDialog.Builder(bookchatmy.this, imageUrls, (imageView, url) -> {
                        Glide.with(bookchatmy.this)
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
                    new PhotoViewDialog.Builder(bookchatmy.this, imageUrls, (imageView, url) -> {
                        Glide.with(bookchatmy.this)
                                .load(url)
                                .into(imageView);
                    }).build().show();
                }
            });

//            holder.senderimage1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment1();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//            holder.senderimage2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment2();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//            holder.senderimage3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment3();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//            holder.senderimage4.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment4();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//
//            holder.receiverimage1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment1();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//            holder.receiverimage2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment2();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//            holder.receiverimage3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment3();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
//            holder.receiverimage4.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
//                    view.startAnimation(animation);
//                    profileContainer.imageviewuser = "D";
//                    profileContainer.imageviewuserurl = postmodels.get(position).getAttachment4();
//                    startActivity(new Intent(getApplicationContext(), imageview.class));
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView timedate, timedate2, discriptionsender, discriptionrecevier, date, avatar;
            ImageView imagesender;
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
            }
        }
    }
}