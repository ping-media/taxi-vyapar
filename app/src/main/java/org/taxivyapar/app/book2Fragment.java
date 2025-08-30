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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class book2Fragment extends Fragment {
    View view;
    ConstraintLayout barloading;
    EditText bookid;
    TextView sample;
    SwipeRefreshLayout homerefresh;
    NestedScrollView nested;
    RecyclerView homepostscreen;
    recycleradepter adeptro;
    ArrayList<ModelmyBook> postmodels, data, modelpass;
    Dialog loading;
    ListenerRegistration transactionsrealtime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book2, container, false);

        barloading = view.findViewById(R.id.loading);

        nested = view.findViewById(R.id.nested);

        sample = view.findViewById(R.id.sample);

        bookid = view.findViewById(R.id.searchcustomer);

        homerefresh = view.findViewById(R.id.refresher);

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
                        adeptro.notifyItemRangeInserted(siz, k);
                    }
                }
            }
        });

        bookid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bookid.getWindowToken(), 0);

                    progress();

                    return true;
                }
                return false;
            }
        });

        return view;
    }

//    @Override
//    public void onStop() {
//        try {
//            transactionsrealtime.remove();
//        } catch (Exception e) {
//
//        }
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        try {
//            transactionsrealtime.remove();
//        } catch (Exception e) {
//
//        }
//        super.onDestroy();
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        initial();
    }

    private void initial() {
        barloading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("postBooking")
                .whereEqualTo("BookingAssignNo", profileContainer.userMobileNo)
                .orderBy("TimeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        barloading.setVisibility(View.GONE);
                        data.clear();
                        List<DocumentSnapshot> list = value.getDocuments();
                        for (DocumentSnapshot d : list) {
                            ModelmyBook obj = d.toObject(ModelmyBook.class);
                            data.add(obj);
                        }
                        progress();
                    }
                });
