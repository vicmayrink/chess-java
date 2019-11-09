package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;
	
	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p== null || p.getColor() != this.getColor();
	}
	
	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
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
		
		//Special move: Castling
		if(getMoveCount() == 0 && !chessMatch.getCheck()) {
			//Castling king side rook
			Position kingRookPosition = new Position(position.getRow(), position.getCol() + 3);
			if (testRookCastling(kingRookPosition)) {
				Position p1 = new Position(position.getRow(), position.getCol() + 1);
				Position p2 = new Position(position.getRow(), position.getCol() + 2);
				if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2)) {
					mat[position.getRow()][position.getCol() + 2] = true;
				}
			}
			//Castling queen side rook
			Position queenRookPosition = new Position(position.getRow(), position.getCol() - 4);
			if (testRookCastling(queenRookPosition)) {
				Position p1 = new Position(position.getRow(), position.getCol() - 1);
				Position p2 = new Position(position.getRow(), position.getCol() - 2);
				Position p3 = new Position(position.getRow(), position.getCol() - 3);
				if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2) && !getBoard().thereIsAPiece(p3)) {
					mat[position.getRow()][position.getCol() - 2] = true;
				}
			}
		}
		
		
		return mat;
	}

}
