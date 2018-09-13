package com.cioc.monomerce.startup;

public interface SmsListener {
    void messageReceived(String messageBody);
}
