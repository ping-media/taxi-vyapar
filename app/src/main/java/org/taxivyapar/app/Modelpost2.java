package org.taxivyapar.app;

public class Modelpost2 implements Comparable<Modelpost2> {
    String Address, AddressHash, AddressLat,
            AddressLng, EndDate, EndTime,
            PickAnyLocation, Remark, SenderMobileNo,
            SenderName, StartDate, StartTime,
            Status, TimeStamp, TransactionId,
            AddressCity,VehicleName;
    double radiusInM;

    public Modelpost2() {
    }

    public Modelpost2(String address, String addressHash, String addressLat, String addressLng, String endDate,
                      String endTime, String pickAnyLocation, String remark, String senderMobileNo, String senderName,
                      String startDate, String startTime, String status, String timeStamp, String transactionId,
                      String addressCity, String vehicleName, double radiusInM) {
        Address = address;
        AddressHash = addressHash;
        AddressLat = addressLat;
        AddressLng = addressLng;
        EndDate = endDate;
        EndTime = endTime;
        PickAnyLocation = pickAnyLocation;
        Remark = remark;
        SenderMobileNo = senderMobileNo;
        SenderName = senderName;
        StartDate = startDate;
        StartTime = startTime;
        Status = status;
        TimeStamp = timeStamp;
        TransactionId = transactionId;
        AddressCity = addressCity;
        VehicleName = vehicleName;
        this.radiusInM = radiusInM;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAddressHash() {
        return AddressHash;
    }

    public void setAddressHash(String addressHash) {
        AddressHash = addressHash;
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

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getPickAnyLocation() {
        return PickAnyLocation;
    }

    public void setPickAnyLocation(String pickAnyLocation) {
        PickAnyLocation = pickAnyLocation;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getAddressCity() {
        return AddressCity;
    }

    public void setAddressCity(String addressCity) {
        AddressCity = addressCity;
    }

    public String getVehicleName() {
        return VehicleName;
    }

    public void setVehicleName(String vehicleName) {
        VehicleName = vehicleName;
    }

    public double getRadiusInM() {
        return radiusInM;
    }

    public void setRadiusInM(double radiusInM) {
        this.radiusInM = radiusInM;
    }

    @Override
    public int compareTo(Modelpost2 otherModelpost) {
        return otherModelpost.getTimeStamp().compareTo(this.TimeStamp);
    }

}
