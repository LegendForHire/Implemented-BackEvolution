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
	private ArrayList<double[][]> stateList;
	public Game(Controller[] c){
		this.c = c;
		Player2Data = new double[s.getLegends().length + s.getWeapons().length + 3];
		Player1Data = new double[s.getLegends().length + s.getWeapons().length + 3];
		levelData = new double[s.getStages().length];
	}
	public double getData(Controller controller, int i) {
		if(i > s.getWeapons().length*4+s.getLegends().length*2+5){
			return levelData[i-s.getWeapons().length*4+s.getLegends().length*2+5];
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
				for (int j =3; j < s.getWeapons().length+3; j++){
					if (s.getWeapons()[j-3].getName().equals(EquippedWeapon(i).getName())) levelData[j]=1;
					else levelData[j] = 0;
				}
				for (int j = 3 + s.getWeapons().length; j < s.getWeapons().length*2+3; j++){
					if (CurrentLegend(i).isEquipabble(s.getWeapons()[j- 3 + s.getWeapons().length])) levelData[j]=1;
					else levelData[j] = 0;
				}
				for (int j = 3 + s.getWeapons().length*2; j < s.getLegends().length +3 + s.getWeapons().length*2; j++){
					if (s.getLegends()[j-3 + s.getWeapons().length*2].getName().equals(CurrentLegend(i).getName())) levelData[j]=1;
					else levelData[j] = 0;
				}
			}
			for (int i = 0; i < s.getStages().length; i++){
				if(s.getStages()[i].getName().equals(currentStage.getName()))levelData[i]=1;
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
		double[][] gameState = {Player1Data.clone(),Player2Data.clone()};
		return gameState;
	}
	public void stateStart() {
		stateList = new ArrayList<double[][]>();
		stateList.add(getState());
	}
	public void addState(double[][] state) {
		stateList.add(state);
		
	}
}
