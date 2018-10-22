package BackEvolution.Trader;

import java.io.IOException;

import General.DataManager;
import General.Startup;

public class TraderStartup extends Startup {

	@Override
	public DataManager SetupStartup() {
		TraderDataManager data = new TraderDataManager();
		try {
			MarketManager.start(data);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return data;
	}
	@Override
	public void AfterStartup(DataManager data) {
		try {
			ProgressTracker.start((TraderDataManager) data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
