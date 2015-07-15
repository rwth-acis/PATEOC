package main.sg.javapackage.parser;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMParser {
	
	String xmlstring;

    public DOMParser(String xmlstring) {
    	this.xmlstring = xmlstring;
    }

    /**
     * 
     * @param nodename
     *            Tagname to parse
     * @return A nodelist found by the parser for the given node name
     */
    public NodeList getNodes(String nodename) {
    	
		if (xmlstring == null)
		    return null;
	
		DocumentBuilder db;
		try {
		    db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(xmlstring));
		    Document doc = db.parse(is);
		    
		    NodeList nlList= doc.getElementsByTagName(nodename);
		    org.w3c.dom.Node nValue = (org.w3c.dom.Node) nlList.item(0);
		    if(nValue!=null)
		    	return doc.getElementsByTagName(nodename);
		    else
		    	return null;
		} catch (ParserConfigurationException e2) {
		    e2.printStackTrace();
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
			return null;
    }

}
