package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.about);
        TextView title = findViewById(R.id.title);
        title.setText("极品钢琴V" + BuildConfig.VERSION_NAME + " 浴火重生版");
    }
}
