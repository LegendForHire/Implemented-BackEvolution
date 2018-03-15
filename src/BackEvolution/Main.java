package BackEvolution;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class Main {
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, SftpException, JSchException, InstantiationException{
		System.out.println("find your package name 'BackEvolution.TYPE' and enter what you have replaced TYPE with:");
		Scanner in = new Scanner(System.in);
		String type = in.nextLine();
		Class<? extends SpecialMain> mainClass = (Class<? extends SpecialMain>) Class.forName("BackEvolution." + type + "." + type+"Main");
		SpecialMain main = mainClass.newInstance();
		
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
        Singleton s = main.SetupStartup();
		NeuralNetManager.start(s);
		System.out.println(System.currentTimeMillis()-t1);
		main.AfterStartup();		
	}
}
