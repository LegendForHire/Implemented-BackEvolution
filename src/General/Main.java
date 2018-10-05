package General;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Main {
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException{
		// Scanner to select type of network
		System.out.println("find your package name 'BackEvolution.TYPE' and enter what you have replaced TYPE with:");
		Scanner in = new Scanner(System.in);
		String type = in.nextLine();
		//getting Special main from network package
		Class<? extends SpecialMain> mainClass = (Class<? extends SpecialMain>) Class.forName("BackEvolution." + type + "." + type+"Main");
		@SuppressWarnings("deprecation")
		SpecialMain main = mainClass.newInstance();
		//quit thread used to stop neural network
		long t1 = System.currentTimeMillis();
		Thread threadQuit = new Thread(){
        	public void run(){
        		while(true){
        		System.out.println("Type 'q' to quit");
        		String quit = in.nextLine();
        		if(quit.equals("q")) {
        			try {
						Class<? extends Singleton> singleClass = (Class<? extends Singleton>) Class.forName("BackEvolution." + type + "." + type+"Singleton");
						Singleton s = (Singleton) singleClass.getMethod("getInstance").invoke(null, new Object[0]);
						s.getWriter().close();
					} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			System.exit(1);
        		}
        		}
        	}
        };
        threadQuit.start();
        //Setting up prereqs and getting network singleton
        main.SetupStartup();
        //Starting the learning algorithm,
		NeuralNetManager.start(Singleton.getInstance());
		System.out.println(System.currentTimeMillis()-t1);
		//Starting other threads created by Special main package
		main.AfterStartup();		
	}
}
