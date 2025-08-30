package org.taxivyapar.app;

import java.util.ArrayList;

public class Modelinfo {
    String UserName, UserPhoneNumber, userCompany, TimeStamp, VehicleName, VehicleNumber, VehicleYear, Insurance,
            InsuranceExpiry, RC1, RC2, Attachment1, Attachment2, Attachment3, Attachment4, Attachment5, Id,
            LicenseNumber, LicenseFront, LicenseBack, AdharNumber, AdharFront, AdharBack, AddressLat, AddressLng,
            AddressHash, Address, AddressCity, SenderName, SenderMobileNo, StartDate, StartTime, StartTimeStamp,
            Remark, Description, TourDays, TransactionId, Status, DropAddressLat, DropAddressLng, DropAddressHash,
            DropAddress, PaymentSystem, PaymentAmount, PaymentCommission, PaymentNegotiable, BookingSecure, ProfileHide,
            Diesel, Carrier, BookingType, BookingId, DropAddressCity, Extra, Date, ReceiverMobileNo, ReceiverName,
            Time, TransactionType, Type, UserProfileImageUri, UserMessageToken, UserStatus, UserVerify;
    ArrayList<String> userTrip, userVehicle;

    public Modelinfo() {
    }

    public Modelinfo(String userName, String userPhoneNumber, String userCompany, String timeStamp, String vehicleName,
                     String vehicleNumber, String vehicleYear, String insurance, String insuranceExpiry, String RC1,
                     String RC2, String attachment1, String attachment2, String attachment3, String attachment4,
                     String attachment5, String id, String licenseNumber, String licenseFront, String licenseBack,
                     String adharNumber, String adharFront, String adharBack, String addressLat, String addressLng,
                     String addressHash, String address, String addressCity, String senderName, String senderMobileNo,
                     String startDate, String startTime, String startTimeStamp, String remark, String description,
                     String tourDays, String transactionId, String status, String dropAddressLat, String dropAddressLng,
                     String dropAddressHash, String dropAddress, String paymentSystem, String paymentAmount,
                     String paymentCommission, String paymentNegotiable, String bookingSecure, String profileHide,
                     String diesel, String carrier, String bookingType, String bookingId, String dropAddressCity,
                     String extra, String date, String receiverMobileNo, String receiverName, String time,
                     String transactionType, String type, String userProfileImageUri, String userMessageToken,
                     String userStatus, String userVerify, ArrayList<String> userTrip, ArrayList<String> userVehicle) {
        UserName = userName;
        UserPhoneNumber = userPhoneNumber;
        this.userCompany = userCompany;
        TimeStamp = timeStamp;
        VehicleName = vehicleName;
        VehicleNumber = vehicleNumber;
        VehicleYear = vehicleYear;
        Insurance = insurance;
        InsuranceExpiry = insuranceExpiry;
        this.RC1 = RC1;
        this.RC2 = RC2;
        Attachment1 = attachment1;
        Attachment2 = attachment2;
        Attachment3 = attachment3;
        Attachment4 = attachment4;
        Attachment5 = attachment5;
        Id = id;
        LicenseNumber = licenseNumber;
        LicenseFront = licenseFront;
        LicenseBack = licenseBack;
        AdharNumber = adharNumber;
        AdharFront = adharFront;
        AdharBack = adharBack;
        AddressLat = addressLat;
        AddressLng = addressLng;
        AddressHash = addressHash;
        Address = address;
        AddressCity = addressCity;
        SenderName = senderName;
        SenderMobileNo = senderMobileNo;
        StartDate = startDate;
        StartTime = startTime;
        StartTimeStamp = startTimeStamp;
        Remark = remark;
        Description = description;
        TourDays = tourDays;
        TransactionId = transactionId;
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
        DropAddressCity = dropAddressCity;
        Extra = extra;
        Date = date;
        ReceiverMobileNo = receiverMobileNo;
        ReceiverName = receiverName;
        Time = time;
        TransactionType = transactionType;
        Type = type;
        UserProfileImageUri = userProfileImageUri;
        UserMessageToken = userMessageToken;
        UserStatus = userStatus;
        UserVerify = userVerify;
        this.userTrip = userTrip;
        this.userVehicle = userVehicle;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPhoneNumber() {
        return UserPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        UserPhoneNumber = userPhoneNumber;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getVehicleName() {
        return VehicleName;
    }

    public void setVehicleName(String vehicleName) {
        VehicleName = vehicleName;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getVehicleYear() {
        return VehicleYear;
    }

    public void setVehicleYear(String vehicleYear) {
        VehicleYear = vehicleYear;
    }

    public String getInsurance() {
        return Insurance;
    }

    public void setInsurance(String insurance) {
        Insurance = insurance;
    }

    public String getInsuranceExpiry() {
        return InsuranceExpiry;
    }

    public void setInsuranceExpiry(String insuranceExpiry) {
        InsuranceExpiry = insuranceExpiry;
    }

    public String getRC1() {
        return RC1;
    }

    public void setRC1(String RC1) {
        this.RC1 = RC1;
    }

    public String getRC2() {
        return RC2;
    }

    public void setRC2(String RC2) {
        this.RC2 = RC2;
    }

    public String getAttachment1() {
        return Attachment1;
    }

    public void setAttachment1(String attachment1) {
        Attachment1 = attachment1;
    }

    public String getAttachment2() {
        return Attachment2;
    }

    public void setAttachment2(String attachment2) {
        Attachment2 = attachment2;
    }

    public String getAttachment3() {
        return Attachment3;
    }

    public void setAttachment3(String attachment3) {
        Attachment3 = attachment3;
    }

    public String getAttachment4() {
        return Attachment4;
    }

    public void setAttachment4(String attachment4) {
        Attachment4 = attachment4;
    }

    public String getAttachment5() {
        return Attachment5;
    }

    public void setAttachment5(String attachment5) {
        Attachment5 = attachment5;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLicenseNumber() {
        return LicenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        LicenseNumber = licenseNumber;
    }

    public String getLicenseFront() {
        return LicenseFront;
    }

    public void setLicenseFront(String licenseFront) {
        LicenseFront = licenseFront;
    }

    public String getLicenseBack() {
        return LicenseBack;
    }

    public void setLicenseBack(String licenseBack) {
        LicenseBack = licenseBack;
    }

    public String getAdharNumber() {
        return AdharNumber;
    }

    public void setAdharNumber(String adharNumber) {
        AdharNumber = adharNumber;
    }

    public String getAdharFront() {
        return AdharFront;
    }

    public void setAdharFront(String adharFront) {
        AdharFront = adharFront;
    }

    public String getAdharBack() {
        return AdharBack;
    }

    public void setAdharBack(String adharBack) {
        AdharBack = adharBack;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAddressCity() {
        return AddressCity;
    }

    public void setAddressCity(String addressCity) {
        AddressCity = addressCity;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getSenderMobileNo() {
        return SenderMobileNo;
    }

    public void setSenderMobileNo(String senderMobileNo) {
        SenderMobileNo = senderMobileNo;
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

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getReceiverMobileNo() {
        return ReceiverMobileNo;
    }

    public void setReceiverMobileNo(String receiverMobileNo) {
        ReceiverMobileNo = receiverMobileNo;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUserProfileImageUri() {
        return UserProfileImageUri;
    }

    public void setUserProfileImageUri(String userProfileImageUri) {
        UserProfileImageUri = userProfileImageUri;
    }

    public String getUserMessageToken() {
        return UserMessageToken;
    }

    public void setUserMessageToken(String userMessageToken) {
        UserMessageToken = userMessageToken;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getUserVerify() {
        return UserVerify;
    }

    public void setUserVerify(String userVerify) {
        UserVerify = userVerify;
    }

    public ArrayList<String> getUserTrip() {
        return userTrip;
    }

    public void setUserTrip(ArrayList<String> userTrip) {
        this.userTrip = userTrip;
    }

    public ArrayList<String> getUserVehicle() {
        return userVehicle;
    }

    public void setUserVehicle(ArrayList<String> userVehicle) {
        this.userVehicle = userVehicle;
    }
}
