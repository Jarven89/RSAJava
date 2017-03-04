package com.jarvan.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Test {


	// public static final Config config =
	// ConfigFactory.load("authorization.conf").withFallback(ConfigFactory.load());
	private static final String DESKEY = "1R2O3N4G5C6L7O8U9D0S0E2C3U4R5E6K7E8Y9";

	public static void readConf() {
		long userCountlimit = 0;
		int appLimit;
		long validityTime;
		boolean isCheck = false;
		try {
			ArrayList<String> config = readFileByLines("src/authorization.conf");
			String license = config.get(3);
			String sign = config.get(2);
			String pb = config.get(0);
			String pv = config.get(1);
			// 验证签名
			boolean status = RSAUtils.verify(RSAUtils.decode(license), pb, sign);
			if (!status) {
				System.out.println(status);
			}
			byte[] decodedData = RSAUtils.decryptByPrivateKey(RSAUtils.decode(license), pv);
			// 做DES对称解密
			String target = DES.decrypt(RSAUtils.encode(DESKEY.getBytes()).getBytes(), decodedData);
			System.out.println(target);

			JsonObject rootObj = new JsonParser().parse(target).getAsJsonObject();
			appLimit = rootObj.getAsJsonPrimitive("appl").getAsInt();
			// 赋值
			userCountlimit = rootObj.getAsJsonPrimitive("ucl").getAsLong();
			validityTime = rootObj.getAsJsonPrimitive("vli").getAsLong();
			isCheck = rootObj.getAsJsonPrimitive("ic").getAsBoolean();
			//
			// logger.info("appLimit={}\tuserCountlimit={}\tvalidityTime={}\tisCheck={}",
			// appLimit, userCountlimit,
			// validityTime, isCheck);
			System.out.println(appLimit + "---" + userCountlimit + "---" + validityTime + "---" + isCheck);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);

		}

	}

	private static final String license = "{\"appl\":2,\"ucl\":100,\"cid\":\"dh\",\"vs\":\"V1.4\",\"vli\":1488538231000,\"ic\":true}";

	public static void createConf() throws Exception {

		try {
			// 判断是否有自己的公钥私钥文件，如果没有则生成，如果有直接用
			String publicKey;
			String privateKey;
			File rongCer = new File("src/RongCer.cer");
			if (rongCer.exists()) {
				ArrayList<String> cers = readFileByLines("src/RongCer.cer");
				publicKey = cers.get(0);
				privateKey = cers.get(0);

			} else {
				Map<String, Object> keyMap = RSAUtils.genKeyPair();
				publicKey = RSAUtils.getPublicKey(keyMap);
				privateKey = RSAUtils.getPrivateKey(keyMap);
				// // 写入自己公钥私钥
				PrintWriter rongkeys = new PrintWriter(new FileOutputStream("src/RongCer.cer"), true);
				rongkeys.print(publicKey);
				rongkeys.print("\n");
				rongkeys.print(privateKey);
				rongkeys.close();
			}

			// 客户公约私钥
			Map<String, Object> keyMap_custom = RSAUtils.genKeyPair();
			String publicKey_custom = RSAUtils.getPublicKey(keyMap_custom);
			String privateKey_custom = RSAUtils.getPrivateKey(keyMap_custom);

			// 加密 license 对licenes 做对称加密
			byte[] des_license = DES.encrypt(RSAUtils.encode(DESKEY.getBytes()).getBytes(), license);

			byte[] encodedData = RSAUtils.encryptByPublicKey(des_license, publicKey_custom);
			// 签名
			String sign = RSAUtils.sign(encodedData, privateKey);

			PrintWriter pw2 = new PrintWriter(new FileOutputStream("src/authorization.conf"), true);
			// 写入自己公钥 和 客户私钥
			// pw2.print("pb=\"");
			pw2.print(publicKey);
			// pw2.print("\"");
			pw2.print("\n");
			// pw2.print("pv=\"");
			pw2.print(privateKey_custom);
			// pw2.print("\"");
			// 写入签名
			pw2.print("\n");
			// pw2.print("signature=\"");
			pw2.print(StringEscapeUtils.escapeJava(sign));
			// pw2.print("\"");
			// 写入license
			pw2.print("\n");
			// pw2.print("license=\"");
			pw2.print(StringEscapeUtils.escapeJava(RSAUtils.encode(encodedData)));
			// pw2.print("\"");
			// pw1.close();
			pw2.close();
			PrintWriter customrw = new PrintWriter(new FileOutputStream("src/CustomCer.cer"), true);
			// // 写入客户公钥私钥
			customrw.print(publicKey_custom);
			customrw.print("\n");
			customrw.print(privateKey_custom);
			customrw.close();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 */
	public static ArrayList<String> readFileByLines(String fileName) {
		File file = new File(fileName);
		ArrayList list = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				list.add(tempString);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return list;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Test.readFileByLines("");
		Test.createConf();
		Test.readConf();
		// System.out.println(Base64Utils.encode(DESKEY.getBytes()));
	}

}
