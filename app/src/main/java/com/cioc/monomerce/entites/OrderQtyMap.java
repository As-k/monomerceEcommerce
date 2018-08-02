package com.cioc.monomerce.entites;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 02/08/18.
 */

public class OrderQtyMap {
    private boolean refundStatus, cancellable;
    public String orderQtyPk, orderTrackingLog, orderProduct, orderQty, orderTotalAmount, orderStatus, orderUpdated, orderRefundAmount, orderDiscountAmount,
            orderCourierName, orderCourierAWBNo, orderNotes, orderProductName, orderProductPrice, orderPpAfterDiscount;
    JSONObject object;

    public OrderQtyMap(JSONObject jsonObject) throws JSONException {
        this.object = jsonObject;

        this.orderQtyPk = object.getString("pk");
        this.orderTrackingLog = object.getString("trackingLog");
        this.orderProduct = object.getString("product");
        this.orderQty = object.getString("qty");
        this.orderTotalAmount = object.getString("totalAmount");
        this.orderStatus = object.getString("status");
        this.orderUpdated = object.getString("updated");
        this.orderRefundAmount = object.getString("refundAmount");
        this.orderDiscountAmount = object.getString("discountAmount");
        this.refundStatus = object.getBoolean("refundStatus");
        this.cancellable = object.getBoolean("cancellable");
        this.orderCourierName = object.getString("courierName");
        this.orderCourierAWBNo = object.getString("courierAWBNo");
        this.orderNotes = object.getString("notes");
        this.orderProductName = object.getString("productName");
        this.orderProductPrice = object.getString("productPrice");
        this.orderPpAfterDiscount = object.getString("ppAfterDiscount");
    }


    public boolean isRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(boolean refundStatus) {
        this.refundStatus = refundStatus;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public String getOrderQtyPk() {
        return orderQtyPk;
    }

    public void setOrderQtyPk(String orderQtyPk) {
        this.orderQtyPk = orderQtyPk;
    }

    public String getOrderTrackingLog() {
        return orderTrackingLog;
    }

    public void setOrderTrackingLog(String orderTrackingLog) {
        this.orderTrackingLog = orderTrackingLog;
    }

    public String getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(String orderProduct) {
        this.orderProduct = orderProduct;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public String getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(String orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderUpdated() {
        return orderUpdated;
    }

    public void setOrderUpdated(String orderUpdated) {
        this.orderUpdated = orderUpdated;
    }

    public String getOrderRefundAmount() {
        return orderRefundAmount;
    }

    public void setOrderRefundAmount(String orderRefundAmount) {
        this.orderRefundAmount = orderRefundAmount;
    }

    public String getOrderDiscountAmount() {
        return orderDiscountAmount;
    }

    public void setOrderDiscountAmount(String orderDiscountAmount) {
        this.orderDiscountAmount = orderDiscountAmount;
    }

    public String getOrderCourierName() {
        return orderCourierName;
    }

    public void setOrderCourierName(String orderCourierName) {
        this.orderCourierName = orderCourierName;
    }

    public String getOrderCourierAWBNo() {
        return orderCourierAWBNo;
    }

    public void setOrderCourierAWBNo(String orderCourierAWBNo) {
        this.orderCourierAWBNo = orderCourierAWBNo;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public String getOrderProductName() {
        return orderProductName;
    }

    public void setOrderProductName(String orderProductName) {
        this.orderProductName = orderProductName;
    }

    public String getOrderProductPrice() {
        return orderProductPrice;
    }

    public void setOrderProductPrice(String orderProductPrice) {
        this.orderProductPrice = orderProductPrice;
    }

    public String getOrderPpAfterDiscount() {
        return orderPpAfterDiscount;
    }

    public void setOrderPpAfterDiscount(String orderPpAfterDiscount) {
        this.orderPpAfterDiscount = orderPpAfterDiscount;
    }
}
