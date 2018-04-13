package BackEvolution.Chess;
import General.Neuron;

public class ChessNeuron extends Neuron {
	ChessSingleton s = ChessSingleton.getInstance();
	private String player;
	private ChessNetwork parent;
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
				location = 63-location;
				if(piece.toLowerCase().equals(piece))piece = piece.toUpperCase();
				else piece = piece.toLowerCase();
			}
			if(chessboard[location].equals(piece))setValue(1);
		}
		else {
			if(parent.getBestMoveValue()< value) {
				parent.setBestMoveValue(value);
				if(player.equals("black")) parent.setBestMove(methods[0].toLowerCase());
				else parent.setBestMove(methods[0]);
			}
			
		}
	}

	public void setPlayer(String player) {
		this.player = player;
	}
	public void setParent(ChessNetwork parent) {
		this.parent = parent;
		
	}
	
}
