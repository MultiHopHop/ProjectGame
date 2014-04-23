package com.zian.scott.authentication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Base64;

public class T4ServerAuthentication implements Authentication {

	private Socket client;
	private final String serverPassword;
	private final int RSAKeySize = 512;
	private PublicKey pubKey = null;
	private PrivateKey priKey = null;
	private PublicKey clientPubKey = null;
	private byte[] nonceP, nonceG;
	private String clientPassword;
	private SecretKey symkey = null;

	public T4ServerAuthentication(Socket socket, String password) {
		this.client = socket;
		this.serverPassword = password;
	}

	public boolean initialize() throws Exception {
		// part 1 Generate key pair
		KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		RSAKeyGen.initialize(RSAKeySize, random);
		KeyPair pair = RSAKeyGen.generateKeyPair();
		pubKey = pair.getPublic();
		priKey = pair.getPrivate();

		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		int seedByteCount = 4;
		nonceG = sr.generateSeed(seedByteCount);

		// part 2 Receive public and nonce from client
		byte[] lenb = new byte[4];
		client.getInputStream().read(lenb, 0, 4);
		ByteBuffer bb1 = ByteBuffer.wrap(lenb);
		int len = bb1.getInt();
		System.out.println("Length of the public key: " + len);

		byte[] cPubKeyBytes = new byte[len];
		client.getInputStream().read(cPubKeyBytes);
		System.out.println("pubKey: " + Arrays.toString(cPubKeyBytes));

		X509EncodedKeySpec ks = new X509EncodedKeySpec(cPubKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		clientPubKey = kf.generatePublic(ks);
		System.out.println("clientPubKey: "
				+ Arrays.toString(clientPubKey.getEncoded()));

		nonceP = new byte[4];
		client.getInputStream().read(nonceP);
		System.out.println("NonceP: " + Arrays.toString(nonceP));

		// part 3 Send serverPubKey and nonceG to client
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(pubKey.getEncoded().length);
		client.getOutputStream().write(bb.array());
		System.out.println("Sent len: " + Arrays.toString(bb.array()));
		client.getOutputStream().write(pubKey.getEncoded());
		System.out.println("Sent pubKey: "
				+ Arrays.toString(pubKey.getEncoded()));
		client.getOutputStream().write(nonceG);
		System.out.println("Sent nonceG: " + Arrays.toString(nonceG));
		client.getOutputStream().flush();

		// Part 4 Receive digest from client
		ObjectInputStream obIn = new ObjectInputStream(client.getInputStream());
		String encryptedDigest = obIn.readObject().toString();
		byte[] decoDigest = Base64.decode(encryptedDigest,Base64.DEFAULT);
		System.out.println("Received Digest from Client");
		System.out.println("decoDigest:\n" + Arrays.toString(decoDigest));

		// Part 5.1: encrypt passwordG + nonceP by clientPubKey
		String modifiedText = serverPassword + "&" + Arrays.toString(nonceP);
		System.out.println("ModifiedText: " + modifiedText);
		byte[] plaintext = modifiedText.getBytes("UTF8");
		System.out.println("Start Encryption for plainText");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, clientPubKey);
		byte[] cipherText = cipher.doFinal(plaintext);

		System.out.println("Finish Encryption to cipherText:\n"
				+ new String(cipherText, "UTF8"));

		String encryptedText = Base64
				.encodeToString(cipherText, Base64.DEFAULT);
		System.out.println("EncrytpedText: \n" + encryptedText);

		// Part 5.2 Send the first half of encrypted text to client
		int pMid = encryptedText.length() / 2;
		String gFirstHalf = encryptedText.substring(0, pMid);
		System.out.println("gFirstHalf: \n" + gFirstHalf);
		ObjectOutputStream obOut = new ObjectOutputStream(
				client.getOutputStream());
		obOut.writeObject(gFirstHalf);
		obOut.flush();

		// Part 6 Receive cipherText from client
		String encryptedCipherText = obIn.readObject().toString();
		System.out.println("Received encryptedCipherText from client");

		// Part 7.1A Verify that the Hash of the cipherText matches the Digest
		byte[] decoCipherText = Base64.decode(encryptedCipherText, Base64.DEFAULT);
		System.out.println("Base64 Decoded:\n" + new String(decoCipherText, "UTF8"));
		MessageDigest digester = MessageDigest.getInstance("MD5");
		digester.update(decoCipherText);
		byte[] digest = digester.digest();
		System.out.println("Digest:\n" + Arrays.toString(digest));
		if(!Arrays.equals(digest, decoDigest)){
			System.out.println("digests do not match");
			return false;
		}

		// Part 7.1B Decrypt cipherText 
		System.out.println("Start decryption");
		Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher2.init(Cipher.DECRYPT_MODE, priKey);
		byte[] newPlainText = cipher2.doFinal(decoCipherText);
		System.out.println("Finish decryption:\n"
				+ new String(newPlainText, "UTF8"));

		// Part 7.2 Check nonceG
		String combinedString = new String(newPlainText, "UTF8");
		String[] passwordAndNonce = combinedString.split("&");
		clientPassword = passwordAndNonce[0];
		System.out.println("clientPassword:" + clientPassword);
		String nonceG2 = passwordAndNonce[1];
		System.out.println("Original nonceG: " + Arrays.toString(nonceG));
		if (!nonceG2.equals(Arrays.toString(nonceG))) {
			System.out.println("nonceG is modified");
			return false;
		}

		// Part 8 Send the second half of encrypted text to client
		String gSecondHalf = encryptedText.substring(pMid);
		System.out.println("gSecondHalf: " + gSecondHalf);
		obOut.writeObject(gSecondHalf);
		obOut.flush();
		
		// Part 9.1 Generate Symmetric Key
			// get a DES secrete key
        System.out.println("\nStart generating DES key");
        	// Generate a key that's used for DES encryption algorithm
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecureRandom randomDES = new SecureRandom();
        keyGen.init(56, randomDES);
        symkey = keyGen.generateKey();
        System.out.println("Finish generating DES key");
        
        //Part 9.2 Encrypt Symmetric Key
		System.out.println("Start Encryption for DES key");
		Cipher cipherDES = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, clientPubKey);
		byte[] cipherKey = cipher.doFinal(symkey.getEncoded());
		String encryptedKey = Base64
				.encodeToString(cipherKey, Base64.DEFAULT);
		System.out.println("Finish Encryption to encryptedKey");
		
