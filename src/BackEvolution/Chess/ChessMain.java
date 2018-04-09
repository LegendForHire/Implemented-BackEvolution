package BackEvolution.Chess;

import General.Singleton;
import General.SpecialMain;

public class ChessMain implements SpecialMain {

	@Override
	public Singleton SetupStartup() {
		// TODO Auto-generated method stub
		return ChessSingleton.getInstance();
	}

	@Override
	public void AfterStartup() {
		// TODO Auto-generated method stub
		
	}

}
