package com.github.mikesafonov.jenkins.telegram.chatops.utils;

import com.google.common.io.BaseEncoding;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class HexUtils {
    public static String toHex(String value) {
        return BaseEncoding.base16().encode(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String fromHex(String hexString) {
        return new String(BaseEncoding.base16().decode(hexString), StandardCharsets.UTF_8);
    }
}
