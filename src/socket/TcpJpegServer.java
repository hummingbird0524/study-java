package	socket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.PropertyUtil;

/**
 * Socket通信による画像転送学習用サーバークラス.
 * <br/>
 * 
 * @author	y_nishikawa
 */
public class TcpJpegServer implements Runnable{
	/** プロパティファイルパス */
	private static final String	PROP_PATH = "resources/socket.properties";
	/** Jpeg受信命令     */
	private static final String	JPEG_CODE  = "JPEG";
	/** 通信終了命令     */
	private static final String	END_CODE   = "QUIT";
	/** 受信バイト数     */
	private static final int		RECV_LEN   = 512;
	
	/** TCP通信ポート    */
	private static String	port;
	
	// ====================================================================================================
	
	/*
	 * (非 Javadoc)
	 * @see	java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		ServerSocket		serverSocket  = null;	// TCP通信サーバーソケット
		Socket				socket        = null;	// TCP通信ソケット
		
		InputStream			is            = null;	// 受信ストリーム
		FileOutputStream	outputFile    = null;	// JPEG出力ストリーム
		
		// 設定読込み
		readProperty();
		
		System.out.println("**************************************************");
		System.out.println("* TCP Receiver is Running...");
		System.out.println("**************************************************");
		
		try{
			// ポートより、TCP通信用のソケットを生成
			serverSocket = new ServerSocket(Integer.parseInt(port));
			
			String	receive     = null;
			byte[]	jpegByte    = null;
			int		receiveByte = 0;
			
			while(true){
				if(JPEG_CODE.equals(receive)){
					// JPEG受信
					SimpleDateFormat	sdf   = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss.SSSSSSSSS");
					
					// JPEG受信待ち
					System.out.println("$ wait image...");
					
					socket.close();
					socket = serverSocket.accept();
					
					// 開始時刻表示
					System.out.println("### Jpeg Receive Start : " + sdf.format(new Date()));
					
					// 受信処理
					jpegByte = new byte[RECV_LEN];
					is = socket.getInputStream();
					
					outputFile = new FileOutputStream("received.jpg");
					
					receiveByte = 0;
					
					while(is.read(jpegByte) > 0){
						receiveByte += RECV_LEN;
						
						outputFile.write(jpegByte);
						outputFile.flush();
						
						if(receiveByte % 1024000 == 0){
							System.out.println(receiveByte + "Byte is received...");
						}
					}
					
					outputFile.flush();
					outputFile.close();
					
					System.out.println(receiveByte + "Byte is received...Finish!");
					
					// 終了時刻表示
					System.out.println("### Jpeg Send End   : " + sdf.format(new Date()));
					
					receive = null;
					
				}else{
					// 受信待ち
					System.out.println("$ wait message...");
					
					socket = serverSocket.accept();
					
					// 受信処理
					jpegByte   = new byte[RECV_LEN];
					is = socket.getInputStream();
					is.read(jpegByte);
					
					// 後方空白を除去
					receive = new String(jpegByte).trim();
					
					System.out.println("msg: " + receive);
				}
				
				socket.close();
				
				if(END_CODE.equals(receive)){
					// 通信終了命令の場合
					break;
				}
			}
		}catch (IOException e){
			// 入出力例外
			e.printStackTrace();
			
		}finally{
			try {
				if(socket        != null){socket.close();}
				if(serverSocket  != null){serverSocket.close();}
				
			}catch (IOException e){
				// 入出力例外
				e.printStackTrace();
			}
		}
		
		System.out.println("**************************************************");
		System.out.println("* TCP Receiver Is End...");
		System.out.println("**************************************************");
	}
	
	/**
	 * 設定読込み.
	 * <br/>
	 */
	private void readProperty(){
		PropertyUtil	pu = new PropertyUtil(PROP_PATH);
		
		port    = pu.getProperty("SOCKET_PORT", "50000");
	}
	
	// ====================================================================================================
	
	/**
	 * テスト用main処理.
	 * <br/>
	 * 
	 * @param args (String[])引数
	 */
	public static void main(String[] args){
		new Thread(new TcpJpegServer()).start();
	}
}
