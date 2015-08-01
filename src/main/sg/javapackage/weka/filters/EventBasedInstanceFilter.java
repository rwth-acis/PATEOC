package main.sg.javapackage.weka.filters;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class EventBasedInstanceFilter {
	
	public EventBasedInstanceFilter() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Selects the attributes based on the event being 
	 * modeled - survive, merge, split, dissolve
	 * @param data
	 * @param i - integer corresponding to event
	 * 			1-survive
	 * 			2-merge
	 * 			3-split
	 * 			4-dissove
	 * 
	 * @return updated instances
	 * @throws Exception
	 */
	public static Instances evolutionFilter(Instances data, int i) throws Exception{
		
		int n_attributes = data.numAttributes();
		String options=null;
		Remove remove =  new Remove();
		
		if(i == 1){
			System.out.println("For SURVIVE :-");
			options = ""+(n_attributes-2)+","+(n_attributes-1)+","+(n_attributes);
			remove.setAttributeIndices(options);
			//remove.setAttributeIndices("30,31,32");
		}
		else if(i == 2){
			System.out.println("For MERGE :-");
			options = ""+(n_attributes-3)+","+(n_attributes-1)+","+(n_attributes);
			remove.setAttributeIndices(options);
			//remove.setAttributeIndices("29,31,32");
		}
		else if(i == 3){
			System.out.println("For SPLIT :-");
			options = ""+(n_attributes-3)+","+(n_attributes-2)+","+(n_attributes);
			remove.setAttributeIndices(options);
			//remove.setAttributeIndices("29,30,32");
		}
		else if(i == 4){
			System.out.println("For DISSOLVE :-");
			options = ""+(n_attributes-3)+","+(n_attributes-2)+","+(n_attributes-1);
			remove.setAttributeIndices(options);
			//remove.setAttributeIndices("29,30,31");
		}

		remove.setInputFormat(data);
		Instances updated_traningset = Filter.useFilter(data, remove);
		return updated_traningset;
	}

}
