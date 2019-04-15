package cn.rf.hz.web.utils;

/**
 * &copy; 2012 杭州立方自动化有限公司
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.Base64Encoder;

/**
 * 加密解密工具类
 * 
 * @author ZHUWEI
 * @create 2012-10-16
 * @version 1.0
 */
public class EncryptDecryptUtil
{
	/** 定义 加密算法,可用 DES,DESede,Blowfish */
	private static String Algorithm = "DESede";
	/** 定义 加密KEY */
	private static final String DEFAULT_KEY = "A3F2DEI566WBCJSJEOTY45DYQWF68H2Y";

	/**
	 * 加密
	 * 
	 * @param value
	 *            待加密的值
	 * @return 返回加密后字符串
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public static String encryptString(String value) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			UnsupportedEncodingException
	{
		return encryptString(DEFAULT_KEY, value);
	}

	public static String encryptString(String key, String value) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException
	{
		byte[] bytesKey = null, bytes = null, bytesCipher = null;
		SecretKey deskey = null;
		if (value == null)
			new IllegalArgumentException("待加密字串不允许为空");

		/* 密码器 */
		Cipher desCipher = Cipher.getInstance(Algorithm);
		try
		{
			bytesKey = Base64Decoder.decodeToBytes(key);
			deskey = new SecretKeySpec(bytesKey, Algorithm);
			bytes = value.getBytes(); // 待解密的字串
			desCipher.init(Cipher.ENCRYPT_MODE, deskey);// 初始化密码器，用密钥deskey,进入解密模式
			bytesCipher = desCipher.doFinal(bytes);
			return Base64Encoder.encode(bytesCipher).trim();
		} finally
		{
			bytesKey = null;
			bytes = null;
			bytesCipher = null;
		}
	}

	/**
	 * 解密
	 * 
	 * @param value
	 *            待解密的值
	 * @return 解密后的字符串
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public static String decryptString(String value) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			UnsupportedEncodingException
	{
		return decryptString(DEFAULT_KEY, value);
	}

	public static String decryptString(String key, String value) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException
	{
		byte[] bytesKey = null, bytes = null, bytesCipher = null;
		SecretKey deskey = null;
		if (value == null)
			new IllegalArgumentException("待解密字串不允许为空");

		/* 密码器 */
		Cipher desCipher = Cipher.getInstance(Algorithm);
		try
		{
			bytesKey = Base64Decoder.decodeToBytes(key);
			deskey = new SecretKeySpec(bytesKey, Algorithm);
			bytes = Base64Decoder.decodeToBytes(value); // 加密后的字串
			desCipher.init(Cipher.DECRYPT_MODE, deskey); // 初始化密码器，用密钥deskey,进入解密模式
			bytesCipher = desCipher.doFinal(bytes);
			return new String(bytesCipher, "UTF-8");
		} finally
		{
			bytesKey = null;
			bytes = null;
			bytesCipher = null;
		}
	}

	public static void main(String args[])
	{
		try
		{
			String strEncode = encryptString("tttttttt");
			System.out.println("加密返回：" + strEncode);
			String strOrg = decryptString(strEncode);
			System.out.println("解密返回：" + strOrg);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
