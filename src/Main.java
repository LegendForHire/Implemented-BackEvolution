
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.*;

/**/
@SuppressWarnings("unused")

public class Main {

	/*created to keep track of the main neural network's wallets which I use to see if the program has become profitable*/
	static ArrayList<Wallet> wallets;
	/*This is a variable created to see what the value of the wallets would be if no action was taken used to see profitability in output files*/
	private static ArrayList<Wallet> noactwallets;
	private static Session session;
	private static ChannelSftp sftpChannel;
	/* main methods initializes all the separate threads threads*/
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, SftpException, JSchException{
		/* This thread creates a thread for each market to keep it updated with the latest data*/		
		long t1 = System.currentTimeMillis();
		Thread threadQuit = new Thread(){
        	public void run(){
        		while(true){
        		System.out.println("Type 'q' to quit");
        		@SuppressWarnings("resource")
			Scanner in = new Scanner(System.in);
        		String quit = in.nextLine();
        		if(quit.equals("q")) {
        			Singleton s = Singleton.getInstance();
        			s.getWriter().close();
        			System.exit(1);
        		}
        		}
        	}
        };
        threadQuit.start();
        SFTPManager.start();
		MarketManager.start();		
		/* had to put a wait time here for the threads from the previous thread to get started*/
		long t = System.currentTimeMillis();
		while(System.currentTimeMillis() - t < 20000);
		NeuralNetManager.start();
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		ProgressTracker.start();

	}

}

