package com.cioc.monomerce.entites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 26/07/18.
 */

public class Cart {
    public String pk, user, quantity, type;
//            parentType, source, productPk, productName, productPrice, productDiscount, productDiscountedPrice, specifications;
//    boolean approved;
//    public String filesPk, filesLink, filesAttachment, filesMediaType;
    JSONObject jsonObject;
    ListingParent listingParent;
    ArrayList<ListingParent> parents = new ArrayList<>();

    public Cart() {
    }

    public Cart(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {

            this.pk = jsonObject.getString("pk");
            this.user = jsonObject.getString("user");
            this.quantity = jsonObject.getString("qty");
            this.type = jsonObject.getString("typ");
            JSONObject product = jsonObject.getJSONObject("product");
            this.listingParent = new ListingParent(product);
            this.parents.add(listingParent);
//            this.approved = jsonObject.getBoolean("approved");
//            this.parentType = jsonObject.getString("parentType");
//            this.source = jsonObject.getString("source");
//
//            JSONObject productObj = jsonObject.getJSONObject("product");
//            this.productPk = productObj.getString("pk");
//            this.productName = productObj.getString("name");
//            this.productPrice = productObj.getString("price");
//            this.productDiscount = productObj.getString("discount");
//            this.productDiscountedPrice = productObj.getString("discountedPrice");
//
//            String str = jsonObject.getString("specifications");
//            this.specifications = str;
//
//            JSONArray filesArray = jsonObject.getJSONArray("files");
//            for(int i=0; i<filesArray.length(); i++) {
//                JSONObject filesObject = filesArray.getJSONObject(i);
//                this.filesPk = filesObject.getString("pk");
//                this.filesLink = filesObject.getString("link");
//                this.filesAttachment = filesObject.getString("attachment");
//                this.filesMediaType = filesObject.getString("mediaType");
//            }
//
//            JSONArray dfsArray = jsonObject.getJSONArray("dfs");
//            for (int i=0; i<dfsArray.length(); i++) {
//                String dtsPkStr = dfsArray.getString(i);
//            }

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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ListingParent getListingParent() {
        return listingParent;
    }

    public ArrayList<ListingParent> getParents() {
        return parents;
    }

    public void setParents(ArrayList<ListingParent> parents) {
        this.parents = parents;
    }

    public void setListingParent(ListingParent listingParent) {
        this.listingParent = listingParent;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
