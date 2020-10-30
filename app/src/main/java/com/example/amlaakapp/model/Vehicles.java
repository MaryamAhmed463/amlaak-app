package com.example.amlaakapp.model;

import java.util.ArrayList;
import java.util.List;

public class Vehicles {
    String VID,VCode,VName,VType,VFuelType,VTankCapacity,VOpeningKm,VImageUrl,VModel,VPaymentMethod;
    List<String> Vdriver = new ArrayList<>();

    public String getVID() {
        return VID;
    }

    public void setVID(String VID) {
        this.VID = VID;
    }

    public String getVCode() {
        return VCode;
    }

    public void setVCode(String VCode) {
        this.VCode = VCode;
    }

    public String getVName() {
        return VName;
    }

    public void setVName(String VName) {
        this.VName = VName;
    }

    public String getVType() {
        return VType;
    }

    public void setVType(String VType) {
        this.VType = VType;
    }

    public String getVFuelType() {
        return VFuelType;
    }

    public void setVFuelType(String VFuelType) {
        this.VFuelType = VFuelType;
    }

    public String getVTankCapacity() {
        return VTankCapacity;
    }

    public void setVTankCapacity(String VTankCapacity) {
        this.VTankCapacity = VTankCapacity;
    }

    public String getVOpeningKm() {
        return VOpeningKm;
    }

    public void setVOpeningKm(String VOpeningKm) {
        this.VOpeningKm = VOpeningKm;
    }

    public String getVImageUrl() {
        return VImageUrl;
    }

    public void setVImageUrl(String VImageUrl) {
        this.VImageUrl = VImageUrl;
    }

    public String getVModel() {
        return VModel;
    }

    public void setVModel(String VModel) {
        this.VModel = VModel;
    }

    public String getVPaymentMethod() {
        return VPaymentMethod;
    }

    public void setVPaymentMethod(String VPaymentMethod) {
        this.VPaymentMethod = VPaymentMethod;
    }

    public List<String> getVdriver() {
        return Vdriver;
    }

    public void setVdriver(List<String> vdriver) {
        Vdriver = vdriver;
    }
}
