package com.cicada.coin.model;

import com.cicada.coin.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class Block implements Serializable {
    private String previousHash;
    private String currentHash;
    private String data;
    private int nonce;
    private long timestamp;

    public Block(String data, String previousHash) {
        this.data = data;
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
        return StringUtils.applyHash256(previousHash + Long.toString(timestamp) + Integer.toString(nonce) + data);
    }


    public void mineBlock(int difficult) {
        String target = new String(new char[difficult]).replace("\0", "0");
        while(!hash().substring(0, difficult).equals(target))
        {
            nonce++;
            this.currentHash = calculateHash();
        }
        System.out.println("Block mined with hash : " + hash());
    }


    public String hash() {
        return this.currentHash;
    }

    public String previousHash() {
        return previousHash;
    }

}
