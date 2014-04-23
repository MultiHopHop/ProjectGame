package com.badlogic.androidgame.authentication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

import android.util.Base64;

public class T2ServerAuthentication implements Authentication {

	private Socket client;
	private final String serverPassword;
	private final int RSAKeySize = 512;
	private PublicKey pubKey = null;
	private PrivateKey priKey = null;
	private PublicKey clientPubKey = null;
	private byte[] nonceP, nonceG;
	private String clientPassword;

	public T2ServerAuthentication(Socket socket, String password) {
		this.client = socket;
		this.serverPassword = password;
	}

	public boolean initialize() throws Exception {
		// Part 1 Generate key pair
		KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		RSAKeyGen.initialize(RSAKeySize, random);
		KeyPair pair = RSAKeyGen.generateKeyPair();
		pubKey = pair.getPublic();
		priKey = pair.getPrivate();

		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		int seedByteCount = 4;
		nonceG = sr.generateSeed(seedByteCount);

		// Part 2 Receive public and nonceP from client
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

		// Part 3 Send serverPubKey and nonceG to client
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

		// Part 4 Receive P's password + nonceG (first half)
		ObjectInputStream obIn = new ObjectInputStream(client.getInputStream());
		Object pFirstHalf = obIn.readObject();
		System.out.println("pFirstHalf received from client:\n" + pFirstHalf);

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

		// Part 6 Receive P's password + nonceG (second half)
		Object pSecondHalf = obIn.readObject();
		System.out.println("pSecondHalf received from client:\n" + pSecondHalf);

		// Part 7.1 Concatenates the two halves of the cipher text received from
		// P
		String pCipherText = pFirstHalf.toString() + pSecondHalf.toString();

		byte[] deco = Base64.decode(pCipherText, Base64.DEFAULT);
		System.out.println("Base64 Decoded:\n" + new String(deco, "UTF8"));

		System.out.println("Start decryption");
		Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher2.init(Cipher.DECRYPT_MODE, priKey);
		byte[] newPlainText = cipher2.doFinal(deco);
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

		return true;
	}
	
	public String getClientPassword() {
		return clientPassword;
	}

	public void safeWrite(String msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String safeRead() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}