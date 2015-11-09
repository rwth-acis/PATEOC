package main.sg.javapackage.sna.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import main.sg.javapackage.domain.CustomSubgraph;
import main.sg.javapackage.domain.Node;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Support class for feature extraction
 * -Node Level
 * @author Stephen
 *
 */
public class NodeFeatures {

	public NodeFeatures() {

	}
	/**
	 * Calculation of EigenVector Centrality of nodes in a graph
	 * @param subgraph - subgraph of the main graph corresponding
	 * 			to a community
	 * @return Double - vector of centrality measure
	 */
	public List<Double> eigenvectorcentralityLeadershipCalculation(CustomSubgraph subgraph){
		//Asserted
		int numVert = subgraph.vertexSet().size();
	    
		//matrix of nxn vertices for undirected subgraph
		DoubleMatrix eigenMatrix = new DoubleMatrix(new double[numVert][numVert]);
		Set<DefaultWeightedEdge> edges = subgraph.edgeSet();
		Set<Node> nodes = subgraph.vertexSet();
		if(!edges.isEmpty()){
			for(DefaultWeightedEdge edge : edges){
				//Diagonal value = 1 
				eigenMatrix.put(getIndex(nodes, subgraph.getEdgeTarget(edge)),
						getIndex(nodes, subgraph.getEdgeTarget(edge)), 1.0);
				
				eigenMatrix.put(getIndex(nodes, subgraph.getEdgeSource(edge)),
						getIndex(nodes, subgraph.getEdgeSource(edge)), 1.0);
				
				eigenMatrix.put(getIndex(nodes, subgraph.getEdgeTarget(edge)),
						getIndex(nodes, subgraph.getEdgeSource(edge)), 1.0);
				
				eigenMatrix.put(getIndex(nodes, subgraph.getEdgeSource(edge)),
						getIndex(nodes, subgraph.getEdgeTarget(edge)), 1.0);
			}
		}
		//compute eigenvector
		List<Double> eigenVector = getEigenvector(eigenMatrix);
		List<Double> newEigenVector = new ArrayList<Double>();
		
	    for(int i=0; i<eigenVector.size(); i++){
	    	newEigenVector.add( normalize(eigenVector.get(i), 0, getMax(eigenVector), 0, 1) );
	    }
	    
	    return newEigenVector;
	}
	
	/**
	 * Based on
	 * jblas-calculating-eigenvector-centrality-of-an-adjacency-matrix/
	 */
	private static List<Double> getEigenvector(DoubleMatrix matrix) {
	    int maxIndex = getMaxIndex(matrix);		    
	    ComplexDoubleMatrix eigenVectors = Eigen.eigenvectors(matrix)[0];
	    return getEigenVector(eigenVectors, maxIndex);
	}
	private static int getMaxIndex(DoubleMatrix matrix) {
	    ComplexDouble[] doubleMatrix = Eigen.eigenvalues(matrix).toArray();
	    int maxIndex = 0;
	    for (int i = 0; i < doubleMatrix.length; i++){
	        double newnumber = doubleMatrix[i].abs();
	        if ((newnumber > doubleMatrix[maxIndex].abs())){
	            maxIndex = i;
	        }
	    }
	    return maxIndex;
	}
	private static List<Double> getEigenVector(ComplexDoubleMatrix eigenvector, int columnId) {
	    ComplexDoubleMatrix column = eigenvector.getColumn(columnId);
	 
	    List<Double> values = new ArrayList<Double>();
	    for (ComplexDouble value : column.toArray()) {
	        values.add(value.abs()  );
	    }
	    return values;
	}
	
	/**
	 * Map value from one range to another range
	 * 
	 * @param value - i
	 * @param in_min range1 min
	 * @param in_max range1 max
	 * @param out_min range2 min
	 * @param out_max range2 max
	 * @return double
	 */
	private double normalize(double value, double in_min, double in_max, double out_min, double out_max)
	{
	  return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	/**
	 * custom max value function
	 */
	private double getMax(List<Double> list){
	    Double max = Double.MIN_VALUE;
	    for(int i=0; i<list.size(); i++){
	        if(list.get(i) > max){
	            max = list.get(i);
	        }
	    }
	    return max;
	}
	
	/**
	 * Returns the index of the node in the list for matrix operations
	 * 
	 * @param nodes - list of nodes
	 * @param value - nodevalue
	 * 
	 * @return Integer
	 */
	public static int getIndex(Set<Node> nodes, Object value) {
	   int result = 0;
	   for (Object entry:nodes) {
	     if (entry.equals(value)) return result;
	     result++;
	   }
	   return -1;
	}


}
