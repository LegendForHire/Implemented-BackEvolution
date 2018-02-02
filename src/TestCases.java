import java.io.IOException;
import java.lang.reflect.Method;

public class TestCases {
	public static void main(String[] args) throws IOException{
		NeuralNetwork nn1;
		NeuralNetwork nn2;
		Layer in = new Layer(true,false);
		Layer out = new Layer(false, true);
		Market grow = new Market("Test");
		Method m;
		m = new OutputMethods().getClass().getMethods()[0];
		OutputNeuron n = new OutputNeuron(m, 1, grow, 5);
		Method fake = new OutputMethods().getClass().getMethods()[2];
		InputNeuron n2 = new InputNeuron(fake);
		Gene g = new Gene(n, 0.5);
		n2.AddGenes(g);
		g.setInput(n2);
		n.input.add(g);
		in.addInputNeuron(n2);
		out.addOutputNeuron(n);
		nn1 = new NeuralNetwork(in,out, null);
		Layer in2 = new Layer(true,false);
		Layer out2 = new Layer(false, true);
		Market fall = new Market("Test2");
		OutputNeuron n3 = new OutputNeuron(m, 1, fall, 5);
		Gene g2 = new Gene(n3, 0.5);
		InputNeuron n4 = new InputNeuron(fake);
		n4.AddGenes(g2);
		g2.setInputI(n4);
		n3.input.add(g2);
		in2.addInputNeuron(n4);
		out2.addOutputNeuron(n3);
		nn2 = new NeuralNetwork(in2,out2,null);
		NeuralNetwork[] nns = {nn1, nn2};
	}
	
}
