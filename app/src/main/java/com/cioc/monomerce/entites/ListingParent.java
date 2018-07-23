package com.cioc.monomerce.entites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 20/07/18.
 */

public class ListingParent {
    public String pk, user, parentType, source, productPk, productName, productPrice, productDiscount, productDiscountedPrice;
    boolean approved;
    public String filesPk, filesLink, filesAttachment, filesMediaType;
    JSONObject jsonObject;

    public ListingParent() {
    }

    public ListingParent(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {
            this.pk = jsonObject.getString("pk");
            this.approved = jsonObject.getBoolean("approved");
            this.parentType = jsonObject.getString("parentType");
            this.source = jsonObject.getString("source");

            JSONObject productObj = jsonObject.getJSONObject("product");
            this.productPk = productObj.getString("pk");
            this.productName = productObj.getString("name");
            this.productPrice = productObj.getString("price");
            this.productDiscount = productObj.getString("discount");
            this.productDiscountedPrice = productObj.getString("discountedPrice");

            JSONArray filesArray = jsonObject.getJSONArray("files");
            for(int i=0; i<filesArray.length(); i++) {
                JSONObject filesObject = filesArray.getJSONObject(i);
                this.filesPk = filesObject.getString("pk");
                this.filesLink = filesObject.getString("link");
                this.filesAttachment = filesObject.getString("attachment");
                this.filesMediaType = filesObject.getString("mediaType");
            }

            JSONArray dfsArray = jsonObject.getJSONArray("dfs");
            for (int i=0; i<dfsArray.length(); i++) {
                String dtsPkStr = dfsArray.getString(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


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

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProductPk() {
        return productPk;
    }

    public void setProductPk(String productPk) {
        this.productPk = productPk;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductDiscountedPrice() {
        return productDiscountedPrice;
    }

    public void setProductDiscountedPrice(String productDiscountedPrice) {
        this.productDiscountedPrice = productDiscountedPrice;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getFilesPk() {
        return filesPk;
    }

    public void setFilesPk(String filesPk) {
        this.filesPk = filesPk;
    }

    public String getFilesLink() {
        return filesLink;
    }

    public void setFilesLink(String filesLink) {
        this.filesLink = filesLink;
    }

    public String getFilesAttachment() {
        return filesAttachment;
    }

    public void setFilesAttachment(String filesAttachment) {
        this.filesAttachment = filesAttachment;
    }

    public String getFilesMediaType() {
        return filesMediaType;
    }

    public void setFilesMediaType(String filesMediaType) {
        this.filesMediaType = filesMediaType;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
