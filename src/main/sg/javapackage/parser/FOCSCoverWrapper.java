package main.sg.javapackage.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;

/**
 * Wrapper method for parsing covers
 * from AFOCS Algorithm
 * 
 * @author Stephen
 *
 */
public class FOCSCoverWrapper {
	
	//path to cover files
	private String basePath = null;
	
	//constructor
	public FOCSCoverWrapper(String AFOCSCoverPath) {
		//Path to AFOCS Generated covers
		this.basePath = AFOCSCoverPath;
	}
	
	/**
	 * parse the cover file to convert nodelist into communities
	 */
	public void parseAFOCSCommunities(){
		try {
			readGeneratedCommunities(this.basePath, PreProcessing.totalGraphCount());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * form the recurring file name from the base path 
	 * @param BasePath
	 * @param number
	 * @return
	 */
	private String formulateInputPath(String BasePath, int number){
		String inputPath;
		inputPath = BasePath.substring(0,BasePath.length()-5);
		return (System.getProperty("user.dir").concat("\\"+inputPath + number + ".txt"));
	}
	
	/**
	 * count number of lines (corresponding to communities) in the cover file
	 */
	private int countLines(File filename) throws IOException{
		
		LineNumberReader  lnr = new LineNumberReader(new FileReader(filename));
		lnr.skip(Long.MAX_VALUE);
		int value = lnr.getLineNumber();
		lnr.close();
		return value;
	}

	/**
	 * store the communities into the custom community data structure for further processing
	 * @param BasePath
	 * @param totalTimesteps
	 * @throws IOException
	 */
	private void readGeneratedCommunities(String BasePath,int totalTimesteps) throws IOException {
		int timestep=1, totalCommunityCount =0;
		
		//Tokenize the input
		while(timestep<=totalTimesteps) {

			Logger.writeToLogln("Reading communities of graph "+timestep);
			File inputFile= new File(formulateInputPath(BasePath, timestep));
			
			//count the number of communities
			int communityCount = countLines(inputFile);
			
			//reserve array of communities
			Community[] tempComm = new Community[communityCount+1];
			for(int i=1; i<=communityCount; i++ ){
				tempComm[i] = new Community();
				tempComm[i].setId((long)i);
			}
			
			//0th community is saved as a header holding the communities count for the particualr timestep
			tempComm[0] = new Community();
			tempComm[0].setId((long) communityCount); 
			tempComm[0].setHeaderLabel();

			
			int community=0;
			//read the file
			try (BufferedReader reader = 
					new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
				String communityLine = null;
				while( (communityLine = reader.readLine())!= null ){
					String [] tempNodes = communityLine.split("\\s+");
				    int tempNodeCount = 0;
				    
				    //if there exists a line, means there exists a community
				    community++;
				    while(tempNodeCount<tempNodes.length) {

				    	Node node = new Node( PreProcessing.masterlistGetNode( (tempNodes[tempNodeCount]) ) );
				    	tempComm[community].addNode(node);
				    	tempNodeCount++;
				    }
				}
				//Close buffer
				reader.close();
			} 
			catch (IOException e) {
	            e.printStackTrace();
			}
			
			for(int i=1; i<=communityCount; i++ ){
				Logger.writeToLogln("	Community "+ i + " has "+ tempComm[i].getNodeList().size() + " nodes");

			}
			
			//save the cover in data structure
			OverlapCommunityDetection.Communities.put(timestep, tempComm);
			Logger.writeToLogln("Number of communities :" + communityCount);
			totalCommunityCount += communityCount;
			
			timestep++;
		}
		Logger.writeToLogln("Total number of communities :" + totalCommunityCount);
		
		
	}

}
