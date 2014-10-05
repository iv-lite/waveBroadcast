package node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import tools.DataBuffer;

public class Edge extends Thread{
	public static int DEFAULT_PORT = 8000;
	
	private DataBuffer<Integer> toSend;
	private Socket socket;
	private InetAddress peerAddress;
	private int port;
	
	private boolean interrupted;

	public Edge(InetAddress peer) {
		this(peer, Edge.DEFAULT_PORT);
	}
	
	public Edge(InetAddress peerAddress, int port){
		this.toSend = new DataBuffer<Integer>();
		this.peerAddress = peerAddress;
		this.port = port;
		this.interrupted = false;
	}
	
	public void send(int data){
		this.toSend.push(data);
	}
	
	public void run(){
		while(!this.interrupted){
			int data = -47;

			try {
				data = toSend.pop();
			} catch (InterruptedException e) {
				return;
			}
			
			while(this.socket == null || this.socket.isOutputShutdown()) {
				try {
					this.socket = new Socket(this.peerAddress, this.port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			boolean sended = false;
			while(!sended || !this.interrupted) {
				try {
					this.socket.getOutputStream().write(data);
					sended = true;
				} catch (IOException e) {
					try {
						this.socket = new Socket(this.peerAddress, this.port);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				};
			}
		}
	}
	
	public void interrupt(){
		this.interrupted = true;
	}
}
