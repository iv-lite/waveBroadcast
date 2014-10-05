package bootstrap;

import node.Node;
import node.NodeMaker;

public class Start {

	public static void main(String[] args) {
		try {
			Node localNode = NodeMaker.parse(args);
			localNode.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
