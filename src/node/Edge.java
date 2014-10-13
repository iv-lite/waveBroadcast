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
    private boolean finished;

	public Edge(InetAddress peer) {
		this(peer, Edge.DEFAULT_PORT);
	}
	
	public Edge(InetAddress peerAddress, int port){
		this.toSend = new DataBuffer<Integer>();
		this.peerAddress = peerAddress;
		this.port = port;
		this.interrupted = false;
        this.finished = false;
	}
	
	public void send(int data){
		this.toSend.push(data);
	}
	
	public void run(){
		while(!this.interrupted || !this.toSend.isEmpty()){
			int data = -47;

			try {
                this.finished = true;
				data = toSend.pop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

            this.finished = false;
			while(this.socket == null || this.socket.isOutputShutdown()) {
				try{
                    try {
                        this.socket = new Socket(this.peerAddress, this.port);
                    } catch (IOException e) {
                        // Edge is unable to connect onto provided neighbor
                        // Thus, it should sleep 1s before trying again.
                        this.sleep(1000);
                    }
                }catch (InterruptedException e1) {
                }
			}
			
			boolean sended = false;
			while(!sended) {
				try {
					this.socket.getOutputStream().write(data);
                    System.out.println("Sending "+data+" to "+peerAddress);
					sended = true;
				} catch (IOException e) {
                    try{
                        try {
                            this.socket = new Socket(this.peerAddress, this.port);
                        } catch (IOException e1) {
                            // Edge is unable to connect onto provided neighbor
                            // Thus, it should sleep 1s before trying again.
                            this.sleep(1000);
                        }
                    }catch (InterruptedException e1) {
                    }
				};
			}
		}
        this.finished = true;

        try {
            this.socket.close();
        } catch (IOException e) {
            // Socket was already closed.
        }
    }
	
	public void interrupt(){
		this.interrupted = true;
	}

    public boolean isFinished(){
        return this.finished;
    }
}
