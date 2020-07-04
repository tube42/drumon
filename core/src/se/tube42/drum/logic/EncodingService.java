
package se.tube42.drum.logic;

import java.io.*;

import se.tube42.lib.service.*;
import se.tube42.drum.data.*;


/**
 * handles quasi-base64 encoding of binary data
 */

public final class EncodingService
{
    private final static String BASE64_STR =
          "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
          "abcdefghijklmnopqrstuvwxyz" +
          "0123456789+/=";

    private final static byte [] BASE64 = BASE64_STR.getBytes();


    // ------------------------------------------------
    //  encode

    public static String encode64(byte []bs)
    {
        final int cnt = bs.length;
        final int pad = cnt % 3 == 0 ? 0 : 3 - (cnt % 3);

        // System.out.println("CNT = " + cnt + ", PAD = " + pad);
        int off = 0;
        byte [] chars = new byte[1 + ((cnt + pad) * 4 ) / 3];


        chars[off++] = BASE64[pad];
        for(int i = 0; i < cnt; ) {
            int i0 = 0xFF & (int)bs[i++];
            if(i < cnt) i0 |= (0xFF & (int)bs[i++]) << 8;
            if(i < cnt) i0 |= (0xFF & (int)bs[i++]) << 16;

            chars[off++] = BASE64[i0 & 63]; i0 >>= 6;
            chars[off++] = BASE64[i0 & 63]; i0 >>= 6;
            chars[off++] = BASE64[i0 & 63]; i0 >>= 6;
            chars[off++] = BASE64[i0 & 63]; i0 >>= 6;
        }

        return new String(chars);
    }

    // ------------------------------------------------
    //  decode

    public static byte [] decode64(String str)
    {
        if(!isValid64(str))
            return null;


        byte [] chars = str.getBytes();
        int off = 0;
        int cnt = chars.length - 1;
        int pad = BASE64_STR.indexOf(chars[off++]);

        byte [] ret = new byte[cnt * 3 / 4 - pad];


        // System.out.println("DECODE: len= " + chars.length + " pad=" + pad + " ret.length= " + ret.length);

        for(int i = 0; i < ret.length; ) {
            int i0 = BASE64_STR.indexOf(chars[off++]);
            i0 |= BASE64_STR.indexOf(chars[off++]) << 6;
            i0 |= BASE64_STR.indexOf(chars[off++]) << 12;
            i0 |= BASE64_STR.indexOf(chars[off++]) << 18;

            for(int j = 0; j < 3 && i < ret.length; j++) {
                ret[i++] = (byte)(i0 & 0xFF);
                i0 >>= 8;
            }


        }

        return ret;
    }

    // ------------------------------------------------
    //  checks

    public static boolean isValid64(String str)
    {
        final int len = str.length();

        if(len < 1)
            return false;

        if( (len - 1) % 4 != 0)
            return false;

        for(int i = 0; i < len; i++)
            if( BASE64_STR.indexOf(str.charAt(i)) < 0)
                return false;

        return true;
    }


    // ------------------------------------------------

    public static char nibToHex(int n)
    {
        n &= 15;

        if(n > 9)
            return (char)(n + 'A' - 10);
        else
            return (char)(n + '0');
    }

    public static String byteToHex(int b)
    {
        char [] cs = new char[2];
        cs[0] = nibToHex(b >> 4);
        cs[1] = nibToHex(b);
        return new String(cs);
    }

    public static String shortToHex(int s)
    {
        char [] cs = new char[4];

        cs[0] = nibToHex(s >> 12);
        cs[1] = nibToHex(s >> 8);
        cs[2] = nibToHex(s >> 4);
        cs[3] = nibToHex(s);
        return new String(cs);
    }

    /*
    public static void test()
    {

        for(int i = 0; i < 1000; i++) {
            byte []din = new byte[i];
            for(int j = 0; j < i; j++)
                din[j] = (byte) j;

            String s = encode(din);
            // System.out.println(" " + i + "\t" + s);

            byte [] dout = decode(s);

            if(dout == null)
                System.out.println("GOT NULL");
            else if(din.length != dout.length)
                System.out.println("BAD SIZE: " + din.length + " " + dout.length);
            else
                for(int k = 0; k < din.length; k++)
                    if(din[k] != dout[k])
                        System.out.println("BAD DATA at " + k + ": " + din[k] + " " + dout[k]);

        }
        System.exit(0);
       }
     */
}
