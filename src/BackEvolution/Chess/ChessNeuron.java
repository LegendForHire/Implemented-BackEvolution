package BackEvolution.Chess;

import java.util.ArrayList;

import General.Gene;
import General.Neuron;

public class ChessNeuron extends Neuron {
	ChessSingleton s = ChessSingleton.getInstance();
	private String player;
	public ChessNeuron(){
		super();
		//TODO
	}
	public ChessNeuron(String method){
		super(method);
		//TODO
	}
	public ChessNeuron(Neuron n) {
		super(n);
		//TODO
	}
	@Override
	public void invoke(){
		String[] chessboard = s.getChessboard();
		String[] methods = method.split("_");
		if(methods[0].equals("isPiece")) {
			String piece = methods[1];
			int location = Integer.parseInt(methods[2]);
			if(player.equals("black")) {
				location = 64-location;
				if(piece.toLowerCase().equals(piece))piece = piece.toUpperCase();
				else piece = piece.toLowerCase();
			}
			if(chessboard[location].equals(piece))setValue(1);
		}
		else {
			if(player.equals("black"))move(chessboard,methods[0].toLowerCase(),Integer.parseInt(methods[1]));
			else move(chessboard,methods[0],Integer.parseInt(methods[1]));
		}
	}
	private void move(String[] chessboard, String piece, int move) {
		// TODO Auto-generated method stub
		if(piece.contains("B")||piece.contains("b")) {
			//bishop move
		}
		if(piece.contains("K")||piece.contains("k")) {
			if(piece.length()>1) {
				//knight move
			}
			else {
				//king move
			}
		}
		if(piece.contains("R")||piece.contains("r")){
			//rook move
		}
		if(piece.contains("Q")||piece.contains("q")){
			//queen move
		}
		if(piece.contains("P")||piece.contains("")){
			//queen move
		}
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	
}
