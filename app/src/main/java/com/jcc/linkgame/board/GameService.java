package com.jcc.linkgame.board;

import com.jcc.linkgame.object.LinkInfo;
import com.jcc.linkgame.view.Piece;

/**
 * Created by juyuan on 1/6/2016.
 */
public interface GameService {
    void start();

    Piece[][] getPieces();

    boolean hasPieces();

    Piece findPiece(float touchX, float touchY);

    LinkInfo link(Piece p1, Piece p2);
}
