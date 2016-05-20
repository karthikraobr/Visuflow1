package de.visuflow.analyzeMe.ex1;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class TargetClass1 {
	public static void main(String[] args){
		try {
			wrong1();
			wrong2();
			correct1();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	private static void wrong1() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher.getInstance("DES");
	}
	private static void wrong2() throws NoSuchAlgorithmException, NoSuchPaddingException {
		String des ="DES";
		Cipher.getInstance(des);
	}
	private static void correct1() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher.getInstance("AES");
	}
	private static void unreachable() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher.getInstance("DES");
	}
}
