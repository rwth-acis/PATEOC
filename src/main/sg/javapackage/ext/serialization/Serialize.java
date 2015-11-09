package main.sg.javapackage.ext.serialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import main.sg.javapackage.domain.Node;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;

/**
 * Class to serialize/ de-serialize the graph object 
 * @author Stephen
 *
 */
public class Serialize {
	//deprecated
	public Serialize() {
		
	}
	
	public static void serializeGraphOutput(UndirectedGraph<Node, DefaultEdge> Graph, int timestep) throws IOException{
		
		FileOutputStream out = new FileOutputStream("SerialGraph_"+timestep+".tmp");
        ObjectOutputStream oout = new ObjectOutputStream(out);

        // write to the file
        oout.writeObject(Graph);

        // close the stream
        oout.close();
	}
	
	@SuppressWarnings("unchecked")
	public static UndirectedGraph<Node, DefaultEdge> serializeGraphInput(int timestep) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		FileInputStream in = new FileInputStream("SerialGraph_"+timestep+".tmp");
		ObjectInputStream oin = new ObjectInputStream(in);

        // read and print what we wrote before
        UndirectedGraph<Node, DefaultEdge> tempGraph = (UndirectedGraph<Node, DefaultEdge>) oin.readObject();
		System.out.println("First Check " + tempGraph.vertexSet().size() + " " + tempGraph.edgeSet().size());

        oin.close();
        return tempGraph;
	}

}