		//Part 9.3 Send Encrypt Symmetric Key
		obOut.writeObject(encryptedKey);
		obOut.flush();
		System.out.println("Sent encryptedKey");
        

		return true;
	}
	
	public String getClientPassword() {
		return clientPassword;
	}

	public void safeWrite(String msg) throws Exception {
		/// get a DES cipher and print the provider
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        System.out.println("\n" + cipher.getProvider().getInfo());

        // encrypt using the key and the plaintext
        System.out.println("\nStart encrypting text");
        cipher.init(Cipher.ENCRYPT_MODE, symkey);
        byte[] cipherText = cipher.doFinal(msg.getBytes("UTF8"));
        System.out.println("Finish encrypting text: ");
        System.out.println(new String(cipherText, "UTF8"));
        System.out.println(cipherText.length);

        /***** BASE64 Encode ****/
        String encryptedText = Base64.encodeToString(cipherText,Base64.DEFAULT);
        System.out.println("Base64 Encoded:\n" + encryptedText);
        ObjectOutputStream obOut = new ObjectOutputStream(
        		client.getOutputStream());
		obOut.writeObject(encryptedText);
		obOut.flush();
		System.out.println("encryptedText sent.");
		
	}

	public String safeRead() throws Exception {
		ObjectInputStream obIn = new ObjectInputStream(client.getInputStream());
		Object msg = obIn.readObject();
		System.out.println("Message received: "+ msg.toString());

		byte[] newPlainText = null;
		byte[] deco = Base64.decode(msg.toString(),Base64.DEFAULT);
		System.out.println("Start decrypting msg");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, symkey);
		newPlainText = cipher.doFinal(deco);
		
		String output = new String(newPlainText, "UTF8");
		System.out.println("Decrypted Text: "+ output);
		return output;
	}
}