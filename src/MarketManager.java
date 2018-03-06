import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MarketManager {
	public static void start() throws IOException {
		Singleton s = Singleton.getInstance();
		Market[] markets = marketCreator();
		s.setMarkets(markets);
		Thread thread = new Thread() {
				public void run() {
					try {
						updater(markets);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
		};
		thread.start();
	}
	//creates all the markets
		public static Market[] marketCreator() throws IOException{
			//loads the bittrex api to get a list off all markets
			URL marketAPI = new URL("https://bittrex.com/api/v1.1/public/getmarketsummaries");
			BufferedReader in = new BufferedReader(new InputStreamReader(marketAPI.openStream()));
			String[] marketlist = in.readLine().substring(39).split("},");
			String[] names = new String[marketlist.length];
			for (int i = 0; i < marketlist.length;i++){
				String[] s2 = marketlist[i].split(",");
				names[i] = (s2[0].substring(15,s2[0].length()-1));
			}
			int banned = 0;
			for (String name : names){
				if (name.contains("ANS") || name.contains("SEC")){
					banned++;
				}
			}
			//creates the markets from the api includes some banned apis because they were broken.
			Market[] markets = new Market[names.length-banned];
			int j = 0;
			for (int i = 0; i < names.length ;i++){
				if (!names[i].contains("ANS") && !names[i].contains("SEC")){
					markets[i-j] = new Market(names[i]);
				}
				else j++;
			}
			return markets;
		}
	//creates threads that keep all the markets updated in real time.
	public static void updater (Market[] markets) throws IOException{
			ArrayList<Thread> threads = new ArrayList<Thread>();
			for (Market market : markets){
				threads.add (new Thread() {
					public void run(){			
								
									try {
										updater(market);
									} catch (IOException e) {
										e.printStackTrace();
									}
								
						}
				});
				
			}
			for (Thread thread: threads){
				thread.start();
			}
			Singleton s = Singleton.getInstance();
			s.getWriter().println("all started updating");
		}
	//keeps a single market constantly updated
	public static void updater (Market market) throws IOException{
		//had to modify this while loop this when the api was updating at some point and had null data in places. left it in for easy modification in future updates.
			while(!market.getMarketName().equals("BTC-HKG") && !market.getMarketName().equals("BTC-XBB")){
				market.updateMarkets();
			}
		}
}
