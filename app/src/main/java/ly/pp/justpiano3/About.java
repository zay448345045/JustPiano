package ly.pp.justpiano3;

import android.app.Activity;
import android.os.Bundle;

public class About extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(3);
        setContentView(R.layout.about);
    }
}
