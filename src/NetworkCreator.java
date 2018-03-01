import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class NetworkCreator {
	public static Random rand = new Random();
		public static NeuralNetwork[] CreateNetworks(int i, Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SftpException, JSchException{
		//if there are no load files , it creates one random gene for each neural network.
		NeuralNetwork[] NetworkList = new NeuralNetwork[i];
		
		try{
			File file = new File("Generation.txt");					
			load(NetworkList, file);
			System.out.println("loaded Networks");
		}
		catch(Exception e){
		System.out.println("Creating Networks from Scratch");
		for (int j = 0; j<i;j++){		
				Layer[] layers = creator(markets);
				Layer outputlayercopy = layers[1];
				Layer inputlayercopy = layers[0];
				outputlayercopy.setNumber(2);
				inputlayercopy.setNumber(1);
				int inputrand = 0;
				if(inputlayercopy.getINeurons().size()-1 >0) inputrand = rand.nextInt(inputlayercopy.getINeurons().size()-1);
				int outputrand = 0;
				if(outputlayercopy.getONeurons().size()-1 >0) outputrand = rand.nextInt(outputlayercopy.getONeurons().size()-1);
				Gene starter = new Gene(outputlayercopy.getONeurons().get(outputrand), (Math.random()*2)-1);
				inputlayercopy.getINeurons().get(inputrand).AddGenes(starter);
				starter.setInput(inputlayercopy.getINeurons().get(inputrand));
				NetworkList[j] = new NeuralNetwork(inputlayercopy, outputlayercopy, markets);			
				NetworkList[j] = new NeuralNetwork(inputlayercopy, outputlayercopy, markets);
				for (OutputNeuron n : outputlayercopy.getONeurons()){
						n.updateWallets(NetworkList[j].getWallets());
				}
				}									
		}
		return NetworkList;		
	}
		//loads the most recent save file.
		private static void load(NeuralNetwork[] networkList, File f) throws IOException, SftpException, JSchException {
			//loads this to find the actual most recent network. may be obsolete.
			@SuppressWarnings("resource")
			Scanner fin = new Scanner(f);	
			for (NeuralNetwork nn : networkList){
				
				String[] netData = fin.nextLine().split(";");
				// creates a layer based on the numlayer data point
				for (int i = 2; i < Integer.parseInt(netData[0]); i++){
					nn.addLayer(new Layer(false,false));
				}
				//creates neurons in each layer
				String[] NeuronData = netData[1].split(",");
				for (int i =1; i < nn.getLayers().size()-1; i++){
					for (int j = 0; j < Integer.parseInt(NeuronData[i]); j++){
						nn.getLayers().get(i).addNeuron(new Neuron());
					}
				}
				//creates the genes and adds them to each neuron.
				String[] GeneData = netData[2].split(",");
				Neuraltracker(nn);
				for(String data : GeneData){
					if(!data.equals("")){
					String[] g = data.split(":");
					int inLayer = Integer.parseInt(g[0]);
					Integer inNeuron = 0;
					if (inLayer == 1){
						String[] nData = g[1].split("_");
						boolean selected = false;
						for (InputNeuron n : nn.getLayers().get(0).getINeurons()){
							if (n.getMarket().getMarketName().equals(nData[0]) && n.getSelector() == Integer.parseInt(nData[1])){
								selected = true;
								inNeuron = n.getNumber();
							}
						}
						if(!selected)inNeuron = (Integer) null;
					}				
					else{
						try{
						inNeuron = Integer.parseInt(g[1]);
						}
						catch(Exception e){
							
						}
					}
					int outLayer = Integer.parseInt(g[3]);
					Integer  outNeuron = 0;
					if (outLayer == Integer.parseInt(netData[0])){
						String[] nData = g[2].split("_");
						boolean selected = false;
						for (OutputNeuron n : nn.getLayers().get(Integer.parseInt(netData[0])-1).getONeurons()){
							if (n.getMarket().getMarketName().equals(nData[0]) && n.getSelector() == Integer.parseInt(nData[1])){
								outNeuron = n.getNumber();
								selected = true;
							}
						}
						if(!selected)outNeuron = (Integer) null;;
					}
					else{
						outNeuron = Integer.parseInt(g[2]);
					}
					double weight = Double.parseDouble(g[4]);
					int enabled = 1;
					try {
						enabled = Integer.parseInt(g[5]);
					}
					catch (Exception e){
					}
					try{
					if(inLayer == 1 && inNeuron != null && outNeuron != null){
						if(outLayer == Integer.parseInt(netData[0])){
							Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1), weight);
							if (enabled == 0)g2.toggle();
							nn.getLayers().get(0).getINeurons().get(inNeuron-1).AddGenes(g2);
							g2.setInput(nn.getLayers().get(0).getINeurons().get(inNeuron-1));
							nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1).addInput(g2);
						}
						else{
							Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
							if (enabled == 0)g2.toggle();
							nn.getLayers().get(0).getINeurons().get(inNeuron-1).AddGenes(g2);
							g2.setInput(nn.getLayers().get(0).getINeurons().get(inNeuron-1));
							nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
						}
					}
					else if(inNeuron != null && outNeuron != null){
						if(outLayer == Integer.parseInt(netData[0])){
							Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1), weight);
							if (enabled == 0)g2.toggle();
							nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
							g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
							nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1).addInput(g2);
						}
						else{
							Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
							if (enabled == 0)g2.toggle();
							nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
							g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
							nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
						}
					}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					}
				}
			}
			
		}
		//Makes sure neuron and gene location data are correct.
		public static void Neuraltracker(NeuralNetwork nn){
			
			for (int i = 0; i < nn.getLayers().size(); i++){
				Layer l = nn.getLayers().get(i);
				l.setNumber(i+1);
				if (l.isInput()){
					ArrayList<InputNeuron> ns = l.getINeurons();
					for (int j = 0; j < ns.size(); j++){
						InputNeuron n = ns.get(j);
						n.setLayernumber(i+1);
						n.setNumber(j+1);
					}
				}
				else if (l.isOutput()){
					ArrayList<OutputNeuron> ns = l.getONeurons();
					for (int j = 0; j < ns.size(); j++){
						OutputNeuron n = ns.get(j);
						n.setLayernumber(i+1);
						n.setNumber(j+1);
					}
				}
				else {
					ArrayList<Neuron> ns = l.getNeurons();
					for (int j = 0; j < ns.size(); j++){
						Neuron n = ns.get(j);
						n.setLayernumber(i+1);
						n.setNumber(j+1);
					}
				}
			}
			
		}
		//builds the input and output layers for each neural network
		public static Layer[] creator(Market[] markets) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
			Layer[] layers = new Layer[2];
			
			Class<?> marketclass = Class.forName("Market");
			Method[] ms = marketclass.getMethods();
			Layer input = new Layer(true,false);
			Method m = null;
			for (Method method: ms){
				if (method.getName().equals("getData")){
					m = method;
				}
			}
			//creates 89 input neurons for each market for the 89 outputs of the get data method.
			for (Market market : markets){			
				for (int i = 0; i <= 89;i++){
					if (!market.getMarketName().equals("BTC-HKG")&&!market.getMarketName().equals("BTC-XBB")){
					InputNeuron in2 = new InputNeuron(m, market, i);
					input.addInputNeuron(in2);
					in2.setLayernumber(1);
					}
				}
			}
			for (InputNeuron n : input.getINeurons()){
				n.setLayernumber(1);
			}
			layers[0] = input;
			Layer output = new Layer(false,true);
			Class<?> outputs = Class.forName("OutputMethods");
			Method m1 = outputs.getMethods()[0];
			Method m2 = outputs.getMethods()[1];
			//creates 5 buy and 5 sell outputs for each market.
			for (Market market: markets){
				for (int i = 1; i <= 5;i++){
					if (!market.getMarketName().equals("BTC-HKG")&&!market.getMarketName().equals("BTC-XBB")){
					OutputNeuron out = new OutputNeuron(m1,1,market,i);
					output.addOutputNeuron(out);
					out.setLayernumber(2);
					out = new OutputNeuron(m2,1,market,i);
					output.addOutputNeuron(out);
					out.setLayernumber(2);
					}
				}
				
				
			}
			for(OutputNeuron n : output.getONeurons()){
				n.setLayernumber(2);
			}
			layers[1] = output;
			
			return layers;
			}
}



