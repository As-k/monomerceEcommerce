package com.cioc.monomerce;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by admin on 30/07/18.
 */

public class SessionManager {
    Context context;
    String pk = "pk";
    ArrayList<String> pkList;

    SharedPreferences sp;
    SharedPreferences.Editor spe;

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("status", Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public String getPk() {
        return sp.getString(pk, "");
    }

    public void setPk(String pk1) {
        spe.putString(pk, pk1);
        spe.apply();
    }

    public void clearAll(){
        spe = sp.edit();
        spe.clear();
        spe.apply();
    }
}
