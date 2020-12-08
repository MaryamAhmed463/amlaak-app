package com.example.amlaakapp.model;

public class Invoice {
    String date;
    String vCode;
    String fuleType;
    String paymentMethode;
    String station;
    String longitude, latitude;
    String invoiceImgUrl;
    String invoiceID;
    Double volume, amount, vkm;
    String vID;
    String unitPrice;
    String DID, DFN, DSN, DLN;

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public String getDFN() {
        return DFN;
    }

    public void setDFN(String DFN) {
        this.DFN = DFN;
    }

    public String getDSN() {
        return DSN;
    }

    public void setDSN(String DSN) {
        this.DSN = DSN;
    }

    public String getDLN() {
        return DLN;
    }

    public void setDLN(String DLN) {
        this.DLN = DLN;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }


    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getInvoiceImgUrl() {
        return invoiceImgUrl;
    }

    public void setInvoiceImgUrl(String invoiceImgUrl) {
        this.invoiceImgUrl = invoiceImgUrl;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getvID() {
        return vID;
    }

    public void setvID(String vID) {
        this.vID = vID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getvCode() {
        return vCode;
    }

    public void setvCode(String vCode) {
        this.vCode = vCode;
    }

    public String getFuleType() {
        return fuleType;
    }

    public void setFuleType(String fuleType) {
        this.fuleType = fuleType;
    }

    public String getPaymentMethode() {
        return paymentMethode;
    }

    public void setPaymentMethode(String paymentMethode) {
        this.paymentMethode = paymentMethode;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getVkm() {
        return vkm;
    }

    public void setVkm(Double vkm) {
        this.vkm = vkm;
    }
}
