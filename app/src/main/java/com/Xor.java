package com;

import java.io.ByteArrayOutputStream;

public class Xor {
    private static String regex = "(.{2})";

    public static String sljz(int value, int length) {
        String count = Integer.toHexString(value);
        if ((count.length() % 2) != 0) {
            count = "0" + count;
        }
        StringBuffer sb = new StringBuffer(count);
        int mlength = sb.length() / 2;
        if (length != mlength) {
            String z = "00";
            for (int i = 0; i < length - mlength; i++) {
                sb.insert(0, z);
            }
        }
        return sb.toString();
    }

    public static String ql(String context) {
        return context.replaceAll(regex, "$1 ").replaceAll("00", "").replaceAll(" ", "");
    }

    public static String autoGenericCode(int id, int count, int ldms, int ldys, int zl, int status) {
        String slcount = sljz(count, 2);
        String slid = sljz(id, 2);
        String slldms = sljz(ldms, 1);
        String slldys = sljz(ldys, 1);
        String szl = sljz(zl, 1);
        String sstatus = sljz(status, 1);
        StringBuffer sb = new StringBuffer();
        sb.append("26 57 4D 53 5C " + szl + "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
        sb.append(" " + slcount);
        sb.append("00 " + sstatus + slid);
        sb.append("00 00 " + slldms + slldys);
        sb.append(xor(sb.toString()).toUpperCase());
        sb.append(" 0D 0A");
        return "client " + sb.toString().toUpperCase();
    }

    public static String newAutoGenericCode2(int id, String clientCode, String xh, String hubIp, int count, int ldms,
                                             int ldys, int zl, int status, int fnq, int isCleared) {
        String head = "26574D5338", slcount = sljz(count, 2), sxh = encode(null == xh ? "" : xh, 25),
                shubIp = encode(null == hubIp ? "" : hubIp, 15), slid = sljz(id, 2), slldms = sljz(ldms, 1),
                slldys = sljz(ldys, 1), szl = sljz(zl, 1), sstatus = sljz(status, 1), sfnq = sljz(fnq, 1),
                sclientCode = encode(null == clientCode ? "" : clientCode, 6), sisCleared = sljz(isCleared, 1);
        StringBuffer sb = new StringBuffer();
        sb.append(head + szl + sxh + sclientCode + shubIp + slcount + sstatus + slid + sisCleared + slldms + slldys + sfnq);
        sb.append(xor(sb.toString()).toUpperCase());
        sb.append("0D0A");
        return sb.toString().toUpperCase().replaceAll(regex, "$1 ");
    }

    public static String xor(String content) {
        content = content.replaceAll(regex, "$1 ");
        String str = "";
        String returnValue = null;
        for (int i = 0; i < content.length(); i++) {
            str += content.substring(i, i + 1);
        }
        content = str.trim();
        String[] contentSplit = content.split(" ");
        int xorSum = 0;
        for (int i = 0; i < contentSplit.length; i++) {
            xorSum = xorSum ^ Integer.parseInt(contentSplit[i], 16);
        }
        if (xorSum < 10) {
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(xorSum);
            returnValue = sb.toString();
        }
        returnValue = Integer.toHexString(xorSum);
        if (1 == returnValue.length()) {
            returnValue = "0" + returnValue;
        }
        return returnValue;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static String hexString = "0123456789ABCDEF";

    public static String encode(String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    public static String encode(String str, int length) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        int mlength = sb.length() / 2;
        if (length != mlength) {
            String z = "00";
            for (int i = 0; i < length - mlength; i++) {
                sb.insert(0, z);
            }
        }
        return sb.toString();
    }

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        for (int i = 0; i < bytes.length(); i += 2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        }
        return new String(baos.toByteArray());
    }

    public static String careateLockCommand(int status, int chNumber) {
        return String.valueOf(status) + String.valueOf(chNumber);
    }
}