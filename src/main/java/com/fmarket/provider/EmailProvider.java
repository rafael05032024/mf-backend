package com.fmarket.provider;

public interface EmailProvider {

    void sendEmail(String to, String subject, String html);
}
