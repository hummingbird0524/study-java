package base64;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * BASE64学習.
 * <br/>
 * <p>BASE64エンコード/デコード学習用クラス。</p>
 * 
 * @author	y_nishikawa
 */
public class Base64EncDec{
	/**
	 * 画像ファイルからBASE64文字列へのエンコード.
	 * <br/>
	 * <p>指定された画像ファイルをエンコードしBASE64文字列を返却する。</p>
	 * 
	 * @param imgFilePath (String)エンコードする画像ファイルのパス
	 * @return base64Str (String)エンコードされたBASE64文字列
	 * @throws IOException 入出力例外
	 */
	public static String imgEncode(String imgFilePath)
		throws IOException{
		
		String	base64Str = null;
		
		try(
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			BufferedOutputStream		bos  = new BufferedOutputStream(baos)
		){
			// 画像ファイル読み込み
			BufferedImage	bi = ImageIO.read(new File(imgFilePath));
			
			bi.flush();
			ImageIO.write(bi, "jpeg", bos);
			
			// BASE64エンコード
			base64Str = Base64.getEncoder().encodeToString(baos.toByteArray());
			
		}catch(IOException ioe){
			throw	ioe;
		}
		
		return	base64Str;
	}
	
	/**
	 * BASE64文字列から画像ファイルへのデコード.
	 * <br/>
	 * <p>渡されたBASE64文字列をデコードし、指定されたパスに画像ファイルとして出力</p>
	 * 
	 * @param base64Str (String)BASE64文字列
	 * @param imgFilePath (String)出力ファイルパス
	 * @throws FileNotFoundException ファイル不存在例外
	 * @throws IOException 入出力例外
	 */
	public static void imgDecode(String base64Str, String imgFilePath)
		throws IOException{
		
		try(
			FileOutputStream	fos = new FileOutputStream(imgFilePath)
		){
			// BASE64デコード
			byte[]	decodeBytes = Base64.getDecoder().decode(base64Str);
			
			// デコード結果から画像ファイル生成
			fos.write(decodeBytes);
			fos.flush();
			
		}catch(FileNotFoundException fnfe){
			throw	fnfe;
			
		}catch(IOException ioe){
			throw	ioe;
		}
	}
	
	// ====================================================================================================
	
	/**
	 * 動作確認用mainメソッド.
	 * <br/>
	 * 
	 * @param args (String[])引数
	 */
	public static void main(String[] args){
		try{
			// ちょっと強引なパス指定…
			String	path = System.getProperty("user.dir") + "\\bin\\base64\\";
			
			System.out.println("**************************************************");
			System.out.println("* Step.1 Encode" );
			System.out.println("**************************************************");
			String	base64Str = imgEncode(path + "JavaLogo.jpg");
			
			System.out.println("* Encode Finish");
			
			System.out.println("Base64Str: ");
			for(int i=0; i<base64Str.length(); i++){
				System.out.print(base64Str.substring(i, i+1));
				
				if(i%100 == 0){
					System.out.println("");
				}
			}
			
			System.out.println();
			System.out.println("--------------------------------------------------");
			
			System.out.println("**************************************************");
			System.out.println("* Step.2 Decode" );
			System.out.println("**************************************************");
			imgDecode(base64Str, path + "JavaLogoDecoded.jpg");
			
			System.out.println("* Decode Finish!");
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
