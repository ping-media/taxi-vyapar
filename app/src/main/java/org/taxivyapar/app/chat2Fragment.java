package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class chat2Fragment extends Fragment {

    View view;
    RecyclerView recyclerchat;
    recycleradepterchat adeptrochat;
    ArrayList<Modelinfo> modelchat;
    TextView chatno;
    ConstraintLayout loading;
    ListenerRegistration transactionsrealtime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat2, container, false);

        loading = view.findViewById(R.id.loading);
        chatno = view.findViewById(R.id.sample);
        recyclerchat = view.findViewById(R.id.recyclercutomer);

        recyclerchat.setLayoutManager(new LinearLayoutManager(getContext()));
        modelchat = new ArrayList<>();
        adeptrochat = new recycleradepterchat(modelchat);
        recyclerchat.setAdapter(adeptrochat);

        return view;
    }

    @Override
    public void onStop() {
        try {
            transactionsrealtime.remove();
        } catch (Exception e) {

        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            transactionsrealtime.remove();
        } catch (Exception e) {

        }
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        initial();
    }

    private void initial() {
        loading.setVisibility(View.VISIBLE);
        try {
            transactionsrealtime = FirebaseFirestore.getInstance().collection("userChat")
                    .document(profileContainer.userMobileNo).collection("chatReceive")
                    .orderBy("TimeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            loading.setVisibility(View.GONE);
                            modelchat.clear();
                            List<DocumentSnapshot> list = value.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Modelinfo obj = d.toObject(Modelinfo.class);
                                modelchat.add(obj);
                            }
                            adeptrochat.notifyDataSetChanged();
                        }
                    });

        } catch (Exception e) {

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
//            holder.detail.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
//            FirebaseFirestore.getInstance().collection("postBooking")
//                    .document(postmodels.get(position).getTransactionId())
//                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            if (documentSnapshot.exists()) {
//                                if (documentSnapshot.getString("BookingAssignNo") != null) {
//                                    if (documentSnapshot.getString("BookingAssignNo")
//                                            .equals(profileContainer.userMobileNo)) {
//                                        holder.detail.setBackgroundTintList(ColorStateList
//                                                .valueOf(Color.parseColor("#CBFFCF")));
//                                    }
//                                }
//                            }
//                        }
//                    });

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
                    Intent i = new Intent(getContext(), bookchat.class);
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