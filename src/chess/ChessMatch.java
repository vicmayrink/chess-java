package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getCols()];
		for (int i=0; i < board.getRows(); i++) {
			for (int j=0; j < board.getCols(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public int getTurn() {
		return this.turn;
	}
	
	public Color getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public boolean getCheck() {
		return this.check;
	}
	
	public boolean getCheckMate() {
		return this.checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return this.promoted;		
	}
	
	public boolean[][] possibleMoves (ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove (ChessPosition sourcePos, ChessPosition targetPos) {
		Position source = sourcePos.toPosition();
		Position target = targetPos.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece) board.piece(target);
		
		//Special move: promotion
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ( (movedPiece.getColor() == Color.WHITE && target.getRow() == 0) ||
					(movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece) board.piece(target);
				promoted = replacePromotedPiece("Q");
				
			}
		}
		
		
		//Special move: En Passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		
		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("Q") && !type.equals("R")) {
			throw new InvalidParameterException("Invalid type for promotion.");
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
		
	}
	
	private ChessPiece newPiece(String type, Color color) {
		
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
		
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		//Special Move: Castling king side rook
		if (p instanceof King && target.getCol() == source.getCol() + 2) {
			Position rookSourcePosition = new Position(source.getRow(), source.getCol() + 3);
			Position rookTargetPosition = new Position(source.getRow(), source.getCol() + 1);
			ChessPiece kingRook = (ChessPiece) board.removePiece(rookSourcePosition);
			board.placePiece(kingRook, rookTargetPosition);
			kingRook.increaseMoveCount();
		}
		
		//Special Move: Castling queen side rook
		if (p instanceof King && target.getCol() == source.getCol() - 2) {
			Position rookSourcePosition = new Position(source.getRow(), source.getCol() - 4);
			Position rookTargetPosition = new Position(source.getRow(), source.getCol() - 1);
			ChessPiece queenRook = (ChessPiece) board.removePiece(rookSourcePosition);
			board.placePiece(queenRook, rookTargetPosition);
			queenRook.increaseMoveCount();
		}
		
		//Special Move: En Passant
		if (p instanceof Pawn) {
			if (source.getCol() != target.getCol() && capturedPiece == null) {
				Position capturedPawnPosition;
				if (p.getColor() == Color.WHITE) {
					capturedPawnPosition = new Position(target.getRow() + 1, target.getCol());
				} else {
					capturedPawnPosition = new Position(target.getRow() - 1, target.getCol());
				}
				
				capturedPiece = board.removePiece(capturedPawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
				
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		
		//Special Move: Castling king side rook
		if (p instanceof King && target.getCol() == source.getCol() + 2) {
			Position rookSourcePosition = new Position(source.getRow(), source.getCol() + 3);
			Position rookTargetPosition = new Position(source.getRow(), source.getCol() + 1);
			ChessPiece kingRook = (ChessPiece) board.removePiece(rookTargetPosition);
			board.placePiece(kingRook, rookSourcePosition);
			kingRook.decreaseMoveCount();
		}
		
		//Special Move: Castling queen side rook
		if (p instanceof King && target.getCol() == source.getCol() - 2) {
			Position rookSourcePosition = new Position(source.getRow(), source.getCol() - 4);
			Position rookTargetPosition = new Position(source.getRow(), source.getCol() - 1);
			ChessPiece queenRook = (ChessPiece) board.removePiece(rookTargetPosition);
			board.placePiece(queenRook, rookSourcePosition);
			queenRook.decreaseMoveCount();
		}
		
		//Special Move: En Passant
		if (p instanceof Pawn) {
			if (source.getCol() != target.getCol() && capturedPiece == enPassantVulnerable) {
				
				ChessPiece pawn = (ChessPiece) board.removePiece(target);
				Position capturedPawnPosition;
				
				if (p.getColor() == Color.WHITE) {
					capturedPawnPosition = new Position(3, target.getCol());
				} else {
					capturedPawnPosition = new Position(4, target.getCol());
				}
				board.placePiece(pawn, capturedPawnPosition);				
			}
		}
		
		
	}
	
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position.");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible move for the selected piece");
		}
		if (((ChessPiece) board.piece(position)).getColor() != currentPlayer) {
			throw new ChessException("The selected piece is not from the current player.");
		}
	}
	
	private void validateTargetPosition(Position position, Position target) {
		if (!board.piece(position).possibleMove(target)) {
			throw new ChessException("The selected piece can't move to target position.");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.BLACK) ? Color.WHITE : Color.BLACK;
	}
	
	private ChessPiece king (Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("Ther is no " + color + "king on the board.");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean mat[][] = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getCol()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		} else {
			List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
			for (Piece p : list) {
				boolean[][] mat = p.possibleMoves();
				for (int i=0; i<mat.length; i++) {
					for (int j=0; j<mat.length; j++) {
						if (mat[i][j]) {
							Position source = (((ChessPiece) p).getChessPosition()).toPosition();
							Position target = new Position(i, j);
							Piece capturedPiece = makeMove(source, target);
							boolean testCheck = testCheck(color);
							undoMove(source, target, capturedPiece);
							if (!testCheck) {
								return false;
							}
						}
					}
				}
			}
			return true;
		}
	}
	
	private void placeNewPiece(char col, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(col, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private void initialSetup() {
		
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
		
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
		
	}
	
}
