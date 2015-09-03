package main.sg.javapackage.parser;

import java.util.Scanner;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.graph.PreProcessing;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.ocd.OverlapCommunityDetection;

/**
 * Wrapper method for parsing covers
 * from OCD Webservice
 * 
 * @author Stephen
 *
 */
public class OCDWebServiceCoverWrapper {
	
	public OCDWebServiceCoverWrapper() {
		// TODO Auto-generated constructor stub
	}
	private int countNoOfCommunities(String cover){
		
		String values[] = null;
		Scanner scanner = new Scanner(cover);
		if (scanner.hasNextLine()) {
		    String line = scanner.nextLine();
		    values = line.split("\\s+");
		}
		else {
			System.out.println("Invalid cover received");
		}
		System.out.println("Detected number of communities : " +(values.length-1));
		System.out.println("-----------------------------------------");
		scanner.close();
		return (values.length-1);
		
	}
		    
	public void parseCommunityMatrix(String coverMatrix, int timestep){
		
		Logger.writeToFile("bin\\Cover_"+timestep+".txt", coverMatrix,false);

		int numOfCommunities = countNoOfCommunities(coverMatrix);
		Community[] tempComm = new Community[numOfCommunities+1];
		
		for(int i=1; i<=numOfCommunities; i++ ){
			tempComm[i] = new Community();
			tempComm[i].setId((long)i);
		}
		//Set the 0th Node as header node with long id as number of communities
		tempComm[0] = new Community();
		tempComm[0].setId((long) numOfCommunities); 
		tempComm[0].setHeaderLabel();
		
		try{
			
			Scanner scanner = new Scanner(coverMatrix);
	
			while (scanner.hasNextLine()) {
			    String line = scanner.nextLine();
			    String values[] = line.split("\\s+");
			    
			    if (values != null && values.length > 0) {
					String nodeValue = values[0].toString();
					//New node for each community to hold its own unique values
					Node node = new Node(PreProcessing.masterlistGetNode(nodeValue));
	
					if (values.length > 1) {
					    for (int i = 1; i < values.length; i++) {
						double memValue = Double.parseDouble(values[i]);
							if (memValue != 0) {
								//TODO: Re-check the membership values
								tempComm[i].addNode(node);
							}
					    }
					}
			    }
			}
			scanner.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("Invalid cover detected. Exit Code :5");
			System.exit(5);
		}
		OverlapCommunityDetection.Communities.put(timestep, tempComm);
		Logger.writeToLogln("Total number of communities :" + numOfCommunities);
		
		for(int i=1; i<=numOfCommunities; i++ ){
			Logger.writeToLogln("	Community "+ i + " has "+ tempComm[i].getNodeList().size() + " nodes");

		}
	}

}
