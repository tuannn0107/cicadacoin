package com.cicada.coin.model;

import com.cicada.coin.constants.Constants;
import com.cicada.coin.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {
    private static Logger log = LoggerFactory.getLogger(Block.class);
    private String previousHash;
    private String currentHash;
    private String merkleRoot;
    private int nonce;
    private long timestamp;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.currentHash = calculateHash();
    }


    /**
     * calculate hash of block.
     *
     * @return
     */
    public String calculateHash()
    {
        return StringUtils.applyHash256(previousHash + Long.toString(timestamp) + Integer.toString(nonce) + this.merkleRoot);
    }


    /**
     * mine block
     *
     * @param difficult
     */
    public void mineBlock(int difficult) {
        log.info("Start mining block with difficulty is " + difficult + " and " + this.transactions.size() + " transaction.");
        this.merkleRoot = StringUtils.getMerkleRoot(transactions);
        String target = new String(new char[difficult]).replace('\0', '0');
        while(!hash().substring(0, difficult).equals(target))
        {
            nonce++;
            this.currentHash = calculateHash();
        }
        log.info("Block mined with hash : " + hash());
    }


    /**
     * add block to chain.
     *
     * @param transaction
     * @return
     */
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if it is valid, if the block is not genesis block, then add to chain.
        if (transaction == null) {
            return false;
        }

        if (!previousHash.equalsIgnoreCase(Constants.GENESIS_BLOCK_HASH)) {
            if (!transaction.verifyTransaction()) {
                log.info("Verify transaction failed!");
                return false;
            }
        }

        transactions.add(transaction);
        log.info("Transaction being added to block.");
        return true;
    }


    public String hash() {
        return this.currentHash;
    }

    public String previousHash() {
        return previousHash;
    }

}
