package	socket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import util.PropertyUtil;

/**
 * Socket通信による画像転送学習用サーバークラス.
 * <br/>
 * 
 * @author	y_nishikawa
 */
public class UdpJpegServer implements Runnable{
	/** プロパティファイルパス */
	private static final String	PROP_PATH = "resources/socket.properties";
	/** Jpeg受信命令     */
	private static final String	JPEG_CODE = "JPEG";
	/** 通信終了命令     */
	private static final String	END_CODE  = "QUIT";
	/** 受信バイト数     */
	private static final int		RECV_LEN  = 512;
	
	/** UDP通信ポート    */
	private static String	port;
	
	// ====================================================================================================
	
	/*
	 * (非 Javadoc)
	 * @see	java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		DatagramSocket	udpSocket  = null;	// UDP通信ソケット
		DatagramPacket	packet     = null;	// UDPパケット
		
		FileOutputStream	outputFile = null;	// JPEG出力ストリーム
		
		// 受信したメッセージを格納するバイト配列
		byte[]	receiveMsg = new byte[RECV_LEN];

		String	receive     = null;
		int		jpegByte    = 0;
		int		receiveByte = 0;
		
		// 設定読込み
		readProperty();
		
		System.out.println("**************************************************");
		System.out.println("* UDP Receiver is Running...");
		System.out.println("**************************************************");
		
		try{
			// UDP通信ソケットを生成
			udpSocket = new DatagramSocket(Integer.parseInt(port));
			
			while(true){
				if(JPEG_CODE.equals(receive)){
					// JPEG受信命令の場合
					System.out.println("$ wait image...");
					
					outputFile = new FileOutputStream("received.jpg");
					
					// 受信パケットの受け皿を生成する
					packet = new DatagramPacket(receiveMsg, receiveMsg.length);
					
					receiveByte = 0;
					
					// UDPパケットを受信する
					udpSocket.receive(packet);
					
					jpegByte = Integer.parseInt(new String(receiveMsg).trim());
					
					while(true){
						// UDPパケットを受信する
						udpSocket.receive(packet);
						
						receiveByte += receiveMsg.length;
						
						outputFile.write(receiveMsg);
						outputFile.flush();
						
						if(receiveByte >= jpegByte) {
							break;
						}
						
						if(receiveByte % 1024000 == 0){
							System.out.println(receiveByte + "/" + jpegByte + "Byte is received...");
						}
					}
					
					outputFile.close();
				}
				
				System.out.println("$ wait message...");
				
				// 受信パケットの受け皿を生成する
				packet = new DatagramPacket(receiveMsg, receiveMsg.length);
				
				// UDPパケットを受信する
				udpSocket.receive(packet);
				
				// 受信したメッセージを文字列に変換
				receive = new String(receiveMsg, 0, receiveMsg.length, "MS932").trim();

				System.out.println("msg: " + receive);
				
				receiveMsg = new byte[256];
				
				if(END_CODE.equals(receive)){
					// 通信終了命令の場合
					break;
				}
			}
		}catch(SocketException e){
			// ソケット例外
			e.printStackTrace();
			
		}catch(UnsupportedEncodingException e){
			// エンコード例外
			e.printStackTrace();
			
		}catch(IOException e){
			// 入出力例外
			e.printStackTrace();
		}
		
		System.out.println("**************************************************");
		System.out.println("* UDP Receiver is End...");
		System.out.println("**************************************************");
	}
	
	/**
	 * 設定読込み.
	 * <br/>
	 */
	private void readProperty(){
		PropertyUtil	pu = new PropertyUtil(PROP_PATH);
		
		port = pu.getProperty("SOCKET_PORT", "50000");
	}
	
	// ====================================================================================================
	
	/**
	 * テスト用main処理.
	 * <br/>
	 * 
	 * @param args (String[])引数
	 */
	public static void main(String[] args){
		new Thread(new UdpJpegServer()).start();
	}

}
