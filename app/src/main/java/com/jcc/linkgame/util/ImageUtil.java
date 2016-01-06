package com.jcc.linkgame.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jcc.linkgame.R;
import com.jcc.linkgame.view.PieceImage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by juyuan on 1/5/2016.
 */
public class ImageUtil {
    //获取连连看所有图片的ID（约定所有图片ID以p_开头）
    private static List<Integer> imageValues = getImageValues();
    public static List<Integer> getImageValues(){
        try{
            Field[] drawableFields = R.drawable.class.getFields();
            List<Integer> resourceValues = new ArrayList<Integer>();
            for(Field field : drawableFields){
                if(field.getName().startsWith("p_")){
                    resourceValues.add(field.getInt(R.drawable.class));
                }
            }
            return resourceValues;
        }catch (Exception e){
            return null;
        }
    }

    //随机从sourceValues的集合中获取size个图片ID，返回结果为图片ID的集合
    public static List<Integer> getRandomValues(List<Integer> sourceValues, int size){
        Random random = new Random();
        List<Integer> result = new ArrayList<Integer>();
        for(int i=0; i<size; i++){
            try{
                int index = random.nextInt(sourceValues.size());
                Integer image = sourceValues.get(index);
                result.add(image);
            }catch (IndexOutOfBoundsException e){
                return result;
            }
        }
        return result;
    }

    //从drawable目录中获取size个图片资源ID，其中size为游戏数量
    public static List<Integer> getPlayValues(int size){
        if(size % 2 != 0){
            size += 1;
        }

        List<Integer> playImageValues = getRandomValues(imageValues, size / 2);
        playImageValues.addAll(playImageValues);
        Collections.shuffle(playImageValues);
        return playImageValues;
    }

    //将图片ID集合转换为PieceImage集合， PieceImage封装了图片ID与图片本身
    public static List<PieceImage> getPlayImages(Context context, int size){
        List<Integer> resourceValues = getPlayValues(size);
        List<PieceImage> result = new ArrayList<PieceImage>();
        for (Integer value : resourceValues){
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), value);
            if (bm == null) Log.e("LinkGame", "Image Bitmap is NULL: " + String.format("0x%08X", value));
            Log.d("LinkGame", "image Size = " + bm.getWidth() + "x" + bm.getHeight());
            PieceImage pieceImage = new PieceImage(bm, value);
            result.add(pieceImage);
        }
        return result;
    }

    //获取选中标识的图片
    public static Bitmap getSelectImage(Context context){
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.selected);
        return bm;
    }
}
