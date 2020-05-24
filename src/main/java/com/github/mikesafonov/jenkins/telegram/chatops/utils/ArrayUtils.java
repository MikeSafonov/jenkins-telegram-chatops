package com.github.mikesafonov.jenkins.telegram.chatops.utils;

import lombok.experimental.UtilityClass;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class ArrayUtils {

    public static String[] copyWithoutFirst(String[] input) {
        String[] tmp = new String[input.length - 1];
        System.arraycopy(input, 1, tmp, 0, input.length - 1);
        return tmp;
    }

}
