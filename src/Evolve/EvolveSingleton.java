package Evolve;

import General.Singleton;

public interface EvolveSingleton extends Singleton {	
	public double getAdjustProbability();
	public double getRandomProbability();
	public double getDisableProbability();
	public double getNewGeneProbability();
	public double getExistingLayerProbability();
}
