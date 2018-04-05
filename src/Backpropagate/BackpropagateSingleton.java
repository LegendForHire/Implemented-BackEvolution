package Backpropagate;

import General.Singleton;

public interface BackpropagateSingleton extends Singleton{
	public int getTiming();
	public double getLearningRate();
	public double getMomentum();
	double getAllowedError();
	public double getTotalGlobalError();
	public void setTotalGlobalError(double totalGlobalError);
}
