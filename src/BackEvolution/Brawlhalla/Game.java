package BackEvolution.Brawlhalla;

public class Game {
	Controller[] c;
	Controller opponent;
	Controller player;
	private double[] data;
	private boolean gameOver;
	public Game(Controller[] c){
		this.c = c;
	}
	public double getData(Controller controller, int i) {
		player = c[0];
		opponent = c[1];
		if(controller == c[1]){
			player = c[1];
			opponent = c[2];
		}
		return data[i];
	}
	public boolean isOver() {
		return gameOver;
	}
	public void updater(){
		//replace with when i get game interface
		if(isOver()){
			gameOver = true;
		}
		else {
			gameOver = false;
		}
		
	}
}
