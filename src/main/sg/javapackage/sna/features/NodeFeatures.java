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
import org.la4j.matrix.Matrix;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.BasicVector;

/**
 * @author Stephen
 * Based on algorithm by Sebastian - https://github.com/rwth-acis/REST-OCD-Services
 *
 */
public class NodeFeatures {

	/**
	 * The iteration bound for the leadership calculation phase. The default
	 * value is 1000. Must be greater than 0.
	 */
	private int randomWalkIterationBound = 1000;
	/**
	 * The precision factor for the leadership calculation phase.
	 * The phase ends when the infinity norm of the difference between the updated vector and
	 * the previous one is smaller than this factor.
	 * The default value is 0.001. Must be greater than 0 and smaller than infinity.
	 * Recommended are values close to 0.
	 */
	private double randomWalkPrecisionFactor = 0.001;

	
	public NodeFeatures() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * Executes the random walk phase of the algorithm and returns global
	 * leaders.
	 * 
	 * @param graph The graph whose leaders will be detected.
	 * 
	 * @return A list containing all nodes which are global leaders.
	 */
	protected List<Double> disassortativityLeadershipCalculation(CustomSubgraph subgraph){
		Matrix disassortativityMatrix = getTransposedDisassortativityMatrix(subgraph);
		Vector disassortativityVector = executeRandomWalk(disassortativityMatrix);
		Vector leadershipVector = getLeadershipValues(subgraph,
				disassortativityVector);
		
		//Convert to list
		List<Double> leadershipValues = new ArrayList<Double>();
        for(int i =0;i < leadershipVector.length();i++){
        	leadershipValues.add(leadershipVector.get(i));
        }

		return leadershipValues;

	}

	/**
	 * Returns the transposed normalized disassortativity matrix for the random
	 * walk phase.
	 * 
	 * @param subgraph The graph whose disassortativity matrix will be derived.
	 * 
	 * @return The transposed normalized disassortativity matrix.
	 */
	protected Matrix getTransposedDisassortativityMatrix(CustomSubgraph subgraph){
		/**
		 * Calculates transposed disassortativity matrix in a special sparse
		 * matrix format.
		 */
		Matrix disassortativities = new CCSMatrix(subgraph.vertexSet().size(),
				subgraph.vertexSet().size());
		
		Set<DefaultWeightedEdge> edges = subgraph.edgeSet();
		Set<Node> nodes = subgraph.vertexSet();
		double disassortativity;	
		
		if(!edges.isEmpty()){
			for(DefaultWeightedEdge edge : edges){
				disassortativity = Math
						.abs(subgraph.degreeOf((subgraph.getEdgeTarget(edge)))
								- subgraph.degreeOf(subgraph.getEdgeSource(edge)));
				disassortativities.set( getIndex(nodes, subgraph.getEdgeTarget(edge)),
						getIndex(nodes, subgraph.getEdgeSource(edge)), disassortativity);
				
				//TODO: SHOULD THIS BE HERE????!!
				disassortativities.set( getIndex(nodes, subgraph.getEdgeSource(edge)),
						getIndex(nodes, subgraph.getEdgeTarget(edge)), disassortativity);
			}
		}

		/*
		 * Column normalizes transposed disassortativity matrix.
		 */
		double norm;
		Vector column;

		for (int i = 0; i < disassortativities.columns(); i++) {
			column = disassortativities.getColumn(i);
			norm = column.fold(Vectors.mkManhattanNormAccumulator());
			if (norm > 0) {
				disassortativities.setColumn(i, column.divide(norm));
			}
		}
		return disassortativities;
	}
	/**
	 * Executes the random walk for the random walk phase. The vector is
	 * initialized with a uniform distribution.
	 * 
	 * @param disassortativityMatrix The disassortativity matrix on which the
	 * random walk will be performed.
	 * 
	 * @return The resulting disassortativity vector.
	 */
	protected Vector executeRandomWalk(Matrix disassortativityMatrix) {
		
		Vector vec1 = new BasicVector(disassortativityMatrix.columns());
		for (int i = 0; i < vec1.length(); i++) {
			vec1.set(i, (double) 1.0 / vec1.length());
		}
		Vector vec2 = new BasicVector(vec1.length());
		for (int iteration = 0; vec1.subtract(vec2).fold(
				Vectors.mkInfinityNormAccumulator()) > randomWalkPrecisionFactor
				&& iteration < randomWalkIterationBound; iteration++) {

			vec2 = new BasicVector(vec1);
			vec1 = disassortativityMatrix.multiply(vec1);
		}
		/*if (iteration >= randomWalkIterationBound) {
			System.out.println(	"Random walk iteration bound exceeded: iteration "
							+ iteration);
		}*/
		return vec1;
	}

	/**
	 * Calculates the leadership values of all nodes for the random walk phase.
	 * 
	 * @param subgraph The graph containing the nodes.
	 * 
	 * @param disassortativityVector The disassortativity vector calculated
	 * earlier in the random walk phase.
	 * 
	 * @return A vector containing the leadership value of each node in the
	 * entry given by the node index.
	 */
	protected Vector getLeadershipValues(CustomSubgraph subgraph,Vector disassortativityVector) {
		
		Vector leadershipVector = new BasicVector(subgraph.vertexSet().size());
		Set<Node> nodes = subgraph.vertexSet();
		double leadershipValue;
		if (!nodes.isEmpty()) {

			for(Node node : nodes){
				/*
				 * Note: degree normalization is left out since it
				 * does not influence the outcome.
				 */
				leadershipValue = subgraph.degreeOf(node)
						* disassortativityVector.get(getIndex(nodes, node));
				leadershipVector.set(getIndex(nodes, node), leadershipValue);
				
			}

		}
		return leadershipVector;
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
	
	

	public List<Double> eigenvectorcentralityLeadershipCalculation(CustomSubgraph subgraph){
		
		int numVert = subgraph.vertexSet().size();
	    
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
		List<Double> eigenVector = getEigenvector(eigenMatrix);
		List<Double> newEigenVector = new ArrayList<Double>();
		
	    for(int i=0; i<eigenVector.size(); i++){
	    	newEigenVector.add( mapping(eigenVector.get(i), 0, getMax(eigenVector), 0, 1) );
	    }
	    
		//TODO: to normalise or not?
	    return newEigenVector;
	}
	
	/**
	 * Based on
	 * http://www.markhneedham.com/blog/2013/08/05/javajblas-calculating-eigenvector-centrality-of-an-adjacency-matrix/
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
	
	private double mapping(double value, double in_min, double in_max, double out_min, double out_max)
	{
	  return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	private double getMax(List<Double> list){
	    Double max = Double.MIN_VALUE;
	    for(int i=0; i<list.size(); i++){
	        if(list.get(i) > max){
	            max = list.get(i);
	        }
	    }
	    return max;
	}


}
