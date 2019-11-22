package com.mp.model;

import java.util.ArrayList;

public class PhoneNumber {
    public static ArrayList<String> phone = new ArrayList<>();

    public static ArrayList<String> getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        PhoneNumber.phone.add(phone);
    }

    public static void clearPhone() {
        PhoneNumber.phone.clear();
    }
}
