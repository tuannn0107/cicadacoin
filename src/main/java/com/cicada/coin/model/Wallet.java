package com.cicada.coin.model;

import com.cicada.coin.CicadaChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet implements Serializable {
    private static Logger log = LoggerFactory.getLogger(Wallet.class);
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();





    public Wallet() {
        generateKeypair();
    }


    /**
     * generate key pair
     */
    public void generateKeypair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
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


    /**
     * get balance
     *
     * @return balance
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: CicadaChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(this.publicKey)) {
                getUTXOs().put(UTXO.getTransactionId(), UTXO);
                total += UTXO.getValue();
            }
        }

        return total;
    }


    public Transaction sendFunds(PublicKey receipient, float value) {
        if (getBalance() < value) {
            log.info("Insufficient balance. The transaction was discard.");
            return null;
        }

        //Create list transaction input
        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> txOutput: getUTXOs().entrySet()) {
            total += txOutput.getValue().getValue();
            inputs.add(new TransactionInput(txOutput.getValue().getTransactionId()));
            if (total > value) {
                break;
            }
        }

        Transaction transaction = new Transaction(publicKey, receipient, value, inputs);
        transaction.generateSignature(privateKey);

        for (TransactionInput txInput: inputs) {
            getUTXOs().remove(txInput.getTransactionOutputId());
        }

        return transaction;
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

    public HashMap<String, TransactionOutput> getUTXOs() {
        return UTXOs;
    }

    public void setUTXOs(HashMap<String, TransactionOutput> UTXOs) {
        this.UTXOs = UTXOs;
    }
}
