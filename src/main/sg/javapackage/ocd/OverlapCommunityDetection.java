package main.sg.javapackage.ocd;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.sg.javapackage.domain.Community;
import main.sg.javapackage.domain.GlobalVariables;
import main.sg.javapackage.domain.GlobalVariables.Algorithm;
import main.sg.javapackage.domain.Node;
import main.sg.javapackage.logging.Logger;
import main.sg.javapackage.parser.OCDWebServiceCoverWrapper;
import main.sg.javapackage.parser.FOCSCoverWrapper;
import main.sg.javapackage.parser.DOMParser;
import main.sg.javapackage.utils.LocalFileManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

/**
 * @author Stephen
 * based on the similar application from sathvik
 * 
 * https://github.com/rwth-acis/Expert-Recommender-Service
 * 
 */
public class OverlapCommunityDetection {
	//Declarations
	public static Map<Integer, Community[]> Communities = new HashMap<Integer, Community[]>(); 	//Cover list stored as a MAP
    private String graphId;
    private String coverId;
    private static Algorithm algo;
	private static int totalGraphs;
    private boolean isWeighted = true;
    //private static final String BASE_PATH = "http://localhost:8080/ocd/"; //137.226.232.16:7070
    private static final String BASE_PATH = "http://137.226.232.16:7070/ocd/";
    /*
     * inputFormat=WEIGHTED_EDGE_LIST || inputFormat=UNWEIGHTED_EDGE_LIST
     */
    private static final String UPLOAD_URL_FORWEIGHTED = BASE_PATH + "graphs?name=%s&inputFormat=WEIGHTED_EDGE_LIST&doMakeUndirected=true";
    private static final String UPLOAD_URL_FORUNWEIGHTED = BASE_PATH + "graphs?name=%s&inputFormat=UNWEIGHTED_EDGE_LIST&doMakeUndirected=true";

        
    /*
     * outputFormat=META_XML || outputFormat=DEFAULT_XML || outputFormat=LABELED_MEMBERSHIP_MATRIX
     */
    private static final String GET_COVERS_URL = BASE_PATH + "covers/%s/graphs/%s?outputFormat=LABELED_MEMBERSHIP_MATRIX";

    /*
     * algorithm=RANDOM_WALK_LABEL_PROPAGATION_ALGORITHM || algorithm=SPEAKER_LISTENER_LABEL_PROPAGATION_ALGORITHM ||
     * algorithm=EXTENDED_SPEAKER_LISTENER_LABEL_PROPAGATION_ALGORITHM || algorithm=SSK_ALGORITHM ||
     * algorithm=LINK_COMMUNITIES_ALGORITHM || algorithm=WEIGHTED_LINK_COMMUNITIES_ALGORITHM || algorithm=CLIZZ_ALGORITHM ||
     * algorithm=MERGING_OF_OVERLAPPING_COMMUNITIES_ALGORITHM || algorithm=BINARY_SEARCH_RANDOM_WALK_LABEL_PROPAGATION_ALGORITHM
     */
    private static final String IDENTIFY_COVERS_URL = BASE_PATH + "covers/graphs/%s/algorithms?algorithm=%s";
    
    private static final String ALGORITHM_LABEL_1 = "SPEAKER_LISTENER_LABEL_PROPAGATION_ALGORITHM";
    private static final String ALGORITHM_LABEL_2 = "RANDOM_WALK_LABEL_PROPAGATION_ALGORITHM";
    //private static final String ALGORITHM_LABEL_3 = "LINK_COMMUNITIES_ALGORITHM";

	//Constructor
    public OverlapCommunityDetection(Algorithm selectedAlgo, boolean isWeighted, int totalGraphs) {
    	
    	OverlapCommunityDetection.algo = selectedAlgo;
    	this.isWeighted = isWeighted;
    	OverlapCommunityDetection.totalGraphs = totalGraphs;
    }
    
