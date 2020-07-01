package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class OLUsersPage extends Activity implements OnClickListener {

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
        switch (view.getId()) {
            case R.id.ol_search_b:
                intent.putExtra("head", 6);
                intent.setClass(this, SearchSongs.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_userInfo_b:
                intent.putExtra("head", 0);
                intent.setClass(this, UsersInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_friends_b:
                intent.setClass(this, FriendMailPage.class);
                intent.putExtra("head", 5);
                intent.putExtra("type", "F");
                startActivity(intent);
                finish();
                return;
            case R.id.ol_mail_b:
                intent.setClass(this, FriendMailPage.class);
                intent.putExtra("head", 5);
                intent.putExtra("type", "M");
                startActivity(intent);
                finish();
                return;
            case R.id.ol_local_b:
                Toast.makeText(this, "数据备份功能正在施工中，敬请期待!", Toast.LENGTH_SHORT).show();
//                intent.setClass(this, ShowTopInfo.class);
//                intent.putExtra("head", 5);
//                intent.putExtra("keywords", "F");
//                startActivity(intent);
//                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        JPApplication jpApplication = (JPApplication) getApplication();
        setContentView(R.layout.ol_users_page);
        jpApplication.setBackGround(this, "ground", findViewById(R.id.layout));
        jpApplication.setGameMode(0);
        Button f4570b = findViewById(R.id.ol_userInfo_b);
        f4570b.setOnClickListener(this);
        View f4571c = findViewById(R.id.ol_search_b);
        f4571c.setOnClickListener(this);
        Button f4572d = findViewById(R.id.ol_mail_b);
        f4572d.setOnClickListener(this);
        Button f4573e = findViewById(R.id.ol_friends_b);
        f4573e.setOnClickListener(this);
        Button f4574f = findViewById(R.id.ol_local_b);
        f4574f.setOnClickListener(this);
    }
}
