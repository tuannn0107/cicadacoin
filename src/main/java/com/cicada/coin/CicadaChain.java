package com.cicada.coin;

import com.cicada.coin.constants.Constants;
import com.cicada.coin.model.Block;
import com.cicada.coin.model.Transaction;
import com.cicada.coin.model.TransactionOutput;
import com.cicada.coin.model.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootApplication
public class CicadaChain {
    private static Logger log = LoggerFactory.getLogger(CicadaChain.class);

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static float minimumTransaction = 0.1f;

    public static void main(String[] args) {
        //SpringApplication.run(CoinApplication.class, args);
        /*Block genesisBlock = new Block("This is a genesis block.", "0");
        genesisBlock.mineBlock(Constants.DIFFICULT);
        blockchain.add(genesisBlock);
        System.out.println("genesis block: " + genesisBlock.hash());

        Block firstBlock = new Block("This is a first block.", genesisBlock.hash());
        firstBlock.mineBlock(Constants.DIFFICULT);
        blockchain.add(firstBlock);
        System.out.println("first block:" + firstBlock.hash());

        Block secondBlock = new Block("This is a first block.", firstBlock.hash());
        secondBlock.mineBlock(Constants.DIFFICULT);
        blockchain.add(secondBlock);
        System.out.println("second block: " + secondBlock.hash());

        System.out.println("-------------------------------");

        if (!isValidChain()) {
            System.out.println("The chain is invalid.");
            return;
        }

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);*/

        Security.addProvider(new BouncyCastleProvider());

        //create wallet
        Wallet bobWallet = new Wallet();
        Wallet aliceWallet = new Wallet();
        Wallet coinbase = new Wallet();

        log.info("Init genesis block.");
        Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), bobWallet.getPublicKey(), 100, null);
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        genesisTransaction.setTransactionId(Constants.GENESIS_BLOCK_TXID);
        genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getReceipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
        UTXOs.put(genesisTransaction.getOutputs().get(0).getTransactionId(), genesisTransaction.getOutputs().get(0));

        log.info("Mining genesis block.");
        Block genesisBlock = new Block(Constants.GENESIS_BLOCK_HASH);
        genesisBlock.addTransaction(genesisTransaction);
        addBlock(genesisBlock);
        log.info("Coinbase's wallet :" + coinbase.getBalance());
        log.info("Bob's wallet :" + bobWallet.getBalance());
        log.info("Alice's wallet :" + aliceWallet.getBalance());

        log.info("Start send funds 40 coins from Bob to Alice");
        Block block1 = new Block(genesisBlock.hash());
        block1.addTransaction(bobWallet.sendFunds(aliceWallet.getPublicKey(), 40f));
        addBlock(block1);
        log.info("Bob's wallet :" + bobWallet.getBalance());
        log.info("Alice's wallet :" + aliceWallet.getBalance());

        log.info("Try to send funds out of balance.");
        Block block2 = new Block(block1.hash());
        block2.addTransaction(bobWallet.sendFunds(aliceWallet.getPublicKey(),110f));
        addBlock(block2);
        log.info("Bob's wallet :" + bobWallet.getBalance());
        log.info("Alice's wallet :" + aliceWallet.getBalance());

        Block block3 = new Block(block2.hash());
        block3.addTransaction(aliceWallet.sendFunds(bobWallet.getPublicKey(), 20f));
        addBlock(block3);
        log.info("Bob's wallet :" + bobWallet.getBalance());
        log.info("Alice's wallet :" + aliceWallet.getBalance());
    }


    /**
     * check the chain is valid
     *
     * @return
     */
    public static Boolean isValidChain() {
        Block currenBlock;
        Block previousBlock;

        for(int i = 1; i < blockchain.size(); i++) {
            currenBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!currenBlock.hash().equals(currenBlock.calculateHash())) {
                log.info("Hash for current block invalid.");
                return false;
            }

            if(!currenBlock.previousHash().equals(previousBlock.hash())) {
                log.info("Previous hash for current block invalid.");
                return false;
            }
        }

        return true;
    }


    /**
     * add block to chain
     *
     * @param block
     */
    public static void addBlock(Block block) {
        block.mineBlock(Constants.DIFFICULT);
        blockchain.add(block);
    }
}
