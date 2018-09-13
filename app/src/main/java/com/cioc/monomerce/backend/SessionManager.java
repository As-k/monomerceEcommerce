package com.cioc.monomerce.backend;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private String csrfId = "csrftoken";
    private String sessionId = "sessionid";
    private String userName = "username";
    private String loginVerify = "login";

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("registered_status", Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public String getCsrfId() {
        return sp.getString(csrfId, "");
    }

    public void setCsrfId(String csrf) {
        spe.putString(csrfId, csrf);
        spe.apply();
    }

    public String getSessionId() {
        return sp.getString(sessionId, "");
    }

    public void setSessionId(String session) {
        spe.putString(sessionId, session);
        spe.apply();
    }
    public Boolean getLoginVerify() {
        return sp.getBoolean(loginVerify, false);
    }

    public void setLoginVerify(Boolean session) {
        spe.putBoolean(loginVerify, session);
        spe.apply();
    }

    public String getUsername() {
        return sp.getString(userName, "");
    }

    public void setUsername(String username) {
        spe.putString(userName, username);
        spe.apply();
    }

    public void clearAll(){
        spe = sp.edit();
        spe.clear();
        spe.apply();
    }
}
