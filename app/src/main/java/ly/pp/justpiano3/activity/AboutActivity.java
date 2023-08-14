package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.os.Bundle;
import ly.pp.justpiano3.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(3);
        setContentView(R.layout.about);
    }
}
