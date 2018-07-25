package com.cioc.monomerce.entites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 25/07/18.
 */

public class ListingLite {
    public String pk, user, parentType, source, productPk, productName, productPrice, productDiscount, productDiscountedPrice, specifications;
    boolean approved;
    public String parentPk, parentName;
    public String parentTypePk, parentTypeName, parentTypeMinCost, parentTypeVisual;
    public String fieldsPk, fieldsName, fieldsValue, fieldsType, fieldsHelpText, fieldsUnit, fieldsData;
    public String filesPk, filesLink, filesAttachment, filesMediaType;
    JSONObject jsonObject;

    public String name, value, fieldType, helpText, unit, data;
    ArrayList<Integer> size= new ArrayList<Integer>();

    public ListingLite() {
    }

    public ListingLite(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {
            this.pk = jsonObject.getString("pk");
            this.approved = jsonObject.getBoolean("approved");
            this.source = jsonObject.getString("source");

            JSONObject parentType = jsonObject.getJSONObject("parentType");
            this.parentTypePk = parentType.getString("pk");
            this.parentTypeName = parentType.getString("name");
            this.parentTypeMinCost = parentType.getString("minCost");
            this.parentTypeVisual = parentType.getString("visual");
            JSONArray fields = parentType.getJSONArray("fields");
            for (int i=0; i<fields.length(); i++) {
                JSONObject fieldsObject = fields.getJSONObject(i);
                this.fieldsPk = fieldsObject.getString("pk");
                this.fieldsName = fieldsObject.getString("name");
                this.fieldsValue = fieldsObject.getString("value");
                this.fieldsType = fieldsObject.getString("fieldType");
                this.fieldsHelpText = fieldsObject.getString("helpText");
                this.fieldsUnit = fieldsObject.getString("unit");
                this.fieldsData = fieldsObject.getString("data");
            }
            JSONObject parent = jsonObject.getJSONObject("parent");
            this.parentPk = parent.getString("pk");
            this.parentName = parent.getString("name");

            String str = jsonObject.getString("specifications");
            this.specifications = str;
            JSONArray data = new JSONArray(str);
            for (int i=0; i<data.length(); i++){
                JSONObject object = data.getJSONObject(i);
                this.name = object.getString("name");
                this.value = object.getString("value");
                this.fieldType = object.getString("fieldType");
                this.helpText = object.getString("helpText");
                this.unit = object.getString("unit");
                this.data = object.getString("data");
                size.add(i);
            }

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

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getParentPk() {
        return parentPk;
    }

    public void setParentPk(String parentPk) {
        this.parentPk = parentPk;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentTypePk() {
        return parentTypePk;
    }

    public void setParentTypePk(String parentTypePk) {
        this.parentTypePk = parentTypePk;
    }

    public String getParentTypeName() {
        return parentTypeName;
    }

    public void setParentTypeName(String parentTypeName) {
        this.parentTypeName = parentTypeName;
    }

    public String getParentTypeMinCost() {
        return parentTypeMinCost;
    }

    public void setParentTypeMinCost(String parentTypeMinCost) {
        this.parentTypeMinCost = parentTypeMinCost;
    }

    public String getParentTypeVisual() {
        return parentTypeVisual;
    }

    public void setParentTypeVisual(String parentTypeVisual) {
        this.parentTypeVisual = parentTypeVisual;
    }

    public String getFieldsPk() {
        return fieldsPk;
    }

    public void setFieldsPk(String fieldsPk) {
        this.fieldsPk = fieldsPk;
    }

    public String getFieldsName() {
        return fieldsName;
    }

    public void setFieldsName(String fieldsName) {
        this.fieldsName = fieldsName;
    }

    public String getFieldsValue() {
        return fieldsValue;
    }

    public void setFieldsValue(String fieldsValue) {
        this.fieldsValue = fieldsValue;
    }

    public String getFieldsType() {
        return fieldsType;
    }

    public void setFieldsType(String fieldsType) {
        this.fieldsType = fieldsType;
    }

    public String getFieldsHelpText() {
        return fieldsHelpText;
    }

    public void setFieldsHelpText(String fieldsHelpText) {
        this.fieldsHelpText = fieldsHelpText;
    }

    public String getFieldsUnit() {
        return fieldsUnit;
    }

    public void setFieldsUnit(String fieldsUnit) {
        this.fieldsUnit = fieldsUnit;
    }

    public String getFieldsData() {
        return fieldsData;
    }

    public void setFieldsData(String fieldsData) {
        this.fieldsData = fieldsData;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<Integer> getSize() {
        return size;
    }

    public void setSize(ArrayList<Integer> size) {
        this.size = size;
    }
}
