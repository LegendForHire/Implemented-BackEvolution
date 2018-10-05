package Competitive;

import General.NeuralNetwork;
import General.SpecialNetManager;

public interface CompetitionManager extends SpecialNetManager{
	//used for any setup before each competition
	public void setupCompetition();
	public boolean getGameOver();
	public void setEndCompetitionState();
	public boolean isTurn(NeuralNetwork nn);
}
