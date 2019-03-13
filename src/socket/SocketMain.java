package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * Socket通信学習用Mainクラス(Windows環境用).
 * <br/>
 * 
 * @author	y_nishikawa
 */
public class SocketMain{
	/** Menu表示用 */
	private static final String[][]	MENU = {
		{"1", "TCP Socket Server", "socket.TcpSocketServer"}
	  , {"2", "TCP Socket Client", "socket.TcpSocketClient"}
	};
	
	// ====================================================================================================
	
	/**
	 * mainメソッド.
	 * <br/>
	 * 
	 * @param args (String[])引数
	 */
	public static void main(String[] args){
		String	selectedMenu  = "";
		String	selectedClass = "";
		
		try{
			while(true){
				// メニュー表示＆プログラム選択
				selectedMenu = dispMenu();
				
				// クリアスクリーン
				clearScreen();
				
				switch(selectedMenu){
					case "1":
					case "2":
						selectedClass = MENU[Integer.parseInt(selectedMenu)-1][2];
						break;
					
					case "q":
						System.out.println("# Program is over!!");
						System.out.println("# Please press any key...");
						System.exit(0);
					
					default:
						break;
					}
				
				System.out.println("");
				
				// プログラム実行
				reflection(selectedClass);
				
				// クリアスクリーン
				clearScreen();
			}
		}catch(InterruptedException ie){
			ie.printStackTrace();
			
		}catch(IOException ioe){
			ioe.printStackTrace();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * メニュー表示＆選択.
	 * <br/>
	 * 
	 * @return (String)メニュー選択値
	 */
	private static String dispMenu(){
		System.out.println("--------------------------------------------------");
		System.out.println("◆Socket通信テストプログラム");
        for(int i=0; i<MENU.length; i++){
			System.out.println("  " + MENU[i][0] + ". " + MENU[i][1]);
		}
		System.out.println("-------------------------");
		System.out.println("q. end");
		System.out.println("--------------------------------------------------");
		System.out.println("");
		
		System.out.print("### Please input program number: ");
		
		try{
			// closeするとエラーになる
			BufferedReader	br = new BufferedReader(new InputStreamReader(System.in));
			
			return	br.readLine();
			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return	"q";
	}
	
	/**
	 * [DOS]clsコマンド発行.
	 * <br/>
	 * 
	 * @throws InterruptedException 割り込み例外
	 * @throws IOException 入出力例外
	 */
	private static void clearScreen() throws InterruptedException, IOException{
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}
	
	/**
	 * リフレクションによるmainメソッド実行.
	 * <br/>
	 * 
	 * @param className (String)実行クラス名
	 * @throws Exception 例外(多すぎるのでまとめた…)
	 */
	private static void reflection(String className) throws Exception{
		// 実行クラス名よりインスタンス生成
		Object	classObject = Class.forName(className).newInstance();
		
		// 実行クラスのmainメソッドを取得
		Method	mainMethod = classObject.getClass().getMethod("main", String[].class);
		
		// 引数生成
		String[]	args = {"arg1"};
		
		// mainメソッド呼び出し
		mainMethod.invoke(null, (Object)args);
	}
}
