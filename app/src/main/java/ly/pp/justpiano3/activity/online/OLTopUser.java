package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;

public final class OLTopUser extends BaseActivity implements OnClickListener {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, OLMainMode.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("address", "");
        int id = view.getId();
        if (id == R.id.ol_class_b) {
            intent.putExtra("head", 10);
            intent.putExtra("keywords", "C");
            intent.setClass(this, ShowTopInfo.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_level_b) {
            intent.putExtra("head", 4);
            intent.putExtra("keywords", "C");
            intent.setClass(this, ShowTopInfo.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_topUsers_b) {
            intent.putExtra("head", 0);
            intent.putExtra("keywords", "C");
            intent.setClass(this, ShowTopInfo.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_topScore_b) {
            intent.putExtra("head", 1);
            intent.putExtra("keywords", "C");
            intent.setClass(this, ShowTopInfo.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_bless) {
            intent.putExtra("head", 7);
            intent.putExtra("keywords", "C");
            intent.setClass(this, ShowTopInfo.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_contribution) {
            intent.putExtra("head", 9);
            intent.putExtra("keywords", "C");
            intent.setClass(this, ShowTopInfo.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_top_user);
        findViewById(R.id.ol_level_b).setOnClickListener(this);
        findViewById(R.id.ol_topUsers_b).setOnClickListener(this);
        findViewById(R.id.ol_topScore_b).setOnClickListener(this);
        findViewById(R.id.ol_bless).setOnClickListener(this);
        findViewById(R.id.ol_class_b).setOnClickListener(this);
        findViewById(R.id.ol_contribution).setOnClickListener(this);
    }
}
