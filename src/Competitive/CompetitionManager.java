package Competitive;

import General.NeuralNetwork;
import General.SpecialNetManager;

public interface CompetitionManager extends SpecialNetManager{
	//used for any setup before each competition
	void setupCompetition();
	boolean getGameOver();
	void setEndCompetitionState();
	boolean isTurn(NeuralNetwork nn);
}
