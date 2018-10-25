package General;

import java.util.Scanner;

public class Main {
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args){
		System.out.println(Properties.NUM_NETWORKS.toString());
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