    /**
     * function that performs community detection on 
     * the input graphs based on the selected OCD algorithm
     * 
     * @param inputGraphPath - path to input graphs 
     * 
     * @param DOCACoverPath - path to DOCA covers manually generated incase DOCA selected
     * 
     */
    public void performOverlapCommunityDectection(String inputGraphPath, String DOCACoverPath){
    	
    	int timestep=1;
    	if(algo == Algorithm.focs){
    		//Parse directly the offline generated communities from FOCS
    		FOCSCoverWrapper doca = new FOCSCoverWrapper(DOCACoverPath);
    		doca.parseDOCACommunities();
    	}
    	
    	else if (algo == Algorithm.slpa || algo == Algorithm.dmid ){
    		
    		//Perform OCD from web service
        	while(timestep <= totalGraphs){
    		    System.out.println("Processing Graph: "+timestep);
        		Logger.writeToLogln("Processing Graph: "+timestep);        		
    		    String graphContentAsString = LocalFileManager.getGraphAsString(formulateInputPath(inputGraphPath, timestep)).toString();
        		String covers = getCovers("InGraph_"+timestep,algo,graphContentAsString);

        		//Parse the generated communities from SLPA/DMID
        		OCDWebServiceCoverWrapper parser = new OCDWebServiceCoverWrapper();
        		parser.parseCommunityMatrix(covers, timestep);
        		timestep++;
        	}
    	}
    }
    
    /**
     * supporting functions to store/retrieve/perform
     * actions on the list of Communities stored as a MAP
     * 
     */
    public static long numOfCommunities(int timestep){
    	return OverlapCommunityDetection.Communities.get(timestep)[0].getId();
    }
    
    public static Community[] getTimestepCommunities(int timestep){
    	return OverlapCommunityDetection.Communities.get(timestep);
    }
    public static Community getParticularCommunity(int timestep,int j_commnumber){
    	return OverlapCommunityDetection.Communities.get(timestep)[j_commnumber];
    }
    
    public static List<Node> getCommunityNodes(int timestep,int community){
    	return OverlapCommunityDetection.Communities.get(timestep)[community].getNodeList();
    }
    public static int sizeOfCommunity(int timestep, int community){
    	return OverlapCommunityDetection.Communities.get(timestep)[community].getNodeList().size();
    }
    
    /**
	 * formulates the path to input file 
	 * for each timestep file by suffixing
	 * timestep to the basepath - "file_i"
	 *  
	 * @param basepath from constructor
	 * @param timestep
	 * 
	 * @return String
	 */
    private String formulateInputPath(String BasePath, int number){
		String inputPath;
		inputPath = BasePath.substring(0,BasePath.length()-5);
		return (System.getProperty("user.dir").concat("\\"+inputPath + number + ".txt"));
	}

    /**
     * returns username password for REST-OCD-Service
     * 
     * @return String 
     */
    private String getBasicAuthEncodedString() {
		//byte[] encoding = Base64.encodeBase64("anonymous:anonymous".getBytes());
    	byte[] encoding = Base64.encodeBase64("User:user".getBytes());
		return new String(encoding);
    }

    /**
     * uploads the graph to the web service
     *
     * @param graphName
     *            String value that will be used to save the graph on the
     *            server.
     * @param graphContent
     *            String containing details about the graph, generally an xml
     *            string.
     * @return boolean - status code
     */
    private boolean uploadGraph(String graphName, String graphContent) {
    	
    	HttpPost httppost = null;
		try{
			if(this.isWeighted)
			    httppost = new HttpPost(String.format(UPLOAD_URL_FORWEIGHTED, graphName));
			else
			    httppost = new HttpPost(String.format(UPLOAD_URL_FORUNWEIGHTED, graphName));

		    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());
	
		    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		    postParameters.add(new BasicNameValuePair("name", "testing_graph_upload"));
		    httppost.setEntity(new StringEntity(graphContent));
	
		    CloseableHttpClient httpclient = HttpClients.createDefault();
		    CloseableHttpResponse response = httpclient.execute(httppost);
	
		    HttpEntity entity = response.getEntity();
		    String responseString = EntityUtils.toString(entity, "UTF-8");
		
		    int statusCode = response.getStatusLine().getStatusCode();
		    EntityUtils.consume(entity);
	
		    // Parse the XML response to get the id of the graph.
		    if (statusCode == 200 && responseString != null && responseString.length() > 0) {
				DOMParser parser = new DOMParser(responseString);
				NodeList list = parser.getNodes("Error");
				if(list!= null){
			    	System.out.println("Graph upload failed.");
			    	System.out.println("Message from server:" + list.item(0).getTextContent().toString());
			    	return false;
				}
				else{
			    	System.out.println("Graph uploaded successfully with Status code :: "+ statusCode);
					list = parser.getNodes("Id");
					if (list != null) {
					    for (int i = 0; i < list.getLength(); i++) {
						graphId = list.item(i).getTextContent();
					    }
					}
				}
		    }
		    else{
		    	System.out.println("Connection to server failed. Unauthorized.");
		    	return false;
		    }
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return true;
    }

