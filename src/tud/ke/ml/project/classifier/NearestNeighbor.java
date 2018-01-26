package tud.ke.ml.project.classifier;

import java.io.Serializable;
import java.util.ArrayList;
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
		m_data = new LinkedList<List<Object>>(data);
	}

	@Override
	protected Map<Object, Double> getUnweightedVotes(List<Pair<List<Object>, Double>> subset) {

		Map<Object, Double> unweightedVotes = new HashMap<Object, Double>();
		
		for(Pair<List<Object>,Double> subsetInstance : subset) {
			Object instanceClass = subsetInstance.getA().get(subsetInstance.getA().size() -1); 
			
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
			Object instanceClass = subsetInstance.getA().get(subsetInstance.getA().size() -1); 
			
			if(!weightedVotes.containsKey(instanceClass))
					weightedVotes.put(instanceClass, 0.0);
			
			weightedVotes.put(instanceClass, weightedVotes.get(instanceClass) +(1/subsetInstance.getB()));
			
		}
		
		return weightedVotes;
	}

	@Override
	protected Object getWinner(Map<Object, Double> votes) {
		//======================================================================
		//return winner class
		//======================================================================
		double maxCount = 0.0;    								//Count of winner class(es)
		List<Object> winnerClasses = new ArrayList<Object>();	//Class(es) with highest count
		
		//======================================================================
		//Search for highest votes in map
		//======================================================================
		for (Map.Entry<Object, Double> entry : votes.entrySet())
		{
			if(entry.getValue() == maxCount) {
		    	winnerClasses.add(entry.getKey());
		    }
			if(entry.getValue() > maxCount) {
		    	maxCount = entry.getValue();
		    	winnerClasses.clear();
		    	winnerClasses.add(entry.getKey());
		    }
		}
		//======================================================================
		//More than one class has highest votes -> decide for class with highest 
		//Occurrence(prior) / else decide for the one class
		//======================================================================
		/*
		if (winnerClasses.size() > 1) {
			Map<Object, Integer> priors = new HashMap<Object, Integer>();
			//======================================================================
			//Put win Candidates in Map
			for (Object winCandidate : winnerClasses) {
				priors.put(winCandidate, 0);
			}
			//=================================================================
			//Iterate through instances and count if class is candidate class
			for (List<Object> instance : this.m_data) {
				Object instanceClass = instance.get(instance.size() -1);	
				if(priors.containsKey(instanceClass))
					priors.put(instanceClass, priors.get(instanceClass) + 1);
			}
			//=================================================================
			//Iterate through map and take class with highest prior
			//re-use variable MaxCount
			Object win = null;
			maxCount = 0.0;
			for (Map.Entry<Object, Integer> entry : priors.entrySet()) {
				if(entry.getValue() > maxCount) {
			    	maxCount = entry.getValue();
			    	win		 = entry.getKey();			    	
			    }
			}
			//Return class with highest prior
			return win;
			
		} 	//else return winner class if only one
		
		else */ return winnerClasses.get(0);
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

		double st[][] = normalizationScaling();
		this.scaling = st[0];
		this.translation = st[1];
		//======================================================================
		//Calculate Distance of every model instance with parameter instance "data"
		//======================================================================
		for(List<Object> instance : m_data) {
			
			double distance;
			
			//Normalizing instance/data - Attributes
			for(int i = 0; i < instance.size(); i++) {
				if(!(data.get(i) instanceof String))
					instance.set(i, ((Double)instance.get(i) + translation[i])*scaling[i]);
				
				if(!(data.get(i) instanceof String))
					data.set(i, ((Double)data.get(i) + translation[i])*scaling[i]);
			}
			
			if(getMetric() == 0) distance = determineManhattanDistance(instance, data);
			else distance = determineEuclideanDistance(instance, data); 
			
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
					val1 = ((Pair<List<Object>, Double>)o1).getB();
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
		double distance = 0;
		//Check if instances match (size)
		if (instance1.size() != instance2.size()) {
			throw new IllegalArgumentException("number of instances does not match");
		 }
		 
		//Check if attribute-types match and cast them to types for calculation
		for (int i = 0; i < instance2.size(); i++) {
			double difference = 0.0;
			//Do not count Class-Attribute
			if(i == this.getClassAttribute()) continue;
			
			if (instance1.get(i) instanceof String && instance2.get(i) instanceof String) {
				if (((String)instance1.get(i)).equals((String)instance2.get(i))) difference = 0;
				else difference = 1;
			}
			else if (instance1.get(i) instanceof Double && instance2.get(i) instanceof Double) {
				difference = Math.abs((Double) instance1.get(i) - (Double) instance2.get(i));
			}
			else throw new IllegalArgumentException("class of attributes does not match");
			
			distance += difference;
		}

		return distance;
	}

	@Override
	protected double determineEuclideanDistance(List<Object> instance1, List<Object> instance2) {
		double attribute_dist = 0.0;
		double euclidean_dist = 0.0;
		if (instance1.size() != instance2.size()) {
			throw new IllegalArgumentException("number of instances does not match");
		 }
		 
		for (int i = 0; i < instance2.size(); i++) {
			//Do not count Class-Attribute
			if(i == this.getClassAttribute()) continue;
			
			if (instance1.get(i) instanceof String && instance2.get(i) instanceof String) {
				if (((String)instance1.get(i)).equals((String)instance2.get(i))) attribute_dist = 0;
				else attribute_dist = 1;
			}
			else if (instance1.get(i) instanceof Double && instance2.get(i) instanceof Double) {
				attribute_dist = Math.abs((Double) instance1.get(i) - (Double) instance2.get(i));
			}
			else throw new IllegalArgumentException("class of attributes does not match");
			
			euclidean_dist += Math.pow(attribute_dist, 2);
		}

		return Math.sqrt(euclidean_dist);
	}

	@Override
	protected double[][] normalizationScaling() {
		double st[][] = new double[m_data.get(0).size()-1][m_data.get(0).size()-1];

		double minValues[] = new double[m_data.get(0).size()-1];
		double maxValues[] = new double[m_data.get(0).size()-1];
		if(isNormalizing()) {
			for(int i = 0; i < m_data.size() - 1; i++) {
				for(int j = 0; j < m_data.get(0).size() - 1; j++) {
					if(!(m_data.get(i).get(j) instanceof String) && i != getClassAttribute()) {
						
						//Test -> minValue < als Value der Instanz
						if((Double)m_data.get(i).get(j) < minValues[j]) {
							double d = (Double)m_data.get(i).get(j);
							minValues[j] = (Double)m_data.get(i).get(j);
						}
						//Test -> maxValue > als Value der Instanz
						if((Double)m_data.get(i).get(j) > maxValues[j]) maxValues[j] = (Double)m_data.get(i).get(j);
					}
				}
			}
		}
		//Fill scaling and translation array
		for(int j = 0; j < m_data.get(0).size() - 1; j++) {
				st[0][j] = 1 / (maxValues[j] - minValues[j]);
				st[1][j] = -1 * minValues[j];
		}
		return st;
	}

}
