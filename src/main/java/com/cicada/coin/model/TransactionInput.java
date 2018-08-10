package com.cicada.coin.model;

import java.io.Serializable;

public class TransactionInput implements Serializable {
    private String transactionOutputId;
    private TransactionOutputs UTXO;


    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

}
