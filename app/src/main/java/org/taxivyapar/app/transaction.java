package org.taxivyapar.app;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class transaction extends AppCompatActivity {
    View back;
    ConstraintLayout loading;
    TextView balance, balance2;
    Button withdrawmoney;
    RecyclerView recyler;
    ArrayList<Modelbabal> model;
    recycleradveptor adeptro;
    TextView sample;
    DecimalFormat df = new DecimalFormat("0.00");
    Dialog load, postPopup;
    RadioButton radio1, radio2;
    Button btn1;
    String detail = "", type = "UPI Id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        back = findViewById(R.id.view);

        loading = findViewById(R.id.loading);

        balance = findViewById(R.id.name);
        withdrawmoney = findViewById(R.id.btn1);

        sample = findViewById(R.id.sample);

        recyler = findViewById(R.id.recycler);
        recyler.setLayoutManager(new LinearLayoutManager(this));
        model = new ArrayList<>();
        adeptro = new recycleradveptor(model);
        recyler.setAdapter(adeptro);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        postPopup = new Dialog(this);
        postPopup.setContentView(R.layout.popup_withdraw);
        postPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radio1 = postPopup.findViewById(R.id.radio1);
        radio2 = postPopup.findViewById(R.id.radio2);
        btn1 = postPopup.findViewById(R.id.btn1);
        balance2 = postPopup.findViewById(R.id.balance);

        initial();

        withdrawmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                withdrawmoney.startAnimation(animation);
                radio1.setChecked(true);
                radio2.setChecked(false);
                detail = profileContainer.upiId;
                postPopup.show();
            }
        });

        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detail = profileContainer.upiId;
                type = "UPI Id";
            }
        });

        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detail = profileContainer.bankAccount;
                type = "Bank Account";
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail != null && !detail.equals("")) {
                    if (profileContainer.userWallet <= 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(transaction.this);
                        builder.setTitle("Insufficient Balance");
                        builder.setMessage("There is no amount to withdraw.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                postPopup.dismiss();
                            }
                        });
                        builder.create();
                        builder.show();
                    } else {
                        String id = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                        id = id + profileContainer.userMobileNo;
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
                        user.put("Amount", df.format(profileContainer.userWallet));
                        user.put("Type", "withdraw");
                        user.put("description", "Type: " + type + " Transfer\nAccount Detail: " + detail);
                        user.put("Status", "pending");
                        user.put("Date", currentDate);
                        user.put("Time", currentTime);
                        user.put("Id", id);
                        user.put("TimeStamp", timestmp);

                        Map<String, Object> bal = new HashMap<>();
                        bal.put("userWallet", 0);

                        String finalId = id;
                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).update(bal)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        load.dismiss();

                                        profileContainer.userWallet = 0;

                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(profileContainer.userMobileNo).collection("transaction")
                                                .document(finalId).set(user);

                                        FirebaseFirestore.getInstance().collection("transaction")
                                                .document(finalId).set(user);

                                        postPopup.dismiss();
                                        initial();
                                    }
                                });
                    }


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(transaction.this);
                    builder.setTitle("Payment Method");
                    builder.setMessage("Please add your UPI Id and Bank Account Detail");
                    builder.setPositiveButton("Add Bank/UPI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            postPopup.dismiss();
                            startActivity(new Intent(getApplicationContext(), mykyc.class));
                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                transaction.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        balance.setText("₹" + df.format(profileContainer.userWallet));
        initial();
    }

    private void initial() {
        sample.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        balance.setText("₹" + df.format(profileContainer.userWallet));
        balance2.setText("₹" + df.format(profileContainer.userWallet));
        FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                .collection("transaction")
                .orderBy("TimeStamp", Query.Direction.DESCENDING).limit(500)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        loading.setVisibility(View.GONE);
                        model.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            Modelbabal obj = d.toObject(Modelbabal.class);
                            model.add(obj);
                        }
                        adeptro.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(transaction.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public class recycleradveptor extends RecyclerView.Adapter<recycleradveptor.postviewholder> {
        ArrayList<Modelbabal> postmodels;

        public recycleradveptor(ArrayList<Modelbabal> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradveptor.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.walletcard, parent, false);
            return new recycleradveptor.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradveptor.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.date.setText(postmodels.get(position).getDate() + " " + postmodels.get(position).getTime());
            holder.amount.setText("₹" + postmodels.get(position).getAmount());
            holder.detail.setText(postmodels.get(position).getDescription());
            if (postmodels.get(position).getType().equals("recharge")) {
                holder.status.setText("Recharge");
                holder.status.setTextColor(Color.parseColor("#4CAF50"));
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9FFEB")));
            } else if (postmodels.get(position).getType().equals("withdraw")) {
                if (postmodels.get(position).getStatus().equals("accept")) {
                    holder.status.setText("Withdraw");
                    holder.status.setTextColor(Color.parseColor("#4CAF50"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9FFEB")));
                } else if (postmodels.get(position).getStatus().equals("reject")) {
                    holder.status.setText("Rejected");
                    holder.status.setTextColor(Color.parseColor("#F44336"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFECEB")));
                } else if (postmodels.get(position).getStatus().equals("pending")) {
                    holder.status.setText("Pending");
                    holder.status.setTextColor(Color.parseColor("#f7b217"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFBEF")));
                }
            } else if (postmodels.get(position).getType().equals("commissionReceive")) {
                if (postmodels.get(position).getStatus().equals("accept")) {
                    holder.status.setText("Commission Receive");
                    holder.status.setTextColor(Color.parseColor("#4CAF50"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9FFEB")));
                } else if (postmodels.get(position).getStatus().equals("refund")) {
                    holder.status.setText("Commission Refunded");
                    holder.status.setTextColor(Color.parseColor("#F44336"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFECEB")));
                } else if (postmodels.get(position).getStatus().equals("pending")) {
                    holder.status.setText("Commission Pending");
                    holder.status.setTextColor(Color.parseColor("#f7b217"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFBEF")));
                } else if (postmodels.get(position).getStatus().equals("cancel")) {
                    holder.status.setText("Booking Cancel");
                    holder.status.setTextColor(Color.parseColor("#F44336"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFECEB")));
                }

            } else if (postmodels.get(position).getType().equals("commissionPay")) {
                if (postmodels.get(position).getStatus().equals("accept")) {
                    holder.status.setText("Commission Pay");
                    holder.status.setTextColor(Color.parseColor("#F44336"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFECEB")));
                } else if (postmodels.get(position).getStatus().equals("refund")) {
                    holder.status.setText("Commission Refunded");
                    holder.status.setTextColor(Color.parseColor("#4CAF50"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9FFEB")));
                } else if (postmodels.get(position).getStatus().equals("pending")) {
                    holder.status.setText("Commission Pending");
                    holder.status.setTextColor(Color.parseColor("#f7b217"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFBEF")));
                } else if (postmodels.get(position).getStatus().equals("cancel")) {
                    holder.status.setText("Booking Cancel");
                    holder.status.setTextColor(Color.parseColor("#F44336"));
                    holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFECEB")));
                }
            }

        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView date, amount, status, detail;


            public postviewholder(@NonNull View itemView) {
                super(itemView);
                status = itemView.findViewById(R.id.order_status);
                detail = itemView.findViewById(R.id.detail);
                date = itemView.findViewById(R.id.timer);
                amount = itemView.findViewById(R.id.amount);
            }
        }
    }
}