package org.taxivyapar.app;

public class Modelreview {
     String TimeStamp;
    String Id;
    String SenderMobileNo;
    String SenderName;
    String UserName;
    String UserPhoneNumber;
    String BookingId;
    String TransactionId;
    double userRating;
    String Remark;

    public Modelreview() {
    }

    public Modelreview(String timeStamp, String id, String senderMobileNo, String senderName, String userName,
                       String userPhoneNumber, String bookingId, String transactionId, double userRating, String remark) {
        TimeStamp = timeStamp;
        Id = id;
        SenderMobileNo = senderMobileNo;
        SenderName = senderName;
        UserName = userName;
        UserPhoneNumber = userPhoneNumber;
        BookingId = bookingId;
        TransactionId = transactionId;
        this.userRating = userRating;
        Remark = remark;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
