package com.cicada.coin;

import com.cicada.coin.model.Block;
import com.cicada.coin.model.TransactionOutputs;
import com.google.gson.GsonBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootApplication
public class CoinApplication {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutputs> UTXOs = new HashMap<>();

    public static void main(String[] args) {
        //SpringApplication.run(CoinApplication.class, args);
        Block genesisBlock = new Block("This is a genesis block.", "0");
        genesisBlock.mineBlock(5);
        blockchain.add(genesisBlock);
        System.out.println("genesis block: " + genesisBlock.hash());

        Block firstBlock = new Block("This is a first block.", genesisBlock.hash());
        firstBlock.mineBlock(5);
        blockchain.add(firstBlock);
        System.out.println("first block:" + firstBlock.hash());

        Block secondBlock = new Block("This is a first block.", firstBlock.hash());
        secondBlock.mineBlock(5);
        blockchain.add(secondBlock);
        System.out.println("second block: " + secondBlock.hash());

        System.out.println("-------------------------------");

        if (!isValidChain()) {
            System.out.println("The chain is invalid.");
            return;
        }

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }


    /**
     * check the chin is valid
     * @return
     */
    public static Boolean isValidChain() {
        Block currenBlock;
        Block previousBlock;

        for(int i = 1; i < blockchain.size(); i++) {
            currenBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!currenBlock.hash().equals(currenBlock.calculateHash())) {
                System.out.println("Hash for current block invalid.");
                return false;
            }

            if(!currenBlock.previousHash().equals(previousBlock.hash())) {
                System.out.println("Previous hash for current block invalid.");
                return false;
            }
        }

        return true;
    }


}
