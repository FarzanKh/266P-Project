package com.sample.bankapp;

import java.util.regex.Pattern;

public class FormatChecker {
    private static final double MAX_INPUT = 4294967295.99;

    public static boolean isValidUsernameFormat(String username){
        return username.length() >= 1 && username.length() <= 127 && Pattern.matches("[_\\-\\.0-9a-z]*",username);
    }

    public static boolean isValidPasswordFormat(String password){
        return password.length() >= 6 && password.length() <= 127 && Pattern.matches("[_\\-\\.0-9a-z]*",password);
    }

    public static boolean isValidNumberFormat(String number){
        return number.length() >= 4 && number.length() <= 13 && Pattern.matches("[0-9.]+",number) && Pattern.matches("(0|[1-9][0-9]*).[0-9]{2}",number) && Double.parseDouble(number) <= MAX_INPUT;
    }
}
