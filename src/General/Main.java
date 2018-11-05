package General;

public class Main {
	@SuppressWarnings({ "unchecked" })
	public static void main(String[] args){
		String type = PropertyReader.getProperty(Properties.TYPE.toString());
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
