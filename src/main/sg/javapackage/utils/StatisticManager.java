package main.sg.javapackage.utils;

import java.util.List;

/**
 * 
 * @author Stephen
 * support function for statistical computation such as 
 * Mean, Variance and Standard-Deviation
 *
 */
public class StatisticManager {
	
	public StatisticManager() {

	}

	/**
	 * returns the mean of the input list
	 * 
	 * @param dataVector - list of values
	 * @return double
	 */
    public static double getMean(List<Double> dataVector)
    {
        double sum = 0.0;
        for(double data : dataVector){
            sum += data;
        }
        sum = sum/(double) dataVector.size();
        return sum;
    }

    /**
     * returns variance value of input list
     * @param dataVector - list of values
     * @return double
     */
    public static double getVariance(List<Double> dataVector)
    {
        double mean = getMean(dataVector);
        double var_sum = 0;
        for(double data :dataVector)
            var_sum += Math.pow(data-mean, 2);
        var_sum = var_sum/(double) dataVector.size();
        return var_sum;
    }
    
    /**
     * returns standard deviation of input list
     * 
     * @param dataVector - list of values
     * @return double
     */

    public static double getStdDev(List<Double> dataVector)
    {
    	double variance = getVariance(dataVector);	
        return Math.sqrt(variance);
    }

}