    /**
     * This method executes a OCD service to identify covers in the graph.
     * 
     * Response string of the format.
     * 
     * @param algorithm selected
     * @return Integer - status code
     * 
     */
    private int identifyCovers(Algorithm selectedAlgo) {
	int statusCode = -1;

	try {
		String url;
//		if(selectedAlgo == Algorithm.focs)
//			url = String.format(IDENTIFY_COVERS_URL, graphId, ALGORITHM_LABEL_3);
		if(selectedAlgo == Algorithm.dmid)
			url = String.format(IDENTIFY_COVERS_URL, graphId, ALGORITHM_LABEL_2);
		else 
			url = String.format(IDENTIFY_COVERS_URL, graphId, ALGORITHM_LABEL_1);
		
	    StringEntity strentity = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-16\"?><Parameters></Parameters>");

	    HttpPost httppost = new HttpPost(url);
	    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());
	    httppost.setEntity(strentity);

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);

	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");

	    statusCode = response.getStatusLine().getStatusCode();
	    EntityUtils.consume(entity);
	    
	    /*
	     *  Return format
	     *	<?xml version="1.0" encoding="UTF-16"?> 
	     * <Cover><Id><CoverId>101</CoverId><GraphId>101</GraphId></Id></Cover>
	     */
	    if (statusCode == 200 && responseString != null && responseString.length() > 0) {
	    	System.out.println("Covers generated successfully with Status code :: "+ statusCode);
			DOMParser parser = new DOMParser(responseString);
			NodeList list = parser.getNodes("CoverId");
			if (list != null) {
			    for (int i = 0; i < list.getLength(); i++) {
				coverId = list.item(i).getTextContent();
			    }
			}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return statusCode;
    }
    
    /**
     * retrieves the covers for the uploaded
     * graph from the web service
     * 
     * @return String - content of the cover as string
     */
    public String retrieveCover(){
    	
		String url = String.format(GET_COVERS_URL, coverId, graphId);
		String responseString = null;
		try {
	
		    HttpGet httpget = new HttpGet(url);
		    httpget.setHeader("Authorization", "Basic " + getBasicAuthEncodedString());
	
		    CloseableHttpClient httpclient = HttpClients.createDefault();
		    CloseableHttpResponse response = httpclient.execute(httpget);
		
		    
		    HttpEntity entity = response.getEntity();
		    responseString = EntityUtils.toString(entity, "UTF-8");
		    
		    int statusCode = response.getStatusLine().getStatusCode();
	    	System.out.println("Cover downloaded with Status code :: "+ statusCode);

		    EntityUtils.consume(entity);
	
		} catch (Exception e) {
		    e.printStackTrace();
		}
    	
    	return responseString;
    }
    
    /**
     * function to manage uploading and retrieving 
     * of covers from the webservice
     * 
     * @param graphName - upload graphname
     * @param selectedAlgo - OCD algorithm
     * @param graphContent - content of the graph as string
     * 
     * @return String - content of the cover as string
     */
    public String getCovers(String graphName, Algorithm selectedAlgo, String graphContent) {
		String responseString=null;
    	if(uploadGraph(graphName, graphContent)){
    		identifyCovers(selectedAlgo);
    		//Thread wait to allow algorithm to complete execution
    		try {
    			Thread.sleep(GlobalVariables.ocdWebServiceSleepTime);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	    responseString = retrieveCover();
    		System.out.println("GraphId = " + graphId + " CoverId = " + coverId);
    		Logger.writeToLogln("Processed "+ graphName +" with graphID :"+ graphId + " and coverID " + coverId);
    	}
    	else{
    		System.out.println("Covers not generated. Exiting.");
    		System.exit(2);
    	}
		
		return responseString;
    }

}