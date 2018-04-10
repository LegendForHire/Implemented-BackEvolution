package BackEvolution.Chess;

import java.io.PrintWriter;

import Backpropagate.BackpropagateSingleton;
import Competitive.CompetitionSingleton;
import Evolve.EvolveSingleton;
import General.NeuralNetwork;

public class ChessSingleton implements EvolveSingleton, BackpropagateSingleton, CompetitionSingleton {
	private static ChessSingleton uniqueInstance = new ChessSingleton();
	private String[] board;

	private ChessSingleton() {
		
	}
	@Override
	public PrintWriter getWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTiming() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWriter(PrintWriter w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworks(NeuralNetwork[] nns) {
		// TODO Auto-generated method stub

	}

	@Override
	public NeuralNetwork[] getNetworks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getActivation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incrementGen() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumNetworks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numCompeting() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLearningType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getCurrentPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentPlayers(int[] players) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getLearningRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMomentum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAllowedError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalGlobalError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTotalGlobalError(double totalGlobalError) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getAdjustProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRandomProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDisableProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNewGeneProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getExistingLayerProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static ChessSingleton getInstance() {
		// TODO Auto-generated method stub
		return uniqueInstance;
	}
	public void startBoard(){
		String [] boardInit = {"R1","K1","B1","Q","K","B2","K2","R2","P1","P2","P3","P4","P5","P6","P7","P8","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","p1","p2","p3","p4","p5","p6","p7","p8","r1","k1","b1","q","k","b2","k2","r2"};
		board = boardInit;
	}
	public String[] getChessboard() {
		return board;
	}
	public void setChessboard(String[] board) {
		this.board = board;
	}

}
