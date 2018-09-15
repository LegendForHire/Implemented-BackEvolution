package Backpropagate;

import General.Singleton;

public interface BackpropagateSingleton extends Singleton{
	public double getTotalGlobalError();
	public void setTotalGlobalError(double totalGlobalError);
}
