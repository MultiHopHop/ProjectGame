package com.badlogic.androidgames.mygame;

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

public class ClientAuthentication {
	
	final String password;
	final int RSAKeySize = 512;
	PublicKey pubKey = null;
	PrivateKey priKey = null;
	PublicKey serverPubKey = null;
	byte[] nonceP, nonceG;
	Socket socket;

	public ClientAuthentication(Socket socket, String password) {
		this.socket = socket;
		this.password = password;
	}

	@SuppressLint("TrulyRandom")
	public void authenticate() throws Exception {
		// part 1 Generate key pair
		KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		RSAKeyGen.initialize(RSAKeySize, random);
		KeyPair pair = RSAKeyGen.generateKeyPair();
		pubKey = pair.getPublic();
		priKey = pair.getPrivate();
		
		// create nonceP
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		int seedByteCount = 4;
		nonceP = sr.generateSeed(seedByteCount);

		// part 2 encode pubKey
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
		System.out.println("serverPubKey: " + Arrays.toString(serverPubKey.getEncoded()));

		nonceG = new byte[4];
		socket.getInputStream().read(nonceG);
		System.out.println("NonceG: "+Arrays.toString(nonceG));
		
		// part 4 Encrypt password + nonceG by serverPubKey
		byte[] plaintext = password.getBytes("UTF8");
		System.out.println("Start Encryption for plainText");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, serverPubKey);
		byte[] cipherText = cipher.doFinal(plaintext);

		System.out.println("Finish Encryption to cipherText:\n"
				+ new String(cipherText, "UTF8"));
		
		String encryptedText = Base64.encodeToString(cipherText, Base64.DEFAULT);
		System.out.println("EncrytpedText: \n" + encryptedText);
		// part 3 receive message from server
//		ObjectInputStream obIn = new ObjectInputStream(
//				socket.getInputStream());
//		Object obj = obIn.readObject();

		// part 4
	}
}