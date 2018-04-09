package BackEvolution.Chess;
//No piece = 000
//pawn = 001
//rook = 010
//knight = 011
//bishop = 100
//queen = 110
//king = 111
//black = 1;
//white = 0;


public class Chessboard {
	String[] board;
	public Chessboard(){
		String [] boardInit = {"R1","K1","B1","Q","K","B2","K2","R2","P1","P2","P3","P4","P5","P6","P7","P8","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","p1","p2","p3","p4","p5","p6","p7","p8","r1","k1","b1","q","k","b2","k2","r2"};
		board = boardInit;
	}
	
}
