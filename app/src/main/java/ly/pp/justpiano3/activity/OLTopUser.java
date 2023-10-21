package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class OLTopUser extends Activity implements OnClickListener {

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
        String f4568s = "";
        intent.putExtra("address", f4568s);
        String f4567r = "C";
        switch (view.getId()) {
            case R.id.ol_class_b:
                intent.putExtra("head", 10);
                intent.putExtra("keywords", f4567r);
                intent.setClass(this, ShowTopInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_level_b:
                intent.putExtra("head", 4);
                intent.putExtra("keywords", f4567r);
                intent.setClass(this, ShowTopInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_topUsers_b:
                intent.putExtra("head", 0);
                intent.putExtra("keywords", f4567r);
                intent.setClass(this, ShowTopInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_topScore_b:
                intent.putExtra("head", 1);
                intent.putExtra("keywords", f4567r);
                intent.setClass(this, ShowTopInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_bless:
                intent.putExtra("head", 7);
                intent.putExtra("keywords", f4567r);
                intent.setClass(this, ShowTopInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_contribution:
                intent.putExtra("head", 9);
                intent.putExtra("keywords", f4567r);
                intent.setClass(this, ShowTopInfo.class);
                startActivity(intent);
                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_top_user);
        ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
        findViewById(R.id.ol_level_b).setOnClickListener(this);
        findViewById(R.id.ol_topUsers_b).setOnClickListener(this);
        findViewById(R.id.ol_topScore_b).setOnClickListener(this);
        findViewById(R.id.ol_bless).setOnClickListener(this);
        findViewById(R.id.ol_class_b).setOnClickListener(this);
        findViewById(R.id.ol_contribution).setOnClickListener(this);
    }
}
