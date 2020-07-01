package ly.pp.justpiano3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class OLMainMode extends BaseActivity implements OnClickListener {
    final OLMainMode context = this;
    public JPApplication jpapplication;
    OLMainModeHandler olMainModeHandler = new OLMainModeHandler(this);
    String f4293s;

    final void mo2803a(String str, String str2, int i) {
        if (str != null && str2 != null && !str.isEmpty() && !str2.isEmpty()) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle(str);
            jpdialog.setMessage(str2);
            if (i <= 0) {
                jpdialog.setSecondButton("确定", new DialogDismissClick());
            } else {
                jpdialog.setFirstButton(i == 1 ? "获取" : "升级", (dialog, which) -> {
                    dialog.dismiss();
                    new OLMainModeTask(context).execute(jpapplication.getVersion(), "kuang", jpapplication.getAccountName());
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
            }
            jpdialog.showDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null) {
            jpprogressBar.dismiss();
        }
        Intent intent = new Intent();
        intent.putExtra("no_auto", true);
        intent.setClass(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ol_ance_b:
                JPDialog jpdialog = new JPDialog(this);
                jpdialog.setTitle("提示");
                jpdialog.setMessage("新官网正在制作中，网站包含在线曲库曲谱上传、家族族徽上传、用户问题反馈、新版软件下载等功能");
                jpdialog.setSecondButton("确定", new DialogDismissClick());
                jpdialog.showDialog();
                //f4293s = "score";
                //new OLMainModeTask(this).execute(jpapplication.getVersion(), "score", jpapplication.getAccountName());
                return;
            case R.id.ol_songs_b:
                intent.setClass(this, OLSongsPage.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_playhall_b:
                if (jpapplication.getAccountName().isEmpty()) {
                    Toast.makeText(context, "您已经掉线请返回重新登陆!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int i;
                TestSQL testSQL = new TestSQL(this, "data");
                SQLiteDatabase writableDatabase = testSQL.getWritableDatabase();
                Cursor query = writableDatabase.query("jp_data", new String[]{"online"}, "online=1", null, null, null, null);
                int count = query.getCount();
                query.close();
                testSQL.close();
                writableDatabase.close();
                String str = "";
                if (count < 5218) {
                    str = "您载入的曲谱数量为:" + count + "。本版本曲谱数量为:5218。曲库不完整。无法进行在线对战。请在设置中的应用程序选项找到极品钢琴，清除程序数据，再重新打开游戏。或者您也可以卸载后重新安装本程序。载入曲谱时请勿中断或后台本软件!";
                    i = 1;
                } else {
                    if (jpapplication.getIsBindService()) {
                        jpprogressBar.show();
                        try {
                            jpapplication.unbindService(jpapplication.mo2696L());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        jpapplication.setIsBindService(jpapplication.bindService(new Intent(this, ConnectionService.class), jpapplication.mo2696L(), Context.BIND_AUTO_CREATE));
                    } else {
                        jpprogressBar.show();
                        jpapplication.setIsBindService(jpapplication.bindService(new Intent(this, ConnectionService.class), jpapplication.mo2696L(), Context.BIND_AUTO_CREATE));
                    }
                    i = 0;
                }
                jpdialog = new JPDialog(context);
                jpdialog.setTitle("检查曲库");
                jpdialog.setMessage(str);
                jpdialog.setFirstButton("确定", new DialogDismissClick());
                if (i != 0) {
                    jpdialog.showDialog();
                    return;
                }
                return;
            case R.id.ol_top_b:
                intent.setClass(this, OLTopUser.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_users_b:
                intent.setClass(this, OLUsersPage.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_bindmail_b:
                Toast.makeText(this, "此版本不支持绑定邮箱!", Toast.LENGTH_SHORT).show();
                return;
            case R.id.ol_zhuanyi_b:
                Toast.makeText(this, "极品钢琴2服务器已失效!", Toast.LENGTH_SHORT).show();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 0;
        jpapplication = (JPApplication) getApplication();
        jpprogressBar = new JPProgressBar(this, jpapplication);
        jpapplication.loadSettings(1);
        setContentView(R.layout.olmainmode);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        JPApplication jPApplication = jpapplication;
        jPApplication.setGameMode(0);
        Button f4285k = findViewById(R.id.ol_top_b);
        f4285k.setOnClickListener(this);
        Button f4286l = findViewById(R.id.ol_users_b);
        f4286l.setOnClickListener(this);
        Button f4288n = findViewById(R.id.ol_playhall_b);
        f4288n.setOnClickListener(this);
        Button f4287m = findViewById(R.id.ol_songs_b);
        f4287m.setOnClickListener(this);
        Button f4289o = findViewById(R.id.ol_ance_b);
        f4289o.setOnClickListener(this);
        f4289o.setVisibility(View.VISIBLE);
        Button f4290p = findViewById(R.id.ol_bindmail_b);
        f4290p.setOnClickListener(this);
        Button f4291q = findViewById(R.id.ol_zhuanyi_b);
        f4291q.setOnClickListener(this);
        try {
            if (jpapplication.getConnectionService() != null) {
                jpapplication.getConnectionService().outLine();
            }
            if (jpapplication.getIsBindService()) {
                jpapplication.unbindService(jpapplication.mo2696L());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPStack.create();
        JPStack.push(this);
        if (jpapplication.f4073g != null && jpapplication.f4074h != null && !jpapplication.f4073g.isEmpty() && !jpapplication.f4074h.isEmpty()) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle(jpapplication.f4073g);
            jpdialog.setMessage(jpapplication.f4074h);
            jpdialog.setFirstButton("确定", (dialog, which) -> {
                jpapplication.f4074h = "";
                jpapplication.f4073g = "";
                dialog.dismiss();
            });
            jpdialog.showDialog();
        }
    }

    @Override
    protected void onDestroy() {
        JPStack.create();
        JPStack.pop(this);
        super.onDestroy();
    }
}
