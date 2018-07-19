package com.cioc.monomerce.entites;

import java.util.ArrayList;

/**
 * Created by admin on 12/07/18.
 */

public class Address {
    public String city, street, pincode, state, buyerName, mobile;
    ArrayList<String> addressList = new ArrayList<>();

    public Address() {
    }

    public Address(String city, String street, String pincode, String state, String buyerName, String mobile) {
        this.city = city;
        this.street = street;
        this.pincode = pincode;
        this.state = state;
        this.buyerName = buyerName;
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void addAddressList(String addresslist) {
        this.addressList.add(addresslist);
    }

    public ArrayList<String> getAddressList(){ return this.addressList; }

}
