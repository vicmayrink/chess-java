package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "P";
	}
	
	private boolean can(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p== null || p.getColor() != this.getColor();
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getCols()];
		
		Position p = new Position(0, 0);
		
		if (this.getColor() == Color.WHITE) {
			//forward
			p.setValues(position.getRow() - 1, position.getCol());
			if (getBoard().positionExists(p) && getBoard().piece(p) == null) mat[p.getRow()][p.getCol()] = true;
			//capture-left
			p.setValues(position.getRow() - 1, position.getCol() - 1);
			if (getBoard().positionExists(p) && getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != this.getColor()) mat[p.getRow()][p.getCol()] = true;
			//capture-right
			p.setValues(position.getRow() - 1, position.getCol() + 1);
			if (getBoard().positionExists(p) && getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != this.getColor()) mat[p.getRow()][p.getCol()] = true;
		} else {
			//backward
			p.setValues(position.getRow() + 1, position.getCol());
			if (getBoard().positionExists(p) && getBoard().piece(p) == null) mat[p.getRow()][p.getCol()] = true;
			//capture-left
			p.setValues(position.getRow() + 1, position.getCol() - 1);
			if (getBoard().positionExists(p) && getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != this.getColor()) mat[p.getRow()][p.getCol()] = true;
			//capture-right
			p.setValues(position.getRow() + 1, position.getCol() + 1);
			if (getBoard().positionExists(p) && getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != this.getColor()) mat[p.getRow()][p.getCol()] = true;			
		}
		
		return mat;
	}

}
