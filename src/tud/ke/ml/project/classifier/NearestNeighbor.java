package tud.ke.ml.project.classifier;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
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
		throw new NotImplementedException();
	}

	@Override
	protected Map<Object, Double> getWeightedVotes(List<Pair<List<Object>, Double>> subset) {
		throw new NotImplementedException();
	}

	@Override
	protected Object getWinner(Map<Object, Double> votes) {
		throw new NotImplementedException();
	}

	@Override
	protected Object vote(List<Pair<List<Object>, Double>> subset) {
		throw new NotImplementedException();
	}

	@Override
	protected List<Pair<List<Object>, Double>> getNearest(List<Object> data) {
		
		LinkedList<Pair<List<Object>, Double>> pairs =
				new LinkedList< Pair< List<Object> , Double >>();
		
		//======================================================================
		//Calculate Distance of every model instance with parameter instance "data"
		//======================================================================
		for(List<Object> instance :m_data) {
			double distance = determineManhattanDistance(instance, data);
			
			Pair<List<Object>, Double> pair = 
					new Pair< List<Object>, Double>(instance, distance);
			
			pairs.add(pair);
		}
		//======================================================================
		//Sort List and get first 5 instances (lowest distance)
		//======================================================================
		//Collections.sort(pairs, Comparator.comparing(p -> -p.getRight()));
		
		throw new NotImplementedException();
	}

	@Override
	protected double determineManhattanDistance(List<Object> instance1, List<Object> instance2) {
		throw new NotImplementedException();
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
