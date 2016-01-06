package com.jcc.linkgame.board.impl;

import com.jcc.linkgame.board.AbstractBoard;
import com.jcc.linkgame.object.GameConf;
import com.jcc.linkgame.view.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juyuan on 1/5/2016.
 */
public class VerticalBoard extends AbstractBoard{
    @Override
    protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
        List<Piece> notNullPieces = new ArrayList<Piece>();
        for (int i=0;i<pieces.length;i++){
            for(int j=0;j<pieces[i].length;j++){
                if (i % 2 == 0){
                    Piece piece = new Piece(i, j);
                    notNullPieces.add(piece);
                }
            }
        }
        return notNullPieces;
    }
}
