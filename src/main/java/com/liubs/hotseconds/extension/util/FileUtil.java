package com.liubs.hotseconds.extension.util;

/**
 * @author Liubsyy
 * @date 2024/6/5
 **/
public class FileUtil {

    public static boolean isClassFile(byte[] bytes) {
        // Check if the byte array has at least 4 bytes.
        if (null == bytes || bytes.length < 4) {
            return false;
        }

        // Check for the 0xCAFEBABE magic number.
        int magic = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);

        return magic == 0xCAFEBABE;
    }
}
