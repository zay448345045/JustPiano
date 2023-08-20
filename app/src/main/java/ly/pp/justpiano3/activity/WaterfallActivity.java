package ly.pp.justpiano3.activity;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.R;

public class WaterfallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String songName = bundle.getString("songName");
        String songPath = bundle.getString("songPath");
        setContentView(R.layout.waterfall);
    }
}