package com.cicada.coin.model;

import com.cicada.coin.utils.StringUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    private String transactionId;
    private PublicKey sender;
    private PublicKey receipient;
    private float value;
    private byte[] signature;

    private static int sequence = 0;

    private ArrayList<TransactionInput> inputs;
    private ArrayList<TransactionOutputs> outputs;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.receipient = to;
        this.value = value;
        this.inputs = inputs;
    }


    /**
     * calculate hash
     *
     * @return
     */
    public String calculateHash() {
        sequence++;
        return StringUtils.applyHash256(StringUtils.getStringFromPublicKey(getSender()) + StringUtils.getStringFromPublicKey(getReceipient()) + Float.toString(value) + sequence);
    }


    /**
     * generate signature
     *
     * @param privateKey
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtils.getStringFromPublicKey(sender) + StringUtils.getStringFromPublicKey(receipient) + Float.toString(value);
        StringUtils.applyECDSASign(privateKey, data);
    }

    /**
     * verify signature
     *
     * @return
     */
    public boolean verifySignature() {
        String data = StringUtils.getStringFromPublicKey(sender) + StringUtils.getStringFromPublicKey(receipient) + Float.toString(value);
        return StringUtils.verifyECDSASign(sender, data, getSignature());
    }


    public Boolean verifyTransaction() {
        if (!verifySignature()) {
            System.out.println("Singature verify failed.");
            return false;
        }

        return true;
    }


    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getReceipient() {
        return receipient;
    }

    public byte[] getSignature() {
        return signature;
    }
}
