package org.taxivyapar.app;

import static android.view.View.VISIBLE;
import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class subscribe extends AppCompatActivity implements PaymentResultListener {
    RecyclerView recycler;
    recycleradepter adeptro;
    ArrayList<Modelinfo> model;
    TextView subscription;
    Dialog load;
    ConstraintLayout loading, card;
    String isActive = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        loading = findViewById(R.id.loading);

        subscription = findViewById(R.id.subscription);
        card = findViewById(R.id.card);

        recycler = findViewById(R.id.recyclercutomer);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        model = new ArrayList<>();
        adeptro = new recycleradepter(model);
        recycler.setAdapter(adeptro);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        initial();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initial();
    }

    private void initial() {
        isActive = "no";
        try {
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        if (profileContainer.freeTrial.equals("yes")) {
            card.setVisibility(VISIBLE);
            subscription.setText("Free Trail");
        } else if (profileContainer.userFreeTrial != null && profileContainer.userFreeTrial.equals("yes")) {
            card.setVisibility(VISIBLE);
            subscription.setText("Free Trail");
        } else if (isActive.equals("yes")) {
            card.setVisibility(VISIBLE);
            String inputString = profileContainer.planExpiry;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime dateTime = LocalDateTime.parse(inputString, inputFormatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH);
            String outputString = dateTime.format(outputFormatter);
            subscription.setText("Plan will expire on " + outputString);
        } else {
            card.setVisibility(GONE);
            loading.setVisibility(VISIBLE);
            FirebaseFirestore.getInstance().collection("plans")
                    .orderBy("TimeStamp", Query.Direction.ASCENDING)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            loading.setVisibility(View.GONE);
                            model.clear();
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Modelinfo obj = d.toObject(Modelinfo.class);
                                model.add(obj);
                            }
                            adeptro.notifyDataSetChanged();
                        }
                    });
        }


    }

    public void back(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        view.startAnimation(animation);
        subscribe.super.onBackPressed();
    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<Modelinfo> postmodels;

        public recycleradepter(ArrayList<Modelinfo> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradepter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_plan, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            holder.name.setText(postmodels.get(position).getRemark());
            holder.desc.setText(postmodels.get(position).getDescription());
            holder.price.setText("₹" + postmodels.get(position).getPaymentAmount());
            holder.buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.buy.startAnimation(animation);
                    profileContainer.rechargeAmount = postmodels.get(position).getPaymentAmount();
                    profileContainer.rechargePlan = postmodels.get(position).getTime();
                    profileContainer.rechargeType = postmodels.get(position).getRemark();

                    if (profileContainer.rechargeAmount.equals("0")) {
                        profileContainer.referralNo = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
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
                        user.put("userMobileNo", profileContainer.userMobileNo);
                        user.put("userName", profileContainer.userName);
                        user.put("Amount", profileContainer.rechargeAmount);
                        user.put("Type", "recharge");
                        user.put("description", profileContainer.rechargeType + " Recharge of " + profileContainer.rechargeAmount + "/-");
                        user.put("Status", "accept");
                        user.put("Date", currentDate);
                        user.put("Time", currentTime);
                        user.put("Id", profileContainer.referralNo);
                        user.put("TimeStamp", timestmp);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(profileContainer.rechargePlan));
                        String futureTimestamp = dateFormat.format(calendar.getTime());

                        Map<String, Object> user2 = new HashMap<>();
                        user2.put("isActive", "yes");
                        user2.put("activePlan", profileContainer.rechargeType + " Recharge of ₹" + profileContainer.rechargeAmount);
                        user2.put("planExpiry", futureTimestamp);

                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).update(user2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        load.dismiss();
                                        profileContainer.isActive = "yes";
                                        profileContainer.planExpiry = futureTimestamp;
                                        profileContainer.activePlan = profileContainer.rechargeType + " Recharge of ₹" + profileContainer.rechargeAmount;

                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(profileContainer.userMobileNo).collection("transaction")
                                                .document(profileContainer.referralNo).set(user);

                                        FirebaseFirestore.getInstance().collection("transaction")
                                                .document(profileContainer.referralNo).set(user);

                                        card.setVisibility(VISIBLE);
                                        String inputString = profileContainer.planExpiry;
                                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                                        LocalDateTime dateTime = LocalDateTime.parse(inputString, inputFormatter);
                                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH);
                                        String outputString = dateTime.format(outputFormatter);
                                        subscription.setText("Plan will expire on " + outputString);
                                    }
                                });
                    } else {
                        checkout();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            CardView buy;
            TextView name, price, desc;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                buy = itemView.findViewById(R.id.addproduct);
                name = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.price);
                desc = itemView.findViewById(R.id.desc);
            }
        }

    }

    private void checkout() {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", Integer.valueOf(profileContainer.rechargeAmount) * 100);
            orderRequest.put("currency", "INR");

            String url = "https://api.razorpay.com/v1/orders";
            RequestQueue requestQueue = Volley.newRequestQueue(subscribe.this);
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
                                    checkout.open(subscribe.this, options);
                                } catch (Exception e) {
                                    Log.i("sdf", "Error in Razorpay Checkout", e);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(subscribe.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(subscribe.this, error.toString(), Toast.LENGTH_SHORT).show();
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
        user.put("userMobileNo", profileContainer.userMobileNo);
        user.put("userName", profileContainer.userName);
        user.put("Amount", profileContainer.rechargeAmount);
        user.put("Type", "recharge");
        user.put("description", "Plan Type: " + profileContainer.rechargeType + "\nRecharge Amount: ₹" + profileContainer.rechargeAmount);
        user.put("Status", "accept");
        user.put("Date", currentDate);
        user.put("Time", currentTime);
        user.put("Id", profileContainer.referralNo);
        user.put("TimeStamp", timestmp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(profileContainer.rechargePlan));
        String futureTimestamp = dateFormat.format(calendar.getTime());

        Map<String, Object> user2 = new HashMap<>();
        user2.put("isActive", "yes");
        user2.put("activePlan", profileContainer.rechargeType + " Recharge of ₹" + profileContainer.rechargeAmount);
        user2.put("planExpiry", futureTimestamp);

        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).update(user2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        load.dismiss();
                        profileContainer.isActive = "yes";
                        profileContainer.planExpiry = futureTimestamp;
                        profileContainer.activePlan = profileContainer.rechargeType + " Recharge of ₹" + profileContainer.rechargeAmount;

                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).collection("transaction")
                                .document(profileContainer.referralNo).set(user);

                        FirebaseFirestore.getInstance().collection("transaction")
                                .document(profileContainer.referralNo).set(user);

                        card.setVisibility(VISIBLE);
                        String inputString = profileContainer.planExpiry;
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                        LocalDateTime dateTime = LocalDateTime.parse(inputString, inputFormatter);
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH);
                        String outputString = dateTime.format(outputFormatter);
                        subscription.setText("Plan will expire on " + outputString);
                    }
                });
    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}