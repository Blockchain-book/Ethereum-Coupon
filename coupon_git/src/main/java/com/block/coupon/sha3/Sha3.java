package com.block.coupon.sha3;


import java.util.Formatter;


public class Sha3 {

//	返回sha3加密算法的256位加密结果
    public static String sha3(String str) {
        byte[] b = getByteArray(str);
        String s = getHexStringByByteArray(b);
        Keccak keccak = new Keccak(1600);

//        System.out.println("sha-224 = " + keccak.getHash(s, 1152, 28));
          String sha3Str=keccak.getHash(s, 1088, 32);
//        System.out.println("sha-384 = " + keccak.getHash(s, 832, 48));
//        System.out.println("sha-512 = " + keccak.getHash(s, 576, 64));
          return sha3Str;
    }

    public static byte[] getByteArray(String s) {
        return (s != null) ? s.getBytes(): null;
    }

    /**
     * Convert the byte array to a hex-string.
     *
     * @param array  byte array
     * @return  hex string
     */
    public static String getHexStringByByteArray(byte[] array) {
        if (array == null)
            return null;

        StringBuilder stringBuilder = new StringBuilder(array.length * 2);
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter(stringBuilder);
        for (byte tempByte : array)
            formatter.format("%02x", tempByte);

        return stringBuilder.toString();
    }

}
