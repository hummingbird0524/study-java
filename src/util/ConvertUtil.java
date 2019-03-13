package util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * 変換関連クラス.
 * <br/>
 * 
 * @author	y_nishikawa
 */
public class ConvertUtil{
	/**
	 * Jpegファイルをbyte配列に変換する.
	 * <br/>
	 * 
	 * @param fileName (String)変換対象Jpegファイルパス
	 * @return (byte[])変換後byte配列
	 */
	public static byte[] jpeg2byte(String fileName){
		BufferedImage				bi   = null;
		
		try(
			InputStream					is   = new FileInputStream(fileName);
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		){
			// 変換対象jpegファイルの読み込み
			bi   = ImageIO.read(is);
			
			ImageIO.write(bi, "jpeg", new BufferedOutputStream(baos));
			
			return	baos.toByteArray();
			
		}catch(FileNotFoundException e){
			// ファイル不存在例外
			e.printStackTrace();
			
		}catch(IOException e){
			// 入出力例外
			e.printStackTrace();
		}
		
		return	null;
	}
	
	/**
	 * byte配列をJpegファイルに変換する.
	 * <br/>
	 * 
	 * @param imgSrc (byte[])変換対象byte配列
	 * @param fileName (String)変換後Jpegファイル名
	 * @return (boolean)変換成否(true: 成功 / false: 失敗)
	 */
	public static boolean byte2jpeg(byte[] imgSrc, String fileName){
		BufferedImage	bi = null;
		
		try{
			bi = ImageIO.read(new ByteArrayInputStream(imgSrc));
			ImageIO.write(bi, "jpeg", new File(fileName));
			
			return	true;
			
		}catch(IOException e){
			// 入出力例外
			e.printStackTrace();
		}
		
		return	false;
	}
}
