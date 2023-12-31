package ly.pp.justpiano3.activity.local;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.task.PlayFinishTask;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.ShareUtil;
import ly.pp.justpiano3.view.JPProgressBar;

public final class PlayFinish extends BaseActivity implements OnClickListener {
    private int head;
    public String scoreArray = "";
    public JPProgressBar jpprogressBar;
    private String name = "";
    public String songID;
    private int comboScore;
    private int perfect;
    private int cool;
    private int great;
    private int bad;
    private int miss;
    private int clickNum;
    private int combo;
    private double nandu;
    private double leftNandu;
    private int hand;
    private int songsTime;
    private String path = "";
    private ImageButton finishButton;
    private ImageButton retryButton;
    private Button shareButton;
    private boolean isWinner;
    private int topScore;
    private int totalScore;
    private int perfectScore;
    private int coolScore;
    private int greatScore;
    private int badScore;
    private int missScore;

    @Override
    public void onBackPressed() {
        OLMelodySelect.songBytes = null;
        setResult(-1, new Intent(this, PianoPlay.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PianoPlay.class);
        int topScore = Math.max(this.topScore, totalScore);
        if (view == finishButton) {
            OLMelodySelect.songBytes = null;
            setResult(-1, intent);
            finish();
        } else if (view == retryButton) {
            switch (head) {
                case 0 -> {
                    setResult(-1, intent);
                    intent = new Intent(this, PianoPlay.class);
                    intent.putExtra("head", head);
                    intent.putExtra("hand", hand);
                    intent.putExtra("name", name);
                    intent.putExtra("path", path);
                    intent.putExtra("nandu", nandu);
                    intent.putExtra("leftnandu", leftNandu);
                    intent.putExtra("songstime", songsTime);
                    intent.putExtra("isrecord", false);
                    intent.putExtra("score", topScore);
                    startActivity(intent);
                    finish();
                }
                case 1 -> {
                    if (OLMelodySelect.songBytes != null) {
                        setResult(-1, intent);
                        intent = new Intent(this, PianoPlay.class);
                        intent.putExtra("head", head);
                        intent.putExtra("songName", name);
                        intent.putExtra("degree", nandu);
                        intent.putExtra("songBytes", OLMelodySelect.songBytes);
                        intent.putExtra("songID", songID);
                        intent.putExtra("topScore", topScore);
                        startActivity(intent);
                        finish();
                    }
                }
                default -> {
                }
            }
        } else if (view == shareButton) {
            ShareUtil.share(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jpprogressBar = new JPProgressBar(this);
        Bundle extras = getIntent().getExtras();
        head = extras.getInt("head");
        switch (head) {
            case 0 -> {
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
            }
            case 1 -> {
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
            }
        }
        setContentView(R.layout.play_finish);
        finishButton = findViewById(R.id.ok);
        finishButton.setOnClickListener(this);
        retryButton = findViewById(R.id.retry);
        retryButton.setOnClickListener(this);
        shareButton = findViewById(R.id.share_score);
        shareButton.setOnClickListener(this);
        ((TextView) findViewById(R.id.perfect)).setText(String.valueOf(perfect));
        ((TextView) findViewById(R.id.cool)).setText(String.valueOf(cool));
        ((TextView) findViewById(R.id.great)).setText(String.valueOf(great));
        ((TextView) findViewById(R.id.bad)).setText(String.valueOf(bad));
        ((TextView) findViewById(R.id.miss)).setText(String.valueOf(miss));
        ((TextView) findViewById(R.id.total)).setText(String.valueOf(clickNum));
        ((TextView) findViewById(R.id.combo)).setText(String.valueOf(combo));
        ((TextView) findViewById(R.id.combo_scr)).setText(String.valueOf(comboScore));
        ((TextView) findViewById(R.id.perfect_scr)).setText(String.valueOf(perfectScore));
        ((TextView) findViewById(R.id.cool_scr)).setText(String.valueOf(coolScore));
        ((TextView) findViewById(R.id.great_scr)).setText(String.valueOf(greatScore));
        ((TextView) findViewById(R.id.bad_scr)).setText(String.valueOf(badScore));
        ((TextView) findViewById(R.id.miss_scr)).setText(String.valueOf(missScore));
        ((TextView) findViewById(R.id.total_scr)).setText(String.valueOf(totalScore));
        ((TextView) findViewById(R.id.highscore)).setText(String.valueOf(topScore));
        ((TextView) findViewById(R.id.titlescore)).setText(String.valueOf(totalScore));
        ((TextView) findViewById(R.id.report)).setText(isWinner ? "恭喜您获得了《" + name + "》曲目的冠军!"
                : "很不幸，您差点就是《" + name + "》曲目的冠军了");
    }
}
