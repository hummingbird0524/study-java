package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import util.PropertyUtil;

/**
 * Socket通信学習用サーバークラス.
 * 
 * @author	y_nishikawa
 */
public class TcpSocketServer{
	/** プロパティファイルパス */
	private final String	PROP_PATH = "resources/socket.properties";
	
	/** ポート番号 */
	private String	port;
	
	// ====================================================================================================
	
	/**
	 * mainメソッド.
	 * <br/>
	 * 
	 * @param args (String[])引数
	 */
	public static void main(String[] args){
		TcpSocketServer	srt = new TcpSocketServer();
		
		srt.execute();
	}
	
	/**
	 * コンストラクター
	 */
	public TcpSocketServer(){
		PropertyUtil	pu = new PropertyUtil(PROP_PATH);
		
		port = pu.getProperty("SOCKET_PORT", "50000");
	}
	
	/**
	 * Socket通信サーバー.
	 * <br/>
	 */
	public void execute(){
		System.out.println("--------------------------------------------------");
		System.out.println("- [Java] TCP Socket Server");
		System.out.println("--------------------------------------------------");
		
		Socket				socket   = null;
		ServerSocket		ss       = null;
		
		BufferedReader	br       = null;
		String				recvText = null;
		
		try{
			// 待ち受けSocket生成
			ss = new ServerSocket(Integer.parseInt(port));
			
			System.out.print("### Waiting for connections");
			
			while(true){
				System.out.println("...");
				
				// 接続待機
				socket = ss.accept();
				
				// テキスト受信
				br       = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				recvText = br.readLine();
				
				System.out.println("received: " + recvText);
				
				if("quit".equals(recvText)){
					// 接続終了
					return;
				}
				
				br.close();
				socket.close();
			}
		}catch(UnknownHostException e){
			e.printStackTrace();
			
		}catch(IOException e){
			e.printStackTrace();
			
		}finally{
			try{
				if(br     != null){br.close();}
				if(socket != null){socket.close();}
				if(ss     != null){ss.close();}
				
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Socket通信サーバー(Thread実行用).
	 * <br/>
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
