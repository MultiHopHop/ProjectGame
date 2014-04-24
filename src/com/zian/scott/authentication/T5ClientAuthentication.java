package com.zian.scott.authentication;

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

public class T5ClientAuthentication implements Authentication {

	private final int RSAKeySize = 1024;
	private PublicKey pubKey = null;
	private PrivateKey priKey = null;
	private PublicKey serverPubKey = null;
	private Socket socket;

	public T5ClientAuthentication(Socket socket) {
		this.socket = socket;
	}

	// Assume the exchange of public keys is safe
	@SuppressLint("TrulyRandom")
	public boolean initialize() throws Exception {
		// part 1 Generate key pair
		KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		RSAKeyGen.initialize(RSAKeySize, random);
		KeyPair pair = RSAKeyGen.generateKeyPair();
		pubKey = pair.getPublic();
		priKey = pair.getPrivate();

		// part 2 encode and send pubKey
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(pubKey.getEncoded().length);
		socket.getOutputStream().write(bb.array());
		System.out.println("Sent: " + Arrays.toString(bb.array()));
		socket.getOutputStream().write(pubKey.getEncoded());
		System.out.println("Sent: " + Arrays.toString(pubKey.getEncoded()));
		socket.getOutputStream().flush();

		// part 3 receive serverPubKey
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
		
		return true;

	}

	public void safeWrite(String msg) throws Exception {
		byte[] plaintext = msg.getBytes("UTF8");
		System.out.println("Start Encryption for plainText");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, serverPubKey);
		byte[] cipherText = cipher.doFinal(plaintext);
		System.out.println("Finish Encryption to cipherText:\n"
				+ new String(cipherText, "UTF8"));

		String encryptedText = Base64
				.encodeToString(cipherText, Base64.DEFAULT);
		System.out.println("EncrytpedText: " + encryptedText);

		// send encryptedText to server
		ObjectOutputStream obOut = new ObjectOutputStream(
				socket.getOutputStream());
		obOut.writeObject(encryptedText);
		obOut.flush();
	}

	public String safeRead() throws Exception {		
		ObjectInputStream obIn = new ObjectInputStream(socket.getInputStream());
		Object msg = obIn.readObject();
		System.out.println("Message received: "+ msg.toString());

		byte[] deco = Base64.decode(msg.toString(), Base64.DEFAULT);
		System.out.println("Base64 Decoded:\n" + new String(deco, "UTF8"));

		System.out.println("Start decryption");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		byte[] newPlainText = cipher.doFinal(deco);

		String output = new String(newPlainText, "UTF8");
		System.out.println("Decrypted Text: "+ output);
		return output;
	}

}