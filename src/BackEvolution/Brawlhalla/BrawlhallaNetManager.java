package BackEvolution.Brawlhalla;

import java.util.ArrayList;

import Backpropagate.BackpropagateManager;
import Competitive.CompetitionManager;
import Evolve.EvolveManager;
import General.Layer;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;

public class BrawlhallaNetManager implements BackpropagateManager,EvolveManager,CompetitionManager {
	private BrawlhallaSingleton s = BrawlhallaSingleton.getInstance();
	private ArrayList<String> possibleActions;
	@Override
	public String saveInput(Neuron n){
		return getMethodName(Integer.parseInt(n.getMethod()));
	}
	public String savOutput(Neuron n){
		return n.getMethod();
	}
	private String getMethodName(int i) {
		String name = "";
		if(i%2 == 0 && !(i > s.getWeapons().size()*4+s.getLegends().size()*2+5) ) {
			name += "Player1";
		}
		else if(!(i > s.getWeapons().size()*4+s.getLegends().size()*2+5)) {
			name += "Player2";
		}
		else {
			name += "LevelData";
		}
		if(i/2 == 0) {
			name += "PlayerX";
		}
		else if(i/2 == 1) {
			name += "PlayerY";
		}
		else if(i/2 == 2) {
			name += "PlayerJumps";
		}
		else if(i/2 <  s.getWeapons().size()+3) {
			name += s.getWeapons().get(i-3).getName();
		}
		else if(i/2 <  s.getWeapons().size()*2+3) {
			name += s.getWeapons().get(i-s.getWeapons().size()-3).getName();
		}
		else if(i/2 <  s.getWeapons().size()*2+s.getLegends().size()+3) {
			name += s.getWeapons().get(i-s.getWeapons().size()*2-3).getName();
		}
		else {
			name += s.getStages().get(i-s.getWeapons().size()*4+s.getLegends().size()*2+5).getName();
		}
		return name;
	}
	@Override
	public void BackpropagationSetup() {
		s.setLastState();
	}

	@Override
	public void setup() {	
		int competing = Integer.parseInt(PropertyReader.getProperty("competing"));
		Controller[] controllers = new Controller[competing];
		for(int i = 0; i < competing ;i++){
			controllers[i] = new Controller();
		}
		s.setGame(new Game(controllers));
		s.setLegends();
		s.setWeapons();
		s.setStages();
		possibleActions = new ArrayList<String>();
		String[] outputs1 = {"PressA","PressB","PressX","PressY","ReleaseA","ReleaseB","ReleaseX","ReleaseY"};
		String[] outputs2 = {"PressUp","PressDown","PressLeft","PressRight","ReleaseUp","ReleaseDown","ReleaseLeft","ReleaseRight"};
		String[] outputs3 ={"PressRB","PressLB","PressRT","PressLT","ReleaseRB","ReleaseLB","ReleaseRT","ReleaseLT"};
		for (String o1 : outputs1) {
			possibleActions.add(o1);
			for(String o2: outputs2) {
				possibleActions.add(o1+"_"+o2);
				for(String o3: outputs3) {
					possibleActions.add(o1+"_"+o2+"_"+o3);
					
				}
			}
		}
		for(String o2: outputs2) {
			possibleActions.add(o2);
			for(String o3: outputs3) {
				possibleActions.add(o2+"_"+o3);
				
			}
		}
		for(String o3: outputs3) {
			possibleActions.add(o3);
			
		}
		possibleActions.add("");	
	}

	@Override
	public void BackIterationHandling() {		
		s.setLastState();
	}

	@Override
	public void EvolveSetup() {
		for(NeuralNetwork  nn: s.getNetworks()) {
			BrawlhallaNetwork bn = (BrawlhallaNetwork) nn;
			bn.resetWins();
		}
		
	}

	@Override
	public void EvolveTeardown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAct() {
		for(int i: s.getCurrentPlayers()) {
			BrawlhallaNetwork bn = (BrawlhallaNetwork) s.getNetworks()[i];
			double[][] lastState = s.getLastState();
			double[][] currentState = s.getGame().getState();
			String lastAction = s.getLastAction();
			String bestAction = lastAction;
			double bestValue = getStateValue(currentState, bn);
			for(String action: possibleActions) {
				double[][] actionState = getStateForAction(action,lastState,bn);
				if(getStateValue(actionState, bn)>bestValue) {
					bestAction = action;
					bestValue = getStateValue(actionState,bn);
				}
			}
			for(Layer l: bn.getLayers()) {
				for(Neuron n: l.getNeurons()) {
					if(bestAction.contains(n.getMethod()))n.setActive(true);
					else n.setActive(false);
				}
			}
		}
		
	}

	private double[][] getStateForAction(String action, double[][] lastState, BrawlhallaNetwork bn) {
		// TODO Auto-generated method stub
		return null;
	}

	private double getStateValue(double[][] currentState, BrawlhallaNetwork bn) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setupCompetition() {
		int[] currentPlayers = s.getCurrentPlayers();
		NeuralNetwork[] nns = s.getNetworks();
		for(int i = 0; i<Integer.parseInt(PropertyReader.getProperty("competing"));i++) {
			BrawlhallaNetwork nn = (BrawlhallaNetwork) nns[currentPlayers[i]];
			nn.setController(s.getControllers()[i]);
		}
	}
	@Override
	public boolean getGameOver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTurn(NeuralNetwork nn) {
		// simultaneous play so always true;
		return true;
	}

	@Override
	public void setEndCompetitionState(){
		int[] currentPlayers = s.getCurrentPlayers();
		BrawlhallaNetwork nn1 = (BrawlhallaNetwork) s.getNetworks()[currentPlayers[0]];
		BrawlhallaNetwork nn2 = (BrawlhallaNetwork) s.getNetworks()[currentPlayers[0]];
		if(isWinner(nn1))nn1.addWin();
		if(isWinner(nn2))nn2.addWin();
	}

	private boolean isWinner(BrawlhallaNetwork nn1) {
		// TODO Auto-generated method stub
		return false;
	}

}
