package BackEvolution.Chess;

import java.io.PrintWriter;
import java.util.ArrayList;

import Backpropagate.BackpropagateSingleton;
import Competitive.CompetitionSingleton;
import Evolve.EvolveSingleton;
import General.NeuralNetwork;

public class ChessSingleton implements EvolveSingleton, BackpropagateSingleton, CompetitionSingleton {
	private static ChessSingleton uniqueInstance = new ChessSingleton();
	private String[] board;
	private ArrayList<String> legalMoves;

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
	public void checkLegal() {
		legalMoves = new ArrayList<String>();
		for(int i=0; i<64;i++) {
			if(board[i].contains("R")) {
				for(int j = 0; j<7; j++) {
					if(i+j*8+8 > 63) break;
					if(board[i+j*8+8].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i+j*8+8].toLowerCase().equals(board[i+j*8+8])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 7; j<14; j++) {
					if(i+j-6 > 63 || (i+j-6)%8<i%8) break;
					if(board[i+j-6].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i+j-6].toLowerCase().equals(board[i+j-6])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 14; j<21; j++) {
					if(i-((j-13)*8) <  0) break;
					if(board[i-((j-13)*8)].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i-((j-13)*8)].toLowerCase().equals(board[i+(j-13)*8])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 21; j<28; j++) {
					if(i-j+20 < 0 || (i-j+20)%8>i%8) break;
					if(board[i+j-20].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i-j+20].toLowerCase().equals(board[i-j+20])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
			}
			if(board[i].contains("r")) {
				for(int j = 0; j<7; j++) {
					if(i-j*8-8 <0) break;
					if(board[i-j*8-8].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i-j*8-8].toUpperCase().equals(board[i-j*8-8])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 7; j<14; j++) {
					if(i-j+6 < 0 || (i-j+6)%8>i%8) break;
					if(board[i-j+6].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i-j+6].toUpperCase().equals(board[i-j+6])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 14; j<21; j++) {
					if(i+((j-13)*8) > 63) break;
					if(board[i+((j-13)*8)].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i+((j-13)*8)].toUpperCase().equals(board[i+((j-13)*8)])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 21; j<28; j++) {
					if(i+j-20 < 0 || (i+j-20)%8<i%8) break;
					if(board[i+j-20].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[i+j-20].toUpperCase().equals(board[i+j-20])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
			}
			if(board[i].contains("B")) {
				for(int j = 0; j<7;j++){
					int location = i+j*7+7;
					if(location > 63 || location%8>i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 7; j<14;j++){
					int location = i+(j-6)*9;
					if(location > 63 || location%8<i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 14; j<21;j++){
					int location = i-(j-13)*7;
					if(location < 0 || location%8>i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 21; j<28;j++){
					int location = i-(j-21)*9;
					if(location < 0 || location%8<i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
			}
			if(board[i].contains("b")){
				for(int j = 0; j<7;j++){
					int location = i-j*7-7;
					if(location < 0 || location%8>i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 7; j<14;j++){
					int location = i-(j-6)*9;
					if(location < 0 || location%8<i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 14; j<21;j++){
					int location = i+(j-13)*7;
					if(location > 63 || location%8>i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				for(int j = 21; j<28;j++){
					int location = i+(j-21)*9;
					if(location > 63 || location%8<i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
			}
		}
	}
}
