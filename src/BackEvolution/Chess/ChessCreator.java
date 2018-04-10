package BackEvolution.Chess;
import General.Layer;
import General.Neuron;
import General.SpecialCreator;
public class ChessCreator implements SpecialCreator{

	@Override
	public void NeuronSetup(Neuron no, int j) {
		
	}

	@Override
	public void InputOutputcreator(Layer[] copies) {
		String[] pieces = {"K1","Q","K","K2","P1","P2","P3","P4","P5","P6","P7","P8"};
		for( String piece :  pieces) {
			for (int i = 0; i< 64; i++) {
				ChessNeuron n = new ChessNeuron("isPiece_"+piece+"_"+i);	
				copies[0].addNeuron(n);
				n = new ChessNeuron("isPiece_"+piece.toLowerCase()+"_"+i);
				copies[0].addNeuron(n);
			}
		}
		for(int i = 1; i<9; i++) {
			ChessNeuron n = new ChessNeuron("P"+i+"_1");
			copies[1].addNeuron(n);
			n = new ChessNeuron("P"+i+"_2");
			copies[1].addNeuron(n);
			n = new ChessNeuron("K1_"+(i-1));
			copies[1].addNeuron(n);
			n = new ChessNeuron("K2_"+(i-1));
			copies[1].addNeuron(n);
			n = new ChessNeuron("K_"+(i-1));
			copies[1].addNeuron(n);
		}
		for(int i = 0; i < 28; i++) {		
			ChessNeuron n = new ChessNeuron("R1_"+i);
			copies[1].addNeuron(n);
			n = new ChessNeuron("R2_"+i);
			copies[1].addNeuron(n);
			n = new ChessNeuron("B1_"+i);
			copies[1].addNeuron(n);
			n = new ChessNeuron("B2_"+i);
			copies[1].addNeuron(n);
		}
		
	}
	
}
