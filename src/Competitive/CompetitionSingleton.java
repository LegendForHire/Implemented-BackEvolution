package Competitive;

import General.Singleton;

public interface CompetitionSingleton extends Singleton {
	public int[] getCurrentPlayers();
	public void setCurrentPlayers(int[] players);
}
