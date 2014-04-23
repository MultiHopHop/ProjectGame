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

import android.annotation.SuppressLint;
import android.util.Base64;

public class T2ClientAuthentication implements Authentication {

	private final String clientPassword;
	private final int RSAKeySize = 512;
	private PublicKey pubKey = null;
	private PrivateKey priKey = null;
	private PublicKey serverPubKey = null;
	private byte[] nonceP, nonceG;
	private Socket socket;
	private String serverPassword;

	public T2ClientAuthentication(Socket socket, String password) {
		this.socket = socket;
		this.clientPassword = password;
	}

	@SuppressLint("TrulyRandom")
	public boolean initialize() throws Exception {
		// part 1.1 Generate key pair
		KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		RSAKeyGen.initialize(RSAKeySize, random);
		KeyPair pair = RSAKeyGen.generateKeyPair();
		pubKey = pair.getPublic();
		priKey = pair.getPrivate();

		// Part 1.2 Create nonceP
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		int seedByteCount = 4;
		nonceP = sr.generateSeed(seedByteCount);

		// part 2 send pubKey
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(pubKey.getEncoded().length);
		socket.getOutputStream().write(bb.array());
		System.out.println("Sent: " + Arrays.toString(bb.array()));
		socket.getOutputStream().write(pubKey.getEncoded());
		System.out.println("Sent: " + Arrays.toString(pubKey.getEncoded()));
		socket.getOutputStream().write(nonceP);
		System.out.println("Sent: " + Arrays.toString(nonceP));
		socket.getOutputStream().flush();

		// part 3 receive serverPubKey and nonceG
		byte[] lenb = new byte[4];
		socket.getInputStream().read(lenb, 0, 4);
		ByteBuffer bb1 = ByteBuffer.wrap(lenb);
		int len = bb1.getInt();
		System.out.println("Length of the public key: " + len);

		byte[] sPubKeyBytes = new byte[len];
		socket.getInputStream().read(sPubKeyBytes);

		X509EncodedKeySpec ks = new X509EncodedKeySpec(sPubKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		serverPubKey = kf.generatePublic(ks);
		System.out.println("serverPubKey: "
				+ Arrays.toString(serverPubKey.getEncoded()));

		nonceG = new byte[4];
		socket.getInputStream().read(nonceG);
		System.out.println("NonceG: " + Arrays.toString(nonceG));

		// Part 4.1 Encrypt password + nonceG by serverPubKey
		String modifiedText = clientPassword + "&" + Arrays.toString(nonceG);
		System.out.println("ModifiedText: " + modifiedText);
		byte[] plaintext = modifiedText.getBytes("UTF8");
		System.out.println("Start Encryption for plainText");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, serverPubKey);
		byte[] cipherText = cipher.doFinal(plaintext);

		System.out.println("Finish Encryption to cipherText:\n"
				+ new String(cipherText, "UTF8"));

		String encryptedText = Base64
				.encodeToString(cipherText, Base64.DEFAULT);
		System.out.println("EncrytpedText: " + encryptedText);

		// Part 4.2 Send the first half of encrypted text to server
		int pMid = encryptedText.length() / 2;
		String pFirstHalf = encryptedText.substring(0, pMid);
		System.out.println("pFirstHalf: " + pFirstHalf);
		ObjectOutputStream obOut = new ObjectOutputStream(
				socket.getOutputStream());
		obOut.writeObject(pFirstHalf);
		obOut.flush();

		// Part 5 Receive G's password + nonceP (first half)
		ObjectInputStream obIn = new ObjectInputStream(socket.getInputStream());
		Object gFirstHalf = obIn.readObject();
		System.out.println("gFirstHalf received from server: " + gFirstHalf);

		// Part 6 Send the second half of encrypted text to server
		String pSecondHalf = encryptedText.substring(pMid);
		System.out.println("pSecondHalf: " + pSecondHalf);
		obOut.writeObject(pSecondHalf);
		obOut.flush();

		// Part 7 Receive G's password + nonceG (second half)
		Object gSecondHalf = obIn.readObject();
		System.out.println("gSecondHalf received from server:\n" + gSecondHalf);

		// Part 8.1 Concatenates the two halves of the cipher text received from G
		String gCipherText = gFirstHalf.toString() + gSecondHalf.toString();

		byte[] deco = Base64.decode(gCipherText, Base64.DEFAULT);
		System.out.println("Base64 Decoded:\n" + new String(deco, "UTF8"));

		System.out.println("Start decryption");
		Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher2.init(Cipher.DECRYPT_MODE, priKey);
		byte[] newPlainText = cipher2.doFinal(deco);
		System.out.println("Finish decryption:\n"
				+ new String(newPlainText, "UTF8"));

		// Part 8.2 Check nonceP
		String combinedString = new String(newPlainText, "UTF8");
		String[] passwordAndNonce = combinedString.split("&");
		serverPassword = passwordAndNonce[0];
		System.out.println("serverPassword:" + serverPassword);
		String nonceP2 = passwordAndNonce[1];
		System.out.println("Original nonceP: " + Arrays.toString(nonceP));
		if (!nonceP2.equals(Arrays.toString(nonceP))) {
			System.out.println("nonceG is modified");
			return false;
		}
		
		return true;
	}
	
	public String getServerPassword() {
		return serverPassword;
	}

	public void send(String msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String receive() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	//// 
}