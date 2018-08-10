package com.cicada.coin.utils;

import java.security.*;
import java.util.Base64;

public class StringUtils {


    /**
     * create string hash256
     * @param input
     * @return
     */
    public static String applyHash256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(input.getBytes("UTF-8"));
            StringBuffer hexBuffer = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexBuffer.append("0");
                }
                hexBuffer.append(hex);
            }

            return hexBuffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * apply ECDSA signature
     *
     * @param privateKey
     * @param input
     * @return
     */
    public static byte[] applyECDSASign(PrivateKey privateKey, String input) {
        Signature dsaSignature;
        byte[] output = new byte[0];

        try {
            dsaSignature = Signature.getInstance("ECDSA", "EC");
            dsaSignature.initSign(privateKey);
            dsaSignature.update(input.getBytes("UTF-8"));
            output = dsaSignature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }


    /**
     * verify EC DSA signature
     *
     * @param publicKey
     * @param data
     * @param signature
     * @return
     */
    public static Boolean verifyECDSASign(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "EC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes("UTF-8"));
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Could not verify signature");
        }
    }


    /**
     * get publickey string
     *
     * @param publicKey
     * @return
     */
    public static String getStringFromPublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}






