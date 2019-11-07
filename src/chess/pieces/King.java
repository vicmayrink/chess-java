package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p== null || p.getColor() != this.getColor();
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getCols()];
		
		Position p = new Position(0, 0);
		
		//forward
		p.setValues(position.getRow() - 1, position.getCol());
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//backward
		p.setValues(position.getRow() + 1, position.getCol());
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//left
		p.setValues(position.getRow(), position.getCol() - 1);
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//right
		p.setValues(position.getRow(), position.getCol() + 1);
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//forward-left
		p.setValues(position.getRow() - 1, position.getCol() - 1);
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//forward-right
		p.setValues(position.getRow() - 1, position.getCol() + 1);
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//backward-left
		p.setValues(position.getRow() + 1, position.getCol() - 1);
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		//backward-right
		p.setValues(position.getRow() + 1, position.getCol() + 1);
		if (getBoard().positionExists(p) && canMove(p)) mat[p.getRow()][p.getCol()] = true;
		
		
		return mat;
	}

}
