package BackEvolution.Chess;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import General.Layer;
import General.NeuralNetwork;

public class ChessNetwork extends NeuralNetwork {

	private double bestMoveValue;
	private String bestMove;
	public ChessNetwork(Layer inputLayer, Layer outputLayer, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(inputLayer, outputLayer, a);
		// TODO Auto-generated constructor stub
	}
	public ChessNetwork(NeuralNetwork nn, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(nn, a);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void addLayer(Layer l) {
		super.addLayer(l);
		ChessLayer cl = (ChessLayer) l;
		cl.setNetwork(this);
	}
	public double getBestMoveValue() {
		// TODO Auto-generated method stub
		return bestMoveValue;
	}
	public void setBestMoveValue(double value) {
		bestMoveValue = value;
	}
	public void setBestMove(String move) {
		bestMove = move;
		
	}
	public String getBestMove() {
		return bestMove;
		
	}

	

}
