package com.jarvan.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jarvan.model.Authorization;

public class Test {

	// public static final Config config =
	// ConfigFactory.load("authorization.conf").withFallback(ConfigFactory.load());
	private static final String DESKEY = "1R2O3N4G5C6L7O8U9D0S0E2C3U4R5E6K7E8Y9";
	private static Scanner sc;

	public static void readConf(String patch) {
		long userCountlimit = 0;
		int appLimit;
		long validityTime;
		boolean isCheck = false;
		try {
			ArrayList<String> config = readFileByLines(patch + "/authorization.conf");
			String license = config.get(3);
			String sign = config.get(2);
			String pb = config.get(0);
			String pv = config.get(1);
			// 验证签名
			boolean status = RSAUtils.verify(RSAUtils.decode(StringEscapeUtils.unescapeJava(license)),
					StringEscapeUtils.unescapeJava(pb), StringEscapeUtils.unescapeJava(sign));
			if (!status) {
				System.out.println(status);
			}
			byte[] decodedData = RSAUtils.decryptByPrivateKey(RSAUtils.decode(StringEscapeUtils.unescapeJava(license)),
					StringEscapeUtils.unescapeJava(pv));
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

	public static void createConf(String files, String license) {

		try {
			// 判断是否有自己的公钥私钥文件，如果没有则生成，如果有直接用
			String publicKey;
			String privateKey;
			File rootFile = new File(files);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File rongCer = new File(files + "/RongCer.cer");
			if (rongCer.exists()) {
				ArrayList<String> cers = readFileByLines(files + "/RongCer.cer");
				publicKey = cers.get(0);
				privateKey = cers.get(0);

			} else {
				Map<String, Object> keyMap = RSAUtils.genKeyPair();
				publicKey = RSAUtils.getPublicKey(keyMap);
				privateKey = RSAUtils.getPrivateKey(keyMap);
				// // 写入自己公钥私钥
				PrintWriter rongkeys = new PrintWriter(new FileOutputStream(files + "/RongCer.cer"), true);
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

			PrintWriter pw2 = new PrintWriter(new FileOutputStream(files + "/authorization.conf"), true);
			// 写入自己公钥 和 客户私钥
			// pw2.print("pb=\"");
			pw2.print(StringEscapeUtils.escapeJava(publicKey));
			// pw2.print("\"");
			pw2.print("\n");
			// pw2.print("pv=\"");
			pw2.print(StringEscapeUtils.escapeJava(privateKey_custom));
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
			PrintWriter customrw = new PrintWriter(new FileOutputStream(files + "/CustomCer.cer"), true);
			// // 写入客户公钥私钥
			customrw.print(publicKey_custom);
			customrw.print("\n");
			customrw.print(privateKey_custom);
			customrw.close();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
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
				System.out.println(tempString);
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

	/**
	 * 时间戳转换成日期格式字符串
	 * 
	 * @param millisecond
	 *            精确到秒的字符串
	 * @param formatStr
	 * @return
	 */
	public static String timeStamp2Date(String millisecond, String format) {
		if (millisecond == null || millisecond.isEmpty() || millisecond.equals("null")) {
			return "";
		}
		if (format == null || format.isEmpty())
			format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(millisecond)));
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Test.readFileByLines("");4668725532
		String license = "{\"appl\":2,\"ucl\":100,\"cid\":\"dh\",\"vs\":\"V1.4\",\"vli\":1488956022000,\"ic\":false}";
		Authorization authorization = new Authorization();
		sc = new Scanner(System.in);
		try {

			System.out.println("是否验证(true/false):");
			String ic = sc.next();
			authorization.setIc(new Boolean(ic));
			System.out.println("是否验证:" + authorization.isIc());
			
			if (ic.equals("true")) {
				System.out.println("输入APP限制(int):");
				String appl = sc.next();
				System.out.println("App限制个数为：" + appl);
				authorization.setAppl(Integer.parseInt(appl));
				System.out.println("输入用户限制(long):");
				String ucl = sc.next();
				System.out.println("用户限制个数为:" + ucl);
				authorization.setUcl(Long.parseLong(ucl));

				System.out.println("输入私有云客户ID(String):");
				String cid = sc.next();
				System.out.println("私有云客户ID为:" + cid);
				authorization.setCid(cid);

				System.out.println("输入版本信息(String):");
				String vs = sc.next();
				System.out.println("版本信息为:" + vs);
				authorization.setVs(vs);

				System.out.println("输入到期时间(毫秒):");
				String vli = sc.next();
				System.out.println("到期时间为:" + timeStamp2Date(vli, "yyyy-MM-dd HH:mm:ss"));
				authorization.setVli(Long.parseLong(vli));

			}

			Gson gson = new Gson();
			license = gson.toJson(authorization);
			System.out.println(license);
			System.out.println("请输入文件生成路径:");
			String patch = sc.next();
			System.out.println("路径:" + patch);
			Test.createConf(patch, license);
			Test.readConf(patch);
		} catch (Exception e) {
			System.out.println("请输入正确类型数据");
			// TODO: handle exception
		}

	}

}
