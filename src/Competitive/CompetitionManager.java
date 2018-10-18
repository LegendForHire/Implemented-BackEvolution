package Competitive;

import General.DataManager;
import General.MethodManager;
import General.NeuralNetwork;

public abstract class CompetitionManager extends MethodManager{
	//used for any setup before each competition
	public CompetitionManager() {
		super();
	}
	public abstract void setupCompetition(DataManager data);
	public abstract boolean getGameOver(DataManager data);
	public abstract void setEndCompetitionState(DataManager data);
	public abstract boolean isTurn(NeuralNetwork nn, DataManager data);
}
