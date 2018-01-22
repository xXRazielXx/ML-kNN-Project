package tud.ke.ml.project.classifier;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tud.ke.ml.project.util.Pair;

/**
 * This implementation assumes the class attribute is always available (but probably not set).
 * 
 */
public class NearestNeighbor extends INearestNeighbor implements Serializable {
	private static final long serialVersionUID = 1L;

	protected double[] scaling;
	protected double[] translation;
	
	List<List<Object>> m_data;

	@Override
	public String getMatrikelNumbers() {
		return new String("2994693,1678266,2568919");
	}

	@Override
	protected void learnModel(List<List<Object>> data) {
		m_data = new LinkedList<List<Object>>();
		for(List<Object> i : data) {
			m_data.add(new LinkedList<Object>(i));
		}
	}

	@Override
	protected Map<Object, Double> getUnweightedVotes(List<Pair<List<Object>, Double>> subset) {

		Map<Object, Double> unweightedVotes = new HashMap<Object, Double>();
		
		for(Pair<List<Object>,Double> subsetInstance : subset) {
			Object instanceClass = subsetInstance.getA().get(subsetInstance.getA().size()); 
			
			if(!unweightedVotes.containsKey(instanceClass))
					unweightedVotes.put(instanceClass, 0.0);
			
			unweightedVotes.put(instanceClass, unweightedVotes.get(instanceClass) + 1);
		}
		
		return unweightedVotes;
	}

	@Override
	protected Map<Object, Double> getWeightedVotes(List<Pair<List<Object>, Double>> subset) {
		Map<Object, Double> weightedVotes = new HashMap<Object, Double>();
		
		for(Pair<List<Object>,Double> subsetInstance : subset) {
			Object instanceClass = subsetInstance.getA().get(subsetInstance.getA().size()); 
			
			if(!weightedVotes.containsKey(instanceClass))
					weightedVotes.put(instanceClass, 0.0);
			
			weightedVotes.put(instanceClass, weightedVotes.get(instanceClass) + 1/subsetInstance.getB());
		}
		
		return weightedVotes;
	}

	@Override
	protected Object getWinner(Map<Object, Double> votes) {
		//======================================================================
		//return winner class
		//======================================================================
		double maxCount = 0.0;    //Count of winner class
		Object winnerClass = null;//Class with highest count
		
		for (Map.Entry<Object, Double> entry : votes.entrySet())
		{
		    if(entry.getValue() > maxCount) {
		    	maxCount = entry.getValue();
		    	winnerClass = entry.getKey();
		    }
		}
		return winnerClass;
	}

	@Override
	protected Object vote(List<Pair<List<Object>, Double>> subset) {
		
		if(isInverseWeighting()) return getWinner(getWeightedVotes(subset));
		else return getWinner(getUnweightedVotes(subset));
	}

	@Override
	protected List<Pair<List<Object>, Double>> getNearest(List<Object> data) {
		
		LinkedList<Pair<List<Object>, Double>> pairs =
				new LinkedList< Pair< List<Object> , Double >>();
		
		//======================================================================
		//Calculate Distance of every model instance with parameter instance "data"
		//======================================================================
		for(List<Object> instance : m_data) {
			
			double distance;
			
			if(getMetric() == 0) distance = determineEuclideanDistance(instance, data);
			else distance = determineManhattanDistance(instance, data);
			
			Pair<List<Object>, Double> pair = 
					new Pair< List<Object>, Double>(instance, distance);
			
			pairs.add(pair);
		}
		//======================================================================
		//Sort List and get first 5 instances (lowest distance)
		//======================================================================
		Collections.sort(pairs, 
			new Comparator<Pair<List<Object>, Double>>() {
				@Override
				public int compare(Pair<List<Object>, Double> o1, Pair<List<Object>, Double> o2) {
					//For simplicity -> distance values are read to compare them
					double val1, val2;
					val1 = ((Pair<List<Object>, Double>)o2).getB();
					val2 = ((Pair<List<Object>, Double>)o2).getB();
					return  val1 == val2 ? 0 : val1 < val2 ? -1 : 1; //sort in descending order
				}
			});
		
		LinkedList<Pair<List<Object>, Double>> bestFive =
				new LinkedList< Pair< List<Object> , Double >>();
		
		//Check if size of Pairs is less than 5 (neccesary? discuss!! :D)
		int bestN = 5;
		if(pairs.size() < 5) bestN = pairs.size(); 
		
		for(int i = 0; i < bestN; i++) {
			bestFive.add(pairs.get(i));
		}
		
		return bestFive;
	}

	@Override
	protected double determineManhattanDistance(List<Object> instance1, List<Object> instance2) {
		double d = 0;
		if (instance1.size() != instance2.size()) {
			throw new IllegalArgumentException("number of instances does not match");
		 }
		 
		for (int i = 0; i < instance2.size(); i++) {
			if (instance1.get(i) instanceof String && instance2.get(i) instanceof String) {
				if (((String)instance1.get(i)).equals((String)instance2.get(i))) d = 0;
				else d = 1;
			}
			else if (instance1.get(i) instanceof Double && instance2.get(i) instanceof Double) {
				d = Math.abs((Double) instance1.get(i) - (Double) instance2.get(i));
			}
			else throw new IllegalArgumentException("class of attributes does not match");
		}

		return d;
	}

	@Override
	protected double determineEuclideanDistance(List<Object> instance1, List<Object> instance2) {
		throw new NotImplementedException();
	}

	@Override
	protected double[][] normalizationScaling() {
		throw new NotImplementedException();
	}

}
