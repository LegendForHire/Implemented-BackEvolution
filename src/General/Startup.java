package General;

import java.util.Scanner;

public abstract class Startup {
	public Startup() {
		
	}
	public void start(){
		long t1 = System.currentTimeMillis();
		
        //Setting up prereqs and getting network singleton
        DataManager data = SetupStartup();
        Scanner in = new Scanner(System.in);
		quitThread(in, data);
        //Starting the learning algorithm,
        NeuralNetManager netManager = data.getNetManager();
		netManager.start();
		System.out.println(System.currentTimeMillis()-t1);
		//Starting other threads created by Special main package
		AfterStartup(data);
	}
	private static void quitThread(Scanner in, DataManager data) {
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
	}
	// Setup threads and data you need to startup before your networks are run. returns your implemntation of data manager
	public abstract DataManager SetupStartup();
	//setup threads that track additional information from your neural networks
	public abstract void AfterStartup(DataManager data);
	
}
