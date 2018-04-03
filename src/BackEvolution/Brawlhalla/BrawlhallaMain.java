package BackEvolution.Brawlhalla;

import General.Singleton;
import General.SpecialMain;

public class BrawlhallaMain implements SpecialMain {

	@Override
	public Singleton SetupStartup() {
		BrawlhallaSingleton s = BrawlhallaSingleton.getInstance();
		//start brawlhalla here
		return s;
	}

	@Override
	public void AfterStartup() {
		// TODO Auto-generated method stub
		
	}

}
