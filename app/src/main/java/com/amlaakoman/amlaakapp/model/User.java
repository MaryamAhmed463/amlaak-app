package com.amlaakoman.amlaakapp.model;

public class User {
    String sFName;
    String sSName;
    String sLName;
    String sEmail;
    String sPhone;
    String sPass;
    String sCPass;
    String sUserCode;
    String sUserId;
    String sUserRole;
    String sUserStatus;


    public User() {
    }

    public String getsUserStatus() {
        return sUserStatus;
    }

    public void setsUserStatus(String sUserStatus) {
        this.sUserStatus = sUserStatus;
    }

    public String getsUserRole() {
        return sUserRole;
    }

    public void setsUserRole(String sUserRole) {
        this.sUserRole = sUserRole;
    }

    public String getsFName() {
        return sFName;
    }

    public void setsFName(String sFName) {
        this.sFName = sFName;
    }

    public String getsSName() {
        return sSName;
    }

    public void setsSName(String sSName) {
        this.sSName = sSName;
    }

    public String getsLName() {
        return sLName;
    }

    public void setsLName(String sLName) {
        this.sLName = sLName;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getsPass() {
        return sPass;
    }

    public void setsPass(String sPass) {
        this.sPass = sPass;
    }

    public String getsCPass() {
        return sCPass;
    }

    public void setsCPass(String sCPass) {
        this.sCPass = sCPass;
    }

    public String getsUserCode() {
        return sUserCode;
    }

    public void setsUserCode(String sUserCode) {
        this.sUserCode = sUserCode;
    }

    public String getsUserId() {
        return sUserId;
    }

    public void setsUserId(String sUserId) {
        this.sUserId = sUserId;
    }
}
