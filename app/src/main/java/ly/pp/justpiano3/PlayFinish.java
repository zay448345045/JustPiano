package ly.pp.justpiano3;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.sql.Date;

public class PlayFinish extends Activity implements OnClickListener {
    private static int head;
    public JPApplication jpapplication;
    String scoreArray = "";
    JPProgressBar jpprogressBar;
    String name = "";
    String songID;
    private int comboScore;
    private int perfect = 0;
    private int cool = 0;
    private int great = 0;
    private int bad = 0;
    private int miss = 0;
    private int clickNum = 0;
    private int combo = 0;
    private double nandu;
    private String path = "";
    private ImageButton f4671o;
    private ImageButton f4672p;
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
        if (view == f4671o) {
            OLMelodySelect.f4294d = null;
            setResult(-1, intent);
            finish();
        } else if (view == f4672p) {
            switch (head) {
                case 0:
                    setResult(-1, intent);
                    intent = new Intent();
                    intent.setClass(this, PianoPlay.class);
                    intent.putExtra("head", head);
                    intent.putExtra("name", name);
                    intent.putExtra("path", path);
                    intent.putExtra("nandu", nandu);
                    intent.putExtra("score", i);
                    startActivity(intent);
                    finish();
                    return;
                case 1:
                    if (OLMelodySelect.f4294d != null) {
                        setResult(-1, intent);
                        intent = new Intent();
                        intent.setClass(this, PianoPlay.class);
                        intent.putExtra("head", head);
                        intent.putExtra("songName", name);
                        intent.putExtra("degree", nandu);
                        intent.putExtra("songBytes", OLMelodySelect.f4294d);
                        intent.putExtra("songID", OLMelodySelect.songID);
                        intent.putExtra("topScore", i);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    return;
                default:
            }
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
                combo = extras.getInt("top_combo");
                comboScore = extras.getInt("combo_scr");
                perfectScore = perfect * 10;
                coolScore = cool * 8;
                greatScore = great * 5;
                badScore = bad * 2;
                missScore = miss * -5;
                totalScore = extras.getInt("totalScore");
                Date f4652P = new Date(System.currentTimeMillis());
                clickNum = perfect + cool + great + bad + miss;
                SQLiteDatabase writableDatabase = new TestSQL(this, "data").getWritableDatabase();
                Cursor query = writableDatabase.query("jp_data", new String[]{"_id", "name", "score", "date", "isnew"}, "name='" + name.replace("'", "''") + "'", null, null, null, null);
                while (query.moveToNext()) {
                    topScore = query.getInt(query.getColumnIndexOrThrow("score"));
                }
                if (topScore <= totalScore) {
                    isWinner = true;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("score", totalScore);
                    contentValues.put("date", String.valueOf(f4652P));
                    contentValues.put("isnew", 0);
                    writableDatabase.update("jp_data", contentValues, "name='" + name.replace("'", "''") + "'", null);
                    contentValues.clear();
                } else {
                    isWinner = false;
                }
                try {
                    query.close();
                } catch (Exception e) {
                    break;
                }
                writableDatabase.close();
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
                    scoreArray = GZIP.arrayToZIP(extras.getByteArray("scoreArray"));
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
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        f4671o = findViewById(R.id.ok);
        f4671o.setOnClickListener(this);
        f4672p = findViewById(R.id.retry);
        f4672p.setOnClickListener(this);
        Button f4654R = findViewById(R.id.share_score);
        f4654R.setOnClickListener(this);
        TextView f4681y = findViewById(R.id.perfect);
        f4681y.setText(String.valueOf(perfect));
        TextView f4682z = findViewById(R.id.cool);
        f4682z.setText(String.valueOf(cool));
        TextView f4637A = findViewById(R.id.great);
        f4637A.setText(String.valueOf(great));
        TextView f4638B = findViewById(R.id.bad);
        f4638B.setText(String.valueOf(bad));
        TextView f4639C = findViewById(R.id.miss);
        f4639C.setText(String.valueOf(miss));
        TextView f4640D = findViewById(R.id.total);
        f4640D.setText(String.valueOf(clickNum));
        TextView f4641E = findViewById(R.id.combo);
        f4641E.setText(String.valueOf(combo));
        TextView f4642F = findViewById(R.id.combo_scr);
        f4642F.setText(String.valueOf(comboScore));
        TextView f4643G = findViewById(R.id.perfect_scr);
        f4643G.setText(String.valueOf(perfectScore));
        TextView f4644H = findViewById(R.id.cool_scr);
        f4644H.setText(String.valueOf(coolScore));
        TextView f4645I = findViewById(R.id.great_scr);
        f4645I.setText(String.valueOf(greatScore));
        TextView f4646J = findViewById(R.id.bad_scr);
        f4646J.setText(String.valueOf(badScore));
        TextView f4647K = findViewById(R.id.miss_scr);
        f4647K.setText(String.valueOf(missScore));
        TextView f4648L = findViewById(R.id.total_scr);
        f4648L.setText(String.valueOf(totalScore));
        TextView f4649M = findViewById(R.id.highscore);
        f4649M.setText(String.valueOf(topScore));
        TextView f4650N = findViewById(R.id.totlescore);
        f4650N.setText(String.valueOf(totalScore));
        TextView f4651O = findViewById(R.id.report);
        if (isWinner) {
            f4651O.setText("恭喜您获得了<" + name + ">曲目的冠军!");
        } else {
            f4651O.setText("很不幸,你差点就是<" + name + ">曲目的冠军了。");
        }
    }
}
