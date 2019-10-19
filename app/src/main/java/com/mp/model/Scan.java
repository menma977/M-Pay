package com.mp.model;

public class Scan {

    private String barcodeQR;
    private String nominal;
    private Integer status;
    private String phone;
    private String name;
    private String type;

    public String getBarcodeQR() {
        return barcodeQR;
    }

    public void setBarcodeQR(String barcodeQR) {
        this.barcodeQR = barcodeQR;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
