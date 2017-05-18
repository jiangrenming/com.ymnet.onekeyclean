package com.ymnet.onekeyclean.cleanmore.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GetMD5
{
    
    /**
     * MD5的算法在RFC1321 中定义 在RFC 1321中，给出了Test suite用来检验你的实现是否正确： MD5 ("") =
     * d41d8cd98f00b204e9800998ecf8427e MD5 ("a") =
     * 0cc175b9c0f1b6a831c399e269772661 MD5 ("abc") =
     * 900150983cd24fb0d6963f7d28e17f72 MD5 ("message digest") =
     * f96b697d7cb7938d525a2f31aaf161d0 MD5 ("abcdefghijklmnopqrstuvwxyz") =
     * c3fcd3d76192e4007dfb496cca67e13b
     * 
     * @author haogj
     * 
     *         传入参数：一个字节数组 传出参数：字节数组的 MD5 结果字符串
     */
    
    public static String Md5(String plainText)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++)
            {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}
