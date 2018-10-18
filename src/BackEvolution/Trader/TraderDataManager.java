package BackEvolution.Trader;

import General.DataManager;

public class TraderDataManager extends DataManager {
	private Market[] markets;

	public TraderDataManager(TraderMethodManager methods){
		super(methods);
	}
	public void setMarkets(Market[] markets) {
		this.markets = markets;
		
	}
	public Market[] getMarkets() {
		return markets;
	}
	
}
