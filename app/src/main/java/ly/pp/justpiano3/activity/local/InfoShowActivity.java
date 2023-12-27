package ly.pp.justpiano3.activity.local;

import android.os.Bundle;
import android.widget.TextView;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;

public final class InfoShowActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int title = bundle.getInt("title", -1);
            if (title != -1) {
                ((TextView) findViewById(R.id.show_title)).setText(title);
            }
            int info = bundle.getInt("info", -1);
            if (info != -1) {
                ((TextView) findViewById(R.id.show_info)).setText(info);
            }
        }
    }
}
