package com.cioc.monomerce.entites;

import com.cioc.monomerce.backend.BackendServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 20/07/18.
 */

public class ListingParent {
    public String pk, user, parentType, source, productPk, productName, productPrice, productDiscount, productDiscountedPrice, serialNo, howMuch, unit,  specifications;
    public String sku, updated, unitPerpack, created, price, discountedPrice, parent_id, id;
    public int productIntPrice, productIntDiscountedPrice;
    public boolean approved, inStock;
    public String filesPk, filesLink, filesAttachment, filesMediaType;
    public String addedCart, addedWish;
    public JSONArray itemArray;
    JSONObject jsonObject;
    JSONArray array;

    public ListingParent(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {
            this.pk = jsonObject.getString("pk");

            this.approved = jsonObject.getBoolean("approved");
            this.parentType = jsonObject.getString("parentType");
            this.source = jsonObject.getString("source");
            this.addedCart = jsonObject.getString("added_cart");
            this.addedWish = jsonObject.getString("added_saved");
            this.inStock = jsonObject.getBoolean("in_stock");

            JSONObject productObj = jsonObject.getJSONObject("product");
            this.productPk = productObj.getString("pk");
            this.productName = productObj.getString("name");
            this.howMuch = productObj.getString("howMuch");
            this.serialNo = productObj.getString("serialNo");
            this.unit = productObj.getString("unit");
            this.user = jsonObject.getString("user");
//            this.productPrice = productObj.getString("price");
            Double d = Double.parseDouble(productObj.getString("price"));
            this.productIntPrice = (int) Math.round(d);
            this.productPrice = String.valueOf(this.productIntPrice);
            this.productDiscount = productObj.getString("discount");
//            this.productDiscountedPrice = productObj.getString("discountedPrice");
            Double d1 = Double.parseDouble(productObj.getString("discountedPrice"));
            this.productIntDiscountedPrice = (int) Math.round(d1);
            this.productDiscountedPrice = String.valueOf(this.productIntDiscountedPrice);
            String str = jsonObject.getString("specifications");
            this.specifications = str;

            this.itemArray = jsonObject.getJSONArray("product_variants");
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject jsonObj = itemArray.getJSONObject(i);
                this.sku = jsonObj.getString("sku");
                this.updated = jsonObj.getString("updated");
                this.unitPerpack = jsonObj.getString("unitPerpack");
                this.created = jsonObj.getString("created");
                this.price = jsonObj.getString("price");
                this.discountedPrice = jsonObj.getString("discountedPrice");
                this.parent_id = jsonObj.getString("parent_id");
                this.id = jsonObj.getString("id");
            }


            JSONArray filesArray = jsonObject.getJSONArray("files");
            if (filesArray==null|| filesArray.equals("null")|| filesArray.equals("[]")){
                this.filesAttachment = BackendServer.url+"/static/images/ecommerce.jpg";
            } else {
                array = filesArray;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject filesObject = array.getJSONObject(i);
                    this.filesPk = filesObject.getString("pk");
                    this.filesLink = filesObject.getString("link");
                    String filesAttachment = filesObject.getString("attachment");
                    if (filesAttachment.equals("null") || filesAttachment.equals("") || filesAttachment == null) {
                        this.filesAttachment = BackendServer.url + "/static/images/ecommerce.jpg";
                    } else {
                        this.filesAttachment = filesAttachment;
                    }
                    this.filesMediaType = filesObject.getString("mediaType");
                }
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

    public int getProductIntPrice() {
        return productIntPrice;
    }

    public void setProductIntPrice(int productIntPrice) {
        this.productIntPrice = productIntPrice;
    }

    public int getProductIntDiscountedPrice() {
        return productIntDiscountedPrice;
    }

    public void setProductIntDiscountedPrice(int productIntDiscountedPrice) {
        this.productIntDiscountedPrice = productIntDiscountedPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUnitPerpack() {
        return unitPerpack;
    }

    public void setUnitPerpack(String unitPerpack) {
        this.unitPerpack = unitPerpack;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(String howMuch) {
        this.howMuch = howMuch;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONArray getItemArray() {
        return itemArray;
    }

    public void setItemArray(JSONArray itemArray) {
        this.itemArray = itemArray;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
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

    public String getAddedCart() {
        return addedCart;
    }

    public void setAddedCart(String addedCart) {
        this.addedCart = addedCart;
    }

    public String getAddedWish() {
        return addedWish;
    }

    public void setAddedWish(String addedWish) {
        this.addedWish = addedWish;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
