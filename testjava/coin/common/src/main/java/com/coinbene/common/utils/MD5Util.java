package com.coinbene.common.utils;


import com.coinbene.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5Util
 */
public class MD5Util {

    public static boolean checkFile(String md5, String filePath) {
        return md5.toLowerCase().equals(encodeFile(filePath).toLowerCase());
    }

    public static String encodeFile(String filePath) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            File file = new File(filePath);

            InputStream inputStream = new FileInputStream(file);
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, md);

            byte[] buffer = new byte[1024];
            while (digestInputStream.read(buffer) > 0) ;

            md = digestInputStream.getMessageDigest();
            result = toHex(md.digest());
        } catch (Exception e) {

        }
        return result;
    }

    public static String md5(byte[] buff) {
        try {
            MessageDigest digester = MessageDigest.getInstance("md5");
            digester.update(buff);
            byte[] buffer = digester.digest();
            return toHex(buffer);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static String encode(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
            // if (type) {
            // return buf.toString(); // 32
            // } else {
            // return buf.toString().substring(8, 24);// 16
            // }
        } catch (Exception e) {
            return null;
        }
    }


    public static String MD5(String s) {
        s = s.trim() + Constants.MD5_APPEND_KEY;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

}
