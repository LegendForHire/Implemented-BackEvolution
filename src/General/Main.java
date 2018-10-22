package General;

import java.util.Scanner;

public abstract class Main {
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args){
		String type = PropertyReader.getProperty("type");	
		try {
			Class<? extends Startup> mainClass = (Class<? extends Startup>) Class.forName("BackEvolution." + type + "." + type+"Startup");
			Startup startup = mainClass.newInstance();
			startup.start();
			
	       	
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}	
}
