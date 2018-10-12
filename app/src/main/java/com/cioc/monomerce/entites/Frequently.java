package com.cioc.monomerce.entites;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 31/07/18.
 */

public class Frequently {
    public String pk, created, user, questions, answer;
    public JSONObject object;

    public Frequently(JSONObject object) throws JSONException {
        this.object = object;

        this.pk = object.getString("pk");
        this.created = object.getString("created");
        this.user = object.getString("user");
        this.questions = object.getString("ques");
        this.answer = object.getString("ans");
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }
}
