package org.taxivyapar.app;

public class ModelpostBook2 implements Comparable<ModelpostBook2> {
    String VehicleName, StartDate, StartTime,
            StartTimeStamp, Remark,Description,TourDays, TimeStamp,
            SenderMobileNo, SenderName, AddressLat,
            AddressLng, AddressHash, TransactionId,
            Address, Status, DropAddressLat,
            DropAddressLng, DropAddressHash, DropAddress,
            PaymentSystem, PaymentAmount, PaymentCommission,
            PaymentNegotiable, BookingSecure, ProfileHide,
            Diesel, Carrier, BookingType, BookingId,
            AddressCity,DropAddressCity,Extra;
    double radiusInM;

    public ModelpostBook2() {
    }

    public ModelpostBook2(String vehicleName, String startDate, String startTime, String startTimeStamp, String remark,
                         String description, String tourDays, String timeStamp, String senderMobileNo, String senderName,
                         String addressLat, String addressLng, String addressHash, String transactionId, String address,
                         String status, String dropAddressLat, String dropAddressLng, String dropAddressHash,
                         String dropAddress, String paymentSystem, String paymentAmount, String paymentCommission,
                         String paymentNegotiable, String bookingSecure, String profileHide, String diesel,
                         String carrier, String bookingType, String bookingId, String addressCity,
                         String dropAddressCity, String extra, double radiusInM) {
        VehicleName = vehicleName;
        StartDate = startDate;
        StartTime = startTime;
        StartTimeStamp = startTimeStamp;
        Remark = remark;
        Description = description;
        TourDays = tourDays;
        TimeStamp = timeStamp;
        SenderMobileNo = senderMobileNo;
        SenderName = senderName;
        AddressLat = addressLat;
        AddressLng = addressLng;
        AddressHash = addressHash;
        TransactionId = transactionId;
        Address = address;
        Status = status;
        DropAddressLat = dropAddressLat;
        DropAddressLng = dropAddressLng;
        DropAddressHash = dropAddressHash;
        DropAddress = dropAddress;
        PaymentSystem = paymentSystem;
        PaymentAmount = paymentAmount;
        PaymentCommission = paymentCommission;
        PaymentNegotiable = paymentNegotiable;
        BookingSecure = bookingSecure;
        ProfileHide = profileHide;
        Diesel = diesel;
        Carrier = carrier;
        BookingType = bookingType;
        BookingId = bookingId;
        AddressCity = addressCity;
        DropAddressCity = dropAddressCity;
        Extra = extra;
        this.radiusInM = radiusInM;
    }

    public String getVehicleName() {
        return VehicleName;
    }

    public void setVehicleName(String vehicleName) {
        VehicleName = vehicleName;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getStartTimeStamp() {
        return StartTimeStamp;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        StartTimeStamp = startTimeStamp;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTourDays() {
        return TourDays;
    }

    public void setTourDays(String tourDays) {
        TourDays = tourDays;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSenderMobileNo() {
        return SenderMobileNo;
    }

    public void setSenderMobileNo(String senderMobileNo) {
        SenderMobileNo = senderMobileNo;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getAddressLat() {
        return AddressLat;
    }

    public void setAddressLat(String addressLat) {
        AddressLat = addressLat;
    }

    public String getAddressLng() {
        return AddressLng;
    }

    public void setAddressLng(String addressLng) {
        AddressLng = addressLng;
    }

    public String getAddressHash() {
        return AddressHash;
    }

    public void setAddressHash(String addressHash) {
        AddressHash = addressHash;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDropAddressLat() {
        return DropAddressLat;
    }

    public void setDropAddressLat(String dropAddressLat) {
        DropAddressLat = dropAddressLat;
    }

    public String getDropAddressLng() {
        return DropAddressLng;
    }

    public void setDropAddressLng(String dropAddressLng) {
        DropAddressLng = dropAddressLng;
    }

    public String getDropAddressHash() {
        return DropAddressHash;
    }

    public void setDropAddressHash(String dropAddressHash) {
        DropAddressHash = dropAddressHash;
    }

    public String getDropAddress() {
        return DropAddress;
    }

    public void setDropAddress(String dropAddress) {
        DropAddress = dropAddress;
    }

    public String getPaymentSystem() {
        return PaymentSystem;
    }

    public void setPaymentSystem(String paymentSystem) {
        PaymentSystem = paymentSystem;
    }

    public String getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public String getPaymentCommission() {
        return PaymentCommission;
    }

    public void setPaymentCommission(String paymentCommission) {
        PaymentCommission = paymentCommission;
    }

    public String getPaymentNegotiable() {
        return PaymentNegotiable;
    }

    public void setPaymentNegotiable(String paymentNegotiable) {
        PaymentNegotiable = paymentNegotiable;
    }

    public String getBookingSecure() {
        return BookingSecure;
    }

    public void setBookingSecure(String bookingSecure) {
        BookingSecure = bookingSecure;
    }

    public String getProfileHide() {
        return ProfileHide;
    }

    public void setProfileHide(String profileHide) {
        ProfileHide = profileHide;
    }

    public String getDiesel() {
        return Diesel;
    }

    public void setDiesel(String diesel) {
        Diesel = diesel;
    }

    public String getCarrier() {
        return Carrier;
    }

    public void setCarrier(String carrier) {
        Carrier = carrier;
    }

    public String getBookingType() {
        return BookingType;
    }

    public void setBookingType(String bookingType) {
        BookingType = bookingType;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getAddressCity() {
        return AddressCity;
    }

    public void setAddressCity(String addressCity) {
        AddressCity = addressCity;
    }

    public String getDropAddressCity() {
        return DropAddressCity;
    }

    public void setDropAddressCity(String dropAddressCity) {
        DropAddressCity = dropAddressCity;
    }

    public String getExtra() {
        return Extra;
    }

    public void setExtra(String extra) {
        Extra = extra;
    }

    public double getRadiusInM() {
        return radiusInM;
    }

    public void setRadiusInM(double radiusInM) {
        this.radiusInM = radiusInM;
    }

    @Override
    public int compareTo(ModelpostBook2 otherModelpost) {
        return otherModelpost.getTimeStamp().compareTo(this.TimeStamp);
    }

}
