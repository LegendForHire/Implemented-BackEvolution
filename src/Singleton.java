import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.jcraft.jsch.ChannelSftp;

public class Singleton {
	
	private static Singleton uniqueInstance = new Singleton();
	private ChannelSftp channel;
	private Market[] markets;
	private NeuralNetwork[] networks;
	private double totalGlobalError;
	private PrintWriter writer;
	public static final double ACTIVATION = .8;
	
	private Singleton() {
		File f = new File("log.txt");
		try {
			writer = new PrintWriter(f);
		} catch (FileNotFoundException e) {}
	}
	
	public static Singleton getInstance() {
		return uniqueInstance;
	}

	public ChannelSftp getChannel() {
		return channel;
		// TODO Auto-generated method stub	
	}

	public void setChannel(ChannelSftp sftp) {
		// TODO Auto-generated method stub
		
	}

	public void setMarkets(Market[] markets) {
		this.markets = markets;
		
	}

	public Market[] getMarkets() {
		// TODO Auto-generated method stub
		return markets;
	}

	public void setNetworks(NeuralNetwork[] nns) {
		// TODO Auto-generated method stub
		networks = nns;
	}

	public NeuralNetwork[] getNetworks() {
		// TODO Auto-generated method stub
		return networks;
	}

	public double getTotalGlobalError() {
		return totalGlobalError;
	}

	public void setTotalGlobalError(double totalGlobalError) {
		this.totalGlobalError = totalGlobalError;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	

}
