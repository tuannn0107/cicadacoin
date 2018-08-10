package com.cicada.coin.model;

import java.io.Serializable;
import java.security.PublicKey;

public class TransactionOutputs implements Serializable {
    private String transactionId;
    private PublicKey receipient;
    private float value;
    private String parentTransactionId;


    public TransactionOutputs(PublicKey receipient, float value, String parentTransactionId) {
        this.receipient = receipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
    }


    public Boolean isMine(PublicKey publicKey) {
        return (getReceipient().equals(publicKey));
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getReceipient() {
        return receipient;
    }

    public float getValue() {
        return value;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }
}
