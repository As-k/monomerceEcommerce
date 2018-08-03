package com.cioc.monomerce.entites;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 12/07/18.
 */

public class Address {
    public String pk, user, name, city, street, pincode, state, buyerName, mobile, country, title, landMark, lat, lon;
    ArrayList<String> addressList = new ArrayList<>();
    JSONObject object;

    public Address() {
    }

    public Address(JSONObject object) throws JSONException {
        this.object = object;

        this.pk = object.getString("pk");
        this.user = object.getString("user");
        this.city = object.getString("city");
        this.street = object.getString("street");
        this.pincode = object.getString("pincode");
        this.state = object.getString("state");
//        this.buyerName = object.getString("buyerName");
//        this.mobile = object.getString("mobile");
        this.country = object.getString("country");
        this.title = object.getString("title");
        this.landMark = object.getString("landMark");
        this.lat = object.getString("lat");
        this.lon = object.getString("lon");

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

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void addAddressList(String addresslist) {
        this.addressList.add(addresslist);
    }

    public ArrayList<String> getAddressList(){ return this.addressList; }

}
