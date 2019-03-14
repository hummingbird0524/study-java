package	socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import util.ConvertUtil;
import util.PropertyUtil;

/**
 * Socket通信による画像転送学習用サーバークラス.
 * <br/>
 * 
 * @author	y_nishikawa
 */
public class UdpJpegClient implements Runnable{
	/** プロパティファイルパス */
	private static final String	PROP_PATH = "resources/socket.properties";
	/** Jpeg送信命令     */
	private static final String	JPEG_CODE = "JPEG";
	/** 通信終了命令     */
	private static final String	END_CODE  = "QUIT";
	/** 送信バイト数     */
	private static final int		SEND_LEN  = 512;
	
	/** 通信先IPアドレス */
	private static String	address;
	/** UDP通信ポート    */
	private static String	port;
	
	// ====================================================================================================
	
	/*
	 * (非 Javadoc)
	 * @see	java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		DatagramSocket	udpSocket   = null;	// UDP通信ソケット
		DatagramPacket	packet      = null;	// UDPパケット
		InetAddress		iAddress    = null;	// UDPパケットに設定する為のアドレス
		
		BufferedReader	inputReader = null;	// 送信内容入力用リーダー
		
		// 設定読込み
		readProperty();
		
		System.out.println("**************************************************");
		System.out.println("* UDP Sender is Running...");
		System.out.println("**************************************************");
		
		try{
			// 送信内容入力用リーダーを生成
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			
			String	input     = null;	// 入力文字列
			byte[]	sendBytes = null;	// 送信用バイト配列
			int		sendByte  = 0;		// 送信済みbyte数
			
			while(true){
				// UDPソケットを生成
				udpSocket = new DatagramSocket();
				// UDPパケットに渡す為のアドレスを作る
				iAddress = InetAddress.getByName(address);
				
				if(JPEG_CODE.equals(input)){
					// Jpegファイルを送信
					
					// 送信ファイル名入力
					System.out.print("$ input jpeg file: ");
					input = inputReader.readLine();
					
					// UDPソケットを生成
					udpSocket.close();
					udpSocket = new DatagramSocket();
					
					// Jpegファイルをbyte配列に変換
					sendBytes = ConvertUtil.jpeg2byte(input);
					
					sendByte = 0;
					
					packet = new DatagramPacket(String.valueOf(sendBytes.length).getBytes(), 0, String.valueOf(sendBytes.length).getBytes().length, iAddress, Integer.parseInt(port));
					
					udpSocket.send(packet);
					
					while(sendByte < sendBytes.length - SEND_LEN){
						packet = new DatagramPacket(sendBytes, 0 + sendByte, SEND_LEN, iAddress, Integer.parseInt(port));
						
						udpSocket.send(packet);
						
						sendByte += SEND_LEN;
						
						if(sendByte % 1024000 == 0){
							System.out.println(sendByte + "Byte is sent...");
						}
					}
					
					packet = new DatagramPacket(sendBytes, sendByte, sendBytes.length - sendByte, iAddress, Integer.parseInt(port));
					
					udpSocket.send(packet);
					
					System.out.println(sendBytes.length + "Byte is sent...Finish!");
					
					// ソケットを一旦閉じる
					udpSocket.close();
				}
				
				System.out.print("$ input message: ");
				
				// 送信内容入力
				input = inputReader.readLine();
				
				// UDPパケットに乗せる為に、送信メッセージをバイト配列化
				sendBytes = input.getBytes();

				// 送信メッセージ、相手先IPアドレス、相手先ポートを指定して
				// UDPパケットを生成
				packet = new DatagramPacket(sendBytes, sendBytes.length, iAddress, Integer.parseInt(port));

				// UDPソケット使用して、UDPパケットを送る
				udpSocket.send(packet);
				
				// ソケットを一旦閉じる
				udpSocket.close();
				
				if(END_CODE.equals(input)){
					// 通信終了命令の場合
					break;
				}
			}
		}catch(SocketException e){
			// ソケット生成時例外
			e.printStackTrace();
			
		}catch(UnknownHostException e){
			// 接続先不存在例外
			e.printStackTrace();
			
		}catch(IOException e){
			// 入出力例外
			e.printStackTrace();
			
		}finally{
			try{
				// クローズ処理
				if(inputReader != null){inputReader.close();}
				if(udpSocket   != null){udpSocket.close();}
				
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		System.out.println("**************************************************");
		System.out.println("* UDP Sender is End...");
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
		new Thread(new UdpJpegClient()).start();
	}
}
