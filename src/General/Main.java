package General;

import java.util.Scanner;

public class Main {
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args){
		// Scanner to select type of network
		System.out.println("find your package name 'BackEvolution.TYPE' and enter what you have replaced TYPE with:");
		Scanner in = new Scanner(System.in);
		String type = in.nextLine();
		//getting Special main from network package
		
		try {
			Class<? extends MethodManager> mainClass = (Class<? extends MethodManager>) Class.forName("BackEvolution." + type + "." + type+"MethodManager");
			MethodManager main = mainClass.newInstance();
			long t1 = System.currentTimeMillis();
			
	        //Setting up prereqs and getting network singleton
	        DataManager data = main.SetupStartup();
	        Thread threadQuit = new Thread(){
	        	public void run(){
	        		while(true){
	        		System.out.println("Type 'q' to quit");
	        		String quit = in.nextLine();
	        		if(quit.equals("q")) {
	        			try {
							data.getWriter().close();
						} catch (IllegalArgumentException|SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        			System.exit(1);
	        		}
	        		}
	        	}
	        };
	        threadQuit.start();
	        //Starting the learning algorithm,
			NeuralNetManager.start(data);
			System.out.println(System.currentTimeMillis()-t1);
			//Starting other threads created by Special main package
			main.AfterStartup(data);	
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
