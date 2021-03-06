package raaidsm.spring.test.models.pieces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.moves_and_attacks.AttackDir;
import raaidsm.spring.test.models.moves_and_attacks.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.piece_properties.PieceType;
import raaidsm.spring.test.models.moves_and_attacks.MoveCalcResultStruct;
import raaidsm.spring.test.models.square_properties.SquarePreviewStruct;
import raaidsm.spring.test.models.square_properties.SqrStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {
    private final Logger logger = LoggerFactory.getLogger(Pawn.class);
    private boolean hasInitialPawnMove = true;

    public Pawn() {}
    public Pawn(PieceType name, Colour colour, String location) {
        super(name, colour, location);
        String[] promotionPieces = { "queen", "rook", "bishop", "knight" };
        promotion.addAll(Arrays.asList(promotionPieces));
    }

    public void removeInitialPawnMove() {
        hasInitialPawnMove = false;
    }

    @Override
    protected List<MoveCalcResultStruct> calculateSquarePreviewResults() {
        List<MoveCalcResultStruct> results = new ArrayList<>();
        MoveCalcResultStruct up1Result = up1();
        MoveCalcResultStruct up2Result = up2();
        MoveCalcResultStruct upCaptureResult1 = upCapture(-1, AttackDir.DIAGONAL_DESCENDING);
        MoveCalcResultStruct upCaptureResult2 = upCapture(1, AttackDir.DIAGONAL_ASCENDING);
        if (up1Result != null) results.add(up1Result);
        if (up2Result != null) results.add(up2Result);
        if (upCaptureResult1 != null) results.add(upCaptureResult1);
        if (upCaptureResult2 != null) results.add(upCaptureResult2);
        return results;
    }
    private MoveCalcResultStruct up1() {
        //OVERVIEW: ONLY_MOVE, VERTICAL
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //AttackType and AttackDir for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.ONLY_MOVE;
        AttackDir attackDir = AttackDir.VERTICAL;
        SquarePreviewStruct preview = previewRelativeSquare(0, directionByColour);
        SqrStat status = preview.squareStatus;
        String squareName = preview.squareName;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return null;
        //Guard clause for there being a piece in the way
        if (status != SqrStat.EMPTY) return null;
        //Square is free to move to
        return new MoveCalcResultStruct(null, squareName, attackType, attackDir);
    }
    private MoveCalcResultStruct up2() {
        //OVERVIEW: ONLY_MOVE, VERTICAL
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //AttackType and AttackDir for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.ONLY_MOVE;
        AttackDir attackDir = AttackDir.VERTICAL;
        String squareName = null;
        //Guard clause for not having initial pawn move to make
        if (!hasInitialPawnMove) return null;
        for (int i = 1; i <= 2; i++) {
            SquarePreviewStruct preview = previewRelativeSquare(0, i * directionByColour);
            SqrStat status = preview.squareStatus;
            squareName = preview.squareName;
            //Guard clause for relative point going off the board
            if (status == SqrStat.NO_SQUARE) return null;
            //Guard clause for there being a piece in the way
            if (status != SqrStat.EMPTY) return null;
        }
        //Both squares above are open so up2 is valid
        return new MoveCalcResultStruct(null, squareName, attackType, attackDir);
    }
    private MoveCalcResultStruct upCapture(int direction, AttackDir attackDir) {
        //OVERVIEW: ONLY_CAPTURE, DIAGONAL_DESCENDING/DIAGONAL_ASCENDING
        assert direction == 1 || direction == -1;
        //This variable inverts "left" and "right" for black pieces
        int directionByColour = colour == Colour.WHITE ? 1 : -1;
        //AttackType and AttackDir for this collection of attacks (yes, collection even though there's only one)
        AttackType attackType = AttackType.ONLY_CAPTURE;
        SquarePreviewStruct preview = previewRelativeSquare(direction * directionByColour, directionByColour);
        SqrStat status = preview.squareStatus;
        String squareName = preview.squareName;
        Piece piece = preview.piece;
        //Guard clause for relative point going off the board
        if (status == SqrStat.NO_SQUARE) return null;
        //Square has a shadow pawn that can be captured
        if (preview.shadowPawn != null) {
            return new MoveCalcResultStruct(null, squareName, attackType, attackDir);
        }
        //Guard clause for there being no piece to capture
        if (status == SqrStat.EMPTY) {
            return new MoveCalcResultStruct(null, squareName, attackType, attackDir, false);
        }
        //Square has a same-coloured piece that can't be captured
        if (colour == preview.pieceColour) {
            return new MoveCalcResultStruct(null, squareName, attackType, attackDir, false);
        }
        //Square has an enemy piece to capture at this square
        if (status == SqrStat.KING) return new MoveCalcResultStruct((King)piece, squareName, attackType, attackDir);
        return new MoveCalcResultStruct(null, squareName, attackType, attackDir);
    }
}