//        try {
//            transactionsrealtime = FirebaseFirestore.getInstance().collection("postBooking")
//                    .whereEqualTo("BookingAssignNo", profileContainer.userMobileNo)
//                    .orderBy("TimeStamp", Query.Direction.DESCENDING)
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                            barloading.setVisibility(View.GONE);
//                            data.clear();
//                            List<DocumentSnapshot> list = value.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                ModelpostBook obj = d.toObject(ModelpostBook.class);
//                                data.add(obj);
//                            }
//                            progress();
//                        }
//                    });
//        } catch (Exception e) {
//
//        }

    }

    public void progress() {
        modelpass.clear();
        sample.setVisibility(View.VISIBLE);
        for (ModelmyBook doc : data) {
            if (bookid.getText().toString().trim().isEmpty()) {
                modelpass.add(doc);
            } else {
                if (doc.getBookingId().contains(bookid.getText().toString().trim())) {
                    modelpass.add(doc);
                }
            }
        }
        postmodels.clear();
        int run = 50;
        if (modelpass.size() < 50) {
            run = modelpass.size();
        }
        for (int i = 0; i < run; i++) {
            postmodels.add(modelpass.get(i));
        }
        adeptro.notifyDataSetChanged();
    }

    public void sendmsg(String custno, String custname, String msg, String str_bkid, String transId) {
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
        user2.put("UserProfileImageUri", "");
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
                    getContext(), getActivity());
            notificationsSender.SendNotifications();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<ModelmyBook> postmodels;

        public recycleradepter(ArrayList<ModelmyBook> postmodels) {
            this.postmodels = postmodels;
        }


        @NonNull
        @Override
        public recycleradepter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_booking_receive, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.bookingid.setText("ID: " + postmodels.get(position).getBookingId()
                    + " (" + postmodels.get(position).getBookingPlatform() + ")");

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
            } else {
                holder.hidden.setVisibility(View.GONE);
            }

            if (postmodels.get(position).getPaymentSystem().equals("booking")) {
                long amt = Long.valueOf(postmodels.get(position).getPaymentAmount());
                long com = 0;
                if (!postmodels.get(position).getPaymentCommission().equals("")) {
                    com = Long.valueOf(postmodels.get(position).getPaymentCommission());
                }
                NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                holder.amount.setText("₹" + numberFormat.format(amt));
                holder.commision.setText("₹" + numberFormat.format(com));
                holder.laycommision.setVisibility(View.VISIBLE);

                if (postmodels.get(position).getPaymentNegotiable().equals("yes")) {
                    holder.negotiable.setVisibility(View.VISIBLE);
                } else {
                    holder.negotiable.setVisibility(View.GONE);
                }
            } else {
                holder.negotiable.setVisibility(View.GONE);
                holder.laycommision.setVisibility(View.GONE);
                holder.amount.setText("Bid Best Price");
            }
            holder.location.setText(postmodels.get(position).getAddressCity());
            holder.droplocation.setText(postmodels.get(position).getDropAddressCity());

            if (postmodels.get(position).getBookingType().equals("oneWay")) {
                holder.trip.setText("One Way");
            } else {
                holder.trip.setText("Round Trip");
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

            if(postmodels.get(position).getBookingAssignNo()!=null){
                if(postmodels.get(position).getCommissionRequest()!=null){
                    if (postmodels.get(position).getCommissionRequest().contains(postmodels.get(position).getBookingAssignNo())) {
                        int ind = postmodels.get(position).getCommissionRequest().indexOf(postmodels.get(position).getBookingAssignNo());
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                        holder.amount.setText("₹" + numberFormat.format(Double.valueOf(postmodels.get(position).getCommissionAmount().get(ind))));
                        holder.commision.setText("₹" + numberFormat.format(Double.valueOf(postmodels.get(position).getCommissionPay().get(ind))));
                        holder.laycommision.setVisibility(View.VISIBLE);

                        holder.negotiable.setVisibility(View.GONE);
                    }
                }
            }

            holder.chatbutton.setVisibility(View.VISIBLE);
            holder.pickupbutton.setVisibility(View.GONE);
            holder.bookingid.setTextColor(ContextCompat.getColor(getContext(), R.color.black_color));
            if (postmodels.get(position).getStatus().equals("assign")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Assign)");
                String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                if (timestmp1.compareTo(postmodels.get(position).getStartTimeStamp()) > 0) {
                    holder.pickupbutton.setVisibility(View.VISIBLE);
                }
            } else if (postmodels.get(position).getStatus().equals("pickup")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Picked)");
            } else if (postmodels.get(position).getStatus().equals("complete")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Complete)");
            } else if (postmodels.get(position).getStatus().equals("cancel")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Cancel)");
                holder.bookingid.setTextColor(ContextCompat.getColor(getContext(), R.color.red_color));
            }


            holder.chatbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    view.startAnimation(animation);
                    Intent i = new Intent(getContext(), bookchat.class);
                    i.putExtra("transId", postmodels.get(position).getTransactionId());
                    i.putExtra("custno", postmodels.get(position).getSenderMobileNo());
                    i.putExtra("custname", postmodels.get(position).getSenderName());
                    startActivity(i);
                }
            });

            holder.pickupbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    view.startAnimation(animation);
                    loading.show();
                    FirebaseFirestore.getInstance().collection("postBooking")
                            .document(postmodels.get(position).getTransactionId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    loading.dismiss();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Status", "pickup");
                                    FirebaseFirestore.getInstance().collection("postBooking")
                                            .document(postmodels.get(position).getTransactionId()).update(user);
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(postmodels.get(position).getSenderMobileNo())
                                            .collection("postBooking")
                                            .document(postmodels.get(position).getTransactionId()).update(user);


                                    sendmsg(postmodels.get(position).getSenderMobileNo(), postmodels.get(position).getSenderName(),
                                            "Booking is Pickup by Driver", postmodels.get(position).getBookingId(),
                                            postmodels.get(position).getTransactionId());

                                    initial();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(getContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            });

            holder.custname = postmodels.get(position).getSenderName();
            holder.custno = postmodels.get(position).getSenderMobileNo();
            holder.name.setText(holder.custname);

            FirebaseFirestore.getInstance().collection("users")
                    .document(holder.custno).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.name.setText(documentSnapshot.getString("UserName"));
                        }
                    });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
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
                    amount, commision, name;
            CardView chatbutton, pickupbutton;
            ImageView vehicleimage;
            LinearLayout laycommision;
            CardView hidden;
            String custno, custname;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                vehiclename = itemView.findViewById(R.id.vehiclename);
                vehicleimage = itemView.findViewById(R.id.vehicleimage);
                startdate = itemView.findViewById(R.id.startdate);
                location = itemView.findViewById(R.id.location);
                remark = itemView.findViewById(R.id.remark);
                bookingid = itemView.findViewById(R.id.bookingid);
                trip = itemView.findViewById(R.id.trip);
                droplocation = itemView.findViewById(R.id.droplocation);
                amount = itemView.findViewById(R.id.amount);
                commision = itemView.findViewById(R.id.commision);
                hidden = itemView.findViewById(R.id.securetag);
                laycommision = itemView.findViewById(R.id.laycommi);
                negotiable = itemView.findViewById(R.id.negotiable);
                name = itemView.findViewById(R.id.textView14);
                chatbutton = itemView.findViewById(R.id.chatbutton);
                pickupbutton = itemView.findViewById(R.id.pickupbutton);
            }
        }
    }
}