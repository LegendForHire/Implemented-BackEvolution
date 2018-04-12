package BackEvolution.Chess;

import java.io.PrintWriter;
import java.util.ArrayList;
import Backpropagate.BackpropagateSingleton;
import Competitive.CompetitionSingleton;
import Evolve.EvolveSingleton;
import General.NeuralNetwork;

public class ChessSingleton implements EvolveSingleton, BackpropagateSingleton, CompetitionSingleton {
	private static ChessSingleton uniqueInstance = new ChessSingleton();
	private static final int TIMING = 0;
	private static final double ACTIVATION = 0;
	private static final String TYPE = "Chess";
	private static final int NUM_NETWORKS = 200;
	private static final double LEARNING_RATE = .01;
	private static final double MOMENTUM = .25;
	private static final double ALLOWED_ERROR = 250;
	private static final int NUM_COMPETING = 2;
	public static final double  WEIGHT_ADJUST = .65;
	public static final double  RANDOM_WEIGHT = .1+WEIGHT_ADJUST;
	public static final double ENABLE_DISABLE = .05+RANDOM_WEIGHT;
	public static final double  NEW_GENE = ENABLE_DISABLE + .17 ;
	public static final double EXISTING_LAYER = NEW_GENE + .029;
	private double totalGlobalError;
	private PrintWriter writer;
	private NeuralNetwork[] nns;
	private int Gen;
	private static final int LEARNTYPE = 1;
	private String[] chessboard;
	private ArrayList<String> currentLegalMoves;
	private int[] currentPlayers;

	private ChessSingleton() {
		
	}
	@Override
	public PrintWriter getWriter() {
		// TODO Auto-generated method stub
		return writer;
	}

	@Override
	public int getTiming() {
		// TODO Auto-generated method stub
		return TIMING;
	}

	@Override
	public void setWriter(PrintWriter w) {
		// TODO Auto-generated method stub
		writer = w;
	}

	@Override
	public void setNetworks(NeuralNetwork[] nns) {
		this.nns = nns;

	}

	@Override
	public NeuralNetwork[] getNetworks() {
		// TODO Auto-generated method stub
		return nns;
	}

