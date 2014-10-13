package node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
                try {
                    Socket connection = this.socket.accept();
                    this.handle(connection);
                } catch (SocketException e) {
                    this.socket = new ServerSocket(this.parent.getPort());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
		}

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
