package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.view.play.PmFileParser;

public class WaterfallActivity extends Activity {

    /**
     * pm文件解析起
     */
    private PmFileParser pmFileParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waterfall);
        Bundle bundle = getIntent().getExtras();
        String songPath = bundle.getString("songPath");
        pmFileParser = new PmFileParser(this, songPath);
        TextView songNameView = findViewById(R.id.waterfall_song_name);
        songNameView.setText(pmFileParser.getSongName());
    }
}