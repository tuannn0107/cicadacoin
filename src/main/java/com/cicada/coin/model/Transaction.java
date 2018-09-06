package com.cicada.coin.model;

import com.cicada.coin.CicadaChain;
import com.cicada.coin.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    private static Logger log = LoggerFactory.getLogger(Transaction.class);
    private String transactionId;
    private PublicKey sender;
    private PublicKey receipient;
    private float value;
    private byte[] signature;

    private static int sequence = 0;

    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

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
        this.signature = StringUtils.applyECDSASign(privateKey, data);
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


    public boolean verifyTransaction() {
        if (!verifySignature()) {
            log.info("Signature verify failed.");
            return false;
        }

        for(TransactionInput it: inputs) {
            it.setUTXO(CicadaChain.UTXOs.get(it.getTransactionOutputId()));
        }

        if (getInputValues() < CicadaChain.minimumTransaction) {
            log.info("Input value is too small (" + getInputValues() + ").");
            return false;
        }

        //Generate transaction output.
        float leftOver = getInputValues() - value;
        this.transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.receipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, this.transactionId));

        //add output to unspent list
        for (TransactionOutput txOutput: outputs) {
            CicadaChain.UTXOs.put(txOutput.getTransactionId(), txOutput);
        }

        //Remove transaction input from chain as spent
        for (TransactionInput txInput: inputs) {
            if(txInput.getUTXO() != null)
            {
                CicadaChain.UTXOs.remove(txInput.getUTXO().getTransactionId());
            }
        }

        return true;
    }


    public float getInputValues() {
        float total = 0;
        for (TransactionInput txInput: inputs) {
            if (txInput.getUTXO() != null) {
                total += txInput.getUTXO().getValue();
            }
        }

        return total;
    }


    public float getOutputValue() {
        float total = 0;
        for (TransactionOutput txOutput : outputs) {
            total += txOutput.getValue();
        }
        return total;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public void setSender(PublicKey sender) {
        this.sender = sender;
    }

    public PublicKey getReceipient() {
        return receipient;
    }

    public void setReceipient(PublicKey receipient) {
        this.receipient = receipient;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public static int getSequence() {
        return sequence;
    }

    public static void setSequence(int sequence) {
        Transaction.sequence = sequence;
    }

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<TransactionOutput> outputs) {
        this.outputs = outputs;
    }
}