	@Override
	public double getActivation() {
		// TODO Auto-generated method stub
		return ACTIVATION;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	public void incrementGen() {
		Gen++;

	}

	@Override
	public int getGen() {
		// TODO Auto-generated method stub
		return Gen;
	}

	@Override
	public int getNumNetworks() {
		// TODO Auto-generated method stub
		return NUM_NETWORKS;
	}

	@Override
	public int numCompeting() {
		// TODO Auto-generated method stub
		return NUM_COMPETING;
	}

	@Override
	public int getLearningType() {
		// TODO Auto-generated method stub
		return LEARNTYPE;
	}

	@Override
	public int[] getCurrentPlayers() {
		// TODO Auto-generated method stub
		return currentPlayers;
	}

	@Override
	public void setCurrentPlayers(int[] players) {
		currentPlayers = players;

	}

	@Override
	public double getLearningRate() {
		// TODO Auto-generated method stub
		return LEARNING_RATE;
	}

	@Override
	public double getMomentum() {
		// TODO Auto-generated method stub
		return MOMENTUM;
	}

	@Override
	public double getAllowedError() {
		// TODO Auto-generated method stub
		return ALLOWED_ERROR;
	}

	@Override
	public double getTotalGlobalError() {
		// TODO Auto-generated method stub
		return totalGlobalError;
	}

	@Override
	public void setTotalGlobalError(double totalGlobalError) {
		this.totalGlobalError = totalGlobalError;

	}

	@Override
	public double getAdjustProbability() {
		// TODO Auto-generated method stub
		return WEIGHT_ADJUST;
	}

	@Override
	public double getRandomProbability() {
		// TODO Auto-generated method stub
		return RANDOM_WEIGHT;
	}

	@Override
	public double getDisableProbability() {
		// TODO Auto-generated method stub
		return ENABLE_DISABLE;
	}

	@Override
	public double getNewGeneProbability() {
		// TODO Auto-generated method stub
		return NEW_GENE;
	}

	@Override
	public double getExistingLayerProbability() {
		// TODO Auto-generated method stub
		return EXISTING_LAYER;
	}

	public static ChessSingleton getInstance() {
		// TODO Auto-generated method stub
		return uniqueInstance;
	}
	public void startBoard(){
		String [] boardInit = {"R1","K1","B1","Q","K","B2","K2","R2","P1","P2","P3","P4","P5","P6","P7","P8","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","p1","p2","p3","p4","p5","p6","p7","p8","r1","k1","b1","q","k","b2","k2","r2"};
		chessboard = boardInit;
	}
	public String[] getChessboard() {
		return chessboard;
	}
	public void setChessboard(String[] board) {
		this.chessboard = board;
	}
	public void checkLegal() {
		currentLegalMoves = checkLegalForState(chessboard);
		for(String move: currentLegalMoves) {
			if(YourKingInCheck(getStateforMove(move),move.equals(move.toUpperCase())))currentLegalMoves.remove(move); 
		}
	}
	public ArrayList<String> checkLegalForState(String[] board) {
		ArrayList<String> legalMoves = new ArrayList<String>();
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
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
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
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
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
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
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
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
			}
			else if(board[i].contains("Q")) {
				for(int j=0; i<7; j++) {
					int location = i+j*8+8;
					if(location > 63)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=7; i<14; j++) {
					int location = i+(j-6)*9;
					if(location > 63 || location%8 < i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=14; i<21; j++) {
					int location = i+(j-13);
					if(location > 63 || location%8 < i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=21; i<28; j++) {
					int location = i-(j-20)*7;
					if(location < 0 || location%8 < i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=28; i<35; j++) {
					int location = i-(j-27)*8;
					if(location < 0)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=35; i<42; j++) {
					int location = i-(j-34)*9;
					if(location < 0 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=42; i<49; j++) {
					int location = i-(j-41);
					if(location < 0 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=49; i<56; j++) {
					int location = i+(j-48)*7;
					if(location > 63 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
			}
			else if(board[i].contains("q")) {
				for(int j=0; i<7; j++) {
					int location = i-j*8-8;
					if(location < 63)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=7; i<14; j++) {
					int location = i-(j-6)*9;
					if(location < 0 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=14; i<21; j++) {
					int location = i-(j-13);
					if(location < 0 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=21; i<28; j++) {
					int location = i+(j-20)*7;
					if(location > 63 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=28; i<35; j++) {
					int location = i+(j-27)*8;
					if(location > 63)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=35; i<42; j++) {
					int location = i+(j-34)*9;
					if(location > 63 || location%8 < i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=42; i<49; j++) {
					int location = i-(j-41);
					if(location < 0 || location%8 > i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
				for(int j=49; i<56; j++) {
					int location = i-(j-48)*7;
					if(location < 0 || location%8 < i%8)break;
					if(board[location].equals("")) {
						legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toUpperCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}				
				}
			}
			else if(board[i].contains("P")) {
				for (int j=0;i<2;j++) {
					int location = i+j*8+8;
					if(location > 63)break;
					if(board[location].equals("")) {
						if(j==1){
							if (i/8==2)legalMoves.add(board[i]+"_"+j);
						}
						else legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				if(board[i+7]!="" && board[i+7].toLowerCase().equals(board[i+7]))legalMoves.add(board[i]+"_"+3);
				if(board[i+7]!="" && board[i+9].toLowerCase().equals(board[i+9]))legalMoves.add(board[i]+"_"+4);
			}
			else if(board[i].contains("p")) {
				for (int j=0;i<2;j++) {
					int location = i-j*8-8;
					if(location > 63)break;
					if(board[location].equals("")) {
						if(j==1){
							if (i/8==7)legalMoves.add(board[i]+"_"+j);
						}
						else legalMoves.add(board[i]+"_"+j);
					}
					else {
						if (board[location].toLowerCase().equals(board[location])) legalMoves.add(board[i]+"_"+j);
						break;
					}
				}
				if(board[i-9]!="" && board[i-9].toLowerCase().equals(board[i+7]))legalMoves.add(board[i]+"_"+3);
				if(board[i-7]!="" && board[i-7].toLowerCase().equals(board[i+9]))legalMoves.add(board[i]+"_"+4);
			}
			else if(board[i].contains("K")) {
				if(board[i].length() ==1) {
					int location = i+8;
					if(location<=63) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+0);
					}
					location = i+9;
					if(location<=63 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+1);
					}
					location = i+1;
					if((location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+2);
					}
					location = i-7;
					if(location>=0 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+3);
					}
					location = i-8;
					if(location>=0) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+4);
					}
					location = i-9;
					if(location>=0&&(location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+5);
					}
					location = i-1;
					if((location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+6);
					}
					location = i+7;
					if(location<=63&&(location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+7);
					}
				}
				else {
					int location = i+17;
					if(location<=63&& (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+0);
					}
					location = i+10;
					if(location<=63 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+1);
					}
					location = i-6;
					if(location>=0 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+2);
					}
					location = i-15;
					if(location>=0 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+3);
					}
					location = i-17;
					if(location>=0 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+4);
					}
					location = i-10;
					if(location>=0 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+5);
					}
					location = i+6;
					if(location<=63 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+6);
					}
					location = i+15;
					if(location<=63 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+7);
					}
				}
				
			}
			else if(board[i].contains("k")) {
				if(board[i].length() ==1) {
					int location = i+8;
					if(location<=63) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+4);
					}
					location = i+9;
					if(location<=63 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+5);
					}
					location = i+1;
					if((location)%8>i%8) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+6);
					}
					location = i-7;
					if(location>=0 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+7);
					}
					location = i-8;
					if(location>=0) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+0);
					}
					location = i-9;
					if(location>=0&&(location)%8<i%8) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+1);
					}
					location = i-1;
					if((location)%8<i%8) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+2);
					}
					location = i+7;
					if(location<=63&&(location)%8<i%8) {
						if(board[location].equals("") || board[location].toUpperCase().equals(board[location]))legalMoves.add(board[i]+"_"+3);
					}
				}
				else {
					int location = i+17;
					if(location<=63&& (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+4);
					}
					location = i+10;
					if(location<=63 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+5);
					}
					location = i-6;
					if(location>=0 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+6);
					}
					location = i-15;
					if(location>=0 && (location)%8>i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+7);
					}
					location = i-17;
					if(location>=0 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+0);
					}
					location = i-10;
					if(location>=0 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+1);
					}
					location = i+6;
					if(location<=63 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+2);
					}
					location = i+15;
					if(location<=63 && (location)%8<i%8) {
						if(board[location].equals("") || board[location].toLowerCase().equals(board[location]))legalMoves.add(board[i]+"_"+3);
					}
				}
			}
		}
		return legalMoves;		
	}
	private boolean YourKingInCheck(String[] stateforMove, boolean b) {
		ArrayList<String> moveSet = checkLegalForState(stateforMove);
		boolean check = true;
		
		for(String move: moveSet) {
			if(b & move.equals(move.toLowerCase())) {
				for(String piece: getStateforMove(move))if(piece.equals("K"))check=false;
			}
			else if(move.equals(move.toUpperCase())) {
				for(String piece: getStateforMove(move))if(piece.equals("k"))check=false;
			}
		}
		
		return check;
	}
	private String[] getStateforMove(String legalMove) {
		// TODO Auto-generated method stub
		return null;
	}
}
