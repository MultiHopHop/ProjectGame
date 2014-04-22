package com.badlogic.androidgames.mygame;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class ClientAuthentication {
	final int RSAKeySize = 512;
	PublicKey pubKey = null;
	PrivateKey priKey = null;
	byte[] nonceP;
	Socket socket;
	
	public ClientAuthentication(Socket socket) {
		this.socket = socket;
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
		nonceP = sr.generateSeed(seedByteCount);
		
		// part 2 encode pubKey
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putInt(pubKey.getEncoded().length);
		socket.getOutputStream().write(bb.array());
		socket.getOutputStream().write(pubKey.getEncoded());
		socket.getOutputStream().write(nonceP);
		socket.getOutputStream().flush();
		
		// part 3 receive message from server
//		ObjectInputStream obIn = new ObjectInputStream(
//				socket.getInputStream());
//		Object obj = obIn.readObject();
		
		// part 4
	}
}
