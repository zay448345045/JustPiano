package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.task.PlayFinishTask;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.ShareUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.view.JPProgressBar;

import java.util.Collections;
import java.util.List;

public class PlayFinish extends Activity implements OnClickListener {
    private int head;
    public JPApplication jpapplication;
    public String scoreArray = "";
    public JPProgressBar jpprogressBar;
    public String name = "";
    public String songID;
    private int comboScore;
    private int perfect = 0;
    private int cool = 0;
    private int great = 0;
    private int bad = 0;
    private int miss = 0;
    private int clickNum = 0;
    private int combo = 0;
    private double nandu;
    private double leftNandu;
    private int hand;
    private int songsTime;
    private String path = "";
    private ImageButton finishButton;
    private ImageButton retryButton;
    private Button shareButton;
    private boolean isWinner = false;
    private int topScore;
    private int totalScore;
    private int perfectScore;
    private int coolScore;
    private int greatScore;
    private int badScore;
    private int missScore;

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PianoPlay.class);
        int i = Math.max(topScore, totalScore);
        if (view == finishButton) {
            OLMelodySelect.songBytes = null;
            setResult(-1, intent);
            finish();
        } else if (view == retryButton) {
            switch (head) {
                case 0:
                    setResult(-1, intent);
                    intent = new Intent();
                    intent.setClass(this, PianoPlay.class);
                    intent.putExtra("head", head);
                    intent.putExtra("hand", hand);
                    intent.putExtra("name", name);
                    intent.putExtra("path", path);
                    intent.putExtra("nandu", nandu);
                    intent.putExtra("leftnandu", leftNandu);
                    intent.putExtra("songstime", songsTime);
                    intent.putExtra("isrecord", false);
                    intent.putExtra("score", i);
                    startActivity(intent);
                    finish();
                    return;
                case 1:
                    if (OLMelodySelect.songBytes != null) {
                        setResult(-1, intent);
                        intent = new Intent();
                        intent.setClass(this, PianoPlay.class);
                        intent.putExtra("head", head);
                        intent.putExtra("songName", name);
                        intent.putExtra("degree", nandu);
                        intent.putExtra("songBytes", OLMelodySelect.songBytes);
                        intent.putExtra("songID", songID);
                        intent.putExtra("topScore", i);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    return;
                default:
            }
        } else if (view == shareButton) {
            ShareUtil.share(this);
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        nandu = 0;
        jpprogressBar = new JPProgressBar(this);
        Bundle extras = getIntent().getExtras();
        int i = extras.getInt("head");
        head = i;
        switch (i) {
            case 0:
                perfect = extras.getInt("perf");
                cool = extras.getInt("cool");
                great = extras.getInt("great");
                bad = extras.getInt("bad");
                miss = extras.getInt("miss");
                name = extras.getString("name");
                path = extras.getString("path");
                nandu = extras.getDouble("nandu");
                leftNandu = extras.getDouble("leftnandu");
                songsTime = extras.getInt("songstime");
                hand = extras.getInt("hand");
                combo = extras.getInt("top_combo");
                comboScore = extras.getInt("combo_scr");
                perfectScore = perfect * 10;
                coolScore = cool * 8;
                greatScore = great * 5;
                badScore = bad * 2;
                missScore = miss * -5;
                totalScore = extras.getInt("totalScore");
                clickNum = perfect + cool + great + bad + miss;
                List<Song> songByPath = JPApplication.getSongDatabase().songDao().getSongByFilePath(path);
                for (Song song : songByPath) {
                    topScore = hand == 0 ? song.getRightHandHighScore() : song.getLeftHandHighScore();
                    if (topScore <= totalScore) {
                        isWinner = true;
                        if (hand == 0) {
                            song.setRightHandHighScore(totalScore);
                        } else {
                            song.setLeftHandHighScore(totalScore);
                        }
                        song.setHighScoreDate(System.currentTimeMillis());
                        song.setNew(0);
                        JPApplication.getSongDatabase().songDao().updateSongs(Collections.singletonList(song));
                    } else {
                        isWinner = false;
                    }
                }
                break;
            case 1:
                topScore = extras.getInt("topScore");
                perfect = extras.getInt("perf");
                cool = extras.getInt("cool");
                great = extras.getInt("great");
                bad = extras.getInt("bad");
                miss = extras.getInt("miss");
                name = extras.getString("name");
                songID = extras.getString("songID");
                path = extras.getString("path");
                nandu = extras.getDouble("nandu");
                combo = extras.getInt("top_combo");
                comboScore = extras.getInt("combo_scr");
                try {
                    byte[] scoreArrays = extras.getByteArray("scoreArray");
                    scoreArray = GZIPUtil.arrayToZIP(scoreArrays);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                totalScore = extras.getInt("totalScore");
                perfectScore = perfect * 10;
                coolScore = cool * 8;
                greatScore = great * 5;
                badScore = bad * 2;
                missScore = miss * -5;
                clickNum = perfect + cool + great + bad + miss;
                int score = perfectScore + coolScore + greatScore + badScore + missScore + comboScore;
                isWinner = totalScore >= topScore;
                if (score > 0) {
                    new PlayFinishTask(this).execute();
                }
                break;
        }
        setContentView(R.layout.finish);
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        finishButton = findViewById(R.id.ok);
        finishButton.setOnClickListener(this);
        retryButton = findViewById(R.id.retry);
        retryButton.setOnClickListener(this);
        shareButton = findViewById(R.id.share_score);
        shareButton.setOnClickListener(this);
        TextView perfectView = findViewById(R.id.perfect);
        perfectView.setText(String.valueOf(perfect));
        TextView coolView = findViewById(R.id.cool);
        coolView.setText(String.valueOf(cool));
        TextView greatView = findViewById(R.id.great);
        greatView.setText(String.valueOf(great));
        TextView badView = findViewById(R.id.bad);
        badView.setText(String.valueOf(bad));
        TextView missView = findViewById(R.id.miss);
        missView.setText(String.valueOf(miss));
        TextView totalView = findViewById(R.id.total);
        totalView.setText(String.valueOf(clickNum));
        TextView comboView = findViewById(R.id.combo);
        comboView.setText(String.valueOf(combo));
        TextView comboScoreView = findViewById(R.id.combo_scr);
        comboScoreView.setText(String.valueOf(comboScore));
        TextView perfectScoreView = findViewById(R.id.perfect_scr);
        perfectScoreView.setText(String.valueOf(perfectScore));
        TextView coolScoreView = findViewById(R.id.cool_scr);
        coolScoreView.setText(String.valueOf(coolScore));
        TextView greatScoreView = findViewById(R.id.great_scr);
        greatScoreView.setText(String.valueOf(greatScore));
        TextView badScoreView = findViewById(R.id.bad_scr);
        badScoreView.setText(String.valueOf(badScore));
        TextView missScoreView = findViewById(R.id.miss_scr);
        missScoreView.setText(String.valueOf(missScore));
        TextView totalScoreView = findViewById(R.id.total_scr);
        totalScoreView.setText(String.valueOf(totalScore));
        TextView highScoreView = findViewById(R.id.highscore);
        highScoreView.setText(String.valueOf(topScore));
        TextView titleScoreView = findViewById(R.id.titlescore);
        titleScoreView.setText(String.valueOf(totalScore));
        TextView reportView = findViewById(R.id.report);
        if (isWinner) {
            reportView.setText("恭喜您获得了《" + name + "》曲目的冠军!");
        } else {
            reportView.setText("很不幸，您差点就是《" + name + "》曲目的冠军了");
        }
    }
}
