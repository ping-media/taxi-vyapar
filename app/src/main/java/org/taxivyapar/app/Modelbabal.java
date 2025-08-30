package org.taxivyapar.app;

public class Modelbabal {
    String Id, Price, TimeStamp, Amount, Date, Status, Time, Type, UPI, PaymentSc,
            userMobileNo, userName, AccountNo, ifseCode, Number, SenderName, SenderMobileNo,
            ReceiverName, ReceiverMobileNo, description, wallet, PaymentAmount,
            PaymentCommission, BookingId, TransactionId;

    public Modelbabal() {
    }

    public Modelbabal(String id, String price, String timeStamp, String amount, String date, String status,
                      String time, String type, String UPI, String paymentSc, String userMobileNo, String userName,
                      String accountNo, String ifseCode, String number, String senderName, String senderMobileNo,
                      String receiverName, String receiverMobileNo, String description, String wallet,
                      String paymentAmount, String paymentCommission, String bookingId, String transactionId) {
        Id = id;
        Price = price;
        TimeStamp = timeStamp;
        Amount = amount;
        Date = date;
        Status = status;
        Time = time;
        Type = type;
        this.UPI = UPI;
        PaymentSc = paymentSc;
        this.userMobileNo = userMobileNo;
        this.userName = userName;
        AccountNo = accountNo;
        this.ifseCode = ifseCode;
        Number = number;
        SenderName = senderName;
        SenderMobileNo = senderMobileNo;
        ReceiverName = receiverName;
        ReceiverMobileNo = receiverMobileNo;
        this.description = description;
        this.wallet = wallet;
        PaymentAmount = paymentAmount;
        PaymentCommission = paymentCommission;
        BookingId = bookingId;
        TransactionId = transactionId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUPI() {
        return UPI;
    }

    public void setUPI(String UPI) {
        this.UPI = UPI;
    }

    public String getPaymentSc() {
        return PaymentSc;
    }

    public void setPaymentSc(String paymentSc) {
        PaymentSc = paymentSc;
    }

    public String getUserMobileNo() {
        return userMobileNo;
    }

    public void setUserMobileNo(String userMobileNo) {
        this.userMobileNo = userMobileNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public String getIfseCode() {
        return ifseCode;
    }

    public void setIfseCode(String ifseCode) {
        this.ifseCode = ifseCode;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
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

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getReceiverMobileNo() {
        return ReceiverMobileNo;
    }

    public void setReceiverMobileNo(String receiverMobileNo) {
        ReceiverMobileNo = receiverMobileNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
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
}
