package com.cioc.monomerce.backend;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by admin on 19/07/18.
 */

public class BackendServer {

//    public String url = "http://10.0.2.2:8000/";
    public static String url = "http://192.168.1.123:8080/";
//    public static String url = "https://sterlingselect.in/";
//    public static String url = "http://192.168.43.9:8000/";
    public Context context;
    SessionManager sessionManager;

    public BackendServer(Context context){
        this.context = context;
    }

    public AsyncHttpClient getHTTPClient(){
        sessionManager = new SessionManager(context);
        final String csrftoken = sessionManager.getCsrfId();
        final String sessionid = sessionManager.getSessionId();
//        AsyncHttpClient client = new AsyncHttpClient(true, 80,443);
        AsyncHttpClient client = new AsyncHttpClient();
//        if (sessionid.length()>csrftoken.length()) {
//            client.addHeader("X-CSRFToken" , sessionid);
//            client.addHeader("COOKIE", String.format("csrftoken=%s; sessionid=%s", sessionid, csrftoken));
//        } else {
            client.addHeader("X-CSRFToken" , csrftoken);
            client.addHeader("COOKIE", String.format("csrftoken=%s; sessionid=%s", csrftoken, sessionid));
//        }
        return client;
    }
}
