package node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
	
	private ServerSocket socket;
	private Node parent;
	private boolean interrupted;

	public Server(Node parent) {
		this.parent = parent;
		this.interrupted = false;
	}
	
	public void run(){
		try {
			this.socket = new ServerSocket(this.parent.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		while(!this.interrupted){
			try {
				Socket connection = this.socket.accept();
				this.handle(connection);
			} catch (IOException e) {
				
			}
		}
	}
	
	private void handle(Socket connection) throws IOException{
		int data = connection.getInputStream().read();
		System.out.println("Received data: "+data);
		this.parent.send(data);
	}
	
	public void interrupt(){
		this.interrupted = true;
		try {
			this.socket.close();
		} catch (Exception e) {
		}
	}
}
