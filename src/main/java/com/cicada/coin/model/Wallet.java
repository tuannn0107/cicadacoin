package com.cicada.coin.model;

import java.io.Serializable;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet implements Serializable {
    private PrivateKey privateKey;
    private PublicKey publicKey;


    public Wallet() {
        generateKeypair();
    }


    public void generateKeypair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA","EC");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyPairGenerator.initialize(ecGenParameterSpec, random);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            setPublicKey(keyPair.getPublic());
            setPrivateKey(keyPair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }


    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
