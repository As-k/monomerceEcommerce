package com.cioc.monomerce.entites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 01/08/18.
 */

public class Order {
    public String pk, created, updated, totalAmount, paymentMode, paymentRefId, paymentChannel, modeOfShopping, paidAmount;
    public String promoCode, status, approved,  landMark, street, city, state, pincode, country, mobileNo, promoDiscount;
    private boolean paymentStatus;//, refundStatus, cancellable;
//    public String orderQtyPk, orderTrackingLog, orderProduct, orderQty, orderTotalAmount, orderStatus, orderUpdated, orderRefundAmount, orderDiscountAmount,
//    orderCourierName, orderCourierAWBNo, orderNotes, orderProductName, orderProductPrice, orderPpAfterDiscount;

    JSONObject jsonObject;
    ArrayList<OrderQtyMap> orderQtyMaps = new ArrayList<>();

    public Order(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;

        this.pk = jsonObject.getString("pk");
        this.created = jsonObject.getString("created");
        this.updated = jsonObject.getString("updated");
        this.totalAmount = jsonObject.getString("totalAmount");
        this.paymentMode = jsonObject.getString("paymentMode");
        this.paymentRefId = jsonObject.getString("paymentRefId");
        this.paymentChannel = jsonObject.getString("paymentChannel");
        this.modeOfShopping = jsonObject.getString("modeOfShopping");
        this.paidAmount = jsonObject.getString("paidAmount");
        this.paymentStatus = jsonObject.getBoolean("paymentStatus");
        this.promoCode = jsonObject.getString("promoCode");
        String approved = jsonObject.getString("approved");
        if (approved.equals("null") || approved==null) {
            this.approved = "";
        } else
            this.approved = approved;

        this.status = jsonObject.getString("status");
        this.landMark = jsonObject.getString("landMark");
        this.street = jsonObject.getString("street");
        this.city = jsonObject.getString("city");
        this.state = jsonObject.getString("state");
        this.pincode = jsonObject.getString("pincode");
        this.country = jsonObject.getString("country");
        this.mobileNo = jsonObject.getString("mobileNo");
        this.promoDiscount = jsonObject.getString("promoDiscount");


        JSONArray jsonArray = jsonObject.getJSONArray("orderQtyMap");
        for (int i=0; i<jsonArray.length(); i++){
            JSONObject object = jsonArray.getJSONObject(i);
            OrderQtyMap qtyMap = new OrderQtyMap(object);
            this.orderQtyMaps.add(qtyMap);
//            this.orderQtyPk = object.getString("pk");
//            this.orderTrackingLog = object.getString("trackingLog");
//            this.orderProduct = object.getString("product");
//            this.orderQty = object.getString("qty");
//            this.orderTotalAmount = object.getString("totalAmount");
//            this.orderStatus = object.getString("status");
//            this.orderUpdated = object.getString("updated");
//            this.orderRefundAmount = object.getString("refundAmount");
//            this.orderDiscountAmount = object.getString("discountAmount");
//            this.refundStatus = object.getBoolean("refundStatus");
//            this.cancellable = object.getBoolean("cancellable");
//            this.orderCourierName = object.getString("courierName");
//            this.orderCourierAWBNo = object.getString("courierAWBNo");
//            this.orderNotes = object.getString("notes");
//            this.orderProductName = object.getString("productName");
//            this.orderProductPrice = object.getString("productPrice");
//            this.orderPpAfterDiscount = object.getString("ppAfterDiscount");
        }



    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentRefId() {
        return paymentRefId;
    }

    public void setPaymentRefId(String paymentRefId) {
        this.paymentRefId = paymentRefId;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getModeOfShopping() {
        return modeOfShopping;
    }

    public void setModeOfShopping(String modeOfShopping) {
        this.modeOfShopping = modeOfShopping;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }


    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPromoDiscount() {
        return promoDiscount;
    }

    public void setPromoDiscount(String promoDiscount) {
        this.promoDiscount = promoDiscount;
    }

//    public String getOrderQtyPk() {
//        return orderQtyPk;
//    }
//
//    public void setOrderQtyPk(String orderQtyPk) {
//        this.orderQtyPk = orderQtyPk;
//    }
//
//    public String getOrderTrackingLog() {
//        return orderTrackingLog;
//    }
//
//    public void setOrderTrackingLog(String orderTrackingLog) {
//        this.orderTrackingLog = orderTrackingLog;
//    }
//
//    public String getOrderProduct() {
//        return orderProduct;
//    }
//
//    public void setOrderProduct(String orderProduct) {
//        this.orderProduct = orderProduct;
//    }
//
//    public String getOrderQty() {
//        return orderQty;
//    }
//
//    public void setOrderQty(String orderQty) {
//        this.orderQty = orderQty;
//    }
//
//    public String getOrderTotalAmount() {
//        return orderTotalAmount;
//    }
//
//    public void setOrderTotalAmount(String orderTotalAmount) {
//        this.orderTotalAmount = orderTotalAmount;
//    }
//
//    public String getOrderStatus() {
//        return orderStatus;
//    }
//
//    public void setOrderStatus(String orderStatus) {
//        this.orderStatus = orderStatus;
//    }
//
//    public String getOrderUpdated() {
//        return orderUpdated;
//    }
//
//    public void setOrderUpdated(String orderUpdated) {
//        this.orderUpdated = orderUpdated;
//    }
//
//    public String getOrderRefundAmount() {
//        return orderRefundAmount;
//    }
//
//    public void setOrderRefundAmount(String orderRefundAmount) {
//        this.orderRefundAmount = orderRefundAmount;
//    }
//
//    public String getOrderDiscountAmount() {
//        return orderDiscountAmount;
//    }
//
//    public void setOrderDiscountAmount(String orderDiscountAmount) {
//        this.orderDiscountAmount = orderDiscountAmount;
//    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

//    public boolean isRefundStatus() {
//        return refundStatus;
//    }
//
//    public void setRefundStatus(boolean refundStatus) {
//        this.refundStatus = refundStatus;
//    }
//
//    public boolean isCancellable() {
//        return cancellable;
//    }
//
//    public void setCancellable(boolean cancellable) {
//        this.cancellable = cancellable;
//    }
//
//    public String getOrderCourierName() {
//        return orderCourierName;
//    }
//
//    public void setOrderCourierName(String orderCourierName) {
//        this.orderCourierName = orderCourierName;
//    }
//
//    public String getOrderCourierAWBNo() {
//        return orderCourierAWBNo;
//    }
//
//    public void setOrderCourierAWBNo(String orderCourierAWBNo) {
//        this.orderCourierAWBNo = orderCourierAWBNo;
//    }
//
//    public String getOrderNotes() {
//        return orderNotes;
//    }
//
//    public void setOrderNotes(String orderNotes) {
//        this.orderNotes = orderNotes;
//    }
//
//    public String getOrderProductName() {
//        return orderProductName;
//    }
//
//    public void setOrderProductName(String orderProductName) {
//        this.orderProductName = orderProductName;
//    }
//
//    public String getOrderProductPrice() {
//        return orderProductPrice;
//    }
//
//    public void setOrderProductPrice(String orderProductPrice) {
//        this.orderProductPrice = orderProductPrice;
//    }
//
//    public String getOrderPpAfterDiscount() {
//        return orderPpAfterDiscount;
//    }
//
//    public void setOrderPpAfterDiscount(String orderPpAfterDiscount) {
//        this.orderPpAfterDiscount = orderPpAfterDiscount;
//    }

    public ArrayList<OrderQtyMap> getOrderQtyMaps() {
        return orderQtyMaps;
    }

    public void setOrderQtyMaps(ArrayList<OrderQtyMap> orderQtyMaps) {
        this.orderQtyMaps = orderQtyMaps;
    }
}
