package com.mp.model;

public class User {

    private static String phone;
    private static String email;
    private static String pin;
    private static String name;
    private static Integer type;
    private static String image;
    private static Integer balance;
    private static Integer status;
    private static String imei;

    public static String getImei() {
        return imei;
    }

    public static void setImei(String imei) {
        User.imei = imei;
    }

    public static String getUrl() {
        return "https://multipayment.co/api";
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        User.phone = phone;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getPin() {
        return pin;
    }

    public static void setPin(String pin) {
        User.pin = pin;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static Integer getType() {
        return type;
    }

    public static void setType(Integer type) {
        User.type = type;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        User.image = image;
    }

    public static Integer getBalance() {
        return balance;
    }

    public static void setBalance(Integer balance) {
        User.balance = balance;
    }

    public static Integer getStatus() {
        return status;
    }

    public static void setStatus(Integer status) {
        User.status = status;
    }
}
