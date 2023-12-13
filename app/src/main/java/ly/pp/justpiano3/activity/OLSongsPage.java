package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class OLSongsPage extends BaseActivity implements OnClickListener {

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
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.ol_songs_page);
        ImageLoadUtil.setBackground(this);
        GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
        findViewById(R.id.ol_melodyList_b).setOnClickListener(this);
        findViewById(R.id.ol_hotSongs_b).setOnClickListener(this);
        findViewById(R.id.ol_search_b).setOnClickListener(this);
        findViewById(R.id.ol_newSongs_b).setOnClickListener(this);
        findViewById(R.id.ol_favor).setOnClickListener(this);
        findViewById(R.id.ol_test_b).setOnClickListener(this);
        findViewById(R.id.ol_recent_b).setOnClickListener(this);
    }
}
