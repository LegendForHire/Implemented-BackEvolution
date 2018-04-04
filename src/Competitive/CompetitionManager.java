package Competitive;

import General.NeuralNetwork;

public interface CompetitionManager {
	//used for any setup before each competition
	//@params currentPlayers: provides an array of ints containing the location of the current players in the network array.
	void setupCompetition();
	boolean getGameOver();
	void setEndCompetitionState();
	boolean isTurn(NeuralNetwork nn);
}
