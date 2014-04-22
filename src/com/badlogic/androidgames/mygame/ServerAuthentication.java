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
	final int RSAKeySize = 512;
	PublicKey pubKey = null;
	PrivateKey priKey = null;
	PublicKey clientPubKey = null;
	byte[] nonceP;
	
	public ServerAuthentication(Socket socket) {
		this.client = socket;
	}
	
	public void authenticateServer() throws Exception {
		// part 1 Generate key pair
		KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		RSAKeyGen.initialize(RSAKeySize, random);
		KeyPair pair = RSAKeyGen.generateKeyPair();
		pubKey = pair.getPublic();
		priKey = pair.getPrivate();
		
		// part 2 Receive public and nonce from client
		byte[] lenb = new byte[4];
		client.getInputStream().read(lenb, 0, 4);
		ByteBuffer bb = ByteBuffer.wrap(lenb);
		int len = bb.getInt();
		System.out.println("Length of the public key: " + len);

		byte[] cPubKeyBytes = new byte[len];
		client.getInputStream().read(cPubKeyBytes);
//		System.out.println("Public Key: \n"
//				+ DatatypeConverter.printHexBinary(cPubKeyBytes));

		X509EncodedKeySpec ks = new X509EncodedKeySpec(cPubKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		clientPubKey = kf.generatePublic(ks);
//		System.out.println("Encoded Public Key: \n"
//				+ DatatypeConverter.printHexBinary(clientPubKey
//						.getEncoded()));
		
		byte[] lenNonce = new byte[4];
		client.getInputStream().read(lenNonce, 0, 4);
		ByteBuffer bb2 = ByteBuffer.wrap(lenNonce);
		int len2 = bb2.getInt();
		byte[] nonceP = new byte[len2];
		client.getInputStream().read(nonceP);
		Log.d("NonceP", Arrays.toString(nonceP));
		System.out.println("NonceP: "+Arrays.toString(nonceP));
		
	}
}
