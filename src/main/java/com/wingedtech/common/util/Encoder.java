package com.wingedtech.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

/**
 * 获取 License
 *
 * @author Jason
 * @since 2019/9/16 12:22
 */
public class Encoder {
    private final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private Object salt;
    private String algorithm;

    public Encoder() {
    }

    public Encoder(Object salt, String algorithm) {
        this.salt = salt;
        this.algorithm = algorithm;
    }

    public String encode(String rawPass) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            //加密后的字符串
            result = byteArrayToHexString(md.digest(mergePasswordAndSalt(rawPass).getBytes("utf-8")));
        } catch (Exception ex) {
        }
        return result;
    }

    public boolean isPasswordValid(String encPass, String rawPass) {
        String pass1 = "" + encPass;
        String pass2 = encode(rawPass);

        return pass1.equals(pass2);
    }

    private String mergePasswordAndSalt(String password) {
        if (password == null) {
            password = "";
        }

        if ((salt == null) || "".equals(salt)) {
            return password;
        } else {
            return password + "{" + salt.toString() + "}";
        }
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    private String byteArrayToHexString(byte[] b) {
        StringBuilder results = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            results.append(byteToHexString(b[i]));
        }
        return results.toString();
    }

    private String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public String generateLicense(String publisher, String type, String input, String expireDate, int length, int offset) {
        return encode(publisher, type, input, expireDate, length, offset);

    }

    public Boolean validateLicense(String input, String lic, int length, int offset) {
        String[] array = lic.split("-");
        String content = array[0].substring(2);
        String type = array[0].substring(0, 2);
        String group = array[2];
        int group1 = Integer.parseInt(group.substring(0, offset));
        int group2 = Integer.parseInt(group.substring((length - offset)));
        String st = contentSalt(type, array[1], group1, group2);
        Encoder encoderMd5 = new Encoder(st, "MD5");
        int index = lic.indexOf(group) + group.length() + 1;
        if (index < lic.length()) {
            String expireDate = lic.substring(index);
            System.out.println("exp:" + expireDate);
            String decrypt = decryptAes(expireDate, content.substring(0, 16));
            System.out.println(decrypt);
        }
        return content.equals(encoderMd5.encode(input));
    }

    public static String decryptAes(String sSrc, String sKey) {
        try {
            if (sKey == null) {
                return null;
            }
            if (sKey.length() < 16) {
                return "";
            }
            String key = sKey.substring(0, 16);
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            try {
                return new String(cipher.doFinal(Base64.getDecoder().decode(sSrc)), "utf-8");
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public String encryptAes(String sSrc, String sKey) {
        if (sKey == null) {
            return "";
        }
        if (sKey.length() < 16) {
            return "";
        }
        String key = sKey.substring(0, 16);
        try {
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
        }
        return "";
    }

//    public  String encode(String publisher, String type, String input,String expireDate,int length,int offset){
//
//    }

    public String encode(String publisher, String type, String input, String expireDate, int length, int offset) {
        String curTime = getCurTime(length);
        int group1 = new Random().nextInt(Integer.valueOf(curTime.substring(0, offset)));
        int group2 = new Random().nextInt(Integer.valueOf(curTime.substring(offset)));
        String pubSalt = publisherSalt(group1, group2);
        String pubEncode = new Encoder(pubSalt, "MD5").encode(publisher);
        String contentSalt = contentSalt(type, pubEncode, group1, group2);
        Encoder encoderMd5 = new Encoder(contentSalt, "MD5");
        String formatG1 = "%0" + offset + "d";
        String formatG2 = "%0" + (length - offset) + "d";
        String enInput = encoderMd5.encode(input);
        return type + enInput + "-" + pubEncode + "-" + String.format(formatG1, group1) + "" + String.format(formatG2, group2) + getExpire(expireDate, enInput);
    }

    private String getCurTime(int length) {
        long currentTimeMillis = System.currentTimeMillis();
        String curTime = currentTimeMillis + "";
        if (curTime.length() > length) {
            int start = curTime.length() - length;
            curTime = curTime.substring(start);
        } else {
            String formater = "%0" + length + "d";
            curTime = String.format(formater, currentTimeMillis);
        }
        return curTime;
    }

    private String publisherSalt(int group1, int group2) {
        int num = (group1 % group2) + (group2 % group1);
        return "p@" + num;
    }

    private String contentSalt(String type, String pubEncode, int group1, int group2) {
        int num = (group1 + group2) + (group1 % group2) + (group2 % group1);
        return "c@" + type + pubEncode + num;
    }

    private String getExpire(String expireDate, String key) {
        String expire = encryptAes(expireDate, key.substring(0, 16));
        if (expire != null && expire.trim().length() > 0) {
            return "-" + expire;
        }
        return "";
    }


//    public static void main(String[] args) {
//        Encoder encoder = new Encoder();
//        String machineCode = "BFEBFBFF000906EA";
////        String machineCode = "BFEBFBFF000906EA";
//
//        String lic = encoder.generateLicense("agena", "0x", machineCode, "2020-12-22", 14, 7);
//        System.out.println("----------generated license-----------");
//        System.out.println(lic);
//
//        System.out.println("----------validateContent license-----------");
//        Boolean check = encoder.validateLicense(machineCode, lic, 14, 7);
//        System.out.println("validateContent result:" + check);
//    }
}
