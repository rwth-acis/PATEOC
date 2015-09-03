package main.sg.javapackage.weka.filters;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Remove;


/**
 * Evaluation segregation of input attribute set
 * into 3 problem classes:
 * -1)Intra Features = Community and Leader features
 * -2)Inter Features = Community, Leader and Temporal features
 * -3)Inter* Features = MostImporatant(Community, Leader and Temporal features) 
 * 						using WEKA Wrapper method
 * @author Stephen
 *
 */
public class AttributeSelectionFilter {
	
	public AttributeSelectionFilter() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * returns only the community features from 
	 * the main training data
	 * 
	 * @return Instances
	 * @throws Exception
	 */
	public static Instances performAttributeSelection1(Instances data) throws Exception{
		
		//Only community attributes featuring from 1-12, and last nominal attribute
		Remove remove =  new Remove();
		remove.setAttributeIndices("1-12,last");
		remove.setInvertSelection(true);
		remove.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, remove);
		return updated_traningset;

	}
	
	/**
	 * returns all features : community features +
	 * leadership features + temporal features from
	 * the main training data
	 * 
	 * @return Instances
	 * @throws Exception
	 */
	public static Instances performAttributeSelection2(Instances data) throws Exception{

		return data;
	}
	
	/**
	 * performs attribute selection using
	 * WrapperFunction / SubsetEvalutions
	 * Bestfirst / GreedyStepwise search
	 * 
	 * @param data
	 * @param model
	 * @return Instances
	 * @throws Exception
	 */
	public static Instances performAttributeSelection3(Instances data, Classifier model) throws Exception{
		
		AttributeSelection attributeselector = new AttributeSelection();
		WrapperSubsetEval wrapper = new WrapperSubsetEval();
		wrapper.setClassifier(model);
		BestFirst searcher = new BestFirst();
		attributeselector.setEvaluator(wrapper);
		attributeselector.setSearch(searcher);
		attributeselector.setInputFormat(data);
		// generate new data
		Instances updated_traningset = Filter.useFilter(data, attributeselector);
		return updated_traningset;
	}
	

}
