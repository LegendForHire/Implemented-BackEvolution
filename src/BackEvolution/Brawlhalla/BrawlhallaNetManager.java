package BackEvolution.Brawlhalla;

import Backpropagate.BackpropagateManager;
import Competitive.CompetitionManager;
import Evolve.EvolveManager;
import General.NeuralNetwork;

public class BrawlhallaNetManager implements BackpropagateManager,EvolveManager,CompetitionManager {
	private BrawlhallaSingleton s = BrawlhallaSingleton.getInstance();
	@Override
	public void BackpropagationSetup() {
		s.setLastState();
	}

	@Override
	public void setup() {		
		Controller[] controllers = new Controller[s.numCompeting()];
		for(int i = 0; i < s.numCompeting();i++){
			controllers[i] = new Controller();
		}
		s.setGame(new Game(controllers));
	}

	@Override
	public void BackIterationHandling() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void EvolveSetup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void EvolveTeardown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAct() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupCompetition(int[] currentPlayers) {
		NeuralNetwork[] nns = s.getNetworks();
		for(int i = 0; i<s.numCompeting();i++) {
			BrawlhallaNetwork nn = (BrawlhallaNetwork) nns[currentPlayers[i]];
			nn.setController(s.getControllers()[i]);
		}
	}
	@Override
	public boolean getGameOver() {
		// TODO Auto-generated method stub
		return false;
	}

}
