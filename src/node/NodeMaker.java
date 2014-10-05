package node;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class NodeMaker {
	public static Node parse(String[] params) throws Exception{
		if(params.length < 3)
			throw new Exception("Wrong number of parameters: "+params.length+" given");
		
		int port = Integer.valueOf(params[0]);

		ArrayList<InetAddress> peers = NodeMaker.getPeers(params[1]);
		Node.type type = NodeMaker.getType(params[2]);
		
		if(params.length > 3)
			return new Node(port, Integer.valueOf(params[3]), peers, type);
		
		return new Node(port, peers, type);
	}
	
	public static Node.type getType(String type){
		return Node.type.valueOf(type);
	}
	
	public static ArrayList<InetAddress> getPeers(String file) throws FileNotFoundException{
		Scanner scan = new Scanner(new FileInputStream(file));
		scan.nextLine();
		ArrayList<InetAddress> peers = new ArrayList<InetAddress>();
		while(scan.hasNext()) {
			try {
				peers.add(InetAddress.getByName(scan.nextLine()));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		scan.close();
		
		return peers;
	}
}
