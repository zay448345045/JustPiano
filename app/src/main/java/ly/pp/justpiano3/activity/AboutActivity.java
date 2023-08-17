package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.about);
        TextView title = findViewById(R.id.title);
        JPApplication jpApplication = (JPApplication) getApplication();
        title.setText("极品钢琴V" + jpApplication.getVersion() + " 浴火重生版");
    }
}
