package BackEvolution.Brawlhalla;

import General.Singleton;
import General.SpecialMain;

public class BrawlhallaMain implements SpecialMain {

	@Override
	public Singleton SetupStartup() {
		BrawlhallaSingleton s = BrawlhallaSingleton.getInstance();
		return s;
	}

	@Override
	public void AfterStartup() {
		
	}

}
