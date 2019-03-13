package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * プロパティ関連クラス.
 * <br/>
 * 
 * @author  y_nishikawa
 */
public class PropertyUtil{
	/** デフォルトプロパティファイル */
	private final String	DEFAULT_PATH = "resourse/default.properties";
	
	/** プロパティファイル */
	private Properties		prop;
	
	// ====================================================================================================
	
	/**
	 * コンストラクター.
	 * <br/>
	 * 
	 * @param propPath (String)プロパティファイルパス
	 */
	public PropertyUtil(String propPath){
		if(propPath == null || "".equals(propPath)){
			propPath = DEFAULT_PATH;
		}
		
		try{
			prop = new Properties();
			prop.load(Files.newBufferedReader(Paths.get(propPath), StandardCharsets.UTF_8));
			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	/**
	 * キー指定で設定値を取得.
	 * <br/>
	 * 
	 * @param key (String)キー
	 * @return (String)設定値
	 */
	public String getProperty(String key){
		return	getProperty(key, "");
	}
	
	/**
	 * キー指定で設定値を取得(デフォルト値指定あり).
	 * <br/>
	 * 
	 * @param key (String)キー
	 * @param defaultValue (String)デフォルト値
	 * @return (String)設定値
	 */
	public String getProperty(String key, String defaultValue){
		return	prop.getProperty(key, defaultValue);
	}
}
