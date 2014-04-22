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

import android.util.Log;

public class ServerAuthentication {

	Socket client;
	final String password;
	final int RSAKeySize = 512;
	PublicKey pubKey = null;
	PrivateKey priKey = null;
	PublicKey clientPubKey = null;
	byte[] nonceP, nonceG;

	public ServerAuthentication(Socket socket, String password) {
		this.client = socket;
		this.password = password;
	}

	public void authenticateServer() throws Exception {
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
		System.out.println("pubKey: "+Arrays.toString(cPubKeyBytes));


		X509EncodedKeySpec ks = new X509EncodedKeySpec(cPubKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		clientPubKey = kf.generatePublic(ks);
		System.out.println("clientPubKey: " + Arrays.toString(clientPubKey.getEncoded()));

		nonceP = new byte[4];
		client.getInputStream().read(nonceP);
		System.out.println("NonceP: "+Arrays.toString(nonceP));
		
		// part 3 Send serverPubKey and nonceG to client
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(pubKey.getEncoded().length);
		client.getOutputStream().write(bb.array());
		System.out.println("Sent: " + Arrays.toString(bb.array()));
		client.getOutputStream().write(pubKey.getEncoded());
		System.out.println("Sent: " + Arrays.toString(pubKey.getEncoded()));
		client.getOutputStream().write(nonceG);
		System.out.println("Sent: " + Arrays.toString(nonceG));
		client.getOutputStream().flush();


	}
}