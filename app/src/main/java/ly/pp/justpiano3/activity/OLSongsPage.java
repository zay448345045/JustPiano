package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.GameModeEnum;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class OLSongsPage extends Activity implements OnClickListener {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ol_melodyList_b:
                intent.setClass(this, OLMelodySelect.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_hotSongs_b:
                intent.putExtra("head", "1");
                intent.putExtra("keywords", "H");
                intent.setClass(this, ShowSongsInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_newSongs_b:
                intent.putExtra("head", "1");
                intent.putExtra("keywords", "N");
                intent.setClass(this, ShowSongsInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_favor:
                intent.putExtra("head", "1");
                intent.putExtra("keywords", "F");
                intent.setClass(this, ShowSongsInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_test_b:
                intent.putExtra("head", "1");
                intent.putExtra("keywords", "M");
                intent.setClass(this, ShowSongsInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_search_b:
                intent.putExtra("head", 0);
                intent.setClass(this, SearchSongs.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_recent_b:
                intent.putExtra("head", "1");
                intent.putExtra("keywords", "T");
                intent.setClass(this, ShowSongsInfo.class);
                startActivity(intent);
                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPApplication jpApplication = (JPApplication) getApplication();
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.ol_songs_page);
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        jpApplication.setGameMode(GameModeEnum.NORMAL.getCode());
        Button f4544b = findViewById(R.id.ol_melodyList_b);
        f4544b.setOnClickListener(this);
        Button f4545c = findViewById(R.id.ol_hotSongs_b);
        f4545c.setOnClickListener(this);
        Button f4546d = findViewById(R.id.ol_search_b);
        f4546d.setOnClickListener(this);
        Button f4548f = findViewById(R.id.ol_newSongs_b);
        f4548f.setOnClickListener(this);
        Button f4547e = findViewById(R.id.ol_favor);
        f4547e.setOnClickListener(this);
        Button f4548e = findViewById(R.id.ol_test_b);
        f4548e.setOnClickListener(this);
        View f4549g = findViewById(R.id.ol_recent_b);
        f4549g.setOnClickListener(this);
    }
}
