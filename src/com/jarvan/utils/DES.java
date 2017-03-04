package com.jarvan.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created with IntelliJ IDEA.WARN 可能有线程安全问题 WARN 如果加密的key被重置，目前只能重启，以后做reload
 * User: srp Date: 14-3-20 Time: 下午2:31
 */
public class DES {
	// DES算法要求有一个可信任的随机数源
	private static SecureRandom sr = new SecureRandom();
	// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
	private static SecretKeyFactory keyFactory;
	// 为我们选择的DES算法生成一个KeyGenerator对象
	private static KeyGenerator kg;

	static {
		try {
			keyFactory = SecretKeyFactory.getInstance("DES");
			kg = KeyGenerator.getInstance("DES");
			kg.init(sr);
		} catch (NoSuchAlgorithmException neverHappens) {
			neverHappens.printStackTrace();
		}
	}

	/**
	 * 生成一个key用于加密解密
	 * 
	 * @return
	 */
	public static byte[] generateKey() {
		// 生成密匙
		SecretKey key = kg.generateKey();
		// 获取密匙数据
		byte rawKeyData[] = key.getEncoded();
		return rawKeyData;
	}

	/**
	 * 加密方法
	 * 
	 * @param rawKeyData
	 * @param str
	 * @return
	 * @throws com.rcloud.error.TokenException
	 */
	public static byte[] encrypt(byte rawKeyData[], String str) throws Exception {
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKey key = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, key, sr);
		// 现在，获取数据并加密
		byte data[] = str.getBytes();
		// 正式执行加密操作
		byte[] encryptedData = cipher.doFinal(data);
		return encryptedData;
	}

	/**
	 * 解密方法
	 * 
	 * @param rawKeyData
	 * @param encryptedData
	 * @throws com.rcloud.error.TokenException
	 */
	public static String decrypt(byte rawKeyData[], byte[] encryptedData) throws Exception {

		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = null;
		dks = new DESKeySpec(rawKeyData);
		SecretKey key = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, key, sr);
		// 正式执行解密操作
		byte decryptedData[] = cipher.doFinal(encryptedData);
		return new String(decryptedData);

	}
}