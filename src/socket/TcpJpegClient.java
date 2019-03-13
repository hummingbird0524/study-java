package	socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.ConvertUtil;
import util.PropertyUtil;

/**
 * Socket通信による画像転送学習用クライアントクラス.
 * <br/>
 * 
 * @author	y_nishikawa
 */
public class TcpJpegClient implements Runnable{
	/** プロパティファイルパス */
	private static final String	PROP_PATH = "resources/socket.properties";
	/** Jpeg送信命令           */
	private static final String	JPEG_CODE = "JPEG";
	/** 通信終了命令           */
	private static final String	END_CODE  = "QUIT";
	/** 送信バイト数           */
	private static final int		SEND_LEN  = 512;
	
	
	/** 通信先IPアドレス */
	private static String	address;
	/** TCP通信ポート    */
	private static String	port;
	
	// ====================================================================================================
	
	/*
	 * (非 Javadoc)
	 * @see	java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		Socket				tcpSocket   = null;	// TCP通信ソケット
		OutputStream		sendStream  = null;	// 送信用ストリーム
		BufferedReader	inputReader = null;	// 送信内容入力用リーダー
		
		// 設定読込み
		readProperty();
		
		System.out.println("**************************************************");
		System.out.println("* TCP Sender is Running...");
		System.out.println("**************************************************");
		
		try{
			// 送信内容入力用リーダーを生成
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			
			String	input    = null;	// 入力文字列
			byte[]	jpegByte = null;	// byte配列化したJpeg画像
			int		sendByte = 0;		// 送信済みbyte数
			
			while(true){
				// IPアドレスとポートより、TCP通信用のソケットを生成
				tcpSocket  = new Socket(address, Integer.parseInt(port));
				
				// 送信用ストリームを生成
				sendStream  = tcpSocket.getOutputStream();
				
				if(JPEG_CODE.equals(input)){
					// Jpegファイルを送信
					
					// 送信ファイル名入力
					System.out.print("$ input jpeg file: ");
					input = inputReader.readLine();
					
					SimpleDateFormat	sdf   = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss.SSSSSSSSS");
					Date				start = null;
					Date				end   = null;
					
					// Jpegファイルをbyte配列に変換
					jpegByte = ConvertUtil.jpeg2byte(input);
					
					sendByte = 0;
					
					// 開始時刻表示
					start = new Date();
					System.out.println("### Jpeg Send Start : " + sdf.format(start));
					
					// 指定したバイト数ずつ送信
					while(sendByte < jpegByte.length - SEND_LEN){
						sendStream.write(jpegByte, sendByte, SEND_LEN);
						sendStream.flush();
						
						sendByte += SEND_LEN;
						
						if(sendByte % 1024000 == 0){
							System.out.println(sendByte + "Byte is sent...");
						}
					}
					
					sendStream.write(jpegByte, sendByte, jpegByte.length - sendByte);
					sendStream.flush();
					
					System.out.println(jpegByte.length + "Byte is sent...Finish!");
					
					// 終了時刻表示
					end = new Date();
					System.out.println("### Jpeg Send End   : " + sdf.format(end));
					
					// 入力文字列をクリア
					input = null;
					
				}else{
					// 送信内容入力
					System.out.print("$ input message: ");
					input = inputReader.readLine();
					
					// 入力文字列を送信
					sendStream.write(input.getBytes());
					sendStream.flush();
				}
				
				// ストリーム＆ソケットを閉じる
				sendStream.close();
				tcpSocket.close();
				
				if(END_CODE.equals(input)){
					// 通信終了命令の場合
					break;
				}
			}
		}catch(UnknownHostException e){
			// 接続先不存在例外
			e.printStackTrace();
			
		}catch (IOException e){
			// 入出力例外
			e.printStackTrace();
			
		}finally{
			try{
				// クローズ処理
				if(inputReader != null){inputReader.close();}
				if(sendStream  != null){sendStream.close();}
				if(tcpSocket   != null){tcpSocket.close();}
				
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		System.out.println("**************************************************");
		System.out.println("* TCP Sender is End...");
		System.out.println("**************************************************");
	}
	
	/**
	 * 設定読込み.
	 * <br/>
	 */
	private void readProperty(){
		PropertyUtil	pu = new PropertyUtil(PROP_PATH);
		
		address = pu.getProperty("SOCKET_IP",   "127.0.0.1");
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
		new Thread(new TcpJpegClient()).start();
	}
}
