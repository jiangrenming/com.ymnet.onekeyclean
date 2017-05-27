package utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017-4-21.
 */

public class Md5Utils {

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * 加密
     *
     * @param str
     */
    public static String strCodeZm(String str) {
        //token sign
        String hash = "N#gK3OgTw#eRUI8+8bZsti78P==4s.5";
//		String hash = "sjlm20160729";
        String key = Md5(hash);

        byte[] bstr = str.getBytes();

        int keylen = key.length();
        int strlen = bstr.length;

        byte resultByte[] = new byte[bstr.length];
        for (int i = 0; i < strlen; i++) {
            int k = i % keylen;
            int x = bstr[i];
            int y = key.charAt(k);
            byte z = (byte) (x ^ y);
            resultByte[i] = z;
        }

        try {
            String r = new String(Base64.encode(resultByte, Base64.DEFAULT), "utf-8");
            return r;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String strCodeRt(String str) {
        try {
            String hash = null;
            hash = "e176de519a0a97a8";
            String key = str + hash;
            byte[] bstr = key.getBytes();
            String r = new String(Base64.encode(bstr, Base64.DEFAULT), "utf-8");
            r = r.replaceAll("[\\s*\t\n\r]", "");
            return Md5(r);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String Md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                int i = b[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
