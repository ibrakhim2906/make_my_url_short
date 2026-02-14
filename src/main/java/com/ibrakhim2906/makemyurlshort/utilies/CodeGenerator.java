package com.ibrakhim2906.makemyurlshort.utilies;

import java.security.SecureRandom;

public class CodeGenerator {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static SecureRandom RNG = new SecureRandom();

    public static String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i=0; i<length; i++) {
            sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }
}
