package com.jcc.linkgame.board;

import android.util.Log;

import com.jcc.linkgame.object.GameConf;
import com.jcc.linkgame.util.ImageUtil;
import com.jcc.linkgame.view.Piece;
import com.jcc.linkgame.view.PieceImage;

import java.util.List;

/**
 * Created by juyuan on 1/5/2016.
 */
public abstract class AbstractBoard {
    protected abstract List<Piece> createPieces(GameConf config, Piece[][] pieces);

    public Piece[][] create(GameConf config){
        Piece[][] pieces = new Piece[config.getXSize()][config.getYSize()];
        List<Piece> notNullPieces = createPieces(config, pieces);
        List<PieceImage> playImages = ImageUtil.getPlayImages(config.getContext(), notNullPieces.size());
        int imageWidth = playImages.get(0).getImage().getWidth();
        int imageHeight = playImages.get(0).getImage().getHeight();
        Log.d("LinkGame", "imageWidth x imageHeight = " + imageWidth + "x" + imageHeight);
        GameConf.PIECE_WIDTH = imageWidth;
        GameConf.PIECE_HEIGHT = imageHeight;
        for(int i=0; i<notNullPieces.size();i++){
            Piece piece = notNullPieces.get(i);
            piece.setImage(playImages.get(i));
            piece.setBeginX(piece.getIndexX() * imageWidth + config.getBeginImageX());
            piece.setBeginY(piece.getIndexY() * imageHeight + config.getBeginImageY());
            pieces[piece.getIndexX()][piece.getIndexY()] = piece;
        }
        return pieces;
    }
}
