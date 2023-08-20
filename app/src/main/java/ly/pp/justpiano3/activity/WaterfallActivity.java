package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.KeyboardModeView;
import ly.pp.justpiano3.view.WaterfallView;
import ly.pp.justpiano3.view.play.PmFileParser;

public class WaterfallActivity extends Activity {

    /**
     * pm文件解析器
     */
    private PmFileParser pmFileParser;

    /**
     * 钢琴键盘view
     */
    private KeyboardModeView keyboardModeView;

    /**
     * 瀑布流view
     */
    private WaterfallView waterfallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waterfall);
        Bundle bundle = getIntent().getExtras();
        String songPath = bundle.getString("songPath");
        pmFileParser = new PmFileParser(this, songPath);
        keyboardModeView = findViewById(R.id.waterfall_keyboard);
        waterfallView = findViewById(R.id.waterfall_view);
        waterfallView.setNoteFallListener(new WaterfallView.NoteFallListener() {
            @Override
            public void onNoteFallDown(int pitch, int volume) {
                SoundEngineUtil.playSound(pitch, volume);
                keyboardModeView.fireKeyDown(pitch, volume, KeyboardModeView.isBlackKey(pitch) ? 2 : 15, false);
            }

            @Override
            public void onNoteLeave(int pitch, int volume) {
                keyboardModeView.fireKeyUp(pitch, false);
            }
        });
        TextView songNameView = findViewById(R.id.waterfall_song_name);
        songNameView.setText(pmFileParser.getSongName());
    }
}