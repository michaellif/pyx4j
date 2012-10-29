package com.pyx4j.tester.client;

import java.util.Random;

public class TesterUtils {
    private static final String[] words = new String[] { "this", "bunch", "of", "words", "can", "be", "used", "to", "create", "various", "text", "elements" };

    private static final Random rand = new Random();

    public static String getRandomWord() {
        return words[rand.nextInt(words.length)];
    }

    public static String getRandomString(int size) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < size) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(getRandomWord());
        }
        return sb.toString();
    }
}
