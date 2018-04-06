package BackEvolution.Brawlhalla;

import java.util.ArrayList;

public class Game {
	Controller[] c;
	Controller opponent;
	Controller player;
	private double[] Player1Data;
	private double[] Player2Data;
	private double[] levelData;
	private boolean gameOver;
	BrawlhallaSingleton s = BrawlhallaSingleton.getInstance();
	private Stage currentStage;
	private Weapon player1Equipped;
	private Weapon player2Equipped;
	private Legend player1Legend;
	private Legend w;
	public Game(Controller[] c){
		this.c = c;
		Player2Data = new double[s.getLegends().size() + s.getWeapons().size() + 3];
		Player1Data = new double[s.getLegends().size() + s.getWeapons().size() + 3];
		levelData = new double[s.getStages().size()];
	}
	public double getData(Controller controller, int i) {
		if(i > s.getWeapons().size()*4+s.getLegends().size()*2+5){
			return levelData[i-s.getWeapons().size()*4+s.getLegends().size()*2+5];
		}
		else if(c[1] == controller && i%2 == 0 || c[0] == controller && i%2 == 1 ){
			return Player2Data[i/2];
		}
		return Player1Data[i/2];
	}
	public boolean isOver() {
		return gameOver;
	}
	public void updater(){
		//TODO replace with when i get game interface
		if(isOver()){
			gameOver = true;
		}
		else {
			gameOver = false;
			double[][] datas = {Player1Data,Player2Data};
			for(int i = 0; i < 2; i++){
				datas[i][0] = getPlayerX(i);
				datas[i][1] = getPlayerY(i);
				datas[i][2] = getPlayerJumpsLeft(i);
				for (int j =3; j < s.getWeapons().size()+3; j++){
					if (s.getWeapons().get(j-3).getName().equals(EquippedWeapon(i).getName())) datas[i][j]=1;
					else datas[i][j] = 0;
				}
				for (int j = 3 + s.getWeapons().size(); j < s.getWeapons().size()*2+3; j++){
					if (CurrentLegend(i).isEquipabble(s.getWeapons().get(j- 3 + s.getWeapons().size()))) datas[i][j]=1;
					else datas[i][j] = 0;
				}
				for (int j = 3 + s.getWeapons().size()*2; j < s.getLegends().size() +3 + s.getWeapons().size()*2; j++){
					if (s.getLegends().get(j-3 + s.getWeapons().size()*2).getName().equals(CurrentLegend(i).getName())) datas[i][j]=1;
					else datas[i][j] = 0;
				}
			}
			for (int i = 0; i < s.getStages().size(); i++){
				if(s.getStages().get(i).getName().equals(currentStage.getName()))levelData[i]=1;
				else levelData[i] = 0;
			}
			
		}
		
	}
	private Legend CurrentLegend(int i) {
		if(i == 0)return player1Legend;
		else return w;
	}
	private Weapon EquippedWeapon(int i) {
		// TODO Auto-generated method stub
		if(i == 0)return player1Equipped;
		else return player2Equipped;
	}
	private double getPlayerJumpsLeft(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	private double getPlayerY(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	private double getPlayerX(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
	public double[][] getState() {
		// TODO Auto-generated method stub
		double[][] gameState = {Player1Data.clone(),Player2Data.clone(), levelData.clone()};
		return gameState;
	}
	public ArrayList<Legend> getLegends() {
		// TODO Auto-generated method stub
		return null;
	}
	public ArrayList<Weapon> getWeapons() {
		// TODO Auto-generated method stub
		return null;
	}
	public ArrayList<Stage> getStages() {
		// TODO Auto-generated method stub
		return null;
	}
}
