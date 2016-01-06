package com.jcc.linkgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jcc.linkgame.board.GameService;
import com.jcc.linkgame.board.impl.GameServiceImpl;
import com.jcc.linkgame.object.GameConf;
import com.jcc.linkgame.object.LinkInfo;
import com.jcc.linkgame.view.GameView;
import com.jcc.linkgame.view.Piece;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private GameConf config;
    private GameService gameService;

    private GameView gameView;
    private Button startButton;
    private TextView timeTextView;

    private AlertDialog.Builder lostDialog;
    private AlertDialog.Builder successDialog;

    private Timer timer = new Timer();
    private int gameTime;
    private boolean isPlaying;

    SoundPool soundPool = buildSoundPool();
//    new SoundPool(2, AudioManager.STREAM_SYSTEM, 8);
    int dis;
    private Piece selected = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x123:
                    timeTextView.setText("剩余时间：" + gameTime);
                    gameTime--;
                    if (gameTime < 0){
                        stopTimer();
                        isPlaying = false;
                        lostDialog.show();
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private SoundPool buildSoundPool(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_SYSTEM).build())
                    .build();
        } else {
            return new SoundPool(2, AudioManager.STREAM_SYSTEM, 8);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (isPlaying){
            startGame(gameTime);
        }
        super.onResume();
    }

    private void init(){
        config = new GameConf(8, 9, 2, 10, 100000, this);
        gameView = (GameView)findViewById(R.id.gameView);
        timeTextView = (TextView)findViewById(R.id.timeText);
        startButton = (Button)findViewById(R.id.startButton);
        dis = soundPool.load(this, R.raw.dis, 1);
        gameService = new GameServiceImpl(this.config);
        gameView.setGameService(gameService);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(GameConf.DEFAULT_TIME);
            }
        });
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isPlaying) {
                    return false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameViewTouchDown(motionEvent);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gameViewTouchUp(motionEvent);
                }
                return true;
            }
        });
        lostDialog = createDialog("Lost", "游戏失败！重新开始", R.drawable.lost)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame(GameConf.DEFAULT_TIME);
                    }
                });
        successDialog = createDialog("Success", "游戏胜利！重新开始", R.drawable.success)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame(GameConf.DEFAULT_TIME);
                    }
                });
    }

    private AlertDialog.Builder createDialog(String title, String message, int imageResource){
        return new AlertDialog.Builder(this).setTitle(title).setMessage(message).setIcon(imageResource);
    }
    private void stopTimer(){
        this.timer.cancel();
        this.timer = null;
    }

    private void gameViewTouchDown(MotionEvent event){
        Piece[][] pieces = gameService.getPieces();
        float touchX = event.getX();
        float touchY = event.getY();
        Log.d("LinkGame", "Touch at " + touchX + "x" + touchY);
        //根据用户触碰的坐标得到对应的Piece对象
        Piece currentPiece = gameService.findPiece(touchX, touchY);
        if (currentPiece == null)
            return;
        this.gameView.setSelectedPiece(currentPiece);
        //之前没有选中任何一个Piece
        if (this.selected == null){
            this.selected = currentPiece;
            this.gameView.postInvalidate();
            return;
        }
        //之前已经选择了一个
        if (this.selected != null){
            //对currentPiece和prePiece进行判断并进行连接
            LinkInfo linkInfo = this.gameService.link(this.selected, currentPiece);
            if (linkInfo == null){ //两个piece不可连
                this.selected = currentPiece;
                this.gameView.postInvalidate();
            } else { //可以成功连接
                handleSuccessLink(linkInfo, this.selected, currentPiece, pieces);
            }
        }
    }

    private void gameViewTouchUp(MotionEvent e){
        this.gameView.postInvalidate();
    }

    //以gameTime作为剩余时间开始或回复游戏
    private void startGame(int gameTime){
        if (this.timer != null){
            stopTimer();
        }
        this.gameTime = gameTime;
        if (gameTime == GameConf.DEFAULT_TIME){
            gameView.startGame();
        }
        isPlaying = true;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 1000);
        this.selected = null;
    }

    //成功连接后处理
    private void handleSuccessLink(LinkInfo linkInfo, Piece prePiece, Piece currentPiece, Piece[][] pieces){
        this.gameView.setLinkInfo(linkInfo);
        this.gameView.setSelectedPiece(null);
        this.gameView.postInvalidate();

        pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
        pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;
        this.selected = null;

        soundPool.play(dis, 1, 1, 0, 0, 1);

        if (!this.gameService.hasPieces()){
            this.successDialog.show();
            stopTimer();
            isPlaying = false;
        }
    }
}
