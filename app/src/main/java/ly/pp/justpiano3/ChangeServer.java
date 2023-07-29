package ly.pp.justpiano3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeServer extends BaseActivity implements OnClickListener {
    public JPApplication jpapplication;
    private SharedPreferences sharedPreferences;
    private EditText et;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra("no_auto", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.server1:
                SharedPreferences.Editor edit2 = sharedPreferences.edit();
                edit2.putString("ip", JPApplication.ONLINE_SERVER_URL);
                edit2.apply();
                jpapplication.setServer(JPApplication.ONLINE_SERVER_URL);
                Intent intent = new Intent();
                intent.putExtra("no_auto", false);
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            case R.id.server2:
                edit2 = sharedPreferences.edit();
                edit2.putString("ip", "test.justpiano.fun");
                edit2.commit();
                jpapplication.setServer("test.justpiano.fun");
                intent = new Intent();
                intent.putExtra("no_auto", false);
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ip_confirm:
                edit2 = sharedPreferences.edit();
                String ip = et.getText().toString().trim();
                edit2.putString("ip", ip);
                edit2.putString("ipText", ip);
                edit2.commit();
                jpapplication.setServer(ip);
                intent = new Intent();
                intent.putExtra("no_auto", false);
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.changeserver);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        Button f4189o = findViewById(R.id.server1);
        f4189o.setOnClickListener(this);
        Button f4192r = findViewById(R.id.server2);
        f4192r.setOnClickListener(this);
        et = findViewById(R.id.server_ip);
        et.setText(sharedPreferences.getString("ipText", ""));
        Button confirm = findViewById(R.id.ip_confirm);
        confirm.setOnClickListener(this);
    }
}
