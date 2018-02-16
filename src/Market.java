import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Market {
	Double[] marketData;
	String marketName;
	long lastUpdate;
	double old;
	public Market(String name) throws IOException{
		marketData = new Double[90];
		lastUpdate = System.currentTimeMillis();
		marketName = name;
		}
	public Double getData(int selector) throws IOException{
		return marketData[selector];
	}
	//market data is stored here in 89 points of data.
	void updateMarkets() throws IOException {
		try{
		URL marketAPI = new URL("https://bittrex.com/api/v1.1/public/getmarketsummary?market="+ marketName);
		BufferedReader in = new BufferedReader(new InputStreamReader(marketAPI.openStream()));
		String[] s2 = in.readLine().substring(39).replace("}", "").split(",");
		marketData[0] = Double.parseDouble(s2[1].substring(7,s2[1].length())); //high 0
		marketData[1] = Double.parseDouble(s2[2].substring(6,s2[2].length())); //low 1
		marketData[2] = Double.parseDouble(s2[3].substring(9,s2[3].length())); //volume 2
		marketData[3] = Double.parseDouble(s2[4].substring(7,s2[4].length())); //last 3
		marketData[4] = Double.parseDouble(s2[5].substring(13,s2[5].length())); //basevolume 4
		marketData[5] = Double.parseDouble(s2[7].substring(6,s2[7].length())); //bid 5
		marketData[6] = Double.parseDouble(s2[8].substring(6,s2[8].length())); //ask 6
		marketData[7] = Double.parseDouble(s2[9].substring(16,s2[9].length())); //OpenBuyOrders 7
		marketData[8] = Double.parseDouble(s2[10].substring(17,s2[10].length())); //OpenSellOrders 8
		marketData[9] = Double.parseDouble(s2[11].substring(10,s2[11].length())); //PrevDay 9
		URL marketHistory = new URL("https://bittrex.com/api/v1.1/public/getmarkethistory?market=" + marketName);
		in = new BufferedReader(new InputStreamReader(marketHistory.openStream()));
		String[] historyList = in.readLine().substring(40).split("},");
		if (historyList.length > 2){
		for (int selector = 10; selector < 90; selector++){
			//creates averages of recent buys and sells
			double data2 = 0.0;
			int selector2 = (selector-10) % 4;
			int selector3 = (int) ((selector-10)/4);
			for (int i = 0; i < 10; i++){
				String[] s4 = historyList[(selector3*10)+i].split(",");
				if (selector2 == 0){
				data2 += Double.parseDouble(s4[2].substring(11,s4[2].length()))/10; //Quantity 1
				}
				else if (selector2 == 1){
				data2 += Double.parseDouble(s4[3].substring(8,s4[3].length()))/10; //Price 2
				}
				else if (selector2 == 1){
				data2 += Double.parseDouble(s4[4].substring(8,s4[4].length())+",")/10; //Total 3
				}
				else{
				data2 += Double.parseDouble(s4[6].substring(13,s4[6].length()).replace("BUY", "1").replace("SELL", "0").replace("}", "").replace("]", "").replace("\"", "")); //OrderType 5 BUY = 1 SELL = 0
				}
				
			}
			marketData[selector] = data2;
		}
		
		lastUpdate = System.currentTimeMillis();
		}
		}
		catch (Exception e){
			//included so i can see if a market is constantly failing.
			//System.out.println(marketName + "update failed at :" + System.currentTimeMillis());
			//set to 0 so that any input associated with this essentially becomes disabled on a failed update.
			for (int i =0; i < 90; i++){
				if (i == 3 && marketData[i] == null) marketData[i] = .01;
				else if (marketData[i] == null) marketData[i] = 0.0;				
			}
		}
	}
	public String getMarketName(){
		return marketName;
	}
	public Double getOld() {
		// TODO Auto-generated method stub
		return old;
	}
	public void setOld(){
		old = marketData[3];
	}
	public Double getReturn() {
		return marketData[3]/old;
	}
	public void update3(double d){
		marketData[3] = d;
	}
}

