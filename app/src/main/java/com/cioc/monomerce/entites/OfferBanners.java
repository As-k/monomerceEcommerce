package com.cioc.monomerce.entites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 24/07/18.
 */

public class OfferBanners {
    public String pk, user, image, imagePortrait, created, level, title, subTitle, state, params;
    boolean active;
    JSONObject jsonObject;

    public OfferBanners() {
    }

    public OfferBanners(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {
            this.pk = jsonObject.getString("pk");
            this.active = jsonObject.getBoolean("active");
            this.created = jsonObject.getString("created");
            this.image = jsonObject.getString("image");
            this.imagePortrait = jsonObject.getString("imagePortrait");
            this.level = jsonObject.getString("level");
            this.title = jsonObject.getString("title");
            this.subTitle = jsonObject.getString("subtitle");
//            this.state = jsonObject.getString("state");
            this.params = jsonObject.getString("params");


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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagePortrait() {
        return imagePortrait;
    }

    public void setImagePortrait(String imagePortrait) {
        this.imagePortrait = imagePortrait;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }



}
