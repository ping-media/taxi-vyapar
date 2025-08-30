package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class send_review extends AppCompatActivity {
    TextView mobile, name, avatar;
    ImageView profileimage;
    View verify;
    String transId = "", custno = "", custname = "", type = "", str_bkid = "", str_com = "0";
    RatingBar ratingBar;
    EditText reviewInput;
    CardView submitReview;
    Dialog load;
    ConstraintLayout loading;
    RecyclerView recyler;
    ArrayList<Modelinfo> model, data;
    ArrayList<String> arroption;
    recycleradveptor adeptro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_review);

        loading = findViewById(R.id.loading);

        name = findViewById(R.id.textView14);
        mobile = findViewById(R.id.mobilenumber);
        avatar = findViewById(R.id.avatar);
        profileimage = findViewById(R.id.profileimage);
        verify = findViewById(R.id.view10);

        ratingBar = findViewById(R.id.ratingBar);
        reviewInput = findViewById(R.id.remark);
        submitReview = findViewById(R.id.login);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        arroption = new ArrayList<>();

        recyler = findViewById(R.id.recycler);
        recyler.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        model = new ArrayList<>();
        adeptro = new recycleradveptor(model);
        recyler.setAdapter(adeptro);

        Intent i = getIntent();
        type = i.getStringExtra("type");
        transId = i.getStringExtra("transId");
        custno = i.getStringExtra("custno");
        custname = i.getStringExtra("custname");

        loading.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("header").document("header")
                .collection("driver").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        data.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            Modelinfo obj = d.toObject(Modelinfo.class);
                            data.add(obj);
                        }
                        process("5");
                    }
                });

        FirebaseFirestore.getInstance().collection("postBooking").document(transId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loading.setVisibility(View.GONE);
                        try {
                            avatar.setText(custname.substring(0, 1).toUpperCase());
                        } catch (Exception e) {
                            avatar.setText("A");
                        }
                        str_bkid = documentSnapshot.getString("BookingId");

                        str_com = "0";
                        if (documentSnapshot.get("CommissionRequest") != null) {
                            ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                            ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");
                            if (commissionRequest.contains(custno)) {
                                int ind = commissionRequest.indexOf(custno);
                                str_com = CommissionPay.get(ind);
                            }
                        }

                        mobile.setText("Driver");

                        name.setText(custname);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(custno).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        name.setText(documentSnapshot.getString("UserName"));

                                        if (documentSnapshot.getString("userCompany") != null) {
                                            if (!documentSnapshot.getString("userCompany").equals("")) {
                                                mobile.setVisibility(View.VISIBLE);
                                                mobile.setText(documentSnapshot.getString("userCompany"));
                                            }
                                        }

                                        if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                            verify.setVisibility(View.VISIBLE);
                                        }
                                        try {
                                            Glide.with(getApplicationContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                                    .into(profileimage);
                                        } catch (Exception e) {
                                        }
                                    }
                                });

                    }
                });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if (rating > 4) {
                        process("5");
                    } else if (rating > 3) {
                        process("4");
                    } else if (rating > 2) {
                        process("3");
                    } else if (rating > 1) {
                        process("2");
                    } else if (rating > 0) {
                        process("1");
                    } else {
                        process("5");
                    }
                }
            }
        });

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                view.startAnimation(animation);

                float rating = ratingBar.getRating();
                String review = "";
                for (String s : arroption) {
                    review = review + s + " ";
                }
                review = review + reviewInput.getText().toString().trim();

                if (rating == 0 || review.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please provide a rating and review", Toast.LENGTH_SHORT).show();
                    return;
                }

                load.show();
                String finalReview = review;
                FirebaseFirestore.getInstance().collection("postBooking")
                        .document(transId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                load.dismiss();


                                Map<String, Object> updateBooking = new HashMap<>();
                                updateBooking.put("Status", "complete");

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                db.collection("postBooking").document(transId).update(updateBooking);
                                db.collection("users").document(profileContainer.userMobileNo)
                                        .collection("postBooking").document(transId).update(updateBooking);

                                Map<String, Object> updateTransaction = new HashMap<>();
                                updateTransaction.put("Status", "accept");

                                db.collection("users")
                                        .document(profileContainer.userMobileNo)
                                        .collection("transaction")
                                        .document(documentSnapshot.getString("BookingAssignPayId"))
                                        .update(updateTransaction);

                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentReference userRef = db.collection("users").document(profileContainer.userMobileNo);
                                        DocumentSnapshot snapshot = transaction.get(userRef);

                                        double currentWallet = 0.0;
                                        if (snapshot.contains("userWallet") && snapshot.getDouble("userWallet") != null) {
                                            currentWallet = snapshot.getDouble("userWallet");
                                        }

                                        double commissionValue = Double.parseDouble(str_com);
                                        transaction.update(userRef, "userWallet", currentWallet + commissionValue);
                                        sendmsg("Booking is Completed by Agent");

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                                        String timeStamp1 = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                                        String fladid = timeStamp1 + profileContainer.userMobileNo.substring(profileContainer.userMobileNo.length() - 4);

                                        load.show();
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("TimeStamp", timestmp);
                                        user.put("Id", fladid);
                                        user.put("SenderMobileNo", profileContainer.userMobileNo);
                                        user.put("SenderName", profileContainer.userName);
                                        user.put("UserName", custname);
                                        user.put("UserPhoneNumber", custno);
                                        user.put("BookingId", str_bkid);
                                        user.put("TransactionId", transId);
                                        user.put("userRating", rating);
                                        user.put("Remark", finalReview);
                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(custno).collection("reviews").document(fladid).set(user);
                                        FirebaseFirestore.getInstance().collection("reviews").document(fladid).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        FirebaseFirestore.getInstance().collection("users")
                                                                .document(custno).collection("reviews")
                                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                                        load.dismiss();
                                                                        double total = 0;
                                                                        for (DocumentSnapshot doc : querySnapshot) {
                                                                            total += doc.getDouble("userRating");
                                                                        }
                                                                        double avg = total / querySnapshot.size();
                                                                        Map<String, Object> update = new HashMap<>();
                                                                        update.put("userRating", avg);

                                                                        Map<String, Object> requestreview = new HashMap<>();
                                                                        requestreview.put("request", "pending");
                                                                        requestreview.put("SenderMobileNo", profileContainer.userMobileNo);
                                                                        requestreview.put("SenderName", profileContainer.userName);
                                                                        requestreview.put("BookingId", str_bkid);
                                                                        requestreview.put("TransactionId", transId);
                                                                        requestreview.put("TimeStamp", timestmp);

                                                                        FirebaseFirestore.getInstance().collection("users")
                                                                                .document(custno).collection("reviewRequest")
                                                                                .document(str_bkid)
                                                                                .set(requestreview);

                                                                        FirebaseFirestore.getInstance().collection("users")
                                                                                .document(custno).update(update);

                                                                        try {
                                                                            JSONObject body = new JSONObject();
                                                                            body.put("app_id", profileContainer.oneSignal);

                                                                            JSONArray externalUserIds = new JSONArray();
                                                                            externalUserIds.put(custno.replace("+91", ""));
                                                                            body.put("include_external_user_ids", externalUserIds);

                                                                            JSONObject headings = new JSONObject();
                                                                            headings.put("en", rating + " Review Receive");
                                                                            body.put("headings", headings);

                                                                            JSONObject contents = new JSONObject();
                                                                            contents.put("en", profileContainer.userName + " send a review for booking #" + str_bkid);
                                                                            body.put("contents", contents);

                                                                            body.put("small_icon", "logo");
                                                                            body.put("android_channel_id", profileContainer.oneSignalChannelMsg);

                                                                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(body,
                                                                                    getApplicationContext(), send_review.this);
                                                                            notificationsSender.SendNotifications();
                                                                        } catch (Exception e) {
                                                                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        Toast.makeText(send_review.this, "Booking completed.", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(getApplicationContext(), home.class));
                                                                        finishAffinity();
                                                                    }
                                                                });
                                                    }
                                                });

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                load.dismiss();
                                Toast.makeText(send_review.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }

    private void process(String no) {
        model.clear();
        for (Modelinfo modelinfo : data) {
            if (modelinfo.getType().contains(no)) {
                model.add(modelinfo);
            }
        }
        adeptro.notifyDataSetChanged();
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
        user1.put("UserProfileImageUri", "");
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
                    getApplicationContext(), send_review.this);
            notificationsSender.SendNotifications();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public class recycleradveptor extends RecyclerView.Adapter<recycleradveptor.postviewholder> {
        ArrayList<Modelinfo> postmodels;

        public recycleradveptor(ArrayList<Modelinfo> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradveptor.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_send_review, parent, false);
            return new recycleradveptor.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradveptor.postviewholder holder, @SuppressLint("RecyclerView") int position) {

            holder.name.setText(postmodels.get(position).getRemark());
            if (arroption.contains(postmodels.get(position).getRemark())) {
                holder.name.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                holder.name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black_color)));
            } else {
                holder.name.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
                holder.name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
            }

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (arroption.contains(postmodels.get(position).getRemark())) {
                        arroption.remove(postmodels.get(position).getRemark());
                        holder.name.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
                        holder.name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                    } else {
                        arroption.add(postmodels.get(position).getRemark());
                        holder.name.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        holder.name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black_color)));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView name;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
            }
        }
    }
}