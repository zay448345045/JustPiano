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
        intent.setClass(this, Login.class);
        intent.putExtra("no_auto", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sfyserv:
                SharedPreferences.Editor edit2 = sharedPreferences.edit();
                edit2.putString("ip", "111.67.204.158");
                edit2.apply();
                jpapplication.setServer("111.67.204.158");
                Intent intent = new Intent();
                intent.putExtra("no_auto", false);
                intent.setClass(this, Login.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ouneiserv:
                edit2 = sharedPreferences.edit();
                edit2.putString("ip", "124.76.172.41");
                edit2.commit();
                jpapplication.setServer("124.76.172.41");
                intent = new Intent();
                intent.putExtra("no_auto", false);
                intent.setClass(this, Login.class);
                startActivity(intent);
                finish();
                return;
            case R.id.guanfangserv:
                JPDialog jpDialog = new JPDialog(this);
                jpDialog.setTitle("抱歉");
                jpDialog.setMessage("此版本及后续版本将不再提供对官方服的访问支持!");
                jpDialog.setFirstButton("确定", new DialogDismissClick());
                jpDialog.showDialog();
                return;
            case R.id.ip_confirm:
                edit2 = sharedPreferences.edit();
                edit2.putString("ip", et.getText().toString());
                edit2.putString("ipText", et.getText().toString());
                edit2.commit();
                jpapplication.setServer(et.getText().toString());
                intent = new Intent();
                intent.putExtra("no_auto", false);
                intent.setClass(this, Login.class);
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
        Button f4189o = findViewById(R.id.sfyserv);
        f4189o.setOnClickListener(this);
        Button f4192r = findViewById(R.id.ouneiserv);
        f4192r.setOnClickListener(this);
        Button f4193r = findViewById(R.id.guanfangserv);
        f4193r.setOnClickListener(this);
        et = findViewById(R.id.server_ip);
        et.setText(sharedPreferences.getString("ipText", ""));
        Button confirm = findViewById(R.id.ip_confirm);
        confirm.setOnClickListener(this);
    }
}
