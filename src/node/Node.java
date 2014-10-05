package node;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Node extends Thread{
	public enum type{
		INIT, WAIT
	}
	public static int DEFAULT_DATA = 47;
	
	private int port;
	private int destPort;
	private ArrayList<Edge> edges;
	private int data;
	private Server server;
	
	private Semaphore counter;
	
	public Node(int port, ArrayList<InetAddress> peers, type type){
		this(port, port, peers, type);
	}
	
	public Node(int port, int destPort, ArrayList<InetAddress> peers, type type){
		this.setPorts(port, destPort);
		this.setPeers(peers);
		if(type == Node.type.INIT)
			this.send(Node.DEFAULT_DATA, false);
	}
	
	public void send(int data){
		this.send(data, true);
	}
	
	public void send(int data, boolean release) {
		this.setData(data);
		for(Edge edge: edges)
			edge.send(data);
		if(this.counter.availablePermits() < 1)
			System.out.println("Sending data to all peers.");
		if(release)
			this.counter.release();
	}

	private void setData(int data) {
		if(this.data == data)
			return;
		
		this.data = data;
		System.out.println("Local data changed to: "+data);
	}

	public void setPorts(int port, int destPort){
		if(this.port < 0 || this.destPort < 0)
			return;
		
		this.port = port;
		this.destPort = destPort;
		this.server = new Server(this);
	}
	
	public void setPeers(ArrayList<InetAddress> peers){
		this.edges = new ArrayList<Edge>();
		for(InetAddress peer: peers)
			this.edges.add(new Edge(peer, this.destPort));
		
		this.counter = new Semaphore(1-this.edges.size());
	}
	
	public void run() {
		server.start();
		for(Edge edge: edges) 
			edge.start();
		
		boolean valided = false;
		while(!valided) {
			try {
				this.counter.acquire();
				valided = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Data was send to all peers");

		for(Edge edge: edges)
			edge.interrupt();
		
		this.server.interrupt();
		
		System.exit(0);
	}

	public int getPort() {
		return this.port;
	}
}
