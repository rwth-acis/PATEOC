package main.sg.javapackage.utils;

import java.util.List;

/**
 * 
 * @author Stephen
 * Statistical computation such as 
 * Mean, Variance and Standard-Deviation
 *
 */
public class StatisticManager {
	
	public StatisticManager() {
		// TODO Auto-generated constructor stub
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
        return sum/(double) dataVector.size();
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
        return var_sum/(double) dataVector.size();
    }
    
    /**
     * returns standard deviation of input list
     * 
     * @param dataVector - list of values
     * @return double
     */

    public static double getStdDev(List<Double> dataVector)
    {
        return Math.sqrt(getVariance(dataVector));
    }

}
