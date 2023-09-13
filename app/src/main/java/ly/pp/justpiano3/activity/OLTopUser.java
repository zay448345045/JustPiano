package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        Button f4561l = findViewById(R.id.ol_level_b);
        f4561l.setOnClickListener(this);
        Button f4557h = findViewById(R.id.ol_topUsers_b);
        f4557h.setOnClickListener(this);
        Button f4558i = findViewById(R.id.ol_topScore_b);
        f4558i.setOnClickListener(this);
        Button f4559j = findViewById(R.id.ol_bless);
        f4559j.setOnClickListener(this);
        Button f4562m = findViewById(R.id.ol_class_b);
        f4562m.setOnClickListener(this);
        Button f4560k = findViewById(R.id.ol_contribution);
        f4560k.setOnClickListener(this);
    }
}
