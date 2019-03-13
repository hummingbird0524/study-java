package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import util.PropertyUtil;

/**
 * Socket通信学習用クライアントクラス.
 * 
 * @author	y_nishikawa
 */
public class TcpSocketClient{
	/** プロパティファイルパス */
	private final String	PROP_PATH = "resources/socket.properties";
	
	/** IPアドレス */
	private String address = "127.0.0.1";
	/** ポート番号 */
	private String port    = "50000";
	
	// ====================================================================================================
	
	/**
	 * main処理
	 * 
	 * @param	args	(String[])引数
	 */
	public static void main(String[] args){
		TcpSocketClient	sst = new TcpSocketClient();
		
		sst.execute();
	}
	
	/**
	 * コンストラクター
	 */
	public TcpSocketClient(){
		PropertyUtil	pu = new PropertyUtil(PROP_PATH);
		
		address = pu.getProperty("SOCKET_IP",   "127.0.0.1");
		port    = pu.getProperty("SOCKET_PORT", "50000");
	}
	
	/**
	 * Socket通信クライアント
	 */
	private void execute(){
		System.out.println("--------------------------------------------------");
		System.out.println("- [Java] TCP Socket Client");
		System.out.println("--------------------------------------------------");
		
		try{
			Socket				socket   = null;
			PrintWriter		pw       = null;
			
			BufferedReader	br       = null;
			String				sendText = "";
			
			try{
				while(!"quit".equals(sendText)){
					System.out.print("Input send message: ");
					
					// 入力待機
					br       = new BufferedReader(new InputStreamReader(System.in));
					sendText = br.readLine();
					
					// Socket接続
					socket = new Socket(address, Integer.parseInt(port));
					pw     = new PrintWriter(socket.getOutputStream());
					
					// 入力テキストを送信
					pw.println(sendText);
					pw.flush();
					
					System.out.println("***** Send success *****");
				}
			}catch(UnknownHostException e){
				// 接続先未発見例外
				e.printStackTrace();
				
			}catch(IOException e){
				// 入出力例外
				e.printStackTrace();
				
			}finally{
				try{
					if(pw     != null){pw.close();}
					if(socket != null){socket.close();}
					
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Socket通信クライアント(Thread実行)
	 */
	public void executeThread(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				execute();
			}
		}).start();
	}
}
