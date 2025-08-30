package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class reviewuser extends AppCompatActivity {
    TextView mobile, name, avatar, avg_rating, review_count;
    ImageView profileimage;
    View verify;
    ProgressBar rat1, rat2, rat3, rat4, rat5;
    ConstraintLayout barloading;
    String custno = "", custname = "";
    RecyclerView recyclerchat;
    recycleradepterchat adeptrochat;
    ArrayList<Modelreview> modelchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewuser);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.parseColor("#000000"));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                int flags = getWindow().getDecorView().getSystemUiVisibility();
//                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//                getWindow().getDecorView().setSystemUiVisibility(flags);
//            }
//        }

        recyclerchat = findViewById(R.id.recycler);

        recyclerchat.setLayoutManager(new LinearLayoutManager(this));
        modelchat = new ArrayList<>();
        adeptrochat = new recycleradepterchat(modelchat);
        recyclerchat.setAdapter(adeptrochat);

        barloading = findViewById(R.id.loading);

        name = findViewById(R.id.textView14);
        mobile = findViewById(R.id.mobilenumber);
        avatar = findViewById(R.id.avatar);
        profileimage = findViewById(R.id.profileimage);
        avg_rating = findViewById(R.id.avg_rating);
        review_count = findViewById(R.id.review_count);

        rat1 = findViewById(R.id.rat1);
        rat2 = findViewById(R.id.rat2);
        rat3 = findViewById(R.id.rat3);
        rat4 = findViewById(R.id.rat4);
        rat5 = findViewById(R.id.rat5);
        verify = findViewById(R.id.view10);

        Intent i = getIntent();
        custno = i.getStringExtra("custno");
        custname = i.getStringExtra("custname");

        barloading.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("users")
                .document(custno).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        barloading.setVisibility(View.GONE);

                        name.setText(documentSnapshot.getString("UserName"));
                        try {
                            avatar.setText(documentSnapshot.getString("UserName").substring(0, 1).toUpperCase());
                        } catch (Exception e) {
                            avatar.setText("A");
                        }
                        mobile.setVisibility(View.GONE);
                        if (documentSnapshot.getString("userCompany") != null) {
                            if (!documentSnapshot.getString("userCompany").equals("")) {
                                mobile.setVisibility(View.VISIBLE);
                                mobile.setText(documentSnapshot.getString("userCompany"));
                            }
                        }
                        verify.setVisibility(View.GONE);
                        if (documentSnapshot.getString("UserVerify") != null) {
                            if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                verify.setVisibility(View.VISIBLE);
                            }
                        }
                        try {
                            Glide.with(getApplicationContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                    .into(profileimage);
                        } catch (Exception e) {
                        }

                        double prrat = 0;
                        if (documentSnapshot.get("userRating") != null) {
                            prrat = ((Number) documentSnapshot.get("userRating")).doubleValue();
                        }
                        DecimalFormat df = new DecimalFormat("0.0");
                        avg_rating.setText(df.format(prrat));
                    }
                });

        FirebaseFirestore.getInstance().collection("users").document(custno)
                .collection("reviews")
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        modelchat.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        double st1 = 0, st2 = 0, st3 = 0, st4 = 0, st5 = 0;
                        for (DocumentSnapshot d : list) {
                            Modelreview obj = d.toObject(Modelreview.class);
                            modelchat.add(obj);
                            if (d.getDouble("userRating") > 4) {
                                st5++;
                            } else if (d.getDouble("userRating") > 3) {
                                st4++;
                            } else if (d.getDouble("userRating") > 2) {
                                st3++;
                            } else if (d.getDouble("userRating") > 1) {
                                st2++;
                            } else {
                                st1++;
                            }
                        }
                        adeptrochat.notifyDataSetChanged();
                        review_count.setText(queryDocumentSnapshots.size() + " Reviews");
                        rat1.setProgress((int) ((st1 / queryDocumentSnapshots.size()) * 100));
                        rat2.setProgress((int) ((st2 / queryDocumentSnapshots.size()) * 100));
                        rat3.setProgress((int) ((st3 / queryDocumentSnapshots.size()) * 100));
                        rat4.setProgress((int) ((st4 / queryDocumentSnapshots.size()) * 100));
                        rat5.setProgress((int) ((st5 / queryDocumentSnapshots.size()) * 100));

                    }
                });

    }

    public class recycleradepterchat extends RecyclerView.Adapter<recycleradepterchat.postviewholder> {
        ArrayList<Modelreview> postmodels;

        public recycleradepterchat(ArrayList<Modelreview> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradepterchat.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review, parent, false);
            return new recycleradepterchat.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepterchat.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            holder.verify.setVisibility(View.GONE);

            holder.city.setText(postmodels.get(position).getRemark());

            try {
                holder.avatar.setText(postmodels.get(position).getSenderName().substring(0, 1).toUpperCase());
            } catch (Exception e) {
                holder.avatar.setText("A");
            }

            holder.name.setText(postmodels.get(position).getSenderName());
            FirebaseFirestore.getInstance().collection("users")
                    .document(postmodels.get(position).getSenderMobileNo()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.name.setText(documentSnapshot.getString("UserName"));
                            if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                holder.verify.setVisibility(View.VISIBLE);
                            }

                            try {
                                Glide.with(getApplicationContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                        .into(holder.image);
                            } catch (Exception e) {
                            }
                        }
                    });

            double prrat = postmodels.get(position).getUserRating();
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

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView name, city, avatar;
            View verify, rat1, rat2, rat3, rat4, rat5;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                city = itemView.findViewById(R.id.city);
                image = itemView.findViewById(R.id.profileimage);
                avatar = itemView.findViewById(R.id.avatar);
                verify = itemView.findViewById(R.id.view10);
                rat1 = itemView.findViewById(R.id.rat1);
                rat2 = itemView.findViewById(R.id.rat2);
                rat3 = itemView.findViewById(R.id.rat3);
                rat4 = itemView.findViewById(R.id.rat4);
                rat5 = itemView.findViewById(R.id.rat5);
            }
        }

    }
}